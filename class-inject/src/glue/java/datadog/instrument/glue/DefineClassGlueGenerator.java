/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import static datadog.instrument.utils.Glue.classHeader;
import static datadog.instrument.utils.Glue.packBytecode;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

/**
 * Generates glue bytecode for a {@link BiFunction} around {@code ClassLoader.defineClass} that
 * accepts a map of class-names to bytecode and a class-loader (or protection domain) and returns
 * the defined classes. The glue bytecode is encoded as a UTF-16BE string and written as a constant
 * inside a Java class at build-time.
 *
 * <p>At runtime the glue bytecode is decoded from the string and defined as a hidden/anonymous
 * class using instrumentation. This class provides access to {@code ClassLoader.defineClass}
 * without using unsafe reflection.
 *
 * <p>Two versions of glue bytecode are generated: one for Java 8 and another for Java 9 and above.
 */
final class DefineClassGlueGenerator {
  // our glue must be located inside the java.lang namespace for accessibility reasons
  private static final String DEFINECLASS_GLUE_CLASS = "java/lang/$Datadog$DefineClass$Glue$";

  private static final String OBJECT_CLASS = "java/lang/Object";
  private static final String CLASS_CLASS = "java/lang/Class";
  private static final String STRING_CLASS = "java/lang/String";
  private static final String MAP_CLASS = "java/util/Map";
  private static final String MAP_ENTRY_CLASS = "java/util/Map$Entry";
  private static final String SET_CLASS = "java/util/Set";
  private static final String ARRAYLIST_CLASS = "java/util/ArrayList";
  private static final String ITERATOR_CLASS = "java/util/Iterator";

  private static final String BYTE_ARRAY = "[B";

  private static final String CLASSLOADER_CLASS = "java/lang/ClassLoader";
  private static final String PROTECTIONDOMAIN_CLASS = "java/security/ProtectionDomain";
  private static final String BIFUNCTION_CLASS = "java/util/function/BiFunction";

  // to keep the bytecode small we only implement the raw API
  private static final String BIFUNCTION_APPLY_DESCRIPTOR =
      "(L" + OBJECT_CLASS + ";L" + OBJECT_CLASS + ";)L" + OBJECT_CLASS + ";";

  private static final String UNSAFE_DEFINECLASS_DESCRIPTOR =
      "(L"
          + STRING_CLASS
          + ";"
          + BYTE_ARRAY
          + "IIL"
          + CLASSLOADER_CLASS
          + ";L"
          + PROTECTIONDOMAIN_CLASS
          + ";)L"
          + CLASS_CLASS
          + ";";

  private static final String CLASSLOADER_DEFINECLASS_DESCRIPTOR =
      "(L"
          + STRING_CLASS
          + ";"
          + BYTE_ARRAY
          + "IIL"
          + PROTECTIONDOMAIN_CLASS
          + ";)L"
          + CLASS_CLASS
          + ";";

  private DefineClassGlueGenerator() {}

  private static byte[] generateBytecode(String unsafeNamespace) {

    // use Unsafe to define boot classes that have no class-loader
    final String unsafeClass = unsafeNamespace + "/Unsafe";
    final String unsafeDescriptor = "L" + unsafeClass + ";";

    ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);

    cw.visit(
        V1_8,
        ACC_PUBLIC | ACC_FINAL,
        DEFINECLASS_GLUE_CLASS,
        null,
        OBJECT_CLASS,
        new String[] {BIFUNCTION_CLASS});

