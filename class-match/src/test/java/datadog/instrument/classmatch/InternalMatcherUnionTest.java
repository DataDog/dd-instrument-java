package datadog.instrument.classmatch;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class InternalMatcherUnionTest {
  @Test
  void extraEntriesSurviveExpansionOfBothNestedOperands() {
    Union left = new Union("a", "b");
    Union right = new Union("c", "d");
    Object[] entries = {left, right, "extra", "last"};
    Union combined = new Union(entries);
    assertArrayEquals(new Object[] {"a", "b", "c", "d", "extra", "last"}, combined.matchers);
    assertArrayEquals(new Object[] {left, right, "extra", "last"}, entries);
    assertArrayEquals(new Object[] {"a", "b"}, left.matchers);
    assertArrayEquals(new Object[] {"c", "d"}, right.matchers);
  }

  private static final class Union extends InternalMatchers.MatcherUnion<Object> {
    Union(Object... entries) {
      super(entries);
    }
  }
}
