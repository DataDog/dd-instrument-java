package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.AccessMatcher.CONCRETE;
import static datadog.instrument.classmatch.AccessMatcher.INSTANCE;
import static datadog.instrument.classmatch.AccessMatcher.PROTECTED;
import static datadog.instrument.classmatch.AccessMatcher.PUBLIC;
import static datadog.instrument.classmatch.ClassMatcher.declares;
import static datadog.instrument.classmatch.FieldMatcher.field;
import static datadog.instrument.classmatch.MatcherWorkload.CONTROLLERS;
import static datadog.instrument.classmatch.MatcherWorkload.HANDLERS;
import static datadog.instrument.classmatch.MatcherWorkload.MAPPINGS;
import static datadog.instrument.classmatch.MatcherWorkload.REQUESTS;
import static datadog.instrument.classmatch.MethodMatcher.method;
import static datadog.instrument.classmatch.StandardMatchers.nameStartsWith;
import static datadog.instrument.classmatch.StandardMatchers.namedOneOf;
import static datadog.instrument.classmatch.StandardMatchers.not;
import static datadog.instrument.classmatch.TypeMatcher.typeOneOf;

/** {@link MatcherWorkload} graphs built with the class-match API. */
final class ClassMatcherGraphs {
  private ClassMatcherGraphs() {}

  static ClassMatcher create(MatcherWorkload workload) {
    switch (workload) {
      case COMBINED:
        return httpGraph().or(annotationGraph());
      case REAL_WORLD:
        return classSelectionGraph().or(typeNameGraph());
      case CLASS_SELECTION:
        return classSelectionGraph();
      case TYPE_NAMES:
        return typeNameGraph();
      case STRUCTURAL:
        return structuralGraph(false);
      case STRUCTURAL_SIGNATURES:
        return structuralGraph(true);
      default:
        throw new AssertionError(workload);
    }
  }

  /** Each workload's branches in graph construction order. */
  static ClassMatcher[] branches(MatcherWorkload workload) {
    switch (workload) {
      case REAL_WORLD:
        ClassMatcher[] members = classSelectionBranches();
        ClassMatcher[] names = typeNameBranches();
        ClassMatcher[] all = java.util.Arrays.copyOf(members, members.length + names.length);
        System.arraycopy(names, 0, all, members.length, names.length);
        return all;
      case CLASS_SELECTION:
        return classSelectionBranches();
      case TYPE_NAMES:
        return typeNameBranches();
      case STRUCTURAL:
        return structuralBranches(false);
      case STRUCTURAL_SIGNATURES:
        return structuralBranches(true);
      case COMBINED:
        // HTTP branches (handler, client, stream, headers), then annotation branches.
        return new ClassMatcher[] {
          declares(handler()),
          declares(client()),
          declares(stream()),
          headers(),
          declares(endpoint()),
          controller()
        };
      default:
        throw new AssertionError(workload);
    }
  }

  private static ClassMatcher httpGraph() {
    return declares(handler().or(client()).or(stream())).or(headers());
  }

  private static MethodMatcher handler() {
    return method(namedOneOf(HANDLERS))
        .access(PUBLIC.or(PROTECTED).and(INSTANCE))
        .parameters(2)
        .parameter(0, typeOneOf(REQUESTS))
        .and(method().returning(void.class).or(method().returning("reactor.core.publisher.Mono")));
  }

  private static MethodMatcher client() {
    // RestOperations/RestTemplate URI overloads: execute and exchange both take four arguments.
    return method(namedOneOf("execute", "exchange"))
        .access(PUBLIC.and(INSTANCE))
        .parameters(4)
        .parameter(0, "java.net.URI")
        .parameter(1, "org.springframework.http.HttpMethod");
  }

  private static MethodMatcher stream() {
    return method(namedOneOf("getInputStream", "getBody"))
        .access(PUBLIC.and(INSTANCE))
        .noParameters()
        .returning(typeOneOf("java.io.InputStream", "reactor.core.publisher.Flux"));
  }

  private static ClassMatcher headers() {
    return declares(field("headers").type("org.springframework.http.HttpHeaders"))
        .and(
            declares(
                method("getHeaders")
                    .access(PUBLIC)
                    .noParameters()
                    .returning("org.springframework.http.HttpHeaders")));
  }

  private static ClassMatcher annotationGraph() {
    return declares(endpoint()).or(controller());
  }

  private static MethodMatcher endpoint() {
    return method().access(PUBLIC.or(PROTECTED).and(INSTANCE)).annotatedWith(MAPPINGS);
  }

  private static ClassMatcher controller() {
    return ClassMatcher.annotatedWith(CONTROLLERS)
        .or(ClassMatcher.annotatedWith(MAPPINGS))
        .and(declares(method().access(PUBLIC.and(INSTANCE).and(CONCRETE))));
  }

