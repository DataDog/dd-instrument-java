package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.AccessMatcher.PRIVATE;
import static datadog.instrument.classmatch.AccessMatcher.PUBLIC;
import static datadog.instrument.classmatch.AccessMatcher.STATIC;
import static datadog.instrument.classmatch.FieldMatcher.field;
import static datadog.instrument.classmatch.MatcherTestSupport.assertMatches;
import static datadog.instrument.classmatch.MatcherTestSupport.fieldOutline;
import static datadog.instrument.classmatch.StandardMatchers.nameEndsWith;
import static datadog.instrument.classmatch.StandardMatchers.nameStartsWith;
import static datadog.instrument.classmatch.StandardMatchers.namedOneOf;
import static datadog.instrument.classmatch.TypeMatcher.type;
import static datadog.instrument.classmatch.TypeMatcher.typeEndsWith;
import static datadog.instrument.classmatch.TypeMatcher.typeOneOf;
import static datadog.instrument.classmatch.TypeMatcher.typeStartsWith;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class FieldMatcherTest {
  @ParameterizedTest
  @MethodSource("datadog.instrument.classmatch.MatcherTestSupport#types")
  void exactTypeOverloads(String name, Class<?> type, String descriptor) {
    FieldOutline candidate = fieldOutline(descriptor);
    assertMatches(field("value").type(name), candidate, true);
    assertMatches(field("value").type(type), candidate, true);
    assertMatches(field("missing").type(type), candidate, false);
    assertMatches(field("missing").type(name), candidate, false);
    assertMatches(field("value").type(Object.class), candidate, false);
    assertMatches(field("value").type(name), fieldOutline("Ljava/lang/Object;"), false);
  }

  @Test
  void namesAccessAndTypeMustBelongToTheSameField() {
    FieldOutline candidate = fieldOutline(Modifier.PUBLIC | Modifier.STATIC, "value", "I");
    assertMatches(
        field(namedOneOf("missing", "value")).access(PUBLIC.and(STATIC)).type(int.class),
        candidate,
        true);
    assertMatches(field(nameStartsWith("val").and(nameEndsWith("ue"))), candidate, true);
    assertMatches(field("value").access(PRIVATE), candidate, false);
    assertMatches(field("Value"), candidate, false);
  }

  @Test
  void objectTypePredicatesUseInternalNames() {
    FieldOutline candidate = fieldOutline("Ljava/lang/String;");
    assertMatches(field("value").and(type("java.lang.String")), candidate, true);
    assertMatches(
        field("value").and(typeOneOf("java.lang.Object", "java.lang.String")), candidate, true);
    assertMatches(
        field("value").and(typeStartsWith("java.lang.").and(typeEndsWith("String"))),
        candidate,
        true);
    assertMatches(field("value").and(type("java.lang.Object")), candidate, false);
    assertMatches(
        field("missing")
            .and(
                (TypeMatcher)
                    t -> {
                      throw new AssertionError("name should short circuit");
                    }),
        candidate,
        false);
  }

  @Test
  void primitiveAndArrayTypesNeverReachTypePredicates() {
    TypeMatcher unexpected =
        t -> {
          throw new AssertionError("only object names are supported");
        };
    for (String descriptor : new String[] {"I", "Z", "[I", "[[Ljava/lang/String;"}) {
      assertMatches(field("value").and(unexpected), fieldOutline(descriptor), false);
    }
  }
}
