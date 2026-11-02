package datadog.instrument.classmatch.recorder;

import java.util.ArrayList;
import java.util.List;

/**
 * Disjunction of member specs. {@code fallback} means some part couldn't be expressed as a
 * pattern, so the whole selector must use the predicate fallback.
 */
abstract class Members<M extends Members<M>> {
  final List<Spec> alts;
  final boolean fallback;

  Members(List<Spec> alts, boolean fallback) {
    this.alts = alts;
    this.fallback = fallback;
  }

  abstract M make(List<Spec> alts, boolean fallback);

  M fallback() {
    return make(new ArrayList<>(), true);
  }

  /** Conjunction distributes over the disjunctions (DNF). */
  public M and(M other) {
    if (fallback || other.fallback) {
      return fallback();
    }
    List<Spec> out = new ArrayList<>();
    for (Spec a : alts) {
      for (Spec b : other.alts) {
        Spec merged = a.merge(b);
        if (!merged.unsat) {
          out.add(merged);
        }
      }
    }
    return make(out, false);
  }

  public M or(M other) {
    if (fallback || other.fallback) {
      return fallback();
    }
    List<Spec> out = new ArrayList<>(alts);
    out.addAll(other.alts);
    return make(out, false);
  }

  /** Renders to patterns, or {@code null} when the fallback is needed. */
  List<String> toPatterns() {
    if (fallback) {
      return null;
    }
    List<String> out = new ArrayList<>();
    for (Spec s : alts) {
      List<String> p = s.toPatterns();
      if (p == null) {
        return null;
      }
      out.addAll(p);
    }
    return out;
  }
}
