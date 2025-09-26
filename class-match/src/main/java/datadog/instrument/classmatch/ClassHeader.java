/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

/** Minimal class header that describes its access modifiers and immediate class hierarchy. */
public class ClassHeader {

  /**
   * Access modifiers for this class.
   *
   * @see <a href="https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html#jvms-4.1-200-E.1">
   *     Expected values</a>
   */
  public final int access;

  /** Internal name of this class. */
  public final String className;

  /** Internal name of the super-class declared by this class. */
  public final String superName;

  /** Internal names of the interfaces declared by this class. */
  public final String[] interfaces;

  ClassHeader(int access, String className, String superName, String[] interfaces) {
    this.access = access;
    this.className = className;
    this.superName = superName;
    this.interfaces = interfaces;
  }
}
