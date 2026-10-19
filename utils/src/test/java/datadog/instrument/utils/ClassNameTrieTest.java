package datadog.instrument.utils;

import static java.util.Collections.singletonList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;
import static javax.tools.ToolProvider.getSystemJavaCompiler;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import java.util.Arrays;
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
import org.junit.jupiter.params.provider.ValueSource;

@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
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

    assertFalse(new ClassNameTrie.Builder(trie).isEmpty());

    assertEquals(1, trie.apply("a"));
    assertEquals(2, trie.apply("ab"));
    assertEquals(3, trie.apply("abc"));
    assertEquals(-1, trie.apply(""));
    assertEquals(-1, trie.apply("b"));
    assertEquals(-1, trie.apply("c"));

    ClassNameTrie emptyTrie =
        ClassNameTrie.readFrom(
            new DataInputStream(
                new ByteArrayInputStream(
                    new byte[] {
                      (byte) 0xDD, 0x09, 0x72, 0x13, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
                    })));

    assertTrue(new ClassNameTrie.Builder(emptyTrie).isEmpty());

    assertEquals(-1, emptyTrie.apply("a"));
    assertEquals(-1, emptyTrie.apply("ab"));
    assertEquals(-1, emptyTrie.apply("abc"));
    assertEquals(-1, emptyTrie.apply(""));
    assertEquals(-1, emptyTrie.apply("b"));
    assertEquals(-1, emptyTrie.apply("c"));
  }

  @Test
  void badData() {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();

    assertThrows(IllegalArgumentException.class, () -> builder.put(null, 1));
    assertThrows(IllegalArgumentException.class, () -> builder.put("", 1));
    assertThrows(IllegalArgumentException.class, () -> builder.put("*", 1));
    assertThrows(IllegalArgumentException.class, () -> builder.put("test", -1));
    assertThrows(IllegalArgumentException.class, () -> builder.put("test", 8192));
    assertThrows(
        IllegalArgumentException.class,
        () -> builder.put(Stream.generate(() -> "_").limit(8192).collect(joining()), 1));

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
  void tooManyLongJumps() {
    String padding = padding(7700);
    ClassNameTrie.Builder builder = builderAtLongJumpLimit(padding);
    // last put should exceed the number of allowed long-jumps
    assertThrows(
        IllegalArgumentException.class, () -> builder.put("" + ((char) 67) + ((char) 2), 1));
  }

  @ParameterizedTest
  @ValueSource(chars = {'\u7FFF', '\u8000', '\uFFFF'})
  void extendingLeafPreservesFollowingUnicodeSegment(char nextCharacter) {
    for (String suffix : new String[] {"x", "xyz"}) {
      ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
      String followingKey = "b" + nextCharacter;
      builder.put("a", 1);
      builder.put(followingKey, 2);

      String extension = "a" + suffix;
      builder.put(extension, 3);
      assertEquals(1, builder.apply("a"));
      assertEquals(2, builder.apply(followingKey));
      assertEquals(3, builder.apply(extension));

      builder.put(extension + "x", 4);
      builder.put(followingKey + "x", 5);
      ClassNameTrie trie = builder.buildTrie();
      assertEquals(1, trie.apply("a"));
      assertEquals(2, trie.apply(followingKey));
      assertEquals(3, trie.apply(extension));
      assertEquals(4, trie.apply(extension + "x"));
      assertEquals(5, trie.apply(followingKey + "x"));
    }
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void builtTriePreservesValuesAfterBuilderUpdates(boolean newBuilder) {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    builder.put("a", 1);
    ClassNameTrie original = builder.buildTrie();
    if (newBuilder) {
      builder = new ClassNameTrie.Builder(original);
    }

    builder.put("a", 2);
    ClassNameTrie updated = builder.buildTrie();
    builder.put("a", 3);
    builder.put("ab", 4);

    assertEquals(1, original.apply("a"));
    assertEquals(-1, original.apply("ab"));
    assertEquals(2, updated.apply("a"));
    assertEquals(-1, updated.apply("ab"));
    assertEquals(3, builder.apply("a"));
    assertEquals(4, builder.apply("ab"));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void builtTriePreservesLongJumpsAfterBuilderUpdates(boolean newBuilder) {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    String padding = padding(7700);
    for (char branch = 'a'; branch <= 'h'; branch++) {
      builder.put("aa" + branch + padding, 1);
    }
    builder.put("abx", 2);
    ClassNameTrie original = builder.buildTrie();
    if (newBuilder) {
      builder = new ClassNameTrie.Builder(original);
    }

    String extension = "aaa" + padding + "x";
    builder.put(extension, 3); // shifts the existing long jump to 'abx'
    ClassNameTrie updated = builder.buildTrie();
    builder.put(extension + "x", 4); // shifts it again after another build

    assertEquals(2, original.apply("abx"));
    assertEquals(2, updated.apply("abx"));
    assertEquals(2, builder.apply("abx"));
    for (char branch = 'a'; branch <= 'h'; branch++) {
      assertEquals(1, original.apply("aa" + branch + padding));
      assertEquals(1, updated.apply("aa" + branch + padding));
      assertEquals(1, builder.apply("aa" + branch + padding));
    }
    assertEquals(-1, original.apply(extension));
    assertEquals(3, updated.apply(extension));
    assertEquals(-1, updated.apply(extension + "x"));
    assertEquals(3, builder.apply(extension));
    assertEquals(4, builder.apply(extension + "x"));
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void growsLongJumpTableAfterCompaction(boolean newBuilder) {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    String padding = padding(7700);
    for (char branch = 'a'; branch <= 'h'; branch++) {
      builder.put("aa" + branch + padding, 1);
    }
    builder.put("ab", 2); // first long jump skips the padded sub-trie
    ClassNameTrie compacted = builder.buildTrie(); // compacts the long-jump table to one entry
    if (newBuilder) {
      builder = new ClassNameTrie.Builder(compacted);
    }

    builder.put("ac", 3); // needs a second long jump

    ClassNameTrie trie = builder.buildTrie();
    for (char branch = 'a'; branch <= 'h'; branch++) {
      assertEquals(1, trie.apply("aa" + branch + padding));
    }
    assertEquals(2, trie.apply("ab"));
    assertEquals(3, trie.apply("ac"));
  }

  @Test
  void failedInsertionPreservesExistingMappings() {
    String padding = padding(7700);
    ClassNameTrie.Builder builder = builderAtLongJumpLimit(padding);
    for (int j = 1; j <= 8; j++) {
      assertEquals(1, builder.apply("" + (char) 67 + '\u0001' + (char) j + padding));
    }

    // A nontrivial suffix forces insertion to move existing data before allocating a long jump.
    assertThrows(
        IllegalArgumentException.class, () -> builder.put("" + (char) 67 + (char) 2 + padding, 1));

    // Catching a rejected insertion must leave previously registered mappings readable.
    for (int j = 1; j <= 8; j++) {
      assertEquals(
          1,
          builder.apply("" + (char) 67 + '\u0001' + (char) j + padding),
          "Existing mapping " + j + " changed after a rejected insertion");
    }
  }

  private static ClassNameTrie.Builder builderAtLongJumpLimit(String padding) {
    // 65 groups x 64 long-jumps/group > 4096-entry limit; each group's sub-trie spans
    // ~61600 chars (8 padded entries) forcing 64 small leaf entries to use long-jumps
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    for (int g = 1; g <= 65; g++) {
      char groupChar = (char) (g <= 41 ? g : g + 1); // skip '*' = chr(42)
      for (int j = 1; j <= 8; j++) {
        builder.put("" + groupChar + (char) 1 + (char) j + padding, 1);
      }
      for (int k = 2; k <= 65; k++) {
        char smallChar = (char) (k <= 41 ? k : k + 1); // skip '*' = chr(42)
        builder.put("" + groupChar + smallChar, 1);
      }
    }
    for (int j = 1; j <= 8; j++) {
      builder.put("" + ((char) 67) + '\u0001' + (char) j + padding, 1);
    }
    return builder;
  }

  @ParameterizedTest
  @MethodSource("insertionsGrowingAncestorJump")
  void failedAncestorJumpConversionPreservesTrie(String key) throws IOException {
    ClassNameTrie.Builder builder = builderWithShortJumpNearLimit(0);
    ByteArrayOutputStream before = new ByteArrayOutputStream();
    builder.writeTo(new DataOutputStream(before));

    // These insertions need no local long jump, but grow an ancestor's short jump past the limit.
    for (int attempt = 0; attempt < 2; attempt++) {
      assertThrows(IllegalArgumentException.class, () -> builder.put(key, 2));
      ByteArrayOutputStream after = new ByteArrayOutputStream();
      builder.writeTo(new DataOutputStream(after));
      assertArrayEquals(before.toByteArray(), after.toByteArray());
    }
    assertEquals(1, builder.apply("aa" + padding(7675)));
    assertEquals(1, builder.apply("z"));
  }

  static Stream<String> insertionsGrowingAncestorJump() {
    return Stream.of(
        "aa" + padding(7675) + "x", // extend a leaf segment
        "aa" + padding(100), // insert a bud within a segment
        "aa", // prepend a node to a segment
        "aao", // split a segment on the left
        "aaq", // split a segment on the right
        "az"); // add a branch
  }

  @Test
  void fullLongJumpTableAllowsOverwritesAndExistingJumpUpdates() {
    ClassNameTrie.Builder builder = builderWithShortJumpNearLimit(1);
    String key = "aa" + padding(7675);
    builder.put(key + "x", 2); // consumes the final slot by converting the ancestor jump
    assertEquals(2, builder.apply(key + "x"));
    assertEquals(1, builder.apply(key));

    builder.put(key, 3); // overwrite a bud
    builder.put(key + "x", 4); // overwrite a leaf
    builder.put(key + "xy", 5); // grows the existing long jump without allocating another
    assertEquals(3, builder.apply(key));
    assertEquals(4, builder.apply(key + "x"));
    assertEquals(5, builder.apply(key + "xy"));
    assertEquals(1, builder.apply("z"));
  }

  private static String padding(int length) {
    char[] chars = new char[length];
    Arrays.fill(chars, 'p');
    return new String(chars);
  }

  @Test
  void fullLongJumpTableAllowsSmallInsertionsWithoutConversion() {
    ClassNameTrie.Builder builder = builderWithShortJumpNearLimit(0, 10);
    String key = "aa" + padding(7666);

    builder.put(key + "x", 2); // grows the ancestor jump from 61430 to 61433

    assertEquals(1, builder.apply(key));
    assertEquals(2, builder.apply(key + "x"));
    assertEquals(1, builder.apply("z"));
  }

  @Test
  void fullLongJumpTableAllowsBranchBeforeExistingLongJump() {
    ClassNameTrie.Builder builder = builderWithShortJumpNearLimit(1);
    String key = "aa" + padding(7675);
    builder.put(key + "x", 2); // consumes the final slot

    builder.put("0", 3); // a collapsed branch adds no content to the existing jumps

    assertEquals(3, builder.apply("0"));
    assertEquals(1, builder.apply(key));
    assertEquals(2, builder.apply(key + "x"));
    assertEquals(1, builder.apply("z"));
  }

  private static ClassNameTrie.Builder builderWithShortJumpNearLimit(int spareLongJumps) {
    return builderWithShortJumpNearLimit(spareLongJumps, 1);
  }

  private static ClassNameTrie.Builder builderWithShortJumpNearLimit(
      int spareLongJumps, int distanceToLimit) {
    // Root branches 'a' and 'z'; the 'a' sub-trie is just below the long-jump boundary.
    // Populate the long-jump table to model a builder whose other slots are occupied.
    int shortJump = 61440 - distanceToLimit;
    char[] data = new char[6 + shortJump];
    data[0] = 2;
    data[1] = 'a';
    data[2] = 'z';
    data[3] = 0;
    data[4] = 0x8001;
    data[5] = (char) shortJump;
    data[6] = 8;
    int offset = 0;
    for (int branch = 0; branch < 8; branch++) {
      int length = branch == 0 ? 7676 - distanceToLimit : 7676;
      data[7 + branch] = (char) ('a' + branch);
      data[15 + branch] = (char) length;
      if (branch > 0) {
        data[22 + branch] = (char) offset;
      }
      Arrays.fill(data, 30 + offset, 30 + offset + length, 'p');
      data[30 + offset + length] = 0x8001;
      offset += length + 1;
    }
    return new ClassNameTrie.Builder(new ClassNameTrie(data, new int[4096 - spareLongJumps]));
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

  @Test
  void roundTripNoMappings() {
    ClassNameTrie.Builder exporter = new ClassNameTrie.Builder();

    ClassNameTrie importer;
    try (ByteArrayOutputStream sink = new ByteArrayOutputStream()) {
      exporter.writeTo(new DataOutputStream(sink));
      try (InputStream source = new ByteArrayInputStream(sink.toByteArray())) {
        importer = ClassNameTrie.readFrom(new DataInputStream(source));
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    assertEquals(-1, importer.apply("test"));
  }

  @Test
  void buildFromEmptyTrie() {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder(ClassNameTrie.EMPTY_TRIE);

    assertTrue(builder.isEmpty());
    assertEquals(-1, builder.apply("test"));

    builder.put("abcde", 42);
    builder.put("ab.de", 67);
    builder.put("a/c/e", 76);

    assertFalse(builder.isEmpty());

    assertEquals(42, builder.apply("abcde"));

    assertEquals(67, builder.apply("ab.de"));
    assertEquals(67, builder.apply("ab/de"));

    assertEquals(76, builder.apply("a.c.e"));
    assertEquals(76, builder.apply("a/c/e"));
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
