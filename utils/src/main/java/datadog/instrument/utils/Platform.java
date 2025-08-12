package datadog.instrument.utils;

/** Provides information about the runtime platform; used to select the best injection approach. */
public final class Platform {
  private static final int JAVA_VERSION = getMajorJavaVersion();

  private Platform() {}

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
