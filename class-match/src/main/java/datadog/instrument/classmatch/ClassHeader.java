/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.List;

/** Minimal class header that describes its access flags and immediate class hierarchy. */
public class ClassHeader {

  public final int access;
  public final String className;
  public final String superName;
  final String[] interfaces;

  public List<String> interfaces() {
    return asList(interfaces);
  }

  ClassHeader(int access, String className, String superName, String[] interfaces) {
    this.access = access;
    this.className = className;
    this.superName = superName;
    this.interfaces = interfaces;
  }
}
