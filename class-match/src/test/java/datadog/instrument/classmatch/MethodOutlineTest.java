package datadog.instrument.classmatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.tabletest.junit.TableTest;

class MethodOutlineTest {
  @TableTest({
    "descriptor ",
    "'(LA;)V'   ",
    "'(ILA;)V'  ",
    "'(LA;LA;)V'"
  })
  void lastObjectParameterExcludesDescriptorDelimiters(String descriptor) {
    MethodOutline method = new MethodOutline(0, "example", descriptor, new String[0]);
    int index = method.parameterCount() - 1;

    TypeString type = method.parameterTypeString(index);
    assertEquals("A", type.toString());
    assertEquals("A".hashCode(), type.hashCode());
    assertTrue(MethodMatcher.method().parameter(index, TypeMatcher.type("A")).test(method));
  }

  @TableTest({
    "descriptor ",
    "'(LA;I)V'  ",
    "'(LA;LA;)V'"
  })
  void earlierObjectParameterExcludesDescriptorDelimiters(String descriptor) {
    MethodOutline method = new MethodOutline(0, "example", descriptor, new String[0]);

    assertEquals("A", method.parameterTypeString(0).toString());
    assertTrue(MethodMatcher.method().parameter(0, TypeMatcher.type("A")).test(method));
  }
}
