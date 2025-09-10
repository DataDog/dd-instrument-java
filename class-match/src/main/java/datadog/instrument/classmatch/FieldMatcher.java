package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.descriptor;

import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Fluent-API for building {@link FieldOutline} predicates. */
@FunctionalInterface
public interface FieldMatcher extends Predicate<FieldOutline> {

  static FieldMatcher field(String name) {
    return f -> name.equals(f.fieldName);
  }

  static FieldMatcher field(Predicate<String> nameMatcher) {
    return f -> nameMatcher.test(f.fieldName);
  }

  default FieldMatcher withAccess(IntPredicate accessMatcher) {
    return and(f -> accessMatcher.test(f.access));
  }

  default FieldMatcher ofType(String type) {
    String descriptor = descriptor(type);
    return and(f -> descriptor.equals(f.descriptor));
  }

  default FieldMatcher and(FieldMatcher other) {
    // simple approach as we don't expect many field-matcher unions
    return f -> test(f) && other.test(f);
  }

  default FieldMatcher or(FieldMatcher other) {
    // simple approach as we don't expect many field-matcher unions
    return f -> test(f) || other.test(f);
  }
}
