package datadog.instrument.glue;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.CheckClassAdapter;

class DefineClassGlueGeneratorTest {

  @ParameterizedTest
  @ValueSource(strings = {"sun/misc", "jdk/internal/misc"})
  void generatedBytecodePassesVerification(String unsafeNamespace) {
    byte[] bytecode = DefineClassGlueGenerator.generateBytecode(unsafeNamespace);
    StringWriter errors = new StringWriter();

    // perform strict class verification, like -Xverify:all
    CheckClassAdapter.verify(new ClassReader(bytecode), false, new PrintWriter(errors));

    assertThat(errors.toString()).isEmpty();
  }
}
