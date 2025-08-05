package datadog.instrument.classmatch;

import datadog.instrument.utils.ClassLoaderIndex;

/**
 * Shares class information from multiple classloaders in a single cache.
 *
 * <p>Information is indexed by class-name, with an optional class-loader filter. When multiple
 * classes are defined with the same name, only one has its information cached at any given time.
 * The class-loader can then be used to check information is for the correct class.
 *
 * @see ClassLoaderIndex#getClassLoaderKeyId
 */
public final class ClassInfoCache<T> {

  private static final int MAX_CAPACITY = 1 << 20;
  private static final int MIN_CAPACITY = 1 << 4;
  private static final int MAX_HASH_ATTEMPTS = 10;

  private static final long START_NANOS = System.nanoTime();

  // individual class-loader keys are zero or above
  private static final int ALL_CLASS_LOADERS = -1;

  // fixed-size hashtable indexed by class-name
  private final SharedInfo[] shared;
  private final int slotMask;

  public ClassInfoCache(int capacity) {
    if (capacity < MIN_CAPACITY) {
      capacity = MIN_CAPACITY;
    } else if (capacity > MAX_CAPACITY) {
      capacity = MAX_CAPACITY;
    }
    // choose enough slot bits to cover the given capacity
    this.slotMask = -1 >>> Integer.numberOfLeadingZeros(capacity - 1);
    this.shared = new SharedInfo[slotMask + 1];
  }

  /** Finds information for the given class-name, across all class-loaders. */
  public T find(String className) {
    return find(className, ALL_CLASS_LOADERS);
  }

  /** Finds information for the given class-name, under the given class-loader. */
  public T find(String className, ClassLoader cl) {
    return find(className, ClassLoaderIndex.getClassLoaderKeyId(cl));
  }

  /**
   * Finds information for the given class-name, under the given class-loader key.
   *
   * @see ClassLoaderIndex#getClassLoaderKeyId(ClassLoader)
   */
  @SuppressWarnings("unchecked")
  public T find(String className, int classLoaderKeyId) {
    return (T) find(className, classLoaderKeyId, shared, slotMask);
  }

  /** Shares information for the given class-name, across all class-loaders. */
  public void share(String className, T info) {
    share(className, info, ALL_CLASS_LOADERS);
  }

  /** Shares information for the given class-name, under the given class-loader. */
  public void share(String className, T info, ClassLoader cl) {
    share(className, info, ClassLoaderIndex.getClassLoaderKeyId(cl));
  }

  /**
   * Shares information for the given class-name, under the given class-loader key.
   *
   * @see ClassLoaderIndex#getClassLoaderKeyId(ClassLoader)
   */
  public void share(String className, T info, int classLoaderKeyId) {
    share(new SharedInfo(className, info, classLoaderKeyId), shared, slotMask);
  }

  /** Finds information by class-name, with an optional class-loader filter. */
  private static Object find(
      String className, int classLoaderKeyId, SharedInfo[] shared, int slotMask) {
    final int hash = className.hashCode();

    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      SharedInfo existing = shared[slot];
      if (existing != null) {
        if (className.equals(existing.className)) {
          // filter on class-loader, -1 on either side matches all
          if ((classLoaderKeyId ^ existing.classLoaderKeyId) <= 0) {
            existing.usedNanos = System.nanoTime();
            return existing.classInfo;
          }
          // fall-through and quit; name matched but class-loader didn't
        } else if (i < MAX_HASH_ATTEMPTS) {
          continue; // collision, rehash and try again
        }
      }
      // quit search when:
      // * we found an empty slot
      // * we found a slot with the same name but different class-loader
      // * we've exhausted all hash attempts
      return null;
    }
  }

  /** Shares class information by class-name. */
  private static void share(SharedInfo info, SharedInfo[] shared, int slotMask) {
    final int hash = info.hashCode();

    long leastUsedNanos = Long.MAX_VALUE;
    int leastUsedSlot = 0;

    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      long nanos;
      int slot = slotMask & h;
      SharedInfo existing = shared[slot];
      if (existing == null || existing.equals(info)) {
        // use first slot that is empty or has the same class-name
        shared[slot] = info;
        return;
      } else if (i == MAX_HASH_ATTEMPTS) {
        // avoid overwriting slots that were only recently used
        shared[leastUsedSlot] = info;
        return;
      } else if ((nanos = (existing.usedNanos - START_NANOS)) < leastUsedNanos) {
        leastUsedNanos = nanos;
        leastUsedSlot = slot;
      }
    }
  }

  private static int rehash(int oldHash) {
    return Integer.reverseBytes(oldHash * 0x9e3775cd) * 0x9e3775cd;
  }

  /**
   * Shared class information wrapper, indexed by class-name.
   *
   * <p>{@link #hashCode} and {@link #equals} must only use the class-name.
   */
  static final class SharedInfo {
    final String className;
    final Object classInfo;

    // optional class-loader key
    final int classLoaderKeyId;

    long usedNanos = System.nanoTime();

    SharedInfo(String className, Object classInfo, int classLoaderKeyId) {
      this.className = className;
      this.classInfo = classInfo;
      this.classLoaderKeyId = classLoaderKeyId;
    }

    @Override
    public int hashCode() {
      // shared class information is indexed by class-name
      return className.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof SharedInfo) {
        // shared class information is indexed by class-name
        return className.equals(((SharedInfo) o).className);
      }
      return false;
    }
  }
}
