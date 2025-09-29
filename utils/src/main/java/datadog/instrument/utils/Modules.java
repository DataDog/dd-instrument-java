/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.utils;

import static datadog.instrument.utils.JVM.atLeastJava;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;

/** Methods for managing <a href="https://openjdk.org/projects/jigsaw/spec/">JPMS</a> modules. */
public final class Modules {

  /**
   * Ensures the module has read access to classes dynamically defined in the given class-loaders.
   *
   * @param inst the instrumentation that can redefine modules
   * @param module the module that should be able to read the classes
   * @param classLoaders the class-loaders defining classes to read
   */
  @SuppressWarnings({"Since15"})
  public static void ensureReadability(
      Instrumentation inst, Object module, Iterable<ClassLoader> classLoaders) {
    if (atLeastJava(9)) { // JPMS is only available on Java 9+
      if (!(module instanceof java.lang.Module)) {
        return; // argument is not a real module
      }
      java.lang.Module theModule = (java.lang.Module) module;
      if (!theModule.isNamed()) {
        return; // unnamed modules can read each other
      }
      Set<java.lang.Module> extraReads = new HashSet<>();
      for (ClassLoader cl : classLoaders) {
        java.lang.Module unnamedModule = cl.getUnnamedModule();
        if (!theModule.canRead(unnamedModule)) {
          extraReads.add(unnamedModule);
        }
      }
      if (!extraReads.isEmpty()) {
        inst.redefineModule(theModule, extraReads, emptyMap(), emptyMap(), emptySet(), emptyMap());
      }
    }
  }
}
