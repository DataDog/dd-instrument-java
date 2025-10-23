package datadog.instrument.utils;

import static java.util.Collections.singletonList;
import static javax.tools.ToolProvider.getSystemJavaCompiler;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class GlueTest {

  private static final byte[] exampleBytecode = {
    -54, -2, -70, -66, 0, 0, 0, 52, 0, 10, 10, 0, 2, 0, 3, 7, 0, 4, 12, 0, 5, 0, 6, 1, 0, 16, 106,
    97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 6, 60, 105, 110, 105,
    116, 62, 1, 0, 3, 40, 41, 86, 7, 0, 8, 1, 0, 7, 69, 120, 97, 109, 112, 108, 101, 1, 0, 4, 67,
    111, 100, 101, 0, 32, 0, 7, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 5, 0, 6, 0, 1, 0, 9, 0, 0, 0, 17,
    0, 1, 0, 1, 0, 0, 0, 5, 42, -73, 0, 1, -79, 0, 0, 0, 0, 0, 0
  };

  @Test
  void glueGeneration(@TempDir File classesDir) throws Exception {

    JavaCompiler compiler = getSystemJavaCompiler();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    fileManager.setLocation(StandardLocation.CLASS_OUTPUT, singletonList(classesDir));

    List<String> glueSource = new ArrayList<>();
    Glue.classHeader(glueSource, "TestGlue");
    glueSource.add("  String BYTECODE =");
    Glue.packBytecode(glueSource, exampleBytecode);
    glueSource.add("}");

    List<InMemoryJavaFile> compilationUnits =
        singletonList(new InMemoryJavaFile("datadog/instrument/glue/TestGlue.java", glueSource));

    JavaCompiler.CompilationTask glueCompilationTask =
        compiler.getTask(null, fileManager, null, null, null, compilationUnits);

    assertTrue(glueCompilationTask.call());

    String packedBytecode;
    try (URLClassLoader cl = URLClassLoader.newInstance(new URL[] {classesDir.toURI().toURL()})) {
      Class<?> testGlueClass = cl.loadClass("datadog.instrument.glue.TestGlue");
      packedBytecode = (String) testGlueClass.getField("BYTECODE").get(null);
    }

    assertArrayEquals(exampleBytecode, Glue.unpackBytecode(packedBytecode));
  }

  @Test
  void paddingRequired() {

    byte[] exampleBytecodeNeedsPadding = {
      -54, -2, -70, -66, 0, 0, 0, 52, 0, 10, 10, 0, 2, 0, 3, 7, 0, 4, 12, 0, 5, 0, 6, 1, 0, 16, 106,
      97, 118, 97, 47, 108, 97, 110, 103, 47, 79, 98, 106, 101, 99, 116, 1, 0, 6, 60, 105, 110, 105,
      116, 62, 1, 0, 3, 40, 41, 86, 7, 0, 8, 1, 0, 2, 69, 103, 1, 0, 4, 67, 111, 100, 101, 0, 32, 0,
      7, 0, 2, 0, 0, 0, 0, 0, 1, 0, 0, 0, 5, 0, 6, 0, 1, 0, 9, 0, 0, 0, 17, 0, 1, 0, 1, 0, 0, 0, 5,
      42, -73, 0, 1, -79, 0, 0, 0, 0, 0, 0
    };

    assertThrows(
        IllegalStateException.class,
        () -> Glue.packBytecode(new ArrayList<>(), exampleBytecodeNeedsPadding),
        "Bytecode length is not even; requires padding");
  }

  @Test
  void glueResource() {
    byte[] loadedBytecode = Glue.loadBytecode(GlueTest.class, "test.glue");
    assertArrayEquals(exampleBytecode, loadedBytecode);
  }

  @Test
  void missingGlueResource() {
    assertThrows(
        MissingResourceException.class, () -> Glue.loadBytecode(GlueTest.class, "missing.glue"));
  }

  static class InMemoryJavaFile extends SimpleJavaFileObject {
    private final List<String> lines;

    InMemoryJavaFile(String name, List<String> lines) {
      super(URI.create("mem:/" + name), Kind.SOURCE);
      this.lines = lines;
    }

    @Override
    public CharSequence getCharContent(boolean ignore) {
      return String.join(System.lineSeparator(), lines);
    }
  }
}
