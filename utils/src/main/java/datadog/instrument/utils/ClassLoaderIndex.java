package datadog.instrument.utils;

import static datadog.instrument.utils.ClassLoaderKey.BOOT_CLASS_LOADER;
import static datadog.instrument.utils.ClassLoaderKey.BOOT_CLASS_LOADER_KEY_ID;
import static datadog.instrument.utils.ClassLoaderKey.SYSTEM_CLASS_LOADER;
import static datadog.instrument.utils.ClassLoaderKey.SYSTEM_CLASS_LOADER_KEY_ID;

/**
 * Semi-stable index of {@link ClassLoaderKey}s that guarantees a unique key and id for different
 * class-loaders. A class-loader may have more than one key-id over its life if it is temporarily
 * displaced from the index. For example if a large number of class-loaders (>500) were created in
 * parallel.
 */
public final class ClassLoaderIndex {

  // fixed-size hashtable of known class-loader keys (table size must be power of 2)
  // this is tuned to support ~700 concurrent class-loaders with minimal collisions
  private static final ClassLoaderKey[] KEYS = new ClassLoaderKey[1024];
  private static final int SLOT_MASK = KEYS.length - 1;

  private static final int MAX_HASH_ATTEMPTS = 10;

  private ClassLoaderIndex() {}

  /**
   * Returns the key-id for the given class-loader. The key-id for a specific class-loader may
   * occasionally change over its life, but no two class-loaders will share the same key-id.
   */
  public static int getClassLoaderKeyId(ClassLoader cl) {
    if (cl == BOOT_CLASS_LOADER) {
      return BOOT_CLASS_LOADER_KEY_ID;
    } else if (cl == SYSTEM_CLASS_LOADER) {
      return SYSTEM_CLASS_LOADER_KEY_ID;
    } else {
      return getClassLoaderKey(cl).keyId;
    }
  }

  /**
   * Returns the weak key for the given class-loader. The key for a specific class-loader may
   * occasionally change over its life, but no two class-loaders will share the same key.
   */
  static ClassLoaderKey getClassLoaderKey(ClassLoader cl) {
    final int hash = System.identityHashCode(cl);
    final ClassLoaderKey[] keys = KEYS;
    final int slotMask = SLOT_MASK;

    int evictedSlot = -1;

    // look for existing slot or one that can be (re)used
    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      ClassLoaderKey existing = keys[slot];
      if (existing != null) {
        // slot was already used
        Object existingCL = existing.get();
        if (existingCL == cl) {
          return existing; // match found
        } else if (existingCL == null && evictedSlot < 0) {
          // slot can be re-used if we don't find a match
          evictedSlot = slot;
        }
        if (i < MAX_HASH_ATTEMPTS) {
          continue; // collision, rehash and try again
        } else {
          // all slots used, re-use first (evicted) slot we found
          slot = evictedSlot < 0 ? slotMask & hash : evictedSlot;
        }
      }
      return (keys[slot] = new ClassLoaderKey(cl, hash));
    }
  }

  private static int rehash(int oldHash) {
    return Integer.reverseBytes(oldHash * 0x9e3775cd) * 0x9e3775cd;
  }
}
