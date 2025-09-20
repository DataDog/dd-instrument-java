/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.utils;

/** Provides information about the JVM; used to select the best injection approach. */
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
}
