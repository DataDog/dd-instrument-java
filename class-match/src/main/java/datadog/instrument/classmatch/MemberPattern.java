/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Compiled form of a member pattern written as a JVM descriptor with simple wildcards.
 *
 * <p>Method pattern grammar: {@code [modifiers] names [ '(' params ')' [return] ]}
 *
 * <ul>
 *   <li>{@code names} is {@code name ('|' name)*}, or {@code *} for any regular method (excludes
 *       {@code <init>} and {@code <clinit>}).
 *   <li>{@code params} is a sequence of JVM type descriptors and {@code ?} (exactly one parameter
 *       of any type), optionally ending with {@code *} (zero or more remaining parameters). {@code
 *       *} is only allowed last, so matching is a single linear pass with no backtracking.
 *   <li>{@code return} is a JVM type descriptor; omitted or {@code *} matches any return type.
 *   <li>Omitting the parameter list entirely matches any descriptor.
 * </ul>
 *
 * <p>Names may also be a {@code prefix*} or a {@code *suffix}. An {@code @internal/Name} token
 * before the names requires a method annotation (registered as of interest at construction).
 *
 * <p>Field pattern grammar: {@code [modifiers] names ':' (descriptor | '*')}
 *
 * <p>Modifiers: {@code public private protected package static final synchronized abstract volatile
 * transient bridge varargs synthetic}, each optionally negated with {@code !}.
 *
 * <p>Examples: {@code run(Lorg/junit/runner/notification/RunNotifier;*)}, {@code public
 * inject(??)}, {@code <init>(??Lcom/amazonaws/services/sqs/model/Message;*)}, {@code !static
 * apply(Ljava/lang/String;)Lscala/Option;}, {@code value:I}.
 *
 * <p>Most patterns reduce to a single {@link String} operation on the member descriptor; only
 * patterns containing {@code ?} or a trailing {@code *} need the general token walk.
 */
final class MemberPattern {
  private static final int ANY = 0; // no descriptor constraint
  private static final int EXACT = 1; // descriptor equals literal
  private static final int PARAMS = 2; // descriptor starts with literal "(...)"
  private static final int RETURN = 3; // descriptor ends with literal return after ')'
  private static final int GENERAL = 4; // token walk

  private static final int PACKAGE_PRIVATE = -1; // pseudo-modifier

  final boolean field;

  /** Member names, or {@code null} for the {@code *} wildcard (regular methods only). */
  final String[] names;

  /** Internal name of a required method annotation, or {@code null}. */
  @Nullable final String annotation;

  private final int requiredAccess;
  private final int forbiddenAccess;
  private final int kind;
  private final String literal; // EXACT/PARAMS/RETURN literal, or GENERAL pattern "(...)ret"

  private MemberPattern(
      boolean field,
      String[] names,
      @Nullable String annotation,
      int requiredAccess,
      int forbiddenAccess,
      int kind,
      String literal) {
    this.field = field;
    this.names = names;
    this.annotation = annotation;
    this.requiredAccess = requiredAccess;
    this.forbiddenAccess = forbiddenAccess;
    this.kind = kind;
    this.literal = literal;
  }

  /** Field patterns always contain {@code ':'}; cheap check usable before compiling. */
  static boolean isFieldPattern(String pattern) {
    return pattern.indexOf('(') < 0 && pattern.indexOf(':') >= 0;
  }

  /** Extracts the member names without compiling; returns {@code null} for the wildcard. */
  static String[] names(String pattern) {
    int end = pattern.length();
    int paren = pattern.indexOf('(');
    if (paren >= 0) {
      end = paren;
    } else {
      int colon = pattern.indexOf(':');
      if (colon >= 0) {
        end = colon;
      }
    }
    int start = pattern.lastIndexOf(' ', end - 1) + 1;
    String names = pattern.substring(start, end);
    return "*".equals(names) ? null : split(names, '|');
  }

