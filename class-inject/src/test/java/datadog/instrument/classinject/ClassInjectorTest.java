package datadog.instrument.classinject;

import static datadog.instrument.classinject.ClassInjectorTestSupport.observeRetransforms;
import static datadog.instrument.classinject.ClassInjectorTestSupport.sampleClass;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.objectweb.asm.Opcodes.*;

import datadog.instrument.glue.DefineClassGlue;
import datadog.instrument.testing.TestAgent;
import datadog.instrument.utils.JVM;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

class ClassInjectorTest {

  @BeforeAll
  static void enableClassInjection() {
    Instrumentation inst = TestAgent.getInstrumentation();
    AtomicInteger retransforms = new AtomicInteger();
    ClassFileTransformer observer = observeRetransforms(retransforms);
    inst.addTransformer(observer, true);
    try {
      ClassInjector.enableClassInjection(inst);
      assertThat(retransforms.get()).isEqualTo(JVM.atLeastJava(15) ? 0 : 2);
    } finally {
      inst.removeTransformer(observer);
    }
  }

  @Test
  void glueIsNotDiscoverableByName() throws Exception {
    assertThatThrownBy(() -> Class.forName(DefineClassGlue.ID, false, null))
        .isInstanceOf(ClassNotFoundException.class);
    Class<?> glue = glueClass(ClassInjector.class);
    assertThat(glue.getClassLoader()).isNull();
    assertThatThrownBy(() -> Class.forName(glue.getName(), false, null))
        .isInstanceOf(ClassNotFoundException.class);
    if (JVM.atLeastJava(15)) {
      assertThat(Class.class.getMethod("isHidden").invoke(glue)).isEqualTo(true);
    }
  }

  @Test
  void independentLibraryCopyGetsItsOwnGlue() throws Exception {
    try (URLClassLoader copy = isolatedLibrary()) {
      Class<?> injector = copy.loadClass(ClassInjector.class.getName());
      injector
          .getMethod("enableClassInjection", Instrumentation.class)
          .invoke(null, TestAgent.getInstrumentation());
      assertThat(glueClass(injector)).isNotSameAs(glueClass(ClassInjector.class));
      assertThat(injectSample(injector)).isEqualTo(42);
    }
  }

