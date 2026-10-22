package datadog.instrument.classmatch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.tabletest.junit.TableTest;
import org.tabletest.junit.TypeConverter;
import org.tabletest.junit.TypeConverterSources;

@TypeConverterSources(TypeStringTest.Converters.class)
class TypeStringTest {
  /** TableTest only calls public converters declared in public classes. */
  public static final class Converters {
    /**
     * Field descriptors give the field type; method descriptors their first parameter, or return.
     */
    @TypeConverter
    public static TypeString typeString(String descriptor) {
      if (descriptor.charAt(0) != '(') {
        return new FieldOutline(0, "example", descriptor).typeString();
      }
      MethodOutline method = new MethodOutline(0, "example", descriptor, new String[0]);
      return method.parameterCount() > 0
          ? method.parameterTypeString(0)
          : method.returnTypeString();
    }
  }

  @TableTest({
    "Scenario  | Type string",
    "field     | 'LA;'      ",
    "parameter | '(LA;I)V'  ",
    "return    | '()LA;'    "
  })
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
}
