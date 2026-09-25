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

import datadog.instrument.utils.JVM;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;

/**
 * Generates {@code KeyWithValue}/{@code ObjectStoreDispatch} bytecode, plus java.lang-relocated
 * copies of them and {@code GlobalObjectStore} packed as glue for the bootstrap classpath.
 */
final class ObjectStoreGlueGenerator {

  // generated dispatch glue
  private static final String KEYWITHVALUE_CLASS = "datadog/instrument/fieldinject/KeyWithValue";
  private static final String OBJECTSTOREDISPATCH_CLASS =
      "datadog/instrument/fieldinject/ObjectStoreDispatch";

  // repackaged support glue
  private static final String GLOBALOBJECTSTORE_CLASS =
      "datadog/instrument/fieldinject/GlobalObjectStore";
  private static final String GLOBALOBJECTSTORE_STOREKEY_CLASS =
      GLOBALOBJECTSTORE_CLASS + "$StoreKey";
  private static final String GLOBALOBJECTSTORE_LOOKUPKEY_CLASS =
      GLOBALOBJECTSTORE_CLASS + "$LookupKey";

  private static final String OBJECT_CLASS = "java/lang/Object";
  private static final String FUNCTION_CLASS = "java/util/function/Function";

  // bootstrap glue must be located inside the java.lang namespace for accessibility reasons
  private static final String JAVA_LANG_PREFIX = "java/lang/$Datadog$";

  private static final String GET_ACCESSOR = "$dd_instrument_$get$";
  private static final String SET_ACCESSOR = "$dd_instrument_$set$";
  private static final String GET_ACCESSOR_DESCRIPTOR = "(I)L" + OBJECT_CLASS + ";";
  private static final String SET_ACCESSOR_DESCRIPTOR = "(IL" + OBJECT_CLASS + ";)V";

  private static final String GET_DESCRIPTOR = "(L" + OBJECT_CLASS + ";I)L" + OBJECT_CLASS + ";";
  private static final String PUT_DESCRIPTOR = "(L" + OBJECT_CLASS + ";IL" + OBJECT_CLASS + ";)V";
  private static final String GETORPUT_DESCRIPTOR =
      "(L" + OBJECT_CLASS + ";IL" + OBJECT_CLASS + ";)L" + OBJECT_CLASS + ";";
  private static final String GETORCOMPUTE_DESCRIPTOR =
      "(L" + OBJECT_CLASS + ";IL" + FUNCTION_CLASS + ";)L" + OBJECT_CLASS + ";";
  private static final String REMOVE_DESCRIPTOR = "(L" + OBJECT_CLASS + ";I)L" + OBJECT_CLASS + ";";

  private static final String FUNCTION_APPLY_DESCRIPTOR =
      "(L" + OBJECT_CLASS + ";)L" + OBJECT_CLASS + ";";

  private ObjectStoreGlueGenerator() {}

  /**
   * Generates glue to dispatch field-injected values, and writes it to the given location.
   *
   * @param resourcePath where to write resource files
   * @param javaPath where to write Java files
   * @throws IOException if the files cannot be written
   * @see GlueGenerator#main
   */
  public static void generateGlue(Path resourcePath, Path javaPath) throws IOException {
    byte[] keyWithValueBytecode = generateKeyWithValueBytecode();
    byte[] objectStoreDispatchBytecode = generateObjectStoreDispatchBytecode();

    // first generate dispatch glue for the field-inject package
    Path fieldInjectPackagePath = resourcePath.resolveSibling("fieldinject");
    Files.createDirectories(fieldInjectPackagePath);
    writeClassResource(fieldInjectPackagePath, KEYWITHVALUE_CLASS, keyWithValueBytecode);
    writeClassResource(
        fieldInjectPackagePath, OBJECTSTOREDISPATCH_CLASS, objectStoreDispatchBytecode);

    // next collect all the glue we need to repackage for the bootstrap.
    List<RelocatedClass> relocatedClasses = new ArrayList<>();
    relocatedClasses.add(
        new RelocatedClass("KEYWITHVALUE", KEYWITHVALUE_CLASS, keyWithValueBytecode));
    relocatedClasses.add(
        new RelocatedClass(
            "OBJECTSTOREDISPATCH", OBJECTSTOREDISPATCH_CLASS, objectStoreDispatchBytecode));
    relocatedClasses.add(
        new RelocatedClass(
            "GLOBALOBJECTSTORE",
            GLOBALOBJECTSTORE_CLASS,
            readClassResource(GLOBALOBJECTSTORE_CLASS)));
    relocatedClasses.add(
        new RelocatedClass(
            "GLOBALOBJECTSTORE_STOREKEY",
            GLOBALOBJECTSTORE_STOREKEY_CLASS,
            readClassResource(GLOBALOBJECTSTORE_STOREKEY_CLASS)));
    relocatedClasses.add(
        new RelocatedClass(
            "GLOBALOBJECTSTORE_LOOKUPKEY",
            GLOBALOBJECTSTORE_LOOKUPKEY_CLASS,
            readClassResource(GLOBALOBJECTSTORE_LOOKUPKEY_CLASS)));

    Map<String, String> renames = new HashMap<>();
    for (RelocatedClass relocated : relocatedClasses) {
      renames.put(relocated.originalClass, relocated.relocatedClass);
    }
    Remapper remapper = new SimpleRemapper(ASM9, renames);

    // make the bootstrap glue available for class injection
    List<String> lines = new ArrayList<>();
    classHeader(lines, "ObjectStoreGlue");
    lines.add("  /** Shared prefix of bootstrap glue */");
    lines.add("  String PREFIX = \"" + JAVA_LANG_PREFIX.replace('/', '.') + "\";");
    for (RelocatedClass relocated : relocatedClasses) {
      packRelocatedClass(lines, remapper, relocated);
    }
    lines.add("}");
    Files.write(javaPath.resolve("ObjectStoreGlue.java"), lines, StandardCharsets.UTF_8);
  }

