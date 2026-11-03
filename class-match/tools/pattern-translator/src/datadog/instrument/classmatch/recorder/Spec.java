package datadog.instrument.classmatch.recorder;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Symbolic conjunction of constraints on a single member, recorded from the fluent API. Merging two
 * specs intersects their constraints; a contradiction marks the spec unsatisfiable.
 */
final class Spec {
  static final boolean STRICT = Boolean.getBoolean("strict");

  boolean field;
  Set<String> names; // null = any name
  boolean regularOnly; // method(): excludes <init>/<clinit>
  int required;
  int forbidden;
  int paramCount = -1; // -1 = unknown
  final Map<Integer, String> params = new TreeMap<>();
  String returnType; // null = any
  String fieldType; // null = any
  String annotation; // internal name, null = none
  boolean unsat;
  boolean inexpressible; // valid constraint, but not as a single pattern

  Spec copy() {
    Spec s = new Spec();
    s.field = field;
    s.names = names == null ? null : new LinkedHashSet<>(names);
    s.regularOnly = regularOnly;
    s.required = required;
    s.forbidden = forbidden;
    s.paramCount = paramCount;
    s.params.putAll(params);
    s.returnType = returnType;
    s.fieldType = fieldType;
    s.annotation = annotation;
    s.unsat = unsat;
    s.inexpressible = inexpressible;
    return s;
  }

  /** Conjunction of two specs on the same member. */
  Spec merge(Spec o) {
    Spec s = copy();
    if (field != o.field) {
      throw new IllegalStateException("mixing field and method constraints");
    }
    if (o.names != null) {
      if (s.names == null) {
        s.names = new LinkedHashSet<>(o.names);
      } else if (hasWildcardName(s.names) || hasWildcardName(o.names)) {
        // prefix/suffix intersections aren't a single name token
        if (!s.names.equals(o.names)) s.inexpressible = true;
      } else {
        s.names.retainAll(o.names);
      }
    }
    if (o.annotation != null) {
      if (s.annotation != null && !s.annotation.equals(o.annotation)) {
        s.inexpressible = true; // patterns carry one annotation
      }
      s.annotation = o.annotation;
    }
    s.inexpressible |= o.inexpressible;
    s.regularOnly |= o.regularOnly;
    s.required |= o.required;
    s.forbidden |= o.forbidden;
    if (o.paramCount >= 0) {
      if (s.paramCount >= 0 && s.paramCount != o.paramCount) {
        s.unsat = true;
      }
      s.paramCount = o.paramCount;
    }
    for (Map.Entry<Integer, String> e : o.params.entrySet()) {
      String prev = s.params.put(e.getKey(), e.getValue());
      if (prev != null && !prev.equals(e.getValue())) {
        s.unsat = true;
      }
    }
    s.returnType = mergeType(s, s.returnType, o.returnType);
    s.fieldType = mergeType(s, s.fieldType, o.fieldType);
    s.unsat |= o.unsat;
    s.normalize();
    return s;
  }

  static boolean hasWildcardName(Set<String> names) {
    for (String n : names) {
      if (n.startsWith("*") || n.endsWith("*")) return true;
    }
    return false;
  }

  private static String mergeType(Spec s, String a, String b) {
    if (a == null) return b;
    if (b == null) return a;
    if (!a.equals(b)) s.unsat = true;
    return a;
  }

  void normalize() {
    if ((required & forbidden) != 0) unsat = true;
    if (names != null) {
      if (regularOnly) {
        names.removeIf(n -> n.startsWith("<"));
      }
      if (names.isEmpty()) unsat = true;
    }
    if (paramCount >= 0) {
      for (int i : params.keySet()) {
        if (i >= paramCount) unsat = true;
      }
    }
  }

  /** Renders this spec as one or more member patterns (empty when unsatisfiable). */
  List<String> toPatterns() {
    if (unsat) return Collections.emptyList();
    if (inexpressible) return null;
    String mods = (annotation == null ? "" : "@" + annotation + " ") + modifiers();
    List<String> out = new ArrayList<>();
    if (field) {
      if (names == null) {
        return null; // wildcard field names unsupported: caller falls back
      }
      out.add(mods + String.join("|", names) + ":" + (fieldType == null ? "*" : fieldType));
      return out;
    }
    // STRICT: complete descriptors only ('?' = one type); open-ended arity goes to the slow path
    if (STRICT && paramCount < 0 && (!params.isEmpty() || returnType != null)) {
      return null;
    }
    String desc = descriptor();
    if (names != null) {
      out.add(mods + String.join("|", names) + desc);
    } else {
      out.add(mods + "*" + desc);
      if (!regularOnly) { // anyMethod() also matches constructors and static initializers
        out.add(mods + "<init>|<clinit>" + desc);
      }
    }
    return out;
  }

  private String descriptor() {
    if (paramCount < 0 && params.isEmpty()) {
      return returnType == null ? "" : "(*)" + returnType;
    }
    StringBuilder b = new StringBuilder("(");
    int n = paramCount >= 0 ? paramCount : ((TreeMap<Integer, String>) params).lastKey() + 1;
    for (int i = 0; i < n; i++) {
      String p = params.get(i);
      b.append(p == null ? "?" : p);
    }
    if (paramCount < 0) b.append('*');
    b.append(')');
    b.append(returnType != null ? returnType : STRICT ? "?" : "");
    return b.toString();
  }

  private String modifiers() {
    StringBuilder b = new StringBuilder();
    int[] mods = {
      Modifier.PUBLIC, Modifier.PRIVATE, Modifier.PROTECTED, Modifier.STATIC, Modifier.FINAL,
      Modifier.SYNCHRONIZED, Modifier.ABSTRACT, Modifier.VOLATILE, Modifier.TRANSIENT
    };
    int handled = 0;
    for (int m : mods) {
      handled |= m;
      String word = Modifier.toString(m);
      if (!field && m == Modifier.VOLATILE) word = "bridge";
      if (!field && m == Modifier.TRANSIENT) word = "varargs";
      if ((required & m) != 0) b.append(word).append(' ');
      if ((forbidden & m) != 0) b.append('!').append(word).append(' ');
    }
    if (((required | forbidden) & ~handled) != 0) {
      throw new IllegalStateException("unhandled modifier bits: " + required + "/" + forbidden);
    }
    return b.toString();
  }

  static String descriptorOf(String type) {
    if (type.endsWith("[]")) {
      return '[' + descriptorOf(type.substring(0, type.length() - 2));
    }
    switch (type) {
      case "boolean": return "Z";
      case "byte": return "B";
      case "char": return "C";
      case "double": return "D";
      case "float": return "F";
      case "int": return "I";
      case "long": return "J";
      case "short": return "S";
      case "void": return "V";
      default: return 'L' + type.replace('.', '/') + ';';
    }
  }
}
