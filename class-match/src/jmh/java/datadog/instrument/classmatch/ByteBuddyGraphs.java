package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.MatcherWorkload.CONTROLLERS;
import static datadog.instrument.classmatch.MatcherWorkload.HANDLERS;
import static datadog.instrument.classmatch.MatcherWorkload.MAPPINGS;
import static datadog.instrument.classmatch.MatcherWorkload.REQUESTS;
import static net.bytebuddy.matcher.ElementMatchers.declaresField;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.fieldType;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isProtected;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * {@link MatcherWorkload} graphs built with Byte Buddy, equivalent to {@link ClassMatcherGraphs}.
 */
final class ByteBuddyGraphs {
  private ByteBuddyGraphs() {}

  static ElementMatcher.Junction<TypeDescription> create(MatcherWorkload workload) {
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

  /** Same validation order as {@link ClassMatcherGraphs#branches(MatcherWorkload)}. */
  static ElementMatcher.Junction<TypeDescription>[] branches(MatcherWorkload workload) {
    switch (workload) {
      case REAL_WORLD:
        ElementMatcher.Junction<TypeDescription>[] members = classSelectionBranches();
        ElementMatcher.Junction<TypeDescription>[] names = typeNameBranches();
        ElementMatcher.Junction<TypeDescription>[] all =
            java.util.Arrays.copyOf(members, members.length + names.length);
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
        return array(
            declaresMethod(handler()),
            declaresMethod(client()),
            declaresMethod(stream()),
            headers(),
            declaresMethod(endpoint()),
            controller());
      default:
        throw new AssertionError(workload);
    }
  }

  @SafeVarargs
  private static ElementMatcher.Junction<TypeDescription>[] array(
      ElementMatcher.Junction<TypeDescription>... matchers) {
    return matchers;
  }

  private static ElementMatcher.Junction<TypeDescription> httpGraph() {
    return declaresMethod(handler().or(client()).or(stream())).or(headers());
  }

  private static ElementMatcher.Junction<MethodDescription> handler() {
    return namedOneOf(HANDLERS)
        .and(isPublic().or(isProtected()))
        .and(not(isStatic()))
        .and(takesArguments(2))
        .and(takesArgument(0, namedOneOf(REQUESTS)))
        .and(returns(namedOneOf("void", "reactor.core.publisher.Mono")));
  }

  private static ElementMatcher.Junction<MethodDescription> client() {
    // RestOperations/RestTemplate URI overloads: execute and exchange both take four arguments.
    return namedOneOf("execute", "exchange")
        .and(isPublic())
        .and(not(isStatic()))
        .and(takesArguments(4))
        .and(takesArgument(0, named("java.net.URI")))
        .and(takesArgument(1, named("org.springframework.http.HttpMethod")));
  }

  private static ElementMatcher.Junction<MethodDescription> stream() {
    return namedOneOf("getInputStream", "getBody")
        .and(isPublic())
        .and(not(isStatic()))
        .and(takesArguments(0))
        .and(returns(namedOneOf("java.io.InputStream", "reactor.core.publisher.Flux")));
  }

  private static ElementMatcher.Junction<TypeDescription> headers() {
    return declaresField(
            named("headers").and(fieldType(named("org.springframework.http.HttpHeaders"))))
        .and(
            declaresMethod(
                named("getHeaders")
                    .and(isPublic())
                    .and(takesArguments(0))
                    .and(returns(named("org.springframework.http.HttpHeaders")))));
  }

  private static ElementMatcher.Junction<TypeDescription> annotationGraph() {
    return declaresMethod(endpoint()).or(controller());
  }

  private static ElementMatcher.Junction<MethodDescription> endpoint() {
    return isMethod()
        .and(isPublic().or(isProtected()))
        .and(not(isStatic()))
        .and(isAnnotatedWith(namedOneOf(MAPPINGS)));
  }

  private static ElementMatcher.Junction<TypeDescription> controller() {
    return isAnnotatedWith(namedOneOf(CONTROLLERS))
        .or(isAnnotatedWith(namedOneOf(MAPPINGS)))
        .and(
            declaresMethod(isPublic().and(not(isStatic())).and(not(isAbstract())).and(isMethod())));
  }

  /** Same declared-member projections and branch order as ClassMatcherGraphs. */
  private static ElementMatcher.Junction<TypeDescription>[] classSelectionBranches() {
    return array(
        declaresField(named("callback")),
        declaresMethod(namedOneOf("getCurrentName", "nextFieldName")),
        declaresMethod(named("subscribe")),
        declaresMethod(nameStartsWith("block")),
        declaresMethod(isMethod().and(isAnnotatedWith(named(MatcherWorkload.WITH_SPAN)))),
        isAnnotatedWith(named(MatcherWorkload.WEB_SERVICE)),
        declaresMethod(
            isMethod().and(isAnnotatedWith(namedOneOf(MatcherWorkload.TRACE_ANNOTATIONS)))),
        declaresField(
                named("_contentParameters")
                    .and(fieldType(named("org.eclipse.jetty.util.MultiMap"))))
            .and(
                declaresField(
                    named("_dispatcherType")
                        .and(fieldType(named("jakarta.servlet.DispatcherType"))))));
  }

  private static ElementMatcher.Junction<TypeDescription> classSelectionGraph() {
    ElementMatcher.Junction<TypeDescription>[] branches = classSelectionBranches();
    ElementMatcher.Junction<TypeDescription> graph = branches[0];
    for (int i = 1; i < branches.length; i++) {
      graph = graph.or(branches[i]);
    }
    return graph;
  }

  private static ElementMatcher.Junction<TypeDescription>[] structuralBranches(boolean signatures) {
    ElementMatcher.Junction<MethodDescription> methodInsn = named("visitMethodInsn");
    ElementMatcher.Junction<MethodDescription> insn = named("visitInsn");
    ElementMatcher.Junction<MethodDescription> intInsn = named("visitIntInsn");
    ElementMatcher.Junction<MethodDescription> ldcInsn = named("visitLdcInsn");
    if (signatures) {
      methodInsn =
          methodInsn.and(
              takesArguments(int.class, String.class, String.class, String.class, boolean.class));
      insn = insn.and(takesArguments(int.class));
      intInsn = intInsn.and(takesArguments(int.class, int.class));
      ldcInsn = ldcInsn.and(takesArguments(Object.class));
    } else {
      methodInsn =
          methodInsn
              .and(takesArguments(5))
              .and(takesArgument(0, int.class))
              .and(takesArgument(1, String.class))
              .and(takesArgument(2, String.class))
              .and(takesArgument(3, String.class))
              .and(takesArgument(4, boolean.class));
      insn = insn.and(takesArguments(1)).and(takesArgument(0, int.class));
      intInsn =
          intInsn
              .and(takesArguments(2))
              .and(takesArgument(0, int.class))
              .and(takesArgument(1, int.class));
      ldcInsn = ldcInsn.and(takesArguments(1)).and(takesArgument(0, Object.class));
    }
    return array(
        declaresMethod(named("loadEvaluatorClass").and(takesArguments(0))),
        declaresMethod(methodInsn)
            .and(declaresMethod(insn))
            .and(declaresMethod(intInsn))
            .and(declaresMethod(ldcInsn)));
  }

  private static ElementMatcher.Junction<TypeDescription> structuralGraph(boolean signatures) {
    ElementMatcher.Junction<TypeDescription>[] branches = structuralBranches(signatures);
    return branches[0].or(branches[1]);
  }

  private static ElementMatcher.Junction<TypeDescription>[] typeNameBranches() {
    return array(
        named("java.lang.Runnable"),
        namedOneOf("org.springframework.http.HttpHeaders", "org.springframework.http.HttpEntity"),
        nameStartsWith("org.springframework.web.")
            .and(not(net.bytebuddy.matcher.ElementMatchers.nameEndsWith("Exception")))
            .and(not(isAbstract())));
  }

  private static ElementMatcher.Junction<TypeDescription> typeNameGraph() {
    ElementMatcher.Junction<TypeDescription>[] branches = typeNameBranches();
    return branches[0].or(branches[1]).or(branches[2]);
  }
}
