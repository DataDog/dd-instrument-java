/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2026-Present Datadog, Inc.
 */

package datadog.instrument.testing;

import java.lang.instrument.Instrumentation;

/** Supplies instrumentation to tests when the testing jar is used as a startup agent. */
public final class TestAgent {
  private static volatile Instrumentation instrumentation;

  private TestAgent() {}

  /** Called by the JVM when the testing jar is supplied with {@code -javaagent}. */
  public static void premain(String arguments, Instrumentation inst) {
    instrumentation = inst;
  }

  /** Returns the instrumentation supplied at JVM startup. */
  public static Instrumentation getInstrumentation() {
    if (instrumentation == null) {
      throw new IllegalStateException("The testing jar must be supplied with -javaagent");
    }
    return instrumentation;
  }
}
