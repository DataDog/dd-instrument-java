/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import javax.annotation.Nullable;

/** Outlines a field; access modifiers, field name, descriptor. */
public final class FieldOutline {

  /**
   * Access modifiers for this field.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.5-200-A.1">
   *     Expected values</a>
   */
  public final int access;

  /** Name of this field. */
  public final String fieldName;

  /** Descriptor containing the raw field type. */
  public final String descriptor;

  FieldOutline(int access, String fieldName, String descriptor) {
    this.access = access;
    this.fieldName = fieldName;
    this.descriptor = descriptor;
  }

  // ----------------------------------------------------------------------------------------------
  // The rest of this class is used to implement advanced matching, while keeping outlines minimal
  // ----------------------------------------------------------------------------------------------

  /** Lazy cache of the field's type-string hash. */
  private int typeStringHash;

  /**
   * Provides a {@link TypeString} of the field type for hierarchy matching purposes.
   *
   * @return type-string; {@code null} if the field type is primitive or an array
   */
  @Nullable
  TypeString typeString() {
    if (descriptor.charAt(0) != 'L') {
      return null; // don't create type-strings for primitive/array types
    }
    int start = 1;
    int end = descriptor.length() - 1;
    if (typeStringHash == 0) {
      typeStringHash = TypeString.computeHash(descriptor, start, end);
    }
    return new TypeString(descriptor, start, end, typeStringHash);
  }
}