  /**
   * Direct member/annotation predicates extracted from dd-trace-java class-selection roots.
   * Type-name and hierarchy gates belong outside this declared-member API.
   */
  private static ClassMatcher[] classSelectionBranches() {
    return new ClassMatcher[] {
      // Apache BasicFuture and many SDK/version structure gates.
      declares(field("callback")),
      // Jackson parsers.
      declares(method(namedOneOf("getCurrentName", "nextFieldName"))),
      // Reactor CorePublisher and BlockingPublisher. The fixed names/prefix exclude
      // <init>/<clinit>.
      declares(method("subscribe")),
      declares(method(nameStartsWith("block"))),
      // OpenTelemetry annotations and JAX-WS; exercise the single-annotation overloads.
      declares(method().annotatedWith(MatcherWorkload.WITH_SPAN)),
      ClassMatcher.annotatedWith(MatcherWorkload.WEB_SERVICE),
      // Configurable trace-annotation set.
      declares(method().annotatedWith(MatcherWorkload.TRACE_ANNOTATIONS)),
      // Jetty 11: both field types discriminate incompatible versions.
      declares(field("_contentParameters").type("org.eclipse.jetty.util.MultiMap"))
          .and(declares(field("_dispatcherType").type("jakarta.servlet.DispatcherType")))
    };
  }

  private static ClassMatcher classSelectionGraph() {
    ClassMatcher[] branches = classSelectionBranches();
    ClassMatcher graph = branches[0];
    for (int i = 1; i < branches.length; i++) {
      graph = graph.or(branches[i]);
    }
    return graph;
  }

  /**
   * Rare structural probes kept separate from the common class-selection workload. The JaCoCo
   * branch represents its MethodVisitor leaf, without resolving a field's class.
   */
  private static ClassMatcher[] structuralBranches(boolean signatures) {
    MethodMatcher methodInsn = method("visitMethodInsn");
    MethodMatcher insn = method("visitInsn");
    MethodMatcher intInsn = method("visitIntInsn");
    MethodMatcher ldcInsn = method("visitLdcInsn");
    if (signatures) {
      methodInsn =
          methodInsn.parameters(int.class, String.class, String.class, String.class, boolean.class);
      insn = insn.parameters(int.class);
      intInsn = intInsn.parameters(int.class, int.class);
      ldcInsn = ldcInsn.parameters(Object.class);
    } else {
      methodInsn =
          methodInsn
              .parameters(5)
              .parameter(0, int.class)
              .parameter(1, String.class)
              .parameter(2, String.class)
              .parameter(3, String.class)
              .parameter(4, boolean.class);
      insn = insn.parameters(1).parameter(0, int.class);
      intInsn = intInsn.parameters(2).parameter(0, int.class).parameter(1, int.class);
      ldcInsn = ldcInsn.parameters(1).parameter(0, Object.class);
    }
    return new ClassMatcher[] {
      // LegacyProvider checks for a legacy SDK API before replacing its implementation.
      declares(method("loadEvaluatorClass").noParameters()),
      declares(methodInsn).and(declares(insn)).and(declares(intInsn)).and(declares(ldcInsn))
    };
  }

  private static ClassMatcher structuralGraph(boolean signatures) {
    ClassMatcher[] branches = structuralBranches(signatures);
    return branches[0].or(branches[1]);
  }

  /** Name predicates used inside hierarchy gates; traversal itself is outside this API. */
  private static ClassMatcher[] typeNameBranches() {
    return new ClassMatcher[] {
      new TypeNameProjection(TypeMatcher.type("java.lang.Runnable")),
      new TypeNameProjection(
          typeOneOf("org.springframework.http.HttpHeaders", "org.springframework.http.HttpEntity")),
      new TypeNameProjection(
          TypeMatcher.typeStartsWith("org.springframework.web.")
              .and(not(TypeMatcher.typeEndsWith("Exception"))),
          CONCRETE)
    };
  }

  private static ClassMatcher typeNameGraph() {
    ClassMatcher[] branches = typeNameBranches();
    return branches[0].or(branches[1]).or(branches[2]);
  }

  /** Adapts the CharSequence type API to the shared unloaded-class validation corpus. */
  private static final class TypeNameProjection implements ClassMatcher {
    private final TypeMatcher matcher;
    private final AccessMatcher access;

    TypeNameProjection(TypeMatcher matcher) {
      this(matcher, null);
    }

    TypeNameProjection(TypeMatcher matcher, AccessMatcher access) {
      this.matcher = matcher;
      this.access = access;
    }

    @Override
    public boolean test(ClassOutline outline) {
      return matcher.test(outline.className) && (access == null || access.test(outline.access));
    }
  }
}
