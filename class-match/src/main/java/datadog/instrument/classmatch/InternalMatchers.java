package datadog.instrument.classmatch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Internally shared matchers, not part of the public API. */
final class InternalMatchers {

  // default matchers that are always true
  static final IntPredicate ANY_ACCESS = access -> true;
  static final Predicate<String> ANY_NAME = name -> true;
  static final Predicate<String> ANY_DESCRIPTOR = descriptor -> true;
  static final Predicate<String[]> ANY_ANNOTATIONS = annotations -> true;

  private InternalMatchers() {}

  /** Matches when at least one annotation has the given name. */
  static Predicate<String[]> annotationNamed(String name) {
    String internalName = internalName(name);
    ClassFile.annotationOfInterest(internalName);
    return as -> anyMatch(as, internalName::equals);
  }

  /** Matches when at least one annotation has one of the given names. */
  static Predicate<String[]> annotationNamed(Collection<String> names) {
    Set<String> internalNames = internalNames(names);
    ClassFile.annotationsOfInterest(internalNames);
    return as -> anyMatch(as, internalNames::contains);
  }

  /** Returns the descriptor for the given type. */
  static String descriptor(String type) {
    return 'L' + internalName(type) + ';';
  }

  /** Returns the internal form of the given name. */
  static String internalName(String name) {
    return name.replace('.', '/');
  }

  /** Returns the internal forms of the given names. */
  static Set<String> internalNames(Collection<String> names) {
    Set<String> internalNames = new HashSet<>((int) (names.size() / 0.75f) + 1);
    for (String name : names) {
      internalNames.add(internalName(name));
    }
    return internalNames;
  }

  /** Returns {@code true} if at least one of the candidates matches. */
  static <T> boolean anyMatch(T[] candidates, Predicate<T> matcher) {
    for (T c : candidates) {
      if (matcher.test(c)) {
        return true;
      }
    }
    return false;
  }
}
