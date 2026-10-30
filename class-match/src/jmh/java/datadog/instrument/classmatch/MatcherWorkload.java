package datadog.instrument.classmatch;

/**
 * Instrumentation-style matcher workloads shared by {@link MatchingApiBenchmark} and {@link
 * ColdMatcherConstructionBenchmark}. Each API's graphs live in a separate holder class, so building
 * one API's graph never loads the other API.
 */
public enum MatcherWorkload {
  /**
   * API-coverage graph of HTTP handler, client, stream, and header checks, combined with annotated
   * endpoints and controllers; includes checks commonly used for method selection.
   */
  COMBINED,
  /** Common name and declared-member graphs constructed together, sharing first-use costs. */
  REAL_WORLD,
  /** Common declared-member and annotation checks from real class-selection roots. */
  CLASS_SELECTION,
  /** Type-name and concrete-class predicates used by hierarchy gates; excludes traversal. */
  TYPE_NAMES,
  /** Rare LegacyProvider and JaCoCo MethodVisitor structural fingerprints. */
  STRUCTURAL,
  /** Equivalent structural fingerprints expressed using complete parameter signatures. */
  STRUCTURAL_SIGNATURES;

  static final String WITH_SPAN = "io.opentelemetry.instrumentation.annotations.WithSpan";
  static final String WEB_SERVICE = "jakarta.jws.WebService";
  static final String[] TRACE_ANNOTATIONS = {
    "datadog.trace.api.Trace", "com.newrelic.api.agent.Trace"
  };

  static final String[] HANDLERS = {"handle", "handleRequest", "handleRequestInternal", "service"};
  static final String[] REQUESTS = {
    "jakarta.servlet.http.HttpServletRequest",
    "javax.servlet.http.HttpServletRequest",
    "org.springframework.http.server.reactive.ServerHttpRequest",
    "org.springframework.web.server.ServerWebExchange"
  };
  static final String[] MAPPINGS = {
    "org.springframework.web.bind.annotation.RequestMapping",
    "org.springframework.web.bind.annotation.GetMapping",
    "org.springframework.web.bind.annotation.PostMapping",
    "org.springframework.web.bind.annotation.PutMapping",
    "org.springframework.web.bind.annotation.PatchMapping",
    "org.springframework.web.bind.annotation.DeleteMapping",
    "org.springframework.web.bind.annotation.ExceptionHandler"
  };
  static final String[] CONTROLLERS = {
    "org.springframework.web.bind.annotation.ControllerAdvice",
    "org.springframework.web.bind.annotation.RestControllerAdvice",
    "org.springframework.web.bind.annotation.RestController"
  };
}
