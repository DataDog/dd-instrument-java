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
 * Immutable set of internal names using linear probing. A parallel array of cached hashes lets
 * probes skip strings whose hashes differ.
 *
 * <p>Queries may be any {@link CharSequence} with a {@link String}-compatible hash code.
 */
final class InternalNames extends AbstractSet<String> {
  private final int[] hashes;
  private final String[] table;
  private final int slotMask;

  /**
   * Creates a new set of internal names containing the given types of interest.
   *
   * @param types the types of interest
   */
  InternalNames(Collection<String> types) {
    // At most 50% full, leaving empty slots to terminate probes.
    int tableSize = Math.max(8, types.size() * 2) - 1;

    int slotMask = -1 >>> Integer.numberOfLeadingZeros(tableSize);
    int[] hashes = new int[slotMask + 1];
    String[] table = new String[slotMask + 1];

    for (String type : types) {
      final String internalName = internalName(type);
      final int h = hash(internalName.hashCode());

      int hashed, slot = h & slotMask;
      while ((hashed = hashes[slot]) != 0) {
        if (hashed == h && table[slot].equals(internalName)) {
          break;
        }
        slot = (slot + 1) & slotMask;
      }

      hashes[slot] = h;
      table[slot] = internalName;
    }

    this.hashes = hashes;
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
    final int h = hash(internalName.hashCode());

    final int slotMask = this.slotMask;
    final int[] hashes = this.hashes;
    final String[] table = this.table;

    int hashed, slot = h & slotMask;
    while ((hashed = hashes[slot]) != 0) {
      if (hashed == h && table[slot].contentEquals(internalName)) {
        return true;
      }
      slot = (slot + 1) & slotMask;
    }
    return false;
  }

  private static int hash(int hashCode) {
    return hashCode != 0 ? hashCode : 0xDD06; // reserve 0 for "no hash"
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
