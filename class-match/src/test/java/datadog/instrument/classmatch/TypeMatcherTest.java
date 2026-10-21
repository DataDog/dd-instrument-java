package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.MatcherTestSupport.assertMatches;
import static datadog.instrument.classmatch.TypeMatcher.type;
import static datadog.instrument.classmatch.TypeMatcher.typeEndsWith;
import static datadog.instrument.classmatch.TypeMatcher.typeOneOf;
import static datadog.instrument.classmatch.TypeMatcher.typeStartsWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TypeMatcherTest {
  static Stream<Arguments> names() {
    return Stream.of("java/lang/String", "sample/Outer$Inner", "例/型", "A", "")
        .flatMap(name -> Stream.of(Arguments.of(name, name), Arguments.of(name, typeString(name))));
  }

  private static TypeString typeString(String name) {
    return new TypeString("L" + name + ";", 1, name.length() + 1, name.hashCode());
  }

  @ParameterizedTest
  @MethodSource("names")
  void exactNamesNormalizeFactoriesAndAcceptCharSequences(String name, CharSequence candidate) {
    assertMatches(type(name), candidate, true);
    assertMatches(type(name.replace('/', '.')), candidate, true);
    assertMatches(type("missing/Type"), candidate, false);
    assertMatches(typeOneOf("missing.Type", name.replace('/', '.')), candidate, true);
    assertMatches(typeOneOf(Arrays.asList("missing.Type", name)), candidate, true);
    assertMatches(typeOneOf(), candidate, false);
    assertMatches(typeOneOf(Collections.emptyList()), candidate, false);
  }

  @Test
  void exactMatchingAcceptsCharSequencesWithoutStringHashes() {
    assertMatches(type("java.lang.String"), new StringBuilder("java/lang/String"), true);
    assertMatches(type("java.lang.Object"), new StringBuilder("java/lang/String"), false);
  }

  @Test
  void prefixesAndSuffixesNormalizeFactoryArguments() {
    for (CharSequence candidate :
        new CharSequence[] {
          "java/lang/String", new StringBuilder("java/lang/String"), typeString("java/lang/String")
        }) {
      assertMatches(typeStartsWith("java.lang."), candidate, true);
      assertMatches(typeEndsWith("lang.String"), candidate, true);
      assertMatches(typeStartsWith(""), candidate, true);
      assertMatches(typeEndsWith(""), candidate, true);
      assertMatches(typeStartsWith("javax."), candidate, false);
      assertMatches(typeEndsWith("Object"), candidate, false);
      assertMatches(typeStartsWith("java/lang/StringExtra"), candidate, false);
      assertMatches(typeEndsWith("Extra/java/lang/String"), candidate, false);
    }
  }

  @Test
  void membershipTakesASnapshotAndNormalizesDuplicates() {
    List<String> names =
        new ArrayList<>(Arrays.asList("sample.Type", "sample/Type", "sample.Other"));
    TypeMatcher matcher = typeOneOf(names);
    names.clear();
    assertMatches(matcher, "sample/Type", true);
    assertMatches(matcher, "sample/Other", true);
    assertMatches(matcher, "sample/Missing", false);
    assertMatches(type("sample.Type"), "sample.Type", false);
  }
}