    cw.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, "UNSAFE", unsafeDescriptor, null, null)
        .visitEnd();

    MethodVisitor mv;

    // cache the Unsafe instance locally as a static constant
    mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
    mv.visitCode();
    mv.visitMethodInsn(INVOKESTATIC, unsafeClass, "getUnsafe", "()" + unsafeDescriptor, false);
    mv.visitFieldInsn(PUTSTATIC, DEFINECLASS_GLUE_CLASS, "UNSAFE", unsafeDescriptor);
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 0);
    mv.visitEnd();

    // default constructor
    mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, OBJECT_CLASS, "<init>", "()V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

    // main function which accepts a map of class-names to bytecode and returns the defined classes
    mv = cw.visitMethod(ACC_PUBLIC, "apply", BIFUNCTION_APPLY_DESCRIPTOR, null, null);

    Label skipProtectionDomain = new Label();
    Label checkBootClassLoader = new Label();
    Label hasNextNonBootClass = new Label();
    Label classLoaderLocked = new Label();
    Label storeNonBootClass = new Label();
    Label classLoaderUnlocked = new Label();
    Label unlockClassLoaderAndThrow = new Label();
    Label setupUnsafeDefiner = new Label();
    Label hasNextBootClass = new Label();
    Label returnDefinedClasses = new Label();

    mv.visitCode();

    // make sure we unlock the class-loader if an exception occurs while defining a class
    mv.visitTryCatchBlock(classLoaderLocked, classLoaderUnlocked, unlockClassLoaderAndThrow, null);

    // store the defined classes in a list
    mv.visitTypeInsn(NEW, "java/util/ArrayList");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
    mv.visitVarInsn(ASTORE, 3);

    // iterate over the map entries
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKEINTERFACE, MAP_CLASS, "entrySet", "()L" + SET_CLASS + ";", true);
    mv.visitMethodInsn(INVOKEINTERFACE, SET_CLASS, "iterator", "()L" + ITERATOR_CLASS + ";", true);
    mv.visitVarInsn(ASTORE, 4);

    mv.visitVarInsn(ALOAD, 2);
    mv.visitTypeInsn(INSTANCEOF, PROTECTIONDOMAIN_CLASS);
    mv.visitJumpInsn(IFEQ, skipProtectionDomain);

    // extract class-loader from protection domain
    mv.visitVarInsn(ALOAD, 2);
    mv.visitTypeInsn(CHECKCAST, PROTECTIONDOMAIN_CLASS);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, 5);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        PROTECTIONDOMAIN_CLASS,
        "getClassLoader",
        "()L" + CLASSLOADER_CLASS + ";",
        false);
    mv.visitVarInsn(ASTORE, 6);
    mv.visitJumpInsn(GOTO, checkBootClassLoader);

    // only have class-loader, no protection domain
    mv.visitLabel(skipProtectionDomain);
    mv.visitInsn(ACONST_NULL);
    mv.visitVarInsn(ASTORE, 5);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitTypeInsn(CHECKCAST, CLASSLOADER_CLASS);
    mv.visitVarInsn(ASTORE, 6);

    // boot classes can only be defined via Unsafe
    mv.visitLabel(checkBootClassLoader);
    mv.visitVarInsn(ALOAD, 6);
    mv.visitJumpInsn(IFNULL, setupUnsafeDefiner);

    // check if we've defined all the given bytecode
    mv.visitLabel(hasNextNonBootClass);
    mv.visitVarInsn(ALOAD, 4);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "hasNext", "()Z", true);
    mv.visitJumpInsn(IFEQ, returnDefinedClasses);

    // extract the name of the next class to define
    mv.visitVarInsn(ALOAD, 4);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "next", "()L" + OBJECT_CLASS + ";", true);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, 8);
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getKey", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, STRING_CLASS);
    mv.visitVarInsn(ASTORE, 9);

    // lock the class-loader
    mv.visitVarInsn(ALOAD, 6);
    mv.visitVarInsn(ALOAD, 9);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        CLASSLOADER_CLASS,
        "getClassLoadingLock",
        "(L" + STRING_CLASS + ";)L" + OBJECT_CLASS + ";",
        false);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, 7);
    mv.visitInsn(MONITORENTER);
    mv.visitLabel(classLoaderLocked);

    // check in case the class has already been defined
    mv.visitVarInsn(ALOAD, 6);
    mv.visitVarInsn(ALOAD, 9);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        CLASSLOADER_CLASS,
        "findLoadedClass",
        "(L" + STRING_CLASS + ";)L" + CLASS_CLASS + ";",
        false);
    mv.visitInsn(DUP);
    mv.visitJumpInsn(IFNONNULL, storeNonBootClass);

    // define the class using the given class-name and bytecode
    mv.visitInsn(POP);
    mv.visitVarInsn(ALOAD, 6);
    mv.visitVarInsn(ALOAD, 9);
    mv.visitVarInsn(ALOAD, 8);
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getValue", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, BYTE_ARRAY);
    mv.visitInsn(DUP);
    mv.visitInsn(ARRAYLENGTH);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(SWAP);
    mv.visitVarInsn(ALOAD, 5);
    mv.visitMethodInsn(
        INVOKEVIRTUAL, CLASSLOADER_CLASS, "defineClass", CLASSLOADER_DEFINECLASS_DESCRIPTOR, false);

    // store the class in the list whether we defined it or it already existed
    mv.visitLabel(storeNonBootClass);
    mv.visitVarInsn(ALOAD, 3);
    mv.visitInsn(SWAP);
    mv.visitMethodInsn(INVOKEVIRTUAL, ARRAYLIST_CLASS, "add", "(L" + OBJECT_CLASS + ";)Z", false);
    mv.visitInsn(POP);

    // unlock the class-loader
    mv.visitVarInsn(ALOAD, 7);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(classLoaderUnlocked);

    mv.visitJumpInsn(GOTO, hasNextNonBootClass);

    // unlock the class-loader if something goes wrong
    mv.visitLabel(unlockClassLoaderAndThrow);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 7);
    mv.visitInsn(MONITOREXIT);
    mv.visitInsn(ATHROW);

    // load Unsafe field into local variable for performance
    mv.visitLabel(setupUnsafeDefiner);
    mv.visitFieldInsn(GETSTATIC, DEFINECLASS_GLUE_CLASS, "UNSAFE", unsafeDescriptor);
    mv.visitVarInsn(ASTORE, 7);

    // check if we've defined all the given bytecode
    mv.visitLabel(hasNextBootClass);
    mv.visitVarInsn(ALOAD, 4);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "hasNext", "()Z", true);
    mv.visitJumpInsn(IFEQ, returnDefinedClasses);

    // define the class using the given class-name and bytecode
    mv.visitVarInsn(ALOAD, 7);
    mv.visitVarInsn(ALOAD, 4);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "next", "()L" + OBJECT_CLASS + ";", true);
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getKey", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, STRING_CLASS);
    mv.visitInsn(SWAP);
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getValue", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, BYTE_ARRAY);
    mv.visitInsn(DUP);
    mv.visitInsn(ARRAYLENGTH);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(SWAP);
    mv.visitInsn(ACONST_NULL);
    mv.visitVarInsn(ALOAD, 5);
    mv.visitMethodInsn(
        INVOKEVIRTUAL, unsafeClass, "defineClass", UNSAFE_DEFINECLASS_DESCRIPTOR, false);

    // store the class in the list
    mv.visitVarInsn(ALOAD, 3);
    mv.visitInsn(SWAP);
    mv.visitMethodInsn(INVOKEVIRTUAL, ARRAYLIST_CLASS, "add", "(L" + OBJECT_CLASS + ";)Z", false);
    mv.visitInsn(POP);

    mv.visitJumpInsn(GOTO, hasNextBootClass);

    // return the defined classes
    mv.visitLabel(returnDefinedClasses);
    mv.visitVarInsn(ALOAD, 3);
    mv.visitInsn(ARETURN);

    mv.visitMaxs(-1, -1);
    mv.visitEnd();

    // pad bytecode to even number of bytes, to make string encoding/decoding easier
    if ((unsafeNamespace.length() & 0x01) == 0) {
      cw.newConst(0);
    }

    cw.visitEnd();

    return cw.toByteArray();
  }

  public static void main(String[] args) throws IOException {
    if (args.length < 1 || !args[0].endsWith(".java")) {
      throw new IllegalArgumentException("Expected: java-file");
    }
    File file = new File(args[0]);
    String name = file.getName();
    List<String> lines = new ArrayList<>();
    classHeader(lines, name.substring(0, name.length() - 5));
    lines.add("  /** Glue Id */");
    lines.add("  String ID = \"" + DEFINECLASS_GLUE_CLASS.replace('/', '.') + "\";");
    lines.add("  /** Packed Java 8 bytecode */");
    lines.add("  String V8 =");
    packBytecode(lines, generateBytecode("sun/misc"));
    lines.add("  /** Packed Java 9+ bytecode */");
    lines.add("  String V9 =");
    packBytecode(lines, generateBytecode("jdk/internal/misc"));
    lines.add("}");
    Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
  }
}