  @Test
  void fallsBackWhenModuleOpeningFails() throws Exception {
    AtomicInteger moduleOpenAttempts = new AtomicInteger();
    AtomicInteger retransforms = new AtomicInteger();
    Instrumentation inst = TestAgent.getInstrumentation();
    Instrumentation refusingModuleOpen =
        (Instrumentation)
            Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] {Instrumentation.class},
                (proxy, method, args) -> {
                  if (method.getName().equals("redefineModule")) {
                    moduleOpenAttempts.incrementAndGet();
                    throw new UnsupportedOperationException("Module opening refused");
                  }
                  try {
                    return method.invoke(inst, args);
                  } catch (InvocationTargetException e) {
                    throw e.getCause();
                  }
                });
    ClassFileTransformer observer = observeRetransforms(retransforms);
    inst.addTransformer(observer, true);
    try (URLClassLoader copy = isolatedLibrary()) {
      Class<?> injector = copy.loadClass(ClassInjector.class.getName());
      injector
          .getMethod("enableClassInjection", Instrumentation.class)
          .invoke(null, refusingModuleOpen);
      assertThat(moduleOpenAttempts.get()).isEqualTo(JVM.atLeastJava(15) ? 1 : 0);
      assertThat(retransforms.get()).isEqualTo(2);
      assertThat(injectSample(injector)).isEqualTo(42);
    } finally {
      inst.removeTransformer(observer);
    }
  }

  @Test
  @SuppressWarnings({"Since15", "unchecked"})
  void onlyJavaLangInvokeIsOpenedToTheLookupHelpersModule() throws Exception {
    if (JVM.atLeastJava(17)) {
      AtomicReference<Module> helperModule = new AtomicReference<>();
      AtomicInteger retransforms = new AtomicInteger();
      Instrumentation inst = TestAgent.getInstrumentation();
      Instrumentation observingModuleOpen =
          (Instrumentation)
              Proxy.newProxyInstance(
                  getClass().getClassLoader(),
                  new Class<?>[] {Instrumentation.class},
                  (proxy, method, args) -> {
                    if (method.getName().equals("redefineModule")) {
                      assertThat(args[0]).isSameAs(Object.class.getModule());
                      Map<String, Set<Module>> extraOpens = (Map<String, Set<Module>>) args[3];
                      assertThat(extraOpens).containsOnlyKeys("java.lang.invoke");
                      assertThat(extraOpens.get("java.lang.invoke")).hasSize(1);
                      helperModule.set(extraOpens.get("java.lang.invoke").iterator().next());
                    } else if (method.getName().equals("retransformClasses")) {
                      retransforms.incrementAndGet();
                    }
                    try {
                      return method.invoke(inst, args);
                    } catch (InvocationTargetException e) {
                      throw e.getCause();
                    }
                  });
      try (URLClassLoader copy = isolatedLibrary();
          URLClassLoader unrelated = URLClassLoader.newInstance(new URL[0], null)) {
        Class<?> injector = copy.loadClass(ClassInjector.class.getName());
        injector
            .getMethod("enableClassInjection", Instrumentation.class)
            .invoke(null, observingModuleOpen);
        assertThat(retransforms.get()).isZero();
        assertThat(helperModule.get()).isNotNull().isNotSameAs(injector.getModule());
        ClassLoader helperLoader = helperModule.get().getClassLoader();
        assertThat(helperLoader).isNotNull().isNotSameAs(copy);
        assertThat(helperLoader.getParent()).isNull();
        assertThat(Object.class.getModule().isOpen("java.lang.invoke", helperModule.get()))
            .isTrue();
        assertThat(Object.class.getModule().isOpen("java.lang.invoke", injector.getModule()))
            .isFalse();
        assertThat(Object.class.getModule().isOpen("java.lang.invoke", getClass().getModule()))
            .isFalse();
        assertThat(
                Object.class.getModule().isOpen("java.lang.invoke", unrelated.getUnnamedModule()))
            .isFalse();
        assertThat(Object.class.getModule().isOpen("java.lang", helperModule.get())).isFalse();
        assertThat(Lookup.class.getDeclaredConstructor(Class.class).trySetAccessible()).isFalse();

        Class<?> helper =
            helperLoader.loadClass("datadog.instrument.glue.$Datadog$ClassLoaderLookup");
        assertThat(helper.getProtectionDomain()).isSameAs(injector.getProtectionDomain());
        assertThat(helper.getDeclaredConstructors()).isEmpty();
        Lookup lookup = (Lookup) helper.getMethod("lookup").invoke(null);
        assertThat(lookup.lookupClass()).isSameAs(ClassLoader.class);
        assertThat(lookup.hasFullPrivilegeAccess()).isTrue();
        assertThat(injectSample(injector)).isEqualTo(42);
      }
    }
  }

  @Test
  void repeatedEnablementPreservesClassInjection() throws Exception {
    ClassLoader loader = newCL();
    String name = "datadog.injected.RepeatedEnablementSample";
    byte[] bytecode = sampleClass(name);
    Class<?> injected = ClassInjector.injectClass(name, bytecode, loader);

    ClassInjector.enableClassInjection(TestAgent.getInstrumentation());

    assertThat(ClassInjector.injectClass(name, bytecode, loader)).isSameAs(injected);
    assertThat(injected.getMethod("value").invoke(null)).isEqualTo(42);
    ClassLoader anotherLoader = newCL();
    Class<?> another = ClassInjector.injectClass(name, bytecode, anotherLoader);
    assertThat(another).isNotSameAs(injected);
    assertThat(another.getClassLoader()).isSameAs(anotherLoader);
    assertThat(another.getMethod("value").invoke(null)).isEqualTo(42);
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

    assertThat(ClassInjector.injectClass(name, sampleClass(name), domain)).isSameAs(injectedClass);
  }

  @Test
  void injectsBootClassWithGivenProtectionDomain() throws Exception {
    ProtectionDomain domain = new ProtectionDomain(null, null, null, null);
    String name = "datadog.injected.BootDomainSample";
    Class<?> injectedClass = ClassInjector.injectClass(name, sampleClass(name), domain);
    assertThat(injectedClass.getClassLoader()).isNull();
    assertThat(Class.forName(name, false, null)).isSameAs(injectedClass);
    assertThat(injectedClass.getProtectionDomain()).isSameAs(domain);
    assertThat(injectedClass.getMethod("value").invoke(null)).isEqualTo(42);

    assertThat(ClassInjector.injectClass(name, sampleClass(name), domain)).isSameAs(injectedClass);
  }

  @Test
  void injectsBootClassesThatReferenceEachOther() throws Exception {
    String caller = "datadog.injected.BootCallerSample";
    String callee = "datadog.injected.BootCalleeSample";
    Map<String, byte[]> bytecode = new LinkedHashMap<>();
    bytecode.put(caller, delegatingClass(caller, callee));
    bytecode.put(callee, sampleClass(callee));

    List<Class<?>> injectedClasses = ClassInjector.injectBootClasses(bytecode);
    assertThat(injectedClasses).extracting(Class::getName).containsExactly(caller, callee);
    assertThat(injectedClasses).allSatisfy(c -> assertThat(c.getClassLoader()).isNull());
    assertThat(Class.forName(caller, false, null)).isSameAs(injectedClasses.get(0));
    assertThat(Class.forName(callee, false, null)).isSameAs(injectedClasses.get(1));
    assertThat(injectedClasses.get(0).getMethod("value").invoke(null)).isEqualTo(42);

    assertThat(ClassInjector.injectBootClasses(bytecode))
        .containsExactlyElementsOf(injectedClasses);
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

  private static URLClassLoader isolatedLibrary() {
    return new URLClassLoader(
        new URL[] {
          ClassInjector.class.getProtectionDomain().getCodeSource().getLocation(),
          JVM.class.getProtectionDomain().getCodeSource().getLocation(),
          ClassReader.class.getProtectionDomain().getCodeSource().getLocation()
        },
        null);
  }

  private static Class<?> glueClass(Class<?> injector) throws Exception {
    Field glue = injector.getDeclaredField("defineClassGlue");
    glue.setAccessible(true);
    return glue.get(null).getClass();
  }

  private static Object injectSample(Class<?> injector) throws Exception {
    String name = "datadog.injected.IsolatedSample";
    Class<?> sample =
        (Class<?>)
            injector
                .getMethod("injectClass", String.class, byte[].class, ClassLoader.class)
                .invoke(null, name, sampleClass(name), newCL());
    return sample.getMethod("value").invoke(null);
  }

  private static ClassLoader newCL() {
    return new ClassLoader(null) {};
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
