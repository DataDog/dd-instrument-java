package datadog.instrument.classinject;

import static org.objectweb.asm.Opcodes.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

final class ClassInjectorTestSupport {
  private ClassInjectorTestSupport() {}

  static ClassFileTransformer observeRetransforms(AtomicInteger retransforms) {
    return new ClassFileTransformer() {
      @Override
      public byte[] transform(
          ClassLoader loader,
          String name,
          Class<?> redefined,
          ProtectionDomain domain,
          byte[] bytecode) {
        if (redefined != null
            && ("java/lang/ClassLoader".equals(name) || "java/lang/Class".equals(name))) {
          retransforms.incrementAndGet();
        }
        return null;
      }
    };
  }

  /** Generates a class with a static {@code value()} method that returns 42. */
  static byte[] sampleClass(String name) {
    ClassWriter writer = new ClassWriter(0);
    writer.visit(V1_8, ACC_PUBLIC, name.replace('.', '/'), null, "java/lang/Object", null);
    MethodVisitor method = writer.visitMethod(ACC_PUBLIC | ACC_STATIC, "value", "()I", null, null);
    method.visitCode();
    method.visitIntInsn(BIPUSH, 42);
    method.visitInsn(IRETURN);
    method.visitMaxs(1, 0);
    method.visitEnd();
    writer.visitEnd();
    return writer.toByteArray();
  }
}
