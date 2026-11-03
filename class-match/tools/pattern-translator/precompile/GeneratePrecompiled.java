// Build-step experiment (negative result): run with the jmh jar on the classpath from class-match/:
//   javac -cp build/libs/*-jmh.jar -d /tmp/pc GeneratePrecompiled.java
//   java -cp /tmp/pc:build/libs/*-jmh.jar datadog.instrument.classmatch.GeneratePrecompiled \
//       src/jmh/java/datadog/instrument/classmatch/DDTracePrecompiled.java
package datadog.instrument.classmatch;

import java.io.*;
import java.util.*;

/** Build-step generator: emits DDTracePrecompiled with every pattern compiled, in index order. */
public class GeneratePrecompiled {
  public static void main(String[] a) throws IOException {
    List<String> exprs = new ArrayList<>();
    for (String[] alts : DDTracePatterns.patterns()) {
      if (alts == null) continue; // fallback selectors have no patterns
      for (String p : alts) exprs.add(MemberPattern.compile(p).toJava());
    }
    int chunk = 200;
    try (PrintWriter w = new PrintWriter(new FileWriter(a[0]))) {
      w.println("package datadog.instrument.classmatch;\n");
      w.println("/** DD_TRACE patterns compiled at build time (generated; index order). */");
      w.println("final class DDTracePrecompiled {");
      w.println("  private DDTracePrecompiled() {}\n");
      w.println("  static MemberPattern[] compiled() {");
      w.printf("    MemberPattern[] result = new MemberPattern[%d];%n", exprs.size());
      for (int c = 0; c < exprs.size(); c += chunk) w.printf("    fill%d(result);%n", c);
      w.println("    return result;\n  }\n");
      for (int c = 0; c < exprs.size(); c += chunk) {
        w.printf("  private static void fill%d(MemberPattern[] result) {%n", c);
        for (int i = c; i < Math.min(exprs.size(), c + chunk); i++) w.printf("    result[%d] = %s;%n", i, exprs.get(i));
        w.println("  }\n");
      }
      w.println("}");
    }
    System.out.println("precompiled patterns: " + exprs.size());
  }
}
