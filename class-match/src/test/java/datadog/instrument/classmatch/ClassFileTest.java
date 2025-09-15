package datadog.instrument.classmatch;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.jar.JarFile;
import org.junit.jupiter.api.Test;

class ClassFileTest {

  static final byte[] sampleUnicodeClass;
  static final byte[] sampleParametersClass;

  static {
    try {
      sampleUnicodeClass =
          Files.readAllBytes(Paths.get("build/classes/java/test/sample/My例クラス.class"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    try {
      sampleParametersClass =
          Files.readAllBytes(Paths.get("build/classes/java/test/sample/MyParameters.class"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  void header() {
    testParsing("asm-test.jar", ClassFile::header);
  }

  @Test
  void unicodeHeader() {
    ClassHeader header = ClassFile.header(sampleUnicodeClass);

    assertEquals("sample/My例クラス", header.className);
    assertEquals("java/util/AbstractCollection", header.superName);
    assertArrayEquals(new String[] {"java/io/Serializable"}, header.interfaces);
  }

  @Test
  void outline() {
    ClassFile.annotationsOfInterest(
        asList("java/lang/Deprecated", "java/lang/FunctionalInterface"));
    testParsing("asm-test.jar", ClassFile::outline);
  }

  @Test
  void unicodeOutline() {
    ClassFile.annotationOfInterest("java/lang/SafeVarargs");
    ClassOutline outline = ClassFile.outline(sampleUnicodeClass);

    assertEquals("sample/My例クラス", outline.className);
    assertEquals("java/util/AbstractCollection", outline.superName);
    assertEquals(singletonList("java/io/Serializable"), outline.interfaces());
    assertEquals(emptyList(), outline.annotations());
    assertEquals("実例", outline.fields().get(0).fieldName);
    assertEquals("Ljava/lang/String;", outline.fields().get(0).descriptor);
    assertEquals("<init>", outline.methods().get(0).methodName);
    assertEquals("()V", outline.methods().get(0).descriptor);
    assertEquals(emptyList(), outline.methods().get(0).annotations());
    assertEquals("何かをする", outline.methods().get(1).methodName);
    assertEquals("([Ljava/lang/Object;)Ljava/lang/Boolean;", outline.methods().get(1).descriptor);
    assertEquals(singletonList("java/lang/SafeVarargs"), outline.methods().get(1).annotations());
  }

  @Test
  void parameterParsing() {
    ClassOutline outline = ClassFile.outline(sampleParametersClass);

    assertEquals(0, outline.methods().get(0).descriptorBoundaries().length);
    assertArrayEquals(new int[0], outline.methods().get(0).descriptorBoundaries());

    assertEquals(19, outline.methods().get(1).descriptorBoundaries().length);
    assertArrayEquals(
        new int[] {2, 3, 4, 5, 6, 7, 8, 9, 27, 29, 32, 36, 41, 47, 54, 62, 71, 86, 106},
        outline.methods().get(1).descriptorBoundaries());
  }

  @SuppressWarnings("SameParameterValue")
  private static void testParsing(String sampleJar, Consumer<byte[]> parser) {
    byte[] buf = new byte[16384];
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (JarFile sample = new JarFile("build/sampleBytecode/" + sampleJar)) {
      sample.stream()
          .filter(e -> e.getName().endsWith(".class"))
          .map(
              e -> {
                out.reset();
                try (InputStream in = sample.getInputStream(e)) {
                  int nRead;
                  while ((nRead = in.read(buf, 0, buf.length)) != -1) {
                    out.write(buf, 0, nRead);
                  }
                  return new SimpleEntry<>(e.getName(), out.toByteArray());
                } catch (IOException ignore) {
                  return null;
                }
              })
          .filter(Objects::nonNull)
          .forEach(
              entry -> {
                try {
                  parser.accept(entry.getValue());
                } catch (Throwable e) {
                  fail("Error parsing '" + entry.getKey() + "' from " + sampleJar, e);
                }
              });
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
