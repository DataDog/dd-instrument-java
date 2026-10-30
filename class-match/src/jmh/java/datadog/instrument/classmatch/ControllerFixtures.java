package datadog.instrument.classmatch;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PRIVATE;
import static org.objectweb.asm.Opcodes.ACC_PROTECTED;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import java.util.LinkedHashMap;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/**
 * Unloaded application examples, supplementing the framework jars without optional API
 * dependencies.
 */
final class ControllerFixtures {
  private ControllerFixtures() {}

  /**
   * Adds positive and negative annotation examples, returning their expected annotation results.
   */
  static Map<String, Boolean> addTo(Map<String, byte[]> corpus) {
    Map<String, Boolean> expected = new LinkedHashMap<>();
    for (String mapping : MatcherWorkload.MAPPINGS) {
      add(
          corpus,
          expected,
          simpleName(mapping) + "Endpoint",
          null,
          mapping,
          "endpoint",
          ACC_PUBLIC,
          true);
    }
    add(
        corpus,
        expected,
        "ProtectedEndpoint",
        null,
        MatcherWorkload.MAPPINGS[0],
        "endpoint",
        ACC_PROTECTED,
        true);
    for (String controller : MatcherWorkload.CONTROLLERS) {
      // No method annotation: these examples must reach the class-annotation branch.
      add(corpus, expected, simpleName(controller), controller, null, "endpoint", ACC_PUBLIC, true);
    }
    add(
        corpus,
        expected,
        "MappedController",
        MatcherWorkload.MAPPINGS[0],
        null,
        "endpoint",
        ACC_PUBLIC,
        true);
    add(corpus, expected, "Unannotated", null, null, "endpoint", ACC_PUBLIC, false);
    add(
        corpus,
        expected,
        "PrivateEndpoint",
        null,
        MatcherWorkload.MAPPINGS[0],
        "endpoint",
        ACC_PRIVATE,
        false);
    add(
        corpus,
        expected,
        "StaticEndpoint",
        null,
        MatcherWorkload.MAPPINGS[0],
        "endpoint",
        ACC_PUBLIC | ACC_STATIC,
        false);
    add(
        corpus,
        expected,
        "ConstructorOnlyController",
        MatcherWorkload.CONTROLLERS[2],
        null,
        null,
        0,
        false);
    add(
        corpus,
        expected,
        "AbstractController",
        MatcherWorkload.CONTROLLERS[2],
        null,
        "endpoint",
        ACC_PUBLIC | ACC_ABSTRACT,
        false);
    add(
        corpus,
        expected,
        "StaticController",
        MatcherWorkload.CONTROLLERS[2],
        null,
        "endpoint",
        ACC_PUBLIC | ACC_STATIC,
        false);
    return expected;
  }

  private static String simpleName(String name) {
    return name.substring(name.lastIndexOf('.') + 1);
  }

  private static void add(
      Map<String, byte[]> corpus,
      Map<String, Boolean> expected,
      String name,
      String classAnnotation,
      String methodAnnotation,
      String methodName,
      int methodAccess,
      boolean matches) {
    String internalName = "benchmark/controllers/" + name;
    ClassWriter writer = new ClassWriter(0);
    writer.visit(
        V1_8,
        ACC_PUBLIC | (methodAccess & ACC_ABSTRACT),
        internalName,
        null,
        "java/lang/Object",
        null);
    if (classAnnotation != null) {
      writer.visitAnnotation(descriptor(classAnnotation), true).visitEnd();
    }
    MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    constructor.visitCode();
    constructor.visitVarInsn(ALOAD, 0);
    constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    constructor.visitInsn(RETURN);
    constructor.visitMaxs(1, 1);
    constructor.visitEnd();
    if (methodName != null) {
      MethodVisitor method =
          writer.visitMethod(methodAccess, methodName, "()Ljava/lang/String;", null, null);
      if (methodAnnotation != null) {
        method.visitAnnotation(descriptor(methodAnnotation), true).visitEnd();
      }
      if ((methodAccess & ACC_ABSTRACT) == 0) {
        method.visitCode();
        method.visitLdcInsn("response");
        method.visitInsn(ARETURN);
        method.visitMaxs(1, (methodAccess & ACC_STATIC) == 0 ? 1 : 0);
      }
      method.visitEnd();
    }
    writer.visitEnd();
    String dottedName = internalName.replace('/', '.');
    corpus.put(dottedName, writer.toByteArray());
    expected.put(dottedName, matches);
  }

  private static String descriptor(String name) {
    return "L" + name.replace('.', '/') + ";";
  }
}
