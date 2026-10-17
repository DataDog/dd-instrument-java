package datadog.instrument.classmatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class TypeStringTest {
  @ParameterizedTest
  @MethodSource("typeStrings")
  void prefixAndSuffixMatchingStayWithinTypeString(TypeString type) {
    String name = type.toString();
    for (String candidate : new String[] {"", "A", "L", "LA", "A;", "A;I", "()LA", "Longer"}) {
      assertEquals(
          name.startsWith(candidate),
          TypeMatcher.typeStartsWith(candidate).test(type),
          "prefix: " + candidate);
      assertEquals(
          name.endsWith(candidate),
          TypeMatcher.typeEndsWith(candidate).test(type),
          "suffix: " + candidate);
    }
  }

  static Stream<TypeString> typeStrings() {
    return Stream.of(
        new FieldOutline(0, "example", "LA;").typeString(),
        new MethodOutline(0, "example", "(LA;I)V", new String[0]).parameterTypeString(0),
        new MethodOutline(0, "example", "()LA;", new String[0]).returnTypeString());
  }
}
