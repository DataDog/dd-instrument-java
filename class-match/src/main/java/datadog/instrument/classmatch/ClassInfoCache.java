package datadog.instrument.classmatch;

import static datadog.instrument.utils.ClassLoaderIndex.getClassLoaderKeyId;

public final class ClassInfoCache<T> {

  private static final int MAX_CAPACITY = 1 << 16;
  private static final int MIN_CAPACITY = 1 << 4;
  private static final int MAX_HASH_ATTEMPTS = 10;

  private static final long START_NANOS = System.nanoTime();

  private final SharedInfo[] shared;
  private final int slotMask;

  public ClassInfoCache(int capacity) {
    if (capacity < MIN_CAPACITY) {
      capacity = MIN_CAPACITY;
    } else if (capacity > MAX_CAPACITY) {
      capacity = MAX_CAPACITY;
    }
    // choose enough slot bits to cover the chosen capacity
    this.slotMask = 0xFFFFFFFF >>> Integer.numberOfLeadingZeros(capacity - 1);
    this.shared = new SharedInfo[slotMask + 1];
  }

  public T find(String className, ClassLoader cl) {
    return find(className, getClassLoaderKeyId(cl));
  }

  public void share(String className, T info, ClassLoader cl) {
    share(className, info, getClassLoaderKeyId(cl));
  }

  @SuppressWarnings("unchecked")
  public T find(String className, int classLoaderKeyId) {
    return (T) find(className, classLoaderKeyId, shared, slotMask);
  }

  public void share(String className, T info, int classLoaderKeyId) {
    share(new SharedInfo(className, info, classLoaderKeyId), shared, slotMask);
  }

  private static Object find(
      String className, int classLoaderKeyId, SharedInfo[] shared, int slotMask) {
    final int hash = className.hashCode();

    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      SharedInfo existing = shared[slot];
      if (existing == null) {
        return null;
      } else if (classLoaderKeyId == existing.classLoaderKeyId
          && className.equals(existing.className)) {
        existing.usedNanos = System.nanoTime();
        return existing.classInfo;
      } else if (i == MAX_HASH_ATTEMPTS) {
        return null;
      }
    }
  }

  private static void share(SharedInfo info, SharedInfo[] shared, int slotMask) {
    final int hash = info.hashCode();

    long leastUsedNanos = Long.MAX_VALUE;
    int leastUsedSlot = 0;

    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      long nanos;
      int slot = slotMask & h;
      SharedInfo existing = shared[slot];
      if (existing == null || existing.equals(info)) {
        shared[slot] = info;
        return;
      } else if (i == MAX_HASH_ATTEMPTS) {
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

  static final class SharedInfo {
    final String className;
    final Object classInfo;
    final int classLoaderKeyId;

    long usedNanos = System.nanoTime();

    SharedInfo(String className, Object classInfo, int classLoaderKeyId) {
      this.className = className;
      this.classInfo = classInfo;
      this.classLoaderKeyId = classLoaderKeyId;
    }

    @Override
    public int hashCode() {
      return className.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof SharedInfo) {
        return className.equals(((SharedInfo) o).className);
      }
      return false;
    }
  }
}
