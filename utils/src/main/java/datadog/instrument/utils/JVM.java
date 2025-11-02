/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/** Assorted JVM helpers; uses the best approach for the current JVM. */
public final class JVM {
  private static final int JAVA_VERSION = getMajorJavaVersion();

  private JVM() {}

  /**
   * Tests whether the Java version is at least the expected version.
   *
   * @param expectedVersion the expected version
   * @return {@code true} if the Java version is at least the expected version
   */
  public static boolean atLeastJava(int expectedVersion) {
    return expectedVersion <= JAVA_VERSION;
  }

  @SuppressWarnings({"Since15", "deprecation", "RedundantSuppression"})
  private static int getMajorJavaVersion() {
    try {
      return parseMajorJavaVersion(System.getProperty("java.version"));
    } catch (Throwable e1) {
      try {
        return Runtime.version().major();
      } catch (Throwable e2) {
        return 8; // assume Java 8, given Runtime.version() doesn't exist
      }
    }
  }

  private static int parseMajorJavaVersion(String str) {
    int value = 0;
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch >= '0' && ch <= '9') {
        value = value * 10 + (ch - '0');
      } else if (ch == '.') {
        if (value == 1) {
          value = 0; // skip leading 1.
        } else {
          break;
        }
      } else {
        throw new NumberFormatException();
      }
    }
    return value;
  }

  private static final int BUFFER_SIZE = 8192;

  /**
   * Reads all remaining bytes from the given input stream.
   *
   * @param is the input stream
   * @return a byte array containing the bytes read from the input stream
   * @throws IOException if an I/O error occurs
   */
  @SuppressWarnings("Since15")
  public static byte[] readAllBytes(InputStream is) throws IOException {
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
  }
}
