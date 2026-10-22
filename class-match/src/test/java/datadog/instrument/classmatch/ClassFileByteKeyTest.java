package datadog.instrument.classmatch;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ClassFileByteKeyTest {
  @Test
  void byteRangesUseTheirContentAndIgnoreSurroundingBytes() {
    byte[] value = {0, -1, 127, -128};
    byte[] surrounded = {42, 0, -1, 127, -128, 99};
    ClassFile.UtfKey whole = new ClassFile.UtfKey(value);
    ClassFile.UtfKey window = new ClassFile.UtfKey(surrounded, 1, value.length);
    assertEquals(Arrays.hashCode(value), whole.hashCode());
    assertEquals(whole.hashCode(), window.hashCode());
    assertEquals(whole, window);
    assertEquals(window, whole);
    assertTrue(ClassFile.sameBytes(surrounded, 1, 5, value, 0));
    assertEquals(Arrays.hashCode(value), ClassFile.hashBytes(surrounded, 1, 5));
    assertEquals(1, ClassFile.hashBytes(value, 2, 2));
    assertTrue(ClassFile.sameBytes(value, 2, 2, new byte[0], 0));
    assertFalse(whole.equals(null));
    assertFalse(whole.equals(new byte[] {0, -1, 127, -128}));
    assertFalse(whole.equals(new ClassFile.UtfKey(new byte[0])));
  }

  @Test
  void distinctBytesWithTheSameHashRemainDifferentKeys() {
    ClassFile.UtfKey first = new ClassFile.UtfKey("Aa".getBytes(US_ASCII));
    ClassFile.UtfKey second = new ClassFile.UtfKey("BB".getBytes(US_ASCII));
    assertEquals(first.hashCode(), second.hashCode());
    assertFalse(first.equals(second));
    Map<ClassFile.UtfKey, String> lookup = new HashMap<>();
    lookup.put(first, "first");
    lookup.put(second, "second");
    assertEquals("first", lookup.get(new ClassFile.UtfKey("!Aa!".getBytes(US_ASCII), 1, 2)));
    assertEquals("second", lookup.get(new ClassFile.UtfKey("!BB!".getBytes(US_ASCII), 1, 2)));
  }
}
