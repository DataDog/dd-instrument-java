/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.internalName;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Compact immutable set of internal names, representing types to be matched against. Implemented as
 * a fixed hashtable, sized to avoid collisions, falling back to sorted array if collisions persist.
 *
 * <p>The key feature of this implementation is that it supports querying by arbitrary {@link
 * CharSequence}s, as long as the char sequence uses the same hash algorithm as {@link String}.
 */
abstract class InternalNames extends AbstractSet<String> {
  private static final int MAX_HASH_ATTEMPTS = 3;
  private static final int MAX_RESIZE_ATTEMPTS = 3;

  InternalNames() {} // avoids synthetic accessor

  /**
   * Creates a new set of internal names containing the given types of interest.
   *
   * @param types the types of interest
   */
  static InternalNames create(Collection<String> types) {
    String[] names = new String[types.size()];
    int n = 0;
    for (String type : types) {
      names[n++] = internalName(type);
    }
    // attempt to hash types into a table with ~75% load factor
    int tableSize = Math.max(8, names.length * 4 / 3) - 1;
    int slotMask = -1 >>> Integer.numberOfLeadingZeros(tableSize);
    String[] table = new String[slotMask + 1];
    int resizes = 0;
    for (int i = 0; i < names.length; i++) {
      // add types one by one, watching out for unsolvable collisions
      if (!add(table, slotMask, names[i])) {
        // bound growth relative to types, in case collisions are unsolvable
        if (resizes++ >= MAX_RESIZE_ATTEMPTS) {
          return new Sorted(names);
        }
        // cannot add type without collision; grow table and restart additions
        slotMask = (slotMask << 1) + 1;
        table = new String[slotMask + 1];
        i = -1;
      }
    }
    return new Hashed(table, slotMask);
  }

  /**
   * Returns {@code true} if the set of internal names contains the given type.
   *
   * @param internalName the internal name of the type
   * @return {@code true} if the set contains this type; otherwise {@code false}
   */
  public abstract boolean containsType(CharSequence internalName);

  @Override
  public final boolean contains(Object o) {
    return o instanceof CharSequence && containsType((CharSequence) o);
  }

  /**
   * Attempts to add a type to a hashtable of names, with a bounded amount of rehashing.
   *
   * @param table a hashtable of internal names
   * @param slotMask mask used to map hashes to slots
   * @param internalName the internal name of the type
   * @return {@code true} if the type was successfully added; otherwise {@code false}
   */
  private static boolean add(String[] table, int slotMask, String internalName) {
    for (int i = 1, h = internalName.hashCode(); true; i++, h = rehash(h)) {
      int slot = slotMask & h;
      String existing = table[slot];
      // be prepared to de-duplicate names
      if (existing == null || existing.equals(internalName)) {
        table[slot] = internalName;
        return true;
      } else if (i < MAX_HASH_ATTEMPTS) {
        continue; // rehash and try again
      }
      return false;
    }
  }

  static int rehash(int oldHash) {
    return Integer.reverseBytes(oldHash * 0x9e3775cd) * 0x9e3775cd;
  }

  /** Normal implementation: a fixed hashtable with at most MAX_HASH_ATTEMPTS probes per lookup. */
  private static final class Hashed extends InternalNames {
    private final String[] table;
    private final int slotMask;

    Hashed(String[] table, int slotMask) {
      this.table = table;
      this.slotMask = slotMask;
    }

    @Override
    public boolean containsType(CharSequence internalName) {
      final String[] table = this.table;
      final int slotMask = this.slotMask;
      for (int i = 1, h = internalName.hashCode(); true; i++, h = rehash(h)) {
        String existing = table[slotMask & h];
        if (existing != null) {
          // use content-equality, not object-equality
          if (existing.contentEquals(internalName)) {
            return true;
          } else if (i < MAX_HASH_ATTEMPTS) {
            continue; // rehash and try again
          }
        }
        return false;
      }
    }

    // ----------------------------------------------------------------------------------------------
    // The rest of this class implements a read-only Set contract for the internal names hashtable
    // ----------------------------------------------------------------------------------------------

    @Override
    public Iterator<String> iterator() {
      return new Iterator<String>() {
        private int i = 0;

        @Override
        public boolean hasNext() {
          for (; i < table.length; i++) {
            if (table[i] != null) {
              return true;
            }
          }
          return false;
        }

        @Override
        public String next() {
          if (hasNext()) {
            return table[i++];
          } else {
            throw new NoSuchElementException();
          }
        }
      };
    }

    @Override
    public int size() {
      // naive implementation to satisfy the AbstractSet contract; nothing calls this
      int size = 0;
      for (String s : table) {
        if (s != null) {
          size++;
        }
      }
      return size;
    }
  }

  /** Bounded-memory fallback for names that cannot be placed in the hash table. */
  private static final class Sorted extends InternalNames {
    private final String[] names;

    Sorted(String[] names) {
      Arrays.sort(names);
      int unique = 0;
      for (String name : names) {
        if (unique == 0 || !name.equals(names[unique - 1])) {
          names[unique++] = name;
        }
      }
      this.names = unique == names.length ? names : Arrays.copyOf(names, unique);
    }

    @Override
    public boolean containsType(CharSequence internalName) {
      int low = 0;
      int high = names.length - 1;
      while (low <= high) {
        int mid = (low + high) >>> 1;
        int comparison = compare(names[mid], internalName);
        if (comparison < 0) {
          low = mid + 1;
        } else if (comparison > 0) {
          high = mid - 1;
        } else {
          return true;
        }
      }
      return false;
    }

    private static int compare(String name, CharSequence query) {
      if (query instanceof String) {
        return name.compareTo((String) query);
      }
      int length = Math.min(name.length(), query.length());
      for (int i = 0; i < length; i++) {
        int comparison = name.charAt(i) - query.charAt(i);
        if (comparison != 0) {
          return comparison;
        }
      }
      return name.length() - query.length();
    }

    // ----------------------------------------------------------------------------------------------
    // The rest of this class implements a read-only Set contract for the internal names array
    // ----------------------------------------------------------------------------------------------

    @Override
    public Iterator<String> iterator() {
      return Arrays.asList(names).iterator();
    }

    @Override
    public int size() {
      return names.length;
    }
  }
}
