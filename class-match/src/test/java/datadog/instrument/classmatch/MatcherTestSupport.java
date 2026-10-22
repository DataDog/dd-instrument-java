package datadog.instrument.classmatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import datadog.instrument.utils.JVM;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Predicate;

/** Small fixtures with explicit JVM descriptors so expectations don't use production conversion. */
final class MatcherTestSupport {
  private MatcherTestSupport() {}

  static FieldOutline fieldOutline(String descriptor) {
    return fieldOutline(0, "value", descriptor);
  }

  static FieldOutline fieldOutline(int access, String name, String descriptor) {
    return new FieldOutline(access, name, descriptor);
  }

  static MethodOutline methodOutline(String descriptor) {
    return methodOutline(0, "execute", descriptor);
  }

  static MethodOutline methodOutline(int access, String name, String descriptor) {
    return new MethodOutline(access, name, descriptor, new String[0]);
  }

  static ClassOutline classOutline(
      FieldOutline[] fields, MethodOutline[] methods, String... annotations) {
    return new ClassOutline(
        0, "sample/Example", "java/lang/Object", new String[0], fields, methods, annotations);
  }

  static ClassOutline emptyClass() {
    return classOutline(new FieldOutline[0], new MethodOutline[0]);
  }

  static <T> void assertMatches(Predicate<T> matcher, T candidate, boolean expected) {
    // Repeating on the same outline also exercises lazily cached descriptor information.
    assertEquals(expected, matcher.test(candidate));
    assertEquals(expected, matcher.test(candidate));
  }

  static ClassOutline parse(Class<?> fixture) {
    String resource = "/" + fixture.getName().replace('.', '/') + ".class";
    try (InputStream in = fixture.getResourceAsStream(resource)) {
      if (in == null) {
        throw new AssertionError("Missing fixture " + resource);
      }
      return ClassFile.outline(JVM.readAllBytes(in));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
