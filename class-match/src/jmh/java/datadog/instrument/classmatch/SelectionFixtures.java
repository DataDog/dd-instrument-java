package datadog.instrument.classmatch;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_ANNOTATION;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

import java.util.LinkedHashMap;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

/** Unloaded positive and negative examples for the real class-selection projections. */
final class SelectionFixtures {
  private SelectionFixtures() {}

  static Map<String, boolean[]> addTo(Map<String, byte[]> corpus) {
    Map<String, boolean[]> expected = new LinkedHashMap<>();
    // Byte Buddy resolves annotation definitions; keep optional annotation APIs in the corpus.
    annotation(corpus, MatcherWorkload.WITH_SPAN);
    annotation(corpus, MatcherWorkload.WEB_SERVICE);
    annotation(corpus, "benchmark.Unrelated");
    for (String name : MatcherWorkload.TRACE_ANNOTATIONS) {
      annotation(corpus, name);
    }
    add(
        corpus,
        expected,
        "Callback",
        null,
        new String[][] {{"callback", "Ljava/lang/Object;"}},
        null,
        true,
        false);
    add(
        corpus,
        expected,
        "CallbackTypo",
        null,
        new String[][] {{"callbacks", "Ljava/lang/Object;"}},
        null,
        false,
        false);
    add(
        corpus,
        expected,
        "Jackson",
        null,
        null,
        new String[][] {{"getCurrentName", "()V"}},
        true,
        false);
    add(
        corpus,
        expected,
        "Publisher",
        null,
        null,
        new String[][] {{"subscribe", "()V"}},
        true,
        false);
    add(
        corpus,
        expected,
        "Blocking",
        null,
        null,
        new String[][] {{"blockOptional", "()V"}},
        true,
        false);
    add(
        corpus,
        expected,
        "Unblocking",
        null,
        null,
        new String[][] {{"unblock", "()V"}},
        false,
        false);
    add(
        corpus,
        expected,
        "WithSpan",
        null,
        null,
        new String[][] {{"endpoint", "()V", MatcherWorkload.WITH_SPAN}},
        true,
        false);
    add(corpus, expected, "WebService", MatcherWorkload.WEB_SERVICE, null, null, true, false);
    for (int i = 0; i < MatcherWorkload.TRACE_ANNOTATIONS.length; i++) {
      add(
          corpus,
          expected,
          "Trace" + i,
          null,
          null,
          new String[][] {{"endpoint", "()V", MatcherWorkload.TRACE_ANNOTATIONS[i]}},
          true,
          false);
    }
    add(
        corpus,
        expected,
        "UnrelatedAnnotation",
        "benchmark.Unrelated",
        null,
        new String[][] {{"endpoint", "()V", "benchmark.Unrelated"}},
        false,
        false);
    add(
        corpus,
        expected,
        "Jetty11",
        null,
        new String[][] {
          {"_contentParameters", "Lorg/eclipse/jetty/util/MultiMap;"},
          {"_dispatcherType", "Ljakarta/servlet/DispatcherType;"}
        },
        null,
        true,
        false);
    add(
        corpus,
        expected,
        "Jetty10",
        null,
        new String[][] {
          {"_contentParameters", "Lorg/eclipse/jetty/util/MultiMap;"},
          {"_dispatcherType", "Ljavax/servlet/DispatcherType;"}
        },
        null,
        false,
        false);
    add(
        corpus,
        expected,
        "JettyMissingField",
        null,
        new String[][] {{"_contentParameters", "Lorg/eclipse/jetty/util/MultiMap;"}},
        null,
        false,
        false);
    add(
        corpus,
        expected,
        "LegacyProvider",
        null,
        null,
        new String[][] {{"loadEvaluatorClass", "()V"}},
        false,
        true);
    add(
        corpus,
        expected,
        "ProviderWrongArity",
        null,
        null,
        new String[][] {{"loadEvaluatorClass", "(I)V"}},
        false,
        false);
    visitor(
        corpus,
        expected,
        "MethodVisitor",
        "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
        "(I)V",
        "(Ljava/lang/Object;)V",
        true,
        true);
    visitor(
        corpus,
        expected,
        "VisitorWrongType",
        "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V",
        "(I)V",
        "(Ljava/lang/Object;)V",
        true,
        false);
    visitor(
        corpus,
        expected,
        "VisitorExtraParameter",
        "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZI)V",
        "(I)V",
        "(Ljava/lang/Object;)V",
        true,
        false);
    visitor(
        corpus,
        expected,
        "VisitorWrongArity",
        "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
        "(II)V",
        "(Ljava/lang/Object;)V",
        true,
        false);
    visitor(
        corpus,
        expected,
        "VisitorArrayParameter",
        "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
        "(I)V",
        "([Ljava/lang/Object;)V",
        true,
        false);
    visitor(
        corpus,
        expected,
        "VisitorMissingMethod",
        "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)V",
        "(I)V",
        "(Ljava/lang/Object;)V",
        false,
        false);
    ClassWriter runnable = new ClassWriter(0);
    runnable.visit(
        V1_8,
        ACC_PUBLIC | ACC_ABSTRACT | ACC_INTERFACE,
        "java/lang/Runnable",
        null,
        "java/lang/Object",
        null);
    runnable.visitEnd();
    corpus.put("java.lang.Runnable", runnable.toByteArray());
    return expected;
  }

