package datadog.instrument.glue;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
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

  /** Perform strict class verification, like -Xverify:all */
  private static void verifyAll(byte[] bytecode) {
    StringWriter errors = new StringWriter();
    CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(errors));
    assertThat(errors.toString()).isEmpty();
  }

  @Test
  void relocatedGlueClassesPassVerification() throws Exception {
    // ObjectStoreGlue is generated at build-time and already compiled onto the test classpath
    List<byte[]> relocatedBytecode = new ArrayList<>();
    for (Field field : ObjectStoreGlue.class.getFields()) {
      if (field.getType() == String.class && !field.getName().equals("PREFIX")) {
        relocatedBytecode.add(Glue.unpackBytecode((String) field.get(null)));
      }
    }

    // KeyWithValue, ObjectStoreDispatch, GlobalObjectStore + $StoreKey + $LookupKey
    assertThat(relocatedBytecode).hasSize(5);

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

  /** Performs structural checks without resolving referenced types */
  private static void verifyStructure(byte[] bytecode) {
    new ClassReader(bytecode).accept(new CheckClassAdapter(new ClassWriter(0), true), 0);
  }
}
