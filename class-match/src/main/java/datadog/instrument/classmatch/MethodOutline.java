/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.BitSet;
import java.util.List;

/** Outlines a method; access modifiers, method name, descriptor, annotations. */
public final class MethodOutline {

  /** Access modifiers for this method. */
  public final int access;

  /** Name of this method. */
  public final String methodName;

  /** Descriptor containing the raw parameter types and return type. */
  public final String descriptor;

  /** Internal names of annotations declared on this method. */
  final String[] annotations;

  /** Lazy cache of boundaries between each parameter/return descriptor. */
  private int[] descriptorBoundaries;

  /**
   * @return internal names of annotations declared on this method
   */
  public List<String> annotations() {
    return asList(annotations);
  }

  MethodOutline(int access, String methodName, String descriptor, String[] annotations) {
    this.access = access;
    this.methodName = methodName;
    this.descriptor = descriptor;
    this.annotations = annotations;
  }

  private static final int[] NO_BOUNDARIES = {};

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
