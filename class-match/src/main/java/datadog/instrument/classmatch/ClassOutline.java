/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

/** Outlines a class; access modifiers, immediate class hierarchy, field, methods, annotations. */
public final class ClassOutline extends ClassHeader {

  /** Outlines of fields declared by this class. */
  public final FieldOutline[] fields;

  /** Outlines of methods declared by this class. */
  public final MethodOutline[] methods;

  /** Internal names of annotations declared on this class. */
  public final String[] annotations;

  ClassOutline(
      int access,
      String className,
      String superName,
      String[] interfaces,
      FieldOutline[] fields,
      MethodOutline[] methods,
      String[] annotations) {
    super(access, className, superName, interfaces);
    this.fields = fields;
    this.methods = methods;
    this.annotations = annotations;
  }
}
