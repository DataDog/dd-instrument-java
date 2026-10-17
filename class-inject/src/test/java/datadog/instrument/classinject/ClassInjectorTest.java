package datadog.instrument.classinject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.*;

import datadog.instrument.testing.TestAgent;
import java.security.ProtectionDomain;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

class ClassInjectorTest {

  @BeforeAll
  static void enableClassInjection() {
    ClassInjector.enableClassInjection(TestAgent.getInstrumentation());
  }

  @Test
  void injectsBootClass() throws Exception {
    String name = "datadog.injected.BootSample";
    Class<?> injectedClass = ClassInjector.injectBootClass(name, sampleClass(name));
    assertThat(injectedClass.getClassLoader()).isNull();
    assertThat(Class.forName(name, false, null)).isSameAs(injectedClass);
    assertThat(injectedClass.getMethod("value").invoke(null)).isEqualTo(42);

    // injecting an already defined class returns the existing class
    assertThat(ClassInjector.injectBootClass(name, sampleClass(name))).isSameAs(injectedClass);
  }

  @Test
  void injectsClassIntoGivenLoader() throws Exception {
    ClassLoader loader = newCL();
    String name = "datadog.injected.LoaderSample";
    Class<?> injectedClass = ClassInjector.injectClass(name, sampleClass(name), loader);
    assertThat(injectedClass.getClassLoader()).isSameAs(loader);
    assertThat(loader.loadClass(name)).isSameAs(injectedClass);
    assertThat(injectedClass.getMethod("value").invoke(null)).isEqualTo(42);

    // injecting an already defined class returns the existing class
    assertThat(ClassInjector.injectClass(name, sampleClass(name), loader)).isSameAs(injectedClass);
  }

  @Test
  void injectsClassWithGivenProtectionDomain() throws Exception {
    ClassLoader loader = newCL();
    ProtectionDomain domain = new ProtectionDomain(null, null, loader, null);
    String name = "datadog.injected.DomainSample";
    Class<?> injectedClass = ClassInjector.injectClass(name, sampleClass(name), domain);
    assertThat(injectedClass.getClassLoader()).isSameAs(loader);
    assertThat(injectedClass.getProtectionDomain()).isSameAs(domain);
    assertThat(injectedClass.getMethod("value").invoke(null)).isEqualTo(42);
  }

  @Test
  void injectsClassesThatReferenceEachOther() throws Exception {
    ClassLoader loader = newCL();
    String caller = "datadog.injected.CallerSample";
    String callee = "datadog.injected.CalleeSample";
    Map<String, byte[]> bytecode = new LinkedHashMap<>();
    bytecode.put(caller, delegatingClass(caller, callee));
    bytecode.put(callee, sampleClass(callee));

    List<Class<?>> injectedClasses = ClassInjector.injectClasses(bytecode, loader);
    assertThat(injectedClasses).extracting(Class::getName).containsExactly(caller, callee);
    assertThat(injectedClasses).allSatisfy(c -> assertThat(c.getClassLoader()).isSameAs(loader));
    assertThat(injectedClasses.get(0).getMethod("value").invoke(null)).isEqualTo(42);
  }

  private static ClassLoader newCL() {
    return new ClassLoader(null) {};
  }

  /** Generates a class with a static {@code value()} method that returns 42. */
  private static byte[] sampleClass(String name) {
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

  /** Generates a class with a static {@code value()} method that delegates to the target. */
  private static byte[] delegatingClass(String name, String target) {
    ClassWriter writer = new ClassWriter(0);
    writer.visit(V1_8, ACC_PUBLIC, name.replace('.', '/'), null, "java/lang/Object", null);
    MethodVisitor method = writer.visitMethod(ACC_PUBLIC | ACC_STATIC, "value", "()I", null, null);
    method.visitCode();
    method.visitMethodInsn(INVOKESTATIC, target.replace('.', '/'), "value", "()I", false);
    method.visitInsn(IRETURN);
    method.visitMaxs(1, 0);
    method.visitEnd();
    writer.visitEnd();
    return writer.toByteArray();
  }
}
