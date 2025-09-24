/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

/**
 * Provides {@link String}-like access to type-strings inside method and field descriptors without
 * allocating a completely separate {@link String}. Hashes are precomputed because type-strings are
 * expected to be used as lookup keys; it also makes the hashes easier to cache and re-use.
 */
final class TypeString implements CharSequence {

  private final String descriptor;
  private final int offset;
  private final int len;
  private final int hash;

  /**
   * Computes the {@link String}-hash for part of the descriptor.
   *
   * @param descriptor the field/method descriptor
   * @param start the start index
   * @param end the end index
   * @return string-hash for the selected range
   */
  static int computeHash(String descriptor, int start, int end) {
    int h = 0;
    for (int i = start; i < end; i++) {
      h = 31 * h + descriptor.charAt(i);
    }
    return h;
  }

  /**
   * Creates a {@link TypeString} window onto a field or method descriptor.
   *
   * @param descriptor the field/method descriptor
   * @param start the start index
   * @param end the end index
   * @param hash the computed hash
   */
  TypeString(String descriptor, int start, int end, int hash) {
    this.descriptor = descriptor;
    this.offset = start;
    this.len = end - start;
    this.hash = hash;
  }

  @Override
  public int length() {
    return len;
  }

  @Override
  public char charAt(int index) {
    return descriptor.charAt(offset + index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    throw new UnsupportedOperationException("Partial TypeStrings not allowed");
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof CharSequence) {
      CharSequence cs = (CharSequence) o;
      if (len != cs.length()) {
        return false;
      }
      for (int i = offset, j = 0; j < len; i++, j++) {
        if (descriptor.charAt(i) != cs.charAt(j)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return descriptor.substring(offset, offset + len);
  }

  /** Avoids string allocation if the char sequence being tested is a {@link TypeString}. */
  static boolean startsWith(CharSequence cs, String prefix) {
    if (cs instanceof TypeString) {
      TypeString ts = (TypeString) cs;
      return ts.descriptor.startsWith(prefix, ts.offset);
    } else {
      return cs.toString().startsWith(prefix);
    }
  }

  /** Avoids string allocation if the char sequence being tested is a {@link TypeString}. */
  static boolean endsWith(CharSequence cs, String suffix) {
    if (cs instanceof TypeString) {
      TypeString ts = (TypeString) cs;
      return ts.descriptor.startsWith(suffix, (ts.offset + ts.len) - suffix.length());
    } else {
      return cs.toString().endsWith(suffix);
    }
  }
}
