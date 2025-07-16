package datadog.instrument.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/** Reference key used to weakly associate a class-loader with a computed value. */
final class ClassLoaderKey extends WeakReference<ClassLoader> {

  static final ClassLoader BOOT_CLASS_LOADER = null;
  static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

  static final int BOOT_CLASS_LOADER_KEY_ID = 0;
  static final int SYSTEM_CLASS_LOADER_KEY_ID = 1;

  // key-ids 0 and 1 are pre-assigned to the boot and system class-loaders
  private static final AtomicInteger NEXT_KEY_ID = new AtomicInteger(2);

  // stale class-loader keys that are now eligible for collection
  private static final ReferenceQueue<ClassLoader> staleKeys = new ReferenceQueue<>();

  // registered cleaners of stale class-loader keys and their values
  private static final List<Consumer<Reference<?>>> cleaners = new CopyOnWriteArrayList<>();

  private static final int MAX_KEYS_CLEANED_PER_CYCLE = 8;

  /** Registers a cleaner of stale class-loader keys. */
  static void registerCleaner(Consumer<Reference<?>> cleaner) {
    cleaners.add(cleaner);
  }

  /** Checks for stale class-loader keys; stale keys are cleaned by the registered cleaners. */
  static void cleanStaleKeys() {
    Reference<?> ref;
    int count = 0;
    while ((ref = staleKeys.poll()) != null) {
      for (Consumer<Reference<?>> cleaner : cleaners) {
        cleaner.accept(ref);
      }
      if (++count >= MAX_KEYS_CLEANED_PER_CYCLE) {
        break; // limit work done per call
      }
    }
  }

  final int hash;
  final int id;

  ClassLoaderKey(ClassLoader cl, int hash) {
    super(cl, staleKeys);
    this.hash = hash;
    this.id = NEXT_KEY_ID.getAndIncrement();
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  @SuppressFBWarnings("Eq") // symmetric because it mirrors LookupKey.equals
  public boolean equals(Object o) {
    if (o instanceof LookupKey) {
      return get() == ((LookupKey) o).cl;
    } else if (o instanceof ClassLoaderKey) {
      return get() == ((ClassLoaderKey) o).get();
    } else {
      return false;
    }
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
    @SuppressFBWarnings("Eq") // symmetric because it mirrors ClassLoaderKey.equals
    public boolean equals(Object o) {
      if (o instanceof ClassLoaderKey) {
        return cl == ((ClassLoaderKey) o).get();
      } else if (o instanceof LookupKey) {
        return cl == ((LookupKey) o).cl;
      } else {
        return false;
      }
    }
  }
}
