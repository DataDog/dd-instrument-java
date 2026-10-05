package datadog.instrument.glue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.objectweb.asm.Opcodes.ASM9;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.util.CheckClassAdapter;

class ObjectStoreGlueGeneratorTest {

  @Test
  void keyWithValueBytecodePassesVerification() {
    verifyAll(ObjectStoreGlueGenerator.generateKeyWithValueBytecode());
  }

  @Test
  void objectStoreDispatchBytecodePassesVerification() {
    verifyAll(ObjectStoreGlueGenerator.generateObjectStoreDispatchBytecode());
  }

  @Test
  void objectStoreDispatchFallbacksMatchGlobalObjectStoreSignatures() throws Exception {
    // GlobalObjectStore is package-private, so look it up by name
    Class<?> globalObjectStore = Class.forName("datadog.instrument.fieldinject.GlobalObjectStore");

    // collect every static call the generated dispatch makes to GlobalObjectStore
    String globalObjectStoreClass = Type.getInternalName(globalObjectStore);
    Set<String> fallbackCalls = new TreeSet<>();
    new ClassReader(ObjectStoreGlueGenerator.generateObjectStoreDispatchBytecode())
        .accept(
            new ClassVisitor(ASM9) {
              @Override
              public MethodVisitor visitMethod(
                  int access,
                  String name,
                  String descriptor,
                  String signature,
                  String[] exceptions) {
                return new MethodVisitor(ASM9) {
                  @Override
                  public void visitMethodInsn(
                      int opcode,
                      String owner,
                      String name,
                      String descriptor,
                      boolean isInterface) {
                    if (opcode == Opcodes.INVOKESTATIC && owner.equals(globalObjectStoreClass)) {
                      fallbackCalls.add(name + descriptor);
                    }
                  }
                };
              }
            },
            0);

    // the dispatch lives in the same package, so it can call any non-private static method
    Set<String> accessibleStaticMethods = new TreeSet<>();
    for (Method method : globalObjectStore.getDeclaredMethods()) {
      int modifiers = method.getModifiers();
      if (Modifier.isStatic(modifiers) && !Modifier.isPrivate(modifiers)) {
        accessibleStaticMethods.add(method.getName() + Type.getMethodDescriptor(method));
      }
    }

    assertThat(fallbackCalls).isNotEmpty();
    assertThat(accessibleStaticMethods)
        .as("generated fallbacks must match non-private static methods on GlobalObjectStore")
        .containsAll(fallbackCalls);
  }

  /** Perform strict class verification, like -Xverify:all */
  private static void verifyAll(byte[] bytecode) {
    StringWriter errors = new StringWriter();
    CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(errors));
    assertThat(errors.toString()).isEmpty();
  }

  @Test
  void relocatedGlueClassesPassVerification() throws Exception {
    List<byte[]> relocatedBytecode = relocatedBytecode();

    // KeyWithValue, ObjectStoreDispatch, GlobalObjectStore + $StoreKey + $LookupKey
    assertThat(relocatedBytecode).hasSize(7);

    // the relocated classes reference each other by their new java.lang names, which can't be
    // loaded via a normal class loader (the JVM forbids defining classes into java.* packages
    // outside the bootstrap loader), so we can't run full type-resolving verification here;
    // fall back to structural verification, which still catches corrupted/inconsistent bytecode
    for (byte[] bytecode : relocatedBytecode) {
      verifyStructure(bytecode);
      // relocated classes must live under the shared java.lang bootstrap namespace
      assertThat(new ClassReader(bytecode).getClassName()).startsWith("java/lang/$Datadog$");
    }
  }

  @Test
  void relocatedGlueClassesOnlyReferenceRelocatedTypes() throws Exception {
    // catches nested/helper classes missing from the relocation list, which would otherwise
    // only fail at runtime with NoClassDefFoundError when loaded from the bootstrap classpath
    Set<String> unrelocatedTypes = new TreeSet<>();
    Remapper collector =
        new Remapper(ASM9) {
          @Override
          public String map(String internalName) {
            if (internalName.startsWith("datadog/")) {
              unrelocatedTypes.add(internalName);
            }
            return internalName;
          }
        };
    for (byte[] bytecode : relocatedBytecode()) {
      new ClassReader(bytecode).accept(new ClassRemapper(new ClassWriter(0), collector), 0);
    }
    assertThat(unrelocatedTypes).isEmpty();
  }

  /** Unpacks the relocated bytecode from the build-time generated {@code ObjectStoreGlue}. */
  private static List<byte[]> relocatedBytecode() throws IllegalAccessException {
    // ObjectStoreGlue is generated at build-time and already compiled onto the test classpath
    List<byte[]> relocatedBytecode = new ArrayList<>();
    for (Field field : ObjectStoreGlue.class.getFields()) {
      if (field.getType() == String.class && !field.getName().equals("PREFIX")) {
        relocatedBytecode.add(Glue.unpackBytecode((String) field.get(null)));
      }
    }
    return relocatedBytecode;
  }

  /** Performs structural checks without resolving referenced types */
  private static void verifyStructure(byte[] bytecode) {
    new ClassReader(bytecode).accept(new CheckClassAdapter(new ClassWriter(0), true), 0);
  }
}
