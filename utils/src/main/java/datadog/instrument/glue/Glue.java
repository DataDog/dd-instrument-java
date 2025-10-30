/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import static java.nio.charset.StandardCharsets.UTF_16BE;

import datadog.instrument.utils.JVM;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Objects;

/** Methods for loading instrumentation glue bytecode from string literals and resource files. */
public final class Glue {

  private static final String GLUE_RESOURCE_PREFIX = "/datadog/instrument/glue/";

  private static final int BUFFER_SIZE = 8192;

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
  @SuppressWarnings({"Since15"})
  public static byte[] loadBytecode(Class<?> host, String glueName) {
    String glueResource = GLUE_RESOURCE_PREFIX + glueName;
    try (InputStream is = Objects.requireNonNull(host.getResourceAsStream(glueResource))) {
      if (JVM.atLeastJava(9)) {
        return is.readAllBytes();
      } else {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
          int bytesRead;
          byte[] buf = new byte[BUFFER_SIZE];
          while ((bytesRead = is.read(buf, 0, BUFFER_SIZE)) != -1) {
            os.write(buf, 0, bytesRead);
          }
          return os.toByteArray();
        }
      }
    } catch (Throwable e) {
      String detail = "Cannot load " + glueResource + " from " + host + ": " + e;
      throw new MissingResourceException(detail, host.getName(), glueResource);
    }
  }
}
