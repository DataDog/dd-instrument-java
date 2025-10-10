/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.utils;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Shares class information from multiple classloaders in a single cache.
 *
 * <p>Information is indexed by class-name, with an optional class-loader matcher. When multiple
 * classes are defined with the same name, only one has its information cached at any given time.
 * The class-loader can then be used to check information is for the correct class.
 *
 * @see ClassLoaderIndex#getClassLoaderKeyId(ClassLoader)
 */
public final class ClassInfoCache<T> {

  private static final int MAX_CAPACITY = 1 << 20;
  private static final int MIN_CAPACITY = 1 << 4;
  private static final int MAX_HASH_ATTEMPTS = 10;

  // individual class-loader keys are zero or above
  private static final int ALL_CLASS_LOADERS = -1;

  // global monotonic counter; cheap way to track aging as info is shared
  static final AtomicLong TICKS = new AtomicLong();

  // fixed-size hashtable of shared information, indexed by class-name
  private final SharedInfo[] shared;
  private final int slotMask;

  /**
   * Creates a new class-info cache with the given capacity.
   *
   * @param capacity the cache capacity
   */
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

  /**
   * Finds information for the given class-name, across all class-loaders.
   *
   * @param className the class-name
   * @return information shared under the class-name
   */
  public T find(CharSequence className) {
    return find(className, ALL_CLASS_LOADERS);
  }

  /**
   * Shares information for the given class-name, across all class-loaders.
   *
   * @param className the class-name
   * @param info the information to share under the class-name
   */
  public void share(String className, T info) {
    share(className, info, ALL_CLASS_LOADERS);
  }

  /**
   * Finds information for the given class-name, scoped to the given class-loader.
   *
   * @param className the class-name
   * @param cl the class-loader
   * @return information shared under the class-name and class-loader
   */
  public T find(CharSequence className, ClassLoader cl) {
    return find(className, ClassLoaderIndex.getClassLoaderKeyId(cl));
  }

  /**
   * Shares information for the given class-name, scoped to the given class-loader.
   *
   * @param className the class-name
   * @param info the information to share under the class-name and class-loader
   * @param cl scope the information to this class-loader
   */
  public void share(String className, T info, ClassLoader cl) {
    share(className, info, ClassLoaderIndex.getClassLoaderKeyId(cl));
  }

  /**
   * Finds information for the given class-name, scoped to the given class-loader key.
   *
   * @param className the class-name
   * @param classLoaderKeyId the class-loader's key-id
   * @return information shared under the class-name and class-loader
   * @see ClassLoaderIndex#getClassLoaderKeyId(ClassLoader)
   */
  @SuppressWarnings("unchecked")
  public T find(CharSequence className, int classLoaderKeyId) {
    final int hash = className.hashCode();
    final SharedInfo[] shared = this.shared;
    final int slotMask = this.slotMask;

    // try to find matching slot, rehashing after each attempt
    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      SharedInfo existing = shared[slot];
      if (existing != null) {
        if (existing.className.contentEquals(className)) {
          // match on class-loader key, -1 on either side matches all
          if ((classLoaderKeyId ^ existing.classLoaderKeyId) <= 0) {
            // use global TICKS as a substitute for access time
            // TICKS is only incremented in 'share' for performance reasons
            existing.accessed = TICKS.get();
            return (T) existing.classInfo;
          }
          // fall-through and quit; name matched but class-loader didn't
        } else if (i < MAX_HASH_ATTEMPTS) {
          continue; // rehash and try again
        }
      }
      // quit search when:
      // * we find an empty slot (we know there won't be further info)
      // * we find a slot with the same name but different class-loader
      // * we've exhausted all hash attempts
      return null;
    }
  }

  /**
   * Finds information for the given class-name, scoped to class-loaders with matching keys.
   *
   * @param className the class-name
   * @param classLoaderKeyMatcher matcher of class-loader keys
   * @return information shared under the class-name and matching class-loader
   * @see ClassLoaderIndex#getClassLoaderKeyId(ClassLoader)
   */
  @SuppressWarnings("unchecked")
  public T find(CharSequence className, ClassLoaderKeyMatcher classLoaderKeyMatcher) {
    final int hash = className.hashCode();
    final SharedInfo[] shared = this.shared;
    final int slotMask = this.slotMask;

    // try to find matching slot, rehashing after each attempt
    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      SharedInfo existing = shared[slot];
      if (existing != null) {
        if (existing.className.contentEquals(className)) {
          // apply custom matcher to class-loader key, -1 always matches
          if (existing.classLoaderKeyId < 0
              || classLoaderKeyMatcher.test(existing.classLoaderKeyId)) {
            // use global TICKS as a substitute for access time
            // TICKS is only incremented in 'share' for performance reasons
            existing.accessed = TICKS.get();
            return (T) existing.classInfo;
          }
          // fall-through and quit; name matched but class-loader didn't
        } else if (i < MAX_HASH_ATTEMPTS) {
          continue; // rehash and try again
        }
      }
      // quit search when:
      // * we find an empty slot (we know there won't be further info)
      // * we find a slot with the same name but different class-loader
      // * we've exhausted all hash attempts
      return null;
    }
  }

  /**
   * Shares information for the given class-name, scoped to the given class-loader key.
   *
   * @param className the class-name
   * @param info the information to share under the class-name and class-loader
   * @param classLoaderKeyId scope the information to this class-loader key-id
   * @see ClassLoaderIndex#getClassLoaderKeyId(ClassLoader)
   */
  @SuppressWarnings("StatementWithEmptyBody")
  public void share(String className, T info, int classLoaderKeyId) {
    final int hash = className.hashCode();
    final SharedInfo[] shared = this.shared;
    final int slotMask = this.slotMask;

    // always wrap info in a new wrapper to avoid consistency issues
    final SharedInfo update = new SharedInfo(className, info, classLoaderKeyId);

    long oldestTick = Long.MAX_VALUE;
    int oldestSlot = -1;

    // search by repeated hashing; stop when we find an empty slot,
    // a matching slot, or we exhaust all attempts and re-use a slot
    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      SharedInfo existing = shared[slot];
      if (existing != null && !existing.equals(update)) {
        // slot already used by a different class
        long tick = existing.accessed;
        if (i < MAX_HASH_ATTEMPTS) {
          // still more slots to search
          if (tick < oldestTick) {
            // record least-recently-used slot for re-use later
            oldestTick = tick;
            oldestSlot = slot;
          }
          continue; // rehash and try again
        }
        // exhausted attempts, pick best slot to re-use
        if (oldestSlot >= 0 && oldestTick <= tick) {
          slot = oldestSlot; // re-use least-recently-used slot
        } else {
          // last hashed slot is least-recently-used, re-use it
        }
      }
      shared[slot] = update;
      // increment global TICKS whenever info is shared
      // avoid incrementing it in 'find' for performance reasons
      update.accessed = TICKS.getAndIncrement();
      return;
    }
  }

  /** Removes all class information from the cache. */
  public void clear() {
    Arrays.fill(shared, null);
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

    long accessed;

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
