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
      return getClassLoaderKey(cl).id;
    }
  }

  /**
   * Returns the weak key for the given class-loader. The key for a specific class-loader may
   * occasionally change over its life, but no two class-loaders will share the same key.
   */
  static ClassLoaderKey getClassLoaderKey(ClassLoader cl) {
    return index(cl, KEYS, SLOT_MASK);
  }

  /** Searches the hashtable for a {@link ClassLoaderKey} matching the given class-loader. */
  @SuppressWarnings("SameParameterValue") // pass fields as parameters for performance
  private static ClassLoaderKey index(ClassLoader cl, ClassLoaderKey[] keys, int slotMask) {
    final int hash = System.identityHashCode(cl);

    // try to find an empty slot or match, rehashing after each attempt
    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      ClassLoaderKey current = keys[slot];
      if (current == null || null == current.get()) {
        // we found an empty slot
        return (keys[slot] = new ClassLoaderKey(cl, hash));
      } else if (hash == current.hash && cl == current.get()) {
        // we found a matching slot
        return current;
      } else if (i == MAX_HASH_ATTEMPTS) {
        slot = slotMask & hash; // overwrite original slot
        return (keys[slot] = new ClassLoaderKey(cl, hash));
      }
    }
  }

  private static int rehash(int oldHash) {
    return Integer.reverseBytes(oldHash * 0x9e3775cd) * 0x9e3775cd;
  }
}
