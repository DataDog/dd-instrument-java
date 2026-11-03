package datadog.instrument.classmatch;

import static net.bytebuddy.matcher.ElementMatchers.declaresField;
import static net.bytebuddy.matcher.ElementMatchers.declaresMethod;
import static net.bytebuddy.matcher.ElementMatchers.fieldType;
import static net.bytebuddy.matcher.ElementMatchers.isAnnotatedWith;
import static net.bytebuddy.matcher.ElementMatchers.isBridge;
import static net.bytebuddy.matcher.ElementMatchers.isConstructor;
import static net.bytebuddy.matcher.ElementMatchers.isMethod;
import static net.bytebuddy.matcher.ElementMatchers.isPrivate;
import static net.bytebuddy.matcher.ElementMatchers.isProtected;
import static net.bytebuddy.matcher.ElementMatchers.isPublic;
import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.nameEndsWith;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWith;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.namedOneOf;
import static net.bytebuddy.matcher.ElementMatchers.not;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import java.util.Arrays;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Matcher patterns extracted from dd-trace-java revision 13139bbdf224f824410eb6fa98946fc86b519300.
 * Retains separate branches for instrumentation versions. The final six branches restrict
 * annotation probes to regular methods. Selectors with no member name, annotation, or type
 * constraint (on any alternative) are omitted: without the hierarchy and loader gates that guard
 * them in dd-trace-java, selectors such as {@code declares(constructor())} match nearly every
 * class.
 *
 * <p>Source locations are relative to dd-java-agent/instrumentation. Chunked fill methods keep
 * factory methods below the JVM method-size limit.
 */
final class DDTraceByteBuddyGraphs {
  private DDTraceByteBuddyGraphs() {}

  @SuppressWarnings("unchecked")
  static ElementMatcher.Junction<TypeDescription>[] branches() {
    ElementMatcher.Junction<TypeDescription>[] result = new ElementMatcher.Junction[1265];
    fill0(result);
    fill40(result);
    fill80(result);
    fill120(result);
    fill160(result);
    fill200(result);
    fill240(result);
    fill280(result);
    fill320(result);
    fill360(result);
    fill400(result);
    fill440(result);
    fill480(result);
    fill520(result);
    fill560(result);
    fill600(result);
    fill640(result);
    fill680(result);
    fill720(result);
    fill760(result);
    fill800(result);
    fill840(result);
    fill880(result);
    fill920(result);
    fill960(result);
    fill1000(result);
    fill1040(result);
    fill1080(result);
    fill1120(result);
    fill1160(result);
    fill1200(result);
    fill1240(result);
    return withoutGaps(result);
  }

  /** Compacts the branches that remain after omitting unconstrained selectors. */
  private static ElementMatcher.Junction<TypeDescription>[] withoutGaps(
      ElementMatcher.Junction<TypeDescription>[] result) {
    int size = 0;
    for (ElementMatcher.Junction<TypeDescription> branch : result) {
      if (branch != null) {
        result[size++] = branch;
      }
    }
    return Arrays.copyOf(result, size);
  }

