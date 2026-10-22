package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.BytecodeTestSupport.classWriter;
import static datadog.instrument.classmatch.FieldMatcher.field;
import static datadog.instrument.classmatch.MatcherTestSupport.assertMatches;
import static datadog.instrument.classmatch.MethodMatcher.method;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.tabletest.junit.TableTest;

class GeneratedSignatureTest {
  private static final Class<?>[] TYPES = {
    boolean.class,
    byte.class,
    char.class,
    short.class,
    int.class,
    long.class,
    float.class,
    double.class,
    Object.class,
    String.class,
    Map.Entry.class,
    int[].class,
    int[][].class,
    String[].class,
    String[][].class
  };

  @SuppressFBWarnings(
      value = "DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED",
      justification = "Generated fixtures use an isolated test class loader")
  @TableTest({
    "offset",
    "0     ",
    "1     ",
    "2     "
  })
  void generatedSignaturesAgreeWithReflection(int offset) throws Exception {
    ClassWriter writer = classWriter("tests/GeneratedSignatures");
    Class<?>[] returns = Arrays.copyOf(TYPES, TYPES.length + 1);
    returns[TYPES.length] = void.class;
    int count = 0;
    for (Class<?> type : TYPES) {
      writer
          .visitField(ACC_PUBLIC, "field" + count++, Type.getDescriptor(type), null, null)
          .visitEnd();
    }
    count = 0;
    // Systematically cover every parameter/return pair, plus parameterless methods.
    for (Class<?> returnType : returns) {
      addMethod(writer, count++, returnType);
      for (Class<?> paramType : TYPES) {
        addMethod(writer, count++, returnType, paramType);
      }
    }
    for (int i = 0; i < 64; i++) {
      Class<?>[] params = new Class<?>[2 + i % 7];
      for (int p = 0; p < params.length; p++) {
        params[p] = TYPES[(offset + i + p) % TYPES.length];
      }
      addMethod(writer, count++, returns[(offset + i) % returns.length], params);
    }
    writer.visitEnd();
    byte[] bytecode = writer.toByteArray();
    Class<?> loaded = new FixtureLoader().define(bytecode);
    ClassOutline outline = ClassFile.outline(bytecode);
    assertEquals(loaded.getName().replace('.', '/'), outline.className);
    assertEquals(loaded.getSuperclass().getName().replace('.', '/'), outline.superName);
    assertEquals(loaded.getModifiers(), outline.access);
    assertEquals(loaded.getDeclaredFields().length, outline.fields.length);
    assertEquals(loaded.getDeclaredMethods().length, outline.methods.length);
    for (FieldOutline f : outline.fields) {
      Class<?> reflectedType = loaded.getDeclaredField(f.fieldName).getType();
      assertEquals(Type.getDescriptor(reflectedType), f.descriptor);
      for (Class<?> type : TYPES) {
        assertMatches(field(f.fieldName).type(type), f, reflectedType == type);
        assertMatches(field(f.fieldName).type(type.getTypeName()), f, reflectedType == type);
      }
    }
    Map<String, Method> methods = new HashMap<>();
    for (Method m : loaded.getDeclaredMethods()) {
      methods.put(m.getName(), m);
    }
    for (MethodOutline m : outline.methods) {
      Method reflected = methods.remove(m.methodName);
      assertEquals(Type.getMethodDescriptor(reflected), m.descriptor);
      assertEquals(reflected.getModifiers(), m.access);
      Class<?>[] params = reflected.getParameterTypes();
      assertEquals(params.length, m.parameterCount());
      assertMatches(method(m.methodName).parameters(params), m, true);
      assertMatches(
          method().parameters(Arrays.stream(params).map(Class::getTypeName).toArray(String[]::new)),
          m,
          true);
      assertMatches(method().parameters(params.length), m, true);
      assertMatches(method().parameters(params.length + 1), m, false);
      for (int p = 0; p < params.length; p++) {
        for (Class<?> type : TYPES) {
          assertMatches(method().parameter(p, type), m, params[p] == type);
          assertMatches(method().parameter(p, type.getTypeName()), m, params[p] == type);
        }
      }
      assertFalse(method().parameter(params.length, Object.class).test(m));
      for (Class<?> type : returns) {
        assertMatches(method().returning(type), m, reflected.getReturnType() == type);
        assertMatches(method().returning(type.getTypeName()), m, reflected.getReturnType() == type);
      }
    }
    assertEquals(0, methods.size());
  }

  private static void addMethod(
      ClassWriter writer, int id, Class<?> returnType, Class<?>... params) {
    Type[] arguments = Arrays.stream(params).map(Type::getType).toArray(Type[]::new);
    writer
        .visitMethod(
            ACC_PUBLIC | ACC_ABSTRACT,
            "method" + id,
            Type.getMethodDescriptor(Type.getType(returnType), arguments),
            null,
            null)
        .visitEnd();
  }

  private static final class FixtureLoader extends ClassLoader {
    Class<?> define(byte[] bytes) {
      return defineClass(null, bytes, 0, bytes.length);
    }
  }
}
