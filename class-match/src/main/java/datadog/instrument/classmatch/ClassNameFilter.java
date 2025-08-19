package datadog.instrument.classmatch;

import java.util.Arrays;

/**
 * Compact filter that records class-name membership by their hash and short 'class-code'.
 *
 * <p>The 'class-code' includes the length of the package-prefix and simple-name, as well as the
 * first and last characters of the simple-name. These elements coupled with the hash of the full
 * class-name means the probability of a false-positive when testing membership is extremely low.
 * (Testing the filter with class-names from 'GitHub Java Corpus' produced zero false-positives.)
 * This means we can avoid storing full class-names, making the filter very compact.
 */
public final class ClassNameFilter {

  private static final int MAX_CAPACITY = 1 << 20;
  private static final int MIN_CAPACITY = 1 << 8;
  private static final int MAX_HASH_ATTEMPTS = 10;

  // fixed-size hashtable of encoded members, indexed by class-name
  private final long[] members;
  private final int slotMask;

  public ClassNameFilter(int capacity) {
    if (capacity < MIN_CAPACITY) {
      capacity = MIN_CAPACITY;
    } else if (capacity > MAX_CAPACITY) {
      capacity = MAX_CAPACITY;
    }
    // choose enough slot bits to cover the given capacity
    slotMask = -1 >>> Integer.numberOfLeadingZeros(capacity - 1);
    members = new long[slotMask + 1];
  }

  /** Tests whether the given class-name is a member of the filter. */
  public boolean contains(String className) {
    final int hash = className.hashCode();
    final long[] members = this.members;
    final int slotMask = this.slotMask;

    // try to find matching slot, rehashing after each attempt
    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      long codeAndHash = members[slotMask & h];
      if (codeAndHash != 0) {
        // check hash first as it's cheap, then check class-code
        if ((int) codeAndHash == hash) {
          return checkClassCode(className, (int) (codeAndHash >>> 32));
        } else if (i < MAX_HASH_ATTEMPTS) {
          continue; // rehash and try again
        }
      }
      return false;
    }
  }

  /** Records the given class-name as a member of the filter. */
  public void add(String className) {
    final int hash = className.hashCode();
    final long[] members = this.members;
    final int slotMask = this.slotMask;

    // pack class-code and class-name hash into long (make hash easy to check)
    long codeAndHash = (long) classCode(className) << 32 | 0xFFFFFFFFL & hash;

    // search by repeated hashing
    for (int i = 1, h = hash; true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      if (members[slot] != 0) {
        // slot already used
        if (i < MAX_HASH_ATTEMPTS) {
          continue; // rehash and try again
        }
        slot = slotMask & hash; // re-use first hashed slot
      }
      members[slot] = codeAndHash;
      return;
    }
  }

  /** Removes all class-names from the filter. */
  public void clear() {
    Arrays.fill(members, 0);
  }

  /**
   * Computes a 32-bit 'class-code' that includes the length of the package-prefix and simple-name,
   * plus the first and last characters of the simple-name (each truncated to fit into 8-bits.)
   */
  private static int classCode(String className) {
    int start = className.lastIndexOf('.') + 1;
    int end = className.length() - 1;
    return (0xFF & className.charAt(end)) << 24
        | (0xFF & className.charAt(start)) << 16
        | (0xFF & (end - start)) << 8
        | (0xFF & start);
  }

  /** Checks whether the 32-bit 'class-code' is consistent with the fully-qualified class-name. */
  private static boolean checkClassCode(String className, int code) {
    int start = (0xFF & code);
    int end = className.length() - 1;
    return end - start == (0xFF & (code >> 8))
        && className.charAt(start) == (0xFF & (code >> 16))
        && className.charAt(end) == (0xFF & (code >> 24));
  }

  private static int rehash(int oldHash) {
    return Integer.reverseBytes(oldHash * 0x9e3775cd) * 0x9e3775cd;
  }
}