  static MemberPattern compile(String pattern) {
    int requiredAccess = 0;
    int forbiddenAccess = 0;
    String annotation = null;
    int pos = 0;
    int paren = pattern.indexOf('(');
    int colon = paren < 0 ? pattern.indexOf(':') : -1;
    int headerEnd = paren >= 0 ? paren : colon >= 0 ? colon : pattern.length();
    // modifiers are space-separated words before the names
    int lastSpace = pattern.lastIndexOf(' ', headerEnd - 1);
    if (lastSpace >= 0) {
      for (String word : split(pattern.substring(0, lastSpace), ' ')) {
        if (word.isEmpty()) {
          continue;
        }
        if (word.charAt(0) == '@') {
          if (annotation != null || word.length() == 1) {
            throw new IllegalArgumentException("Expected one annotation: " + pattern);
          }
          annotation = word.substring(1);
          continue;
        }
        boolean negated = word.charAt(0) == '!';
        int mod = modifier(negated ? word.substring(1) : word);
        if (mod == PACKAGE_PRIVATE) {
          if (negated) {
            throw new IllegalArgumentException("Cannot negate 'package': " + pattern);
          }
          forbiddenAccess |= Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
        } else if (negated) {
          forbiddenAccess |= mod;
        } else {
          requiredAccess |= mod;
        }
      }
      pos = lastSpace + 1;
    }
    String nameSpec = pattern.substring(pos, headerEnd);
    String[] names = "*".equals(nameSpec) ? null : split(nameSpec, '|');
    if (paren < 0) {
      if (colon >= 0) {
        String descriptor = pattern.substring(colon + 1);
        if ("*".equals(descriptor)) {
          return new MemberPattern(
              true, names, annotation, requiredAccess, forbiddenAccess, ANY, null);
        }
        if (checkType(descriptor, 0, pattern) != descriptor.length()) {
          throw new IllegalArgumentException("Bad field type: " + pattern);
        }
        return new MemberPattern(
            true, names, annotation, requiredAccess, forbiddenAccess, EXACT, descriptor);
      }
      return new MemberPattern(
          false, names, annotation, requiredAccess, forbiddenAccess, ANY, null);
    }
    String descriptor = pattern.substring(paren);
    int kind = classify(descriptor, pattern);
    if (kind == PARAMS && descriptor.endsWith("*")) {
      descriptor = descriptor.substring(0, descriptor.length() - 1); // "(..)*" -> prefix "(..)"
    }
    return new MemberPattern(
        false, names, annotation, requiredAccess, forbiddenAccess, kind, descriptor);
  }

  /** Validates the descriptor part and picks the cheapest matching strategy. */
  private static int classify(String desc, String pattern) {
    boolean wildParams = false;
    boolean trailingStar = false;
    int i = 1;
    while (i < desc.length() && desc.charAt(i) != ')') {
      char c = desc.charAt(i);
      if (c == '?') {
        wildParams = true;
        i++;
      } else if (c == '*') {
        trailingStar = true;
        if (i + 1 >= desc.length() || desc.charAt(i + 1) != ')') {
          throw new IllegalArgumentException("'*' must be the last parameter: " + pattern);
        }
        i++;
      } else {
        i = checkType(desc, i, pattern);
      }
    }
    if (i >= desc.length()) {
      throw new IllegalArgumentException("Missing ')': " + pattern);
    }
    String ret = desc.substring(i + 1);
    boolean anyReturn = ret.isEmpty() || "*".equals(ret);
    if (!anyReturn && checkType(ret, 0, pattern) != ret.length()) {
      throw new IllegalArgumentException("Bad return type: " + pattern);
    }
    if (!wildParams && !trailingStar) {
      return anyReturn ? PARAMS : EXACT;
    }
    if (!wildParams && i == 2 && trailingStar && !anyReturn) {
      return RETURN; // "(*)R"
    }
    if (!wildParams && trailingStar && anyReturn && i == 2) {
      return ANY; // "(*)" or "(*)*"
    }
    return GENERAL;
  }

  /** Returns the index after the type descriptor starting at {@code i}, or throws. */
  private static int checkType(String s, int i, String pattern) {
    while (i < s.length() && s.charAt(i) == '[') {
      i++;
    }
    if (i >= s.length()) {
      throw new IllegalArgumentException("Truncated type: " + pattern);
    }
    switch (s.charAt(i)) {
      case 'Z':
      case 'B':
      case 'C':
      case 'S':
      case 'I':
      case 'J':
      case 'F':
      case 'D':
      case 'V':
        return i + 1;
      case 'L':
        int semi = s.indexOf(';', i);
        if (semi < 0) {
          throw new IllegalArgumentException("Unterminated type: " + pattern);
        }
        return semi + 1;
      default:
        throw new IllegalArgumentException("Bad type at " + i + ": " + pattern);
    }
  }

