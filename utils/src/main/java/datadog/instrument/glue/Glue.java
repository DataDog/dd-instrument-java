/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import static java.nio.charset.StandardCharsets.UTF_16BE;
import static java.util.Objects.requireNonNull;

import datadog.instrument.utils.JVM;
import java.io.InputStream;
import java.util.MissingResourceException;

/** Methods for loading instrumentation glue bytecode from string literals and resource files. */
public final class Glue {
  private static final String GLUE_RESOURCE_PREFIX = "/datadog/instrument/glue/";

  private Glue() {}

  /**
   * Unpacks a string literal produced by {@link GlueGenerator#packBytecode} back into bytecode.
   *
   * @param bytecode the packed bytecode
   * @return the unpacked bytecode
   */
  public static byte[] unpackBytecode(String bytecode) {
    return bytecode.getBytes(UTF_16BE);
  }

  /**
   * Loads glue bytecode with the given name from the given host.
   *
   * @param host the host class used to load the glue resource
   * @param glueName the glue resource containing the bytecode
   * @return the glue bytecode
   * @throws MissingResourceException if the bytecode cannot be read
   */
  public static byte[] loadBytecode(Class<?> host, String glueName) {
    String glueResource = GLUE_RESOURCE_PREFIX + glueName;
    try (InputStream is = requireNonNull(host.getResourceAsStream(glueResource))) {
      return JVM.readAllBytes(is);
    } catch (Throwable e) {
      String detail = "Cannot load " + glueResource + " from " + host + ": " + e;
      throw new MissingResourceException(detail, host.getName(), glueResource);
    }
  }
}
