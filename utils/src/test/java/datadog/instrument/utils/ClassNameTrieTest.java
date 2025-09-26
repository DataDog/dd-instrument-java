package datadog.instrument.utils;

import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static javax.tools.ToolProvider.getSystemJavaCompiler;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.tools.JavaCompiler;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ClassNameTrieTest {

  static ClassNameTrie testClassNamesTrie;

  @BeforeAll
  static void buildTestClassNamesTrie() throws IOException {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    builder.readClassNameMapping(Paths.get("build/resources/test/example/test_class_names.trie"));
    testClassNamesTrie = builder.buildTrie();
  }

  @ParameterizedTest
  @MethodSource
  void classNameMapping(String name, int value) {
    assertEquals(value, testClassNamesTrie.apply(name));
    String internalName = name.replace('.', '/');
    assertEquals(value, testClassNamesTrie.apply(internalName));
  }

  static Stream<Arguments> classNameMapping() {
    return Stream.of(
        Arguments.of("One", 1),
        Arguments.of("com.Two", 2),
        Arguments.of("com.foo.Three", 3),
        Arguments.of("company.foo.Four", 4),
        Arguments.of("com.foobar.Five", 5),
        Arguments.of("company.foobar.Six", 6),
        Arguments.of("company.foobar.Sixty", 60),
        Arguments.of("com.f", 7),
        Arguments.of("com.foo.a", 8),
        Arguments.of("com.foobar.b", 9),
        Arguments.of("company.f", 10),
        Arguments.of("company.foo.a", 11),
        Arguments.of("company.foobar.S", 12),
        Arguments.of("com.Two$f", 13),
        Arguments.of("foobar.Two$b", 14),
        Arguments.of("", -1),
        Arguments.of("O", -1),
        Arguments.of("_", -1),
        Arguments.of("On", -1),
        Arguments.of("O_", -1),
        Arguments.of("On_", -1),
        Arguments.of("OneNoMatch", -1),
        Arguments.of("com.Twos", 7),
        Arguments.of("com.foo.Threes", 8),
        Arguments.of("com.foobar.Fives", 9),
        Arguments.of("foobar.Thre", -1),
        Arguments.of("foobar.Three", 15),
        Arguments.of("foobar.ThreeMore", 15));
  }

  @ParameterizedTest
  @MethodSource
  void classNameMappingFromIndex(String name, int value) {
    int from = "garbage.".length();
    assertEquals(value, testClassNamesTrie.apply(name, from));
  }

  static Stream<Arguments> classNameMappingFromIndex() {
    return Stream.of(
        Arguments.of("garbage.One", 1),
        Arguments.of("garbage.com.Two", 2),
        Arguments.of("garbage.com.foo.Three", 3),
        Arguments.of("garbage.company.foo.Four", 4),
        Arguments.of("garbage.com.foobar.Five", 5),
        Arguments.of("garbage.company.foobar.Six", 6),
        Arguments.of("garbage.company.foobar.Sixty", 60),
        Arguments.of("garbage.com.f", 7),
        Arguments.of("garbage.com.foo.a", 8),
        Arguments.of("garbage.com.foobar.b", 9),
        Arguments.of("garbage.company.f", 10),
        Arguments.of("garbage.company.foo.a", 11),
        Arguments.of("garbage.company.foobar.S", 12),
        Arguments.of("garbage.com.Two$f", 13),
        Arguments.of("garbage.foobar.Two$b", 14),
        Arguments.of("garbage.", -1),
        Arguments.of("garbage.O", -1),
        Arguments.of("garbage._", -1),
        Arguments.of("garbage.On", -1),
        Arguments.of("garbage.O_", -1),
        Arguments.of("garbage.On_", -1),
        Arguments.of("garbage.OneNoMatch", -1),
        Arguments.of("garbage.com.Twos", 7),
        Arguments.of("garbage.com.foo.Threes", 8),
        Arguments.of("garbage.com.foobar.Fives", 9),
        Arguments.of("garbage.foobar.Thre", -1),
        Arguments.of("garbage.foobar.Three", 15),
        Arguments.of("garbage.foobar.ThreeMore", 15));
  }

  @Test
  void generateJavaSource(@TempDir File classesDir) throws Exception {
    ClassNameTrie.JavaGenerator.main(
        new String[] {
          "build/resources/test", classesDir.toString(), "example/test_class_names.trie"
        });

    JavaCompiler compiler = getSystemJavaCompiler();
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    fileManager.setLocation(StandardLocation.CLASS_OUTPUT, singletonList(classesDir));

    List<String> trieSource =
        Files.readAllLines(new File(classesDir, "example/TestClassNamesTrie.java").toPath());

    List<InMemoryJavaFile> compilationUnits =
        singletonList(new InMemoryJavaFile("example/TestClassNamesTrie.java", trieSource));

    JavaCompiler.CompilationTask glueCompilationTask =
        compiler.getTask(null, fileManager, null, null, null, compilationUnits);

    assertTrue(glueCompilationTask.call());

    Method trieApply;
    try (URLClassLoader cl = URLClassLoader.newInstance(new URL[] {classesDir.toURI().toURL()})) {
      Class<?> testTrieClass = cl.loadClass("example.TestClassNamesTrie");
      trieApply = testTrieClass.getMethod("apply", String.class);
    }

    assertEquals(5, trieApply.invoke(null, "com.foobar.Five"));
  }

  @Test
  @SuppressWarnings("UnnecessaryUnicodeEscape")
  void manual() throws IOException {
    ClassNameTrie trie =
        new ClassNameTrie("\001\141\u4001\001\142\u4002\001\143\u8003".toCharArray(), null);

    assertEquals(1, trie.apply("a"));
    assertEquals(2, trie.apply("ab"));
    assertEquals(3, trie.apply("abc"));
    assertEquals(-1, trie.apply(""));
    assertEquals(-1, trie.apply("b"));
    assertEquals(-1, trie.apply("c"));

    assertFalse(new ClassNameTrie.Builder(trie).isEmpty());

    ClassNameTrie emptyTrie =
        ClassNameTrie.readFrom(
            new DataInputStream(
                new ByteArrayInputStream(
                    new byte[] {
                      (byte) 0xDD, 0x09, 0x72, 0x13, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                    })));

    assertTrue(new ClassNameTrie.Builder(emptyTrie).isEmpty());
  }

  @Test
  void badData() {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();

    assertThrows(IllegalArgumentException.class, () -> builder.put(null, 1));
    assertThrows(IllegalArgumentException.class, () -> builder.put("test", -1));
    assertThrows(IllegalArgumentException.class, () -> builder.put("test", 8192));

    // check that a bad magic header fails
    assertThrows(
        IOException.class,
        () ->
            ClassNameTrie.readFrom(
                new DataInputStream(
                    new ByteArrayInputStream(
                        new byte[] {
                          (byte) 0xCD,
                          0x09,
                          0x72,
                          0x13,
                          0x00,
                          0x00,
                          0x00,
                          0x00,
                          0x00,
                          0x00,
                          0x00,
                          0x00
                        }))));

    assertTrue(builder.isEmpty());
  }

  @Test
  void overflow() {
    Map<String, Integer> data =
        IntStream.range(0, 8191).boxed().collect(toMap(this::randomKey, identity()));

    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      builder.put(entry.getKey(), entry.getValue());
    }

    ClassNameTrie trie = builder.buildTrie();

    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      assertEquals(entry.getValue(), trie.apply(entry.getKey()));
    }
  }

  @Test
  void overwriting() {
    Map<String, Integer> data =
        IntStream.range(0, 128).boxed().collect(toMap(this::randomKey, identity()));

    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      builder.put(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      builder.put(entry.getKey(), (0x1000 | entry.getValue()));
    }

    ClassNameTrie trie = builder.buildTrie();

    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      assertEquals(0x1000 | entry.getValue(), trie.apply(entry.getKey()));
    }
  }

  @Test
  void roundTrip() {
    Map<String, Integer> data =
        IntStream.range(0, 128).boxed().collect(toMap(this::randomKey, identity()));

    ClassNameTrie.Builder exporter = new ClassNameTrie.Builder();
    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      exporter.put(entry.getKey(), entry.getValue());
    }

    ClassNameTrie importer;
    try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
      exporter.writeTo(new DataOutputStream(sink));
      try (InputStream source = new ByteArrayInputStream(sink.toByteArray())) {
        importer = ClassNameTrie.readFrom(new DataInputStream(source));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    for (Map.Entry<String, Integer> entry : data.entrySet()) {
      assertEquals(entry.getValue(), importer.apply(entry.getKey()));
    }
  }

  private String randomKey(int unused) {
    return UUID.randomUUID().toString().replace('-', '.');
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
