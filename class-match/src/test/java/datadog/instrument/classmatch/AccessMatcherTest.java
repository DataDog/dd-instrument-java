package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.AccessMatcher.ABSTRACT;
import static datadog.instrument.classmatch.AccessMatcher.CLASS;
import static datadog.instrument.classmatch.AccessMatcher.CONCRETE;
import static datadog.instrument.classmatch.AccessMatcher.FINAL;
import static datadog.instrument.classmatch.AccessMatcher.INSTANCE;
import static datadog.instrument.classmatch.AccessMatcher.INTERFACE;
import static datadog.instrument.classmatch.AccessMatcher.NON_FINAL;
import static datadog.instrument.classmatch.AccessMatcher.PACKAGE_PRIVATE;
import static datadog.instrument.classmatch.AccessMatcher.PRIVATE;
import static datadog.instrument.classmatch.AccessMatcher.PROTECTED;
import static datadog.instrument.classmatch.AccessMatcher.PUBLIC;
import static datadog.instrument.classmatch.AccessMatcher.STATIC;
import static datadog.instrument.classmatch.AccessMatcher.SYNCHRONIZED;
import static datadog.instrument.classmatch.AccessMatcher.TRANSIENT;
import static datadog.instrument.classmatch.AccessMatcher.VARARGS;
import static datadog.instrument.classmatch.AccessMatcher.VOLATILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class AccessMatcherTest {
  static Stream<Arguments> flags() {
    return Stream.of(
        Arguments.of("public", PUBLIC, 0x0001, false),
        Arguments.of("private", PRIVATE, 0x0002, false),
        Arguments.of("protected", PROTECTED, 0x0004, false),
        Arguments.of("package-private", PACKAGE_PRIVATE, 0x0007, true),
        Arguments.of("static", STATIC, 0x0008, false),
        Arguments.of("instance", INSTANCE, 0x0008, true),
        Arguments.of("final", FINAL, 0x0010, false),
        Arguments.of("non-final", NON_FINAL, 0x0010, true),
        Arguments.of("synchronized", SYNCHRONIZED, 0x0020, false),
        Arguments.of("volatile", VOLATILE, 0x0040, false),
        Arguments.of("varargs", VARARGS, 0x0080, false),
        Arguments.of("transient", TRANSIENT, 0x0080, false),
        Arguments.of("interface", INTERFACE, 0x0200, false),
        Arguments.of("class", CLASS, 0x0200, true),
        Arguments.of("abstract", ABSTRACT, 0x0400, false),
        Arguments.of("concrete", CONCRETE, 0x0400, true));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("flags")
  void flagsIgnoreUnrelatedBits(String name, AccessMatcher matcher, int flag, boolean inverted) {
    // Exercise every combination of the JVM's 16 access bits, including synthetic/bridge bits.
    for (int access = 0; access <= 0xffff; access++) {
      boolean expected = ((access & flag) != 0) != inverted;
      if (matcher.test(access) != expected) {
        fail(name + ": expected " + expected + " for access " + access);
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
