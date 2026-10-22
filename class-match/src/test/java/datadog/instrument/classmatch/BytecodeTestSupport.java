package datadog.instrument.classmatch;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_8;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/** ASM fixtures are independent of the parser and descriptor conversion under test. */
final class BytecodeTestSupport {
  private BytecodeTestSupport() {}

  static ClassWriter classWriter(String name) {
    ClassWriter writer = new ClassWriter(0);
    writer.visit(V1_8, ACC_PUBLIC | ACC_ABSTRACT, name, null, "java/lang/Object", null);
    return writer;
  }

  static byte[] annotatedClass(String... names) {
    ClassWriter writer = classWriter("tests/Annotated");
    MethodVisitor method =
        writer.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, "marked", "()V", null, null);
    for (String name : names) {
      writer.visitAnnotation("L" + name + ";", true).visitEnd();
      method.visitAnnotation("L" + name + ";", true).visitEnd();
    }
    method.visitEnd();
    writer.visitEnd();
    return writer.toByteArray();
  }
}
