package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.MatcherTestSupport.assertMatches;
import static datadog.instrument.classmatch.MatcherTestSupport.emptyClass;
import static datadog.instrument.classmatch.MatcherTestSupport.fieldOutline;
import static datadog.instrument.classmatch.MatcherTestSupport.methodOutline;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;

/** The same composition contract applies to each predicate API. */
class MatcherCompositionTest {
  @Test
  void classMatchers() {
    checkComposition(
        emptyClass(),
        p -> (ClassMatcher) p::test,
        ClassMatcher::and,
        ClassMatcher::or,
        StandardMatchers::not);
  }

  @Test
  void fieldMatchers() {
    checkComposition(
        fieldOutline("I"),
        p -> (FieldMatcher) p::test,
        FieldMatcher::and,
        FieldMatcher::or,
        StandardMatchers::not);
  }

  @Test
  void methodMatchers() {
    checkComposition(
        methodOutline("()V"),
        p -> (MethodMatcher) p::test,
        MethodMatcher::and,
        MethodMatcher::or,
        StandardMatchers::not);
  }

  @Test
  void typeMatchers() {
    checkComposition(
        (CharSequence) "sample/Type",
        p -> (TypeMatcher) p::test,
        TypeMatcher::and,
        TypeMatcher::or,
        StandardMatchers::not);
  }

  private static <T, M extends Predicate<T>> void checkComposition(
      T candidate,
      Function<Predicate<T>, M> factory,
      BinaryOperator<M> and,
      BinaryOperator<M> or,
      UnaryOperator<M> not) {
    for (int bits = 0; bits < 16; bits++) {
      boolean a = (bits & 1) != 0;
      boolean b = (bits & 2) != 0;
      boolean c = (bits & 4) != 0;
      boolean d = (bits & 8) != 0;
      M ma = factory.apply(ignored -> a), mb = factory.apply(ignored -> b);
      M mc = factory.apply(ignored -> c), md = factory.apply(ignored -> d);
      assertMatches(and.apply(ma, mb), candidate, a && b);
      assertMatches(or.apply(ma, mb), candidate, a || b);
      assertMatches(not.apply(ma), candidate, !a);
      // Nest either operand or both, and mix AND/OR without changing grouping.
      assertMatches(and.apply(and.apply(ma, mb), and.apply(mc, md)), candidate, a && b && c && d);
      assertMatches(and.apply(and.apply(ma, mb), mc), candidate, a && b && c);
      assertMatches(and.apply(ma, and.apply(mb, mc)), candidate, a && b && c);
      assertMatches(or.apply(or.apply(ma, mb), or.apply(mc, md)), candidate, a || b || c || d);
      assertMatches(or.apply(or.apply(ma, mb), mc), candidate, a || b || c);
      assertMatches(or.apply(ma, or.apply(mb, mc)), candidate, a || b || c);
      assertMatches(and.apply(or.apply(ma, mb), or.apply(mc, md)), candidate, (a || b) && (c || d));
      assertMatches(
          or.apply(and.apply(ma, mb), and.apply(mc, md)), candidate, (a && b) || (c && d));
    }

    for (boolean conjunction : new boolean[] {true, false}) {
      BinaryOperator<M> combine = conjunction ? and : or;
      for (int stop = 0; stop <= 4; stop++) {
        int stopIndex = stop;
        List<Integer> calls = new ArrayList<>();
        IntFunction<M> recording =
            index ->
                factory.apply(
                    actual -> {
                      assertSame(candidate, actual);
                      calls.add(index);
                      return index == stopIndex ? !conjunction : conjunction;
                    });
        M left = combine.apply(recording.apply(0), recording.apply(1));
        M right = combine.apply(recording.apply(2), recording.apply(3));
        assertEquals(
            stop == 4 ? conjunction : !conjunction, combine.apply(left, right).test(candidate));
        assertEquals(Arrays.asList(0, 1, 2, 3).subList(0, Math.min(stop + 1, 4)), calls);
      }
    }

    M yes = factory.apply(ignored -> true), no = factory.apply(ignored -> false);
    M all = and.apply(yes, yes), any = or.apply(no, no);
    assertMatches(and.apply(all, no), candidate, false);
    assertMatches(and.apply(no, all), candidate, false);
    assertMatches(or.apply(any, yes), candidate, true);
    assertMatches(or.apply(yes, any), candidate, true);
    assertMatches(all, candidate, true);
    assertMatches(any, candidate, false);
  }
}
