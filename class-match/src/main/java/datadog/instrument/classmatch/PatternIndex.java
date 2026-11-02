/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache License Version 2.0.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

/**
 * Matches many selectors against a class at once, using lazily-compiled {@link MemberPattern}s.
 *
 * <p>Each selector is either a set of alternative member patterns (the selector matches when the
 * class declares a member matching any of them) or a fallback {@link ClassMatcher} for the few
 * shapes patterns can't express (parameter type prefixes, negated names, class-level conjunctions).
 *
 * <p>Patterns are routed into tiers when the index is first used:
 *
 * <ol>
 *   <li>exact member name: hash lookup on the member name
 *   <li>name {@code prefix*} / {@code *suffix}: checked against a short list
 *   <li>{@code @Annotation} with a wildcard name: hash lookup on each method annotation of interest
 *   <li>{@code *} wildcard name: checked on every regular method
 * </ol>
 *
 * <p>Construction stores references and registers annotations of interest (they must be known
 * before classes are parsed). The first match builds the tier index from the raw pattern strings;
 * each pattern compiles the first time a candidate member reaches it.
 *
 * <p>Prototype: not thread-safe.
 */
final class PatternIndex {
  private static final int[] NONE = new int[0];

  private final String[][] selectorPatterns;
  private final ClassMatcher[] fallbacks;

  // built lazily on first match
  private boolean indexed;
  private Map<String, int[]> methodsByName;
  private Map<String, int[]> fieldsByName;
  private Map<String, int[]> methodsByAnnotation;
  private String[] prefixes = new String[0];
  private int[] prefixIds = NONE;
  private String[] suffixes = new String[0];
  private int[] suffixIds = NONE;
  private int[] wildcardMethods = NONE;
  private String[] patterns;
  private int[] patternSelector;
  private MemberPattern[] compiled;
  private int[] fallbackSelectors = NONE;

  // per-class dedup without clearing: selector matched when seen[selector] == epoch
  private int[] seen;
  private int epoch;

  /**
   * @param selectorPatterns per selector, alternative member patterns; {@code null} entries use the
   *     corresponding fallback
   * @param fallbacks per selector, the fallback matcher used when there are no patterns
   * @param annotations internal names of annotations used by the patterns
   */
  PatternIndex(
      String[][] selectorPatterns, @Nullable ClassMatcher[] fallbacks, String... annotations) {
    this.selectorPatterns = selectorPatterns;
    this.fallbacks = fallbacks;
    if (annotations.length > 0) {
      ClassFile.annotationsOfInterest(asList(annotations));
    }
  }

  int selectorCount() {
    return selectorPatterns.length;
  }

