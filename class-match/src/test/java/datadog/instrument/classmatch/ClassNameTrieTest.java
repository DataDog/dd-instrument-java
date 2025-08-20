package datadog.instrument.classmatch;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class ClassNameTrieTest {

  @Test
  @SuppressWarnings("UnnecessaryUnicodeEscape")
  void manual() {
    ClassNameTrie trie =
        new ClassNameTrie("\001\141\u4001\001\142\u4002\001\143\u8003".toCharArray(), null);

    assertEquals(1, trie.apply("a"));
    assertEquals(2, trie.apply("ab"));
    assertEquals(3, trie.apply("abc"));
    assertEquals(-1, trie.apply(""));
    assertEquals(-1, trie.apply("b"));
    assertEquals(-1, trie.apply("c"));
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
      assertEquals(trie.apply(entry.getKey()), entry.getValue());
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
      assertEquals(trie.apply(entry.getKey()), 0x1000 | entry.getValue());
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
      assertEquals(importer.apply(entry.getKey()), entry.getValue());
    }
  }

  private String randomKey(int unused) {
    return UUID.randomUUID().toString().replace('-', '.');
  }
}
