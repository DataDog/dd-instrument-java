/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/** Internally shared matchers, not part of the public API. */
final class InternalMatchers {

  private InternalMatchers() {}

  /** Matches when at least one annotation has the given type. */
  static Predicate<String[]> declaresAnnotation(String type) {
    String internalName = internalName(type);
    // note this annotation is of interest when parsing
    ClassFile.annotationOfInterest(internalName);
    // performance tip: capture this method-ref outside the lambda
    Predicate<String> annotationNamed = internalName::equals;
    return annotations -> anyMatch(annotations, annotationNamed);
  }

  /** Matches when at least one annotation has one of the given types. */
  static Predicate<String[]> declaresAnnotationOneOf(Collection<String> types) {
    InternalNames internalNames = new InternalNames(types);
    // note these annotations are of interest when parsing
    ClassFile.annotationsOfInterest(internalNames);
    return new AnnotationNames(internalNames);
  }

  /**
   * Does the method's descriptor contain the parameter descriptor at the given index?
   *
   * <p>Only for indices of 1 or above; index 0 uses a separate (faster) approach.
   */
  static boolean hasParamDescriptor(MethodOutline method, int paramIndex, String paramDescriptor) {
    // boundaries covers start of second parameter, to start of return descriptor
    int[] boundaries = method.descriptorBoundaries();
    return paramIndex < boundaries.length // ignore return descriptor boundary
        && method.descriptor.startsWith(paramDescriptor, boundaries[paramIndex - 1]);
  }

  /** Does the method's descriptor end with the exact return descriptor? */
  static boolean hasReturnDescriptor(String methodDescriptor, String returnDescriptor) {
    int returnStart = methodDescriptor.length() - returnDescriptor.length();
    return returnStart > 0
        && methodDescriptor.charAt(returnStart - 1) == ')'
        && methodDescriptor.endsWith(returnDescriptor);
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

  /** Returns the internal form of the given type. */
  static String internalName(String type) {
    return type.replace('.', '/');
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

  /** Matches all methods, excluding {@code <init>} and {@code <clinit>}. */
  static final MethodMatcher ALL_METHODS =
      new MethodMatcher() {
        @Override
        public boolean test(MethodOutline outline) {
          return outline.methodName.charAt(0) != '<';
        }

        @Override
        public MethodMatcher and(MethodMatcher other) {
          return new RegularMethod(other); // make it easier to inline name check
        }
      };

  /** Matches regular methods that also match the given matcher. */
  static final class RegularMethod implements MethodMatcher {
    private final MethodMatcher matcher;

    RegularMethod(MethodMatcher matcher) {
      this.matcher = matcher;
    }

    @Override
    public boolean test(MethodOutline outline) {
      return outline.methodName.charAt(0) != '<' && matcher.test(outline);
    }

    @Override
    public MethodMatcher and(MethodMatcher other) {
      return new RegularMethod(matcher.and(other));
    }
  }

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

  /** Logical AND of two {@link TypeMatcher}s; nested conjunctions will be collapsed. */
  static final class TypeConjunction extends MatcherUnion<TypeMatcher> implements TypeMatcher {
    TypeConjunction(TypeMatcher lhs, TypeMatcher rhs) {
      super(new TypeMatcher[] {lhs, rhs});
    }

    @Override
    public boolean test(CharSequence typeString) {
      for (TypeMatcher matcher : matchers) {
        if (!matcher.test(typeString)) {
          return false;
        }
      }
      return true;
    }
  }

  /** Logical OR of two {@link TypeMatcher}s; nested disjunctions will be collapsed. */
  static final class TypeDisjunction extends MatcherUnion<TypeMatcher> implements TypeMatcher {
    TypeDisjunction(TypeMatcher lhs, TypeMatcher rhs) {
      super(new TypeMatcher[] {lhs, rhs});
    }

    @Override
    public boolean test(CharSequence typeString) {
      for (TypeMatcher matcher : matchers) {
        if (matcher.test(typeString)) {
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

      // expand array if either side is a nested union of the same type
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

  /** Matches when at least one annotation has one of the given internal names. */
  static final class AnnotationNames implements Predicate<String[]> {
    private final InternalNames names;

    AnnotationNames(InternalNames names) {
      this.names = names;
    }

    @Override
    public boolean test(String[] annotations) {
      for (String annotation : annotations) {
        if (names.containsType(annotation)) {
          return true;
        }
      }
      return false;
    }
  }
}
