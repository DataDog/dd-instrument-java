package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.BytecodeTestSupport.classWriter;
import static datadog.instrument.classmatch.FieldMatcher.field;
import static datadog.instrument.classmatch.MatcherTestSupport.assertMatches;
import static datadog.instrument.classmatch.MethodMatcher.method;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.ClassWriter;

class ClassFileUnicodeTest {
  @ParameterizedTest
  @ValueSource(strings = {"Café", "Grüße", "例クラス", "\uD801\uDC00", "Asciié例\uD801\uDC00End"})
  void modifiedUtf8NamesRoundTripAtEveryPositionAndAtAnOffset(String name) {
    String className = "tests/" + name;
    String descriptor = "L" + className + ";";
    ClassWriter writer = classWriter(className);
    writer.visitField(ACC_PUBLIC, "field" + name, descriptor, null, null).visitEnd();
    writer
        .visitMethod(
            ACC_PUBLIC | ACC_ABSTRACT,
            "method" + name,
            "(" + descriptor + "I)" + descriptor,
            null,
            null)
        .visitEnd();
    writer.visitEnd();
    byte[] bytes = writer.toByteArray();
    for (int offset : new int[] {0, 29}) {
      byte[] input = new byte[bytes.length + offset];
      System.arraycopy(bytes, 0, input, offset, bytes.length);
      ClassHeader header = ClassFile.header(input, offset);
      ClassOutline outline = ClassFile.outline(input, offset);
      assertEquals(className, header.className);
      assertEquals(className, outline.className);
      assertEquals("field" + name, outline.fields[0].fieldName);
      assertEquals(descriptor, outline.fields[0].descriptor);
      assertEquals("method" + name, outline.methods[0].methodName);
      assertEquals("(" + descriptor + "I)" + descriptor, outline.methods[0].descriptor);
      assertMatches(field("field" + name).type(className), outline.fields[0], true);
      assertMatches(
          method("method" + name).parameters(className, "int").returning(className),
          outline.methods[0],
          true);
    }
  }
}
