/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

/** Outlines a field; access flags, field name, descriptor. */
public final class FieldOutline {

  public final int access;
  public final String fieldName;
  public final String descriptor;

  FieldOutline(int access, String fieldName, String descriptor) {
    this.access = access;
    this.fieldName = fieldName;
    this.descriptor = descriptor;
  }
}
