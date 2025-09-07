package datadog.instrument.classmatch;

import static java.util.Arrays.asList;
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

  static {
    try {
      sampleUnicodeClass =
          Files.readAllBytes(Paths.get("build/classes/java/test/sample/My例クラス.class"));
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
    assertArrayEquals(new String[] {"java/io/Serializable"}, outline.interfaces);
    assertArrayEquals(new String[] {}, outline.annotations);
    assertEquals("実例", outline.fields[0].fieldName);
    assertEquals("Ljava/lang/String;", outline.fields[0].descriptor);
    assertEquals("<init>", outline.methods[0].methodName);
    assertEquals("()V", outline.methods[0].descriptor);
    assertArrayEquals(new String[] {}, outline.methods[0].annotations);
    assertEquals("何かをする", outline.methods[1].methodName);
    assertEquals("([Ljava/lang/Object;)Ljava/lang/Boolean;", outline.methods[1].descriptor);
    assertArrayEquals(new String[] {"java/lang/SafeVarargs"}, outline.methods[1].annotations);
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
