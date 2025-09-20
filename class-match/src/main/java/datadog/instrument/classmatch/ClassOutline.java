/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.List;

/** Outlines a class; access modifiers, immediate class hierarchy, field, methods, annotations. */
public final class ClassOutline extends ClassHeader {

  /** Outlines of fields declared by this class. */
  final FieldOutline[] fields;

  /** Outlines of methods declared by this class. */
  final MethodOutline[] methods;

  /** Internal names of annotations declared on this class. */
  final String[] annotations;

  /**
   * @return outlines of fields declared by this class
   */
  public List<FieldOutline> fields() {
    return asList(fields);
  }

  /**
   * @return outlines of methods declared by this class
   */
  public List<MethodOutline> methods() {
    return asList(methods);
  }

  /**
   * @return internal names of annotations declared on this class
   */
  public List<String> annotations() {
    return asList(annotations);
  }

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
