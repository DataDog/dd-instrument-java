/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.internalName;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Compact immutable hashtable of internal names, representing types to be matched against.
 *
 * <p>The key feature of this implementation is that it supports querying by arbitrary {@link
 * CharSequence}s, as long as the char sequence uses the same hash algorithm as {@link String}.
 */
final class InternalNames extends AbstractSet<String> {
  private static final int MAX_HASH_ATTEMPTS = 3;

  private final String[] table;
  private final int slotMask;

  /**
   * Creates a new set of internal names containing the given types of interest.
   *
   * @param types the types of interest
   */
  InternalNames(Collection<String> types) {
    // attempt to hash types into a table with ~75% load factor
    int tableSize = Math.max(8, types.size() * 4 / 3) - 1;
    int slotMask = -1 >>> Integer.numberOfLeadingZeros(tableSize);
    String[] table = new String[slotMask + 1];
    Iterator<String> itr = types.iterator();
    while (itr.hasNext()) {
      // add types one by one, watching out for unsolvable collisions
      if (!add(table, slotMask, internalName(itr.next()))) {
        // cannot add type without collision; grow table and restart additions
        slotMask = (slotMask << 1) + 1;
        table = new String[slotMask + 1];
        itr = types.iterator();
      }
    }
    this.table = table;
    this.slotMask = slotMask;
  }

  /**
   * Returns {@code true} if the set of internal names contains the given type.
   *
   * @param internalName the internal name of the type
   * @return {@code true} if the set contains this type; otherwise {@code false}
   */
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

  private static int rehash(int oldHash) {
    return Integer.reverseBytes(oldHash * 0x9e3775cd) * 0x9e3775cd;
  }

  // ----------------------------------------------------------------------------------------------
  // The rest of this class implements a read-only Set contract for the internal names hashtable
  // ----------------------------------------------------------------------------------------------

  @Override
  public boolean contains(Object o) {
    return o instanceof CharSequence && containsType((CharSequence) o);
  }

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
