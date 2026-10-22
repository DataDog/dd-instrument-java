package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.AccessMatcher.INSTANCE;
import static datadog.instrument.classmatch.AccessMatcher.PUBLIC;
import static datadog.instrument.classmatch.AccessMatcher.STATIC;
import static datadog.instrument.classmatch.MatcherTestSupport.assertMatches;
import static datadog.instrument.classmatch.MatcherTestSupport.methodOutline;
import static datadog.instrument.classmatch.MethodMatcher.constructor;
import static datadog.instrument.classmatch.MethodMatcher.method;
import static datadog.instrument.classmatch.MethodMatcher.staticInitializer;
import static datadog.instrument.classmatch.StandardMatchers.nameStartsWith;
import static datadog.instrument.classmatch.StandardMatchers.namedOneOf;
import static datadog.instrument.classmatch.TypeMatcher.type;
import static datadog.instrument.classmatch.TypeMatcher.typeEndsWith;
import static datadog.instrument.classmatch.TypeMatcher.typeOneOf;
import static datadog.instrument.classmatch.TypeMatcher.typeStartsWith;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.tabletest.junit.TableTest;

class MethodMatcherTest {
  @TableTest(resource = "types.table")
  void exactSignatureOverloads(String name, Class<?> type, String descriptor) {
    MethodOutline candidate = methodOutline("(" + descriptor + descriptor + ")" + descriptor);
    assertMatches(method("execute").parameters(name, name).returning(name), candidate, true);
    assertMatches(method("execute").parameters(type, type).returning(type), candidate, true);
    assertMatches(method().parameter(0, name).parameter(1, type), candidate, true);
    assertMatches(method().parameter(0, type).parameter(1, name), candidate, true);
    assertMatches(method().parameters(2), candidate, true);
    assertMatches(method().parameters(1), candidate, false);
    assertMatches(method().parameters(type), candidate, false);
    assertMatches(method().parameter(2, type), candidate, false);
    assertMatches(method().parameter(3, name), candidate, false);
    assertMatches(method().parameter(0, Object.class), candidate, false);
    assertMatches(method().parameter(1, "java.lang.Object"), candidate, false);
    assertMatches(method().returning(Object.class), candidate, false);
    assertMatches(method().noParameters(), candidate, false);
  }

  @TableTest(resource = "types.table")
  void exactReturnTypesDistinguishArraysFromTheirComponents(
      String name, Class<?> type, String descriptor) {
    MethodOutline scalar = methodOutline("()" + descriptor);
    MethodOutline array = methodOutline("()[" + descriptor);
    Class<?> arrayType = Array.newInstance(type, 0).getClass();
    assertMatches(method().returning(name), array, false);
    assertMatches(method().returning(type), array, false);
    assertMatches(method().returning(name + "[]"), scalar, false);
    assertMatches(method().returning(arrayType), scalar, false);
    assertMatches(method().returning(name + "[]"), array, true);
    assertMatches(method().returning(arrayType), array, true);
  }

  @Test
  void exactReturnTypesDoNotMatchSuffixesOfOtherClassNames() {
    MethodOutline candidate = methodOutline("()Lsample/LA;");
    assertMatches(method().returning("A"), candidate, false);
    assertMatches(method().returning("sample.LA"), candidate, true);
  }

  @Test
  void emptySignaturesAndVoidReturn() {
    MethodOutline candidate = methodOutline("()V");
    assertMatches(method().noParameters().returning(void.class), candidate, true);
    assertMatches(method().parameters(0).returning("void"), candidate, true);
    assertMatches(method().parameters(new String[0]), candidate, true);
    assertMatches(method().parameters(new Class<?>[0]), candidate, true);
    assertMatches(method().parameter(0, int.class), candidate, false);
    assertMatches(method().parameter(0, "java.lang.String"), candidate, false);
    assertMatches(method().parameters(1), candidate, false);
    assertMatches(method().parameters(-1), candidate, false);
  }

  @Test
  void mixedParametersCountTypesRatherThanSlotsOrArrayDimensions() {
    MethodOutline candidate = methodOutline("(JD[[I[Ljava/lang/String;Ljava/lang/Object;Z)V");
    assertMatches(method().parameters(6), candidate, true);
    assertMatches(
        method()
            .parameters(
                long.class,
                double.class,
                int[][].class,
                String[].class,
                Object.class,
                boolean.class),
        candidate,
        true);
    assertMatches(method().parameter(4, Object.class).parameter(5, "boolean"), candidate, true);
    assertMatches(method().parameter(2, int[].class), candidate, false);
    assertMatches(method().parameter(6, void.class), candidate, false);
  }