  private void index() {
    List<String> patternList = new ArrayList<>();
    List<Integer> selectorList = new ArrayList<>();
    List<Integer> fallbackList = new ArrayList<>();
    for (int s = 0; s < selectorPatterns.length; s++) {
      String[] alternatives = selectorPatterns[s];
      if (alternatives == null) {
        fallbackList.add(s);
        continue;
      }
      for (String p : alternatives) {
        patternList.add(p);
        selectorList.add(s);
      }
    }
    int n = patternList.size();
    patterns = patternList.toArray(new String[0]);
    patternSelector = new int[n];
    compiled = new MemberPattern[n];
    Map<String, List<Integer>> methods = new HashMap<>();
    Map<String, List<Integer>> fields = new HashMap<>();
    Map<String, List<Integer>> annotated = new HashMap<>();
    List<String> prefixList = new ArrayList<>();
    List<Integer> prefixIdList = new ArrayList<>();
    List<String> suffixList = new ArrayList<>();
    List<Integer> suffixIdList = new ArrayList<>();
    List<Integer> wildcards = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      patternSelector[i] = selectorList.get(i);
      String p = patterns[i];
      boolean field = MemberPattern.isFieldPattern(p);
      String[] names = MemberPattern.names(p);
      if (names == null) {
        if (field) {
          throw new IllegalArgumentException("Wildcard field names are not supported: " + p);
        }
        String annotation = MemberPattern.annotation(p);
        if (annotation != null) {
          add(annotated, annotation, i);
        } else {
          wildcards.add(i);
        }
        continue;
      }
      for (String name : names) {
        int last = name.length() - 1;
        if (name.charAt(last) == '*') {
          prefixList.add(name.substring(0, last));
          prefixIdList.add(i);
        } else if (name.charAt(0) == '*') {
          suffixList.add(name.substring(1));
          suffixIdList.add(i);
        } else {
          add(field ? fields : methods, name, i);
        }
      }
    }
    methodsByName = freeze(methods);
    fieldsByName = freeze(fields);
    methodsByAnnotation = freeze(annotated);
    prefixes = prefixList.toArray(new String[0]);
    prefixIds = toArray(prefixIdList);
    suffixes = suffixList.toArray(new String[0]);
    suffixIds = toArray(suffixIdList);
    wildcardMethods = toArray(wildcards);
    fallbackSelectors = toArray(fallbackList);
    seen = new int[selectorPatterns.length];
    indexed = true;
  }

  private static void add(Map<String, List<Integer>> map, String key, int id) {
    // explicit get/put rather than computeIfAbsent: avoids lambda linkage on cold start
    List<Integer> ids = map.get(key);
    if (ids == null) {
      map.put(key, ids = new ArrayList<>());
    }
    ids.add(id);
  }

  private static Map<String, int[]> freeze(Map<String, List<Integer>> map) {
    Map<String, int[]> frozen = new HashMap<>(map.size() * 2);
    for (Map.Entry<String, List<Integer>> e : map.entrySet()) {
      frozen.put(e.getKey(), toArray(e.getValue()));
    }
    return frozen;
  }

  private static int[] toArray(List<Integer> list) {
    int[] array = new int[list.size()];
    for (int i = 0; i < array.length; i++) {
      array[i] = list.get(i);
    }
    return array;
  }

  private MemberPattern pattern(int id) {
    MemberPattern p = compiled[id];
    if (p == null) {
      compiled[id] = p = MemberPattern.compile(patterns[id]);
    }
    return p;
  }

  /** Number of patterns compiled so far; shows how lazy compilation is in practice. */
  int compiledCount() {
    int count = 0;
    if (compiled != null) {
      for (MemberPattern p : compiled) {
        count += p != null ? 1 : 0;
      }
    }
    return count;
  }

  /** Counts the selectors that match the class. */
  int countMatches(ClassOutline outline) {
    return scan(outline, null, false);
  }

  /** Does any selector match the class? */
  boolean matchesAny(ClassOutline outline) {
    return scan(outline, null, true) > 0;
  }

  /** Records which selectors match the class. */
  void matches(ClassOutline outline, boolean[] result) {
    scan(outline, result, false);
  }

  /** Tries pattern {@code id} against the method; records a selector hit. */
  private boolean hit(int id, MethodOutline m, int e) {
    int s = patternSelector[id];
    if (seen[s] != e && pattern(id).matches(m)) {
      seen[s] = e;
      return true;
    }
    return false;
  }

  private int scan(ClassOutline outline, @Nullable boolean[] result, boolean stopAtFirst) {
    if (!indexed) {
      index();
    }
    int e = ++epoch;
    if (e == 0) { // wrapped: reset
      Arrays.fill(seen, 0);
      e = epoch = 1;
    }
    int count = 0;
    for (MethodOutline m : outline.methods) {
      String name = m.methodName;
      // tier 1: exact names
      int[] ids = methodsByName.get(name);
      if (ids != null) {
        for (int id : ids) {
          if (hit(id, m, e)) {
            count++;
            if (stopAtFirst) {
              return count;
            }
          }
        }
      }
      // tier 2: name prefixes and suffixes
      for (int i = 0; i < prefixes.length; i++) {
        if (name.startsWith(prefixes[i]) && hit(prefixIds[i], m, e)) {
          count++;
          if (stopAtFirst) {
            return count;
          }
        }
      }
      for (int i = 0; i < suffixes.length; i++) {
        if (name.endsWith(suffixes[i]) && hit(suffixIds[i], m, e)) {
          count++;
          if (stopAtFirst) {
            return count;
          }
        }
      }
      // tier 3: annotations of interest (most methods have none)
      if (m.annotations.length > 0 && !methodsByAnnotation.isEmpty()) {
        for (String annotation : m.annotations) {
          int[] annotatedIds = methodsByAnnotation.get(annotation);
          if (annotatedIds != null) {
            for (int id : annotatedIds) {
              if (pattern(id).nameMatches(name) && hit(id, m, e)) {
                count++;
                if (stopAtFirst) {
                  return count;
                }
              }
            }
          }
        }
      }
      // tier 4: wildcard names, regular methods only
      if (wildcardMethods.length > 0 && name.charAt(0) != '<') {
        for (int id : wildcardMethods) {
          if (hit(id, m, e)) {
            count++;
            if (stopAtFirst) {
              return count;
            }
          }
        }
      }
    }
    if (!fieldsByName.isEmpty()) {
      for (FieldOutline f : outline.fields) {
        int[] ids = fieldsByName.get(f.fieldName);
        if (ids != null) {
          for (int id : ids) {
            int s = patternSelector[id];
            if (seen[s] != e && pattern(id).matches(f.access, f.descriptor)) {
              seen[s] = e;
              count++;
              if (stopAtFirst) {
                return count;
              }
            }
          }
        }
      }
    }
    for (int s : fallbackSelectors) {
      if (fallbacks[s].test(outline)) {
        seen[s] = e;
        count++;
        if (stopAtFirst) {
          return count;
        }
      }
    }
    if (result != null) {
      for (int s = 0; s < result.length; s++) {
        result[s] = seen[s] == e;
      }
    }
    return count;
  }
}
