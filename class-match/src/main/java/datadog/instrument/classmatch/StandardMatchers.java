package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.InternalMatchers.internalName;
import static datadog.instrument.classmatch.InternalMatchers.internalNames;
import static java.util.Arrays.asList;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/** Standard matchers, part of the class-match API. */
public final class StandardMatchers {

  // Standard access matchers
  public static final IntPredicate PUBLIC = Modifier::isPublic;
  public static final IntPredicate PRIVATE = Modifier::isPrivate;
  public static final IntPredicate PROTECTED = Modifier::isProtected;
  public static final IntPredicate STATIC = Modifier::isStatic;
  public static final IntPredicate FINAL = Modifier::isFinal;
  public static final IntPredicate SYNCHRONIZED = Modifier::isSynchronized;
  public static final IntPredicate VOLATILE = Modifier::isVolatile;
  public static final IntPredicate TRANSIENT = Modifier::isTransient;
  public static final IntPredicate NATIVE = Modifier::isNative;
  public static final IntPredicate INTERFACE = Modifier::isInterface;
  public static final IntPredicate ABSTRACT = Modifier::isAbstract;

  private StandardMatchers() {}

  /** Syntactic sugar around {@link Predicate#negate()}. */
  public static <T> Predicate<T> not(Predicate<T> predicate) {
    return predicate.negate();
  }

  /** Matches when the target has the given name. */
  public static Predicate<String> named(String name) {
    return internalName(name)::equals;
  }

  /** Matches when the target has one of the given names. */
  public static Predicate<String> named(String... names) {
    return internalNames(asList(names))::contains;
  }

  /** Matches when the target has one of the given names. */
  public static Predicate<String> named(Collection<String> names) {
    return internalNames(names)::contains;
  }

  /** Matches when the target starts with the given prefix. */
  public static Predicate<String> nameStartsWith(String prefix) {
    String internalPrefix = internalName(prefix);
    return s -> s.startsWith(internalPrefix);
  }

  /** Matches when the target ends with the given suffix. */
  public static Predicate<String> nameEndsWith(String suffix) {
    String internalSuffix = internalName(suffix);
    return s -> s.endsWith(internalSuffix);
  }
}
