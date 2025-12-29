/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import static datadog.instrument.glue.GlueGenerator.classHeader;
import static datadog.instrument.glue.GlueGenerator.packBytecode;
import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.Opcodes.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
  private static final String DEFINECLASSGLUE_CLASS = "java/lang/$Datadog$DefineClassGlue";

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

  /**
   * Generates glue to access {@code ClassLoader.defineClass} and writes it to the given location.
   *
   * @param resourcePath where to write resource files
   * @param javaPath where to write Java files
   * @throws IOException if the files cannot be written
   * @see GlueGenerator#main
   */
  public static void generateGlue(Path resourcePath, Path javaPath) throws IOException {
    Path defineClassGlue = javaPath.resolve("DefineClassGlue.java");
    List<String> lines = new ArrayList<>();
    classHeader(lines, "DefineClassGlue");
    lines.add("  /** Glue Id */");
    lines.add("  String ID = \"" + DEFINECLASSGLUE_CLASS.replace('/', '.') + "\";");
    lines.add("  /** Packed Java 8 bytecode */");
    lines.add("  String V8 =");
    packBytecode(lines, generateBytecode("sun/misc"));
    lines.add("  /** Packed Java 9+ bytecode */");
    lines.add("  String V9 =");
    packBytecode(lines, generateBytecode("jdk/internal/misc"));
    lines.add("}");
    Files.write(defineClassGlue, lines, StandardCharsets.UTF_8);
  }

  /**
   * Generates glue bytecode to access {@code ClassLoader.defineClass}.
   *
   * @param unsafeNamespace which Unsafe to use for boot injection
   * @return glue to access {@code ClassLoader.defineClass}
   */
  private static byte[] generateBytecode(String unsafeNamespace) {

    // use Unsafe to define boot classes that have no class-loader
    final String unsafeClass = unsafeNamespace + "/Unsafe";
    final String unsafeDescriptor = "L" + unsafeClass + ";";

    ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);

    cw.visit(
        V1_8,
        ACC_PUBLIC | ACC_FINAL,
        DEFINECLASSGLUE_CLASS,
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
    mv.visitFieldInsn(PUTSTATIC, DEFINECLASSGLUE_CLASS, "UNSAFE", unsafeDescriptor);
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

    // main function that accepts a map of class-names to bytecode as well as
    // a class-loader or protection-domain, and returns the defined classes
    mv = cw.visitMethod(ACC_PUBLIC, "apply", BIFUNCTION_APPLY_DESCRIPTOR, null, null);

    // bytecode positions
    final Label skipProtectionDomain = new Label();
    final Label checkBootClassLoader = new Label();
    final Label hasNextNonBootClass = new Label();
    final Label classLoaderLocked = new Label();
    final Label storeNonBootClass = new Label();
    final Label classLoaderUnlocked = new Label();
    final Label unlockClassLoaderAndThrow = new Label();
    final Label setupUnsafeDefiner = new Label();
    final Label hasNextBootClass = new Label();
    final Label storeBootClass = new Label();
    final Label returnDefinedClasses = new Label();

    // common bytecode variables
    final int bytecodeMap = 1;
    final int protectionDomainOrClassLoader = 2;
    final int definedClasses = 3;
    final int mapEntrySetIterator = 4;
    final int protectionDomain = 5;
    final int classLoader = 6;
    final int className = 7;

    // bytecode variables for non-boot classes
    final int classLoadingLock = 8;
    final int bytecodeMapEntry = 9;

    // bytecode variables for boot classes
    final int unsafeInstance = 8;

    mv.visitCode();

    // make sure we unlock the class-loader if an exception occurs while defining a class
    mv.visitTryCatchBlock(classLoaderLocked, classLoaderUnlocked, unlockClassLoaderAndThrow, null);

    // -------- SHARED SETUP CODE  --------

    // store the defined classes in a list
    mv.visitTypeInsn(NEW, "java/util/ArrayList");
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
    mv.visitVarInsn(ASTORE, definedClasses);

    // get entry set iterator from the bytecode map
    mv.visitVarInsn(ALOAD, bytecodeMap);
    mv.visitMethodInsn(INVOKEINTERFACE, MAP_CLASS, "entrySet", "()L" + SET_CLASS + ";", true);
    mv.visitMethodInsn(INVOKEINTERFACE, SET_CLASS, "iterator", "()L" + ITERATOR_CLASS + ";", true);
    mv.visitVarInsn(ASTORE, mapEntrySetIterator);

    // check if second argument is a protection domain or class-loader
    mv.visitVarInsn(ALOAD, protectionDomainOrClassLoader);
    mv.visitTypeInsn(INSTANCEOF, PROTECTIONDOMAIN_CLASS);
    mv.visitJumpInsn(IFEQ, skipProtectionDomain);

    // extract class-loader from protection domain
    mv.visitVarInsn(ALOAD, protectionDomainOrClassLoader);
    mv.visitTypeInsn(CHECKCAST, PROTECTIONDOMAIN_CLASS);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, protectionDomain);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        PROTECTIONDOMAIN_CLASS,
        "getClassLoader",
        "()L" + CLASSLOADER_CLASS + ";",
        false);
    mv.visitVarInsn(ASTORE, classLoader);
    mv.visitJumpInsn(GOTO, checkBootClassLoader);

    // only have class-loader, no protection domain
    mv.visitLabel(skipProtectionDomain);
    mv.visitInsn(ACONST_NULL);
    mv.visitVarInsn(ASTORE, protectionDomain);
    mv.visitVarInsn(ALOAD, protectionDomainOrClassLoader);
    mv.visitTypeInsn(CHECKCAST, CLASSLOADER_CLASS);
    mv.visitVarInsn(ASTORE, classLoader);

    // boot classes can only be defined via Unsafe
    mv.visitLabel(checkBootClassLoader);
    mv.visitVarInsn(ALOAD, classLoader);
    mv.visitJumpInsn(IFNULL, setupUnsafeDefiner);

    // -------- LOOP TO DEFINE NON-BOOT CLASSES --------

    // check if we've defined all the given bytecode
    mv.visitLabel(hasNextNonBootClass);
    mv.visitVarInsn(ALOAD, mapEntrySetIterator);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "hasNext", "()Z", true);
    mv.visitJumpInsn(IFEQ, returnDefinedClasses);

    // extract the name of the next class to define
    mv.visitVarInsn(ALOAD, mapEntrySetIterator);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "next", "()L" + OBJECT_CLASS + ";", true);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, bytecodeMapEntry);
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getKey", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, STRING_CLASS);
    mv.visitVarInsn(ASTORE, className);

    // lock the class-loader
    mv.visitVarInsn(ALOAD, classLoader);
    mv.visitVarInsn(ALOAD, className);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        CLASSLOADER_CLASS,
        "getClassLoadingLock",
        "(L" + STRING_CLASS + ";)L" + OBJECT_CLASS + ";",
        false);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, classLoadingLock);
    mv.visitInsn(MONITORENTER);
    mv.visitLabel(classLoaderLocked);

    // check in case the class has already been defined
    mv.visitVarInsn(ALOAD, classLoader);
    mv.visitVarInsn(ALOAD, className);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        CLASSLOADER_CLASS,
        "findLoadedClass",
        "(L" + STRING_CLASS + ";)L" + CLASS_CLASS + ";",
        false);
    mv.visitInsn(DUP);
    mv.visitJumpInsn(IFNONNULL, storeNonBootClass);
    mv.visitInsn(POP);

    // not yet defined, prepare arguments to define it
    mv.visitVarInsn(ALOAD, classLoader);
    mv.visitVarInsn(ALOAD, className);

    // extract the bytecode of the next class to define
    mv.visitVarInsn(ALOAD, bytecodeMapEntry);
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getValue", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, BYTE_ARRAY);
    mv.visitInsn(DUP);
    mv.visitInsn(ARRAYLENGTH);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(SWAP);

    mv.visitVarInsn(ALOAD, protectionDomain);

    // define the class using the given class-name and bytecode
    mv.visitMethodInsn(
        INVOKEVIRTUAL, CLASSLOADER_CLASS, "defineClass", CLASSLOADER_DEFINECLASS_DESCRIPTOR, false);

    // store the class in the list whether we defined it or it already existed
    mv.visitLabel(storeNonBootClass);
    mv.visitVarInsn(ALOAD, definedClasses);
    mv.visitInsn(SWAP);
    mv.visitMethodInsn(INVOKEVIRTUAL, ARRAYLIST_CLASS, "add", "(L" + OBJECT_CLASS + ";)Z", false);
    mv.visitInsn(POP);

    // unlock the class-loader
    mv.visitVarInsn(ALOAD, classLoadingLock);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(classLoaderUnlocked);

    // check again if we've defined all the given bytecode
    mv.visitJumpInsn(GOTO, hasNextNonBootClass);

    // unlock the class-loader if something goes wrong
    mv.visitLabel(unlockClassLoaderAndThrow);
    mv.visitVarInsn(ALOAD, classLoadingLock);
    mv.visitInsn(MONITOREXIT);
    mv.visitInsn(ATHROW);

    // -------- LOOP TO DEFINE BOOT CLASSES --------

    // load Unsafe field into local variable for performance
    mv.visitLabel(setupUnsafeDefiner);
    mv.visitFieldInsn(GETSTATIC, DEFINECLASSGLUE_CLASS, "UNSAFE", unsafeDescriptor);
    mv.visitVarInsn(ASTORE, unsafeInstance);

    // check if we've defined all the given bytecode
    mv.visitLabel(hasNextBootClass);
    mv.visitVarInsn(ALOAD, mapEntrySetIterator);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "hasNext", "()Z", true);
    mv.visitJumpInsn(IFEQ, returnDefinedClasses);

    mv.visitVarInsn(ALOAD, unsafeInstance);

    // extract the name of the next class to define
    mv.visitVarInsn(ALOAD, mapEntrySetIterator);
    mv.visitMethodInsn(INVOKEINTERFACE, ITERATOR_CLASS, "next", "()L" + OBJECT_CLASS + ";", true);
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getKey", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, STRING_CLASS);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ASTORE, className);
    mv.visitInsn(SWAP);

    // extract the bytecode of the next class to define
    mv.visitMethodInsn(
        INVOKEINTERFACE, MAP_ENTRY_CLASS, "getValue", "()L" + OBJECT_CLASS + ";", true);
    mv.visitTypeInsn(CHECKCAST, BYTE_ARRAY);
    mv.visitInsn(DUP);
    mv.visitInsn(ARRAYLENGTH);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(SWAP);

    mv.visitInsn(ACONST_NULL);
    mv.visitVarInsn(ALOAD, protectionDomain);

    // define the boot class using the given class-name and bytecode
    mv.visitMethodInsn(
        INVOKEVIRTUAL, unsafeClass, "defineClass", UNSAFE_DEFINECLASS_DESCRIPTOR, false);

    // store the class in the list
    mv.visitLabel(storeBootClass);
    mv.visitVarInsn(ALOAD, definedClasses);
    mv.visitInsn(SWAP);
    mv.visitMethodInsn(INVOKEVIRTUAL, ARRAYLIST_CLASS, "add", "(L" + OBJECT_CLASS + ";)Z", false);
    mv.visitInsn(POP);

    // check again if we've defined all the given bytecode
    mv.visitJumpInsn(GOTO, hasNextBootClass);

    // -------- SHARED RETURN CODE  --------

    // return the defined classes
    mv.visitLabel(returnDefinedClasses);
    mv.visitVarInsn(ALOAD, definedClasses);
    mv.visitInsn(ARETURN);

    mv.visitMaxs(-1, -1);
    mv.visitEnd();

    // pad bytecode to even number of bytes, to make string encoding/decoding easier
    if ((unsafeNamespace.length() & 0x01) == 1) {
      cw.newConst(0);
    }

    cw.visitEnd();

    return cw.toByteArray();
  }
}