  /** A class to be relocated into the {@code java.lang} namespace and packed into the glue. */
  private static final class RelocatedClass {
    final String constantName;
    final String originalClass;
    final String relocatedClass;
    final byte[] originalBytecode;

    RelocatedClass(String constantName, String originalClass, byte[] originalBytecode) {
      String simpleName = originalClass.substring(originalClass.lastIndexOf('/') + 1);
      this.constantName = constantName;
      this.originalClass = originalClass;
      this.relocatedClass = JAVA_LANG_PREFIX + simpleName;
      this.originalBytecode = originalBytecode;
    }
  }

  /** Generates bytecode for the {@code KeyWithValue} marker interface. */
  static byte[] generateKeyWithValueBytecode() {
    ClassWriter cw = new ClassWriter(0);
    cw.visit(
        V1_8,
        ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT | ACC_SYNTHETIC,
        KEYWITHVALUE_CLASS,
        null,
        OBJECT_CLASS,
        null);
    cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, GET_ACCESSOR, GET_ACCESSOR_DESCRIPTOR, null, null)
        .visitEnd();
    cw.visitMethod(ACC_PUBLIC | ACC_ABSTRACT, SET_ACCESSOR, SET_ACCESSOR_DESCRIPTOR, null, null)
        .visitEnd();
    cw.visitEnd();
    return cw.toByteArray();
  }

  /** Generates bytecode for {@code ObjectStoreDispatch}. */
  static byte[] generateObjectStoreDispatchBytecode() {
    ClassWriter cw = new ClassWriter(COMPUTE_FRAMES);
    cw.visit(
        V1_8,
        ACC_PUBLIC | ACC_FINAL | ACC_SYNTHETIC,
        OBJECTSTOREDISPATCH_CLASS,
        null,
        OBJECT_CLASS,
        null);

    // no constructor: this class is only ever used via its static dispatch methods

    // simple two-way dispatch: fast path calls the accessor directly, otherwise fall back
    generateCheckedDispatch(cw, "get", GET_DESCRIPTOR);
    generateCheckedDispatch(cw, "put", PUT_DESCRIPTOR);

    // double-checked get-then-set dispatch, shared by getOrPut/getOrCompute/remove
    generateDoubleCheckedDispatch(
        cw, "getOrPut", GETORPUT_DESCRIPTOR, mv -> mv.visitVarInsn(ALOAD, 2));
    generateDoubleCheckedDispatch(
        cw,
        "getOrCompute",
        GETORCOMPUTE_DESCRIPTOR,
        mv -> {
          mv.visitVarInsn(ALOAD, 2);
          mv.visitVarInsn(ALOAD, 0);
          mv.visitMethodInsn(
              INVOKEINTERFACE, FUNCTION_CLASS, "apply", FUNCTION_APPLY_DESCRIPTOR, true);
        });
    generateDoubleCheckedDispatch(cw, "remove", REMOVE_DESCRIPTOR, mv -> mv.visitInsn(ACONST_NULL));

    // weakGet/weakPut skip the fast-path accessor and always go straight to the global store
    generateForwardDispatch(cw, "weakGet", "get", GET_DESCRIPTOR);
    generateForwardDispatch(cw, "weakPut", "put", PUT_DESCRIPTOR);

    cw.visitEnd();
    return cw.toByteArray();
  }

  /** Whether the descriptor's 3rd argument is the value to store, by convention. */
  private static boolean hasValueArg(String descriptor) {
    return Type.getArgumentTypes(descriptor).length > 2;
  }

  /** Generates a method that unconditionally delegates to {@code GlobalObjectStore}. */
  private static void generateForwardDispatch(
      ClassWriter cw, String method, String forwardMethod, String descriptor) {
    final boolean hasValueArg = hasValueArg(descriptor);
    final int returnOpcode = Type.getReturnType(descriptor).getOpcode(IRETURN);

    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_STATIC, method, descriptor, null, null);

    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    emitFallback(mv, forwardMethod, descriptor, hasValueArg, returnOpcode);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  /** Generates a getter or setter that dispatches to the fast-path accessor or the fallback. */
  private static void generateCheckedDispatch(ClassWriter cw, String method, String descriptor) {
    final boolean hasValueArg = hasValueArg(descriptor);
    final int returnOpcode = Type.getReturnType(descriptor).getOpcode(IRETURN);
    final String accessorMethod = hasValueArg ? SET_ACCESSOR : GET_ACCESSOR;
    final String accessorDescriptor =
        hasValueArg ? SET_ACCESSOR_DESCRIPTOR : GET_ACCESSOR_DESCRIPTOR;

    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_STATIC, method, descriptor, null, null);

    final Label fallback = new Label();

    mv.visitCode();
    emitInstanceOfCheck(mv, fallback);

    // fast path: ((KeyWithValue) key).accessorMethod(storeId[, value])
    mv.visitTypeInsn(CHECKCAST, KEYWITHVALUE_CLASS);
    mv.visitVarInsn(ILOAD, 1);
    if (hasValueArg) {
      mv.visitVarInsn(ALOAD, 2);
    }
    mv.visitMethodInsn(
        INVOKEINTERFACE, KEYWITHVALUE_CLASS, accessorMethod, accessorDescriptor, true);
    mv.visitInsn(returnOpcode);

    mv.visitLabel(fallback);
    emitFallback(mv, method, descriptor, hasValueArg, returnOpcode);

    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  /**
   * Generates the double-checked get/set dispatch shared by {@code getOrPut}, {@code getOrCompute}
   * and {@code remove}; {@code valueToStore} emits the value to set on the slow path.
   */
  private static void generateDoubleCheckedDispatch(
      ClassWriter cw, String method, String descriptor, Consumer<MethodVisitor> valueToStore) {

    final boolean hasValueArg = hasValueArg(descriptor);
    final int accessorLocal = hasValueArg ? 3 : 2;
    final int existingLocal = accessorLocal + 1;
    final int storedLocal = hasValueArg ? existingLocal : existingLocal + 1;
    final int skipToReturnOpcode = hasValueArg ? IFNONNULL : IFNULL;

    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_STATIC, method, descriptor, null, null);

    final Label fallback = new Label();
    final Label returnExisting = new Label();
    final Label skipSet = new Label();
    final Label monitorEntered = new Label();
    final Label monitorExited = new Label();
    final Label unlockAndRethrow = new Label();

    mv.visitCode();
    mv.visitTryCatchBlock(monitorEntered, monitorExited, unlockAndRethrow, null);

    emitInstanceOfCheck(mv, fallback);

    // KeyWithValue accessor = (KeyWithValue) key;
    mv.visitTypeInsn(CHECKCAST, KEYWITHVALUE_CLASS);
    mv.visitVarInsn(ASTORE, accessorLocal);

    // Object existing = accessor.$dd_instrument_$get$(storeId);
    emitAccessorGet(mv, accessorLocal, existingLocal);

    // if (existing [is/isn't] null) return existing;
    mv.visitVarInsn(ALOAD, existingLocal);
    mv.visitJumpInsn(skipToReturnOpcode, returnExisting);

    // synchronized (accessor) {
    mv.visitVarInsn(ALOAD, accessorLocal);
    mv.visitInsn(MONITORENTER);
    mv.visitLabel(monitorEntered);

    // existing = accessor.$dd_instrument_$get$(storeId); (re-check now we hold the lock)
    emitAccessorGet(mv, accessorLocal, existingLocal);

    mv.visitVarInsn(ALOAD, existingLocal);
    mv.visitJumpInsn(skipToReturnOpcode, skipSet);

    // stored = <valueToStore>; accessor.$dd_instrument_$set$(storeId, stored);
    valueToStore.accept(mv);
    mv.visitVarInsn(ASTORE, storedLocal);
    mv.visitVarInsn(ALOAD, accessorLocal);
    mv.visitVarInsn(ILOAD, 1);
    mv.visitVarInsn(ALOAD, storedLocal);
    mv.visitMethodInsn(
        INVOKEINTERFACE, KEYWITHVALUE_CLASS, SET_ACCESSOR, SET_ACCESSOR_DESCRIPTOR, true);

    mv.visitLabel(skipSet);
    mv.visitVarInsn(ALOAD, accessorLocal);
    mv.visitInsn(MONITOREXIT);
    mv.visitLabel(monitorExited);
    mv.visitJumpInsn(GOTO, returnExisting);

    // } unlock and rethrow if anything above failed
    mv.visitLabel(unlockAndRethrow);
    mv.visitVarInsn(ALOAD, accessorLocal);
    mv.visitInsn(MONITOREXIT);
    mv.visitInsn(ATHROW);

    mv.visitLabel(returnExisting);
    mv.visitVarInsn(ALOAD, existingLocal);
    mv.visitInsn(ARETURN);

    mv.visitLabel(fallback);
    emitFallback(mv, method, descriptor, hasValueArg, ARETURN);

    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  /**
   * Emits {@code return GlobalObjectStore.method(key, storeId[, value]);} reusing the key reference
   * already left on the stack by {@link #emitInstanceOfCheck}.
   */
  private static void emitFallback(
      MethodVisitor mv, String method, String descriptor, boolean hasValueArg, int returnOpcode) {
    mv.visitVarInsn(ILOAD, 1);
    if (hasValueArg) {
      mv.visitVarInsn(ALOAD, 2);
    }
    mv.visitMethodInsn(INVOKESTATIC, GLOBALOBJECTSTORE_CLASS, method, descriptor, false);
    mv.visitInsn(returnOpcode);
  }

  /**
   * Emits {@code if (!(key instanceof KeyWithValue)) goto notInjected;} leaving the key reference
   * on the stack, so callers don't need to reload it with {@code ALOAD 0}.
   */
  private static void emitInstanceOfCheck(MethodVisitor mv, Label notInjected) {
    mv.visitVarInsn(ALOAD, 0);
    mv.visitInsn(DUP);
    mv.visitTypeInsn(INSTANCEOF, KEYWITHVALUE_CLASS);
    mv.visitJumpInsn(IFEQ, notInjected);
  }

  /** Emits {@code existing = accessor.$dd_instrument_$get$(storeId);} */
  private static void emitAccessorGet(MethodVisitor mv, int accessorLocal, int existingLocal) {
    mv.visitVarInsn(ALOAD, accessorLocal);
    mv.visitVarInsn(ILOAD, 1);
    mv.visitMethodInsn(
        INVOKEINTERFACE, KEYWITHVALUE_CLASS, GET_ACCESSOR, GET_ACCESSOR_DESCRIPTOR, true);
    mv.visitVarInsn(ASTORE, existingLocal);
  }

  /** Packs a relocated class as a string constant into the generated Java source lines. */
  private static void packRelocatedClass(
      List<String> lines, Remapper remapper, RelocatedClass relocated) {
    String constantName = relocated.constantName;
    lines.add("  /** Packed bytecode for relocated " + constantName + " */");
    lines.add("  String " + constantName + " =");
    packBytecode(lines, relocate(remapper, relocated.originalBytecode));
  }

  /** Relocates the given bytecode with the given remapper, dropping debug attributes. */
  private static byte[] relocate(Remapper remapper, byte[] originalBytecode) {
    ClassReader cr = new ClassReader(originalBytecode);
    ClassWriter cw = new ClassWriter(0);
    cr.accept(new ClassRemapper(cw, remapper), ClassReader.SKIP_DEBUG);
    return cw.toByteArray();
  }

  /** Reads the bytecode of an already-compiled class off the classpath. */
  private static byte[] readClassResource(String internalName) throws IOException {
    try (InputStream in =
        ObjectStoreGlueGenerator.class
            .getClassLoader()
            .getResourceAsStream(internalName + ".class")) {
      if (in == null) {
        throw new IOException("Could not find class on classpath: " + internalName);
      }
      return JVM.readAllBytes(in);
    }
  }

  /** Writes the given bytecode as a resource class file under the given package path. */
  private static void writeClassResource(Path packagePath, String internalName, byte[] bytecode)
      throws IOException {
    String simpleName = internalName.substring(internalName.lastIndexOf('/') + 1);
    Files.write(packagePath.resolve(simpleName + ".class"), bytecode);
  }
}