  private static void fill0(ElementMatcher.Junction<TypeDescription>[] result) {
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/AerospikeClientInstrumentation.java:26
    result[0] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(takesArgument(0, nameStartsWith("com.aerospike.client.policy"))));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/AerospikeClientInstrumentation.java:31
    result[1] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(takesArgument(1, nameStartsWith("com.aerospike.client.listener"))));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/CommandInstrumentation.java:28
    result[2] =
        declaresMethod(
            isMethod()
                .and(named("getNode"))
                .and(takesArgument(0, named("com.aerospike.client.cluster.Cluster")))
                .and(returns(named("com.aerospike.client.cluster.Node"))));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/NioEventLoopInstrumentation.java:25
    result[3] =
        declaresMethod(
            isMethod()
                .and(named("execute"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.Runnable"))));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/PartitionInstrumentation.java:29
    result[4] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("getNodeRead", "getNodeWrite"))
                .and(takesArgument(0, named("com.aerospike.client.cluster.Cluster")))
                .and(returns(named("com.aerospike.client.cluster.Node"))));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaActorCellInstrumentation.java:43
    result[5] = declaresMethod(isMethod().and(named("invoke")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaDeadLetterQueueInstrumentation.java:52
    result[6] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("enqueue"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("akka.actor.ActorRef")))
                .and(takesArgument(1, named("akka.dispatch.Envelope")))
                .and(returns(named("void"))));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinExecutorTaskInstrumentation.java:54
    result[8] = declaresMethod(isConstructor().and(takesArgument(0, named("java.lang.Runnable"))));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinExecutorTaskInstrumentation.java:57
    result[9] = declaresMethod(isMethod().and(named("run")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinPoolInstrumentation.java:48
    result[10] = declaresMethod(isMethod().and(namedOneOf("externalPush", "fullExternalPush")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:65
    result[11] = declaresMethod(namedOneOf("exec", "fork", "cancel"));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:98
    result[12] = declaresMethod(isMethod().and(namedOneOf("doExec", "exec")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:100
    result[13] = declaresMethod(isMethod().and(named("fork")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:101
    result[14] = declaresMethod(isMethod().and(named("cancel")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaMailboxInstrumentation.java:31
    result[15] = declaresMethod(isMethod().and(named("run")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaRoutedActorCellInstrumentation.java:40
    result[16] =
        declaresMethod(
            isMethod()
                .and(named("sendMessage").and(takesArgument(0, named("akka.dispatch.Envelope")))));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/init/DisableTracingActorInitInstrumentation.java:29
    result[17] = declaresMethod(named("apply"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttp2ServerInstrumentation.java:42
    result[18] =
        declaresMethod(
            takesArguments(8)
                .and(named("bindAndHandleAsync"))
                .and(takesArgument(0, named("scala.Function1")))
                .and(takesArgument(7, named("akka.stream.Materializer"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttp2ServerInstrumentation.java:48
    result[19] =
        declaresMethod(
            takesArguments(7)
                .and(named("bindAndHandleAsync"))
                .and(takesArgument(0, named("scala.Function1")))
                .and(takesArgument(6, named("akka.stream.Materializer"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttp2ServerInstrumentation.java:54
    result[20] =
        declaresMethod(
            takesArguments(6)
                .and(named("bindAndHandleAsync"))
                .and(takesArgument(0, named("scala.Function1")))
                .and(takesArgument(5, named("akka.stream.Materializer"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttpServerInstrumentation.java:71
    result[21] =
        declaresMethod(
            named("bindAndHandle").and(takesArgument(0, named("akka.stream.scaladsl.Flow"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaPoolMasterActorInstrumentation.java:28
    result[22] =
        declaresMethod(named("akka$http$impl$engine$client$PoolMasterActor$$startPoolInterface"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/DefaultExceptionHandlerInstrumentation.java:30
    result[23] =
        declaresMethod(
            isMethod()
                .and(returns(named("akka.http.scaladsl.server.ExceptionHandler")))
                .and(takesArguments(1))
                .and(takesArgument(0, named("akka.http.scaladsl.settings.RoutingSettings"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/Bug4304Instrumentation.java:76
    result[24] = declaresField(named("oneHundredContinueSent"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/ConfigProvideRemoteAddressHeaderInstrumentation.java:28
    result[26] =
        declaresMethod(
            isPublic()
                .and(named("getBoolean"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("boolean"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/FormDataToStrictInstrumentation.java:37
    result[27] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("toStrict"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("scala.concurrent.duration.FiniteDuration")))
                .and(takesArgument(1, named("akka.stream.Materializer")))
                .and(returns(named("scala.concurrent.Future"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/JacksonUnmarshallerInstrumentation.java:37
    result[28] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(returns(named("akka.http.javadsl.unmarshalling.Unmarshaller")))
                .and(named("byteStringUnmarshaller").or(named("unmarshaller")))
                .and(takesArguments(2))
                .and(takesArgument(0, named("com.fasterxml.jackson.databind.ObjectMapper")))
                .and(takesArgument(1, named("java.lang.Class"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/StrictFormCompanionInstrumentation.java:42
    result[29] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("unmarshaller"))
                .and(returns(named("akka.http.scaladsl.unmarshalling.Unmarshaller")))
                .and(takesArguments(2))
                .and(takesArgument(0, named("akka.http.scaladsl.unmarshalling.Unmarshaller")))
                .and(takesArgument(1, named("akka.http.scaladsl.unmarshalling.Unmarshaller"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/CookieHeaderInstrumentation.java:48
    result[30] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("cookies"))
                .and(returns(named("scala.collection.immutable.Seq")))
                .and(takesArguments(0)));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/FormDataInstrumentation.java:39
    result[31] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("fields"))
                .and(takesArguments(0))
                .and(returns(named("akka.http.scaladsl.model.Uri$Query"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/FormFieldDirectivesInstrumentation.java:56
    result[32] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("formField").or(named("formFields")))
                .and(returns(named("java.lang.Object")))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0, named("akka.http.scaladsl.server.directives.FormFieldDirectives")))
                .and(
                    takesArgument(
                        1,
                        named(
                            "akka.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/FormFieldDirectivesInstrumentation.java:71
    result[33] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("formField").or(named("formFields")))
                .and(
                    returns(named("java.lang.Object"))
                        .or(returns(named("akka.http.scaladsl.server.Directive"))))
                .and(takesArguments(1))
                .and(
                    takesArgument(
                        0,
                        named(
                            "akka.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/HttpHeaderSubclassesInstrumentation.java:56
    result[34] =
        declaresMethod(
            isMethod()
                .and(named("value"))
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/HttpRequestInstrumentation.java:50
    result[35] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("headers"))
                .and(returns(named("scala.collection.immutable.Seq")))
                .and(takesArguments(0)));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/HttpRequestInstrumentation.java:58
    result[36] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(not(isStatic()))
                .and(named("entity"))
                .and(takesArguments(0)));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/MarshallingDirectivesInstrumentation.java:47
    result[37] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("entity"))
                .and(returns(named("akka.http.scaladsl.server.Directive")))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0, named("akka.http.scaladsl.server.directives.MarshallingDirectives")))
                .and(takesArgument(1, named("akka.http.scaladsl.unmarshalling.Unmarshaller"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/MarshallingDirectivesInstrumentation.java:60
    result[38] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("entity"))
                .and(returns(named("akka.http.scaladsl.server.Directive")))
                .and(takesArguments(1))
                .and(takesArgument(1, named("akka.http.scaladsl.unmarshalling.Unmarshaller"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/ParameterDirectivesInstrumentation.java:55
    result[39] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("parameter").or(named("parameters")))
                .and(returns(named("java.lang.Object")))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0, named("akka.http.scaladsl.server.directives.ParameterDirectives")))
                .and(
                    takesArgument(
                        1,
                        named(
                            "akka.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"))));
  }

  private static void fill40(ElementMatcher.Junction<TypeDescription>[] result) {
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/ParameterDirectivesInstrumentation.java:70
    result[40] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("parameter").or(named("parameters")))
                .and(
                    returns(named("java.lang.Object"))
                        .or(returns(named("akka.http.scaladsl.server.Directive"))))
                .and(takesArguments(1))
                .and(
                    takesArgument(
                        0,
                        named(
                            "akka.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/PathMatcherInstrumentation.java:40
    result[41] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(3))
                .and(takesArgument(0, named("akka.http.scaladsl.model.Uri$Path")))
                .and(takesArgument(1, named("java.lang.Object")))
                .and(takesArgument(2, named("akka.http.scaladsl.server.util.Tuple"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/RequestContextInstrumentation.java:39
    result[42] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("request"))
                .and(returns(named("akka.http.scaladsl.model.HttpRequest")))
                .and(takesArguments(0)));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UnmarshallerInstrumentation.java:55
    result[43] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("apply"))
                .and(returns(named("scala.concurrent.Future")))
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.lang.Object")))
                .and(takesArgument(1, named("scala.concurrent.ExecutionContext")))
                .and(takesArgument(2, named("akka.stream.Materializer"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UriInstrumentation.java:44
    result[44] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("queryString"))
                .and(returns(named("scala.Option")))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.nio.charset.Charset"))));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UriInstrumentation.java:52
    result[45] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("rawQueryString"))
                .and(returns(named("scala.Option")))
                .and(takesArguments(0)));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UriInstrumentation.java:59
    result[46] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("query"))
                .and(returns(named("akka.http.scaladsl.model.Uri$Query")))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.nio.charset.Charset")))
                .and(takesArgument(1, named("akka.http.scaladsl.model.Uri$ParsingMode"))));
    // akka/akka-http/akka-http-10.2-iast/src/main/java/datadog/trace/instrumentation/akkahttp102/iast/ParameterDirectivesImplInstrumentation.java:47
    result[47] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("filter"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("akka.http.scaladsl.unmarshalling.Unmarshaller")))
                .and(returns(named("akka.http.scaladsl.server.Directive"))));
    // akka/akka-http/akka-http-10.2-iast/src/main/java/datadog/trace/instrumentation/akkahttp102/iast/ParameterDirectivesImplInstrumentation.java:58
    result[48] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("repeatedFilter"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("akka.http.scaladsl.unmarshalling.Unmarshaller")))
                .and(returns(named("akka.http.scaladsl.server.Directive"))));
    // apache-httpclient/apache-httpasyncclient-4.0/src/main/java/datadog/trace/instrumentation/apachehttpasyncclient/ApacheHttpClientRedirectInstrumentation.java:40
    result[49] =
        declaresMethod(
            isMethod()
                .and(named("getRedirect"))
                .and(takesArgument(2, named("org.apache.http.protocol.HttpContext"))));
    // apache-httpclient/apache-httpasyncclient-4.0/src/main/java/datadog/trace/instrumentation/apachehttpasyncclient/BasicFutureInstrumentation.java:27
    result[50] = declaresField(named("callback"));
    // apache-httpclient/apache-httpclient-5.0/src/main/java/datadog/trace/instrumentation/apachehttpclient5/IastHttpUriRequestBaseInstrumentation.java:39
    result[52] =
        declaresMethod(
            isConstructor()
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.net.URI")))));
    // apache-httpcore/apache-httpcore-4.0/src/main/java/datadog/trace/instrumentation/apachehttpcore/IastHttpHostInstrumentation.java:32
    result[53] =
        declaresMethod(
            isConstructor()
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(3)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("int")))
                        .and(takesArgument(2, named("java.lang.String")))));
    // apache-httpcore/apache-httpcore-4.0/src/main/java/datadog/trace/instrumentation/apachehttpcore/IastHttpHostInstrumentation.java:35
    result[54] = declaresMethod(named("toURI").and(isMethod()).and(takesArguments(0)));
    // apache-httpcore/apache-httpcore-5.0/src/main/java/datadog/trace/instrumentation/apachehttpcore5/IastHttpHostInstrumentation.java:30
    result[55] =
        declaresMethod(
            isConstructor()
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(4)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.net.InetAddress")))
                        .and(takesArgument(2, named("java.lang.String")))
                        .and(takesArgument(3, named("int")))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ArmeriaMessageDeframerInstrumentation.java:35
    result[56] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        0,
                        named(
                            "com.linecorp.armeria.common.grpc.protocol.ArmeriaMessageDeframer$Listener"))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ArmeriaMessageDeframerInstrumentation.java:43
    result[57] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        2,
                        named(
                            "com.linecorp.armeria.internal.common.grpc.TransportStatusListener"))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ArmeriaMessageDeframerInstrumentation.java:49
    result[58] = declaresMethod(isMethod().and(named("process").or(named("deframe"))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:44
    result[59] =
        declaresMethod(isConstructor().and(takesArgument(4, named("io.grpc.MethodDescriptor"))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:48
    result[60] =
        declaresMethod(isConstructor().and(takesArgument(2, named("io.grpc.MethodDescriptor"))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:55
    result[61] = declaresMethod(named("cancel").and(isMethod()));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:56
    result[62] =
        declaresMethod(
            named("request")
                .and(isMethod())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("int"))))
                .or(isMethod().and(named("halfClose").and(takesArguments(0)))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:62
    result[63] = declaresMethod(named("sendMessage").and(isMethod()));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:64
    result[64] = declaresMethod(named("close").and(isMethod().and(takesArguments(2))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:68
    result[65] =
        declaresMethod(
            named("close")
                .and(isMethod())
                .and(takesArguments(3))
                .and(takesArgument(2, named("java.lang.Throwable"))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:77
    result[66] = declaresMethod(named("onNext").or(named("messageRead")));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/server/HandlerRegistryBuilderInstrumentation.java:43
    result[67] =
        declaresMethod(
            isMethod()
                .and(named("addService"))
                .and(takesArgument(0, named("io.grpc.ServerServiceDefinition"))));
    // armeria/armeria-jetty-1.24/src/main/java/datadog/trace/instrumentation/armeria/jetty/ArmeriaJettyInstrumentation.java:30
    result[69] =
        declaresMethod(
            isMethod()
                .and(named("serve"))
                .and(
                    returns(named("com.linecorp.armeria.common.HttpResponse"))
                        .and(
                            takesArgument(
                                0, named("com.linecorp.armeria.server.ServiceRequestContext")))));
    // aws-java/aws-java-dynamodb-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/dynamodb/DynamoDbClientInstrumentation.java:27
    result[70] = declaresMethod(isMethod().and(named("resolveExecutionInterceptors")));
    // aws-java/aws-java-eventbridge-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/eventbridge/EventBridgeClientInstrumentation.java:27
    result[71] = declaresMethod(isMethod().and(named("resolveExecutionInterceptors")));
    // aws-java/aws-java-lambda-handler-1.2/src/main/java/datadog/trace/instrumentation/aws/v1/lambda/LambdaHandlerInstrumentation.java:68
    result[72] =
        declaresMethod(
            isMethod()
                .and(named("handleRequest"))
                .and(takesArgument(2, named("com.amazonaws.services.lambda.runtime.Context"))));
    // aws-java/aws-java-s3-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/s3/S3ClientInstrumentation.java:27
    result[73] = declaresMethod(isMethod().and(named("resolveExecutionInterceptors")));
    // aws-java/aws-java-sdk-1.11/src/main/java/datadog/trace/instrumentation/aws/v0/AWSHttpClientInstrumentation.java:39
    result[74] = declaresMethod(isMethod().and(named("doExecute")));
    // aws-java/aws-java-sdk-1.11/src/main/java/datadog/trace/instrumentation/aws/v0/HandlerChainFactoryInstrumentation.java:31
    result[75] = declaresMethod(isMethod().and(named("newRequestHandler2Chain")));
    // aws-java/aws-java-sdk-1.11/src/main/java/datadog/trace/instrumentation/aws/v0/RequestExecutorInstrumentation.java:36
    result[76] = declaresMethod(isMethod().and(named("doExecute")));
    // aws-java/aws-java-sdk-2.2/src/main/java/datadog/trace/instrumentation/aws/v2/AwsClientInstrumentation.java:23
    result[77] = declaresMethod(isMethod().and(named("resolveExecutionInterceptors")));
    // aws-java/aws-java-sdk-2.2/src/main/java/datadog/trace/instrumentation/aws/v2/AwsHttpClientInstrumentation.java:48
    result[78] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("execute"))
                .and(
                    takesArgument(
                        1,
                        named(
                            "software.amazon.awssdk.core.internal.http.RequestExecutionContext"))));
    // aws-java/aws-java-sfn-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sfn/SfnClientInstrumentation.java:29
    result[79] = declaresMethod(isMethod().and(named("resolveExecutionInterceptors")));
  }

  private static void fill80(ElementMatcher.Junction<TypeDescription>[] result) {
    // aws-java/aws-java-sns-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sns/SnsClientInstrumentation.java:32
    result[80] = declaresMethod(isMethod().and(named("newRequestHandler2Chain")));
    // aws-java/aws-java-sns-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sns/SnsClientInstrumentation.java:30
    result[81] = declaresMethod(isMethod().and(named("resolveExecutionInterceptors")));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/QueueBufferConfigInstrumentation.java:32
    result[82] = declaresField(named("receiveAttributeNames"));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/SqsClientInstrumentation.java:34
    result[84] = declaresMethod(isMethod().and(named("newRequestHandler2Chain")));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/SqsJmsMessageInstrumentation.java:35
    result[85] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(2, named("com.amazonaws.services.sqs.model.Message"))));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/SqsReceiveResultInstrumentation.java:42
    result[87] = declaresMethod(isMethod().and(named("getMessages")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsClientInstrumentation.java:23
    result[88] = declaresMethod(isMethod().and(named("resolveExecutionInterceptors")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsJmsMessageInstrumentation.java:35
    result[89] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(2, named("software.amazon.awssdk.services.sqs.model.Message"))));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsMd5ChecksumInterceptorInstrumentation.java:21
    result[90] = declaresMethod(isMethod().and(named("afterExecution")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveRequestInstrumentation.java:27
    result[91] = declaresField(named("attributeNames"));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveRequestInstrumentation.java:32
    result[92] = declaresMethod(isMethod().and(named("build")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveResponseBuilderImplInstrumentation.java:27
    result[93] =
        declaresMethod(
            isMethod()
                .and(named("build"))
                .and(takesArguments(0))
                .and(
                    returns(
                        named(
                            "software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse"))));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveResponseBuilderInstrumentation.java:26
    result[94] =
        declaresMethod(
            isMethod()
                .and(named("toBuilder"))
                .and(takesArguments(0))
                .and(
                    returns(
                        named(
                            "software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse$Builder"))));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveResultInstrumentation.java:23
    result[95] = declaresMethod(isMethod().and(named("messages")));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/AxisEngineInstrumentation.java:34
    result[96] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("receive", "send", "sendFault"))
                .and(takesArgument(0, named("org.apache.axis2.context.MessageContext"))));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/AxisEngineInstrumentation.java:39
    result[97] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("resumeReceive", "resumeSend", "resumeSendFault"))
                .and(takesArgument(0, named("org.apache.axis2.context.MessageContext"))));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/AxisEngineInstrumentation.java:44
    result[98] =
        declaresMethod(
            isMethod()
                .and(named("invoke"))
                .and(takesArgument(0, named("org.apache.axis2.context.MessageContext"))));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/WebSphereAsyncInstrumentation.java:25
    result[99] = declaresMethod(isMethod().and(named("sendSOAPRequestAsync")));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/WebSphereAsyncInstrumentation.java:28
    result[100] = declaresMethod(isMethod().and(named("releaseBuffer")));
    // axway-api-7.5/src/main/java/datadog/trace/instrumentation/axway/AxwayHTTPPluginInstrumentation.java:37
    result[101] = declaresMethod(isMethod().and(isPublic()).and(named("invokeDispose")));
    // axway-api-7.5/src/main/java/datadog/trace/instrumentation/axway/AxwayHTTPPluginInstrumentation.java:39
    result[102] = declaresMethod(isMethod().and(isPublic()).and(named("tryTransaction")));
    // axway-api-7.5/src/main/java/datadog/trace/instrumentation/axway/AxwayHTTPPluginInstrumentation.java:41
    result[103] = declaresMethod(isMethod().and(isPublic()).and(named("sendResponse")));
    // azure-functions-1.2.2/src/main/java/datadog/trace/instrumentation/azure/functions/AzureFunctionsInstrumentation.java:48
    result[104] =
        declaresMethod(
            isAnnotatedWith(named("com.microsoft.azure.functions.annotation.FunctionName")));
    // azure-functions-worker-2.7/src/main/java/datadog/trace/instrumentation/azure/functions/worker/AzureFunctionsWorkerInstrumentation.java:36
    result[105] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("invoke"))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0,
                        named(
                            "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareContext")))
                .and(
                    takesArgument(
                        1,
                        named(
                            "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareChain"))));
    // azure-functions-worker-2.7/src/main/java/datadog/trace/instrumentation/azure/functions/worker/DurableOrchestrationExecutorInstrumentation.java:36
    result[106] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("execute"))
                .and(takesArgument(0, named("java.util.List")))
                .and(takesArgument(1, named("java.util.List"))));
    // azure-functions-worker-2.7/src/main/java/datadog/trace/instrumentation/azure/functions/worker/DurableOrchestrationMiddlewareInstrumentation.java:33
    result[107] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("invoke"))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0,
                        named(
                            "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareContext")))
                .and(
                    takesArgument(
                        1,
                        named(
                            "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareChain"))));
    // beanshell-2.0/src/main/java/datadog/trace/instrumentation/beanshell/BeanShellInstrumentation.java:73
    result[108] =
        declaresMethod(
            named("eval")
                .and(isMethod())
                .and(
                    takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("bsh.NameSpace")))));
    // beanshell-2.0/src/main/java/datadog/trace/instrumentation/beanshell/BeanShellInstrumentation.java:86
    result[109] =
        declaresMethod(
            named("eval")
                .and(isMethod())
                .and(
                    takesArguments(3)
                        .and(takesArgument(0, named("java.io.Reader")))
                        .and(takesArgument(1, named("bsh.NameSpace")))
                        .and(takesArgument(2, named("java.lang.String")))));
    // beanshell-2.0/src/main/java/datadog/trace/instrumentation/beanshell/BeanShellInstrumentation.java:96
    result[110] =
        declaresMethod(
            named("eval")
                .and(isMethod())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.lang.String")))));
    // caffeine-1.0/src/main/java/datadog/trace/instrumentation/caffeine/BoundedLocalCacheInstrumentation.java:29
    result[111] = declaresMethod(named("scheduleDrainBuffers").and(takesArguments(0)));
    // cics-9.1/src/main/java/datadog/trace/instrumentation/cics/ECIInteractionInstrumentation.java:28
    result[112] = declaresMethod(named("execute"));
    // cics-9.1/src/main/java/datadog/trace/instrumentation/cics/JavaGatewayInterfaceInstrumentation.java:37
    result[113] = declaresMethod(named("flow"));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/CommonsFileUploadAppSecInstrumentation.java:40
    result[114] =
        declaresMethod(
            named("parseRequest")
                .and(isPublic())
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest"))));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/CommonsFileuploadInstrumentation.java:27
    result[115] =
        declaresMethod(
            isMethod()
                .and(named("parse"))
                .and(isPublic())
                .and(returns(named("java.util.Map")))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(4)
                        .and(takesArgument(0, named("char[]")))
                        .and(takesArgument(1, named("int")))
                        .and(takesArgument(2, named("int")))
                        .and(takesArgument(3, named("char")))));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/FileItemInstrumentation.java:37
    result[116] = declaresMethod(named("getInputStream").and(isPublic()).and(takesArguments(0)));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/FileItemIteratorInstrumentation.java:37
    result[117] = declaresMethod(named("next").and(isPublic()).and(takesArguments(0)));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/FileItemStreamInstrumentation.java:37
    result[118] = declaresMethod(named("openStream").and(isPublic()).and(takesArguments(0)));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/ServletFileUploadInstrumentation.java:33
    result[119] =
        declaresMethod(
            named("parseRequest")
                .and(isPublic())
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest"))));
  }

  private static void fill120(ElementMatcher.Junction<TypeDescription>[] result) {
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/ServletFileUploadInstrumentation.java:38
    result[120] =
        declaresMethod(
            named("parseParameterMap")
                .and(isPublic())
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest"))));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/ServletFileUploadInstrumentation.java:43
    result[121] =
        declaresMethod(
            named("getItemIterator")
                .and(isPublic())
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest"))));
    // commons-httpclient-2.0/src/main/java/datadog/trace/instrumentation/commonshttpclient/IastHttpMethodBaseInstrumentation.java:45
    result[122] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaDeserializerInstrumentation.java:36
    result[123] =
        declaresMethod(
            isMethod()
                .and(named("configure"))
                .and(isPublic())
                .and(takesArguments(2))
                .and(takesArgument(1, named("boolean"))));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaDeserializerInstrumentation.java:46
    result[124] =
        declaresMethod(
            isMethod()
                .and(named("deserialize"))
                .and(isPublic())
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(2, named("byte[]"))));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaSerializerInstrumentation.java:37
    result[125] =
        declaresMethod(
            isMethod()
                .and(named("configure"))
                .and(isPublic())
                .and(takesArguments(2))
                .and(takesArgument(1, named("boolean"))));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaSerializerInstrumentation.java:47
    result[126] =
        declaresMethod(
            isMethod()
                .and(named("serialize"))
                .and(isPublic())
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("byte[]"))));
    // couchbase/couchbase-2.0/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseBucketInstrumentation.java:48
    result[127] = declaresMethod(isMethod().and(isPublic()).and(returns(named("rx.Observable"))));
    // couchbase/couchbase-2.0/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseClusterInstrumentation.java:49
    result[128] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(returns(named("rx.Observable")))
                .and(not(named("core"))));
    // couchbase/couchbase-2.6/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseCoreInstrumentation.java:41
    result[129] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(takesArgument(0, named("com.couchbase.client.core.message.CouchbaseRequest")))
                .and(named("send")));
    // couchbase/couchbase-2.6/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseNetworkInstrumentation.java:52
    result[130] =
        declaresMethod(
            isMethod()
                .and(named("encode"))
                .and(takesArguments(3))
                .and(
                    takesArgument(
                        0,
                        named("com.couchbase.client.deps.io.netty.channel.ChannelHandlerContext")))
                .and(takesArgument(2, named("java.util.List"))));
    // couchbase/couchbase-3.1/src/main/java/datadog/trace/instrumentation/couchbase_31/client/CoreInstrumentation.java:52
    result[133] = declaresMethod(isConstructor().and(takesArgument(2, named("java.util.Set"))));
    // couchbase/couchbase-3.1/src/main/java/datadog/trace/instrumentation/couchbase_31/client/DefaultErrorUtilInstrumentation.java:40
    result[134] =
        declaresMethod(isStatic().and(isMethod()).and(named("keyValueStatusToException")));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/CoreEnvironmentBuilderInstrumentation.java:34
    result[137] = declaresMethod(isMethod().and(named("requestTracer")));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/CoreInstrumentation.java:39
    result[138] = declaresMethod(isConstructor().and(takesArgument(2, named("java.util.Set"))));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/CoreInstrumentation.java:42
    result[139] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(2, named("com.couchbase.client.core.util.ConnectionString"))));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/DefaultErrorUtilInstrumentation.java:26
    result[140] =
        declaresMethod(isStatic().and(isMethod()).and(named("keyValueStatusToException")));
    // cucumber-5.4/src/main/java/datadog/trace/instrumentation/cucumber/CucumberInstrumentation.java:38
    result[141] =
        declaresMethod(
            named("execute")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.Object[]")))));
    // cxf-2.1/src/main/java/datadog/trace/instrumentation/cxf/InvokerInstrumentation.java:44
    result[142] = declaresMethod(isMethod().and(named("invoke")));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:47
    result[143] = declaresMethod(named("loadEvaluatorClass").and(takesArguments(0)));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:52
    result[144] = declaresMethod(named("initialize").and(takesArguments(1)));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:55
    result[145] = declaresMethod(named("getProviderHooks").and(takesArguments(0)));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:58
    result[146] =
        declaresMethod(
            namedOneOf(
                    "getBooleanEvaluation",
                    "getStringEvaluation",
                    "getIntegerEvaluation",
                    "getDoubleEvaluation",
                    "getObjectEvaluation")
                .and(takesArguments(3)));
    // datadog/openfeature/dd-openfeature-1.69/src/main/java/datadog/trace/instrumentation/openfeature/DDOpenFeatureInstrumentation.java:53
    result[147] =
        declaresMethod(isMethod().and(isStatic()).and(named("detect")).and(takesArguments(0)));
    // datadog/tracing/trace-annotation/src/main/java/datadog/trace/instrumentation/trace_annotation/DoNotTraceAnnotationInstrumentation.java:30
    result[150] = declaresMethod(isAnnotatedWith(named("datadog.trace.api.DoNotTrace")));
    // datadog/tracing/trace-annotation/src/main/java/datadog/trace/instrumentation/trace_annotation/DoNotTraceAnnotationInstrumentation.java:35
    result[151] = declaresMethod(isAnnotatedWith(named("datadog.trace.api.DoNotTrace")));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:54
    result[152] =
        declaresMethod(
            isMethod().and(namedOneOf("persistObject", "deleteObject", "refreshObject")));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:58
    result[153] =
        declaresMethod(
            isMethod()
                .and(
                    namedOneOf(
                        "refreshAllObjects", "persistObjects", "deleteObjects", "findObjects")));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:63
    result[154] =
        declaresMethod(
            isMethod()
                .and(named("findObject"))
                .and(takesArguments(4))
                .and(takesArgument(3, named("java.lang.String"))));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:69
    result[155] =
        declaresMethod(
            isMethod()
                .and(named("findObject"))
                .and(takesArguments(4))
                .and(takesArgument(2, named("java.lang.Class"))));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/JDOQueryInstrumentation.java:34
    result[156] =
        declaresMethod(
            isMethod()
                .and(
                    namedOneOf(
                        "execute",
                        "executeInternal",
                        "executeList",
                        "executeResultList",
                        "executeResultUnique",
                        "executeUnique",
                        "executeWithArray",
                        "executeWithMap",
                        "deletePersistentAll",
                        "deletePersistentInternal")));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/JDOTransactionInstrumentation.java:26
    result[157] = declaresMethod(isMethod().and(namedOneOf("commit", "rollback")));
    // datastax-cassandra/datastax-cassandra-3.0/src/main/java/datadog/trace/instrumentation/datastax/cassandra/CassandraClientInstrumentation.java:40
    result[158] =
        declaresMethod(isMethod().and(isPrivate()).and(named("newSession")).and(takesArguments(0)));
    // datastax-cassandra/datastax-cassandra-3.0/src/main/java/datadog/trace/instrumentation/datastax/cassandra/CassandraClusterInstrumentation.java:51
    result[159] = declaresMethod(isConstructor().and(takesArgument(1, named("java.util.List"))));
  }

  private static void fill160(ElementMatcher.Junction<TypeDescription>[] result) {
    // datastax-cassandra/datastax-cassandra-3.8/src/main/java/datadog/trace/instrumentation/datastax/cassandra38/CassandraClusterInstrumentation.java:45
    result[160] = declaresMethod(isConstructor().and(takesArgument(1, named("java.util.List"))));
    // datastax-cassandra/datastax-cassandra-4.0/src/main/java/datadog/trace/instrumentation/datastax/cassandra4/CassandraClientInstrumentation.java:29
    result[161] =
        declaresMethod(
            isMethod()
                .and(named("init"))
                .and(isStatic())
                .and(takesArguments(3))
                .and(takesArgument(1, named("java.util.Set")))
                .and(returns(named("java.util.concurrent.CompletionStage"))));
    // dropwizard/dropwizard-views-0.7/src/main/java/datadog/trace/instrumentation/dropwizard/view/DropwizardViewInstrumentation.java:57
    result[162] =
        declaresMethod(
            isMethod()
                .and(named("render"))
                .and(takesArgument(0, named("io.dropwizard.views.View")))
                .and(isPublic()));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-5.0/src/main/java/datadog/trace/instrumentation/elasticsearch5/Elasticsearch5RestClientInstrumentation.java:46
    result[163] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("performRequestAsync", "performRequestAsyncNoCatch"))
                .and(takesArguments(7))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(5, named("org.elasticsearch.client.ResponseListener"))));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-6.4/src/main/java/datadog/trace/instrumentation/elasticsearch6_4/Elasticsearch6RestClientInstrumentation.java:46
    result[164] =
        declaresMethod(
            isMethod()
                .and(named("performRequestAsyncNoCatch"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.elasticsearch.client.Request")))
                .and(takesArgument(1, named("org.elasticsearch.client.ResponseListener"))));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-7.0/src/main/java/datadog/trace/instrumentation/elasticsearch7/Elasticsearch7RestClientInstrumentation.java:56
    result[165] =
        declaresMethod(
            isMethod()
                .and(named("performRequest"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.elasticsearch.client.Request"))));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-7.0/src/main/java/datadog/trace/instrumentation/elasticsearch7/Elasticsearch7RestClientInstrumentation.java:62
    result[166] =
        declaresMethod(
            isMethod()
                .and(named("performRequestAsync"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.elasticsearch.client.Request")))
                .and(takesArgument(1, named("org.elasticsearch.client.ResponseListener"))));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-2.0/src/main/java/datadog/trace/instrumentation/elasticsearch2/Elasticsearch2TransportClientInstrumentation.java:49
    result[167] =
        declaresMethod(
            isMethod()
                .and(named("execute"))
                .and(takesArgument(0, named("org.elasticsearch.action.Action")))
                .and(takesArgument(1, named("org.elasticsearch.action.ActionRequest")))
                .and(takesArgument(2, named("org.elasticsearch.action.ActionListener"))));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-5.0/src/main/java/datadog/trace/instrumentation/elasticsearch5/Elasticsearch5TransportClientInstrumentation.java:49
    result[168] =
        declaresMethod(
            isMethod()
                .and(named("execute"))
                .and(takesArgument(0, named("org.elasticsearch.action.Action")))
                .and(takesArgument(1, named("org.elasticsearch.action.ActionRequest")))
                .and(takesArgument(2, named("org.elasticsearch.action.ActionListener"))));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-5.3/src/main/java/datadog/trace/instrumentation/elasticsearch5_3/Elasticsearch53TransportClientInstrumentation.java:50
    result[169] =
        declaresMethod(
            isMethod()
                .and(named("execute"))
                .and(takesArgument(0, named("org.elasticsearch.action.Action")))
                .and(takesArgument(1, named("org.elasticsearch.action.ActionRequest")))
                .and(takesArgument(2, named("org.elasticsearch.action.ActionListener"))));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-6.0/src/main/java/datadog/trace/instrumentation/elasticsearch6/Elasticsearch6TransportClientInstrumentation.java:53
    result[170] =
        declaresMethod(
            isMethod()
                .and(named("execute"))
                .and(takesArgument(0, named("org.elasticsearch.action.Action")))
                .and(takesArgument(1, named("org.elasticsearch.action.ActionRequest")))
                .and(takesArgument(2, named("org.elasticsearch.action.ActionListener"))));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-7.3/src/main/java/datadog/trace/instrumentation/elasticsearch7_3/Elasticsearch73TransportClientInstrumentation.java:59
    result[171] =
        declaresMethod(
            isMethod()
                .and(named("execute"))
                .and(takesArguments(3))
                .and(takesArgument(0, named("org.elasticsearch.action.ActionType")))
                .and(takesArgument(1, named("org.elasticsearch.action.ActionRequest")))
                .and(takesArgument(2, named("org.elasticsearch.action.ActionListener"))));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-common/src/main/java/datadog/trace/instrumentation/elasticsearch/ThreadedActionListenerInstrumentation.java:48
    result[173] = declaresMethod(namedOneOf("onResponse", "onFailure").and(takesArguments(1)));
    // finatra-2.9/src/main/java/datadog/trace/instrumentation/finatra/FinatraInstrumentation.java:49
    result[174] =
        declaresMethod(
            isMethod()
                .and(named("handleMatch"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("com.twitter.finagle.http.Request"))));
    // freemarker/freemarker-2.3.24/src/main/java/datadog/trace/instrumentation/freemarker24/ObjectWrapperInstrumentation.java:44
    result[175] =
        declaresMethod(
            named("wrap")
                .and(takesArgument(0, named("java.lang.Object")))
                .and(returns(named("freemarker.template.TemplateModel"))));
    // gax-1.4/src/main/java/datadog/trace/instrumentation/gax/CallbackChainRetryingFutureInstrumentation.java:54
    result[176] =
        declaresMethod(
            named("setAttemptFuture")
                .and(takesArguments(1))
                .and(takesArgument(0, named("com.google.api.core.ApiFuture"))));
    // glassfish-3.0/src/main/java/datadog/trace/instrumentation/glassfish/GlassFishInstrumentation.java:36
    result[177] = declaresMethod(isMethod().and(named("addToBlackList")).and(takesArguments(1)));
    // google-pubsub-1.116/src/main/java/datadog/trace/instrumentation/googlepubsub/ReceiverInstrumentation.java:21
    result[178] =
        declaresMethod(
            isMethod()
                .and(named("newBuilder"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("com.google.cloud.pubsub.v1.MessageReceiver"))));
    // google-pubsub-1.116/src/main/java/datadog/trace/instrumentation/googlepubsub/ReceiverWithAckInstrumentation.java:22
    result[179] =
        declaresMethod(
            isMethod()
                .and(named("newBuilder"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, nameEndsWith("MessageReceiverWithAckResponse"))));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/AnnotationSubstitutionProcessorInstrumentation.java:21
    result[180] =
        declaresMethod(
            isMethod()
                .and(named("lookup"))
                .and(takesArgument(0, named("jdk.vm.ci.meta.ResolvedJavaField"))));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/AnnotationSubstitutionProcessorInstrumentation.java:26
    result[181] = declaresMethod(isMethod().and(named("findTargetClasses")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/LinkAtBuildTimeInstrumentation.java:20
    result[182] =
        declaresMethod(
            isMethod()
                .and(named("linkAtBuildTime"))
                .and(takesArgument(0, named("java.lang.Class"))));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/NativeImageGeneratorRunnerInstrumentation.java:25
    result[183] = declaresMethod(isMethod().and(named("main")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/NativeImageGeneratorRunnerInstrumentation.java:28
    result[184] = declaresMethod(isMethod().and(named("extractDriverArguments")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/ResourcesFeatureInstrumentation.java:25
    result[185] = declaresMethod(isMethod().and(named("beforeAnalysis")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/VMRuntimeModule.java:33
    result[186] = declaresMethod(isMethod().and(named("initialize")));
    // graphql-java/graphql-java-14.0/src/main/java/datadog/trace/instrumentation/graphqljava14/GraphQLJavaInstrumentation.java:54
    result[187] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("checkInstrumentationDefaultState"))
                .and(returns(named("graphql.execution.instrumentation.Instrumentation"))));
    // graphql-java/graphql-java-20.0/src/main/java/datadog/trace/instrumentation/graphqljava20/GraphQLJavaInstrumentation.java:43
    result[188] =
        declaresMethod(isMethod().and(named("build")).and(returns(named("graphql.GraphQL"))));
    // graphql-java/graphql-java-common/src/main/java/datadog/trace/instrumentation/graphqljava/GraphQLUnwrapExceptionInstrumentation.java:32
    result[189] =
        declaresMethod(
            isMethod().and(named("getException")).and(returns(named("java.lang.Throwable"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/DefaultFilterChainInstrumentation.java:27
    result[190] =
        declaresMethod(
            isMethod()
                .and(isPrivate())
                .and(named("notifyFailure"))
                .and(
                    takesArgument(0, named("org.glassfish.grizzly.filterchain.FilterChainContext")))
                .and(takesArgument(1, named("java.lang.Throwable"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/DefaultFilterChainInstrumentation.java:34
    result[191] =
        declaresMethod(
            isMethod()
                .and(named("executeFilter"))
                .and(
                    takesArgument(
                        2, named("org.glassfish.grizzly.filterchain.FilterChainContext"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/FilterInstrumentation.java:30
    result[192] =
        declaresMethod(
            named("handleRead")
                .and(
                    takesArgument(0, named("org.glassfish.grizzly.filterchain.FilterChainContext")))
                .and(isPublic()));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:35
    result[193] =
        declaresMethod(
            named("setInputBuffer")
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.glassfish.grizzly.http.io.InputBuffer"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:41
    result[194] = declaresMethod(named("read").and(takesArguments(0)));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:43
    result[195] =
        declaresMethod(named("read").and(takesArguments(1)).and(takesArgument(0, named("byte[]"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:46
    result[196] =
        declaresMethod(
            named("read")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(3)
                        .and(takesArgument(0, named("byte[]")))
                        .and(takesArgument(1, named("int")))
                        .and(takesArgument(2, named("int")))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:49
    result[197] =
        declaresMethod(
            named("readBuffer")
                .and(
                    takesArguments(0)
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("int"))))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:52
    result[198] = declaresMethod(named("isFinished").and(takesArguments(0)));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:55
    result[199] = declaresMethod(named("recycle").and(takesArguments(0)));
  }

  private static void fill200(ElementMatcher.Junction<TypeDescription>[] result) {
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:30
    result[200] =
        declaresMethod(
            named("setInputBuffer")
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.glassfish.grizzly.http.io.InputBuffer"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:35
    result[201] = declaresMethod(named("read").and(takesArguments(0)));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:37
    result[202] =
        declaresMethod(named("read").and(takesArguments(1)).and(takesArgument(0, named("char[]"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:40
    result[203] =
        declaresMethod(
            named("read")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(3)
                        .and(takesArgument(0, named("char[]")))
                        .and(takesArgument(1, named("int")))
                        .and(takesArgument(2, named("int")))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:43
    result[204] =
        declaresMethod(
            named("read")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.nio.CharBuffer")))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:46
    result[205] = declaresMethod(named("isFinished").and(takesArguments(0)));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:49
    result[206] = declaresMethod(named("recycle").and(takesArguments(0)));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/HttpCodecFilterInstrumentation.java:19
    result[207] =
        declaresMethod(
            named("handleRead")
                .and(
                    takesArgument(0, named("org.glassfish.grizzly.filterchain.FilterChainContext")))
                .and(takesArgument(1, named("org.glassfish.grizzly.http.HttpHeader")))
                .and(isPublic()));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/HttpServerFilterInstrumentation.java:19
    result[208] =
        declaresMethod(
            named("prepareResponse")
                .and(
                    takesArgument(0, named("org.glassfish.grizzly.filterchain.FilterChainContext")))
                .and(takesArgument(1, named("org.glassfish.grizzly.http.HttpRequestPacket")))
                .and(takesArgument(2, named("org.glassfish.grizzly.http.HttpResponsePacket")))
                .and(takesArgument(3, named("org.glassfish.grizzly.http.HttpContent")))
                .and(isPrivate()));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/ParsedBodyParametersInstrumentation.java:57
    result[209] =
        declaresMethod(
            named("processParameters")
                .and(takesArgument(0, named("org.glassfish.grizzly.Buffer")))
                .and(takesArgument(1, named("int")))
                .and(takesArgument(2, named("int")))
                .and(takesArgument(3, named("java.nio.charset.Charset"))));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/QueuedCommandInstrumentation.java:41
    result[211] =
        declaresMethod(
            isMethod()
                .and(
                    named("run")
                        .and(
                            takesArguments(1)
                                .and(takesArgument(0, named("io.netty.channel.Channel"))))));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/AbstractClientStreamInstrumentation.java:26
    result[212] =
        declaresMethod(
            named("start")
                .and(
                    isMethod()
                        .and(
                            takesArgument(0, named("io.grpc.internal.ClientStreamListener"))
                                .and(takesArguments(1)))));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:43
    result[214] = declaresMethod(named("cancel").and(isMethod()));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:44
    result[215] =
        declaresMethod(
            named("request")
                .and(isMethod())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("int"))))
                .or(isMethod().and(named("halfClose").and(takesArguments(0)))));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:50
    result[216] = declaresMethod(named("sendMessage").and(isMethod()));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:52
    result[217] = declaresMethod(named("closeObserver").and(takesArguments(3)));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientStreamListenerImplInstrumentation.java:31
    result[219] =
        declaresMethod(
            named("exceptionThrown")
                .and(takesArgument(0, named("io.grpc.Status")))
                .and(takesArguments(1)));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientStreamListenerImplInstrumentation.java:36
    result[220] = declaresMethod(namedOneOf("messageRead", "messagesAvailable"));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientStreamListenerImplInstrumentation.java:38
    result[221] = declaresMethod(named("headersRead"));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/MessagesAvailableInstrumentation.java:42
    result[223] = declaresMethod(named("runInContext"));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/server/GrpcServerBuilderInstrumentation.java:55
    result[224] =
        declaresMethod(isMethod().and(isPublic()).and(named("build")).and(takesArguments(0)));
    // gson-1.6/src/main/java/datadog/trace/instrumentation/gson/JsonReaderInstrumentation.java:43
    result[226] =
        declaresMethod(
            isConstructor().and(takesArguments(1)).and(takesArgument(0, named("java.io.Reader"))));
    // gson-1.6/src/main/java/datadog/trace/instrumentation/gson/JsonReaderInstrumentation.java:46
    result[227] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(returns(named("java.lang.String")))
                .and(namedOneOf("nextName", "nextString"))
                .and(takesArguments(0)));
    // guava-10.0/src/main/java/datadog/trace/instrumentation/guava10/ListenableFutureInstrumentation.java:50
    result[228] =
        declaresMethod(
            named("addListener")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.Runnable")))
                        .and(takesArgument(1, named("java.util.concurrent.Executor")))));
    // guidewire-10.0/src/main/java/datadog/trace/instrumentation/guidewire/WsiAsyncResponseInstrumentation.java:73
    result[230] = declaresMethod(named("run").and(takesArguments(0)).and(isPublic()));
    // hazelcast/hazelcast-3.6/src/main/java/datadog/trace/instrumentation/hazelcast36/ClientInvocationInstrumentation.java:27
    result[231] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        0, named("com.hazelcast.client.impl.HazelcastClientInstanceImpl"))));
    // hazelcast/hazelcast-3.6/src/main/java/datadog/trace/instrumentation/hazelcast36/DistributedObjectInstrumentation.java:55
    result[232] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(
                    namedOneOf(
                        "acquire",
                        "add",
                        "addAll",
                        "addAndGet",
                        "addIndex",
                        "aggregate",
                        "availablePermits",
                        "awaitTermination",
                        "capacity",
                        "clear",
                        "contains",
                        "containsAll",
                        "containsEntry",
                        "containsKey",
                        "containsValue",
                        "decrementAndGet",
                        "delete",
                        "drainPermits",
                        "drainTo",
                        "element",
                        "entrySet",
                        "estimate",
                        "evict",
                        "evictAll",
                        "execute",
                        "executeOnAllMembers",
                        "executeOnEntries",
                        "executeOnKey",
                        "executeOnKeyOwner",
                        "executeOnKeys",
                        "executeOnMember",
                        "executeOnMembers",
                        "flush",
                        "forceUnlock",
                        "get",
                        "getAll",
                        "getAndAdd",
                        "getAndDecrement",
                        "getAndIncrement",
                        "getAndSubtract",
                        "getEntryView",
                        "getLocalExecutorStats",
                        "getLockCount",
                        "getQueryCache",
                        "getQueryCacheContext",
                        "getRemainingLeaseTime",
                        "headSequence",
                        "increasePermits",
                        "incrementAndGet",
                        "indexOf",
                        "init",
                        "invokeAll",
                        "invokeAny",
                        "isEmpty",
                        "isLocked",
                        "isLockedByCurrentThread",
                        "isShutdown",
                        "isTerminated",
                        "iterator",
                        "keySet",
                        "lastIndexOf",
                        "listIterator",
                        "loadAll",
                        "lock",
                        "newCondition",
                        "newId",
                        "offer",
                        "peek",
                        "poll",
                        "project",
                        "publish",
                        "put",
                        "putAll",
                        "putIfAbsent",
                        "putTransient",
                        "readFromEventJournal",
                        "readOne",
                        "reducePermits",
                        "release",
                        "remainingCapacity",
                        "remove",
                        "removeAll",
                        "replace",
                        "reset",
                        "retainAll",
                        "set",
                        "setTtl",
                        "shutdown",
                        "shutdownNow",
                        "size",
                        "subList",
                        "submitToKey",
                        "submitToKeys",
                        "subscribeToEventJournal",
                        "subtractAndGet",
                        "tailSequence",
                        "take",
                        "toArray",
                        "tryAcquire",
                        "tryLock",
                        "tryPut",
                        "tryRemove",
                        "unlock",
                        "valueCount",
                        "values")));
    // hazelcast/hazelcast-3.6/src/main/java/datadog/trace/instrumentation/hazelcast36/DistributedObjectInstrumentation.java:168
    result[233] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(
                    namedOneOf(
                        "addAllAsync",
                        "addAsync",
                        "estimateAsync",
                        "getAsync",
                        "putAsync",
                        "readManyAsync",
                        "removeAsync",
                        "setAsync"))
                .and(returns(named("com.hazelcast.core.ICompletableFuture"))));
    // hazelcast/hazelcast-3.9/src/main/java/datadog/trace/instrumentation/hazelcast39/ClientInvocationInstrumentation.java:38
    result[234] = declaresMethod(isMethod().and(named("invokeOnSelection")));
    // hazelcast/hazelcast-3.9/src/main/java/datadog/trace/instrumentation/hazelcast39/ClientInvocationInstrumentation.java:40
    result[235] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        0,
                        namedOneOf(
                            "com.hazelcast.client.impl.HazelcastClientInstanceImpl",
                            "com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl"))));
    // hazelcast/hazelcast-3.9/src/main/java/datadog/trace/instrumentation/hazelcast39/ClientMessageInstrumentation.java:30
    result[236] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("setOperationName"))
                .and(takesArgument(0, named("java.lang.String"))));
    // hazelcast/hazelcast-4.0/src/main/java/datadog/trace/instrumentation/hazelcast4/ClientInvocationInstrumentation.java:25
    result[237] = declaresMethod(isMethod().and(named("invokeOnSelection")));
    // hazelcast/hazelcast-4.0/src/main/java/datadog/trace/instrumentation/hazelcast4/ClientInvocationInstrumentation.java:28
    result[238] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        0,
                        named(
                            "com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl"))));
    // hazelcast/hazelcast-4.0/src/main/java/datadog/trace/instrumentation/hazelcast4/ClientListenerInstrumentation.java:33
    result[239] =
        declaresMethod(
            isMethod()
                .and(named("handleEventMessageOnCallingThread"))
                .and(takesArgument(0, named("com.hazelcast.client.impl.protocol.ClientMessage"))));
  }

  private static void fill240(ElementMatcher.Junction<TypeDescription>[] result) {
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/CriteriaInstrumentation.java:40
    result[240] = declaresMethod(isMethod().and(namedOneOf("list", "uniqueResult", "scroll")));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/IastQueryInstrumentation.java:34
    result[241] = declaresMethod(isMethod().and(named("before").and(takesArguments(0))));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/QueryInstrumentation.java:46
    result[242] =
        declaresMethod(
            isMethod().and(namedOneOf("list", "executeUpdate", "uniqueResult", "scroll")));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:52
    result[243] = declaresMethod(isMethod().and(named("close")).and(takesArguments(0)));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:57
    result[244] =
        declaresMethod(
            isMethod()
                .and(
                    namedOneOf(
                        "save",
                        "replicate",
                        "saveOrUpdate",
                        "update",
                        "merge",
                        "persist",
                        "lock",
                        "refresh",
                        "insert",
                        "delete",
                        "iterate",
                        "immediateLoad",
                        "internalLoad")));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:79
    result[245] =
        declaresMethod(
            isMethod()
                .and(named("get"))
                .and(returns(named("java.lang.Object")))
                .and(takesArgument(0, named("java.lang.String"))));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:88
    result[246] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("beginTransaction", "getTransaction"))
                .and(returns(named("org.hibernate.Transaction"))));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/TransactionInstrumentation.java:42
    result[247] = declaresMethod(isMethod().and(named("commit")).and(takesArguments(0)));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/CriteriaInstrumentation.java:40
    result[248] = declaresMethod(isMethod().and(namedOneOf("list", "uniqueResult", "scroll")));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/IastQueryInstrumentation.java:33
    result[249] = declaresMethod(isMethod().and(named("before").and(takesArguments(0))));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/QueryInstrumentation.java:47
    result[250] =
        declaresMethod(
            isMethod().and(namedOneOf("list", "executeUpdate", "uniqueResult", "scroll")));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionFactoryInstrumentation.java:41
    result[251] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("openSession", "openStatelessSession"))
                .and(takesArguments(0))
                .and(
                    returns(
                        namedOneOf("org.hibernate.Session", "org.hibernate.StatelessSession"))));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:49
    result[252] = declaresMethod(isMethod().and(named("close")).and(takesArguments(0)));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:54
    result[253] =
        declaresMethod(
            isMethod()
                .and(
                    namedOneOf(
                        "save",
                        "replicate",
                        "saveOrUpdate",
                        "update",
                        "merge",
                        "persist",
                        "lock",
                        "refresh",
                        "insert",
                        "delete",
                        "iterate",
                        "immediateLoad",
                        "internalLoad")));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:75
    result[254] =
        declaresMethod(
            isMethod()
                .and(named("get"))
                .and(returns(named("java.lang.Object")))
                .and(takesArgument(0, named("java.lang.String"))));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:84
    result[255] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("beginTransaction", "getTransaction"))
                .and(returns(named("org.hibernate.Transaction"))));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/TransactionInstrumentation.java:42
    result[256] = declaresMethod(isMethod().and(named("commit")).and(takesArguments(0)));
    // hibernate/hibernate-core-4.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_3/ProcedureCallInstrumentation.java:45
    result[257] = declaresMethod(isMethod().and(named("getOutputs")));
    // hystrix-1.4/src/main/java/datadog/trace/instrumentation/hystrix/HystrixInstrumentation.java:53
    result[258] =
        declaresMethod(named("getExecutionObservable").and(returns(named("rx.Observable"))));
    // hystrix-1.4/src/main/java/datadog/trace/instrumentation/hystrix/HystrixInstrumentation.java:56
    result[259] =
        declaresMethod(named("getFallbackObservable").and(returns(named("rx.Observable"))));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheAsyncInstrumentation.java:24
    result[260] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(
                    namedOneOf(
                        "loadCacheAsync",
                        "sizeAsync",
                        "sizeLongAsync",
                        "invokeAllAsync",
                        "getAllAsync",
                        "getEntriesAsync",
                        "getAllOutTxAsync",
                        "containsKeysAsync",
                        "putAllAsync",
                        "removeAllAsync")));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheAsyncInstrumentation.java:40
    result[261] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(
                    namedOneOf(
                        "getAndPutIfAbsentAsync",
                        "getAsync",
                        "getEntryAsync",
                        "containsKeyAsync",
                        "getAndPutAsync",
                        "putAsync",
                        "putIfAbsentAsync",
                        "removeAsync",
                        "getAndRemoveAsync",
                        "replaceAsync",
                        "getAndReplaceAsync",
                        "clearAsync",
                        "invokeAsync")));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheSyncInstrumentation.java:26
    result[262] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(
                    namedOneOf(
                        "loadCache",
                        "size",
                        "sizeLong",
                        "invokeAll",
                        "getAll",
                        "getEntries",
                        "getAllOutTx",
                        "containsKeys",
                        "putAll",
                        "removeAll")));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheSyncInstrumentation.java:42
    result[263] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(
                    namedOneOf(
                        "getAndPutIfAbsent",
                        "get",
                        "getEntry",
                        "containsKey",
                        "getAndPut",
                        "put",
                        "putIfAbsent",
                        "remove",
                        "getAndRemove",
                        "replace",
                        "getAndReplace",
                        "clear",
                        "invoke")));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheSyncInstrumentation.java:61
    result[264] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("query"))
                .and(
                    takesArgument(
                        0,
                        namedOneOf(
                            "org.apache.ignite.cache.query.Query",
                            "org.apache.ignite.cache.query.SqlFieldsQuery"))));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1FactoryInstrumentation.java:33
    result[265] =
        declaresMethod(
            named("createJsonParser")
                .and(isMethod())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.io.InputStream"))))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.io.Reader"))))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.net.URL"))))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("byte[]"))))));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1FactoryInstrumentation.java:43
    result[266] =
        declaresMethod(
            named("createJsonParser")
                .and(isMethod())
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(3)
                                .and(takesArgument(0, named("byte[]")))
                                .and(takesArgument(1, named("int")))
                                .and(takesArgument(2, named("int"))))));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1ParserInstrumentation.java:41
    result[267] =
        declaresMethod(
            named("getText")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1ParserInstrumentation.java:44
    result[268] =
        declaresMethod(
            named("getCurrentName")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1ParserInstrumentation.java:59
    result[269] = declaresMethod(namedOneOf("getText", "getCurrentName"));
    // jackson-core/jackson-core-2.0/src/main/java/datadog/trace/instrumentation/jackson_2/core/JsonParserInstrumentation.java:45
    result[270] =
        declaresMethod(
            namedOneOf("getCurrentName", "nextFieldName")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-2.12/src/main/java/datadog/trace/instrumentation/jackson_2_12/core/JsonParserInstrumentation.java:47
    result[271] =
        declaresMethod(
            namedOneOf("getCurrentName", "nextFieldName")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-2.16/src/main/java/datadog/trace/instrumentation/jackson_2_16/core/JsonParserInstrumentation.java:45
    result[272] =
        declaresMethod(
            namedOneOf("getCurrentName", "nextFieldName")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-2.6/src/main/java/datadog/trace/instrumentation/jackson_2_6/core/JsonParserInstrumentation.java:47
    result[273] =
        declaresMethod(
            namedOneOf("getCurrentName", "nextFieldName")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-2.8/src/main/java/datadog/trace/instrumentation/jackson_2_8/core/JsonParserInstrumentation.java:47
    result[274] =
        declaresMethod(
            namedOneOf("getCurrentName", "nextFieldName")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/Json2FactoryInstrumentation.java:33
    result[275] =
        declaresMethod(
            named("createParser")
                .and(isMethod())
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.lang.String")))
                                .or(
                                    net.bytebuddy.matcher.ElementMatchers
                                        .<MethodDescription>takesArguments(1)
                                        .and(takesArgument(0, named("java.io.InputStream"))))
                                .or(
                                    net.bytebuddy.matcher.ElementMatchers
                                        .<MethodDescription>takesArguments(1)
                                        .and(takesArgument(0, named("java.io.Reader"))))
                                .or(
                                    net.bytebuddy.matcher.ElementMatchers
                                        .<MethodDescription>takesArguments(1)
                                        .and(takesArgument(0, named("java.net.URL"))))
                                .or(
                                    net.bytebuddy.matcher.ElementMatchers
                                        .<MethodDescription>takesArguments(1)
                                        .and(takesArgument(0, named("byte[]")))))));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/Json2FactoryInstrumentation.java:45
    result[276] =
        declaresMethod(
            named("createParser")
                .and(isMethod())
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(3)
                                .and(takesArgument(0, named("byte[]")))
                                .and(takesArgument(1, named("int")))
                                .and(takesArgument(2, named("int"))))));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/JsonParserInstrumentation.java:40
    result[277] =
        declaresMethod(
            namedOneOf("getText", "getValueAsString")
                .and(isPublic())
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/TokenBufferInstrumentation.java:38
    result[278] =
        declaresMethod(
            isMethod()
                .and(named("asParser"))
                .and(isPublic())
                .and(returns(named("com.fasterxml.jackson.core.JsonParser"))));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ClassInstrumenterInstrumentation.java:44
    result[279] = declaresMethod(isMethod().and(named("visitTotalProbeCount")));
  }

  private static void fill280(ElementMatcher.Junction<TypeDescription>[] result) {
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/InstrumenterInstrumentation.java:38
    result[280] =
        declaresMethod(
            isMethod()
                .and(named("instrument"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("byte[]")))));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:68
    result[281] =
        declaresMethod(
            named("visitMethodInsn")
                .and(takesArguments(5))
                .and(takesArgument(0, named("int")))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("java.lang.String")))
                .and(takesArgument(3, named("java.lang.String")))
                .and(takesArgument(4, named("boolean"))));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:77
    result[282] =
        declaresMethod(
            named("visitInsn").and(takesArguments(1)).and(takesArgument(0, named("int"))));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:82
    result[283] =
        declaresMethod(
            named("visitIntInsn")
                .and(takesArguments(2))
                .and(takesArgument(0, named("int")))
                .and(takesArgument(1, named("int"))));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:88
    result[284] =
        declaresMethod(
            named("visitLdcInsn")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.Object"))));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:109
    result[285] =
        declaresMethod(
            isMethod()
                .and(named("visitMaxs"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("int"))));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:112
    result[286] =
        declaresMethod(
            isMethod()
                .and(named("insertProbe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("int"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/completablefuture/AsyncTaskInstrumentation.java:38
    result[288] = declaresMethod(named("run"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/completablefuture/AsyncTaskInstrumentation.java:39
    result[289] = declaresMethod(named("cancel"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/completablefuture/CompletableFutureUniCompletionSubclassInstrumentation.java:33
    result[291] =
        declaresMethod(
            named("tryFire")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("int")))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/JavaExecutorInstrumentation.java:21
    result[292] =
        declaresMethod(
            named("execute")
                .and(takesArgument(0, named("java.lang.Runnable")))
                .and(takesArguments(1)));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/NonStandardExecutorInstrumentation.java:10
    result[293] =
        declaresMethod(
            named("dispatch")
                .and(takesArgument(0, named("java.lang.Runnable")))
                .and(takesArgument(1, named("kotlinx.coroutines.scheduling.TaskContext"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/NonStandardExecutorInstrumentation.java:16
    result[294] =
        declaresMethod(
            named("dispatch")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.Runnable"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/RejectedExecutionHandlerInstrumentation.java:60
    result[295] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("rejectedExecution", "rejected"))
                .and(takesArgument(0, named("java.lang.Runnable"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/ThreadPoolExecutorInstrumentation.java:89
    result[296] =
        declaresMethod(
            named("beforeExecute")
                .and(isMethod())
                .and(takesArgument(1, named("java.lang.Runnable"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/ThreadPoolExecutorInstrumentation.java:94
    result[297] =
        declaresMethod(
            named("afterExecute")
                .and(isMethod())
                .and(takesArgument(0, named("java.lang.Runnable"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/ThreadPoolExecutorInstrumentation.java:99
    result[298] =
        declaresMethod(named("remove").and(isMethod()).and(returns(named("java.lang.Runnable"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinPoolInstrumentation.java:29
    result[299] = declaresMethod(isMethod().and(namedOneOf("externalPush", "externalSubmit")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinPoolInstrumentation.java:32
    result[300] = declaresMethod(isMethod().and(named("poolSubmit")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinPoolInstrumentation.java:34
    result[301] = declaresMethod(isMethod().and(named("scheduleDelayedTask")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinTaskInstrumentation.java:45
    result[302] = declaresMethod(namedOneOf("doExec", "exec", "fork", "cancel"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinTaskInstrumentation.java:51
    result[303] = declaresMethod(isMethod().and(namedOneOf("doExec", "exec")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinTaskInstrumentation.java:53
    result[304] = declaresMethod(isMethod().and(named("fork")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinWorkQueueInstrumentation.java:68
    result[305] =
        declaresField(fieldType(named("int")).and(named("top")))
            .and(declaresField(fieldType(named("int")).and(named("base"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinWorkQueueInstrumentation.java:69
    result[306] = declaresField(fieldType(named("int")).and(named("base")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/runnable/ConsumerTaskInstrumentation.java:49
    result[308] = declaresMethod(named("run"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/runnable/RunnableInstrumentation.java:58
    result[310] = declaresMethod(named("run").and(takesArguments(0)).and(isPublic()));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/timer/JavaTimerInstrumentation.java:29
    result[311] =
        declaresMethod(
            isMethod()
                .and(isPrivate())
                .and(
                    named("sched")
                        .and(takesArguments(3))
                        .and(takesArgument(0, named("java.util.TimerTask")))
                        .and(takesArgument(1, named("long")))
                        .and(takesArgument(2, named("long")))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/timer/TimerTaskInstrumentation.java:41
    result[312] = declaresMethod(named("cancel").and(takesArguments(0)).and(isPublic()));
    // java/java-concurrent/java-concurrent-21.0/src/main/java/datadog/trace/instrumentation/java/concurrent/virtualthread/TaskRunnerInstrumentation.java:53
    result[315] = declaresMethod(isMethod().and(named("run")));
    // java/java-io-1.8/src/main/java/datadog/trace/instrumentation/java/lang/InputStreamInstrumentation.java:41
    result[318] =
        declaresMethod(isConstructor().and(takesArgument(0, named("java.io.InputStream"))));
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/ProcessImplInstrumentation.java:33
    result[319] =
        declaresMethod(
            named("start")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(5)
                        .and(takesArgument(0, named("java.lang.String[]")))
                        .and(takesArgument(1, named("java.util.Map")))
                        .and(takesArgument(2, named("java.lang.String")))
                        .and(takesArgument(3, named("ProcessBuilder.Redirect[]")))
                        .and(takesArgument(4, named("boolean")))));
  }

  private static void fill320(ElementMatcher.Junction<TypeDescription>[] result) {
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/RuntimeInstrumentation.java:33
    result[320] =
        declaresMethod(
            named("exec")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(3)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.lang.String[]")))
                        .and(takesArgument(2, named("java.io.File")))));
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/ShutdownInstrumentation.java:32
    result[321] = declaresMethod(isMethod().and(isStatic()).and(named("runHooks")));
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/management/CustomMBeanBuilderInstrumentation.java:34
    result[322] =
        declaresMethod(
            isMethod()
                .and(named("newMBeanServer"))
                .and(returns(named("javax.management.MBeanServer"))));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/PollerInstrumentation.java:39
    result[323] =
        declaresMethod(
            named("start")
                .and(takesArguments(0))
                .and(returns(named("void")))
                .or(
                    named("startReadPoller")
                        .and(takesArguments(0))
                        .and(returns(named("sun.nio.ch.Poller")))));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:112
    result[324] =
        declaresMethod(
            isConstructor()
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(4)
                        .and(takesArgument(0, named("java.util.concurrent.Executor")))
                        .and(takesArgument(1, named("java.lang.String")))
                        .and(takesArgument(2, named("int")))
                        .and(takesArgument(3, named("java.lang.Runnable")))));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:119
    result[325] =
        declaresMethod(
            isMethod()
                .and(named("run"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.Runnable"))))
                .and(returns(named("void"))));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:123
    result[326] = declaresMethod(isMethod().and(named("mount")));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:124
    result[327] = declaresMethod(isMethod().and(named("unmount")));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:125
    result[328] =
        declaresMethod(
            isMethod()
                .and(named("afterDone"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("boolean")))));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:128
    result[329] =
        declaresMethod(
            isMethod()
                .and(named("afterTerminate"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("boolean")))
                        .and(takesArgument(1, named("boolean")))));
    // java/java-lang/java-lang-22.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk22/LinkerInstrumentation.java:18
    result[330] =
        declaresMethod(
            isMethod()
                .and(named("downcallHandle"))
                .and(takesArgument(0, named("java.lang.foreign.MemorySegment"))));
    // java/java-lang/java-lang-22.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk22/NativeLibraryInstrumentation.java:31
    result[332] = declaresMethod(isMethod().and(named("find")).and(returns(named("long"))));
    // java/java-lang/java-lang-classloading-1.8/src/main/java/datadog/trace/instrumentation/java/lang/classloading/ClassloadingInstrumentation.java:68
    result[334] =
        declaresMethod(
            isMethod()
                .and(named("loadClass"))
                .and(
                    takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))
                        .or(
                            takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("boolean")))))
                .and(isPublic().or(isProtected()))
                .and(not(isStatic())));
    // java/java-lang/java-lang-classloading-1.8/src/main/java/datadog/trace/instrumentation/java/lang/classloading/DefineClassInstrumentation.java:37
    result[335] =
        declaresMethod(
            isMethod()
                .and(
                    named("defineClass")
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(5)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("byte[]")))
                                .and(takesArgument(2, named("int")))
                                .and(takesArgument(3, named("int")))
                                .and(takesArgument(4, named("java.security.ProtectionDomain"))))));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/HttpUrlConnectionInstrumentation.java:60
    result[336] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(namedOneOf("connect", "getOutputStream", "getInputStream")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/HttpUrlConnectionInstrumentation.java:63
    result[337] = declaresMethod(isMethod().and(isProtected()).and(named("plainConnect")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SocketConnectInstrumentation.java:44
    result[338] =
        declaresMethod(
            isMethod()
                .and(named("connect"))
                .and(takesArgument(0, named("java.net.SocketAddress")))
                .and(takesArgument(1, named("int"))));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SslSocketInstrumentation.java:48
    result[339] = declaresMethod(isMethod().and(named("close").and(takesArguments(0))));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SslSocketInstrumentation.java:51
    result[340] = declaresMethod(isMethod().and(named("getInputStream")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SslSocketInstrumentation.java:54
    result[341] = declaresMethod(isMethod().and(named("getOutputStream")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/UrlInstrumentation.java:40
    result[342] = declaresMethod(isMethod().and(isPublic()).and(named("openConnection")));
    // java/java-net/java-net-11.0/src/main/java/datadog/trace/instrumentation/httpclient/HttpClientInstrumentation.java:66
    result[343] =
        declaresMethod(
            isMethod()
                .and(named("send"))
                .and(isPublic())
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.net.http.HttpRequest"))));
    // java/java-net/java-net-11.0/src/main/java/datadog/trace/instrumentation/httpclient/HttpClientInstrumentation.java:74
    result[344] =
        declaresMethod(
            isMethod()
                .and(named("sendAsync"))
                .and(isPublic())
                .and(takesArgument(0, named("java.net.http.HttpRequest")))
                .and(takesArgument(1, named("java.net.http.HttpResponse$BodyHandler"))));
    // java/java-nio-1.8/src/main/java/datadog/trace/instrumentation/directbytebuffer/ByteBufferInstrumentation.java:43
    result[345] =
        declaresMethod(
            isMethod()
                .and(named("allocateDirect"))
                .and(isStatic())
                .and(takesArgument(0, named("int")))
                .and(takesArguments(1)));
    // java/java-nio-1.8/src/main/java/datadog/trace/instrumentation/directbytebuffer/DirectByteBufferInstrumentation.java:41
    result[346] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("long")))
                .and(takesArgument(1, named("int")))
                .and(takesArguments(2)));
    // java/java-nio-1.8/src/main/java/datadog/trace/instrumentation/directbytebuffer/FileChannelImplInstrumentation.java:42
    result[347] =
        declaresMethod(
            isMethod()
                .and(named("map"))
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.nio.channels.FileChannel$MapMode")))
                .and(takesArgument(1, named("long")))
                .and(takesArgument(2, named("long"))));
    // java/java-rmi-1.1/src/main/java/datadog/trace/instrumentation/rmi/client/RmiClientInstrumentation.java:47
    result[348] =
        declaresMethod(
            isMethod()
                .and(named("invoke"))
                .and(takesArgument(0, named("java.rmi.Remote")))
                .and(takesArgument(1, named("java.lang.reflect.Method"))));
    // java/java-rmi-1.1/src/main/java/datadog/trace/instrumentation/rmi/context/client/RmiClientContextInstrumentation.java:80
    result[349] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("sun.rmi.transport.Connection")))
                .and(takesArgument(1, named("java.rmi.server.ObjID"))));
    // java/java-rmi-1.1/src/main/java/datadog/trace/instrumentation/rmi/context/server/RmiServerContextInstrumentation.java:48
    result[350] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("getTarget"))
                .and(takesArgument(0, named("sun.rmi.transport.ObjectEndpoint"))));
    // jboss/jboss-logmanager-1.1/src/main/java/datadog/trace/instrumentation/jbosslogmanager/ExtLogRecordInstrumentation.java:54
    result[352] =
        declaresMethod(
            isMethod().and(named("getMdc")).and(takesArgument(0, named("java.lang.String"))));
    // jboss/jboss-logmanager-1.1/src/main/java/datadog/trace/instrumentation/jbosslogmanager/ExtLogRecordInstrumentation.java:58
    result[353] = declaresMethod(isMethod().and(named("getMdcCopy")).and(takesArguments(0)));
    // jboss/jboss-logmanager-1.1/src/main/java/datadog/trace/instrumentation/jbosslogmanager/LoggerNodeInstrumentation.java:40
    result[354] =
        declaresMethod(
            isMethod()
                .and(named("publish"))
                .and(takesArgument(0, named("org.jboss.logmanager.ExtLogRecord"))));
    // jboss/jboss-modules-1.3/src/main/java/datadog/trace/instrumentation/jbossmodules/ModuleInstrumentation.java:37
    result[355] =
        declaresMethod(
            isMethod()
                .and(named("getResource"))
                .and(takesArguments(1).and(takesArgument(0, named("java.lang.String")))));
    // jboss/jboss-modules-1.3/src/main/java/datadog/trace/instrumentation/jbossmodules/ModuleInstrumentation.java:42
    result[356] =
        declaresMethod(
            isMethod()
                .and(named("getResourceAsStream"))
                .and(takesArguments(1).and(takesArgument(0, named("java.lang.String")))));
    // jboss/jboss-modules-1.3/src/main/java/datadog/trace/instrumentation/jbossmodules/ModuleInstrumentation.java:47
    result[357] =
        declaresMethod(
            isMethod()
                .and(named("loadModuleClass"))
                .and(
                    takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))
                        .or(
                            takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("boolean"))))));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/AbstractPreparedStatementInstrumentation.java:52
    result[359] = declaresMethod(nameStartsWith("execute").and(takesArguments(0)).and(isPublic()));
  }

  private static void fill360(ElementMatcher.Junction<TypeDescription>[] result) {
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/DataSourceInstrumentation.java:49
    result[360] = declaresMethod(named("getConnection"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2LinkedBlockingDequeInstrumentation.java:41
    result[361] = declaresMethod(named("pollFirst").and(takesArguments(1)));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2ManagedConnectionInstrumentation.java:35
    result[362] = declaresMethod(named("updateTransactionStatus"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2PerUserPoolDataSourceInstrumentation.java:35
    result[363] = declaresMethod(named("getPooledConnectionAndInfo"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2PoolingDataSourceInstrumentation.java:35
    result[364] = declaresMethod(named("getConnection"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2PoolingDriverInstrumentation.java:35
    result[365] = declaresMethod(named("connect"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2SharedPoolDataSourceInstrumentation.java:35
    result[366] = declaresMethod(named("getPooledConnectionAndInfo"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/DriverInstrumentation.java:52
    result[367] =
        declaresMethod(
            nameStartsWith("connect")
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("java.util.Properties")))
                .and(returns(named("java.sql.Connection"))));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariConcurrentBagHandoffQueueInstrumentation.java:42
    result[368] = declaresField(named("handoffQueue"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariConcurrentBagInstrumentation.java:63
    result[370] = declaresMethod(named("borrow"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariDataSourceInstrumentation.java:40
    result[371] = declaresMethod(named("getConnection"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariQueuedSequenceSynchronizerInstrumentation.java:30
    result[373] = declaresMethod(named("waitUntilSequenceExceeded"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/IastResultSetInstrumentation.java:51
    result[374] = declaresMethod(isMethod().and(named("next").and(takesArguments(0))));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/IastResultSetInstrumentation.java:54
    result[375] =
        declaresMethod(
            isMethod()
                .and(named("getString").or(named("getNString")))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("int")))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.lang.String"))))));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/StatementInstrumentation.java:66
    result[376] =
        declaresMethod(
            nameStartsWith("execute")
                .and(takesArgument(0, named("java.lang.String")))
                .and(isPublic()));
    // jedis/jedis-1.4/src/main/java/datadog/trace/instrumentation/jedis/JedisInstrumentation.java:46
    result[377] =
        declaresMethod(
            isMethod()
                .and(named("sendCommand"))
                .and(takesArgument(0, named("redis.clients.jedis.Protocol$Command"))));
    // jedis/jedis-3.0/src/main/java/datadog/trace/instrumentation/jedis30/JedisInstrumentation.java:37
    result[378] =
        declaresMethod(
            isMethod()
                .and(named("sendCommand"))
                .and(takesArgument(0, named("redis.clients.jedis.commands.ProtocolCommand"))));
    // jedis/jedis-4.0/src/main/java/datadog/trace/instrumentation/jedis40/JedisInstrumentation.java:39
    result[379] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("executeCommand"))
                .and(takesArgument(0, named("redis.clients.jedis.CommandObject"))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/AbstractStringReaderInstrumentation.java:31
    result[380] =
        declaresMethod(
            named("fromString")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.lang.String"))))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/ContainerRequestInstrumentation.java:32
    result[381] =
        declaresMethod(
            named("setProperty")
                .and(isPublic())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.lang.Object")))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/CookieInstrumentation.java:32
    result[382] =
        declaresMethod(
            named("getName")
                .and(isPublic())
                .and(takesArguments(0).and(returns(named("java.lang.String")))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/CookieInstrumentation.java:35
    result[383] =
        declaresMethod(
            named("getValue")
                .and(isPublic())
                .and(takesArguments(0).and(returns(named("java.lang.String")))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/FormInstrumentation.java:34
    result[384] = declaresMethod(named("asMap").and(isPublic()).and(takesArguments(0)));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/InboundMessageContextInstrumentation.java:43
    result[385] = declaresMethod(named("getHeaders").and(isPublic()).and(takesArguments(0)));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/InboundMessageContextInstrumentation.java:45
    result[386] = declaresMethod(named("getRequestCookies").and(isPublic()).and(takesArguments(0)));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/InboundMessageContextInstrumentation.java:48
    result[387] = declaresMethod(named("readEntity").and(isPublic()).and(takesArguments(4)));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JakartaWSResponseInstrumentation.java:31
    result[388] =
        declaresMethod(
            named("header")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("java.lang.Object"))))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JakartaWSResponseInstrumentation.java:34
    result[389] =
        declaresMethod(
            named("location")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.net.URI"))))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JavaxWSResponseInstrumentation.java:31
    result[390] =
        declaresMethod(
            named("header")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("java.lang.Object"))))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JavaxWSResponseInstrumentation.java:34
    result[391] =
        declaresMethod(
            named("location")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.net.URI"))))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/ReaderInterceptorExecutorInstrumentation.java:35
    result[392] = declaresMethod(named("getInputStream").and(takesArguments(0)));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/UriRoutingContextInstrumentation.java:35
    result[393] =
        declaresMethod(
            named("getPathParameters")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("boolean"))))));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/UriRoutingContextInstrumentation.java:38
    result[394] =
        declaresMethod(
            named("getQueryParameters")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("boolean"))))));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/MessageBodyReaderInstrumentation.java:46
    result[395] = declaresMethod(named("proceed").and(takesArguments(0)));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/MultiPartReaderServerSideInstrumentation.java:50
    result[396] =
        declaresMethod(
            named("readMultiPart")
                .and(isProtected())
                .and(returns(named("org.glassfish.jersey.media.multipart.MultiPart")))
                .and(takesArguments(6)));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/ServerRuntimeResponderInstrumentation.java:38
    result[397] =
        declaresMethod(
            isPublic()
                .and(named("process"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.Throwable")))
                .and(not(isStatic())));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/UriRoutingContextGetPathSegmentsInstrumentation.java:34
    result[398] =
        declaresMethod(
            named("getPathSegments")
                .and(takesArguments(1))
                .and(takesArgument(0, named("boolean"))));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/UriRoutingContextInstrumentation.java:44
    result[399] =
        declaresMethod(
            named("getPathParameters")
                .and(takesArguments(1))
                .and(takesArgument(0, named("boolean"))));
  }

  private static void fill400(ElementMatcher.Junction<TypeDescription>[] result) {
    // jersey/jersey-appsec/jersey-appsec-3.0/src/main/java/datadog/trace/instrumentation/jersey3/MessageBodyReaderInstrumentation.java:45
    result[400] = declaresMethod(named("proceed").and(takesArguments(0)));
    // jersey/jersey-appsec/jersey-appsec-3.0/src/main/java/datadog/trace/instrumentation/jersey3/MultiPartReaderServerSideInstrumentation.java:50
    result[401] =
        declaresMethod(
            named("readMultiPart")
                .and(isProtected())
                .and(returns(named("org.glassfish.jersey.media.multipart.MultiPart")))
                .and(takesArguments(6)));
    // jersey/jersey-appsec/jersey-appsec-3.0/src/main/java/datadog/trace/instrumentation/jersey3/UriRoutingContextGetPathSegmentsInstrumentation.java:34
    result[402] =
        declaresMethod(
            named("getPathSegments")
                .and(takesArguments(1))
                .and(takesArgument(0, named("boolean"))));
    // jersey/jersey-client-2.0/src/main/java/datadog/trace/instrumentation/connection_error/jersey/ClientRuntimeInstrumentation.java:30
    result[403] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("submit", "createRunnableForAsyncProcessing"))
                .and(
                    takesArgument(0, named("org.glassfish.jersey.client.ClientRequest"))
                        .and(
                            takesArgument(
                                1, named("org.glassfish.jersey.client.ResponseCallback")))));
    // jersey/jersey-client-2.0/src/main/java/datadog/trace/instrumentation/connection_error/jersey/ClientRuntimeInstrumentation.java:37
    result[404] =
        declaresMethod(
            isMethod()
                .and(named("invoke"))
                .and(takesArgument(0, named("org.glassfish.jersey.client.ClientRequest"))));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:57
    result[405] =
        declaresField(
                named("_contentParameters")
                    .and(fieldType(named("org.eclipse.jetty.util.MultiMap"))))
            .and(
                declaresField(
                    named("_dispatcherType")
                        .and(fieldType(named("jakarta.servlet.DispatcherType")))));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:60
    result[406] =
        declaresField(
            named("_dispatcherType").and(fieldType(named("jakarta.servlet.DispatcherType"))));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:66
    result[407] =
        declaresMethod(
            named("extractContentParameters").and(takesArguments(0)).or(named("getParts")));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:69
    result[408] = declaresMethod(named("getParts").and(takesArguments(0)));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:71
    result[409] = declaresMethod(named("getParts").and(takesArguments(1)));
    // jetty/jetty-appsec/jetty-appsec-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestExtractParametersInstrumentation.java:28
    result[410] = declaresMethod(named("extractParameters").and(takesArguments(0)));
    // jetty/jetty-appsec/jetty-appsec-7.0/src/main/java/datadog/trace/instrumentation/jetty70/UrlEncodedInstrumentation.java:48
    result[411] =
        declaresMethod(
            named("decodeTo")
                .and(takesArgument(0, named("java.io.InputStream")))
                .and(takesArgument(1, named("org.eclipse.jetty.util.MultiMap")))
                .and(takesArgument(2, named("java.lang.String")))
                .and(isPublic()));
    // jetty/jetty-appsec/jetty-appsec-8.1.3/src/main/java/datadog/trace/instrumentation/jetty8/RequestGetPartsInstrumentation.java:44
    result[412] = declaresMethod(named("getParts").and(takesArguments(0)));
    // jetty/jetty-appsec/jetty-appsec-8.1.3/src/main/java/datadog/trace/instrumentation/jetty8/RequestGetPartsInstrumentation.java:46
    result[413] =
        declaresMethod(
            named("getPart")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:45
    result[414] = declaresMethod(named("extractContentParameters").and(takesArguments(0)));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:48
    result[415] =
        declaresMethod(
            named("getParts")
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.eclipse.jetty.util.MultiMap"))));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:53
    result[416] = declaresMethod(named("getParts").and(takesArguments(0)));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:55
    result[417] = declaresMethod(named("getParts").and(takesArguments(1)));
    // jetty/jetty-appsec/jetty-appsec-9.3/src/main/java/datadog/trace/instrumentation/jetty93/RequestExtractContentParametersInstrumentation.java:45
    result[418] =
        declaresMethod(
            named("extractContentParameters").and(takesArguments(0)).or(named("getParts")));
    // jetty/jetty-appsec/jetty-appsec-9.3/src/main/java/datadog/trace/instrumentation/jetty93/RequestExtractContentParametersInstrumentation.java:48
    result[419] = declaresMethod(named("getParts").and(takesArguments(0)));
    // jetty/jetty-appsec/jetty-appsec-9.3/src/main/java/datadog/trace/instrumentation/jetty93/RequestExtractContentParametersInstrumentation.java:50
    result[420] = declaresMethod(named("getParts").and(takesArguments(1)));
    // jetty/jetty-appsec/jetty-appsec-9.4/src/main/java/datadog/trace/instrumentation/jetty94/RequestExtractContentParametersInstrumentation.java:45
    result[421] =
        declaresMethod(
            named("extractContentParameters").and(takesArguments(0)).or(named("getParts")));
    // jetty/jetty-appsec/jetty-appsec-9.4/src/main/java/datadog/trace/instrumentation/jetty94/RequestExtractContentParametersInstrumentation.java:48
    result[422] = declaresMethod(named("getParts").and(takesArguments(0)));
    // jetty/jetty-appsec/jetty-appsec-9.4/src/main/java/datadog/trace/instrumentation/jetty94/RequestExtractContentParametersInstrumentation.java:50
    result[423] = declaresMethod(named("getParts").and(takesArguments(1)));
    // jetty/jetty-client/jetty-client-12.0/src/main/java/datadog/trace/instrumentation/jetty_client12/JettyHttpClientInstrumentation.java:64
    result[425] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(namedOneOf("listener", "onSuccess", "onFailure", "onComplete"))
                .and(takesArguments(1)));
    // jetty/jetty-client/jetty-client-12.0/src/main/java/datadog/trace/instrumentation/jetty_client12/JettyWebSocketUpgradeInstrumentation.java:55
    result[426] =
        declaresMethod(
            named("upgrade")
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.eclipse.jetty.client.Response")))
                .and(takesArgument(1, named("org.eclipse.jetty.io.EndPoint"))));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/FutureResponseListenerInstrumentation.java:49
    result[427] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(0, named("org.eclipse.jetty.client.api.Request"))
                        .and(takesArguments(2))));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:50
    result[428] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("listener"))
                .and(
                    takesArgument(
                        0, named("org.eclipse.jetty.client.api.Request$RequestListener"))));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:56
    result[429] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("onSuccess"))
                .and(
                    takesArgument(
                        0, named("org.eclipse.jetty.client.api.Request$SuccessListener"))));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:62
    result[430] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("onFailure"))
                .and(
                    takesArgument(
                        0, named("org.eclipse.jetty.client.api.Request$FailureListener"))));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:68
    result[431] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("onComplete"))
                .and(
                    takesArgument(
                        0, named("org.eclipse.jetty.client.api.Response$CompleteListener"))));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/DispatchableInstrumentation.java:62
    result[432] =
        declaresMethod(isMethod().and(named("dispatch")).and(isPublic()).and(takesArguments(0)));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/JettyCommitResponseInstrumentation.java:92
    result[433] =
        declaresMethod(
            named("sendResponse")
                .and(takesArguments(4))
                .and(takesArgument(0, named("org.eclipse.jetty.http.MetaData$Response")))
                .and(takesArgument(1, named("java.nio.ByteBuffer")))
                .and(takesArgument(2, named("boolean")))
                .and(takesArgument(3, named("org.eclipse.jetty.util.Callback"))));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/JettyServerInstrumentation.java:96
    result[434] = declaresMethod(named("recycle").and(takesArguments(0)));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/JettyServerInstrumentation.java:99
    result[435] =
        declaresMethod(
            named("handleException")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.Throwable"))));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/RequestInstrumentation.java:29
    result[436] =
        declaresMethod(named("setContextPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/RequestInstrumentation.java:32
    result[437] =
        declaresMethod(named("setServletPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/RequestInstrumentation.java:35
    result[438] =
        declaresMethod(
            named("setRequestedSessionId").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/ServerHandleInstrumentation.java:43
    result[439] =
        declaresMethod(
            named("handle")
                .or(named("handleAsync"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.eclipse.jetty.server.HttpChannel"))));
  }

  private static void fill440(ElementMatcher.Junction<TypeDescription>[] result) {
    // jetty/jetty-server/jetty-server-11.0/src/main/java/datadog/trace/instrumentation/jetty11/JettyServerInstrumentation.java:70
    result[440] = declaresMethod(named("recycle").and(takesArguments(0)));
    // jetty/jetty-server/jetty-server-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestInstrumentation.java:25
    result[441] =
        declaresMethod(
            named("setContext")
                .and(
                    takesArgument(
                        0, named("org.eclipse.jetty.server.handler.ContextHandler$Context")))
                .and(takesArgument(1, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestInstrumentation.java:30
    result[442] =
        declaresMethod(
            named("setRequestedSessionId").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-12.0/src/main/java/datadog/trace/instrumentation/jetty12/AbstractSessionManagerInstrumentation.java:32
    result[443] =
        declaresMethod(
            named("resolveRequestedSessionId")
                .and(isProtected())
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.eclipse.jetty.server.Request"))));
    // jetty/jetty-server/jetty-server-12.0/src/main/java/datadog/trace/instrumentation/jetty12/ContextHandlerInstrumentation.java:34
    result[444] =
        declaresMethod(
            named("handle")
                .and(takesArguments(3))
                .and(takesArgument(0, named("org.eclipse.jetty.server.Request"))));
    // jetty/jetty-server/jetty-server-12.0/src/main/java/datadog/trace/instrumentation/jetty12/JettyServerInstrumentation.java:50
    result[445] = declaresMethod(named("recycle").and(takesArguments(0)));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/JettyCommitResponseInstrumentation.java:53
    result[446] =
        declaresMethod(
            named("commitResponse")
                .and(takesArguments(1))
                .and(takesArgument(0, named("boolean")))
                .or(named("completeResponse").and(takesArguments(0))));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/JettyGeneratorInstrumentation.java:39
    result[447] = declaresMethod(named("setResponse").and(takesArgument(0, named("int"))));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/JettyServerInstrumentation.java:91
    result[449] = declaresMethod(named("reset").and(takesArgument(0, named("boolean"))));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestInstrumentation.java:47
    result[450] =
        declaresMethod(named("setContextPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestInstrumentation.java:50
    result[451] =
        declaresMethod(named("setServletPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestInstrumentation.java:53
    result[452] =
        declaresMethod(
            named("setRequestedSessionId").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/ServerHandleInstrumentation.java:47
    result[453] =
        declaresMethod(
            named("handle")
                .or(named("handleAsync"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.eclipse.jetty.server.HttpConnection"))));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/JettyCommitResponseInstrumentation.java:64
    result[454] =
        declaresMethod(
            named("commitResponse")
                .and(takesArguments(1))
                .and(takesArgument(0, named("boolean")))
                .or(named("completeResponse").and(takesArguments(0))));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/JettyGeneratorInstrumentation.java:39
    result[455] = declaresMethod(named("setResponse").and(takesArgument(0, named("int"))));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/JettyServerInstrumentation.java:91
    result[457] = declaresMethod(named("reset").and(takesArguments(0)));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/RequestInstrumentation.java:47
    result[458] =
        declaresMethod(named("setContextPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/RequestInstrumentation.java:50
    result[459] =
        declaresMethod(named("setServletPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/RequestInstrumentation.java:53
    result[460] =
        declaresMethod(
            named("setRequestedSessionId").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/ServerHandleInstrumentation.java:47
    result[461] =
        declaresMethod(
            named("handle")
                .or(named("handleAsync"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.eclipse.jetty.server.AbstractHttpConnection"))));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyCommitResponseInstrumentation.java:78
    result[462] =
        declaresMethod(
            named("commitResponse")
                .and(takesArguments(3))
                .and(takesArgument(0, named("org.eclipse.jetty.http.HttpGenerator$ResponseInfo")))
                .and(takesArgument(1, named("java.nio.ByteBuffer")))
                .and(takesArgument(2, named("boolean"))));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyServerInstrumentation.java:101
    result[463] = declaresMethod(named("handle"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyServerInstrumentation.java:104
    result[464] = declaresMethod(namedOneOf("reset", "recycle").and(takesArguments(0)));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyServerInstrumentation.java:110
    result[465] =
        declaresMethod(
            named("handleException")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.Throwable"))));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/RequestInstrumentation.java:54
    result[466] =
        declaresMethod(named("setContextPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/RequestInstrumentation.java:57
    result[467] =
        declaresMethod(named("setServletPath").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/RequestInstrumentation.java:60
    result[468] =
        declaresMethod(
            named("setRequestedSessionId").and(takesArgument(0, named("java.lang.String"))));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/ServerHandleInstrumentation.java:63
    result[469] =
        declaresMethod(
            named("handle")
                .or(named("handleAsync"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.eclipse.jetty.server.HttpChannel"))));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/WebSocketSessionInstrumentation.java:58
    result[470] = declaresMethod(named("close").and(takesArguments(0)));
    // jetty/jetty-server/jetty-server-9.0.4/src/main/java/datadog/trace/instrumentation/jetty904/JettyCommitResponseInstrumentation.java:81
    result[471] =
        declaresMethod(
            named("sendResponse")
                .and(takesArguments(4))
                .and(takesArgument(0, named("org.eclipse.jetty.http.HttpGenerator$ResponseInfo")))
                .and(takesArgument(1, named("java.nio.ByteBuffer")))
                .and(takesArgument(2, named("boolean")))
                .and(takesArgument(3, named("org.eclipse.jetty.util.Callback"))));
    // jetty/jetty-server/jetty-server-9.3/src/main/java/datadog/trace/instrumentation/jetty93/JettyCommitResponseInstrumentation.java:87
    result[472] =
        declaresMethod(
            named("sendResponse")
                .and(takesArguments(4))
                .and(takesArgument(0, named("org.eclipse.jetty.http.MetaData$Response")))
                .and(takesArgument(1, named("java.nio.ByteBuffer")))
                .and(takesArgument(2, named("boolean")))
                .and(takesArgument(3, named("org.eclipse.jetty.util.Callback"))));
    // jetty/jetty-server/jetty-server-9.4.21/src/main/java/datadog/trace/instrumentation/jetty9421/JettyCommitResponseInstrumentation.java:94
    result[473] =
        declaresMethod(
            named("sendResponse")
                .and(takesArguments(4))
                .and(takesArgument(0, named("org.eclipse.jetty.http.MetaData$Response")))
                .and(takesArgument(1, named("java.nio.ByteBuffer")))
                .and(takesArgument(2, named("boolean")))
                .and(takesArgument(3, named("org.eclipse.jetty.util.Callback"))));
    // jetty/jetty-util-9.4.31/src/main/java/datadog/trace/instrumentation/jetty_util/QueuedThreadPoolInstrumentation.java:25
    result[474] = declaresMethod(namedOneOf("dispatch", "execute"));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/JMSMessageConsumerInstrumentation.java:66
    result[475] =
        declaresMethod(
            named("receive").and(takesArguments(0).or(takesArguments(1))).and(isPublic()));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/JMSMessageConsumerInstrumentation.java:69
    result[476] = declaresMethod(named("receiveNoWait").and(takesArguments(0)).and(isPublic()));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/JMSMessageConsumerInstrumentation.java:72
    result[477] = declaresMethod(named("close").and(takesArguments(0)).and(isPublic()));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/MessageInstrumentation.java:38
    result[478] =
        declaresMethod(
            nameStartsWith("acknowledge").and(isMethod()).and(isPublic()).and(takesArguments(0)));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/SessionInstrumentation.java:89
    result[479] = declaresMethod(namedOneOf("recover").and(takesArguments(0)));
  }

  private static void fill480(ElementMatcher.Junction<TypeDescription>[] result) {
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/SessionInstrumentation.java:91
    result[480] = declaresMethod(namedOneOf("commit", "rollback").and(takesArguments(0)));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/SessionInstrumentation.java:93
    result[481] = declaresMethod(named("close").and(takesArguments(0)));
    // jose-jwt-4.0/src/main/java/datadog/trace/instrumentation/josejwt/JSONObjectUtilsInstrumentation.java:32
    result[482] =
        declaresMethod(
            named("parse")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.lang.String"))))));
    // jose-jwt-4.0/src/main/java/datadog/trace/instrumentation/josejwt/JWTParserInstrumentation.java:31
    result[483] =
        declaresMethod(
            named("parsePayload")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.lang.String"))))));
    // jsp-2.3/src/main/java/datadog/trace/instrumentation/jsp/JSPInstrumentation.java:44
    result[484] =
        declaresMethod(
            named("_jspService")
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest")))
                .and(takesArgument(1, named("javax.servlet.http.HttpServletResponse")))
                .and(isPublic()));
    // jsp-2.3/src/main/java/datadog/trace/instrumentation/jsp/JasperJSPCompilationContextInstrumentation.java:36
    result[485] = declaresMethod(named("compile").and(takesArguments(0)).and(isPublic()));
    // junit/junit-4/junit-4-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit4/JUnit4CucumberInstrumentation.java:60
    result[486] =
        declaresMethod(
            named("childrenInvoker")
                .and(takesArgument(0, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit4/JUnit4CucumberSkipInstrumentation.java:70
    result[487] =
        declaresMethod(
            named("run").and(takesArgument(0, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit4/execution/Cucumber4ExecutionInstrumentation.java:76
    result[488] =
        declaresMethod(
            named("runChild")
                .and(takesArgument(0, named("io.cucumber.junit.PickleRunners$PickleRunner")))
                .and(takesArgument(1, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4-munit-0.7.28/src/main/java/datadog/trace/instrumentation/junit4/MUnitInstrumentation.java:53
    result[489] =
        declaresMethod(
            named("run").and(takesArgument(0, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4-munit-0.7.28/src/main/java/datadog/trace/instrumentation/junit4/execution/MUnitExecutionInstrumentation.java:70
    result[490] =
        declaresMethod(
            named("runTest")
                .and(takesArgument(0, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnit4Instrumentation.java:80
    result[492] =
        declaresMethod(
            named("run").and(takesArgument(0, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnit4SkipInstrumentation.java:58
    result[493] =
        declaresMethod(
            named("runChild")
                .and(takesArguments(2))
                .and(takesArgument(1, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnit4SuiteEventsInstrumentation.java:47
    result[494] = declaresMethod(named("fireTestSuiteStarted"));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnitLegacySuiteEventsInstrumentation.java:46
    result[495] =
        declaresMethod(
            named("run").and(takesArgument(0, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/execution/JUnit4ExecutionInstrumentation.java:67
    result[496] =
        declaresMethod(
            named("runLeaf")
                .and(takesArgument(0, named("org.junit.runners.model.Statement")))
                .and(takesArgument(1, named("org.junit.runner.Description")))
                .and(takesArgument(2, named("org.junit.runner.notification.RunNotifier"))));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/BazelRunNotifierWrapperInstrumentation.java:56
    result[497] =
        declaresMethod(
            named("fireTestSuiteStarted")
                .and(takesArgument(0, named("org.junit.runner.Description"))));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/BazelRunNotifierWrapperInstrumentation.java:59
    result[498] =
        declaresMethod(
            named("fireTestSuiteFinished")
                .and(takesArgument(0, named("org.junit.runner.Description"))));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/JUnit4BeforeAfterInstrumentation.java:35
    result[499] =
        declaresMethod(
            named("invokeMethod")
                .and(takesArgument(0, named("org.junit.runners.model.FrameworkMethod"))));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/order/JUnit4TestOrdererInstrumentation.java:56
    result[500] = declaresMethod(named("order"));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/order/JUnit4TestSorterInstrumentation.java:64
    result[501] =
        declaresMethod(
            named("sort").and(takesArgument(0, named("org.junit.runner.manipulation.Sorter"))));
    // junit/junit-5/junit-5-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit5/JUnit5CucumberInstrumentation.java:55
    result[502] =
        declaresMethod(
            named("execute")
                .and(takesArgument(0, named("org.junit.platform.engine.ExecutionRequest"))));
    // junit/junit-5/junit-5-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit5/JUnit5CucumberSkipInstrumentation.java:79
    result[503] = declaresMethod(named("shouldBeSkipped").and(takesArguments(1)));
    // junit/junit-5/junit-5-spock-2.0/src/main/java/datadog/trace/instrumentation/junit5/JUnit5SpockInstrumentation.java:54
    result[504] =
        declaresMethod(
            named("execute")
                .and(takesArgument(0, named("org.junit.platform.engine.ExecutionRequest"))));
    // junit/junit-5/junit-5-spock-2.0/src/main/java/datadog/trace/instrumentation/junit5/JUnit5SpockSkipInstrumentation.java:72
    result[505] = declaresMethod(named("shouldBeSkipped").and(takesArguments(1)));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/JUnit5Instrumentation.java:54
    result[507] =
        declaresMethod(
            named("discover")
                .and(
                    takesArgument(0, named("org.junit.platform.engine.EngineDiscoveryRequest"))
                        .and(takesArgument(1, named("org.junit.platform.engine.UniqueId")))));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/JUnit5Instrumentation.java:60
    result[508] =
        declaresMethod(
            named("execute")
                .and(takesArgument(0, named("org.junit.platform.engine.ExecutionRequest"))));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/JUnit5SkipInstrumentation.java:59
    result[509] = declaresMethod(named("shouldBeSkipped").and(takesArguments(1)));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/execution/JUnit5ExecutionInstrumentation.java:72
    result[510] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        3,
                        named(
                            "org.junit.platform.engine.support.hierarchical.ThrowableCollector.Factory"))));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/execution/JUnit5ExecutionInstrumentation.java:80
    result[511] = declaresMethod(named("execute").and(takesArguments(0)));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/execution/JUnit5ExecutionStoreInstrumentation.java:59
    result[512] =
        declaresMethod(
            named("discover")
                .and(
                    takesArgument(0, named("org.junit.platform.engine.EngineDiscoveryRequest"))
                        .and(takesArgument(1, named("org.junit.platform.engine.UniqueId")))));
    // junit/junit-5/junit-5.8/src/main/java/datadog/trace/instrumentation/junit5/order/JUnit5TestOrderInstrumentation.java:63
    result[515] = declaresMethod(named("getDefaultTestClassOrderer"));
    // junit/junit-5/junit-5.8/src/main/java/datadog/trace/instrumentation/junit5/order/JUnit5TestOrderInstrumentation.java:66
    result[516] = declaresMethod(named("getDefaultTestMethodOrderer"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/ConsumerCoordinatorInstrumentation.java:75
    result[517] =
        declaresMethod(isMethod().and(named("sendOffsetCommitRequest")).and(takesArguments(1)));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/ConsumerCoordinatorInstrumentation.java:78
    result[518] = declaresMethod(isMethod().and(named("onJoinComplete")).and(takesArguments(4)));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInfoInstrumentation.java:100
    result[519] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("org.apache.kafka.clients.consumer.ConsumerConfig")))
                .and(takesArgument(1, named("org.apache.kafka.common.serialization.Deserializer")))
                .and(
                    takesArgument(2, named("org.apache.kafka.common.serialization.Deserializer"))));
  }

  private static void fill520(ElementMatcher.Junction<TypeDescription>[] result) {
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInfoInstrumentation.java:110
    result[520] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("java.util.Map")))
                .and(takesArgument(1, named("org.apache.kafka.common.serialization.Deserializer")))
                .and(
                    takesArgument(2, named("org.apache.kafka.common.serialization.Deserializer"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInfoInstrumentation.java:117
    result[521] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("poll"))
                .and(takesArguments(1))
                .and(returns(named("org.apache.kafka.clients.consumer.ConsumerRecords"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInstrumentation.java:86
    result[522] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("records"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("java.lang.Iterable"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInstrumentation.java:93
    result[523] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("records"))
                .and(takesArgument(0, named("org.apache.kafka.common.TopicPartition")))
                .and(returns(named("java.util.List"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInstrumentation.java:100
    result[524] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("iterator"))
                .and(takesArguments(0))
                .and(returns(named("java.util.Iterator"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:75
    result[525] =
        declaresMethod(
            named("configure")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.util.Map")))
                        .and(takesArgument(1, named("boolean")))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:78
    result[526] =
        declaresMethod(
            named("deserialize").and(takesArguments(2)).and(takesArgument(1, named("byte[]"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:81
    result[527] =
        declaresMethod(
            named("deserialize").and(takesArguments(3)).and(takesArgument(2, named("byte[]"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:84
    result[528] =
        declaresMethod(
            named("deserialize")
                .and(takesArguments(3))
                .and(takesArgument(2, named("java.nio.ByteBuffer"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaProducerInstrumentation.java:111
    result[529] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("org.apache.kafka.clients.producer.ProducerConfig")))
                .and(takesArgument(1, named("org.apache.kafka.common.serialization.Serializer")))
                .and(takesArgument(2, named("org.apache.kafka.common.serialization.Serializer"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaProducerInstrumentation.java:127
    result[530] =
        declaresMethod(
            isMethod()
                .and(isPrivate())
                .and(takesArgument(0, named("int")))
                .and(named("ensureValidRecordSize")));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/MetadataInstrumentation.java:72
    result[531] =
        declaresMethod(
            isMethod()
                .and(named("update"))
                .and(takesArgument(0, named("org.apache.kafka.common.Cluster"))));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/MetadataInstrumentation.java:77
    result[532] =
        declaresMethod(
            isMethod()
                .and(named("update"))
                .and(takesArgument(1, named("org.apache.kafka.common.requests.MetadataResponse"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/ConsumerCoordinatorInstrumentation.java:57
    result[533] =
        declaresMethod(isMethod().and(named("sendOffsetCommitRequest")).and(takesArguments(1)));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/ConsumerCoordinatorInstrumentation.java:60
    result[534] = declaresMethod(isMethod().and(named("onJoinComplete")).and(takesArguments(4)));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInfoInstrumentation.java:72
    result[535] = declaresField(named("offsetCommitCallbackInvoker"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInfoInstrumentation.java:90
    result[536] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("org.apache.kafka.clients.consumer.ConsumerConfig")))
                .and(takesArgument(1, named("org.apache.kafka.common.serialization.Deserializer")))
                .and(
                    takesArgument(2, named("org.apache.kafka.common.serialization.Deserializer"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInfoInstrumentation.java:96
    result[537] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("poll"))
                .and(takesArguments(1))
                .and(returns(named("org.apache.kafka.clients.consumer.ConsumerRecords"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInstrumentation.java:74
    result[538] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("records"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("java.lang.Iterable"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInstrumentation.java:81
    result[539] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("records"))
                .and(takesArgument(0, named("org.apache.kafka.common.TopicPartition")))
                .and(returns(named("java.util.List"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInstrumentation.java:88
    result[540] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("iterator"))
                .and(takesArguments(0))
                .and(returns(named("java.util.Iterator"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaProducerInstrumentation.java:63
    result[541] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("org.apache.kafka.clients.producer.ProducerConfig")))
                .and(takesArgument(1, named("org.apache.kafka.common.serialization.Serializer")))
                .and(takesArgument(2, named("org.apache.kafka.common.serialization.Serializer"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaProducerInstrumentation.java:79
    result[542] =
        declaresMethod(
            isMethod()
                .and(isPrivate())
                .and(takesArgument(0, named("int")))
                .and(named("ensureValidRecordSize")));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/LegacyKafkaConsumerInfoInstrumentation.java:72
    result[543] = declaresField(named("coordinator"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/LegacyKafkaConsumerInfoInstrumentation.java:90
    result[544] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("org.apache.kafka.clients.consumer.ConsumerConfig")))
                .and(takesArgument(1, named("org.apache.kafka.common.serialization.Deserializer")))
                .and(
                    takesArgument(2, named("org.apache.kafka.common.serialization.Deserializer"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/LegacyKafkaConsumerInfoInstrumentation.java:96
    result[545] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("poll"))
                .and(takesArguments(1))
                .and(returns(named("org.apache.kafka.clients.consumer.ConsumerRecords"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/MessageListenerInstrumentation.java:45
    result[546] = declaresMethod(isMethod().and(named("onMessage")));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/MetadataInstrumentation.java:58
    result[547] =
        declaresMethod(
            isMethod()
                .and(named("update"))
                .and(takesArgument(0, named("org.apache.kafka.common.Cluster"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/MetadataInstrumentation.java:63
    result[548] =
        declaresMethod(
            isMethod()
                .and(named("update"))
                .and(takesArgument(1, named("org.apache.kafka.common.requests.MetadataResponse"))));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/OffsetCommitCallbackInvokerInstrumentation.java:31
    result[549] = declaresMethod(isMethod().and(named("enqueueUserCallbackInvocation")));
    // kafka/kafka-connect-0.11/src/main/java/datadog/trace/instrumentation/kafka_connect/ConnectWorkerInstrumentation.java:40
    result[550] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("org.apache.kafka.connect.util.ConnectorTaskId")))
                .and(
                    takesArgument(
                        1, named("org.apache.kafka.connect.runtime.TaskStatus$Listener"))));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:100
    result[551] =
        declaresMethod(
            isConstructor().and(takesArgument(4, named("org.apache.kafka.streams.StreamsConfig"))));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:106
    result[552] =
        declaresMethod(
            isConstructor().and(takesArgument(5, named("org.apache.kafka.streams.StreamsConfig"))));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:112
    result[553] =
        declaresMethod(
            isConstructor().and(takesArgument(6, named("org.apache.kafka.streams.StreamsConfig"))));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:116
    result[554] =
        declaresMethod(
            isMethod().and(named("addRecords")).and(takesArgument(1, named("java.lang.Iterable"))));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:146
    result[555] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("process"))
                .and(takesArguments(0).or(takesArguments(1).and(takesArgument(0, named("long"))))));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamsSourceNodeRecordDeserializerInstrumentation.java:33
    result[556] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("deserialize"))
                .and(takesArgument(0, named("org.apache.kafka.clients.consumer.ConsumerRecord")))
                .and(returns(named("org.apache.kafka.clients.consumer.ConsumerRecord"))));
    // kafka/kafka-streams-1.0/src/main/java/datadog/trace/instrumentation/kafka_streams10/InternalTopologyBuilderInstrumentation.java:37
    result[557] =
        declaresMethod(isMethod().and(named("build")).and(isPrivate()).and(takesArguments(1)));
    // karate/karate-1.0/src/main/java/datadog/trace/instrumentation/karate/KarateExecutionInstrumentation.java:54
    result[558] = declaresMethod(named("run").and(takesArguments(0)));
    // karate/karate-1.0/src/main/java/datadog/trace/instrumentation/karate/KarateExecutionInstrumentation.java:59
    result[559] =
        declaresMethod(
            named("addStepResult")
                .and(takesArguments(1))
                .and(takesArgument(0, named("com.intuit.karate.core.StepResult"))));
  }

  private static void fill560(ElementMatcher.Junction<TypeDescription>[] result) {
    // karate/karate-2.0/src/main/java/datadog/trace/instrumentation/karate2/KarateExecutionInstrumentation.java:64
    result[561] =
        declaresMethod(
            named("call")
                .and(takesArguments(0))
                .and(returns(named("io.karatelabs.core.ScenarioResult")))
                .and(not(isBridge())));
    // karate/karate-2.0/src/main/java/datadog/trace/instrumentation/karate2/KarateExecutionInstrumentation.java:72
    result[562] =
        declaresMethod(
            named("addStepResult")
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.karatelabs.core.StepResult"))));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineContextInstrumentation.java:21
    result[564] =
        declaresMethod(
            named("newCoroutineContext")
                .and(takesArgument(0, named("kotlinx.coroutines.CoroutineScope")))
                .and(takesArgument(1, named("kotlin.coroutines.CoroutineContext"))));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineContextInstrumentation.java:27
    result[565] =
        declaresMethod(
            named("newCoroutineContext")
                .and(takesArgument(0, named("kotlin.coroutines.CoroutineContext")))
                .and(takesArgument(1, named("kotlin.coroutines.CoroutineContext"))));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineFlowContextCheckInstrumentation.java:20
    result[566] =
        declaresMethod(
            named("checkContext")
                .and(takesArgument(0, named("kotlin.coroutines.CoroutineContext"))));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineFlowInstrumentation.java:22
    result[567] =
        declaresMethod(
            named("collectWithContextUndispatched")
                .and(takesArguments(3))
                .and(takesArgument(1, named("kotlin.coroutines.CoroutineContext"))));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineInstrumentation.java:26
    result[569] = declaresMethod(named("onCompletionInternal"));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/LazyCoroutineInstrumentation.java:30
    result[570] = declaresMethod(isMethod().and(named("onStart")).and(takesArguments(0)));
    // lettuce/lettuce-4.0/src/main/java/datadog/trace/instrumentation/lettuce4/LettuceAsyncCommandsInstrumentation.java:34
    result[571] =
        declaresMethod(
            isMethod()
                .and(named("dispatch"))
                .and(takesArgument(0, named("com.lambdaworks.redis.protocol.RedisCommand"))));
    // lettuce/lettuce-4.0/src/main/java/datadog/trace/instrumentation/lettuce4/LettuceClientInstrumentation.java:33
    result[572] = declaresMethod(isMethod().and(named("connectStandalone")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/AsyncCommandInstrumentation.java:49
    result[573] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArguments(1)
                        .and(takesArgument(0, named("io.lettuce.core.protocol.RedisCommand")))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/AsyncCommandInstrumentation.java:55
    result[574] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("complete", "completeExceptionally", "onComplete", "encode")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/AsyncCommandInstrumentation.java:58
    result[575] =
        declaresMethod(
            isMethod()
                .and(named("cancel"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("boolean")))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/CommandHandlerInstrumentation.java:45
    result[576] =
        declaresMethod(
            isMethod()
                .and(named("decode"))
                .and(takesArgument(0, named("io.netty.channel.ChannelHandlerContext")))
                .and(takesArgument(1, named("io.netty.buffer.ByteBuf")))
                .and(takesArgument(2, named("io.lettuce.core.protocol.RedisCommand"))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceAsyncCommandsInstrumentation.java:34
    result[577] =
        declaresMethod(
            isMethod()
                .and(named("dispatch"))
                .and(takesArgument(0, named("io.lettuce.core.protocol.RedisCommand"))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceClientInstrumentation.java:38
    result[578] =
        declaresMethod(
            isMethod()
                .and(isPrivate())
                .and(returns(named("io.lettuce.core.ConnectionFuture")))
                .and(nameStartsWith("connect"))
                .and(nameEndsWith("Async"))
                .and(takesArgument(1, named("io.lettuce.core.RedisURI"))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceReactiveClientInstrumentation.java:88
    result[579] = declaresMethod(isMethod().and(named("subscribe")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceReactiveClientInstrumentation.java:95
    result[580] = declaresMethod(isMethod().and(named("onNext")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/MasterReplicaConnectionProviderInstrumentation.java:59
    result[581] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("getConnection"))
                .and(takesArguments(1))
                .and(returns(named("io.lettuce.core.api.StatefulRedisConnection"))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/MasterReplicaConnectionProviderInstrumentation.java:66
    result[582] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("getConnectionAsync"))
                .and(takesArguments(1))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/PooledClusterConnectionProviderInstrumentation.java:51
    result[583] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("getConnectionAsync"))
                .and(takesArguments(2))
                .and(takesArgument(1, named("int")))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/RedisClusterClientInstrumentation.java:41
    result[584] =
        declaresMethod(
            isMethod()
                .and(named("connectToNodeAsync"))
                .and(takesArguments(4))
                .and(takesArgument(1, named("java.lang.String")))
                .and(returns(named("io.lettuce.core.ConnectionFuture"))));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/GetPartsInstrumentation.java:54
    result[585] =
        declaresMethod(isMethod().and(named("getParts")).and(isPublic()).and(takesArguments(0)));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ParseParametersInstrumentation.java:70
    result[586] =
        declaresMethod(
            isMethod()
                .and(named("parseParameters"))
                .and(isPublic().or(isProtected()))
                .and(takesArguments(0))
                .and(returns(named("void"))));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ParsePostDataInstrumentation.java:53
    result[587] =
        declaresMethod(
            isMethod()
                .and(named("parsePostData"))
                .and(isPublic().or(isProtected()))
                .and(takesArguments(0))
                .and(returns(named("java.util.Hashtable"))));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/RequestFinishInstrumentation.java:35
    result[588] = declaresMethod(named("finish").and(takesArguments(0)));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ResponseFinishInstrumentation.java:44
    result[589] = declaresMethod(named("finish").and(takesArguments(0)));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ResponseFinishInstrumentation.java:47
    result[590] =
        declaresMethod(
            named("closeResponseOutput")
                .and(takesArguments(1))
                .and(takesArgument(0, named("boolean"))));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/WebAppHandleExceptionInstrumentation.java:35
    result[592] =
        declaresMethod(
            isPublic()
                .and(named("handleException"))
                .and(takesArguments(4))
                .and(takesArgument(0, named("java.lang.Throwable")))
                .and(returns(named("void"))));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ParseParametersInstrumentation.java:70
    result[593] =
        declaresMethod(
            isMethod()
                .and(named("parseParameters"))
                .and(isPublic().or(isProtected()))
                .and(takesArguments(0))
                .and(returns(named("void"))));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ParsePostDataInstrumentation.java:53
    result[594] =
        declaresMethod(
            isMethod()
                .and(named("parsePostData"))
                .and(isPublic().or(isProtected()))
                .and(takesArguments(0))
                .and(returns(named("java.util.Hashtable"))));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/RequestFinishInstrumentation.java:35
    result[595] = declaresMethod(named("finish").and(takesArguments(0)));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ResponseFinishInstrumentation.java:44
    result[596] = declaresMethod(named("finish").and(takesArguments(0)));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ResponseFinishInstrumentation.java:47
    result[597] =
        declaresMethod(
            named("closeResponseOutput")
                .and(takesArguments(1))
                .and(takesArgument(0, named("boolean"))));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/WebAppHandleExceptionInstrumentation.java:35
    result[598] =
        declaresMethod(
            isPublic()
                .and(named("handleException"))
                .and(takesArguments(4))
                .and(takesArgument(0, named("java.lang.Throwable")))
                .and(returns(named("void"))));
    // log4j/log4j-1.2.4/src/main/java/datadog/trace/instrumentation/log4j1/CategoryInstrumentation.java:46
    result[599] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("callAppenders"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.apache.log4j.spi.LoggingEvent"))));
  }

  private static void fill600(ElementMatcher.Junction<TypeDescription>[] result) {
    // log4j/log4j-1.2.4/src/main/java/datadog/trace/instrumentation/log4j1/LoggingEventInstrumentation.java:44
    result[600] =
        declaresMethod(
            isMethod().and(named("getMDC")).and(takesArgument(0, named("java.lang.String"))));
    // log4j/log4j-1.2.4/src/main/java/datadog/trace/instrumentation/log4j1/LoggingEventInstrumentation.java:48
    result[601] = declaresMethod(isMethod().and(named("getMDCCopy")).and(takesArguments(0)));
    // log4j/log4j-2.7/src/main/java/datadog/trace/instrumentation/log4j27/ContextDataInjectorFactoryInstrumentation.java:35
    result[603] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(isStatic())
                .and(named("createInjector"))
                .and(returns(named("org.apache.logging.log4j.core.ContextDataInjector"))));
    // logback-1.0/src/main/java/datadog/trace/instrumentation/logback/LogbackLoggerInstrumentation.java:54
    result[604] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("callAppenders"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("ch.qos.logback.classic.spi.ILoggingEvent"))));
    // logback-1.0/src/main/java/datadog/trace/instrumentation/logback/LogbackLoggerInstrumentation.java:64
    result[605] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("callAppenders"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("ch.qos.logback.classic.spi.ILoggingEvent"))));
    // logback-1.0/src/main/java/datadog/trace/instrumentation/logback/LoggingEventInstrumentation.java:54
    result[606] =
        declaresMethod(
            isMethod().and(named("getMDCPropertyMap").or(named("getMdc"))).and(takesArguments(0)));
    // mail/jakarta-mail-2.0.1/src/main/java/datadog/trace/instrumentation/jakarta/mail/JakartaMailInstrumentation.java:34
    result[607] =
        declaresMethod(named("send0").and(takesArgument(0, named("jakarta.mail.Message"))));
    // mail/jakarta-mail-2.0.1/src/main/java/datadog/trace/instrumentation/jakarta/mail/JakartaMailPartInstrumentation.java:28
    result[608] =
        declaresMethod(named("setContent").and(takesArgument(0, named("java.lang.Object"))));
    // mail/jakarta-mail-2.0.1/src/main/java/datadog/trace/instrumentation/jakarta/mail/JakartaMailPartInstrumentation.java:31
    result[609] = declaresMethod(named("setText").and(takesArgument(0, named("java.lang.String"))));
    // mail/javax-mail-1.4.4/src/main/java/datadog/trace/instrumentation/javax/mail/JavaxMailInstrumentation.java:34
    result[610] = declaresMethod(named("send0").and(takesArgument(0, named("javax.mail.Message"))));
    // mail/javax-mail-1.4.4/src/main/java/datadog/trace/instrumentation/javax/mail/JavaxMailPartInstrumentation.java:28
    result[611] =
        declaresMethod(named("setContent").and(takesArgument(0, named("java.lang.Object"))));
    // mail/javax-mail-1.4.4/src/main/java/datadog/trace/instrumentation/javax/mail/JavaxMailPartInstrumentation.java:31
    result[612] = declaresMethod(named("setText").and(takesArgument(0, named("java.lang.String"))));
    // maven/maven-3.2.1/src/main/java/datadog/trace/instrumentation/maven3/MavenInstrumentation.java:42
    result[613] =
        declaresMethod(
            named("customizeContainer")
                .and(takesArgument(0, named("org.codehaus.plexus.PlexusContainer"))));
    // maven/maven-surefire-3.0/src/main/java/datadog/trace/instrumentation/maven/surefire/junit4/JUnit4ClassOrderInstrumentation.java:55
    result[614] = declaresMethod(named("setTestsToRun"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:26
    result[615] =
        declaresMethod(
            isMethod()
                .and(named("channelRead0"))
                .and(takesArgument(0, named("io.netty.channel.ChannelHandlerContext")))
                .and(takesArgument(1, named("io.micronaut.http.HttpRequest"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:33
    result[616] =
        declaresMethod(
            isMethod()
                .and(named("handleRouteMatch"))
                .and(takesArgument(0, named("io.micronaut.web.router.RouteMatch")))
                .and(takesArgument(1, named("io.micronaut.http.server.netty.NettyHttpRequest")))
                .and(takesArgument(2, named("io.netty.channel.ChannelHandlerContext")))
                .and(takesArgument(3, named("boolean"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:42
    result[617] =
        declaresMethod(
            isMethod()
                .and(named("writeFinalNettyResponse"))
                .and(takesArgument(0, named("io.micronaut.http.MutableHttpResponse")))
                .and(takesArgument(1, named("io.micronaut.http.HttpRequest")))
                .and(takesArgument(2, named("io.netty.channel.ChannelHandlerContext"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:50
    result[618] =
        declaresMethod(
            isMethod()
                .and(named("writeDefaultErrorResponse"))
                .and(takesArgument(0, named("io.netty.channel.ChannelHandlerContext")))
                .and(takesArgument(1, named("io.micronaut.http.server.netty.NettyHttpRequest")))
                .and(takesArgument(2, named("java.lang.Throwable")))
                .and(takesArgument(3, named("boolean"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:29
    result[619] =
        declaresMethod(
            isMethod()
                .and(named("channelRead0"))
                .and(takesArgument(0, named("io.netty.channel.ChannelHandlerContext")))
                .and(takesArgument(1, named("io.micronaut.http.HttpRequest"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:36
    result[620] =
        declaresMethod(
            isMethod()
                .and(named("handleRouteMatch"))
                .and(takesArgument(0, named("io.micronaut.web.router.RouteMatch")))
                .and(takesArgument(1, named("io.micronaut.http.server.netty.NettyHttpRequest")))
                .and(takesArgument(2, named("io.netty.channel.ChannelHandlerContext"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:44
    result[621] =
        declaresMethod(
            isMethod()
                .and(named("writeFinalNettyResponse"))
                .and(takesArgument(0, named("io.micronaut.http.MutableHttpResponse")))
                .and(takesArgument(1, named("io.micronaut.http.HttpRequest")))
                .and(takesArgument(2, named("io.netty.channel.ChannelHandlerContext"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:52
    result[622] =
        declaresMethod(
            isMethod()
                .and(named("createDefaultErrorResponse"))
                .and(takesArgument(0, named("io.micronaut.http.HttpRequest")))
                .and(takesArgument(1, named("java.lang.Throwable"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:35
    result[623] = declaresMethod(isMethod().and(named("handleNormal")).and(takesArguments(0)));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:38
    result[624] =
        declaresMethod(
            isMethod()
                .and(named("handleNormal"))
                .and(takesArgument(0, named("io.micronaut.http.server.netty.NettyHttpRequest"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:44
    result[625] =
        declaresMethod(
            isMethod()
                .and(named("findRouteMatch"))
                .and(takesArgument(0, named("io.micronaut.http.HttpRequest")))
                .and(returns(named("io.micronaut.web.router.UriRouteMatch"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:51
    result[626] =
        declaresMethod(
            isMethod()
                .and(named("createDefaultErrorResponse"))
                .and(takesArgument(0, named("io.micronaut.http.HttpRequest")))
                .and(takesArgument(1, named("java.lang.Throwable"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:58
    result[627] =
        declaresMethod(
            isMethod()
                .and(named("encodeHttpResponse"))
                .and(takesArgument(1, named("io.micronaut.http.server.netty.NettyHttpRequest")))
                .and(
                    takesArgument(
                        2,
                        namedOneOf(
                            "io.micronaut.http.MutableHttpResponse",
                            "io.micronaut.http.HttpResponse"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:69
    result[628] =
        declaresMethod(
            isMethod()
                .and(named("encodeHttpResponse"))
                .and(takesArgument(0, named("io.micronaut.http.server.netty.NettyHttpRequest")))
                .and(takesArgument(1, named("io.micronaut.http.HttpResponse"))));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:76
    result[629] =
        declaresMethod(
            isMethod()
                .and(named("encodeHttpResponse"))
                .and(takesArgument(0, named("io.micronaut.http.HttpRequest")))
                .and(takesArgument(1, named("io.micronaut.http.HttpResponse"))));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/ByteBufBsonDocumentInstrumentation.java:37
    result[630] = declaresField(named("byteBuf"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/MongoClient31Instrumentation.java:47
    result[632] = declaresField(named("commandListeners"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/MongoClient31Instrumentation.java:80
    result[633] = declaresField(named("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/MongoClient31Instrumentation.java:87
    result[634] = declaresField(named("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.4/src/main/java/datadog/trace/instrumentation/mongo/MongoClient34Instrumentation.java:59
    result[635] = declaresField(named("commandListeners"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.4/src/main/java/datadog/trace/instrumentation/mongo/MongoClient34Instrumentation.java:92
    result[636] = declaresField(named("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.4/src/main/java/datadog/trace/instrumentation/mongo/MongoClient34Instrumentation.java:99
    result[637] = declaresField(named("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.6/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection36Instrumentation.java:43
    result[638] =
        declaresMethod(
            isMethod()
                .and(named("command"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("org.bson.BsonDocument"))));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.6/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection36Instrumentation.java:50
    result[639] =
        declaresMethod(
            isMethod()
                .and(named("commandAsync"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("org.bson.BsonDocument"))));
  }

  private static void fill640(ElementMatcher.Junction<TypeDescription>[] result) {
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.8/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection38Instrumentation.java:43
    result[640] =
        declaresMethod(
            isMethod()
                .and(named("command"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("org.bson.BsonDocument"))));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.8/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection38Instrumentation.java:50
    result[641] =
        declaresMethod(
            isMethod()
                .and(named("commandAsync"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("org.bson.BsonDocument"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/BaseCluster410Instrumentation.java:25
    result[642] =
        declaresMethod(
            isMethod()
                .and(named("selectServerAsync"))
                .and(takesArgument(2, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/BaseClusterInstrumentation.java:25
    result[643] =
        declaresMethod(
            isMethod()
                .and(named("selectServerAsync"))
                .and(takesArgument(1, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/DefaultConnectionPool410Instrumentation.java:25
    result[644] =
        declaresMethod(
            isMethod()
                .and(named("getAsync"))
                .and(takesArgument(1, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/DefaultConnectionPoolInstrumentation.java:25
    result[645] =
        declaresMethod(
            isMethod()
                .and(named("getAsync"))
                .and(takesArgument(0, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:27
    result[646] =
        declaresMethod(
            isMethod()
                .and(named("readAsync"))
                .and(takesArgument(1, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:34
    result[647] =
        declaresMethod(
            isMethod()
                .and(named("readAsync"))
                .and(takesArgument(2, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:44
    result[648] =
        declaresMethod(
            isMethod()
                .and(named("openAsync"))
                .and(takesArgument(0, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:49
    result[649] =
        declaresMethod(
            isMethod()
                .and(named("writeAsync"))
                .and(takesArgument(1, named("com.mongodb.internal.async.SingleResultCallback"))));
    // mule-4.5/src/main/java/datadog/trace/instrumentation/mule4/ComponentMessageProcessorInstrumentation.java:36
    result[650] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("onEvent", "onEventSynchronous", "prepareAndExecuteOperation"))
                .and(takesArgument(0, named("org.mule.runtime.core.api.event.CoreEvent"))));
    // mule-4.5/src/main/java/datadog/trace/instrumentation/mule4/EventTracerInstrumentation.java:26
    result[652] = declaresMethod(named("updateSelectedCoreEventTracer"));
    // mule-4.5/src/main/java/datadog/trace/instrumentation/mule4/ExecutionInitialSpanInfoInstrumentation.java:27
    result[653] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("org.mule.runtime.api.component.Component"))));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/ChannelFutureListenerInstrumentation.java:71
    result[654] =
        declaresMethod(
            isMethod()
                .and(named("operationComplete"))
                .and(takesArgument(0, named("org.jboss.netty.channel.ChannelFuture"))));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/NettyChannelInstrumentation.java:53
    result[655] =
        declaresMethod(
            isMethod()
                .and(named("connect"))
                .and(returns(named("org.jboss.netty.channel.ChannelFuture"))));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/NettyChannelPipelineInstrumentation.java:79
    result[656] =
        declaresMethod(
            isMethod()
                .and(nameStartsWith("add"))
                .and(takesArgument(1, named("org.jboss.netty.channel.ChannelHandler"))));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/NettyChannelPipelineInstrumentation.java:84
    result[657] =
        declaresMethod(
            isMethod()
                .and(nameStartsWith("add"))
                .and(takesArgument(2, named("org.jboss.netty.channel.ChannelHandler"))));
    // netty/netty-4.0/src/main/java/datadog/trace/instrumentation/netty40/ChannelFutureListenerInstrumentation.java:47
    result[658] =
        declaresMethod(
            isMethod()
                .and(named("operationComplete"))
                .and(takesArgument(0, named("io.netty.channel.ChannelFuture"))));
    // netty/netty-4.0/src/main/java/datadog/trace/instrumentation/netty40/NettyChannelHandlerContextInstrumentation.java:48
    result[659] = declaresMethod(isMethod().and(nameStartsWith("fire")).and(isPublic()));
    // netty/netty-4.0/src/main/java/datadog/trace/instrumentation/netty40/NettyChannelPipelineInstrumentation.java:81
    result[660] =
        declaresMethod(
            isMethod().and(named("connect")).and(returns(named("io.netty.channel.ChannelFuture"))));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/ChannelFutureListenerInstrumentation.java:65
    result[661] =
        declaresMethod(
            isMethod()
                .and(named("operationComplete"))
                .and(takesArgument(0, named("io.netty.channel.ChannelFuture"))));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/HttpPostRequestDecoderInstrumentation.java:69
    result[663] = declaresMethod(named("parseBody").and(takesArguments(0)).and(isPrivate()));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/NettyChannelHandlerContextInstrumentation.java:65
    result[664] = declaresMethod(isMethod().and(nameStartsWith("fire")).and(isPublic()));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/NettyChannelPipelineInstrumentation.java:117
    result[665] =
        declaresMethod(
            isMethod().and(named("connect")).and(returns(named("io.netty.channel.ChannelFuture"))));
    // netty/netty-buffer-4.0/src/main/java/datadog/trace/instrumentation/netty40/buffer/ByteBufInputStreamInstrumentation.java:40
    result[666] =
        declaresMethod(
            isConstructor()
                .and(isPublic())
                .and(takesArguments(3))
                .and(takesArgument(0, named("io.netty.buffer.ByteBuf")))
                .and(takesArgument(1, named("int")))
                .and(takesArgument(2, named("boolean"))));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/NettyPromiseTaskInstrumentation.java:72
    result[667] = declaresMethod(named("runTask"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/NettyPromiseTaskInstrumentation.java:74
    result[668] = declaresMethod(named("runTask"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/NettyPromiseTaskInstrumentation.java:90
    result[669] = declaresMethod(isMethod().and(named("runTask")).and(takesArguments(0)));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorInstrumentation.java:64
    result[670] = declaresField(named("taskQueue"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorInstrumentation.java:71
    result[671] = declaresField(named("delayedTaskQueue"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorInstrumentation.java:78
    result[672] = declaresField(named("scheduledTaskQueue"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorStartThreadInstrumentation.java:37
    result[673] =
        declaresMethod(
            named("startThread")
                .and(takesArguments(0))
                .and(returns(named("void")))
                .and(isPrivate()));
    // netty/netty-promise-4.0/src/main/java/datadog/trace/instrumentation/netty4/promise/NettyPromiseInstrumentation.java:33
    result[674] =
        declaresMethod(
            named("addListener")
                .and(takesArgument(0, named("io.netty.util.concurrent.GenericFutureListener"))));
    // netty/netty-promise-4.0/src/main/java/datadog/trace/instrumentation/netty4/promise/NettyPromiseInstrumentation.java:37
    result[675] =
        declaresMethod(
            named("addListeners")
                .and(takesArgument(0, named("io.netty.util.concurrent.GenericFutureListener[]"))));
    // ognl-appsec-3.3.2/src/main/java/datadog/trace/instrumentation/ognl/OgnlInstrumentation.java:31
    result[676] =
        declaresMethod(
            isMethod()
                .and(named("parseExpression"))
                .and(isStatic())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/AppSecHttpEngineInstrumentation.java:33
    result[677] = declaresMethod(isMethod().and(named("sendRequest")).and(takesArguments(0)));
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/IastHttpUrlInstrumentation.java:48
    result[678] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("parse"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/IastHttpUrlInstrumentation.java:55
    result[679] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("get"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.net.URL"))));
  }

  private static void fill680(ElementMatcher.Junction<TypeDescription>[] result) {
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/IastHttpUrlInstrumentation.java:62
    result[680] = declaresMethod(isMethod().and(named("url")).and(takesArguments(0)));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/AppSecHttpEngineInstrumentation.java:33
    result[682] = declaresMethod(isMethod().and(named("sendRequest")).and(takesArguments(0)));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/IastHttpUrlInstrumentation.java:43
    result[683] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("parse"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/IastHttpUrlInstrumentation.java:50
    result[684] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("get"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.net.URL"))));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/IastHttpUrlInstrumentation.java:57
    result[685] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("get"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/OkHttp3Instrumentation.java:30
    result[686] =
        declaresMethod(
            isConstructor().and(takesArgument(0, named("okhttp3.OkHttpClient$Builder"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceAsyncInstrumentation.java:37
    result[687] =
        declaresMethod(
            isMethod()
                .and(named("create"))
                .and(
                    takesArgument(
                        0, named("com.openai.models.chat.completions.ChatCompletionCreateParams")))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceAsyncInstrumentation.java:46
    result[688] =
        declaresMethod(
            isMethod()
                .and(named("createStreaming"))
                .and(
                    takesArgument(
                        0, named("com.openai.models.chat.completions.ChatCompletionCreateParams")))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceAsyncInstrumentation.java:58
    result[689] = declaresField(named("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceInstrumentation.java:36
    result[690] =
        declaresMethod(
            isMethod()
                .and(named("create"))
                .and(
                    takesArgument(
                        0, named("com.openai.models.chat.completions.ChatCompletionCreateParams")))
                .and(returns(named("com.openai.core.http.HttpResponseFor"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceInstrumentation.java:45
    result[691] =
        declaresMethod(
            isMethod()
                .and(named("createStreaming"))
                .and(
                    takesArgument(
                        0, named("com.openai.models.chat.completions.ChatCompletionCreateParams")))
                .and(returns(named("com.openai.core.http.HttpResponseFor"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceInstrumentation.java:57
    result[692] = declaresField(named("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceAsyncInstrumentation.java:36
    result[693] =
        declaresMethod(
            isMethod()
                .and(named("create"))
                .and(
                    takesArgument(0, named("com.openai.models.completions.CompletionCreateParams")))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceAsyncInstrumentation.java:43
    result[694] =
        declaresMethod(
            isMethod()
                .and(named("createStreaming"))
                .and(
                    takesArgument(0, named("com.openai.models.completions.CompletionCreateParams")))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceAsyncInstrumentation.java:53
    result[695] = declaresField(named("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceInstrumentation.java:35
    result[696] =
        declaresMethod(
            isMethod()
                .and(named("create"))
                .and(
                    takesArgument(0, named("com.openai.models.completions.CompletionCreateParams")))
                .and(returns(named("com.openai.core.http.HttpResponseFor"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceInstrumentation.java:42
    result[697] =
        declaresMethod(
            isMethod()
                .and(named("createStreaming"))
                .and(
                    takesArgument(0, named("com.openai.models.completions.CompletionCreateParams")))
                .and(returns(named("com.openai.core.http.HttpResponseFor"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceInstrumentation.java:52
    result[698] = declaresField(named("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/EmbeddingServiceInstrumentation.java:34
    result[699] =
        declaresMethod(
            isMethod()
                .and(named("create"))
                .and(takesArgument(0, named("com.openai.models.embeddings.EmbeddingCreateParams")))
                .and(returns(named("com.openai.core.http.HttpResponseFor"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/EmbeddingServiceInstrumentation.java:44
    result[700] = declaresField(named("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceAsyncInstrumentation.java:37
    result[701] =
        declaresMethod(
            isMethod()
                .and(named("create"))
                .and(takesArgument(0, named("com.openai.models.responses.ResponseCreateParams")))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceAsyncInstrumentation.java:44
    result[702] =
        declaresMethod(
            isMethod()
                .and(named("createStreaming"))
                .and(takesArgument(0, named("com.openai.models.responses.ResponseCreateParams")))
                .and(returns(named("java.util.concurrent.CompletableFuture"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceAsyncInstrumentation.java:54
    result[703] = declaresField(named("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceInstrumentation.java:36
    result[704] =
        declaresMethod(
            isMethod()
                .and(named("create"))
                .and(takesArgument(0, named("com.openai.models.responses.ResponseCreateParams")))
                .and(returns(named("com.openai.core.http.HttpResponseFor"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceInstrumentation.java:43
    result[705] =
        declaresMethod(
            isMethod()
                .and(named("createStreaming"))
                .and(takesArgument(0, named("com.openai.models.responses.ResponseCreateParams")))
                .and(returns(named("com.openai.core.http.HttpResponseFor"))));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceInstrumentation.java:53
    result[706] = declaresField(named("clientOptions"));
    // opensearch/opensearch-rest-1.0/src/main/java/datadog/trace/instrumentation/opensearch/OpensearchRestClientInstrumentation.java:48
    result[707] =
        declaresMethod(
            isMethod()
                .and(named("performRequest"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.opensearch.client.Request"))));
    // opensearch/opensearch-rest-1.0/src/main/java/datadog/trace/instrumentation/opensearch/OpensearchRestClientInstrumentation.java:54
    result[708] =
        declaresMethod(
            isMethod()
                .and(named("performRequestAsync"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.opensearch.client.Request")))
                .and(takesArgument(1, named("org.opensearch.client.ResponseListener"))));
    // opensearch/opensearch-transport-1.0/src/main/java/datadog/trace/instrumentation/opensearch/OpensearchTransportClientInstrumentation.java:55
    result[709] =
        declaresMethod(
            isMethod()
                .and(named("execute"))
                .and(takesArguments(3))
                .and(takesArgument(0, named("org.opensearch.action.ActionType")))
                .and(takesArgument(1, named("org.opensearch.action.ActionRequest")))
                .and(takesArgument(2, named("org.opensearch.action.ActionListener"))));
    // opensearch/opensearch-transport-1.0/src/main/java/datadog/trace/instrumentation/opensearch/ThreadedActionListenerInstrumentation.java:48
    result[711] = declaresMethod(namedOneOf("onResponse", "onFailure").and(takesArguments(1)));
    // opentelemetry/opentelemetry-0.3/src/main/java/datadog/trace/instrumentation/opentelemetry/OpenTelemetryInstrumentation.java:35
    result[712] =
        declaresMethod(
            named("getTracerProvider")
                .and(returns(named("io.opentelemetry.trace.TracerProvider"))));
    // opentelemetry/opentelemetry-0.3/src/main/java/datadog/trace/instrumentation/opentelemetry/OpenTelemetryInstrumentation.java:38
    result[713] =
        declaresMethod(
            named("getPropagators")
                .and(returns(named("io.opentelemetry.context.propagation.ContextPropagators"))));
    // opentelemetry/opentelemetry-1.27/src/main/java/datadog/trace/instrumentation/opentelemetry127/OpenTelemetryLogsInstrumentation.java:64
    result[714] =
        declaresMethod(
            isMethod()
                .and(named("getLogsBridge"))
                .and(takesArguments(0))
                .and(returns(named("io.opentelemetry.api.logs.LoggerProvider"))));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/OpenTelemetryInstrumentation.java:60
    result[715] =
        declaresMethod(
            isMethod()
                .and(named("getTracerProvider"))
                .and(takesArguments(0))
                .and(returns(named("io.opentelemetry.api.trace.TracerProvider"))));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/OpenTelemetryInstrumentation.java:67
    result[716] =
        declaresMethod(
            isMethod()
                .and(named("getPropagators"))
                .and(takesArguments(0))
                .and(returns(named("io.opentelemetry.context.propagation.ContextPropagators"))));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/context/OpenTelemetryContextInstrumentation.java:57
    result[717] =
        declaresMethod(
            isMethod()
                .and(named("root"))
                .and(takesArguments(0))
                .and(returns(named("io.opentelemetry.context.Context"))));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/context/OpenTelemetryContextStorageInstrumentation.java:58
    result[718] =
        declaresMethod(
            isMethod()
                .and(named("current"))
                .and(takesArguments(0))
                .and(returns(named("io.opentelemetry.context.Context"))));
    // opentelemetry/opentelemetry-1.47/src/main/java/datadog/trace/instrumentation/opentelemetry147/OpenTelemetryMetricsInstrumentation.java:65
    result[719] =
        declaresMethod(
            isMethod()
                .and(named("getMeterProvider"))
                .and(takesArguments(0))
                .and(returns(named("io.opentelemetry.api.metrics.MeterProvider"))));
  }

  private static void fill720(ElementMatcher.Junction<TypeDescription>[] result) {
    // opentelemetry/opentelemetry-annotations-1.20/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/WithSpanAnnotationInstrumentation.java:38
    result[720] =
        declaresMethod(
            isAnnotatedWith(named("io.opentelemetry.instrumentation.annotations.WithSpan")));
    // opentelemetry/opentelemetry-annotations-1.26/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/AddingSpanAttributesInstrumentation.java:37
    result[721] =
        declaresMethod(
            isAnnotatedWith(
                named("io.opentelemetry.instrumentation.annotations.AddingSpanAttributes")));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONArrayInstrumentation.java:39
    result[722] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(0))
                .and(takesArgument(0, named("org.json.JSONTokener"))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONArrayInstrumentation.java:42
    result[723] =
        declaresMethod(
            isMethod().and(isPublic()).and(returns(named("java.lang.Object"))).and(named("opt")));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONCookieInstrumentation.java:34
    result[724] =
        declaresMethod(
            named("toJSONObject")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObject20241224Instrumentation.java:50
    result[725] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.json.JSONTokener")))
                .and(takesArgument(1, named("org.json.JSONParserConfiguration"))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObject20241224Instrumentation.java:58
    result[726] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.util.Map")))
                .and(takesArgument(1, named("int")))
                .and(takesArgument(2, named("org.json.JSONParserConfiguration"))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObject20241224Instrumentation.java:65
    result[727] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(returns(named("java.lang.Object")))
                .and(named("opt"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObjectInstrumentation.java:51
    result[728] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.json.JSONTokener"))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObjectInstrumentation.java:55
    result[729] =
        declaresMethod(
            isConstructor().and(takesArguments(1)).and(takesArgument(0, named("java.util.Map"))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObjectInstrumentation.java:58
    result[730] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(returns(named("java.lang.Object")))
                .and(named("opt"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONTokenerInstrumentation.java:35
    result[731] =
        declaresMethod(
            isConstructor()
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.io.Reader")))));
    // osgi-4.3/src/main/java/datadog/trace/instrumentation/osgi43/BundleReferenceInstrumentation.java:50
    result[732] =
        declaresMethod(
            isMethod()
                .and(named("getResource"))
                .and(takesArguments(1).and(takesArgument(0, named("java.lang.String")))));
    // osgi-4.3/src/main/java/datadog/trace/instrumentation/osgi43/BundleReferenceInstrumentation.java:55
    result[733] =
        declaresMethod(
            isMethod()
                .and(named("getResourceAsStream"))
                .and(takesArguments(1).and(takesArgument(0, named("java.lang.String")))));
    // osgi-4.3/src/main/java/datadog/trace/instrumentation/osgi43/BundleReferenceInstrumentation.java:60
    result[734] =
        declaresMethod(
            isMethod()
                .and(named("loadClass"))
                .and(
                    takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))
                        .or(
                            takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("boolean"))))));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoActorCellInstrumentation.java:43
    result[735] = declaresMethod(isMethod().and(named("invoke")));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoForkJoinExecutorTaskInstrumentation.java:46
    result[737] =
        declaresMethod(isConstructor().and(takesArgument(0, named("java.lang.Runnable"))));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoForkJoinExecutorTaskInstrumentation.java:49
    result[738] = declaresMethod(isMethod().and(named("run")));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoMailboxInstrumentation.java:31
    result[739] = declaresMethod(isMethod().and(named("run")));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoRoutedActorCellInstrumentation.java:40
    result[740] =
        declaresMethod(
            isMethod()
                .and(
                    named("sendMessage")
                        .and(takesArgument(0, named("org.apache.pekko.dispatch.Envelope")))));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoSchedulerInstrumentation.java:45
    result[741] =
        declaresMethod(
            isMethod()
                .and(nameEndsWith("schedule"))
                .and(takesArgument(0, named("scala.concurrent.ExecutionContext")))
                .and(takesArgument(1, named("java.lang.Runnable")))
                .and(takesArgument(2, named("scala.concurrent.duration.FiniteDuration"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttp2ServerInstrumentation.java:43
    result[742] =
        declaresMethod(
            takesArguments(8)
                .and(named("bindAndHandleAsync"))
                .and(takesArgument(0, named("scala.Function1")))
                .and(takesArgument(7, named("org.apache.pekko.stream.Materializer"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttp2ServerInstrumentation.java:49
    result[743] =
        declaresMethod(
            takesArguments(7)
                .and(named("bindAndHandleAsync"))
                .and(takesArgument(0, named("scala.Function1")))
                .and(takesArgument(6, named("org.apache.pekko.stream.Materializer"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttp2ServerInstrumentation.java:55
    result[744] =
        declaresMethod(
            takesArguments(6)
                .and(named("bindAndHandleAsync"))
                .and(takesArgument(0, named("scala.Function1")))
                .and(takesArgument(5, named("org.apache.pekko.stream.Materializer"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttpServerInstrumentation.java:66
    result[745] =
        declaresMethod(
            named("bindAndHandle")
                .and(takesArgument(0, named("org.apache.pekko.stream.scaladsl.Flow"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoPoolMasterActorInstrumentation.java:28
    result[746] =
        declaresMethod(
            named("org$apache$pekko$http$impl$engine$client$PoolMasterActor$$startPoolInterface"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/CookieHeaderInstrumentation.java:48
    result[747] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("cookies"))
                .and(returns(named("scala.collection.immutable.Seq")))
                .and(takesArguments(0)));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/FormDataInstrumentation.java:39
    result[748] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("fields"))
                .and(takesArguments(0))
                .and(returns(named("org.apache.pekko.http.scaladsl.model.Uri$Query"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/FormFieldDirectivesInstrumentation.java:56
    result[749] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("formField").or(named("formFields")))
                .and(returns(named("java.lang.Object")))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.apache.pekko.http.scaladsl.server.directives.FormFieldDirectives")))
                .and(
                    takesArgument(
                        1,
                        named(
                            "org.apache.pekko.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/FormFieldDirectivesInstrumentation.java:74
    result[750] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("formField").or(named("formFields")))
                .and(
                    returns(named("java.lang.Object"))
                        .or(returns(named("org.apache.pekko.http.scaladsl.server.Directive"))))
                .and(takesArguments(1))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.apache.pekko.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/HttpHeaderSubclassesInstrumentation.java:56
    result[751] =
        declaresMethod(
            isMethod()
                .and(named("value"))
                .and(takesArguments(0))
                .and(returns(named("java.lang.String"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/HttpRequestInstrumentation.java:50
    result[752] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("headers"))
                .and(returns(named("scala.collection.immutable.Seq")))
                .and(takesArguments(0)));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/HttpRequestInstrumentation.java:58
    result[753] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(not(isStatic()))
                .and(named("entity"))
                .and(takesArguments(0)));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/MarshallingDirectivesInstrumentation.java:47
    result[754] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("entity"))
                .and(returns(named("org.apache.pekko.http.scaladsl.server.Directive")))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.apache.pekko.http.scaladsl.server.directives.MarshallingDirectives")))
                .and(
                    takesArgument(
                        1, named("org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/MarshallingDirectivesInstrumentation.java:64
    result[755] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("entity"))
                .and(returns(named("org.apache.pekko.http.scaladsl.server.Directive")))
                .and(takesArguments(1))
                .and(
                    takesArgument(
                        1, named("org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesImplInstrumentation.java:42
    result[756] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("filter"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.lang.String")))
                .and(
                    takesArgument(
                        1, named("org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller")))
                .and(returns(named("org.apache.pekko.http.scaladsl.server.Directive"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesImplInstrumentation.java:55
    result[757] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("repeatedFilter"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.lang.String")))
                .and(
                    takesArgument(
                        1, named("org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller")))
                .and(returns(named("org.apache.pekko.http.scaladsl.server.Directive"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesInstrumentation.java:55
    result[758] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("parameter").or(named("parameters")))
                .and(returns(named("java.lang.Object")))
                .and(takesArguments(2))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.apache.pekko.http.scaladsl.server.directives.ParameterDirectives")))
                .and(
                    takesArgument(
                        1,
                        named(
                            "org.apache.pekko.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesInstrumentation.java:73
    result[759] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("parameter").or(named("parameters")))
                .and(
                    returns(named("java.lang.Object"))
                        .or(returns(named("org.apache.pekko.http.scaladsl.server.Directive"))))
                .and(takesArguments(1))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.apache.pekko.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"))));
  }

  private static void fill760(ElementMatcher.Junction<TypeDescription>[] result) {
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/PathMatcherInstrumentation.java:40
    result[760] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(3))
                .and(takesArgument(0, named("org.apache.pekko.http.scaladsl.model.Uri$Path")))
                .and(takesArgument(1, named("java.lang.Object")))
                .and(takesArgument(2, named("org.apache.pekko.http.scaladsl.server.util.Tuple"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/RequestContextInstrumentation.java:39
    result[761] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("request"))
                .and(returns(named("org.apache.pekko.http.scaladsl.model.HttpRequest")))
                .and(takesArguments(0)));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UnmarshallerInstrumentation.java:55
    result[762] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("apply"))
                .and(returns(named("scala.concurrent.Future")))
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.lang.Object")))
                .and(takesArgument(1, named("scala.concurrent.ExecutionContext")))
                .and(takesArgument(2, named("org.apache.pekko.stream.Materializer"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UriInstrumentation.java:44
    result[763] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("queryString"))
                .and(returns(named("scala.Option")))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.nio.charset.Charset"))));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UriInstrumentation.java:52
    result[764] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("rawQueryString"))
                .and(returns(named("scala.Option")))
                .and(takesArguments(0)));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UriInstrumentation.java:59
    result[765] =
        declaresMethod(
            isMethod()
                .and(not(isStatic()))
                .and(named("query"))
                .and(returns(named("org.apache.pekko.http.scaladsl.model.Uri$Query")))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.nio.charset.Charset")))
                .and(
                    takesArgument(
                        1, named("org.apache.pekko.http.scaladsl.model.Uri$ParsingMode"))));
    // play/play-2.3/src/main/java/datadog/trace/instrumentation/play23/PlayInstrumentation.java:47
    result[766] =
        declaresMethod(
            named("apply")
                .and(takesArgument(0, named("play.api.mvc.Request")))
                .and(returns(named("scala.concurrent.Future"))));
    // play/play-2.4/src/main/java/datadog/trace/instrumentation/play24/PlayInstrumentation.java:49
    result[767] =
        declaresMethod(
            named("apply")
                .and(takesArgument(0, named("play.api.mvc.Request")))
                .and(returns(named("scala.concurrent.Future"))));
    // play/play-2.6/src/main/java/datadog/trace/instrumentation/play26/PlayInstrumentation.java:53
    result[768] =
        declaresMethod(
            named("apply")
                .and(takesArgument(0, named("play.api.mvc.Request")))
                .and(returns(named("scala.concurrent.Future"))));
    // play/play-2.6/src/main/java/datadog/trace/instrumentation/play26/SaveRawRemoteConnectionInstrumentation.java:35
    result[769] =
        declaresMethod(
            named("forwardedConnection")
                .and(not(isStatic()))
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.api.mvc.request.RemoteConnection")))
                .and(takesArgument(1, named("play.api.mvc.Headers")))
                .and(returns(named("play.api.mvc.request.RemoteConnection"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/DelegatingBodyParserInstrumentation.java:32
    result[770] =
        declaresMethod(
            named("apply")
                .and(takesArguments(1))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(returns(named("play.libs.streams.Accumulator"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/FormUrlEncodedInstrumentation.java:33
    result[771] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(takesArgument(1, named("akka.util.ByteString")))
                .and(returns(named("java.util.Map"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/HttpErrorHandlerInstrumentation.java:41
    result[772] =
        declaresMethod(
            isPublic()
                .and(named("onServerError"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.api.mvc.RequestHeader")))
                .and(takesArgument(1, named("java.lang.Throwable")))
                .and(returns(named("scala.concurrent.Future"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PathPatternInstrumentation.java:44
    result[773] =
        declaresMethod(
            named("apply")
                .and(not(isStatic()))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("scala.Option"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:40
    result[774] =
        declaresMethod(
            named("tolerantText")
                .and(not(isStatic()))
                .and(takesArguments(1))
                .and(takesArgument(0, named("long")))
                .and(returns(named("play.api.mvc.BodyParser"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:47
    result[775] =
        declaresMethod(
            named("tolerantJson")
                .and(not(isStatic()))
                .and(takesArguments(1))
                .and(takesArgument(0, named("int")))
                .and(returns(named("play.api.mvc.BodyParser"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:54
    result[776] =
        declaresMethod(
            named("tolerantFormUrlEncoded")
                .and(not(isStatic()))
                .and(takesArguments(1))
                .and(takesArgument(0, named("int")))
                .and(returns(named("play.api.mvc.BodyParser"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:61
    result[777] =
        declaresMethod(
            named("multipartFormData")
                .and(not(isStatic()))
                .and(takesArguments(2))
                .and(takesArgument(0, named("scala.Function1")))
                .and(takesArgument(1, named("long")))
                .and(returns(named("play.api.mvc.BodyParser"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/ResultsStatusInstrumentation.java:30
    result[778] = declaresMethod(named("apply"));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/RoutingDslInstrumentation.java:45
    result[779] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(5))
                .and(takesArgument(3, named("java.lang.Object")))
                .and(takesArgument(4, named("java.lang.reflect.Method"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/SirdPathExtractorInstrumentation.java:36
    result[780] =
        declaresMethod(
            namedOneOf("extract", "play$api$routing$sird$PathExtractor$$extract")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("scala.Option"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/StatusHeaderInstrumentation.java:31
    result[781] =
        declaresMethod(
            named("sendJson")
                .and(takesArgument(0, named("com.fasterxml.jackson.databind.JsonNode"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/TolerantJsonInstrumentation.java:37
    result[782] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(takesArgument(1, named("akka.util.ByteString")))
                .and(returns(named("com.fasterxml.jackson.databind.JsonNode"))));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/TolerantTextInstrumentation.java:32
    result[783] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(takesArgument(1, named("akka.util.ByteString")))
                .and(returns(named("java.lang.String"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/DelegatingBodyParserInstrumentation.java:45
    result[784] =
        declaresMethod(
            named("apply")
                .and(takesArguments(1))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(returns(named("play.libs.streams.Accumulator"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/FormUrlEncodedInstrumentation.java:39
    result[785] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(takesArgument(1, named("akka.util.ByteString")))
                .and(returns(named("java.util.Map"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/HttpErrorHandlerInstrumentation.java:47
    result[786] =
        declaresMethod(
            isPublic()
                .and(named("onServerError"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.api.mvc.RequestHeader")))
                .and(takesArgument(1, named("java.lang.Throwable")))
                .and(returns(named("scala.concurrent.Future"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/PathPatternInstrumentation.java:60
    result[787] =
        declaresMethod(
            named("apply")
                .and(not(isStatic()))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("scala.Option"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/ResultsStatusInstrumentation.java:42
    result[788] = declaresMethod(named("apply"));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/RoutingDslInstrumentation.java:67
    result[789] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(5))
                .and(takesArgument(3, named("java.lang.Object")))
                .and(takesArgument(4, named("java.lang.reflect.Method"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/SirdPathExtractorInstrumentation.java:50
    result[790] =
        declaresMethod(
            named("extract")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("scala.Option"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/StatusHeaderInstrumentation.java:45
    result[791] =
        declaresMethod(
            named("sendJson")
                .and(takesArgument(0, named("com.fasterxml.jackson.databind.JsonNode"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/TolerantJsonInstrumentation.java:48
    result[792] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(takesArgument(1, named("akka.util.ByteString")))
                .and(returns(named("com.fasterxml.jackson.databind.JsonNode"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/TolerantTextInstrumentation.java:45
    result[793] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(takesArgument(1, named("akka.util.ByteString")))
                .and(returns(named("java.lang.String"))));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/TolerantXmlInstrumentation.java:45
    result[794] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("play.mvc.Http$RequestHeader")))
                .and(takesArgument(1, named("akka.util.ByteString")))
                .and(returns(named("org.w3c.dom.Document"))));
    // play/play-appsec-2.7/src/main/java/datadog/trace/instrumentation/play27/appsec/RoutingDsl27Instrumentation.java:60
    result[795] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(5))
                .and(takesArgument(3, named("java.lang.Object")))
                .and(takesArgument(4, named("java.lang.reflect.Method"))));
    // quartz-2.0/src/main/java/datadog/trace/instrumentation/quartz/QuartzSchedulingInstrumentation.java:44
    result[796] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("execute"))
                .and(takesArgument(0, named("org.quartz.JobExecutionContext"))));
    // r2dbc/r2dbc-1.0/src/main/java/datadog/trace/instrumentation/r2dbc/R2dbcConnectionInstrumentation.java:50
    result[797] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("createStatement"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // r2dbc/r2dbc-1.0/src/main/java/datadog/trace/instrumentation/r2dbc/R2dbcInstrumentation.java:27
    result[798] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("find"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.r2dbc.spi.ConnectionFactoryOptions"))));
    // rabbitmq-amqp-2.7/src/main/java/datadog/trace/instrumentation/rabbitmq/amqp/RabbitChannelInstrumentation.java:106
    result[799] =
        declaresMethod(
            isMethod().and(named("basicGet")).and(takesArgument(0, named("java.lang.String"))));
  }

  private static void fill800(ElementMatcher.Junction<TypeDescription>[] result) {
    // rabbitmq-amqp-2.7/src/main/java/datadog/trace/instrumentation/rabbitmq/amqp/RabbitChannelInstrumentation.java:109
    result[800] =
        declaresMethod(
            isMethod()
                .and(named("basicConsume"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(6, named("com.rabbitmq.client.Consumer"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ContextParseInstrumentation.java:35
    result[802] =
        declaresMethod(
            named("parse")
                .and(takesArguments(2))
                .and(takesArgument(0, named("ratpack.http.TypedData")))
                .and(takesArgument(1, named("ratpack.parse.Parse"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ContinuationInstrumentation.java:39
    result[803] =
        declaresMethod(named("resume").and(takesArgument(0, named("ratpack.func.Block"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/DefaultExecutionInstrumentation.java:33
    result[804] =
        declaresMethod(
            nameStartsWith("delimit")
                .and(takesArgument(0, named("ratpack.func.Action")))
                .and(takesArgument(1, named("ratpack.func.Action"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/JsonRendererInstrumentation.java:36
    result[805] =
        declaresMethod(
            isMethod()
                .and(named("render"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("ratpack.handling.Context")))
                .and(takesArgument(1, named("ratpack.jackson.JsonRender"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/PathHandlerInstrumentation.java:45
    result[806] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(2))
                .and(takesArgument(0, named("ratpack.path.PathBinder")))
                .and(takesArgument(1, named("ratpack.handling.Handler"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/RatpackRequestBodyInstrumentation.java:28
    result[807] = declaresMethod(named("readStream").and(takesArguments(0)));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/RatpackTypedDataInstrumentation.java:34
    result[808] =
        declaresMethod(
            named("getBuffer")
                .and(takesArguments(0))
                .or(named("getBytes").and(takesArguments(0)))
                .or(
                    named("writeTo")
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.io.OutputStream")))))
                .or(named("getInputStream").and(takesArguments(0))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/RatpackTypedDataInstrumentation.java:41
    result[809] =
        declaresMethod(
            named("getText")
                .and(
                    takesArguments(0)
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.nio.charset.Charset"))))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ServerErrorHandlerInstrumentation.java:34
    result[810] =
        declaresMethod(
            named("error")
                .and(takesArgument(0, named("ratpack.handling.Context")))
                .and(takesArgument(1, named("java.lang.Throwable"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ServerRegistryInstrumentation.java:26
    result[811] = declaresMethod(isMethod().and(isStatic()).and(named("buildBaseRegistry")));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/PublisherInstrumentation.java:46
    result[812] = declaresMethod(isMethod().and(named("subscribe")));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/SubscriberInstrumentation.java:30
    result[813] = declaresMethod(isMethod().and(namedOneOf("onNext", "onError", "onComplete")));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/SubscriberInstrumentation.java:35
    result[814] = declaresMethod(isMethod().and(namedOneOf("onNext", "onError")));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/SubscriberInstrumentation.java:38
    result[815] = declaresMethod(isMethod().and(named("onComplete")));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/BlockingPublisherInstrumentation.java:40
    result[816] = declaresMethod(isMethod().and(nameStartsWith("block")));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/BlockingPublisherInstrumentation.java:45
    result[817] = declaresMethod(isMethod().and(nameStartsWith("block")));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/ContextWritingSubscriberInstrumentation.java:44
    result[819] = declaresMethod(namedOneOf("onNext", "onComplete", "onError"));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/CorePublisherInstrumentation.java:46
    result[820] = declaresMethod(named("subscribe"));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/CorePublisherInstrumentation.java:51
    result[821] =
        declaresMethod(
            named("subscribe")
                .and(not(isStatic()))
                .and(takesArguments(1))
                .and(takesArgument(0, named("reactor.core.CoreSubscriber"))));
    // reactor-netty-1.0/src/main/java/datadog/trace/instrumentation/reactor/netty/HttpClientInstrumentation.java:60
    result[822] =
        declaresMethod(isMethod().and(isStatic()).and(namedOneOf("create", "newConnection")));
    // rediscala-1.8/src/main/java/datadog/trace/instrumentation/rediscala/RedisClientActorInstrumentation.java:43
    result[823] = declaresMethod(isMethod().and(named("onConnect")));
    // rediscala-1.8/src/main/java/datadog/trace/instrumentation/rediscala/RediscalaInstrumentation.java:66
    result[824] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("redis.RedisCommand")))
                .and(returns(named("scala.concurrent.Future"))));
    // redisson/redisson-2.0.0/src/main/java/datadog/trace/instrumentation/redisson/RedissonInstrumentation.java:38
    result[825] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("org.redisson.client.protocol.CommandData"))));
    // redisson/redisson-2.0.0/src/main/java/datadog/trace/instrumentation/redisson/RedissonInstrumentation.java:45
    result[826] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("org.redisson.client.protocol.CommandsData"))));
    // redisson/redisson-2.3.0/src/main/java/datadog/trace/instrumentation/redisson23/RedissonInstrumentation.java:38
    result[827] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("org.redisson.client.protocol.CommandData"))));
    // redisson/redisson-2.3.0/src/main/java/datadog/trace/instrumentation/redisson23/RedissonInstrumentation.java:45
    result[828] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("org.redisson.client.protocol.CommandsData"))));
    // redisson/redisson-3.10.3/src/main/java/datadog/trace/instrumentation/redisson30/RedissonInstrumentation.java:39
    result[829] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("org.redisson.client.protocol.CommandData"))));
    // redisson/redisson-3.10.3/src/main/java/datadog/trace/instrumentation/redisson30/RedissonInstrumentation.java:46
    result[830] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("org.redisson.client.protocol.CommandsData"))));
    // renaissance-0.7/src/main/java/datadog/trace/instrumentation/renaissance/RenaissanceInstrumentation.java:35
    result[831] =
        declaresMethod(
            isMethod().and(named("executeOperation")).and(takesArgument(0, named("int"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:36
    result[832] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCheckedSupplier"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("io.github.resilience4j.core.functions.CheckedSupplier"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:43
    result[833] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCheckedFunction"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("io.github.resilience4j.core.functions.CheckedFunction"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:50
    result[834] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCheckedConsumer"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("io.github.resilience4j.core.functions.CheckedConsumer"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:57
    result[835] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCompletionStage"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("java.util.function.Supplier"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:64
    result[836] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateFuture"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("java.util.function.Supplier"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:71
    result[837] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateConsumer"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("java.util.function.Consumer"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:78
    result[838] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCheckedRunnable"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("io.github.resilience4j.core.functions.CheckedRunnable"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:85
    result[839] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCallable"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("java.util.concurrent.Callable"))));
  }

  private static void fill840(ElementMatcher.Junction<TypeDescription>[] result) {
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:92
    result[840] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateRunnable"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("java.lang.Runnable"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:99
    result[841] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateSupplier"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("java.util.function.Supplier"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:106
    result[842] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateFunction"))
                .and(
                    takesArgument(0, named("io.github.resilience4j.circuitbreaker.CircuitBreaker")))
                .and(returns(named("java.util.function.Function"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackCallableInstrumentation.java:21
    result[843] = declaresMethod(isMethod().and(named("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackCheckedSupplierInstrumentation.java:20
    result[844] = declaresMethod(isMethod().and(named("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackCompletionStageInstrumentation.java:22
    result[845] = declaresMethod(isMethod().and(named("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackSupplierInstrumentation.java:21
    result[846] = declaresMethod(isMethod().and(named("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:32
    result[847] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCompletionStage"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("java.util.function.Supplier"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:39
    result[848] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCheckedSupplier"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("io.github.resilience4j.core.functions.CheckedSupplier"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:46
    result[849] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCheckedRunnable"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("io.github.resilience4j.core.functions.CheckedRunnable"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:53
    result[850] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCallable"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("java.util.concurrent.Callable"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:60
    result[851] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateSupplier"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("java.util.function.Supplier"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:67
    result[852] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateFunction"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("java.util.function.Function"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:74
    result[853] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateCheckedFunction"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("io.github.resilience4j.core.functions.CheckedFunction"))));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:81
    result[854] =
        declaresMethod(
            isMethod()
                .and(isStatic())
                .and(named("decorateRunnable"))
                .and(takesArgument(0, named("io.github.resilience4j.retry.Retry")))
                .and(returns(named("java.lang.Runnable"))));
    // resilience4j/resilience4j-reactor-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerOperatorInstrumentation.java:24
    result[855] =
        declaresMethod(
            isMethod()
                .and(named("apply"))
                .and(takesArgument(0, named("org.reactivestreams.Publisher"))));
    // resilience4j/resilience4j-reactor-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackOperatorInstrumentation.java:26
    result[856] =
        declaresMethod(
            isMethod()
                .and(named("decorate"))
                .and(
                    takesArgument(0, named("java.util.function.UnaryOperator"))
                        .and(returns(named("java.util.function.Function")))));
    // resilience4j/resilience4j-reactor-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryOperatorInstrumentation.java:24
    result[857] =
        declaresMethod(
            isMethod()
                .and(named("apply"))
                .and(takesArgument(0, named("org.reactivestreams.Publisher"))));
    // resteasy/resteasy-3.0/src/main/java/datadog/trace/instrumentation/connection_error/resteasy/ResteasyClientConnectionErrorInstrumentation.java:37
    result[858] = declaresMethod(isMethod().and(isPublic()).and(named("invoke")));
    // resteasy/resteasy-3.0/src/main/java/datadog/trace/instrumentation/connection_error/resteasy/ResteasyClientConnectionErrorInstrumentation.java:41
    result[859] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("submit"))
                .and(returns(named("java.util.concurrent.Future"))));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/CookieParamInjectorInstrumentation.java:26
    result[860] = declaresMethod(named("inject").and(isPublic()).and(takesArguments(2)));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/DecodedFormParametersInstrumentation.java:57
    result[861] = declaresMethod(named("getDecodedFormParameters").and(takesArguments(0)));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/FormParamInjectorInstrumentation.java:26
    result[862] = declaresMethod(named("inject").and(isPublic()).and(takesArguments(2)));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/HeaderParamInjectorInstrumentation.java:26
    result[863] = declaresMethod(named("inject").and(isPublic()).and(takesArguments(2)));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/JavaxWSResponseInstrumentation.java:31
    result[864] =
        declaresMethod(
            named("header")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("java.lang.Object"))))));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/JavaxWSResponseInstrumentation.java:34
    result[865] =
        declaresMethod(
            named("location")
                .and(
                    isPublic()
                        .and(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.net.URI"))))));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/MessageBodyReaderInvocationInstrumentation.java:44
    result[866] =
        declaresMethod(
            named("readFrom")
                .and(takesArguments(1))
                .and(takesArgument(0, nameEndsWith(".MessageBodyReader"))));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/MethodExpressionInstrumentation.java:46
    result[867] =
        declaresMethod(
            named("populatePathParams")
                .and(takesArguments(3))
                .and(takesArgument(0, named("org.jboss.resteasy.spi.HttpRequest")))
                .and(takesArgument(1, named("java.util.regex.Matcher")))
                .and(takesArgument(2, named("java.lang.String"))));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/MultipartFormDataReaderInstrumentation.java:45
    result[868] =
        declaresMethod(
            named("readFrom")
                .and(takesArguments(6))
                .and(
                    returns(
                        named(
                            "org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput"))));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/PathParamInjectorInstrumentation.java:26
    result[869] = declaresMethod(named("inject").and(isPublic()).and(takesArguments(2)));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/QueryParamInjectorInstrumentation.java:26
    result[870] = declaresMethod(named("inject").and(isPublic()).and(takesArguments(2)));
    // restlet-2.2/src/main/java/datadog/trace/instrumentation/restlet/RouteInstrumentation.java:32
    result[871] =
        declaresMethod(
            isMethod()
                .and(named("beforeHandle"))
                .and(takesArgument(0, named("org.restlet.Request")))
                .and(takesArgument(1, named("org.restlet.Response"))));
    // robolectric-4.13/src/main/java/datadog/trace/instrumentation/robolectric/RobolectricInstrumentation.java:40
    result[872] = declaresMethod(named("setUpApplicationState"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/AbstractRequestContextInstrumentation.java:32
    result[873] =
        declaresMethod(
            isMethod()
                .and(named("abortWith"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("jakarta.ws.rs.core.Response"))));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/ContainerRequestFilterInstrumentation.java:42
    result[874] =
        declaresMethod(
            isMethod()
                .and(named("filter"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("jakarta.ws.rs.container.ContainerRequestContext"))));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAnnotationsInstrumentation.java:75
    result[875] = declaresMethod(isAnnotatedWith(named("jakarta.ws.rs.Path")));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAsyncResponseInstrumentation.java:48
    result[876] =
        declaresMethod(
            named("resume").and(takesArgument(0, named("java.lang.Object"))).and(isPublic()));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAsyncResponseInstrumentation.java:51
    result[877] =
        declaresMethod(
            named("resume").and(takesArgument(0, named("java.lang.Throwable"))).and(isPublic()));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAsyncResponseInstrumentation.java:54
    result[878] = declaresMethod(named("cancel"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/MessageBodyWriterInstrumentation.java:46
    result[879] = declaresMethod(named("writeTo").and(takesArguments(7)));
  }

  private static void fill880(ElementMatcher.Junction<TypeDescription>[] result) {
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-1.1.1/src/main/java/datadog/trace/instrumentation/jaxrs1/JaxRsAnnotationsInstrumentation.java:73
    result[880] = declaresMethod(isAnnotatedWith(named("javax.ws.rs.Path")));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/AbstractRequestContextInstrumentation.java:39
    result[881] =
        declaresMethod(
            isMethod()
                .and(named("abortWith"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("javax.ws.rs.core.Response"))));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/ContainerRequestFilterInstrumentation.java:42
    result[882] =
        declaresMethod(
            isMethod()
                .and(named("filter"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("javax.ws.rs.container.ContainerRequestContext"))));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAnnotationsInstrumentation.java:83
    result[883] = declaresMethod(isAnnotatedWith(named("javax.ws.rs.Path")));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAsyncResponseInstrumentation.java:48
    result[884] =
        declaresMethod(
            named("resume").and(takesArgument(0, named("java.lang.Object"))).and(isPublic()));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAsyncResponseInstrumentation.java:51
    result[885] =
        declaresMethod(
            named("resume").and(takesArgument(0, named("java.lang.Throwable"))).and(isPublic()));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAsyncResponseInstrumentation.java:54
    result[886] = declaresMethod(named("cancel"));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/MessageBodyWriterInstrumentation.java:51
    result[887] = declaresMethod(named("writeTo").and(takesArguments(7)));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/CompletableInstrumentation.java:30
    result[889] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.CompletableObserver"))));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/FlowableInstrumentation.java:30
    result[891] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.reactivestreams.Subscriber"))));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/MaybeInstrumentation.java:29
    result[893] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.MaybeObserver"))));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/ObservableInstrumentation.java:29
    result[895] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.Observer"))));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/SingleInstrumentation.java:30
    result[897] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.SingleObserver"))));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/CompletableInstrumentation.java:30
    result[899] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.rxjava3.core.CompletableObserver"))));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/FlowableInstrumentation.java:30
    result[901] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.rxjava3.core.FlowableSubscriber"))));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/MaybeInstrumentation.java:29
    result[903] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.rxjava3.core.MaybeObserver"))));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/ObservableInstrumentation.java:29
    result[905] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.rxjava3.core.Observer"))));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/SingleInstrumentation.java:30
    result[907] =
        declaresMethod(
            isMethod()
                .and(named("subscribe"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.reactivex.rxjava3.core.SingleObserver"))));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinPoolInstrumentation.java:28
    result[908] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("doSubmit", "externalPush"))
                .and(takesArgument(0, named("scala.concurrent.forkjoin.ForkJoinTask"))));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:44
    result[909] = declaresMethod(namedOneOf("exec", "fork", "cancel"));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:50
    result[910] = declaresMethod(isMethod().and(namedOneOf("doExec", "exec")));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:52
    result[911] = declaresMethod(isMethod().and(named("fork")));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:53
    result[912] = declaresMethod(isMethod().and(named("cancel")));
    // scala/scala-promise/scala-promise-2.10/src/main/java/datadog/trace/instrumentation/scala210/concurrent/CallbackRunnableInstrumentation.java:31
    result[914] = declaresMethod(isMethod().and(named("run")));
    // scala/scala-promise/scala-promise-2.10/src/main/java/datadog/trace/instrumentation/scala210/concurrent/CallbackRunnableInstrumentation.java:32
    result[915] = declaresMethod(isMethod().and(named("executeWithValue")));
    // scala/scala-promise/scala-promise-2.10/src/main/java/datadog/trace/instrumentation/scala210/concurrent/PromiseObjectInstrumentation.java:33
    result[916] =
        declaresMethod(isMethod().and(named("scala$concurrent$impl$Promise$$resolveTry")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/DefaultPromiseInstrumentation.java:33
    result[917] = declaresMethod(isMethod().and(named("tryComplete0")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseObjectInstrumentation.java:33
    result[918] = declaresMethod(isMethod().and(named("scala$concurrent$impl$Promise$$resolve")));
  }

  private static void fill920(ElementMatcher.Junction<TypeDescription>[] result) {
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseTransformationInstrumentation.java:33
    result[920] = declaresMethod(isMethod().and(named("submitWithValue")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseTransformationInstrumentation.java:35
    result[921] = declaresMethod(isMethod().and(named("run")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseTransformationInstrumentation.java:36
    result[922] = declaresMethod(isMethod().and(named("cancel")));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestForkInstrumentation.java:35
    result[923] =
        declaresMethod(named("runner").and(takesArgument(1, named("java.lang.String[]"))));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestInstrumentation.java:34
    result[924] =
        declaresMethod(
            named("apply")
                .and(takesArguments(1))
                .and(takesArgument(0, named("org.scalatest.events.Event"))));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestInstrumentation.java:39
    result[925] = declaresMethod(named("fireReadyEvents"));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestSkipInstrumentation.java:41
    result[926] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(2, named("org.scalatest.Filter")))
                .and(takesArgument(5, named("org.scalatest.Tracker"))));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestSkipInstrumentation.java:47
    result[927] =
        declaresMethod(
            named("apply")
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("scala.collection.immutable.Map")))
                .and(takesArgument(2, named("java.lang.String"))));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestSkipInstrumentation.java:54
    result[928] =
        declaresMethod(
            named("apply")
                .and(takesArguments(3))
                .and(takesArgument(0, named("scala.collection.immutable.Set")))
                .and(takesArgument(1, named("scala.collection.immutable.Map")))
                .and(takesArgument(2, named("java.lang.String"))));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/execution/ScalatestExecutionInstrumentation.java:54
    result[929] =
        declaresMethod(
            named("runTestImpl")
                .and(takesArgument(0, named("org.scalatest.Suite")))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("org.scalatest.Args"))));
    // selenium-3.13/src/main/java/datadog/trace/instrumentation/selenium/SeleniumInstrumentation.java:42
    result[931] =
        declaresMethod(
            named("get")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // selenium-3.13/src/main/java/datadog/trace/instrumentation/selenium/SeleniumInstrumentation.java:45
    result[932] = declaresMethod(named("close").and(takesArguments(0)));
    // selenium-3.13/src/main/java/datadog/trace/instrumentation/selenium/SeleniumInstrumentation.java:48
    result[933] = declaresMethod(named("quit").and(takesArguments(0)));
    // servicetalk/servicetalk-0.42.0/src/main/java/datadog/trace/instrumentation/servicetalk0_42_0/ContextMapInstrumentation.java:29
    result[934] =
        declaresMethod(
            isConstructor()
                .and(isPrivate())
                .and(takesArguments(1))
                .and(
                    takesArgument(
                        0,
                        named(
                            "io.servicetalk.concurrent.api.CopyOnWriteContextMap$CopyContextMap"))));
    // servicetalk/servicetalk-0.42.0/src/main/java/datadog/trace/instrumentation/servicetalk0_42_0/ContextPreservingInstrumentation.java:38
    result[935] =
        declaresMethod(
            namedOneOf(
                "accept",
                "apply",
                "call",
                "cancel",
                "onComplete",
                "onError",
                "onSuccess",
                "request",
                "onNext",
                "onSubscribe",
                "run"));
    // servicetalk/servicetalk-0.42.56/src/main/java/datadog/trace/instrumentation/servicetalk0_42_56/CapturedContextProvidersInstrumentation.java:29
    result[936] = declaresMethod(named("loadProviders"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/IastJakartaServletInstrumentation.java:48
    result[937] =
        declaresMethod(
            isMethod()
                .and(named("service"))
                .and(isPublic())
                .and(takesArguments(2))
                .and(takesArgument(0, named("jakarta.servlet.ServletRequest")))
                .and(takesArgument(1, named("jakarta.servlet.ServletResponse"))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/IastOptOutJakartaHttpServletRequestInstrumentation.java:63
    result[938] =
        declaresMethod(
            named("getSession")
                .and(returns(named("jakarta.servlet.http.HttpSession")))
                .and(isPublic()));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:61
    result[939] =
        declaresMethod(
            isMethod()
                .and(named("getHeader"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:64
    result[940] =
        declaresMethod(
            isMethod()
                .and(named("getHeaders"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:67
    result[941] = declaresMethod(isMethod().and(named("getHeaderNames")).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:70
    result[942] =
        declaresMethod(
            isMethod()
                .and(named("getParameter"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:73
    result[943] =
        declaresMethod(
            isMethod()
                .and(named("getParameterValues"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:76
    result[944] = declaresMethod(isMethod().and(named("getParameterMap")).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:79
    result[945] = declaresMethod(isMethod().and(named("getParameterNames")).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:82
    result[946] = declaresMethod(isMethod().and(named("getCookies")).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:85
    result[947] = declaresMethod(isMethod().and(named("getQueryString")).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:88
    result[948] =
        declaresMethod(
            isMethod().and(namedOneOf("getInputStream", "getReader")).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:91
    result[949] =
        declaresMethod(
            isMethod()
                .and(named("getRequestDispatcher"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:52
    result[950] =
        declaresMethod(
            named("addCookie")
                .and(takesArguments(1))
                .and(takesArgument(0, named("jakarta.servlet.http.Cookie"))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:57
    result[951] =
        declaresMethod(
            namedOneOf("setHeader", "addHeader")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:60
    result[952] =
        declaresMethod(
            namedOneOf("encodeRedirectURL", "encodeURL")
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("java.lang.String"))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:65
    result[953] =
        declaresMethod(named("sendRedirect").and(takesArgument(0, named("java.lang.String"))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpSessionInstrumentation.java:39
    result[954] =
        declaresMethod(
            namedOneOf("setAttribute", "putValue")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.lang.Object")))
                        .and(isPublic())));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:46
    result[955] = declaresMethod(named("getName").and(isPublic()).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:49
    result[956] =
        declaresMethod(
            named("getHeader")
                .and(isPublic())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:52
    result[957] =
        declaresMethod(
            named("getHeaders")
                .and(isPublic())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:55
    result[958] = declaresMethod(named("getHeaderNames").and(isPublic()).and(takesArguments(0)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:58
    result[959] = declaresMethod(named("getInputStream").and(isPublic()).and(takesArguments(0)));
  }

  private static void fill960(ElementMatcher.Junction<TypeDescription>[] result) {
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaServletInstrumentation.java:53
    result[960] =
        declaresMethod(
            isMethod()
                .and(named("service"))
                .and(isPublic())
                .and(takesArguments(2))
                .and(takesArgument(0, named("jakarta.servlet.ServletRequest")))
                .and(takesArgument(1, named("jakarta.servlet.ServletResponse"))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/RumAsyncContextInstrumentation.java:44
    result[961] = declaresMethod(isMethod().and(namedOneOf("complete", "dispatch")));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/Servlet5RequestBodyInstrumentation.java:40
    result[962] =
        declaresMethod(
            named("getInputStream")
                .and(takesArguments(0))
                .and(returns(named("jakarta.servlet.ServletInputStream")))
                .and(isPublic()));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/Servlet5RequestBodyInstrumentation.java:46
    result[963] =
        declaresMethod(
            named("getReader")
                .and(takesArguments(0))
                .and(returns(named("java.io.BufferedReader")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/IastServlet2Instrumentation.java:60
    result[964] =
        declaresMethod(
            namedOneOf("doFilter", "service")
                .and(takesArgument(0, named("javax.servlet.ServletRequest")))
                .and(takesArgument(1, named("javax.servlet.ServletResponse")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/Servlet2Instrumentation.java:75
    result[965] =
        declaresMethod(
            namedOneOf("doFilter", "service")
                .and(takesArgument(0, named("javax.servlet.ServletRequest")))
                .and(takesArgument(1, named("javax.servlet.ServletResponse")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/Servlet2ResponseStatusInstrumentation.java:54
    result[966] =
        declaresMethod(namedOneOf("sendError", "setStatus").and(takesArgument(0, named("int"))));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/Servlet2ResponseStatusInstrumentation.java:57
    result[967] = declaresMethod(named("sendRedirect"));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/ServletRequestBodyInstrumentation.java:76
    result[968] =
        declaresMethod(
            named("getInputStream")
                .and(takesArguments(0))
                .and(returns(named("javax.servlet.ServletInputStream")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/ServletRequestBodyInstrumentation.java:82
    result[969] =
        declaresMethod(
            named("getReader")
                .and(takesArguments(0))
                .and(returns(named("java.io.BufferedReader")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/AsyncContextInstrumentation.java:56
    result[970] = declaresMethod(isMethod().and(isPublic()).and(named("dispatch")));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/IastOptOutHttpServletRequest3Instrumentation.java:66
    result[971] =
        declaresMethod(
            named("getSession")
                .and(returns(named("javax.servlet.http.HttpSession")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/IastServlet3Instrumentation.java:55
    result[972] =
        declaresMethod(
            namedOneOf("doFilter", "service")
                .and(takesArgument(0, named("javax.servlet.ServletRequest")))
                .and(takesArgument(1, named("javax.servlet.ServletResponse")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:50
    result[973] = declaresMethod(named("getName").and(isPublic()).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:53
    result[974] =
        declaresMethod(
            named("getHeader")
                .and(isPublic())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:56
    result[975] =
        declaresMethod(
            named("getHeaders")
                .and(isPublic())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:59
    result[976] = declaresMethod(named("getHeaderNames").and(isPublic()).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:62
    result[977] = declaresMethod(named("getInputStream").and(isPublic()).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/RumAsyncContextInstrumentation.java:49
    result[978] = declaresMethod(isMethod().and(namedOneOf("complete", "dispatch")));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/Servlet31RequestBodyInstrumentation.java:56
    result[979] =
        declaresMethod(
            named("getInputStream")
                .and(takesArguments(0))
                .and(returns(named("javax.servlet.ServletInputStream")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/Servlet31RequestBodyInstrumentation.java:62
    result[980] =
        declaresMethod(
            named("getReader")
                .and(takesArguments(0))
                .and(returns(named("java.io.BufferedReader")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/Servlet3Instrumentation.java:69
    result[981] =
        declaresMethod(
            namedOneOf("doFilter", "service")
                .and(takesArgument(0, named("javax.servlet.ServletRequest")))
                .and(takesArgument(1, named("javax.servlet.ServletResponse")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/dispatcher/RequestDispatcherInstrumentation.java:66
    result[982] =
        declaresMethod(
            namedOneOf("forward", "include", "error")
                .and(takesArguments(2))
                .and(takesArgument(0, named("javax.servlet.ServletRequest")))
                .and(takesArgument(1, named("javax.servlet.ServletResponse")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/dispatcher/ServletContextInstrumentation.java:44
    result[983] =
        declaresMethod(
            returns(named("javax.servlet.RequestDispatcher"))
                .and(takesArgument(0, named("java.lang.String")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/filter/FilterInstrumentation.java:49
    result[984] =
        declaresMethod(
            named("doFilter")
                .and(takesArgument(0, named("javax.servlet.ServletRequest")))
                .and(takesArgument(1, named("javax.servlet.ServletResponse")))
                .and(isPublic()));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/CookieInstrumentation.java:44
    result[985] = declaresMethod(isMethod().and(named("getName")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/CookieInstrumentation.java:47
    result[986] = declaresMethod(isMethod().and(named("getValue")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletInstrumentation.java:55
    result[987] =
        declaresMethod(
            named("service")
                .or(nameStartsWith("do"))
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest")))
                .and(takesArgument(1, named("javax.servlet.http.HttpServletResponse")))
                .and(isProtected().or(isPublic())));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:61
    result[988] =
        declaresMethod(
            isMethod()
                .and(named("getHeader"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:64
    result[989] =
        declaresMethod(
            isMethod()
                .and(named("getHeaders"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:67
    result[990] = declaresMethod(isMethod().and(named("getHeaderNames")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:70
    result[991] =
        declaresMethod(
            isMethod()
                .and(named("getParameter"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:73
    result[992] =
        declaresMethod(
            isMethod()
                .and(named("getParameterValues"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:76
    result[993] = declaresMethod(isMethod().and(named("getParameterMap")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:79
    result[994] = declaresMethod(isMethod().and(named("getParameterNames")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:82
    result[995] = declaresMethod(isMethod().and(named("getCookies")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:85
    result[996] = declaresMethod(isMethod().and(named("getQueryString")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:88
    result[997] =
        declaresMethod(
            isMethod().and(namedOneOf("getInputStream", "getReader")).and(takesArguments(0)));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:91
    result[998] =
        declaresMethod(
            isMethod()
                .and(named("getRequestDispatcher"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletResponseInstrumentation.java:44
    result[999] = declaresMethod(namedOneOf("sendError", "sendRedirect"));
  }

  private static void fill1000(ElementMatcher.Junction<TypeDescription>[] result) {
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:57
    result[1000] =
        declaresMethod(
            named("addCookie").and(takesArgument(0, named("javax.servlet.http.Cookie"))));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:60
    result[1001] =
        declaresMethod(
            namedOneOf("setHeader", "addHeader")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.lang.String")))));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:63
    result[1002] =
        declaresMethod(
            namedOneOf("encodeRedirectURL", "encodeURL")
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("java.lang.String"))));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:68
    result[1003] =
        declaresMethod(named("sendRedirect").and(takesArgument(0, named("java.lang.String"))));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpSessionInstrumentation.java:50
    result[1004] =
        declaresMethod(
            namedOneOf("setAttribute", "putValue")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.String")))
                        .and(takesArgument(1, named("java.lang.Object")))
                        .and(isPublic())));
    // slick-3.2/src/main/java/datadog/trace/instrumentation/slick/SlickRunnableInstrumentation.java:49
    result[1006] = declaresMethod(named("run").and(takesArguments(0)));
    // snakeyaml-1.33/src/main/java/datadog/trace/instrumentation/snakeyaml/SnakeYamlInstrumentation.java:52
    result[1007] =
        declaresMethod(
            named("load")
                .and(isMethod())
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.io.InputStream"))))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("java.io.Reader"))))));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/AbstractClusterInstrumentation.java:34
    result[1008] =
        declaresMethod(
            isMethod()
                .and(named("invoke"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("com.alipay.sofa.rpc.core.request.SofaRequest"))));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/BoltServerProcessorInstrumentation.java:21
    result[1009] =
        declaresMethod(
            isMethod()
                .and(named("handleRequest"))
                .and(takesArguments(3))
                .and(takesArgument(2, named("com.alipay.sofa.rpc.core.request.SofaRequest"))));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/H2cServerTaskInstrumentation.java:20
    result[1010] = declaresMethod(isMethod().and(named("run")).and(takesArguments(0)));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/ProviderProxyInvokerInstrumentation.java:33
    result[1011] =
        declaresMethod(
            isMethod()
                .and(named("invoke"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("com.alipay.sofa.rpc.core.request.SofaRequest"))));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/RestServerHandlerInstrumentation.java:20
    result[1012] = declaresMethod(isMethod().and(named("channelRead0")).and(takesArguments(2)));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/TripleServerInstrumentation.java:21
    result[1013] =
        declaresMethod(
            isMethod()
                .and(named("invoke"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("com.alipay.sofa.rpc.core.request.SofaRequest"))));
    // spark/sparkjava-2.3/src/main/java/datadog/trace/instrumentation/sparkjava/RoutesInstrumentation.java:38
    result[1014] =
        declaresMethod(
            named("find")
                .and(takesArgument(0, named("spark.route.HttpMethod")))
                .and(returns(named("spark.routematch.RouteMatch")))
                .and(isPublic()));
    // spring/spring-beans-3.1/src/main/java/datadog/trace/instrumentation/springbeans/BeanFactoryInstrumentation.java:47
    result[1015] =
        declaresMethod(
            isMethod()
                .and(named("registerBeanDefinition"))
                .and(
                    takesArgument(
                        1, named("org.springframework.beans.factory.config.BeanDefinition"))));
    // spring/spring-beans-3.1/src/main/java/datadog/trace/instrumentation/springbeans/BeanFactoryInstrumentation.java:53
    result[1016] =
        declaresMethod(
            isMethod()
                .and(named("resolveBeanClass"))
                .and(
                    takesArgument(
                        0, named("org.springframework.beans.factory.support.RootBeanDefinition"))));
    // spring/spring-boot-1.3/src/main/java/datadog/trace/instrumentation/springboot/SpringApplicationInstrumentation.java:34
    result[1017] =
        declaresMethod(
            named("environmentPrepared")
                .and(
                    takesArgument(
                        0, named("org.springframework.core.env.ConfigurableEnvironment"))));
    // spring/spring-boot-1.3/src/main/java/datadog/trace/instrumentation/springboot/SpringApplicationInstrumentation.java:39
    result[1018] =
        declaresMethod(
            named("environmentPrepared")
                .and(
                    takesArgument(
                        1, named("org.springframework.core.env.ConfigurableEnvironment"))));
    // spring/spring-boot-1.3/src/main/java/datadog/trace/instrumentation/springboot/SpringServletInitializerInstrumentation.java:25
    result[1019] = declaresMethod(named("onStartup").and(takesArguments(1)));
    // spring/spring-cloud-zuul-2.0/src/main/java/datadog/trace/instrumentation/springcloudzuul2/ZuulProxyRequestHelperInstrumentation.java:30
    result[1020] =
        declaresMethod(
            isMethod()
                .and(named("isIncludedHeader"))
                .and(takesArgument(0, named("java.lang.String"))));
    // spring/spring-cloud-zuul-2.0/src/main/java/datadog/trace/instrumentation/springcloudzuul2/ZuulSendForwardFilterInstrumentation.java:33
    result[1021] = declaresMethod(isMethod().and(named("run")).and(takesArguments(0)));
    // spring/spring-core-3.2.2/src/main/java/datadog/trace/instrumentation/springcore/StreamUtilsInstrumentation.java:32
    result[1022] =
        declaresMethod(
            isMethod()
                .and(named("copyToString"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.io.InputStream")))
                .and(takesArgument(1, named("java.nio.charset.Charset"))));
    // spring/spring-jms-3.1/src/main/java/datadog/trace/instrumentation/springjms/AbstractPollingMessageListenerContainerInstrumentation.java:42
    result[1024] =
        declaresMethod(
            named("receiveAndExecute").and(takesArgument(2, named("javax.jms.MessageConsumer"))));
    // spring/spring-messaging-4.0/src/main/java/datadog/trace/instrumentation/springmessaging/KotlinAwareHandlerInstrumentation.java:54
    result[1025] = declaresMethod(isMethod().and(named("doInvoke")));
    // spring/spring-rabbit-1.5/src/main/java/datadog/trace/instrumentation/springamqp/AbstractMessageListenerContainerInstrumentation.java:57
    result[1026] =
        declaresMethod(named("executeListener").and(takesArgument(1, named("java.lang.Object"))));
    // spring/spring-rabbit-1.5/src/main/java/datadog/trace/instrumentation/springamqp/BlockingQueueConsumerInstrumentation.java:32
    result[1027] =
        declaresMethod(
            named("handle")
                .and(takesArgument(0, named("org.springframework.amqp.rabbit.support.Delivery"))));
    // spring/spring-scheduling-3.1/src/main/java/datadog/trace/instrumentation/springscheduling/SpringAsyncInstrumentation.java:26
    result[1029] =
        declaresMethod(
            isMethod()
                .and(
                    named("invoke")
                        .and(
                            takesArgument(
                                0, named("org.aopalliance.intercept.MethodInvocation")))));
    // spring/spring-scheduling-3.1/src/main/java/datadog/trace/instrumentation/springscheduling/SpringSchedulingInstrumentation.java:32
    result[1030] =
        declaresMethod(isConstructor().and(takesArgument(0, named("java.lang.Runnable"))));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/AuthenticationManagerInstrumentation.java:40
    result[1031] =
        declaresMethod(
            isMethod()
                .and(named("authenticate"))
                .and(takesArgument(0, named("org.springframework.security.core.Authentication")))
                .and(returns(named("org.springframework.security.core.Authentication")))
                .and(isPublic()));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/SecurityContextHolderInstrumentation.java:40
    result[1032] =
        declaresMethod(
            isMethod()
                .and(named("setContext"))
                .and(takesArguments(1))
                .and(
                    takesArgument(
                        0, named("org.springframework.security.core.context.SecurityContext")))
                .and(isPublic()));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/SecurityContextHolderInstrumentation.java:49
    result[1033] =
        declaresMethod(
            isMethod().and(named("setDeferredContext")).and(takesArguments(1)).and(isPublic()));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/UserDetailsManagerInstrumentation.java:38
    result[1034] =
        declaresMethod(
            isMethod()
                .and(named("createUser"))
                .and(
                    takesArgument(
                        0, named("org.springframework.security.core.userdetails.UserDetails")))
                .and(isPublic()));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/UsernameNotFoundExceptionInstrumentation.java:36
    result[1035] =
        declaresMethod(
            isConstructor().and(takesArgument(0, named("java.lang.String"))).and(isPublic()));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/client/WebClientFilterInstrumentation.java:33
    result[1037] =
        declaresMethod(isMethod().and(isPublic()).and(named("filter").or(named("filters"))));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/DispatcherHandlerInstrumentation.java:33
    result[1038] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("handle"))
                .and(takesArgument(0, named("org.springframework.web.server.ServerWebExchange")))
                .and(takesArguments(1)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/DispatcherHandlerInstrumentation.java:41
    result[1039] =
        declaresMethod(
            isMethod()
                .and(named("handleResult"))
                .and(takesArgument(0, named("org.springframework.web.server.ServerWebExchange"))));
  }

  private static void fill1040(ElementMatcher.Junction<TypeDescription>[] result) {
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/HandlerAdapterInstrumentation.java:42
    result[1040] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("handle"))
                .and(takesArgument(0, named("org.springframework.web.server.ServerWebExchange")))
                .and(takesArgument(1, named("java.lang.Object")))
                .and(takesArguments(2)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/RouterFunctionInstrumentation.java:38
    result[1041] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("route"))
                .and(
                    takesArgument(
                        0, named("org.springframework.web.reactive.function.server.ServerRequest")))
                .and(takesArguments(1)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/AbstractServerHttpRequestInstrumentation.java:37
    result[1042] =
        declaresMethod(isMethod().and(isPublic()).and(named("getHeaders")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/DataBufferInstrumentation.java:35
    result[1043] = declaresMethod(isMethod().and(named("asInputStream")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:28
    result[1044] =
        declaresMethod(
            isMethod()
                .and(named("get"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.Object")))));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:31
    result[1045] =
        declaresMethod(
            isMethod()
                .and(named("getFirst"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.String")))));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:34
    result[1046] = declaresMethod(isMethod().and(named("toSingleValueMap")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:37
    result[1047] =
        declaresMethod(isMethod().and(named("readOnlyHttpHeaders")).and(takesArguments(1)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpMessageInstrumentation.java:45
    result[1048] =
        declaresMethod(isMethod().and(isPublic()).and(named("getHeaders")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/Json2TokenizerInstrumentation.java:35
    result[1049] =
        declaresMethod(
            isMethod()
                .and(named("apply").or(named("tokenize")))
                .and(takesArgument(0, named("org.springframework.core.io.buffer.DataBuffer")))
                .and(takesArguments(1))
                .and(returns(named("reactor.core.publisher.Flux"))));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ReactorServerHttpRequestInstrumentation.java:36
    result[1050] =
        declaresMethod(isMethod().and(isPublic()).and(named("initHeaders")).and(takesArguments(1)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ReadOnlyHttpHeadersInstrumentation.java:30
    result[1051] =
        declaresMethod(
            isMethod()
                .and(named("get"))
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(1)
                        .and(takesArgument(0, named("java.lang.Object")))));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/RequestHeaderMapResolverInstrumentation.java:34
    result[1052] =
        declaresMethod(
            isMethod()
                .and(named("resolveArgumentValue"))
                .and(takesArgument(0, named("org.springframework.core.MethodParameter")))
                .and(takesArgument(1, named("org.springframework.web.reactive.BindingContext")))
                .and(takesArgument(2, named("org.springframework.web.server.ServerWebExchange")))
                .and(takesArguments(3)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerHttpRequestInstrumentation.java:37
    result[1053] = declaresMethod(isMethod().and(named("getQueryParams")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerHttpRequestInstrumentation.java:40
    result[1054] = declaresMethod(isMethod().and(named("getCookies")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerHttpRequestInstrumentation.java:43
    result[1055] = declaresMethod(isMethod().and(named("getBody")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerServletHttpRequestInstrumentation.java:36
    result[1056] =
        declaresMethod(isMethod().and(isPublic()).and(named("getHeaders")).and(takesArguments(0)));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/TemplateAndMatrixVariablesInstrumentation.java:28
    result[1057] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("handleMatch"))
                .and(
                    takesArgument(
                        0,
                        named("org.springframework.web.reactive.result.method.RequestMappingInfo")))
                .and(takesArgument(1, named("org.springframework.web.method.HandlerMethod")))
                .and(takesArgument(2, named("org.springframework.web.server.ServerWebExchange")))
                .and(takesArguments(3)));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/AppSecDispatcherServletInstrumentation.java:52
    result[1058] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("onRefresh"))
                .and(takesArgument(0, named("org.springframework.context.ApplicationContext")))
                .and(takesArguments(1)));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/DispatcherServletInstrumentation.java:43
    result[1059] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("onRefresh"))
                .and(takesArgument(0, named("org.springframework.context.ApplicationContext")))
                .and(takesArguments(1)));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/DispatcherServletInstrumentation.java:50
    result[1060] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("render"))
                .and(takesArgument(0, named("org.springframework.web.servlet.ModelAndView"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/DispatcherServletInstrumentation.java:56
    result[1061] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(nameStartsWith("processHandlerException"))
                .and(takesArgument(3, named("java.lang.Exception"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HandlerAdapterInstrumentation.java:51
    result[1062] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(nameStartsWith("handle"))
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest")))
                .and(takesArguments(3)));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HandlerMethodReturnValueHandlerCompositeInstrumentation.java:32
    result[1063] = declaresMethod(isMethod().and(named("selectHandler")));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:64
    result[1064] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("read"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.lang.Class")))
                .and(takesArgument(1, named("org.springframework.http.HttpInputMessage"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:72
    result[1065] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("read"))
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.lang.reflect.Type")))
                .and(takesArgument(1, named("java.lang.Class")))
                .and(takesArgument(2, named("org.springframework.http.HttpInputMessage"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:82
    result[1066] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("write"))
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.lang.Object")))
                .and(takesArgument(1, named("org.springframework.http.MediaType")))
                .and(takesArgument(2, named("org.springframework.http.HttpOutputMessage"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:92
    result[1067] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("write"))
                .and(takesArguments(4))
                .and(takesArgument(0, named("java.lang.Object")))
                .and(takesArgument(1, named("java.lang.reflect.Type")))
                .and(takesArgument(2, named("org.springframework.http.MediaType")))
                .and(takesArgument(3, named("org.springframework.http.HttpOutputMessage"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/InvocableHandlerMethodInstrumentation.java:33
    result[1068] = declaresMethod(named("invokeForRequest"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/SpringBeanProcessorInstrumentation.java:33
    result[1069] =
        declaresMethod(
            isMethod()
                .and(named("processBean"))
                .and(
                    takesArgument(
                        3, named("org.springframework.beans.factory.config.BeanDefinition"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/TemplateAndMatrixVariablesInstrumentation.java:69
    result[1070] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("handleMatch"))
                .and(
                    takesArgument(
                        0, named("org.springframework.web.servlet.mvc.method.RequestMappingInfo")))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("javax.servlet.http.HttpServletRequest")))
                .and(takesArguments(3)));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/TemplateVariablesUrlHandlerInstrumentation.java:68
    result[1071] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("preHandle"))
                .and(takesArguments(3))
                .and(takesArgument(0, named("javax.servlet.http.HttpServletRequest")))
                .and(takesArgument(1, named("javax.servlet.http.HttpServletResponse")))
                .and(takesArgument(2, named("java.lang.Object"))));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/WebApplicationContextInstrumentation.java:42
    result[1072] =
        declaresMethod(
            isMethod()
                .and(named("postProcessBeanFactory"))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"))));
    // spring/spring-webmvc/spring-webmvc-5.3/src/main/java/datadog/trace/instrumentation/springweb/AppSecDispatcherServletWithPathPatternsInstrumentation.java:46
    result[1073] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("onRefresh"))
                .and(takesArgument(0, named("org.springframework.context.ApplicationContext")))
                .and(takesArguments(1)));
    // spring/spring-webmvc/spring-webmvc-5.3/src/main/java/datadog/trace/instrumentation/springweb/ServletPathRequestFilterInstrumentation.java:49
    result[1074] =
        declaresMethod(
            isMethod()
                .and(named("postProcessBeanFactory"))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"))));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/DispatcherServletInstrumentation.java:37
    result[1075] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("getHandler"))
                .and(takesArgument(0, named("jakarta.servlet.http.HttpServletRequest")))
                .and(takesArguments(1))
                .and(returns(named("org.springframework.web.servlet.HandlerExecutionChain"))));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/DispatcherServletInstrumentation.java:45
    result[1076] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("render"))
                .and(takesArgument(0, named("org.springframework.web.servlet.ModelAndView"))));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/DispatcherServletInstrumentation.java:51
    result[1077] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(nameStartsWith("processHandlerException"))
                .and(takesArgument(3, named("java.lang.Exception"))));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/HandlerAdapterInstrumentation.java:44
    result[1078] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(nameStartsWith("handle"))
                .and(takesArgument(0, named("jakarta.servlet.http.HttpServletRequest")))
                .and(takesArguments(3)));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/InvocableHandlerMethodInstrumentation.java:23
    result[1079] = declaresMethod(named("invokeForRequest"));
  }

  private static void fill1080(ElementMatcher.Junction<TypeDescription>[] result) {
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/ResourceNameFilterMappingInstrumentation.java:50
    result[1080] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("onRefresh"))
                .and(takesArgument(0, named("org.springframework.context.ApplicationContext")))
                .and(takesArguments(1)));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/ServletPathRequestFilterInstrumentation.java:61
    result[1081] =
        declaresMethod(
            isMethod()
                .and(named("postProcessBeanFactory"))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"))));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/TemplateAndMatrixVariablesInstrumentation.java:51
    result[1082] =
        declaresMethod(
            isMethod()
                .and(isProtected())
                .and(named("handleMatch"))
                .and(
                    takesArgument(
                        0, named("org.springframework.web.servlet.mvc.method.RequestMappingInfo")))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("jakarta.servlet.http.HttpServletRequest")))
                .and(takesArguments(3)));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/TemplateVariablesUrlHandlerInstrumentation.java:51
    result[1083] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("preHandle"))
                .and(takesArguments(3))
                .and(takesArgument(0, named("jakarta.servlet.http.HttpServletRequest")))
                .and(takesArgument(1, named("jakarta.servlet.http.HttpServletResponse")))
                .and(takesArgument(2, named("java.lang.Object"))));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/WebApplicationContextInstrumentation.java:57
    result[1084] =
        declaresMethod(
            isMethod()
                .and(named("postProcessBeanFactory"))
                .and(
                    takesArgument(
                        0,
                        named(
                            "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"))));
    // spring/spring-ws-2.0/src/main/java/datadog/trace/instrumentation/springws2/MethodEndpointInstrumentation.java:29
    result[1085] = declaresMethod(isMethod().and(named("invoke")));
    // spymemcached-2.10/src/main/java/datadog/trace/instrumentation/spymemcached/MemcachedClientInstrumentation.java:59
    result[1086] = declaresMethod(isMethod().and(isPublic()).and(namedOneOf("incr", "decr")));
    // spymemcached-2.10/src/main/java/datadog/trace/instrumentation/spymemcached/MemcachedConnectionInstrumentation.java:28
    result[1087] =
        declaresMethod(
            isMethod()
                .and(named("addOperation"))
                .and(isProtected())
                .and(takesArgument(0, named("net.spy.memcached.MemcachedNode"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientInstrumentation.java:54
    result[1088] =
        declaresMethod(
            isMethod()
                .and(named("responseReceived"))
                .and(takesArgument(0, named("org.apache.http.nio.NHttpClientConnection"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientInstrumentation.java:59
    result[1089] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("closed", "exception", "timeout"))
                .and(takesArgument(0, named("org.apache.http.nio.NHttpClientConnection"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientWorkerInstrumentation.java:39
    result[1090] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        2, named("org.apache.synapse.transport.passthru.TargetResponse"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientWorkerInstrumentation.java:43
    result[1091] = declaresMethod(isMethod().and(named("run")).and(takesArguments(0)));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapsePassthruInstrumentation.java:38
    result[1092] =
        declaresMethod(
            isMethod()
                .and(named("submit"))
                .and(takesArgument(0, named("org.apache.axis2.context.MessageContext"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerInstrumentation.java:47
    result[1093] =
        declaresMethod(
            isMethod()
                .and(named("responseReady"))
                .and(takesArgument(0, named("org.apache.http.nio.NHttpServerConnection"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerInstrumentation.java:52
    result[1094] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("closed", "exception", "timeout"))
                .and(takesArgument(0, named("org.apache.http.nio.NHttpServerConnection"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerWorkerInstrumentation.java:40
    result[1095] =
        declaresMethod(
            isConstructor()
                .and(
                    takesArgument(
                        0, named("org.apache.synapse.transport.passthru.SourceRequest"))));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerWorkerInstrumentation.java:44
    result[1096] = declaresMethod(isMethod().and(named("run")).and(takesArguments(0)));
    // testng/testng-6.4/src/main/java/datadog/trace/instrumentation/testng6/TestNGClassListenerInstrumentation.java:46
    result[1097] =
        declaresMethod(
            named("invokeBeforeClassMethods")
                .and(takesArgument(0, named("org.testng.ITestClass")))
                .and(takesArgument(1, named("org.testng.IMethodInstance"))));
    // testng/testng-6.4/src/main/java/datadog/trace/instrumentation/testng6/TestNGClassListenerInstrumentation.java:52
    result[1098] =
        declaresMethod(
            named("invokeAfterClassMethods")
                .and(takesArgument(0, named("org.testng.ITestClass")))
                .and(takesArgument(1, named("org.testng.IMethodInstance"))));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGClassListenerInstrumentation.java:41
    result[1099] =
        declaresMethod(
            named("invokeBeforeClassMethods")
                .and(takesArgument(0, named("org.testng.ITestClass")))
                .and(takesArgument(1, named("org.testng.IMethodInstance"))));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGClassListenerInstrumentation.java:47
    result[1100] =
        declaresMethod(
            named("invokeAfterClassMethods")
                .and(takesArgument(0, named("org.testng.ITestClass")))
                .and(takesArgument(1, named("org.testng.IMethodInstance"))));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGExecutionInstrumentation.java:44
    result[1101] =
        declaresMethod(
            named("shouldRetryTestMethod").and(takesArgument(1, named("org.testng.ITestResult"))));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGExecutionInstrumentation.java:48
    result[1102] =
        declaresMethod(
            named("runTestResultListener").and(takesArgument(0, named("org.testng.ITestResult"))));
    // testng/testng-common/src/main/java/datadog/trace/instrumentation/testng/TestNGSkipInstrumentation.java:44
    result[1103] =
        declaresMethod(
            named("invokeMethod")
                .and(takesArguments(3))
                .and(takesArgument(0, named("java.lang.reflect.Method")))
                .and(takesArgument(1, named("java.lang.Object")))
                .and(takesArgument(2, named("java.lang.Object[]"))));
    // thymeleaf-3.0/src/main/java/datadog/trace/instrumentation/thymeleaf/ElementTagStructureHandlerInstrumentation.java:28
    result[1104] =
        declaresMethod(
            isMethod()
                .and(named("setBody"))
                .and(takesArgument(0, named("java.lang.CharSequence"))));
    // thymeleaf-3.0/src/main/java/datadog/trace/instrumentation/thymeleaf/StandardUtextTagProcessorInstrumentation.java:22
    result[1105] = declaresMethod(isMethod().and(named("doProcess")));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/JobInstrumentation.java:28
    result[1106] = declaresMethod(named("callProcess"));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/TaskInstrumentation.java:35
    result[1107] = declaresMethod(named("eval"));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/TaskInstrumentation.java:36
    result[1108] = declaresMethod(named("handleError"));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/ThreadPoolInstrumentation.java:27
    result[1109] = declaresMethod(named("schedule"));
    // tibco-businessworks/tibco-businessworks-6.5/src/main/java/datadog/trace/instrumentation/tibcobw6/BehaviorInstrumentation.java:67
    result[1110] = declaresMethod(isMethod().and(named("handleModelEvent")));
    // tibco-businessworks/tibco-businessworks-6.5/src/main/java/datadog/trace/instrumentation/tibcobw6/CallbackHandlerInstrumentation.java:35
    result[1111] = declaresMethod(isMethod().and(named("onMessage")));
    // tibco-businessworks/tibco-businessworks-6.5/src/main/java/datadog/trace/instrumentation/tibcobw6/ProcessInstrumentation.java:41
    result[1113] = declaresMethod(isMethod().and(named("createInstance")));
    // tinylog-2.0/src/main/java/datadog/trace/instrumentation/tinylog2/LogEntryInstrumentation.java:45
    result[1114] = declaresMethod(isMethod().and(named("getContext")).and(takesArguments(0)));
    // tinylog-2.0/src/main/java/datadog/trace/instrumentation/tinylog2/TinylogLoggingProviderInstrumentation.java:41
    result[1115] =
        declaresMethod(
            isMethod()
                .and(isPrivate())
                .and(named("output"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.tinylog.core.LogEntry"))));
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/ContainerBaseInstrumentation.java:28
    result[1116] = declaresMethod(isMethod().and(named("setName")));
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/RequestInstrumentation.java:71
    result[1117] = declaresMethod(named("recycle").and(takesArguments(0)));
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/ResponseInstrumentation.java:48
    result[1118] = declaresMethod(named("recycle").and(takesArguments(0)));
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/TomcatServerInstrumentation.java:92
    result[1119] =
        declaresMethod(
            named("postParseRequest")
                .and(takesArgument(0, named("org.apache.coyote.Request")))
                .and(takesArgument(1, named("org.apache.catalina.connector.Request")))
                .and(takesArgument(2, named("org.apache.coyote.Response")))
                .and(takesArgument(3, named("org.apache.catalina.connector.Response"))));
  }

  private static void fill1120(ElementMatcher.Junction<TypeDescription>[] result) {
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/WsHttpUpgradeHandlerInstrumentation.java:42
    result[1121] = declaresMethod(named("init"));
    // tomcat/tomcat-9.0/src/main/java/datadog/trace/instrumentation/tomcat9/WebappClassLoaderInstrumentation.java:36
    result[1122] = declaresMethod(isMethod().and(named("setResources")));
    // tomcat/tomcat-appsec/tomcat-appsec-5.5/src/main/java/datadog/trace/instrumentation/tomcat55/CommitActionInstrumentation.java:59
    result[1123] =
        declaresMethod(
            isPublic()
                .and(named("action"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.apache.coyote.ActionCode")))
                .and(takesArgument(1, named("java.lang.Object"))));
    // tomcat/tomcat-appsec/tomcat-appsec-5.5/src/main/java/datadog/trace/instrumentation/tomcat55/ParsedBodyParametersInstrumentation.java:76
    result[1124] =
        declaresMethod(
            named("processParameters")
                .and(takesArgument(0, named("byte[]")))
                .and(takesArgument(1, named("int")))
                .and(takesArgument(2, named("int"))));
    // tomcat/tomcat-appsec/tomcat-appsec-5.5/src/main/java/datadog/trace/instrumentation/tomcat55/ParsedBodyParametersInstrumentation.java:84
    result[1125] = declaresMethod(named("handleQueryParameters").and(takesArguments(0)));
    // tomcat/tomcat-appsec/tomcat-appsec-6.0/src/main/java/datadog/trace/instrumentation/tomcat6/ParsedBodyParametersInstrumentation.java:63
    result[1126] =
        declaresMethod(
            named("processParameters")
                .and(takesArgument(0, named("byte[]")))
                .and(takesArgument(1, named("int")))
                .and(takesArgument(2, named("int"))));
    // tomcat/tomcat-appsec/tomcat-appsec-6.0/src/main/java/datadog/trace/instrumentation/tomcat6/ParsedBodyParametersInstrumentation.java:71
    result[1127] = declaresMethod(named("handleQueryParameters").and(takesArguments(0)));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/CommitActionInstrumentation.java:60
    result[1128] =
        declaresMethod(
            isPublic()
                .and(named("action"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.apache.coyote.ActionCode")))
                .and(takesArgument(1, named("java.lang.Object"))));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/ErrorReportValueInstrumentation.java:37
    result[1129] =
        declaresMethod(
            isMethod()
                .and(named("report"))
                .and(takesArgument(0, named("org.apache.catalina.connector.Request")))
                .and(takesArgument(1, named("org.apache.catalina.connector.Response")))
                .and(takesArgument(2, named("java.lang.Throwable")))
                .and(isProtected()));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/GlassFishMultipartInstrumentation.java:52
    result[1130] = declaresMethod(named("getParts").and(takesArguments(0)).and(isPublic()));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/ParsePartsInstrumentation.java:76
    result[1131] =
        declaresMethod(
            named("parseParts")
                .and(
                    takesArguments(0).or(takesArguments(1).and(takesArgument(0, named("boolean")))))
                .and(isPrivate()));
    // twilio-0.0.1/src/main/java/datadog/trace/instrumentation/twilio/TwilioAsyncInstrumentation.java:72
    result[1132] =
        declaresMethod(
            isMethod()
                .and(
                    namedOneOf(
                        "createAsync", "deleteAsync", "readAsync", "fetchAsync", "updateAsync"))
                .and(isPublic())
                .and(returns(named("com.google.common.util.concurrent.ListenableFuture"))));
    // twilio-0.0.1/src/main/java/datadog/trace/instrumentation/twilio/TwilioSyncInstrumentation.java:61
    result[1133] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(namedOneOf("create", "delete", "read", "fetch", "update")));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/FormDataParserInstrumentation.java:58
    result[1134] =
        declaresMethod(
            named("doParse")
                .and(takesArgument(0, named("org.xnio.channels.StreamSourceChannel")))
                .and(takesArguments(1))
                .and(isPrivate()));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/HttpRequestParserInstrumentation.java:57
    result[1135] =
        declaresMethod(
            isMethod()
                .and(named("handle"))
                .and(takesArgument(2, named("io.undertow.server.HttpServerExchange"))));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/HttpServerExchangeSenderInstrumentation.java:49
    result[1136] =
        declaresMethod(not(isPrivate()).and(named("getResponseChannel")).and(takesArguments(0)));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/MultiPartUploadHandlerInstrumentation.java:55
    result[1137] =
        declaresMethod(
            named("parseBlocking")
                .and(takesArguments(0))
                .and(returns(named("io.undertow.server.handlers.form.FormData")))
                .and(isPublic()));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/RequestParserInstrumentation.java:30
    result[1138] =
        declaresMethod(
            isMethod()
                .and(named("handle"))
                .and(takesArgument(2, named("io.undertow.server.HttpServerExchange"))));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/ServletInstrumentation.java:40
    result[1139] = declaresMethod(isMethod().and(named("dispatchRequest")));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/UndertowInstrumentation.java:28
    result[1140] =
        declaresMethod(
            isMethod()
                .and(named("dispatch"))
                .and(takesArgument(0, named("java.util.concurrent.Executor")))
                .and(takesArgument(1, named("java.lang.Runnable"))));
    // undertow/undertow-2.2/src/main/java/datadog/trace/instrumentation/undertow/JakartaServletInstrumentation.java:37
    result[1141] = declaresMethod(isMethod().and(named("dispatchRequest")));
    // valkey-java-5.3/src/main/java/datadog/trace/instrumentation/valkey/ValkeyInstrumentation.java:39
    result[1142] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("executeCommand"))
                .and(takesArgument(0, named("io.valkey.CommandObject"))));
    // velocity-1.5/src/main/java/datadog/trace/instrumentation/velocity/ASTReferenceInstrumentation.java:32
    result[1143] =
        declaresMethod(
            named("render")
                .and(isMethod())
                .and(
                    takesArgument(0, named("org.apache.velocity.context.InternalContextAdapter"))
                        .and(takesArgument(1, named("java.io.Writer")))));
    // vertx/vertx-mysql-client/vertx-mysql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client/MySQLConnectionFactoryInstrumentation.java:33
    result[1144] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(2, named("io.vertx.mysqlclient.MySQLConnectOptions"))));
    // vertx/vertx-mysql-client/vertx-mysql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client/MySQLConnectionImplInstrumentation.java:36
    result[1145] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("io.vertx.mysqlclient.impl.MySQLConnectionFactory"))));
    // vertx/vertx-mysql-client/vertx-mysql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client/MySQLPoolImplInstrumentation.java:33
    result[1146] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(2, named("io.vertx.mysqlclient.MySQLConnectOptions"))));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4/MySQLConnectionFactoryInstrumentation.java:34
    result[1147] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(2))
                .and(takesArgument(1, named("io.vertx.mysqlclient.MySQLConnectOptions"))));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4/MySQLConnectionImplInstrumentation.java:36
    result[1148] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("io.vertx.mysqlclient.impl.MySQLConnectionFactory"))));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4/MySQLPoolImplInstrumentation.java:36
    result[1149] =
        declaresMethod(
            isStatic()
                .and(isPublic())
                .and(isMethod())
                .and(named("create"))
                .and(takesArguments(4))
                .and(takesArgument(2, named("io.vertx.mysqlclient.MySQLConnectOptions"))));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4_4_2/MySQLConnectionFactoryInstrumentation.java:34
    result[1150] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(2))
                .and(takesArgument(1, named("java.util.function.Supplier"))));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4_4_2/MySQLDriverInstrumentation.java:35
    result[1151] =
        declaresMethod(
            isPrivate()
                .and(named("newPoolImpl"))
                .and(
                    takesArguments(4).and(takesArgument(1, named("java.util.function.Supplier")))));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4_4_2/SqlConnectionBaseInstrumentation.java:38
    result[1152] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(4))
                .and(takesArgument(1, named("io.vertx.sqlclient.spi.ConnectionFactory"))));
    // vertx/vertx-pg-client/vertx-pg-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4/PgConnectionFactoryInstrumentation.java:34
    result[1153] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(2))
                .and(takesArgument(1, named("io.vertx.pgclient.PgConnectOptions"))));
    // vertx/vertx-pg-client/vertx-pg-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4/PgConnectionImplInstrumentation.java:36
    result[1154] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(0, named("io.vertx.pgclient.impl.PgConnectionFactory"))));
    // vertx/vertx-pg-client/vertx-pg-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4/PgPoolImplInstrumentation.java:36
    result[1155] =
        declaresMethod(
            isStatic()
                .and(isPublic())
                .and(isMethod())
                .and(named("create"))
                .and(takesArguments(4))
                .and(takesArgument(2, named("io.vertx.pgclient.PgConnectOptions"))));
    // vertx/vertx-pg-client/vertx-pg-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4_4_2/PgConnectionFactoryInstrumentation.java:34
    result[1156] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(2))
                .and(takesArgument(1, named("java.util.function.Supplier"))));
    // vertx/vertx-pg-client/vertx-pg-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4_4_2/PgDriverInstrumentation.java:35
    result[1157] =
        declaresMethod(
            isPrivate()
                .and(named("newPoolImpl"))
                .and(
                    takesArguments(4).and(takesArgument(1, named("java.util.function.Supplier")))));
    // vertx/vertx-redis-client/vertx-redis-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_redis_client/CommandImplInstrumentation.java:33
    result[1158] = declaresMethod(isConstructor().and(takesArgument(0, named("java.lang.String"))));
    // vertx/vertx-redis-client/vertx-redis-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_redis_client/RedisInstrumentation.java:52
    result[1159] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("send"))
                .and(takesArgument(0, named("io.vertx.redis.client.Request")))
                .and(takesArgument(1, named("io.vertx.core.Handler"))));
  }

  private static void fill1160(ElementMatcher.Junction<TypeDescription>[] result) {
    // vertx/vertx-redis-client/vertx-redis-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_redis_client/RedisInstrumentation.java:69
    result[1160] =
        declaresMethod(
            isPublic()
                .and(named("send"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.vertx.redis.client.Request")))
                .and(returns(named("io.vertx.core.Future"))));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/CursorImplInstrumentation.java:33
    result[1161] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("read"))
                .and(takesArgument(1, named("io.vertx.core.Handler"))));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/PreparedQueryInstrumentation.java:44
    result[1162] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("execute"))
                .and(takesArguments(2))
                .and(takesArgument(1, named("io.vertx.core.Handler"))));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/PreparedQueryInstrumentation.java:51
    result[1163] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("executeBatch"))
                .and(takesArguments(2))
                .and(takesArgument(1, named("io.vertx.core.Handler"))));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/PreparedStatementImplInstrumentation.java:36
    result[1164] =
        declaresMethod(isMethod().and(isPublic()).and(named("query")).and(takesArguments(0)));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/QueryImplInstrumentation.java:36
    result[1165] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("execute"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/SqlClientBaseInstrumentation.java:38
    result[1166] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("query"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/SqlClientBaseInstrumentation.java:45
    result[1167] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("preparedQuery"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/SqlConnectionBaseInstrumentation.java:38
    result[1168] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("prepare"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("java.lang.String")))
                .and(takesArgument(1, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/AbstractHttpServerRequestInstrumentation.java:44
    result[1169] =
        declaresMethod(isPublic().and(isMethod()).and(named("params")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/AbstractHttpServerRequestInstrumentation.java:50
    result[1170] =
        declaresMethod(
            isMethod()
                .and(named("handleData").or(named("onData")))
                .and(
                    takesArguments(1).and(takesArgument(0, named("io.vertx.core.buffer.Buffer")))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/BufferInstrumentation.java:51
    result[1171] = declaresMethod(isMethod().and(isPublic()).and(named("toString")));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/BufferInstrumentation.java:53
    result[1172] =
        declaresMethod(isMethod().and(isPublic()).and(named("getByteBuf")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/BufferInstrumentation.java:56
    result[1173] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("appendBuffer"))
                .and(takesArgument(0, named("io.vertx.core.buffer.Buffer"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:62
    result[1174] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("get"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:69
    result[1175] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("getAll"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:76
    result[1176] =
        declaresMethod(isMethod().and(isPublic()).and(named("entries")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:79
    result[1177] =
        declaresMethod(isMethod().and(isPublic()).and(named("names")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:62
    result[1178] =
        declaresMethod(isMethod().and(isPublic()).and(named("get")).and(takesArguments(1)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:65
    result[1179] =
        declaresMethod(isMethod().and(isPublic()).and(named("getAll")).and(takesArguments(1)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:68
    result[1180] =
        declaresMethod(isMethod().and(isPublic()).and(named("entries")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:71
    result[1181] =
        declaresMethod(isMethod().and(isPublic()).and(named("names")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/Http2ServerRequestInstrumentation.java:39
    result[1182] =
        declaresMethod(isPublic().and(isMethod()).and(named("headers")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HttpServerRequestInstrumentation.java:40
    result[1183] =
        declaresMethod(isPublic().and(isMethod()).and(named("headers")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/CookieImplInstrumentation.java:55
    result[1184] = declaresMethod(isMethod().and(named("getName")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/CookieImplInstrumentation.java:57
    result[1185] = declaresMethod(isMethod().and(named("getValue")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerRequestInstrumentation.java:38
    result[1186] =
        declaresMethod(
            isPublic()
                .and(named("bodyHandler"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerResponseEndHandlerInstrumentation.java:26
    result[1187] =
        declaresMethod(
            isMethod()
                .and(named("endHandler"))
                .and(isPublic())
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerResponseInstrumentation.java:35
    result[1188] =
        declaresMethod(
            named("putHeader")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.CharSequence")))
                        .and(takesArgument(1, named("java.lang.CharSequence")))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("java.lang.String"))))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerResponseInstrumentation.java:41
    result[1189] =
        declaresMethod(
            named("putHeader")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.CharSequence")))
                        .and(takesArgument(1, named("java.lang.Iterable")))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("java.lang.Iterable"))))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/IastRoutingContextImplInstrumentation.java:50
    result[1190] = declaresMethod(named("cookies").and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/IastRoutingContextImplInstrumentation.java:51
    result[1191] =
        declaresMethod(
            named("getCookie")
                .and(takesArguments(1))
                .and(takesArgument(0, named("java.lang.String"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/IastRoutingContextImplInstrumentation.java:54
    result[1192] =
        declaresMethod(
            named("reroute")
                .and(takesArguments(2))
                .and(takesArgument(1, named("java.lang.String"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RouteHandlerInstrumentation.java:27
    result[1193] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("handler", "blockingHandler"))
                .and(isPublic())
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RouteImplInstrumentation.java:52
    result[1194] =
        declaresMethod(
            named("matches")
                .and(takesArguments(3))
                .and(takesArgument(0, named("io.vertx.ext.web.impl.RoutingContextImplBase")))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("boolean")))
                .and(isPublic())
                .and(returns(named("int"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RouteImplInstrumentation.java:62
    result[1195] =
        declaresMethod(
            named("matches")
                .and(takesArguments(3))
                .and(
                    takesArgument(
                        0,
                        named("io.vertx.ext.web.impl.RoutingContextImplBase")
                            .or(named("io.vertx.ext.web.RoutingContext"))))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("boolean")))
                .and(returns(named("boolean"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RoutingContextImplInstrumentation.java:42
    result[1196] =
        declaresMethod(
            named("getBodyAsJson").or(named("getBodyAsJsonArray")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RoutingContextImplInstrumentation.java:45
    result[1197] =
        declaresMethod(
            named("setSession").and(takesArgument(0, named("io.vertx.ext.web.Session"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RoutingContextImplInstrumentation.java:48
    result[1198] = declaresMethod(named("fileUploads").and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/VertxImplInstrumentation.java:30
    result[1199] =
        declaresMethod(
            isPublic()
                .and(named("exceptionHandler"))
                .and(takesArguments(0))
                .and(returns(named("io.vertx.core.Handler"))));
  }

  private static void fill1200(ElementMatcher.Junction<TypeDescription>[] result) {
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:62
    result[1200] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("get"))
                .and(takesArguments(1).and(takesArgument(0, named("java.lang.CharSequence")))));
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:68
    result[1201] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("getAll"))
                .and(takesArguments(1).and(takesArgument(0, named("java.lang.CharSequence")))));
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:74
    result[1202] =
        declaresMethod(isMethod().and(isPublic()).and(named("entries")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:77
    result[1203] =
        declaresMethod(isMethod().and(isPublic()).and(named("names")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-3.9/src/main/java/datadog/trace/instrumentation/vertx_3_9/Vertx39HttpServertResponseInstrumentation.java:29
    result[1204] =
        declaresMethod(
            named("addCookie")
                .and(takesArgument(0, named("io.vertx.core.http.Cookie")))
                .and(isPublic()));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:47
    result[1205] =
        declaresMethod(isPublic().and(isMethod()).and(named("headers")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:50
    result[1206] =
        declaresMethod(
            isPublic()
                .and(isMethod())
                .and(named("params"))
                .and(
                    takesArguments(0)
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(1)
                                .and(takesArgument(0, named("boolean"))))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:59
    result[1207] =
        declaresMethod(
            isMethod()
                .and(named("handleData").or(named("onData")))
                .and(
                    takesArguments(1).and(takesArgument(0, named("io.vertx.core.buffer.Buffer")))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:64
    result[1208] =
        declaresMethod(
            isPublic().and(isMethod()).and(named("cookies")).and(returns(named("java.util.Set"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:67
    result[1209] =
        declaresMethod(
            isPublic()
                .and(isMethod())
                .and(named("getCookie"))
                .and(takesArgument(0, named("java.lang.String"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/BufferInstrumentation.java:50
    result[1210] = declaresMethod(isMethod().and(isPublic()).and(named("toString")));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/BufferInstrumentation.java:52
    result[1211] =
        declaresMethod(isMethod().and(isPublic()).and(named("getByteBuf")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/BufferInstrumentation.java:55
    result[1212] =
        declaresMethod(
            isMethod()
                .and(isPublic())
                .and(named("appendBuffer"))
                .and(takesArgument(0, named("io.vertx.core.buffer.Buffer"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/HttpServerResponseInstrumentation.java:32
    result[1213] =
        declaresMethod(
            named("putHeader")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.CharSequence")))
                        .and(takesArgument(1, named("java.lang.CharSequence")))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("java.lang.String"))))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/HttpServerResponseInstrumentation.java:38
    result[1214] =
        declaresMethod(
            named("putHeader")
                .and(
                    ElementMatchers.<MethodDescription>takesArguments(2)
                        .and(takesArgument(0, named("java.lang.CharSequence")))
                        .and(takesArgument(1, named("java.lang.Iterable")))
                        .or(
                            ElementMatchers.<MethodDescription>takesArguments(2)
                                .and(takesArgument(0, named("java.lang.String")))
                                .and(takesArgument(1, named("java.lang.Iterable"))))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/MultiMapInstrumentation.java:67
    result[1215] =
        declaresMethod(isMethod().and(isPublic()).and(named("entries")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/MultiMapInstrumentation.java:70
    result[1216] =
        declaresMethod(isMethod().and(isPublic()).and(named("names")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/CookieImplInstrumentation.java:53
    result[1217] = declaresMethod(isMethod().and(named("getName")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/CookieImplInstrumentation.java:55
    result[1218] = declaresMethod(isMethod().and(named("getValue")).and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/HttpServerRequestInstrumentation.java:36
    result[1219] =
        declaresMethod(
            isPublic()
                .and(named("bodyHandler"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/HttpServerResponseEndHandlerInstrumentation.java:37
    result[1220] =
        declaresMethod(
            isMethod()
                .and(named("endHandler"))
                .and(isPublic())
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/IastRoutingContextImplInstrumentation.java:40
    result[1221] =
        declaresMethod(
            named("reroute")
                .and(takesArguments(2))
                .and(takesArgument(1, named("java.lang.String"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/LocalSessionStoreInstrumentation.java:41
    result[1222] =
        declaresMethod(
            named("init")
                .and(isPublic())
                .and(takesArguments(2))
                .and(takesArgument(0, named("io.vertx.core.Vertx")))
                .and(takesArgument(1, named("io.vertx.core.json.JsonObject"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/LocalSessionStoreInstrumentation.java:48
    result[1223] =
        declaresMethod(
            named("setTimer").and(isPrivate()).and(takesArguments(0)).and(returns(named("void"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RequestBodyInstrumentation.java:32
    result[1224] =
        declaresMethod(
            named("asJsonObject")
                .or(named("asJsonArray"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("int"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RouteHandlerInstrumentation.java:33
    result[1225] =
        declaresMethod(
            isMethod()
                .and(namedOneOf("handler", "blockingHandler"))
                .and(isPublic())
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RouteImplInstrumentation.java:50
    result[1226] =
        declaresMethod(
            named("matches")
                .and(takesArguments(3))
                .and(takesArgument(0, named("io.vertx.ext.web.impl.RoutingContextImplBase")))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("boolean")))
                .and(isPublic())
                .and(returns(named("int"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RouteImplInstrumentation.java:60
    result[1227] =
        declaresMethod(
            named("matches")
                .and(takesArguments(3))
                .and(
                    takesArgument(
                        0,
                        named("io.vertx.ext.web.impl.RoutingContextImplBase")
                            .or(named("io.vertx.ext.web.RoutingContext"))))
                .and(takesArgument(1, named("java.lang.String")))
                .and(takesArgument(2, named("boolean")))
                .and(returns(named("boolean"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextImplInstrumentation.java:45
    result[1228] =
        declaresMethod(
            named("getBodyAsJson")
                .or(named("getBodyAsJsonArray"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("int"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextImplInstrumentation.java:51
    result[1229] =
        declaresMethod(
            named("setSession").and(takesArgument(0, named("io.vertx.ext.web.Session"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextImplInstrumentation.java:54
    result[1230] = declaresMethod(named("fileUploads").and(takesArguments(0)));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextInstrumentation.java:37
    result[1231] =
        declaresMethod(
            named("json").and(takesArguments(1)).and(takesArgument(0, named("java.lang.Object"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/VertxHandlerInstrumentation.java:39
    result[1232] =
        declaresMethod(
            isPublic()
                .and(named("exceptionCaught"))
                .and(takesArguments(2))
                .and(takesArgument(0, named("io.netty.channel.ChannelHandlerContext")))
                .and(takesArgument(1, named("java.lang.Throwable"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/VertxImplInstrumentation.java:36
    result[1233] =
        declaresMethod(
            isPublic()
                .and(named("exceptionHandler"))
                .and(takesArguments(0))
                .and(returns(named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-5.0/src/main/java/datadog/trace/instrumentation/vertx_5_0/server/HttpServerRequestInstrumentation.java:36
    result[1234] =
        declaresMethod(
            isPublic()
                .and(named("bodyHandler"))
                .and(takesArguments(1))
                .and(takesArgument(0, named("io.vertx.core.Handler"))));
    // vertx/vertx-web/vertx-web-5.0/src/main/java/datadog/trace/instrumentation/vertx_5_0/server/RoutingContextImplInstrumentation.java:39
    result[1235] = declaresMethod(named("fileUploads").and(takesArguments(0)));
    // weaver-0.9/src/main/java/datadog/trace/instrumentation/weaver/WeaverInstrumentation.java:32
    result[1236] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(5, named("java.util.concurrent.ConcurrentLinkedQueue"))));
    // weaver-0.9/src/main/java/datadog/trace/instrumentation/weaver/WeaverInstrumentation.java:36
    result[1237] =
        declaresMethod(
            isConstructor()
                .and(takesArgument(5, named("java.util.concurrent.LinkedBlockingQueue"))));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:49
    result[1238] =
        declaresMethod(
            isPublic()
                .and(named("sendText"))
                .and(
                    takesArguments(1).or(takesArguments(2).and(takesArgument(1, named("boolean")))))
                .and(takesArgument(0, named("java.lang.String")))
                .and(returns(named("void"))));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:56
    result[1239] =
        declaresMethod(
            isPublic()
                .and(named("sendBinary"))
                .and(
                    takesArguments(1).or(takesArguments(2).and(takesArgument(1, named("boolean")))))
                .and(takesArgument(0, named("java.nio.ByteBuffer")))
                .and(returns(named("void"))));
  }

  private static void fill1240(ElementMatcher.Junction<TypeDescription>[] result) {
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:63
    result[1240] =
        declaresMethod(
            isPublic().and(named("sendObject")).and(takesArguments(1)).and(returns(named("void"))));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:66
    result[1241] = declaresMethod(isPublic().and(named("getSendStream")).and(takesArguments(0)));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:69
    result[1242] = declaresMethod(isPublic().and(named("getSendWriter")).and(takesArguments(0)));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/MessageHandlerInstrumentation.java:43
    result[1243] =
        declaresMethod(
            isPublic()
                .and(named("onMessage"))
                .and(
                    takesArguments(1)
                        .or(takesArguments(2).and(takesArgument(1, named("boolean"))))));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/SessionInstrumentation.java:64
    result[1244] =
        declaresMethod(
            isPublic().and(namedOneOf("getBasicRemote", "getAsyncRemote")).and(takesArguments(0)));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/SessionInstrumentation.java:76
    result[1245] = declaresMethod(named("close").and(takesArguments(0)));
    // websocket/jetty-websocket/jetty-websocket-10.0/src/main/java/datadog/trace/instrumentation/websocket/jetty10/JavaxWebSocketFrameHandlerInstrumentation.java:24
    result[1246] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(9))
                .and(takesArgument(1, named("java.lang.Object")))
                .and(takesArgument(2, named("java.lang.invoke.MethodHandle")))
                .and(takesArgument(3, named("java.lang.invoke.MethodHandle"))));
    // websocket/jetty-websocket/jetty-websocket-10.0/src/main/java/datadog/trace/instrumentation/websocket/jetty10/JavaxWebSocketFrameHandlerInstrumentation.java:31
    result[1247] =
        declaresMethod(
            isConstructor()
                .and(takesArguments(10))
                .and(takesArgument(2, named("java.lang.Object")))
                .and(takesArgument(3, named("java.lang.invoke.MethodHandle")))
                .and(takesArgument(4, named("java.lang.invoke.MethodHandle"))));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeFrameHandlerInstrumentation.java:28
    result[1248] =
        declaresMethod(
            named("onOpen")
                .and(takesArguments(2))
                .and(takesArgument(0, named("org.eclipse.jetty.websocket.core.CoreSession"))));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeFrameHandlerInstrumentation.java:33
    result[1249] = declaresMethod(named("createMessageSink").and(takesArguments(4)));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeFrameHandlerInstrumentation.java:36
    result[1250] = declaresMethod(named("notifyOnClose").and(takesArguments(2)));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeSessionInstrumentation.java:30
    result[1252] =
        declaresMethod(
            namedOneOf("sendText", "sendBinary")
                .and(takesArguments(2))
                .and(takesArgument(1, named("org.eclipse.jetty.websocket.api.Callback"))));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeSessionInstrumentation.java:35
    result[1253] =
        declaresMethod(
            namedOneOf("sendPartialText", "sendPartialBinary")
                .and(takesArguments(3))
                .and(takesArgument(1, named("boolean")))
                .and(takesArgument(2, named("org.eclipse.jetty.websocket.api.Callback"))));
    // websphere-jmx-8.5/src/main/java/datadog/trace/instrumentation/websphere_jmx/WebsphereSecurityInstrumentation.java:48
    result[1254] =
        declaresMethod(isMethod().and(named("isSecurityEnabled")).and(returns(named("boolean"))));
    // wildfly-9.0/src/main/java/datadog/trace/instrumentation/wildfly/ResourceReferenceProcessorInstrumentation.java:39
    result[1256] = declaresMethod(isMethod().and(named("getEnvironmentEntries")));
    // ws/jax-ws/jax-ws-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxws2/WebServiceProviderInstrumentation.java:51
    result[1257] = declaresMethod(isMethod().and(named("invoke")).and(takesArguments(1)));
    // zio/zio-2.0/src/main/java/datadog/trace/instrumentation/zio/v2_0/ZioRuntimeInstrumentation.java:41
    result[1258] = declaresMethod(isMethod().and(named("defaultSupervisor")));
    // azure-functions-1.2.2/src/main/java/datadog/trace/instrumentation/azure/functions/AzureFunctionsInstrumentation.java:48
    // Regular-method annotation projection.
    result[1259] =
        declaresMethod(
            isMethod()
                .and(
                    isAnnotatedWith(
                        named("com.microsoft.azure.functions.annotation.FunctionName"))));
    // datadog/tracing/trace-annotation/src/main/java/datadog/trace/instrumentation/trace_annotation/DoNotTraceAnnotationInstrumentation.java:30
    // Regular-method annotation projection.
    result[1260] =
        declaresMethod(isMethod().and(isAnnotatedWith(named("datadog.trace.api.DoNotTrace"))));
    // opentelemetry/opentelemetry-annotations-1.26/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/AddingSpanAttributesInstrumentation.java:37
    // Regular-method annotation projection.
    result[1261] =
        declaresMethod(
            isMethod()
                .and(
                    isAnnotatedWith(
                        named(
                            "io.opentelemetry.instrumentation.annotations.AddingSpanAttributes"))));
    // opentelemetry/opentelemetry-annotations-1.20/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/WithSpanAnnotationInstrumentation.java:38
    // Regular-method annotation projection.
    result[1262] =
        declaresMethod(
            isMethod()
                .and(
                    isAnnotatedWith(
                        named("io.opentelemetry.instrumentation.annotations.WithSpan"))));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAnnotationsInstrumentation.java:75
    // Regular-method annotation projection.
    result[1263] = declaresMethod(isMethod().and(isAnnotatedWith(named("jakarta.ws.rs.Path"))));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-1.1.1/src/main/java/datadog/trace/instrumentation/jaxrs1/JaxRsAnnotationsInstrumentation.java:73
    // Regular-method annotation projection.
    result[1264] = declaresMethod(isMethod().and(isAnnotatedWith(named("javax.ws.rs.Path"))));
  }

  static ElementMatcher.Junction<TypeDescription> create() {
    ElementMatcher.Junction<TypeDescription>[] branches = branches();
    ElementMatcher.Junction<TypeDescription> graph = branches[0];
    for (int i = 1; i < branches.length; i++) {
      graph = graph.or(branches[i]);
    }
    return graph;
  }
}
