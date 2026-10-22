package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.AccessMatcher.PRIVATE;
import static datadog.instrument.classmatch.AccessMatcher.PUBLIC;
import static datadog.instrument.classmatch.AccessMatcher.STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;
import org.tabletest.junit.TypeConverterSources;

@TypeConverterSources(AccessMatcherTest.Converters.class)
class AccessMatcherTest {
  /** TableTest only calls public converters declared in public classes. */
  public static final class Converters {
    @TypeConverter
    public static AccessMatcher accessMatcher(String name) throws ReflectiveOperationException {
      return (AccessMatcher) AccessMatcher.class.getField(name).get(null);
    }
  }

  @TableTest({
    "Scenario        | Matcher         | Flag   | Inverted",
    "public          | PUBLIC          | 0x0001 | false   ",
    "private         | PRIVATE         | 0x0002 | false   ",
    "protected       | PROTECTED       | 0x0004 | false   ",
    "package-private | PACKAGE_PRIVATE | 0x0007 | true    ",
    "static          | STATIC          | 0x0008 | false   ",
    "instance        | INSTANCE        | 0x0008 | true    ",
    "final           | FINAL           | 0x0010 | false   ",
    "non-final       | NON_FINAL       | 0x0010 | true    ",
    "synchronized    | SYNCHRONIZED    | 0x0020 | false   ",
    "volatile        | VOLATILE        | 0x0040 | false   ",
    "varargs         | VARARGS         | 0x0080 | false   ",
    "transient       | TRANSIENT       | 0x0080 | false   ",
    "interface       | INTERFACE       | 0x0200 | false   ",
    "class           | CLASS           | 0x0200 | true    ",
    "abstract        | ABSTRACT        | 0x0400 | false   ",
    "concrete        | CONCRETE        | 0x0400 | true    "
  })
  void flagsIgnoreUnrelatedBits(AccessMatcher matcher, int flag, boolean inverted) {
    // Exercise every combination of the JVM's 16 access bits, including synthetic/bridge bits.
    for (int access = 0; access <= 0xffff; access++) {
      boolean expected = ((access & flag) != 0) != inverted;
      if (matcher.test(access) != expected) {
        fail("expected " + expected + " for access " + access);
      }
    }
  }

  @Test
  void compositionAndNegation() {
    for (int access = 0; access < 32; access++) {
      assertEquals(PUBLIC.test(access) && STATIC.test(access), PUBLIC.and(STATIC).test(access));
      assertEquals(PUBLIC.test(access) || PRIVATE.test(access), PUBLIC.or(PRIVATE).test(access));
      assertEquals(!PUBLIC.test(access), StandardMatchers.not(PUBLIC).test(access));
    }
    AccessMatcher unexpected =
        access -> {
          throw new AssertionError("should short circuit");
        };
    assertFalse(PUBLIC.and(unexpected).test(0));
    assertTrue(PUBLIC.or(unexpected).test(1));
  }
}
