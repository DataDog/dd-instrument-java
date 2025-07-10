package datadog.instrument.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Lazily associate a computed value with (potentially) every {@link ClassLoader}. The computed
 * value should not have a strong-reference back to its key, as that would keep the class-loader
 * from being unloadable.
 *
 * <p>It is the responsibility of the subclass to decide when to call {@link #removeStaleEntries}.
 * It could be on every access, periodically using a background thread, or some other condition.
 *
 * <p>Inspired by {@link ClassValue}.
 */
public abstract class ClassLoaderValue<V> {

  private static final ClassLoader BOOT_CLASS_LOADER = null;
  private static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

  @SuppressWarnings("rawtypes")
  private static final AtomicReferenceFieldUpdater<ClassLoaderValue, Object> BOOT_VALUE_UPDATER =
      AtomicReferenceFieldUpdater.newUpdater(ClassLoaderValue.class, Object.class, "bootValue");

  @SuppressWarnings("rawtypes")
  private static final AtomicReferenceFieldUpdater<ClassLoaderValue, Object> SYSTEM_VALUE_UPDATER =
      AtomicReferenceFieldUpdater.newUpdater(ClassLoaderValue.class, Object.class, "systemValue");

  // boot and system class-loaders are never unloaded
  private volatile V bootValue;
  private volatile V systemValue;

  private static final int MAX_REMOVALS_PER_CYCLE = 8;

  // weak keys permit unloading of class-loaders; call removeStaleEntries to clean up map
  private final ReferenceQueue<ClassLoader> staleKeys = new ReferenceQueue<>();
  private final Map<Object, V> values = new ConcurrentHashMap<>();

  /**
   * Computes the given class-loaders's derived value for this {@code ClassLoaderValue}.
   *
   * <p>This method will be invoked within the first thread that accesses the value with the {@link
   * #get} method. Normally, this method is invoked at most once per class-loader, but it may be
   * invoked again if there has been a call to {@link #remove remove}.
   *
   * <p>If this method throws an exception, the corresponding call to {@code get} will terminate
   * abnormally with that exception, and no class-loader value will be recorded.
   *
   * @param cl the class-loader whose value must be computed
   * @return the newly computed value associated with this {@code ClassLoaderValue}, for the given
   *     class-loader
   * @see #get
   * @see #remove
   */
  protected abstract V computeValue(ClassLoader cl);

  /**
   * Returns the value for the given class-loader. If no value has yet been computed, it is obtained
   * by an invocation of the {@link #computeValue} method.
   *
   * <p>The actual installation of the value on the class-loader is performed atomically. At that
   * point if several threads have computed values, one is chosen and returned to all the threads.
   *
   * @param cl the class-loader whose value must be computed or retrieved
   * @return the value currently associated with this {@code ClassLoaderValue}, for the given
   *     class-loader
   * @see #remove
   * @see #computeValue
   */
  public final V get(ClassLoader cl) {
    if (cl == BOOT_CLASS_LOADER) {
      return getBootValue();
    } else if (cl == SYSTEM_CLASS_LOADER) {
      return getSystemValue();
    } else {
      return getValue(cl);
    }
  }

  /**
   * Removes the associated value for the given class-loader.
   *
   * <p>If this is subsequently {@linkplain #get read} for the same class-loader, its value will be
   * reinitialized by invoking its {@link #computeValue computeValue} method. This may result in an
   * additional invocation of the {@code computeValue} method for the given class-loader.
   *
   * @param cl the class-loader whose value must be removed
   */
  public final void remove(ClassLoader cl) {
    if (cl == BOOT_CLASS_LOADER) {
      bootValue = null;
    } else if (cl == SYSTEM_CLASS_LOADER) {
      systemValue = null;
    } else {
      values.remove(new LookupKey(cl));
    }
  }

  /**
   * Removes stale entries from this {@code ClassLoaderValue}.
   *
   * <p>It is the responsibility of the subclass to decide when to call {@code #removeStaleEntries}.
   * It could be on a computation, periodically using a background thread, or some other condition.
   */
  public final void removeStaleEntries() {
    Object ref;
    int count = 0;
    while ((ref = staleKeys.poll()) != null) {
      values.remove(ref);
      if (++count >= MAX_REMOVALS_PER_CYCLE) {
        break;
      }
    }
  }

  /** Lazily associate a computed value with the boot class-loader. */
  private V getBootValue() {
    V value;
    while ((value = bootValue) == null) {
      value = Objects.requireNonNull(computeValue(null));
      if (BOOT_VALUE_UPDATER.compareAndSet(this, null, value)) {
        break;
      }
    }
    return value;
  }

  /** Lazily associate a computed value with the system class-loader. */
  private V getSystemValue() {
    V value;
    while ((value = systemValue) == null) {
      value = Objects.requireNonNull(computeValue(null));
      if (SYSTEM_VALUE_UPDATER.compareAndSet(this, null, value)) {
        break;
      }
    }
    return value;
  }

  /** Lazily associate a computed value with a custom class-loader. */
  private V getValue(ClassLoader cl) {
    V value = values.get(new LookupKey(cl));
    if (value == null) {
      value = computeValue(cl);
      V existingValue = values.putIfAbsent(new WeakKey(cl, staleKeys), value);
      if (existingValue != null) {
        value = existingValue;
      }
    }
    return value;
  }

  /** Minimal key used for lookup purposes without the reference tracking overhead. */
  static final class LookupKey {
    private final ClassLoader cl;
    private final int hash;

    LookupKey(ClassLoader cl) {
      this.cl = cl;
      this.hash = System.identityHashCode(cl);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    @SuppressFBWarnings("Eq") // this is symmetric because it mirrors WeakKey.equals
    public boolean equals(Object o) {
      if (o instanceof WeakKey) {
        return cl == ((WeakKey) o).get();
      } else if (o instanceof LookupKey) {
        return cl == ((LookupKey) o).cl;
      } else {
        return false;
      }
    }
  }

  /** Reference key used to weakly associate a class-loader with a computed value. */
  static final class WeakKey extends WeakReference<ClassLoader> {
    private final int hash;

    WeakKey(ClassLoader cl, ReferenceQueue<ClassLoader> queue) {
      super(cl, queue);
      hash = System.identityHashCode(cl);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    @SuppressFBWarnings("Eq") // this is symmetric because it mirrors LookupKey.equals
    public boolean equals(Object o) {
      if (o instanceof LookupKey) {
        return get() == ((LookupKey) o).cl;
      } else if (o instanceof WeakKey) {
        return get() == ((WeakKey) o).get();
      } else {
        return false;
      }
    }
  }
}
