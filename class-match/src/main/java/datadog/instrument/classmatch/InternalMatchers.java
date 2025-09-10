package datadog.instrument.classmatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/** Internally shared matchers, not part of the public API. */
final class InternalMatchers {

  private InternalMatchers() {}

  /** Matches when at least one annotation has the given name. */
  static Predicate<String[]> declaresAnnotation(String name) {
    String annotationName = internalName(name);
    ClassFile.annotationOfInterest(annotationName);
    // performance tip: capture this method-ref outside the lambda
    Predicate<String> annotationNamed = annotationName::equals;
    return annotations -> anyMatch(annotations, annotationNamed);
  }

  /** Matches when at least one annotation has one of the given names. */
  static Predicate<String[]> declaresAnnotationOneOf(Collection<String> names) {
    Set<String> annotationNames = internalNames(names);
    ClassFile.annotationsOfInterest(annotationNames);
    // performance tip: capture this method-ref outside the lambda
    Predicate<String> annotationNamedOneOf = annotationNames::contains;
    return annotations -> anyMatch(annotations, annotationNamedOneOf);
  }

  /** Returns {@code true} if the parameter declared at the given index has the given descriptor. */
  static boolean declaresParameter(MethodOutline method, int paramIndex, String paramDescriptor) {
    int[] paramOffsets = method.parameterOffsets();
    return paramIndex < paramOffsets.length
        && method.descriptor.regionMatches(
            paramOffsets[paramIndex], paramDescriptor, 0, paramDescriptor.length());
  }

  /** Returns the descriptor for the given type. */
  static String descriptor(String type) {
    if (type.endsWith("[]")) {
      return '[' + descriptor(type.substring(0, type.length() - 2));
    }
    switch (type) {
      case "boolean":
        return "Z";
      case "byte":
        return "B";
      case "char":
        return "C";
      case "double":
        return "D";
      case "float":
        return "F";
      case "int":
        return "I";
      case "long":
        return "J";
      case "short":
        return "S";
      case "void":
        return "V";
      default:
        return 'L' + internalName(type) + ';';
    }
  }

  private static final Map<Class<?>, String> PRIMITIVE_DESCRIPTORS = new HashMap<>();

  static {
    PRIMITIVE_DESCRIPTORS.put(boolean.class, "Z");
    PRIMITIVE_DESCRIPTORS.put(byte.class, "B");
    PRIMITIVE_DESCRIPTORS.put(char.class, "C");
    PRIMITIVE_DESCRIPTORS.put(double.class, "D");
    PRIMITIVE_DESCRIPTORS.put(float.class, "F");
    PRIMITIVE_DESCRIPTORS.put(int.class, "I");
    PRIMITIVE_DESCRIPTORS.put(long.class, "J");
    PRIMITIVE_DESCRIPTORS.put(short.class, "S");
    PRIMITIVE_DESCRIPTORS.put(void.class, "V");
  }

  /** Returns the descriptor for the given type. */
  static String descriptor(Class<?> type) {
    if (type.isPrimitive()) {
      return PRIMITIVE_DESCRIPTORS.get(type);
    } else if (type.isArray()) {
      return internalName(type.getName());
    } else {
      return 'L' + internalName(type.getName()) + ';';
    }
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

  /** Optimized for {@link MethodMatcher#method()} to avoid unnecessary object creation. */
  static final MethodMatcher ALL_METHODS =
      new MethodMatcher() {
        @Override
        public boolean test(MethodOutline outline) {
          return true;
        }

        @Override
        public MethodMatcher and(MethodMatcher other) {
          return other; // no need for MethodConjunction
        }

        @Override
        public MethodMatcher or(MethodMatcher other) {
          return this; // no need for MethodDisjunction
        }
      };

  /** Logical AND of two {@link ClassMatcher}s; nested conjunctions will be collapsed. */
  static final class ClassConjunction extends MatcherUnion<ClassMatcher> implements ClassMatcher {
    ClassConjunction(ClassMatcher lhs, ClassMatcher rhs) {
      super(new ClassMatcher[] {lhs, rhs});
    }

    @Override
    public boolean test(ClassOutline outline) {
      for (ClassMatcher matcher : matchers) {
        if (!matcher.test(outline)) {
          return false;
        }
      }
      return true;
    }
  }

  /** Logical OR of two {@link ClassMatcher}s; nested disjunctions will be collapsed. */
  static final class ClassDisjunction extends MatcherUnion<ClassMatcher> implements ClassMatcher {
    ClassDisjunction(ClassMatcher lhs, ClassMatcher rhs) {
      super(new ClassMatcher[] {lhs, rhs});
    }

    @Override
    public boolean test(ClassOutline outline) {
      for (ClassMatcher matcher : matchers) {
        if (matcher.test(outline)) {
          return true;
        }
      }
      return false;
    }
  }

  /** Logical AND of two {@link MethodMatcher}s; nested conjunctions will be collapsed. */
  static final class MethodConjunction extends MatcherUnion<MethodMatcher>
      implements MethodMatcher {
    MethodConjunction(MethodMatcher lhs, MethodMatcher rhs) {
      super(new MethodMatcher[] {lhs, rhs});
    }

    @Override
    public boolean test(MethodOutline outline) {
      for (MethodMatcher matcher : matchers) {
        if (!matcher.test(outline)) {
          return false;
        }
      }
      return true;
    }
  }

  /** Logical OR of two {@link MethodMatcher}s; nested disjunctions will be collapsed. */
  static final class MethodDisjunction extends MatcherUnion<MethodMatcher>
      implements MethodMatcher {
    MethodDisjunction(MethodMatcher lhs, MethodMatcher rhs) {
      super(new MethodMatcher[] {lhs, rhs});
    }

    @Override
    public boolean test(MethodOutline outline) {
      for (MethodMatcher matcher : matchers) {
        if (matcher.test(outline)) {
          return true;
        }
      }
      return false;
    }
  }

  /** Logical union of two matchers; nested unions of the same type will be collapsed. */
  abstract static class MatcherUnion<M> {
    protected final M[] matchers;

    /**
     * @param matchers array containing the two matchers to combine
     */
    @SuppressWarnings("unchecked")
    MatcherUnion(M[] matchers) {
      M lhs = matchers[0];
      M rhs = matchers[1];

      Class<?> unionType = getClass();

      M[] lhsArray = null;
      M[] rhsArray = null;

      int lhsLength =
          unionType.isInstance(lhs) ? (lhsArray = ((MatcherUnion<M>) lhs).matchers).length : 1;
      int rhsLength =
          unionType.isInstance(rhs) ? (rhsArray = ((MatcherUnion<M>) rhs).matchers).length : 1;

      // only expand array when either side is a nested union of the same type
      int expandedLength = lhsLength + rhsLength;
      if (expandedLength > 2) {
        // original array should only have 2 elements, tolerate if there's extra
        int extra = matchers.length - 2;

        matchers = Arrays.copyOf(matchers, expandedLength + extra);

        if (extra > 0) {
          // move the extra elements (unchanged) to the end of the expanded array
          System.arraycopy(matchers, 2, matchers, expandedLength, extra);
        }

        if (lhsLength > 1) {
          System.arraycopy(lhsArray, 0, matchers, 0, lhsLength);
        }
        // if lhs is not nested then we can leave it unchanged at index 0

        if (rhsLength > 1) {
          System.arraycopy(rhsArray, 0, matchers, lhsLength, rhsLength);
        } else {
          matchers[lhsLength] = rhs;
        }
      }

      this.matchers = matchers;
    }
  }
}
