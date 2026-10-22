package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.MatcherTestSupport.fieldOutline;
import static datadog.instrument.classmatch.MatcherTestSupport.methodOutline;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.tabletest.junit.TableTest;

/** Descriptor views beyond the boundary regressions owned by PR #144. */
class OutlineTypeViewsTest {
  @TableTest({
    "name                ",
    "'A'                 ",
    "'java/lang/String'  ",
    "'sample/Outer$Inner'",
    "'例/型'             "
  })
  void fieldParameterAndReturnViewsHaveStringContentAndHashes(String name) {
    FieldOutline field = fieldOutline("L" + name + ";");
    // Keep an additional primitive parameter: final object parameter coverage is in PR #144.
    MethodOutline method = methodOutline("(L" + name + ";I)L" + name + ";");
    MethodOutline noParameters = methodOutline("()L" + name + ";");
    for (int pass = 0; pass < 2; pass++) {
      assertView(name, field.typeString());
      assertView(name, method.parameterTypeString(0));
      assertView(name, method.returnTypeString());
      assertView(name, noParameters.returnTypeString());
      assertEquals(2, method.parameterCount());
      assertEquals(0, noParameters.parameterCount());
    }
  }

  @TableTest({
    "descriptor            ",
    "'Z'                   ",
    "'B'                   ",
    "'C'                   ",
    "'D'                   ",
    "'F'                   ",
    "'I'                   ",
    "'J'                   ",
    "'S'                   ",
    "'[I'                  ",
    "'[[Ljava/lang/String;'"
  })
  void primitiveAndArrayDescriptorsHaveNoObjectViews(String descriptor) {
    FieldOutline field = fieldOutline(descriptor);
    MethodOutline method = methodOutline("(" + descriptor + ")" + descriptor);
    for (int pass = 0; pass < 2; pass++) {
      assertNull(field.typeString());
      assertNull(method.parameterTypeString(0));
      assertNull(method.parameterTypeString(1));
      assertNull(method.returnTypeString());
      assertEquals(1, method.parameterCount());
    }
  }

  @TableTest({
    "descriptor              ",
    "'()V'                   ",
    "'(I)V'                  ",
    "'(Ljava/lang/String;I)V'"
  })
  void voidReturnsAndMissingParametersHaveNoViews(String descriptor) {
    MethodOutline method = methodOutline(descriptor);
    assertNull(method.returnTypeString());
    assertNull(method.parameterTypeString(method.parameterCount()));
    assertNull(method.parameterTypeString(method.parameterCount() + 1));
  }

  @SuppressFBWarnings(
      value = "EC_UNRELATED_TYPES",
      justification = "TypeString intentionally supports content equality with any CharSequence")
  private static void assertView(String expected, TypeString actual) {
    assertNotNull(actual);
    assertEquals(expected.length(), actual.length());
    assertEquals(expected, actual.toString());
    assertEquals(expected.hashCode(), actual.hashCode());
    assertTrue(actual.equals(expected));
    assertTrue(actual.equals(new StringBuilder(expected)));
    assertTrue(actual.equals(actual));
    assertFalse(actual.equals(expected + "Extra"));
    assertFalse(actual.equals("!" + expected.substring(1)));
    assertFalse(actual.equals(null));
    assertFalse(actual.equals(123));
    for (int i = 0; i < expected.length(); i++) {
      assertEquals(expected.charAt(i), actual.charAt(i));
    }
    assertThrows(UnsupportedOperationException.class, () -> actual.subSequence(0, 1));
  }
}
