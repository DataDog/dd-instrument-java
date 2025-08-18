package datadog.instrument.utils;

import static datadog.instrument.utils.ClassLoaderIndex.getClassLoaderKey;
import static datadog.instrument.utils.ClassLoaderKey.BOOT_CLASS_LOADER;
import static datadog.instrument.utils.ClassLoaderKey.SYSTEM_CLASS_LOADER;

import datadog.instrument.utils.ClassLoaderKey.LookupKey;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Lazily associate a computed value with (potentially) every {@link ClassLoader}. The computed
 * value should not have a strong-reference back to the associated class-loader, as that would stop
 * the class-loader from being unloaded in the future.
 *
 * <p>It is the caller's responsibility to decide how often to call {@link #removeStaleEntries}. It
 * may be on every request, periodically using a background thread, or some other condition.
 *
 * <p>Inspired by {@link ClassValue}.
 */
public abstract class ClassLoaderValue<V> {

  @SuppressWarnings("rawtypes")
  private static final AtomicReferenceFieldUpdater<ClassLoaderValue, Object> BOOT_VALUE_UPDATER =
      AtomicReferenceFieldUpdater.newUpdater(ClassLoaderValue.class, Object.class, "bootValue");

  @SuppressWarnings("rawtypes")
  private static final AtomicReferenceFieldUpdater<ClassLoaderValue, Object> SYSTEM_VALUE_UPDATER =
      AtomicReferenceFieldUpdater.newUpdater(ClassLoaderValue.class, Object.class, "systemValue");

  // boot and system class-loaders are never unloaded
  private volatile V bootValue;
  private volatile V systemValue;

  // maps other (unloadable) class-loaders to their values
  private final Map<Object, V> otherValues = new ConcurrentHashMap<>();

  protected ClassLoaderValue() {
    ClassLoaderKey.registerCleaner(otherValues::remove);
  }

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
      return getOtherValue(cl);
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
      otherValues.remove(new LookupKey(cl));
    }
  }

  /** For testing purposes. */
  int size() {
    return (null != bootValue ? 1 : 0) + (null != systemValue ? 1 : 0) + otherValues.size();
  }

  /**
   * Removes stale entries from {@code ClassLoaderValue}s, where the class-loader is now unused.
   *
   * <p>It is the caller's responsibility to decide how often to call {@code #removeStaleEntries}.
   * It may be periodically with a background thread, on certain requests, or some other condition.
   */
  public static void removeStaleEntries() {
    ClassLoaderKey.cleanStaleKeys();
  }

  /** Lazily associate a computed value with the boot class-loader. */
  private V getBootValue() {
    V value;
    while ((value = bootValue) == null) {
      value = Objects.requireNonNull(computeValue(BOOT_CLASS_LOADER));
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
      value = Objects.requireNonNull(computeValue(SYSTEM_CLASS_LOADER));
      if (SYSTEM_VALUE_UPDATER.compareAndSet(this, null, value)) {
        break;
      }
    }
    return value;
  }

  /** Lazily associate a computed value with a custom class-loader. */
  private V getOtherValue(ClassLoader cl) {
    V value = otherValues.get(new LookupKey(cl));
    if (value == null) {
      value = computeValue(cl);
      V existingValue = otherValues.putIfAbsent(getClassLoaderKey(cl), value);
      if (existingValue != null) {
        value = existingValue;
      }
    }
    return value;
  }
}
