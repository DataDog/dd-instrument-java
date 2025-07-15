package datadog.instrument.utils;

/**
 * Semi-stable index of {@link ClassLoaderKey}s that guarantees a unique key and id for different
 * class-loaders. A class-loader may have more than one key-id over its life if it is temporarily
 * displaced from the index. For example if a large number of class-loaders (>500) were created in
 * parallel.
 */
public final class ClassLoaderIndex {

  // simple hashtable of known class-loader keys for fast access
  private static final ClassLoaderKey[] KEYS = new ClassLoaderKey[1024];
  private static final int SLOT_MASK = KEYS.length - 1;

  private static final int MAX_HASH_ATTEMPTS = 5;

  /**
   * Returns the key-id for the given class-loader. The key-id for a specific class-loader may
   * occasionally change over its life, but no two class-loaders will share the same key-id.
   */
  public static int getClassLoaderId(ClassLoader cl) {
    if (cl == ClassLoaderKey.BOOT_CLASS_LOADER) {
      return 0; // pre-assigned
    } else if (cl == ClassLoaderKey.SYSTEM_CLASS_LOADER) {
      return 1; // pre-assigned
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

    // multiply by -127 to improve identityHashCode spread
    int h = hash - (hash << 7);

    // don't use pre-assigned slots 0 (boot) or 1 (system)
    int slot = Math.max(2, h & slotMask);

    final int initialSlot = slot;

    // try to find a slot or a match 5 times
    for (int i = 1; true; i++) {
      ClassLoaderKey current = keys[slot];
      if (current == null || null == current.get()) {
        // we found an empty slot
        return (keys[slot] = new ClassLoaderKey(cl, hash));
      } else if (hash == current.hash && cl == current.get()) {
        // we found a matching slot
        return current;
      } else if (i == MAX_HASH_ATTEMPTS) {
        // all 5 slots have been taken, re-use the initial one
        return (keys[initialSlot] = new ClassLoaderKey(cl, hash));
      }
      h = rehash(h); // try another slot
      slot = Math.max(2, h & slotMask);
    }
  }

  private static int rehash(int _h) {
    int h = _h * 0x9e3775cd;
    h = Integer.reverseBytes(h);
    return h * 0x9e3775cd;
  }
}
