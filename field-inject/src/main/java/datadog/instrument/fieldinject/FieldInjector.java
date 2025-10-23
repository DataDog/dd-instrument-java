/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import java.lang.instrument.Instrumentation;

/** Supports injection of fields into {@link ObjectStore} keys to store associated values. */
public final class FieldInjector {

  private FieldInjector() {}

  /**
   * Enables field injection via {@link Instrumentation}.
   *
   * @param inst the instrumentation instance
   * @throws UnsupportedOperationException if field injection is not available
   */
  public static void enableFieldInjection(Instrumentation inst) {
    throw new UnsupportedOperationException("Not yet implemented");
  }
}