  private static void visitor(
      Map<String, byte[]> corpus,
      Map<String, boolean[]> expected,
      String name,
      String methodInsn,
      String insn,
      String ldcInsn,
      boolean includeLdc,
      boolean matches) {
    String[][] methods =
        includeLdc
            ? new String[][] {
              {"visitMethodInsn", methodInsn},
              {"visitInsn", insn},
              {"visitIntInsn", "(II)V"},
              {"visitLdcInsn", ldcInsn}
            }
            : new String[][] {
              {"visitMethodInsn", methodInsn}, {"visitInsn", insn}, {"visitIntInsn", "(II)V"}
            };
    add(corpus, expected, name, null, null, methods, false, matches);
  }

  private static void add(
      Map<String, byte[]> corpus,
      Map<String, boolean[]> expected,
      String name,
      String annotation,
      String[][] fields,
      String[][] methods,
      boolean common,
      boolean structural) {
    String internalName = "benchmark/selection/" + name;
    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    writer.visit(V1_8, ACC_PUBLIC, internalName, null, "java/lang/Object", null);
    if (annotation != null) {
      writer.visitAnnotation("L" + annotation.replace('.', '/') + ";", true).visitEnd();
    }
    MethodVisitor constructor = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    constructor.visitCode();
    constructor.visitVarInsn(ALOAD, 0);
    constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
    constructor.visitInsn(RETURN);
    constructor.visitMaxs(0, 0);
    constructor.visitEnd();
    if (fields != null) {
      for (String[] field : fields) {
        writer.visitField(ACC_PUBLIC, field[0], field[1], null, null).visitEnd();
      }
    }
    if (methods != null) {
      for (String[] method : methods) {
        MethodVisitor visitor = writer.visitMethod(ACC_PUBLIC, method[0], method[1], null, null);
        if (method.length > 2) {
          visitor.visitAnnotation("L" + method[2].replace('.', '/') + ";", true).visitEnd();
        }
        visitor.visitCode();
        visitor.visitInsn(RETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
      }
    }
    writer.visitEnd();
    String dottedName = internalName.replace('/', '.');
    corpus.put(dottedName, writer.toByteArray());
    expected.put(dottedName, new boolean[] {common, structural});
  }

  private static void annotation(Map<String, byte[]> corpus, String name) {
    ClassWriter writer = new ClassWriter(0);
    writer.visit(
        V1_8,
        ACC_PUBLIC | ACC_ABSTRACT | ACC_ANNOTATION | ACC_INTERFACE,
        name.replace('.', '/'),
        null,
        "java/lang/Object",
        new String[] {"java/lang/annotation/Annotation"});
    org.objectweb.asm.AnnotationVisitor retention =
        writer.visitAnnotation("Ljava/lang/annotation/Retention;", true);
    retention.visitEnum("value", "Ljava/lang/annotation/RetentionPolicy;", "RUNTIME");
    retention.visitEnd();
    writer.visitEnd();
    corpus.put(name, writer.toByteArray());
  }

  static boolean expected(boolean[] matches, MatcherWorkload workload) {
    switch (workload) {
      case REAL_WORLD:
      case CLASS_SELECTION:
        return matches[0];
      case STRUCTURAL:
      case STRUCTURAL_SIGNATURES:
        return matches[1];
      default:
        return false;
    }
  }
}
