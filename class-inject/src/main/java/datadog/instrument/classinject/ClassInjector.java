/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classinject;

import static java.util.Collections.singletonMap;
import static org.objectweb.asm.Opcodes.*;

import datadog.instrument.glue.DefineClassGlue;
import datadog.instrument.utils.JVM;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Supports injection of auxiliary classes, even on the bootstrap classpath.
 *
 * <p>Uses {@link Instrumentation} to access {@code ClassLoader.defineClass} without reflection.
 *
 * <ul>
 *   <li>To use this feature, first call {@link #enableClassInjection}
 *   <li>To inject classes call {@link #injectClasses} with the target class-loader
 *   <li>Use {@link #injectBootClasses} to inject classes on the bootstrap classpath
 *   <li>The API also supports injecting classes using a custom {@link ProtectionDomain}
 * </ul>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public final class ClassInjector {

  /**
   * Injects a class on the bootstrap classpath.
   *
   * @param name the name of the class
   * @param bytecode the class bytecode
   * @return the injected class
   * @throws IllegalStateException if class injection is not enabled
   */
  public static Class<?> injectBootClass(String name, byte[] bytecode) {
    return injectBootClasses(singletonMap(name, bytecode)).get(0);
  }

  /**
   * Injects a class using the given class-loader.
   *
   * @param name the name of the class
   * @param bytecode the class bytecode
   * @param cl the class-loader to use
   * @return the injected class
   * @throws IllegalStateException if class injection is not enabled
   */
  public static Class<?> injectClass(String name, byte[] bytecode, ClassLoader cl) {
    return injectClasses(singletonMap(name, bytecode), cl).get(0);
  }

  /**
   * Injects a class using the given protection domain.
   *
   * @param name the name of the class
   * @param bytecode the class bytecode
   * @param pd the protection domain to use
   * @return the injected class
   * @throws IllegalStateException if class injection is not enabled
   */
  public static Class<?> injectClass(String name, byte[] bytecode, ProtectionDomain pd) {
    return injectClasses(singletonMap(name, bytecode), pd).get(0);
  }

  /**
   * Injects classes on the bootstrap classpath.
   *
   * @param bytecode the named bytecode to inject
   * @return list of injected classes
   * @throws IllegalStateException if class injection is not enabled
   */
  public static List<Class<?>> injectBootClasses(Map<String, byte[]> bytecode) {
    return (List<Class<?>>) classDefiner().apply(bytecode, null);
  }

  /**
   * Injects classes using the given class-loader.
   *
   * @param bytecode the named bytecode to inject
   * @param cl the class-loader to use
   * @return list of injected classes
   * @throws IllegalStateException if class injection is not enabled
   */
  public static List<Class<?>> injectClasses(Map<String, byte[]> bytecode, ClassLoader cl) {
    return (List<Class<?>>) classDefiner().apply(bytecode, cl);
  }

  /**
   * Injects classes using the given protection domain.
   *
   * @param bytecode the named bytecode to inject
   * @param pd the protection domain to use
   * @return list of injected classes
   * @throws IllegalStateException if class injection is not enabled
   */
  public static List<Class<?>> injectClasses(Map<String, byte[]> bytecode, ProtectionDomain pd) {
    return (List<Class<?>>) classDefiner().apply(bytecode, pd);
  }

  private ClassInjector() {}

  private static BiFunction classDefiner() {
    if (defineClassGlue == null) {
      throw new IllegalStateException("Class injection not enabled");
    }
    return defineClassGlue;
  }

  private static volatile BiFunction defineClassGlue;

  /**
   * Enables class injection via {@link Instrumentation}.
   *
   * @param inst the instrumentation instance
   * @throws UnsupportedOperationException if class injection is not available
   */
  public static void enableClassInjection(Instrumentation inst) {
    if (defineClassGlue != null) {
      return;
    }
    try {
      InjectGlue injectGlue = new InjectGlue();
      try {
        // temporary transformation to install our glue to access 'defineClass'
        inst.addTransformer(injectGlue, true);
        inst.retransformClasses(Class.class);
        defineClassGlue = (BiFunction) Class.forName(DefineClassGlue.ID).newInstance();
      } finally {
        inst.removeTransformer(injectGlue);
        inst.retransformClasses(Class.class);
      }
    } catch (Throwable e) {
      throw new UnsupportedOperationException("Class injection not available", e);
    }
  }

  static final class InjectGlue implements ClassFileTransformer {
    @Override
    public byte[] transform(
        ClassLoader loader,
        String className,
        Class<?> classBeingRedefined,
        ProtectionDomain protectionDomain,
        byte[] bytecode) {
      if ("java/lang/Class".equals(className)) {
        ClassReader cr = new ClassReader(bytecode);
        ClassWriter cw = new ClassWriter(cr, 0);
        cr.accept(new ClassPatch(cw), 0);
        return cw.toByteArray();
      } else {
        return null;
      }
    }
  }

  static final class ClassPatch extends ClassVisitor {
    ClassPatch(ClassVisitor cv) {
      super(ASM9, cv);
    }

    @Override
    public MethodVisitor visitMethod(
        int access, String name, String descriptor, String signature, String[] exceptions) {
      MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
      if ((access & ACC_STATIC) != 0
          && "forName".equals(name)
          && "(Ljava/lang/String;)Ljava/lang/Class;".equals(descriptor)) {
        return new ForNamePatch(mv);
      }
      return mv;
    }
  }

  static final class ForNamePatch extends MethodVisitor {
    ForNamePatch(MethodVisitor mv) {
      super(ASM9, mv);
    }

    @Override
    public void visitCode() {
      mv.visitCode();

      Label notDatadogGlueRequest = new Label();

      // add branch at start of Class.forName method to define our glue as a hidden/anonymous class
      mv.visitLdcInsn(DefineClassGlue.ID);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(
          INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
      mv.visitJumpInsn(IFEQ, notDatadogGlueRequest);

      if (JVM.atLeastJava(15)) {
        // on Java 15+ prepare MethodHandles.lookup()
        mv.visitMethodInsn(
            INVOKESTATIC,
            "java/lang/invoke/MethodHandles",
            "lookup",
            "()Ljava/lang/invoke/MethodHandles$Lookup;",
            false);
        mv.visitLdcInsn(DefineClassGlue.V9);
      } else if (JVM.atLeastJava(9)) {
        // on Java 9+ prepare jdk.internal.misc.Unsafe
        mv.visitMethodInsn(
            INVOKESTATIC,
            "jdk/internal/misc/Unsafe",
            "getUnsafe",
            "()Ljdk/internal/misc/Unsafe;",
            false);
        mv.visitLdcInsn(Type.getType(ClassLoader.class));
        mv.visitLdcInsn(DefineClassGlue.V9);
      } else {
        // on Java 8 prepare sun.misc.Unsafe
        mv.visitMethodInsn(
            INVOKESTATIC, "sun/misc/Unsafe", "getUnsafe", "()Lsun/misc/Unsafe;", false);
        mv.visitLdcInsn(Type.getType(ClassLoader.class));
        mv.visitLdcInsn(DefineClassGlue.V8);
      }

      // unpack the UTF-16BE encoded string back into bytecode
      mv.visitFieldInsn(
          GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_16BE", "Ljava/nio/charset/Charset;");
      mv.visitMethodInsn(
          INVOKEVIRTUAL, "java/lang/String", "getBytes", "(Ljava/nio/charset/Charset;)[B", false);

      if (JVM.atLeastJava(15)) {
        // on Java 15+ use MethodHandles.lookup().defineHiddenClass(...)
        mv.visitInsn(ICONST_0);
        mv.visitInsn(ICONST_0);
        mv.visitTypeInsn(ANEWARRAY, "java/lang/invoke/MethodHandles$Lookup$ClassOption");
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/invoke/MethodHandles$Lookup",
            "defineHiddenClass",
            "([BZ[Ljava/lang/invoke/MethodHandles$Lookup$ClassOption;)Ljava/lang/invoke/MethodHandles$Lookup;",
            false);
        // use lookupClass() to retrieve the hidden class we just defined
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "java/lang/invoke/MethodHandles$Lookup",
            "lookupClass",
            "()Ljava/lang/Class;",
            false);
      } else if (JVM.atLeastJava(9)) {
        // on Java 9+ use jdk.internal.misc.Unsafe.defineAnonymousClass(...)
        mv.visitInsn(ACONST_NULL);
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "jdk/internal/misc/Unsafe",
            "defineAnonymousClass",
            "(Ljava/lang/Class;[B[Ljava/lang/Object;)Ljava/lang/Class;",
            false);
      } else {
        // on Java 8 use sun.misc.Unsafe.defineAnonymousClass(...)
        mv.visitInsn(ACONST_NULL);
        mv.visitMethodInsn(
            INVOKEVIRTUAL,
            "sun/misc/Unsafe",
            "defineAnonymousClass",
            "(Ljava/lang/Class;[B[Ljava/lang/Object;)Ljava/lang/Class;",
            false);
      }

      mv.visitInsn(ARETURN);

      // otherwise this is a standard Class.forName request, handle it as before
      mv.visitLabel(notDatadogGlueRequest);
      mv.visitFrame(F_SAME, 0, null, 0, null);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
      // ensure we have enough stack allocated for our code
      mv.visitMaxs(Math.max(maxStack, 4), maxLocals);
    }
  }
}
