package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.BytecodeTestSupport.classWriter;
import static datadog.instrument.classmatch.MatcherTestSupport.methodOutline;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ByteVector;
import org.objectweb.asm.ClassWriter;
import org.tabletest.junit.TableTest;

class MalformedClassFileTest {
  @TableTest({
    "Descriptor            | Boundaries",
    "'(I'                  | []        ",
    "'(Ljava/lang/String'  | []        ",
    "'(['                  | []        ",
    "'(ILjava/lang/String' | [2]       ",
    "'(II['                | [2, 3]    "
  })
  void incompleteDescriptorsRetainOnlyPreviouslyParsedBoundaries(
      String descriptor, int[] boundaries) {
    MethodOutline method = methodOutline(descriptor);
    for (int pass = 0; pass < 2; pass++) {
      assertArrayEquals(boundaries, method.descriptorBoundaries());
      assertEquals(boundaries.length, method.parameterCount());
    }
  }

  @TableTest({
    "Scenario                         | Tag",
    "never assigned                   | 0  ",
    "unassigned gap after Utf8        | 2  ",
    "unassigned gap after NameAndType | 13 ",
    "beyond highest defined tag       | 99 "
  })
  void invalidConstantPoolTagsAreRejected(int tag) {
    byte[] bytes = classWriter("tests/BadConstantPool").toByteArray();
    bytes[10] = (byte) tag; // first constant pool entry follows magic/version/count
    assertThrows(IllegalArgumentException.class, () -> ClassFile.header(bytes));
    assertThrows(IllegalArgumentException.class, () -> ClassFile.outline(bytes));
  }

  @Test
  void truncatedClassFilesAreRejected() {
    byte[] bytes = classWriter("tests/Truncated").toByteArray();
    for (int length = 0; length < bytes.length; length++) {
      byte[] truncated = Arrays.copyOf(bytes, length);
      assertThrows(
          IndexOutOfBoundsException.class, () -> ClassFile.outline(truncated), "length " + length);
    }
    for (int length : new int[] {0, 8, 10, 11, bytes.length - 7}) {
      byte[] truncated = Arrays.copyOf(bytes, length);
      assertThrows(
          IndexOutOfBoundsException.class, () -> ClassFile.header(truncated), "length " + length);
    }
  }

  @Test
  void invalidAnnotationElementTagsAreRejected() {
    ClassFile.annotationOfInterest("tests/BadAnnotation");
    ClassWriter writer = classWriter("tests/BadAnnotationValue");
    writer.visitAttribute(new InvalidAnnotation());
    byte[] bytes = writer.toByteArray();
    assertEquals("tests/BadAnnotationValue", ClassFile.header(bytes).className);
    assertThrows(IllegalArgumentException.class, () -> ClassFile.outline(bytes));
  }

  private static final class InvalidAnnotation extends Attribute {
    InvalidAnnotation() {
      super("RuntimeVisibleAnnotations");
    }

    @Override
    protected ByteVector write(
        ClassWriter writer, byte[] code, int codeLength, int maxStack, int maxLocals) {
      return new ByteVector()
          .putShort(1) // one annotation
          .putShort(writer.newUTF8("Ltests/BadAnnotation;"))
          .putShort(1) // one element
          .putShort(writer.newUTF8("value"))
          .putByte('!'); // not a JVM annotation element tag
    }
  }
}
