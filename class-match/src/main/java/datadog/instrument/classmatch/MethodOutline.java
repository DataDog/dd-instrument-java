/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import java.util.BitSet;
import javax.annotation.Nullable;

/** Outlines a method; access modifiers, method name, descriptor, annotations. */
public final class MethodOutline {

  /** Access modifiers for this method. */
  public final int access;

  /** Name of this method. */
  public final String methodName;

  /** Descriptor containing the raw parameter types and return type. */
  public final String descriptor;

  /** Internal names of annotations declared on this method. */
  public final String[] annotations;

  MethodOutline(int access, String methodName, String descriptor, String[] annotations) {
    this.access = access;
    this.methodName = methodName;
    this.descriptor = descriptor;
    this.annotations = annotations;
  }

  // ----------------------------------------------------------------------------------------------
  // The rest of this class is used to implement advanced matching, while keeping outlines minimal
  // ----------------------------------------------------------------------------------------------

  private static final int[] NO_BOUNDARIES = {};

  /** Lazy cache of boundaries between each parameter/return descriptor. */
  private int[] descriptorBoundaries;

  /** Lazy cache of hashes for each parameter/return type-string. */
  private int[] typeStringHashes;

  /**
   * @return number of method parameters
   */
  int parameterCount() {
    return descriptorBoundaries().length;
  }

  /**
   * Provides a {@link TypeString} of the indexed parameter type for hierarchy matching purposes.
   *
   * @param paramIndex the parameter index
   * @return type-string; {@code null} if the parameter type is primitive, an array, or missing
   */
  @Nullable
  TypeString parameterTypeString(int paramIndex) {
    int[] boundaries = descriptorBoundaries();
    if (paramIndex >= boundaries.length) {
      return null; // method doesn't have enough parameters to match
    }
    // earliest potential param type can be found at index 2 "(L...;"
    int start = paramIndex == 0 ? 2 : boundaries[paramIndex - 1] + 1;
    if (descriptor.charAt(start - 1) != 'L') {
      return null; // don't create type-strings for primitive/array types
    }
    int end = boundaries[paramIndex] - 1;
    return new TypeString(descriptor, start, end, getHash(paramIndex, start, end));
  }

  /**
   * Provides a {@link TypeString} of the return type for hierarchy matching purposes.
   *
   * @return type-string; {@code null} if the return type is primitive, an array, or missing
   */
  @Nullable
  TypeString returnTypeString() {
    int[] boundaries = descriptorBoundaries();
    int returnIndex = boundaries.length;
    // earliest potential return type can be found at index 3 "()L...;"
    int start = returnIndex == 0 ? 3 : boundaries[returnIndex - 1] + 1;
    if (descriptor.charAt(start - 1) != 'L') {
      return null; // don't create type-strings for primitive/array/void types
    }
    int end = descriptor.length() - 1;
    return new TypeString(descriptor, start, end, getHash(returnIndex, start, end));
  }

  /** Gets a previously cached {@link TypeString} hash; otherwise computes and caches a new hash. */
  private int getHash(int typeStringIndex, int start, int end) {
    if (typeStringHashes == null) {
      // allow for one hash per parameter, plus one for return type
      typeStringHashes = new int[descriptorBoundaries.length + 1];
    }
    int hash = typeStringHashes[typeStringIndex];
    if (hash == 0) {
      typeStringHashes[typeStringIndex] = hash = TypeString.computeHash(descriptor, start, end);
    }
    return hash;
  }

  /**
   * Returns the boundaries between each parameter/return descriptor in the method descriptor.
   *
   * <p>The number of boundaries is the same as the number of parameters: there is no boundary
   * before the first parameter, and the return boundary is omitted if there are no parameters.
   * (This is to save space - the first parameter always starts at index 1; and if there are no
   * parameters then the return descriptor always starts at index 2.)
   */
  int[] descriptorBoundaries() {
    if (descriptorBoundaries == null) {
      descriptorBoundaries = parseBoundaries(descriptor);
    }
    return descriptorBoundaries;
  }

  /** Partitions method descriptor into boundaries between each parameter/return descriptor. */
  private static int[] parseBoundaries(String descriptor) {
    char c = descriptor.charAt(1);
    if (c == ')') {
      return NO_BOUNDARIES; // no parsing required
    }
    BitSet partition = new BitSet();
    try {
      int i = 1;
      while (true) {
        while (c == '[') {
          // skip over array marker(s)
          c = descriptor.charAt(++i);
        }
        if (c == 'L') {
          // skip over class-name
          i = descriptor.indexOf(';', i + 2);
          if (i < 0) {
            break; // malformed descriptor; short-circuit parsing
          }
        }
        // either reached next parameter or end of parameters
        c = descriptor.charAt(++i);
        if (c == ')') {
          // record start of return descriptor
          partition.set(++i);
          break; // end of parameters
        }
        // record start of next parameter descriptor
        partition.set(i);
      }
    } catch (IndexOutOfBoundsException e) {
      // malformed descriptor; short-circuit parsing
    }
    // flatten bit-set into primitive array of boundaries
    int[] boundaries = new int[partition.cardinality()];
    for (int i = 0, p = 0, len = boundaries.length; i < len; i++, p++) {
      boundaries[i] = p = partition.nextSetBit(p);
    }
    return boundaries;
  }
}
