package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.BytecodeTestSupport.annotatedClass;

import java.util.Arrays;
import java.util.Collections;

/** Entry point for testing initial static state in a separate JVM, independent of JUnit order. */
public final class AnnotationRegistrationProbe {
  private AnnotationRegistrationProbe() {}

  public static void main(String[] args) {
    byte[] bytes = annotatedClass("tests/First", "tests/Second");
    expect(bytes); // no registration has occurred in this JVM
    if ("single".equals(args[0])) {
      ClassFile.annotationOfInterest("tests/First");
    } else {
      ClassFile.annotationsOfInterest(Collections.singletonList("tests/First"));
    }
    expect(bytes, "tests/First");
    // Both registration APIs must preserve earlier names and tolerate repeated registration.
    ClassFile.annotationsOfInterest(Arrays.asList("tests/First", "tests/Second"));
    ClassFile.annotationOfInterest("tests/First");
    ClassFile.annotationsOfInterest(Collections.emptyList());
    expect(bytes, "tests/First", "tests/Second");
  }

  private static void expect(byte[] bytes, String... expected) {
    ClassOutline outline = ClassFile.outline(bytes);
    if (!Arrays.equals(expected, outline.annotations)
        || !Arrays.equals(expected, outline.methods[0].annotations)) {
      throw new AssertionError(
          "Expected "
              + Arrays.toString(expected)
              + ", class="
              + Arrays.toString(outline.annotations)
              + ", method="
              + Arrays.toString(outline.methods[0].annotations));
    }
  }
}