  @Test
  void objectParameterPredicatesAndReturnPredicates() {
    // Final object-parameter boundary regressions belong to PR #144's MethodOutlineTest.
    MethodOutline candidate =
        methodOutline("(Ljava/lang/String;ILjava/lang/Object;Z)Ljava/lang/String;");
    assertMatches(method().parameter(0, type("java.lang.String")), candidate, true);
    assertMatches(
        method().parameter(2, typeOneOf("java.lang.String", "java.lang.Object")), candidate, true);
    assertMatches(
        method().parameter(0, typeStartsWith("java.").and(typeEndsWith("String"))),
        candidate,
        true);
    assertMatches(method().parameter(0, type("java.lang.Object")), candidate, false);
    assertMatches(method().returning(type("java.lang.String")), candidate, true);
    assertMatches(
        method().returning(typeOneOf("java.lang.Object", "java.lang.String")), candidate, true);
    assertMatches(
        method().returning(typeStartsWith("java.lang.").and(typeEndsWith("String"))),
        candidate,
        true);
    assertMatches(method().returning(type("java.lang.Object")), candidate, false);
  }

  @TableTest({
    "descriptor              ",
    "'()V'                   ",
    "'()I'                   ",
    "'()[I'                  ",
    "'()[[Ljava/lang/String;'",
    "'(I)V'                  ",
    "'([Ljava/lang/String;)V'"
  })
  void unsupportedOrAbsentTypesDoNotInvokeTypePredicates(String descriptor) {
    TypeMatcher unexpected =
        t -> {
          throw new AssertionError("not an object type");
        };
    MethodOutline candidate = methodOutline(descriptor);
    assertMatches(method().parameter(0, unexpected), candidate, false);
    assertMatches(method().parameter(10, unexpected), candidate, false);
    assertMatches(method().returning(unexpected), candidate, false);
  }

  @Test
  void namesSpecialMethodsAndAccess() {
    MethodOutline ordinary = methodOutline(Modifier.PUBLIC | Modifier.STATIC, "execute", "()V");
    assertMatches(
        method(namedOneOf("other", "execute")).access(PUBLIC.and(STATIC)), ordinary, true);
    assertMatches(method(nameStartsWith("exec")), ordinary, true);
    assertMatches(method("Execute"), ordinary, false);
    assertMatches(method().access(INSTANCE), ordinary, false);
    assertMatches(constructor(), ordinary, false);
    assertMatches(staticInitializer(), ordinary, false);
    MethodOutline init = methodOutline(0, "<init>", "()V");
    MethodOutline clinit = methodOutline(Modifier.STATIC, "<clinit>", "()V");
    assertMatches(constructor().noParameters(), init, true);
    assertMatches(staticInitializer(), clinit, true);
    assertMatches(constructor(), clinit, false);
    assertMatches(staticInitializer(), init, false);
    assertMatches(method(), init, true);
    assertMatches(method(), clinit, true);
    MethodMatcher unexpected =
        m -> {
          throw new AssertionError("all methods should short circuit");
        };
    assertMatches(method().or(unexpected), ordinary, true);
  }

  @Test
  void annotationOverloadsNormalizeAndHandleEmptyAnnotations() {
    MethodOutline candidate =
        new MethodOutline(0, "execute", "()V", new String[] {"sample/Other", "sample/Marker"});
    assertMatches(method().annotatedWith("sample.Marker"), candidate, true);
    assertMatches(method().annotatedWith("sample/Marker"), candidate, true);
    assertMatches(method().annotatedWith("sample.Missing", "sample.Marker"), candidate, true);
    assertMatches(
        method().annotatedWith(Collections.singletonList("sample.Marker")), candidate, true);
    assertMatches(method().annotatedWith("sample.Missing"), candidate, false);
    assertMatches(method().annotatedWith(new String[0]), candidate, false);
    assertMatches(method().annotatedWith(Collections.emptyList()), candidate, false);
    assertMatches(method().annotatedWith("sample.Marker"), methodOutline("()V"), false);
  }
}