  private static int modifier(String word) {
    switch (word) {
      case "public":
        return Modifier.PUBLIC;
      case "private":
        return Modifier.PRIVATE;
      case "protected":
        return Modifier.PROTECTED;
      case "package":
        return PACKAGE_PRIVATE;
      case "static":
        return Modifier.STATIC;
      case "final":
        return Modifier.FINAL;
      case "synchronized":
        return Modifier.SYNCHRONIZED;
      case "abstract":
        return Modifier.ABSTRACT;
      case "volatile":
        return Modifier.VOLATILE;
      case "transient":
        return Modifier.TRANSIENT;
      case "bridge": // same bit as volatile, named for methods
        return 0x0040;
      case "varargs": // same bit as transient, named for methods
        return 0x0080;
      case "synthetic":
        return 0x1000;
      default:
        throw new IllegalArgumentException("Unknown modifier: " + word);
    }
  }

  private static String[] split(String s, char sep) {
    List<String> parts = new ArrayList<>();
    int start = 0;
    for (int i = s.indexOf(sep); i >= 0; i = s.indexOf(sep, start)) {
      parts.add(s.substring(start, i));
      start = i + 1;
    }
    parts.add(s.substring(start));
    return parts.toArray(new String[0]);
  }

  /**
   * Extracts the required annotation without compiling, or {@code null}. The annotation token
   * ({@code @internal/Name}) comes before the member names.
   */
  @Nullable
  static String annotation(String pattern) {
    int at = pattern.indexOf('@');
    if (at < 0) {
      return null;
    }
    int end = pattern.indexOf(' ', at);
    return end < 0 ? null : pattern.substring(at + 1, end);
  }

  /**
   * Does the name match one of this pattern's names? Names may be exact, a {@code prefix*}, or a
   * {@code *suffix}; the {@code *} wildcard (no names) matches any regular method.
   */
  boolean nameMatches(String name) {
    if (names == null) {
      return name.charAt(0) != '<';
    }
    for (String n : names) {
      int last = n.length() - 1;
      if (n.charAt(last) == '*') {
        if (name.regionMatches(0, n, 0, last)) {
          return true;
        }
      } else if (n.charAt(0) == '*') {
        if (name.regionMatches(name.length() - last, n, 1, last)) {
          return true;
        }
      } else if (n.equals(name)) {
        return true;
      }
    }
    return false;
  }

  /** Matches access, descriptor, and annotation; the caller has already matched the name. */
  boolean matches(MethodOutline method) {
    if (!matches(method.access, method.descriptor)) {
      return false;
    }
    if (annotation != null) {
      for (String a : method.annotations) {
        if (annotation.equals(a)) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  /** Matches access and descriptor; the caller has already matched the name. */
  boolean matches(int access, String descriptor) {
    if ((access & requiredAccess) != requiredAccess || (access & forbiddenAccess) != 0) {
      return false;
    }
    switch (kind) {
      case ANY:
        return true;
      case EXACT:
        return literal.equals(descriptor);
      case PARAMS:
        return descriptor.startsWith(literal);
      case RETURN:
        return endsWithReturn(descriptor, literal, 3); // literal is "(*)R"
      default:
        return walk(literal, descriptor);
    }
  }

  private static boolean endsWithReturn(String descriptor, String literal, int retStart) {
    int retLength = literal.length() - retStart;
    int start = descriptor.length() - retLength;
    return start > 0
        && descriptor.charAt(start - 1) == ')'
        && descriptor.regionMatches(start, literal, retStart, retLength);
  }

  /** General token walk; {@code ?} matches one parameter, trailing {@code *} the rest. */
  static boolean walk(String pat, String desc) {
    int p = 1;
    int d = 1; // both start just after '('
    for (; ; ) {
      char pc = pat.charAt(p);
      if (pc == '*') { // trailing wildcard: consume the remaining parameters
        while (desc.charAt(d) != ')') {
          d = skipType(desc, d);
        }
        p++; // validated at compile time: '*' is always followed by ')'
      }
      if (pat.charAt(p) == ')') {
        if (desc.charAt(d) != ')') {
          return false; // descriptor has extra parameters
        }
        break;
      }
      if (desc.charAt(d) == ')') {
        return false; // pattern wants more parameters
      }
      int dEnd = skipType(desc, d);
      if (pc == '?') {
        p++;
      } else {
        int pEnd = skipType(pat, p);
        int len = pEnd - p;
        if (len != dEnd - d || !desc.regionMatches(d, pat, p, len)) {
          return false;
        }
        p = pEnd;
      }
      d = dEnd;
    }
    p++; // step past ')'
    d++;
    int rest = pat.length() - p;
    return rest == 0
        || pat.charAt(p) == '*'
        || (rest == desc.length() - d && desc.regionMatches(d, pat, p, rest));
  }

  private static int skipType(String s, int i) {
    while (s.charAt(i) == '[') {
      i++;
    }
    return s.charAt(i) == 'L' ? s.indexOf(';', i) + 1 : i + 1;
  }
}
