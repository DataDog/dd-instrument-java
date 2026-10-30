package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.AccessMatcher.PRIVATE;
import static datadog.instrument.classmatch.AccessMatcher.PROTECTED;
import static datadog.instrument.classmatch.AccessMatcher.PUBLIC;
import static datadog.instrument.classmatch.AccessMatcher.STATIC;
import static datadog.instrument.classmatch.ClassMatcher.declares;
import static datadog.instrument.classmatch.FieldMatcher.field;
import static datadog.instrument.classmatch.MethodMatcher.constructor;
import static datadog.instrument.classmatch.MethodMatcher.method;
import static datadog.instrument.classmatch.StandardMatchers.nameEndsWith;
import static datadog.instrument.classmatch.StandardMatchers.nameStartsWith;
import static datadog.instrument.classmatch.StandardMatchers.namedOneOf;
import static datadog.instrument.classmatch.StandardMatchers.not;
import static datadog.instrument.classmatch.TypeMatcher.type;
import static datadog.instrument.classmatch.TypeMatcher.typeEndsWith;
import static datadog.instrument.classmatch.TypeMatcher.typeOneOf;
import static datadog.instrument.classmatch.TypeMatcher.typeStartsWith;

import java.util.Arrays;

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
final class DDTraceClassMatcherGraphs {
  private DDTraceClassMatcherGraphs() {}

  // Unlike method(), unconstrained Byte Buddy predicates also accept constructors.
  private static MethodMatcher anyMethod() {
    return m -> true;
  }

  private static FieldMatcher anyField() {
    return f -> true;
  }

  static ClassMatcher[] branches() {
    ClassMatcher[] result = new ClassMatcher[1265];
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
  private static ClassMatcher[] withoutGaps(ClassMatcher[] result) {
    int size = 0;
    for (ClassMatcher branch : result) {
      if (branch != null) {
        result[size++] = branch;
      }
    }
    return Arrays.copyOf(result, size);
  }

  private static void fill0(ClassMatcher[] result) {
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/AerospikeClientInstrumentation.java:26
    result[0] =
        declares(
            method().access(PUBLIC).parameter(0, typeStartsWith("com.aerospike.client.policy")));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/AerospikeClientInstrumentation.java:31
    result[1] =
        declares(
            method().access(PUBLIC).parameter(1, typeStartsWith("com.aerospike.client.listener")));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/CommandInstrumentation.java:28
    result[2] =
        declares(
            method()
                .and(method("getNode"))
                .parameter(0, "com.aerospike.client.cluster.Cluster")
                .returning(type("com.aerospike.client.cluster.Node")));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/NioEventLoopInstrumentation.java:25
    result[3] =
        declares(method().and(method("execute")).parameters(1).parameter(0, "java.lang.Runnable"));
    // aerospike-4.0/src/main/java/datadog/trace/instrumentation/aerospike4/PartitionInstrumentation.java:29
    result[4] =
        declares(
            method()
                .and(method(namedOneOf("getNodeRead", "getNodeWrite")))
                .parameter(0, "com.aerospike.client.cluster.Cluster")
                .returning(type("com.aerospike.client.cluster.Node")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaActorCellInstrumentation.java:43
    result[5] = declares(method().and(method("invoke")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaDeadLetterQueueInstrumentation.java:52
    result[6] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("enqueue"))
                .parameters(2)
                .parameter(0, "akka.actor.ActorRef")
                .parameter(1, "akka.dispatch.Envelope")
                .returning("void"));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinExecutorTaskInstrumentation.java:54
    result[8] = declares(constructor().parameter(0, "java.lang.Runnable"));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinExecutorTaskInstrumentation.java:57
    result[9] = declares(method().and(method("run")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinPoolInstrumentation.java:48
    result[10] = declares(method().and(method(namedOneOf("externalPush", "fullExternalPush"))));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:65
    result[11] = declares(method(namedOneOf("exec", "fork", "cancel")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:98
    result[12] = declares(method().and(method(namedOneOf("doExec", "exec"))));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:100
    result[13] = declares(method().and(method("fork")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaForkJoinTaskInstrumentation.java:101
    result[14] = declares(method().and(method("cancel")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaMailboxInstrumentation.java:31
    result[15] = declares(method().and(method("run")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/concurrent/AkkaRoutedActorCellInstrumentation.java:40
    result[16] =
        declares(method().and(method("sendMessage").parameter(0, "akka.dispatch.Envelope")));
    // akka/akka-actor-2.5/src/main/java/datadog/trace/instrumentation/akka/init/DisableTracingActorInitInstrumentation.java:29
    result[17] = declares(method("apply"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttp2ServerInstrumentation.java:42
    result[18] =
        declares(
            anyMethod()
                .parameters(8)
                .and(method("bindAndHandleAsync"))
                .parameter(0, "scala.Function1")
                .parameter(7, "akka.stream.Materializer"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttp2ServerInstrumentation.java:48
    result[19] =
        declares(
            anyMethod()
                .parameters(7)
                .and(method("bindAndHandleAsync"))
                .parameter(0, "scala.Function1")
                .parameter(6, "akka.stream.Materializer"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttp2ServerInstrumentation.java:54
    result[20] =
        declares(
            anyMethod()
                .parameters(6)
                .and(method("bindAndHandleAsync"))
                .parameter(0, "scala.Function1")
                .parameter(5, "akka.stream.Materializer"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaHttpServerInstrumentation.java:71
    result[21] = declares(method("bindAndHandle").parameter(0, "akka.stream.scaladsl.Flow"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/AkkaPoolMasterActorInstrumentation.java:28
    result[22] =
        declares(method("akka$http$impl$engine$client$PoolMasterActor$$startPoolInterface"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/DefaultExceptionHandlerInstrumentation.java:30
    result[23] =
        declares(
            method()
                .returning(type("akka.http.scaladsl.server.ExceptionHandler"))
                .parameters(1)
                .parameter(0, "akka.http.scaladsl.settings.RoutingSettings"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/Bug4304Instrumentation.java:76
    result[24] = declares(field("oneHundredContinueSent"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/ConfigProvideRemoteAddressHeaderInstrumentation.java:28
    result[26] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("getBoolean"))
                .parameters(1)
                .parameter(0, "java.lang.String")
                .returning("boolean"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/FormDataToStrictInstrumentation.java:37
    result[27] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("toStrict"))
                .parameters(2)
                .parameter(0, "scala.concurrent.duration.FiniteDuration")
                .parameter(1, "akka.stream.Materializer")
                .returning(type("scala.concurrent.Future")));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/JacksonUnmarshallerInstrumentation.java:37
    result[28] =
        declares(
            method()
                .access(STATIC)
                .returning(type("akka.http.javadsl.unmarshalling.Unmarshaller"))
                .and(method("byteStringUnmarshaller").or(method("unmarshaller")))
                .parameters(2)
                .parameter(0, "com.fasterxml.jackson.databind.ObjectMapper")
                .parameter(1, "java.lang.Class"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/appsec/StrictFormCompanionInstrumentation.java:42
    result[29] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("unmarshaller"))
                .returning(type("akka.http.scaladsl.unmarshalling.Unmarshaller"))
                .parameters(2)
                .parameter(0, "akka.http.scaladsl.unmarshalling.Unmarshaller")
                .parameter(1, "akka.http.scaladsl.unmarshalling.Unmarshaller"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/CookieHeaderInstrumentation.java:48
    result[30] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("cookies"))
                .returning(type("scala.collection.immutable.Seq"))
                .parameters(0));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/FormDataInstrumentation.java:39
    result[31] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("fields"))
                .parameters(0)
                .returning(type("akka.http.scaladsl.model.Uri$Query")));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/FormFieldDirectivesInstrumentation.java:56
    result[32] =
        declares(
            method()
                .access(STATIC)
                .and(method("formField").or(method("formFields")))
                .returning("java.lang.Object")
                .parameters(2)
                .parameter(0, "akka.http.scaladsl.server.directives.FormFieldDirectives")
                .parameter(
                    1, "akka.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/FormFieldDirectivesInstrumentation.java:71
    result[33] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("formField").or(method("formFields")))
                .and(
                    anyMethod()
                        .returning("java.lang.Object")
                        .or(anyMethod().returning(type("akka.http.scaladsl.server.Directive"))))
                .parameters(1)
                .parameter(
                    0, "akka.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/HttpHeaderSubclassesInstrumentation.java:56
    result[34] =
        declares(method().and(method("value")).parameters(0).returning("java.lang.String"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/HttpRequestInstrumentation.java:50
    result[35] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("headers"))
                .returning(type("scala.collection.immutable.Seq"))
                .parameters(0));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/HttpRequestInstrumentation.java:58
    result[36] =
        declares(
            method()
                .access(PUBLIC)
                .and(not(anyMethod().access(STATIC)))
                .and(method("entity"))
                .parameters(0));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/MarshallingDirectivesInstrumentation.java:47
    result[37] =
        declares(
            method()
                .access(STATIC)
                .and(method("entity"))
                .returning(type("akka.http.scaladsl.server.Directive"))
                .parameters(2)
                .parameter(0, "akka.http.scaladsl.server.directives.MarshallingDirectives")
                .parameter(1, "akka.http.scaladsl.unmarshalling.Unmarshaller"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/MarshallingDirectivesInstrumentation.java:60
    result[38] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("entity"))
                .returning(type("akka.http.scaladsl.server.Directive"))
                .parameters(1)
                .parameter(1, "akka.http.scaladsl.unmarshalling.Unmarshaller"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/ParameterDirectivesInstrumentation.java:55
    result[39] =
        declares(
            method()
                .access(STATIC)
                .and(method("parameter").or(method("parameters")))
                .returning("java.lang.Object")
                .parameters(2)
                .parameter(0, "akka.http.scaladsl.server.directives.ParameterDirectives")
                .parameter(
                    1, "akka.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"));
  }

  private static void fill40(ClassMatcher[] result) {
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/ParameterDirectivesInstrumentation.java:70
    result[40] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("parameter").or(method("parameters")))
                .and(
                    anyMethod()
                        .returning("java.lang.Object")
                        .or(anyMethod().returning(type("akka.http.scaladsl.server.Directive"))))
                .parameters(1)
                .parameter(
                    0, "akka.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/PathMatcherInstrumentation.java:40
    result[41] =
        declares(
            constructor()
                .parameters(3)
                .parameter(0, "akka.http.scaladsl.model.Uri$Path")
                .parameter(1, "java.lang.Object")
                .parameter(2, "akka.http.scaladsl.server.util.Tuple"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/RequestContextInstrumentation.java:39
    result[42] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("request"))
                .returning(type("akka.http.scaladsl.model.HttpRequest"))
                .parameters(0));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UnmarshallerInstrumentation.java:55
    result[43] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("apply"))
                .returning(type("scala.concurrent.Future"))
                .parameters(3)
                .parameter(0, "java.lang.Object")
                .parameter(1, "scala.concurrent.ExecutionContext")
                .parameter(2, "akka.stream.Materializer"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UriInstrumentation.java:44
    result[44] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("queryString"))
                .returning(type("scala.Option"))
                .parameters(1)
                .parameter(0, "java.nio.charset.Charset"));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UriInstrumentation.java:52
    result[45] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("rawQueryString"))
                .returning(type("scala.Option"))
                .parameters(0));
    // akka/akka-http/akka-http-10.0/src/main/java/datadog/trace/instrumentation/akkahttp/iast/UriInstrumentation.java:59
    result[46] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("query"))
                .returning(type("akka.http.scaladsl.model.Uri$Query"))
                .parameters(2)
                .parameter(0, "java.nio.charset.Charset")
                .parameter(1, "akka.http.scaladsl.model.Uri$ParsingMode"));
    // akka/akka-http/akka-http-10.2-iast/src/main/java/datadog/trace/instrumentation/akkahttp102/iast/ParameterDirectivesImplInstrumentation.java:47
    result[47] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("filter"))
                .parameters(2)
                .parameter(0, "java.lang.String")
                .parameter(1, "akka.http.scaladsl.unmarshalling.Unmarshaller")
                .returning(type("akka.http.scaladsl.server.Directive")));
    // akka/akka-http/akka-http-10.2-iast/src/main/java/datadog/trace/instrumentation/akkahttp102/iast/ParameterDirectivesImplInstrumentation.java:58
    result[48] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("repeatedFilter"))
                .parameters(2)
                .parameter(0, "java.lang.String")
                .parameter(1, "akka.http.scaladsl.unmarshalling.Unmarshaller")
                .returning(type("akka.http.scaladsl.server.Directive")));
    // apache-httpclient/apache-httpasyncclient-4.0/src/main/java/datadog/trace/instrumentation/apachehttpasyncclient/ApacheHttpClientRedirectInstrumentation.java:40
    result[49] =
        declares(
            method()
                .and(method("getRedirect"))
                .parameter(2, "org.apache.http.protocol.HttpContext"));
    // apache-httpclient/apache-httpasyncclient-4.0/src/main/java/datadog/trace/instrumentation/apachehttpasyncclient/BasicFutureInstrumentation.java:27
    result[50] = declares(field("callback"));
    // apache-httpclient/apache-httpclient-5.0/src/main/java/datadog/trace/instrumentation/apachehttpclient5/IastHttpUriRequestBaseInstrumentation.java:39
    result[52] = declares(constructor().parameters("java.lang.String", "java.net.URI"));
    // apache-httpcore/apache-httpcore-4.0/src/main/java/datadog/trace/instrumentation/apachehttpcore/IastHttpHostInstrumentation.java:32
    result[53] = declares(constructor().parameters("java.lang.String", "int", "java.lang.String"));
    // apache-httpcore/apache-httpcore-4.0/src/main/java/datadog/trace/instrumentation/apachehttpcore/IastHttpHostInstrumentation.java:35
    result[54] = declares(method("toURI").and(method()).parameters(0));
    // apache-httpcore/apache-httpcore-5.0/src/main/java/datadog/trace/instrumentation/apachehttpcore5/IastHttpHostInstrumentation.java:30
    result[55] =
        declares(
            constructor()
                .parameters("java.lang.String", "java.net.InetAddress", "java.lang.String", "int"));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ArmeriaMessageDeframerInstrumentation.java:35
    result[56] =
        declares(
            constructor()
                .parameter(
                    0,
                    "com.linecorp.armeria.common.grpc.protocol.ArmeriaMessageDeframer$Listener"));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ArmeriaMessageDeframerInstrumentation.java:43
    result[57] =
        declares(
            constructor()
                .parameter(2, "com.linecorp.armeria.internal.common.grpc.TransportStatusListener"));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ArmeriaMessageDeframerInstrumentation.java:49
    result[58] = declares(method().and(method("process").or(method("deframe"))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:44
    result[59] = declares(constructor().parameter(4, "io.grpc.MethodDescriptor"));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:48
    result[60] = declares(constructor().parameter(2, "io.grpc.MethodDescriptor"));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:55
    result[61] = declares(method("cancel").and(method()));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:56
    result[62] =
        declares(
            method("request")
                .and(method())
                .parameters("int")
                .or(method().and(method("halfClose").parameters(0))));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:62
    result[63] = declares(method("sendMessage").and(method()));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:64
    result[64] = declares(method("close").and(method().parameters(2)));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:68
    result[65] =
        declares(method("close").and(method()).parameters(3).parameter(2, "java.lang.Throwable"));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/client/ClientCallImplInstrumentation.java:77
    result[66] = declares(method("onNext").or(method("messageRead")));
    // armeria/armeria-grpc-0.84/src/main/java/datadog/trace/instrumentation/armeria/grpc/server/HandlerRegistryBuilderInstrumentation.java:43
    result[67] =
        declares(
            method().and(method("addService")).parameter(0, "io.grpc.ServerServiceDefinition"));
    // armeria/armeria-jetty-1.24/src/main/java/datadog/trace/instrumentation/armeria/jetty/ArmeriaJettyInstrumentation.java:30
    result[69] =
        declares(
            method()
                .and(method("serve"))
                .and(
                    anyMethod()
                        .returning(type("com.linecorp.armeria.common.HttpResponse"))
                        .parameter(0, "com.linecorp.armeria.server.ServiceRequestContext")));
    // aws-java/aws-java-dynamodb-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/dynamodb/DynamoDbClientInstrumentation.java:27
    result[70] = declares(method().and(method("resolveExecutionInterceptors")));
    // aws-java/aws-java-eventbridge-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/eventbridge/EventBridgeClientInstrumentation.java:27
    result[71] = declares(method().and(method("resolveExecutionInterceptors")));
    // aws-java/aws-java-lambda-handler-1.2/src/main/java/datadog/trace/instrumentation/aws/v1/lambda/LambdaHandlerInstrumentation.java:68
    result[72] =
        declares(
            method()
                .and(method("handleRequest"))
                .parameter(2, "com.amazonaws.services.lambda.runtime.Context"));
    // aws-java/aws-java-s3-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/s3/S3ClientInstrumentation.java:27
    result[73] = declares(method().and(method("resolveExecutionInterceptors")));
    // aws-java/aws-java-sdk-1.11/src/main/java/datadog/trace/instrumentation/aws/v0/AWSHttpClientInstrumentation.java:39
    result[74] = declares(method().and(method("doExecute")));
    // aws-java/aws-java-sdk-1.11/src/main/java/datadog/trace/instrumentation/aws/v0/HandlerChainFactoryInstrumentation.java:31
    result[75] = declares(method().and(method("newRequestHandler2Chain")));
    // aws-java/aws-java-sdk-1.11/src/main/java/datadog/trace/instrumentation/aws/v0/RequestExecutorInstrumentation.java:36
    result[76] = declares(method().and(method("doExecute")));
    // aws-java/aws-java-sdk-2.2/src/main/java/datadog/trace/instrumentation/aws/v2/AwsClientInstrumentation.java:23
    result[77] = declares(method().and(method("resolveExecutionInterceptors")));
    // aws-java/aws-java-sdk-2.2/src/main/java/datadog/trace/instrumentation/aws/v2/AwsHttpClientInstrumentation.java:48
    result[78] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("execute"))
                .parameter(1, "software.amazon.awssdk.core.internal.http.RequestExecutionContext"));
    // aws-java/aws-java-sfn-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sfn/SfnClientInstrumentation.java:29
    result[79] = declares(method().and(method("resolveExecutionInterceptors")));
  }

  private static void fill80(ClassMatcher[] result) {
    // aws-java/aws-java-sns-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sns/SnsClientInstrumentation.java:32
    result[80] = declares(method().and(method("newRequestHandler2Chain")));
    // aws-java/aws-java-sns-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sns/SnsClientInstrumentation.java:30
    result[81] = declares(method().and(method("resolveExecutionInterceptors")));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/QueueBufferConfigInstrumentation.java:32
    result[82] = declares(field("receiveAttributeNames"));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/SqsClientInstrumentation.java:34
    result[84] = declares(method().and(method("newRequestHandler2Chain")));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/SqsJmsMessageInstrumentation.java:35
    result[85] = declares(constructor().parameter(2, "com.amazonaws.services.sqs.model.Message"));
    // aws-java/aws-java-sqs-1.0/src/main/java/datadog/trace/instrumentation/aws/v1/sqs/SqsReceiveResultInstrumentation.java:42
    result[87] = declares(method().and(method("getMessages")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsClientInstrumentation.java:23
    result[88] = declares(method().and(method("resolveExecutionInterceptors")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsJmsMessageInstrumentation.java:35
    result[89] =
        declares(constructor().parameter(2, "software.amazon.awssdk.services.sqs.model.Message"));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsMd5ChecksumInterceptorInstrumentation.java:21
    result[90] = declares(method().and(method("afterExecution")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveRequestInstrumentation.java:27
    result[91] = declares(field("attributeNames"));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveRequestInstrumentation.java:32
    result[92] = declares(method().and(method("build")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveResponseBuilderImplInstrumentation.java:27
    result[93] =
        declares(
            method()
                .and(method("build"))
                .noParameters()
                .returning(
                    type("software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveResponseBuilderInstrumentation.java:26
    result[94] =
        declares(
            method()
                .and(method("toBuilder"))
                .noParameters()
                .returning(
                    type(
                        "software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse$Builder")));
    // aws-java/aws-java-sqs-2.0/src/main/java/datadog/trace/instrumentation/aws/v2/sqs/SqsReceiveResultInstrumentation.java:23
    result[95] = declares(method().and(method("messages")));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/AxisEngineInstrumentation.java:34
    result[96] =
        declares(
            method()
                .and(method(namedOneOf("receive", "send", "sendFault")))
                .parameter(0, "org.apache.axis2.context.MessageContext"));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/AxisEngineInstrumentation.java:39
    result[97] =
        declares(
            method()
                .and(method(namedOneOf("resumeReceive", "resumeSend", "resumeSendFault")))
                .parameter(0, "org.apache.axis2.context.MessageContext"));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/AxisEngineInstrumentation.java:44
    result[98] =
        declares(
            method().and(method("invoke")).parameter(0, "org.apache.axis2.context.MessageContext"));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/WebSphereAsyncInstrumentation.java:25
    result[99] = declares(method().and(method("sendSOAPRequestAsync")));
    // axis2-1.3/src/main/java/datadog/trace/instrumentation/axis2/WebSphereAsyncInstrumentation.java:28
    result[100] = declares(method().and(method("releaseBuffer")));
    // axway-api-7.5/src/main/java/datadog/trace/instrumentation/axway/AxwayHTTPPluginInstrumentation.java:37
    result[101] = declares(method().access(PUBLIC).and(method("invokeDispose")));
    // axway-api-7.5/src/main/java/datadog/trace/instrumentation/axway/AxwayHTTPPluginInstrumentation.java:39
    result[102] = declares(method().access(PUBLIC).and(method("tryTransaction")));
    // axway-api-7.5/src/main/java/datadog/trace/instrumentation/axway/AxwayHTTPPluginInstrumentation.java:41
    result[103] = declares(method().access(PUBLIC).and(method("sendResponse")));
    // azure-functions-1.2.2/src/main/java/datadog/trace/instrumentation/azure/functions/AzureFunctionsInstrumentation.java:48
    result[104] =
        declares(
            anyMethod().annotatedWith("com.microsoft.azure.functions.annotation.FunctionName"));
    // azure-functions-worker-2.7/src/main/java/datadog/trace/instrumentation/azure/functions/worker/AzureFunctionsWorkerInstrumentation.java:36
    result[105] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("invoke"))
                .parameters(2)
                .parameter(
                    0, "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareContext")
                .parameter(
                    1, "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareChain"));
    // azure-functions-worker-2.7/src/main/java/datadog/trace/instrumentation/azure/functions/worker/DurableOrchestrationExecutorInstrumentation.java:36
    result[106] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("execute"))
                .parameter(0, "java.util.List")
                .parameter(1, "java.util.List"));
    // azure-functions-worker-2.7/src/main/java/datadog/trace/instrumentation/azure/functions/worker/DurableOrchestrationMiddlewareInstrumentation.java:33
    result[107] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("invoke"))
                .parameters(2)
                .parameter(
                    0, "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareContext")
                .parameter(
                    1, "com.microsoft.azure.functions.internal.spi.middleware.MiddlewareChain"));
    // beanshell-2.0/src/main/java/datadog/trace/instrumentation/beanshell/BeanShellInstrumentation.java:73
    result[108] =
        declares(
            method("eval")
                .and(method())
                .and(
                    anyMethod()
                        .parameters(2)
                        .parameter(0, "java.lang.String")
                        .parameter(1, "bsh.NameSpace")));
    // beanshell-2.0/src/main/java/datadog/trace/instrumentation/beanshell/BeanShellInstrumentation.java:86
    result[109] =
        declares(
            method("eval")
                .and(method())
                .and(
                    anyMethod()
                        .parameters(3)
                        .parameter(0, "java.io.Reader")
                        .parameter(1, "bsh.NameSpace")
                        .parameter(2, "java.lang.String")));
    // beanshell-2.0/src/main/java/datadog/trace/instrumentation/beanshell/BeanShellInstrumentation.java:96
    result[110] =
        declares(method("eval").and(method()).parameters("java.lang.String", "java.lang.String"));
    // caffeine-1.0/src/main/java/datadog/trace/instrumentation/caffeine/BoundedLocalCacheInstrumentation.java:29
    result[111] = declares(method("scheduleDrainBuffers").parameters(0));
    // cics-9.1/src/main/java/datadog/trace/instrumentation/cics/ECIInteractionInstrumentation.java:28
    result[112] = declares(method("execute"));
    // cics-9.1/src/main/java/datadog/trace/instrumentation/cics/JavaGatewayInterfaceInstrumentation.java:37
    result[113] = declares(method("flow"));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/CommonsFileUploadAppSecInstrumentation.java:40
    result[114] =
        declares(
            method("parseRequest")
                .access(PUBLIC)
                .parameter(0, "javax.servlet.http.HttpServletRequest"));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/CommonsFileuploadInstrumentation.java:27
    result[115] =
        declares(
            method()
                .and(method("parse"))
                .access(PUBLIC)
                .returning("java.util.Map")
                .parameters("char[]", "int", "int", "char"));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/FileItemInstrumentation.java:37
    result[116] = declares(method("getInputStream").access(PUBLIC).parameters(0));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/FileItemIteratorInstrumentation.java:37
    result[117] = declares(method("next").access(PUBLIC).parameters(0));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/FileItemStreamInstrumentation.java:37
    result[118] = declares(method("openStream").access(PUBLIC).parameters(0));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/ServletFileUploadInstrumentation.java:33
    result[119] =
        declares(
            method("parseRequest")
                .access(PUBLIC)
                .parameter(0, "javax.servlet.http.HttpServletRequest"));
  }

  private static void fill120(ClassMatcher[] result) {
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/ServletFileUploadInstrumentation.java:38
    result[120] =
        declares(
            method("parseParameterMap")
                .access(PUBLIC)
                .parameter(0, "javax.servlet.http.HttpServletRequest"));
    // commons-fileupload-1.5/src/main/java/datadog/trace/instrumentation/commons/fileupload/ServletFileUploadInstrumentation.java:43
    result[121] =
        declares(
            method("getItemIterator")
                .access(PUBLIC)
                .parameter(0, "javax.servlet.http.HttpServletRequest"));
    // commons-httpclient-2.0/src/main/java/datadog/trace/instrumentation/commonshttpclient/IastHttpMethodBaseInstrumentation.java:45
    result[122] = declares(constructor().parameters(1).parameter(0, "java.lang.String"));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaDeserializerInstrumentation.java:36
    result[123] =
        declares(
            method().and(method("configure")).access(PUBLIC).parameters(2).parameter(1, "boolean"));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaDeserializerInstrumentation.java:46
    result[124] =
        declares(
            method()
                .and(method("deserialize"))
                .access(PUBLIC)
                .parameters(3)
                .parameter(0, "java.lang.String")
                .parameter(2, "byte[]"));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaSerializerInstrumentation.java:37
    result[125] =
        declares(
            method().and(method("configure")).access(PUBLIC).parameters(2).parameter(1, "boolean"));
    // confluent-schema-registry/confluent-schema-registry-4.1/src/main/java/datadog/trace/instrumentation/confluentschemaregistry/KafkaSerializerInstrumentation.java:47
    result[126] =
        declares(
            method()
                .and(method("serialize"))
                .access(PUBLIC)
                .parameter(0, "java.lang.String")
                .returning("byte[]"));
    // couchbase/couchbase-2.0/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseBucketInstrumentation.java:48
    result[127] = declares(method().access(PUBLIC).returning(type("rx.Observable")));
    // couchbase/couchbase-2.0/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseClusterInstrumentation.java:49
    result[128] =
        declares(method().access(PUBLIC).returning(type("rx.Observable")).and(not(method("core"))));
    // couchbase/couchbase-2.6/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseCoreInstrumentation.java:41
    result[129] =
        declares(
            method()
                .access(PUBLIC)
                .parameter(0, "com.couchbase.client.core.message.CouchbaseRequest")
                .and(method("send")));
    // couchbase/couchbase-2.6/src/main/java/datadog/trace/instrumentation/couchbase/client/CouchbaseNetworkInstrumentation.java:52
    result[130] =
        declares(
            method()
                .and(method("encode"))
                .parameters(3)
                .parameter(0, "com.couchbase.client.deps.io.netty.channel.ChannelHandlerContext")
                .parameter(2, "java.util.List"));
    // couchbase/couchbase-3.1/src/main/java/datadog/trace/instrumentation/couchbase_31/client/CoreInstrumentation.java:52
    result[133] = declares(constructor().parameter(2, "java.util.Set"));
    // couchbase/couchbase-3.1/src/main/java/datadog/trace/instrumentation/couchbase_31/client/DefaultErrorUtilInstrumentation.java:40
    result[134] =
        declares(anyMethod().access(STATIC).and(method()).and(method("keyValueStatusToException")));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/CoreEnvironmentBuilderInstrumentation.java:34
    result[137] = declares(method().and(method("requestTracer")));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/CoreInstrumentation.java:39
    result[138] = declares(constructor().parameter(2, "java.util.Set"));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/CoreInstrumentation.java:42
    result[139] =
        declares(constructor().parameter(2, "com.couchbase.client.core.util.ConnectionString"));
    // couchbase/couchbase-3.2/src/main/java/datadog/trace/instrumentation/couchbase_32/client/DefaultErrorUtilInstrumentation.java:26
    result[140] =
        declares(anyMethod().access(STATIC).and(method()).and(method("keyValueStatusToException")));
    // cucumber-5.4/src/main/java/datadog/trace/instrumentation/cucumber/CucumberInstrumentation.java:38
    result[141] = declares(method("execute").parameters("java.lang.Object[]"));
    // cxf-2.1/src/main/java/datadog/trace/instrumentation/cxf/InvokerInstrumentation.java:44
    result[142] = declares(method().and(method("invoke")));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:47
    result[143] = declares(method("loadEvaluatorClass").parameters(0));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:52
    result[144] = declares(method("initialize").parameters(1));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:55
    result[145] = declares(method("getProviderHooks").parameters(0));
    // datadog/openfeature/dd-openfeature-1.56/src/main/java/datadog/trace/instrumentation/openfeature/LegacyProviderInstrumentation.java:58
    result[146] =
        declares(
            method(
                    namedOneOf(
                        "getBooleanEvaluation",
                        "getStringEvaluation",
                        "getIntegerEvaluation",
                        "getDoubleEvaluation",
                        "getObjectEvaluation"))
                .parameters(3));
    // datadog/openfeature/dd-openfeature-1.69/src/main/java/datadog/trace/instrumentation/openfeature/DDOpenFeatureInstrumentation.java:53
    result[147] = declares(method().access(STATIC).and(method("detect")).noParameters());
    // datadog/tracing/trace-annotation/src/main/java/datadog/trace/instrumentation/trace_annotation/DoNotTraceAnnotationInstrumentation.java:30
    result[150] = declares(anyMethod().annotatedWith("datadog.trace.api.DoNotTrace"));
    // datadog/tracing/trace-annotation/src/main/java/datadog/trace/instrumentation/trace_annotation/DoNotTraceAnnotationInstrumentation.java:35
    result[151] = declares(anyMethod().annotatedWith("datadog.trace.api.DoNotTrace"));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:54
    result[152] =
        declares(
            method().and(method(namedOneOf("persistObject", "deleteObject", "refreshObject"))));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:58
    result[153] =
        declares(
            method()
                .and(
                    method(
                        namedOneOf(
                            "refreshAllObjects",
                            "persistObjects",
                            "deleteObjects",
                            "findObjects"))));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:63
    result[154] =
        declares(method().and(method("findObject")).parameters(4).parameter(3, "java.lang.String"));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/ExecutionContextInstrumentation.java:69
    result[155] =
        declares(method().and(method("findObject")).parameters(4).parameter(2, "java.lang.Class"));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/JDOQueryInstrumentation.java:34
    result[156] =
        declares(
            method()
                .and(
                    method(
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
                            "deletePersistentInternal"))));
    // datanucleus-4.0.5/src/main/java/datadog/trace/instrumentation/datanucleus/JDOTransactionInstrumentation.java:26
    result[157] = declares(method().and(method(namedOneOf("commit", "rollback"))));
    // datastax-cassandra/datastax-cassandra-3.0/src/main/java/datadog/trace/instrumentation/datastax/cassandra/CassandraClientInstrumentation.java:40
    result[158] = declares(method().access(PRIVATE).and(method("newSession")).parameters(0));
    // datastax-cassandra/datastax-cassandra-3.0/src/main/java/datadog/trace/instrumentation/datastax/cassandra/CassandraClusterInstrumentation.java:51
    result[159] = declares(constructor().parameter(1, "java.util.List"));
  }

  private static void fill160(ClassMatcher[] result) {
    // datastax-cassandra/datastax-cassandra-3.8/src/main/java/datadog/trace/instrumentation/datastax/cassandra38/CassandraClusterInstrumentation.java:45
    result[160] = declares(constructor().parameter(1, "java.util.List"));
    // datastax-cassandra/datastax-cassandra-4.0/src/main/java/datadog/trace/instrumentation/datastax/cassandra4/CassandraClientInstrumentation.java:29
    result[161] =
        declares(
            method()
                .and(method("init"))
                .access(STATIC)
                .parameters(3)
                .parameter(1, "java.util.Set")
                .returning(type("java.util.concurrent.CompletionStage")));
    // dropwizard/dropwizard-views-0.7/src/main/java/datadog/trace/instrumentation/dropwizard/view/DropwizardViewInstrumentation.java:57
    result[162] =
        declares(
            method().and(method("render")).parameter(0, "io.dropwizard.views.View").access(PUBLIC));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-5.0/src/main/java/datadog/trace/instrumentation/elasticsearch5/Elasticsearch5RestClientInstrumentation.java:46
    result[163] =
        declares(
            method()
                .and(method(namedOneOf("performRequestAsync", "performRequestAsyncNoCatch")))
                .parameters(7)
                .parameter(0, "java.lang.String")
                .parameter(1, "java.lang.String")
                .parameter(5, "org.elasticsearch.client.ResponseListener"));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-6.4/src/main/java/datadog/trace/instrumentation/elasticsearch6_4/Elasticsearch6RestClientInstrumentation.java:46
    result[164] =
        declares(
            method()
                .and(method("performRequestAsyncNoCatch"))
                .parameters(2)
                .parameter(0, "org.elasticsearch.client.Request")
                .parameter(1, "org.elasticsearch.client.ResponseListener"));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-7.0/src/main/java/datadog/trace/instrumentation/elasticsearch7/Elasticsearch7RestClientInstrumentation.java:56
    result[165] =
        declares(
            method()
                .and(method("performRequest"))
                .parameters(1)
                .parameter(0, "org.elasticsearch.client.Request"));
    // elasticsearch/elasticsearch-rest/elasticsearch-rest-7.0/src/main/java/datadog/trace/instrumentation/elasticsearch7/Elasticsearch7RestClientInstrumentation.java:62
    result[166] =
        declares(
            method()
                .and(method("performRequestAsync"))
                .parameters(2)
                .parameter(0, "org.elasticsearch.client.Request")
                .parameter(1, "org.elasticsearch.client.ResponseListener"));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-2.0/src/main/java/datadog/trace/instrumentation/elasticsearch2/Elasticsearch2TransportClientInstrumentation.java:49
    result[167] =
        declares(
            method()
                .and(method("execute"))
                .parameter(0, "org.elasticsearch.action.Action")
                .parameter(1, "org.elasticsearch.action.ActionRequest")
                .parameter(2, "org.elasticsearch.action.ActionListener"));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-5.0/src/main/java/datadog/trace/instrumentation/elasticsearch5/Elasticsearch5TransportClientInstrumentation.java:49
    result[168] =
        declares(
            method()
                .and(method("execute"))
                .parameter(0, "org.elasticsearch.action.Action")
                .parameter(1, "org.elasticsearch.action.ActionRequest")
                .parameter(2, "org.elasticsearch.action.ActionListener"));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-5.3/src/main/java/datadog/trace/instrumentation/elasticsearch5_3/Elasticsearch53TransportClientInstrumentation.java:50
    result[169] =
        declares(
            method()
                .and(method("execute"))
                .parameter(0, "org.elasticsearch.action.Action")
                .parameter(1, "org.elasticsearch.action.ActionRequest")
                .parameter(2, "org.elasticsearch.action.ActionListener"));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-6.0/src/main/java/datadog/trace/instrumentation/elasticsearch6/Elasticsearch6TransportClientInstrumentation.java:53
    result[170] =
        declares(
            method()
                .and(method("execute"))
                .parameter(0, "org.elasticsearch.action.Action")
                .parameter(1, "org.elasticsearch.action.ActionRequest")
                .parameter(2, "org.elasticsearch.action.ActionListener"));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-7.3/src/main/java/datadog/trace/instrumentation/elasticsearch7_3/Elasticsearch73TransportClientInstrumentation.java:59
    result[171] =
        declares(
            method()
                .and(method("execute"))
                .parameters(3)
                .parameter(0, "org.elasticsearch.action.ActionType")
                .parameter(1, "org.elasticsearch.action.ActionRequest")
                .parameter(2, "org.elasticsearch.action.ActionListener"));
    // elasticsearch/elasticsearch-transport/elasticsearch-transport-common/src/main/java/datadog/trace/instrumentation/elasticsearch/ThreadedActionListenerInstrumentation.java:48
    result[173] = declares(method(namedOneOf("onResponse", "onFailure")).parameters(1));
    // finatra-2.9/src/main/java/datadog/trace/instrumentation/finatra/FinatraInstrumentation.java:49
    result[174] =
        declares(
            method()
                .and(method("handleMatch"))
                .parameters(2)
                .parameter(0, "com.twitter.finagle.http.Request"));
    // freemarker/freemarker-2.3.24/src/main/java/datadog/trace/instrumentation/freemarker24/ObjectWrapperInstrumentation.java:44
    result[175] =
        declares(
            method("wrap")
                .parameter(0, "java.lang.Object")
                .returning(type("freemarker.template.TemplateModel")));
    // gax-1.4/src/main/java/datadog/trace/instrumentation/gax/CallbackChainRetryingFutureInstrumentation.java:54
    result[176] =
        declares(
            method("setAttemptFuture").parameters(1).parameter(0, "com.google.api.core.ApiFuture"));
    // glassfish-3.0/src/main/java/datadog/trace/instrumentation/glassfish/GlassFishInstrumentation.java:36
    result[177] = declares(method().and(method("addToBlackList")).parameters(1));
    // google-pubsub-1.116/src/main/java/datadog/trace/instrumentation/googlepubsub/ReceiverInstrumentation.java:21
    result[178] =
        declares(
            method()
                .and(method("newBuilder"))
                .parameter(0, "java.lang.String")
                .parameter(1, "com.google.cloud.pubsub.v1.MessageReceiver"));
    // google-pubsub-1.116/src/main/java/datadog/trace/instrumentation/googlepubsub/ReceiverWithAckInstrumentation.java:22
    result[179] =
        declares(
            method()
                .and(method("newBuilder"))
                .parameter(0, "java.lang.String")
                .parameter(1, typeEndsWith("MessageReceiverWithAckResponse")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/AnnotationSubstitutionProcessorInstrumentation.java:21
    result[180] =
        declares(method().and(method("lookup")).parameter(0, "jdk.vm.ci.meta.ResolvedJavaField"));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/AnnotationSubstitutionProcessorInstrumentation.java:26
    result[181] = declares(method().and(method("findTargetClasses")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/LinkAtBuildTimeInstrumentation.java:20
    result[182] = declares(method().and(method("linkAtBuildTime")).parameter(0, "java.lang.Class"));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/NativeImageGeneratorRunnerInstrumentation.java:25
    result[183] = declares(method().and(method("main")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/NativeImageGeneratorRunnerInstrumentation.java:28
    result[184] = declares(method().and(method("extractDriverArguments")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/ResourcesFeatureInstrumentation.java:25
    result[185] = declares(method().and(method("beforeAnalysis")));
    // graal/graal-native-image-20.0/src/main/java/datadog/trace/instrumentation/graal/nativeimage/VMRuntimeModule.java:33
    result[186] = declares(method().and(method("initialize")));
    // graphql-java/graphql-java-14.0/src/main/java/datadog/trace/instrumentation/graphqljava14/GraphQLJavaInstrumentation.java:54
    result[187] =
        declares(
            method()
                .and(method(namedOneOf("checkInstrumentationDefaultState")))
                .returning(type("graphql.execution.instrumentation.Instrumentation")));
    // graphql-java/graphql-java-20.0/src/main/java/datadog/trace/instrumentation/graphqljava20/GraphQLJavaInstrumentation.java:43
    result[188] = declares(method().and(method("build")).returning(type("graphql.GraphQL")));
    // graphql-java/graphql-java-common/src/main/java/datadog/trace/instrumentation/graphqljava/GraphQLUnwrapExceptionInstrumentation.java:32
    result[189] = declares(method().and(method("getException")).returning("java.lang.Throwable"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/DefaultFilterChainInstrumentation.java:27
    result[190] =
        declares(
            method()
                .access(PRIVATE)
                .and(method("notifyFailure"))
                .parameter(0, "org.glassfish.grizzly.filterchain.FilterChainContext")
                .parameter(1, "java.lang.Throwable"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/DefaultFilterChainInstrumentation.java:34
    result[191] =
        declares(
            method()
                .and(method("executeFilter"))
                .parameter(2, "org.glassfish.grizzly.filterchain.FilterChainContext"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/FilterInstrumentation.java:30
    result[192] =
        declares(
            method("handleRead")
                .parameter(0, "org.glassfish.grizzly.filterchain.FilterChainContext")
                .access(PUBLIC));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:35
    result[193] =
        declares(
            method("setInputBuffer")
                .parameters(1)
                .parameter(0, "org.glassfish.grizzly.http.io.InputBuffer"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:41
    result[194] = declares(method("read").parameters(0));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:43
    result[195] = declares(method("read").parameters(1).parameter(0, "byte[]"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:46
    result[196] = declares(method("read").parameters("byte[]", "int", "int"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:49
    result[197] =
        declares(
            method("readBuffer").and(anyMethod().parameters(0).or(anyMethod().parameters("int"))));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:52
    result[198] = declares(method("isFinished").parameters(0));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyByteBodyInstrumentation.java:55
    result[199] = declares(method("recycle").parameters(0));
  }

  private static void fill200(ClassMatcher[] result) {
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:30
    result[200] =
        declares(
            method("setInputBuffer")
                .parameters(1)
                .parameter(0, "org.glassfish.grizzly.http.io.InputBuffer"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:35
    result[201] = declares(method("read").parameters(0));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:37
    result[202] = declares(method("read").parameters(1).parameter(0, "char[]"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:40
    result[203] = declares(method("read").parameters("char[]", "int", "int"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:43
    result[204] = declares(method("read").parameters("java.nio.CharBuffer"));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:46
    result[205] = declares(method("isFinished").parameters(0));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/GrizzlyCharBodyInstrumentation.java:49
    result[206] = declares(method("recycle").parameters(0));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/HttpCodecFilterInstrumentation.java:19
    result[207] =
        declares(
            method("handleRead")
                .parameter(0, "org.glassfish.grizzly.filterchain.FilterChainContext")
                .parameter(1, "org.glassfish.grizzly.http.HttpHeader")
                .access(PUBLIC));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/HttpServerFilterInstrumentation.java:19
    result[208] =
        declares(
            method("prepareResponse")
                .parameter(0, "org.glassfish.grizzly.filterchain.FilterChainContext")
                .parameter(1, "org.glassfish.grizzly.http.HttpRequestPacket")
                .parameter(2, "org.glassfish.grizzly.http.HttpResponsePacket")
                .parameter(3, "org.glassfish.grizzly.http.HttpContent")
                .access(PRIVATE));
    // grizzly/grizzly-http-2.3.20/src/main/java/datadog/trace/instrumentation/grizzlyhttp232/ParsedBodyParametersInstrumentation.java:57
    result[209] =
        declares(
            method("processParameters")
                .parameter(0, "org.glassfish.grizzly.Buffer")
                .parameter(1, "int")
                .parameter(2, "int")
                .parameter(3, "java.nio.charset.Charset"));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/QueuedCommandInstrumentation.java:41
    result[211] =
        declares(
            method()
                .and(
                    method("run")
                        .and(anyMethod().parameters(1).parameter(0, "io.netty.channel.Channel"))));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/AbstractClientStreamInstrumentation.java:26
    result[212] =
        declares(
            method("start")
                .and(
                    method()
                        .and(
                            anyMethod()
                                .parameter(0, "io.grpc.internal.ClientStreamListener")
                                .parameters(1))));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:43
    result[214] = declares(method("cancel").and(method()));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:44
    result[215] =
        declares(
            method("request")
                .and(method())
                .parameters("int")
                .or(method().and(method("halfClose").parameters(0))));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:50
    result[216] = declares(method("sendMessage").and(method()));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientCallImplInstrumentation.java:52
    result[217] = declares(method("closeObserver").parameters(3));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientStreamListenerImplInstrumentation.java:31
    result[219] = declares(method("exceptionThrown").parameter(0, "io.grpc.Status").parameters(1));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientStreamListenerImplInstrumentation.java:36
    result[220] = declares(method(namedOneOf("messageRead", "messagesAvailable")));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/ClientStreamListenerImplInstrumentation.java:38
    result[221] = declares(method("headersRead"));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/client/MessagesAvailableInstrumentation.java:42
    result[223] = declares(method("runInContext"));
    // grpc-1.5/src/main/java/datadog/trace/instrumentation/grpc/server/GrpcServerBuilderInstrumentation.java:55
    result[224] = declares(method().access(PUBLIC).and(method("build")).parameters(0));
    // gson-1.6/src/main/java/datadog/trace/instrumentation/gson/JsonReaderInstrumentation.java:43
    result[226] = declares(constructor().parameters(1).parameter(0, "java.io.Reader"));
    // gson-1.6/src/main/java/datadog/trace/instrumentation/gson/JsonReaderInstrumentation.java:46
    result[227] =
        declares(
            method()
                .access(PUBLIC)
                .returning("java.lang.String")
                .and(method(namedOneOf("nextName", "nextString")))
                .noParameters());
    // guava-10.0/src/main/java/datadog/trace/instrumentation/guava10/ListenableFutureInstrumentation.java:50
    result[228] =
        declares(
            method("addListener")
                .parameters("java.lang.Runnable", "java.util.concurrent.Executor"));
    // guidewire-10.0/src/main/java/datadog/trace/instrumentation/guidewire/WsiAsyncResponseInstrumentation.java:73
    result[230] = declares(method("run").parameters(0).access(PUBLIC));
    // hazelcast/hazelcast-3.6/src/main/java/datadog/trace/instrumentation/hazelcast36/ClientInvocationInstrumentation.java:27
    result[231] =
        declares(
            constructor().parameter(0, "com.hazelcast.client.impl.HazelcastClientInstanceImpl"));
    // hazelcast/hazelcast-3.6/src/main/java/datadog/trace/instrumentation/hazelcast36/DistributedObjectInstrumentation.java:55
    result[232] =
        declares(
            method()
                .access(PUBLIC)
                .and(
                    method(
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
                            "values"))));
    // hazelcast/hazelcast-3.6/src/main/java/datadog/trace/instrumentation/hazelcast36/DistributedObjectInstrumentation.java:168
    result[233] =
        declares(
            method()
                .access(PUBLIC)
                .and(
                    method(
                        namedOneOf(
                            "addAllAsync",
                            "addAsync",
                            "estimateAsync",
                            "getAsync",
                            "putAsync",
                            "readManyAsync",
                            "removeAsync",
                            "setAsync")))
                .returning(type("com.hazelcast.core.ICompletableFuture")));
    // hazelcast/hazelcast-3.9/src/main/java/datadog/trace/instrumentation/hazelcast39/ClientInvocationInstrumentation.java:38
    result[234] = declares(method().and(method("invokeOnSelection")));
    // hazelcast/hazelcast-3.9/src/main/java/datadog/trace/instrumentation/hazelcast39/ClientInvocationInstrumentation.java:40
    result[235] =
        declares(
            constructor()
                .parameter(
                    0,
                    typeOneOf(
                        "com.hazelcast.client.impl.HazelcastClientInstanceImpl",
                        "com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl")));
    // hazelcast/hazelcast-3.9/src/main/java/datadog/trace/instrumentation/hazelcast39/ClientMessageInstrumentation.java:30
    result[236] =
        declares(
            method().and(method(namedOneOf("setOperationName"))).parameter(0, "java.lang.String"));
    // hazelcast/hazelcast-4.0/src/main/java/datadog/trace/instrumentation/hazelcast4/ClientInvocationInstrumentation.java:25
    result[237] = declares(method().and(method("invokeOnSelection")));
    // hazelcast/hazelcast-4.0/src/main/java/datadog/trace/instrumentation/hazelcast4/ClientInvocationInstrumentation.java:28
    result[238] =
        declares(
            constructor()
                .parameter(0, "com.hazelcast.client.impl.clientside.HazelcastClientInstanceImpl"));
    // hazelcast/hazelcast-4.0/src/main/java/datadog/trace/instrumentation/hazelcast4/ClientListenerInstrumentation.java:33
    result[239] =
        declares(
            method()
                .and(method("handleEventMessageOnCallingThread"))
                .parameter(0, "com.hazelcast.client.impl.protocol.ClientMessage"));
  }

  private static void fill240(ClassMatcher[] result) {
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/CriteriaInstrumentation.java:40
    result[240] = declares(method().and(method(namedOneOf("list", "uniqueResult", "scroll"))));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/IastQueryInstrumentation.java:34
    result[241] = declares(method().and(method("before").parameters(0)));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/QueryInstrumentation.java:46
    result[242] =
        declares(
            method().and(method(namedOneOf("list", "executeUpdate", "uniqueResult", "scroll"))));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:52
    result[243] = declares(method().and(method("close")).parameters(0));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:57
    result[244] =
        declares(
            method()
                .and(
                    method(
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
                            "internalLoad"))));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:79
    result[245] =
        declares(
            method()
                .and(method("get"))
                .returning(type("java.lang.Object"))
                .parameter(0, "java.lang.String"));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/SessionInstrumentation.java:88
    result[246] =
        declares(
            method()
                .and(method(namedOneOf("beginTransaction", "getTransaction")))
                .returning(type("org.hibernate.Transaction")));
    // hibernate/hibernate-core-3.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v3_3/TransactionInstrumentation.java:42
    result[247] = declares(method().and(method("commit")).parameters(0));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/CriteriaInstrumentation.java:40
    result[248] = declares(method().and(method(namedOneOf("list", "uniqueResult", "scroll"))));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/IastQueryInstrumentation.java:33
    result[249] = declares(method().and(method("before").parameters(0)));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/QueryInstrumentation.java:47
    result[250] =
        declares(
            method().and(method(namedOneOf("list", "executeUpdate", "uniqueResult", "scroll"))));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionFactoryInstrumentation.java:41
    result[251] =
        declares(
            method()
                .and(method(namedOneOf("openSession", "openStatelessSession")))
                .parameters(0)
                .returning(typeOneOf("org.hibernate.Session", "org.hibernate.StatelessSession")));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:49
    result[252] = declares(method().and(method("close")).parameters(0));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:54
    result[253] =
        declares(
            method()
                .and(
                    method(
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
                            "internalLoad"))));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:75
    result[254] =
        declares(
            method()
                .and(method("get"))
                .returning(type("java.lang.Object"))
                .parameter(0, "java.lang.String"));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/SessionInstrumentation.java:84
    result[255] =
        declares(
            method()
                .and(method(namedOneOf("beginTransaction", "getTransaction")))
                .returning(type("org.hibernate.Transaction")));
    // hibernate/hibernate-core-4.0/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_0/TransactionInstrumentation.java:42
    result[256] = declares(method().and(method("commit")).parameters(0));
    // hibernate/hibernate-core-4.3/src/main/java/datadog/trace/instrumentation/hibernate/core/v4_3/ProcedureCallInstrumentation.java:45
    result[257] = declares(method().and(method("getOutputs")));
    // hystrix-1.4/src/main/java/datadog/trace/instrumentation/hystrix/HystrixInstrumentation.java:53
    result[258] = declares(method("getExecutionObservable").returning(type("rx.Observable")));
    // hystrix-1.4/src/main/java/datadog/trace/instrumentation/hystrix/HystrixInstrumentation.java:56
    result[259] = declares(method("getFallbackObservable").returning(type("rx.Observable")));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheAsyncInstrumentation.java:24
    result[260] =
        declares(
            method()
                .access(PUBLIC)
                .and(
                    method(
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
                            "removeAllAsync"))));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheAsyncInstrumentation.java:40
    result[261] =
        declares(
            method()
                .access(PUBLIC)
                .and(
                    method(
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
                            "invokeAsync"))));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheSyncInstrumentation.java:26
    result[262] =
        declares(
            method()
                .access(PUBLIC)
                .and(
                    method(
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
                            "removeAll"))));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheSyncInstrumentation.java:42
    result[263] =
        declares(
            method()
                .access(PUBLIC)
                .and(
                    method(
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
                            "invoke"))));
    // ignite-2.0/src/main/java/datadog/trace/instrumentation/ignite/v2/cache/IgniteCacheSyncInstrumentation.java:61
    result[264] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("query"))
                .parameter(
                    0,
                    typeOneOf(
                        "org.apache.ignite.cache.query.Query",
                        "org.apache.ignite.cache.query.SqlFieldsQuery")));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1FactoryInstrumentation.java:33
    result[265] =
        declares(
            method("createJsonParser")
                .and(method())
                .and(
                    anyMethod()
                        .parameters("java.lang.String")
                        .or(anyMethod().parameters("java.io.InputStream"))
                        .or(anyMethod().parameters("java.io.Reader"))
                        .or(anyMethod().parameters("java.net.URL"))
                        .or(anyMethod().parameters("byte[]"))));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1FactoryInstrumentation.java:43
    result[266] =
        declares(
            method("createJsonParser")
                .and(method())
                .and(anyMethod().access(PUBLIC).parameters("byte[]", "int", "int")));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1ParserInstrumentation.java:41
    result[267] =
        declares(method("getText").access(PUBLIC).noParameters().returning("java.lang.String"));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1ParserInstrumentation.java:44
    result[268] =
        declares(
            method("getCurrentName").access(PUBLIC).noParameters().returning("java.lang.String"));
    // jackson-core/jackson-core-1.9.13/src/main/java/datadog/trace/instrumentation/jackson/codehouse/core/Json1ParserInstrumentation.java:59
    result[269] = declares(method(namedOneOf("getText", "getCurrentName")));
    // jackson-core/jackson-core-2.0/src/main/java/datadog/trace/instrumentation/jackson_2/core/JsonParserInstrumentation.java:45
    result[270] =
        declares(
            method(namedOneOf("getCurrentName", "nextFieldName"))
                .access(PUBLIC)
                .noParameters()
                .returning("java.lang.String"));
    // jackson-core/jackson-core-2.12/src/main/java/datadog/trace/instrumentation/jackson_2_12/core/JsonParserInstrumentation.java:47
    result[271] =
        declares(
            method(namedOneOf("getCurrentName", "nextFieldName"))
                .access(PUBLIC)
                .noParameters()
                .returning("java.lang.String"));
    // jackson-core/jackson-core-2.16/src/main/java/datadog/trace/instrumentation/jackson_2_16/core/JsonParserInstrumentation.java:45
    result[272] =
        declares(
            method(namedOneOf("getCurrentName", "nextFieldName"))
                .access(PUBLIC)
                .noParameters()
                .returning("java.lang.String"));
    // jackson-core/jackson-core-2.6/src/main/java/datadog/trace/instrumentation/jackson_2_6/core/JsonParserInstrumentation.java:47
    result[273] =
        declares(
            method(namedOneOf("getCurrentName", "nextFieldName"))
                .access(PUBLIC)
                .noParameters()
                .returning("java.lang.String"));
    // jackson-core/jackson-core-2.8/src/main/java/datadog/trace/instrumentation/jackson_2_8/core/JsonParserInstrumentation.java:47
    result[274] =
        declares(
            method(namedOneOf("getCurrentName", "nextFieldName"))
                .access(PUBLIC)
                .noParameters()
                .returning("java.lang.String"));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/Json2FactoryInstrumentation.java:33
    result[275] =
        declares(
            method("createParser")
                .and(method())
                .and(
                    anyMethod()
                        .access(PUBLIC)
                        .and(
                            anyMethod()
                                .parameters("java.lang.String")
                                .or(anyMethod().parameters("java.io.InputStream"))
                                .or(anyMethod().parameters("java.io.Reader"))
                                .or(anyMethod().parameters("java.net.URL"))
                                .or(anyMethod().parameters("byte[]")))));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/Json2FactoryInstrumentation.java:45
    result[276] =
        declares(
            method("createParser")
                .and(method())
                .and(anyMethod().access(PUBLIC).parameters("byte[]", "int", "int")));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/JsonParserInstrumentation.java:40
    result[277] =
        declares(
            method(namedOneOf("getText", "getValueAsString"))
                .access(PUBLIC)
                .noParameters()
                .returning("java.lang.String"));
    // jackson-core/jackson-core-common/src/main/java/datadog/trace/instrumentation/jackson/core/TokenBufferInstrumentation.java:38
    result[278] =
        declares(
            method()
                .and(method("asParser"))
                .access(PUBLIC)
                .returning(type("com.fasterxml.jackson.core.JsonParser")));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ClassInstrumenterInstrumentation.java:44
    result[279] = declares(method().and(method("visitTotalProbeCount")));
  }

  private static void fill280(ClassMatcher[] result) {
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/InstrumenterInstrumentation.java:38
    result[280] = declares(method().and(method("instrument")).parameters("byte[]"));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:68
    result[281] =
        declares(
            method("visitMethodInsn")
                .parameters(5)
                .parameter(0, "int")
                .parameter(1, "java.lang.String")
                .parameter(2, "java.lang.String")
                .parameter(3, "java.lang.String")
                .parameter(4, "boolean"));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:77
    result[282] = declares(method("visitInsn").parameters(1).parameter(0, "int"));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:82
    result[283] =
        declares(method("visitIntInsn").parameters(2).parameter(0, "int").parameter(1, "int"));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:88
    result[284] = declares(method("visitLdcInsn").parameters(1).parameter(0, "java.lang.Object"));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:109
    result[285] = declares(method().and(method("visitMaxs")).parameters(2).parameter(0, "int"));
    // jacoco-0.8.9/src/main/java/datadog/trace/instrumentation/jacoco/ProbeInserterInstrumentation.java:112
    result[286] = declares(method().and(method("insertProbe")).parameters(1).parameter(0, "int"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/completablefuture/AsyncTaskInstrumentation.java:38
    result[288] = declares(method("run"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/completablefuture/AsyncTaskInstrumentation.java:39
    result[289] = declares(method("cancel"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/completablefuture/CompletableFutureUniCompletionSubclassInstrumentation.java:33
    result[291] = declares(method("tryFire").parameters("int"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/JavaExecutorInstrumentation.java:21
    result[292] = declares(method("execute").parameter(0, "java.lang.Runnable").parameters(1));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/NonStandardExecutorInstrumentation.java:10
    result[293] =
        declares(
            method("dispatch")
                .parameter(0, "java.lang.Runnable")
                .parameter(1, "kotlinx.coroutines.scheduling.TaskContext"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/NonStandardExecutorInstrumentation.java:16
    result[294] = declares(method("dispatch").parameters(1).parameter(0, "java.lang.Runnable"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/RejectedExecutionHandlerInstrumentation.java:60
    result[295] =
        declares(
            method()
                .and(method(namedOneOf("rejectedExecution", "rejected")))
                .parameter(0, "java.lang.Runnable"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/ThreadPoolExecutorInstrumentation.java:89
    result[296] =
        declares(method("beforeExecute").and(method()).parameter(1, "java.lang.Runnable"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/ThreadPoolExecutorInstrumentation.java:94
    result[297] = declares(method("afterExecute").and(method()).parameter(0, "java.lang.Runnable"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/executor/ThreadPoolExecutorInstrumentation.java:99
    result[298] = declares(method("remove").and(method()).returning("java.lang.Runnable"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinPoolInstrumentation.java:29
    result[299] = declares(method().and(method(namedOneOf("externalPush", "externalSubmit"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinPoolInstrumentation.java:32
    result[300] = declares(method().and(method("poolSubmit")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinPoolInstrumentation.java:34
    result[301] = declares(method().and(method("scheduleDelayedTask")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinTaskInstrumentation.java:45
    result[302] = declares(method(namedOneOf("doExec", "exec", "fork", "cancel")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinTaskInstrumentation.java:51
    result[303] = declares(method().and(method(namedOneOf("doExec", "exec"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinTaskInstrumentation.java:53
    result[304] = declares(method().and(method("fork")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinWorkQueueInstrumentation.java:68
    result[305] =
        declares(anyField().type("int").and(field("top")))
            .and(declares(anyField().type("int").and(field("base"))));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/forkjoin/JavaForkJoinWorkQueueInstrumentation.java:69
    result[306] = declares(anyField().type("int").and(field("base")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/runnable/ConsumerTaskInstrumentation.java:49
    result[308] = declares(method("run"));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/runnable/RunnableInstrumentation.java:58
    result[310] = declares(method("run").parameters(0).access(PUBLIC));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/timer/JavaTimerInstrumentation.java:29
    result[311] =
        declares(
            method()
                .access(PRIVATE)
                .and(
                    method("sched")
                        .parameters(3)
                        .parameter(0, "java.util.TimerTask")
                        .parameter(1, "long")
                        .parameter(2, "long")));
    // java/java-concurrent/java-concurrent-1.8/src/main/java/datadog/trace/instrumentation/java/concurrent/timer/TimerTaskInstrumentation.java:41
    result[312] = declares(method("cancel").parameters(0).access(PUBLIC));
    // java/java-concurrent/java-concurrent-21.0/src/main/java/datadog/trace/instrumentation/java/concurrent/virtualthread/TaskRunnerInstrumentation.java:53
    result[315] = declares(method().and(method("run")));
    // java/java-io-1.8/src/main/java/datadog/trace/instrumentation/java/lang/InputStreamInstrumentation.java:41
    result[318] = declares(constructor().parameter(0, "java.io.InputStream"));
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/ProcessImplInstrumentation.java:33
    result[319] =
        declares(
            method("start")
                .parameters(
                    "java.lang.String[]",
                    "java.util.Map",
                    "java.lang.String",
                    "ProcessBuilder.Redirect[]",
                    "boolean"));
  }

  private static void fill320(ClassMatcher[] result) {
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/RuntimeInstrumentation.java:33
    result[320] =
        declares(
            method("exec").parameters("java.lang.String", "java.lang.String[]", "java.io.File"));
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/ShutdownInstrumentation.java:32
    result[321] = declares(method().access(STATIC).and(method("runHooks")));
    // java/java-lang/java-lang-1.8/src/main/java/datadog/trace/instrumentation/java/lang/management/CustomMBeanBuilderInstrumentation.java:34
    result[322] =
        declares(
            method().and(method("newMBeanServer")).returning(type("javax.management.MBeanServer")));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/PollerInstrumentation.java:39
    result[323] =
        declares(
            method("start")
                .parameters(0)
                .returning("void")
                .or(method("startReadPoller").parameters(0).returning(type("sun.nio.ch.Poller"))));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:112
    result[324] =
        declares(
            constructor()
                .parameters(
                    "java.util.concurrent.Executor",
                    "java.lang.String",
                    "int",
                    "java.lang.Runnable"));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:119
    result[325] =
        declares(method().and(method("run")).parameters("java.lang.Runnable").returning("void"));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:123
    result[326] = declares(method().and(method("mount")));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:124
    result[327] = declares(method().and(method("unmount")));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:125
    result[328] = declares(method().and(method("afterDone")).parameters("boolean"));
    // java/java-lang/java-lang-21.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk21/VirtualThreadInstrumentation.java:128
    result[329] = declares(method().and(method("afterTerminate")).parameters("boolean", "boolean"));
    // java/java-lang/java-lang-22.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk22/LinkerInstrumentation.java:18
    result[330] =
        declares(
            method().and(method("downcallHandle")).parameter(0, "java.lang.foreign.MemorySegment"));
    // java/java-lang/java-lang-22.0/src/main/java/datadog/trace/instrumentation/java/lang/jdk22/NativeLibraryInstrumentation.java:31
    result[332] = declares(method().and(method("find")).returning("long"));
    // java/java-lang/java-lang-classloading-1.8/src/main/java/datadog/trace/instrumentation/java/lang/classloading/ClassloadingInstrumentation.java:68
    result[334] =
        declares(
            method()
                .and(method("loadClass"))
                .and(
                    anyMethod()
                        .parameters(1)
                        .parameter(0, "java.lang.String")
                        .or(
                            anyMethod()
                                .parameters(2)
                                .parameter(0, "java.lang.String")
                                .parameter(1, "boolean")))
                .and(anyMethod().access(PUBLIC).or(anyMethod().access(PROTECTED)))
                .and(not(anyMethod().access(STATIC))));
    // java/java-lang/java-lang-classloading-1.8/src/main/java/datadog/trace/instrumentation/java/lang/classloading/DefineClassInstrumentation.java:37
    result[335] =
        declares(
            method()
                .and(
                    method("defineClass")
                        .parameters(
                            "java.lang.String",
                            "byte[]",
                            "int",
                            "int",
                            "java.security.ProtectionDomain")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/HttpUrlConnectionInstrumentation.java:60
    result[336] =
        declares(
            method()
                .access(PUBLIC)
                .and(method(namedOneOf("connect", "getOutputStream", "getInputStream"))));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/HttpUrlConnectionInstrumentation.java:63
    result[337] = declares(method().access(PROTECTED).and(method("plainConnect")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SocketConnectInstrumentation.java:44
    result[338] =
        declares(
            method()
                .and(method("connect"))
                .parameter(0, "java.net.SocketAddress")
                .parameter(1, "int"));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SslSocketInstrumentation.java:48
    result[339] = declares(method().and(method("close").parameters(0)));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SslSocketInstrumentation.java:51
    result[340] = declares(method().and(method("getInputStream")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/SslSocketInstrumentation.java:54
    result[341] = declares(method().and(method("getOutputStream")));
    // java/java-net/java-net-1.8/src/main/java/datadog/trace/instrumentation/java/net/UrlInstrumentation.java:40
    result[342] = declares(method().access(PUBLIC).and(method("openConnection")));
    // java/java-net/java-net-11.0/src/main/java/datadog/trace/instrumentation/httpclient/HttpClientInstrumentation.java:66
    result[343] =
        declares(
            method()
                .and(method("send"))
                .access(PUBLIC)
                .parameters(2)
                .parameter(0, "java.net.http.HttpRequest"));
    // java/java-net/java-net-11.0/src/main/java/datadog/trace/instrumentation/httpclient/HttpClientInstrumentation.java:74
    result[344] =
        declares(
            method()
                .and(method("sendAsync"))
                .access(PUBLIC)
                .parameter(0, "java.net.http.HttpRequest")
                .parameter(1, "java.net.http.HttpResponse$BodyHandler"));
    // java/java-nio-1.8/src/main/java/datadog/trace/instrumentation/directbytebuffer/ByteBufferInstrumentation.java:43
    result[345] =
        declares(
            method()
                .and(method("allocateDirect"))
                .access(STATIC)
                .parameter(0, "int")
                .parameters(1));
    // java/java-nio-1.8/src/main/java/datadog/trace/instrumentation/directbytebuffer/DirectByteBufferInstrumentation.java:41
    result[346] = declares(constructor().parameter(0, "long").parameter(1, "int").parameters(2));
    // java/java-nio-1.8/src/main/java/datadog/trace/instrumentation/directbytebuffer/FileChannelImplInstrumentation.java:42
    result[347] =
        declares(
            method()
                .and(method("map"))
                .parameters(3)
                .parameter(0, "java.nio.channels.FileChannel$MapMode")
                .parameter(1, "long")
                .parameter(2, "long"));
    // java/java-rmi-1.1/src/main/java/datadog/trace/instrumentation/rmi/client/RmiClientInstrumentation.java:47
    result[348] =
        declares(
            method()
                .and(method("invoke"))
                .parameter(0, "java.rmi.Remote")
                .parameter(1, "java.lang.reflect.Method"));
    // java/java-rmi-1.1/src/main/java/datadog/trace/instrumentation/rmi/context/client/RmiClientContextInstrumentation.java:80
    result[349] =
        declares(
            constructor()
                .parameter(0, "sun.rmi.transport.Connection")
                .parameter(1, "java.rmi.server.ObjID"));
    // java/java-rmi-1.1/src/main/java/datadog/trace/instrumentation/rmi/context/server/RmiServerContextInstrumentation.java:48
    result[350] =
        declares(
            method()
                .access(STATIC)
                .and(method("getTarget"))
                .parameter(0, "sun.rmi.transport.ObjectEndpoint"));
    // jboss/jboss-logmanager-1.1/src/main/java/datadog/trace/instrumentation/jbosslogmanager/ExtLogRecordInstrumentation.java:54
    result[352] = declares(method().and(method("getMdc")).parameter(0, "java.lang.String"));
    // jboss/jboss-logmanager-1.1/src/main/java/datadog/trace/instrumentation/jbosslogmanager/ExtLogRecordInstrumentation.java:58
    result[353] = declares(method().and(method("getMdcCopy")).parameters(0));
    // jboss/jboss-logmanager-1.1/src/main/java/datadog/trace/instrumentation/jbosslogmanager/LoggerNodeInstrumentation.java:40
    result[354] =
        declares(method().and(method("publish")).parameter(0, "org.jboss.logmanager.ExtLogRecord"));
    // jboss/jboss-modules-1.3/src/main/java/datadog/trace/instrumentation/jbossmodules/ModuleInstrumentation.java:37
    result[355] =
        declares(
            method()
                .and(method("getResource"))
                .and(anyMethod().parameters(1).parameter(0, "java.lang.String")));
    // jboss/jboss-modules-1.3/src/main/java/datadog/trace/instrumentation/jbossmodules/ModuleInstrumentation.java:42
    result[356] =
        declares(
            method()
                .and(method("getResourceAsStream"))
                .and(anyMethod().parameters(1).parameter(0, "java.lang.String")));
    // jboss/jboss-modules-1.3/src/main/java/datadog/trace/instrumentation/jbossmodules/ModuleInstrumentation.java:47
    result[357] =
        declares(
            method()
                .and(method("loadModuleClass"))
                .and(
                    anyMethod()
                        .parameters(1)
                        .parameter(0, "java.lang.String")
                        .or(
                            anyMethod()
                                .parameters(2)
                                .parameter(0, "java.lang.String")
                                .parameter(1, "boolean"))));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/AbstractPreparedStatementInstrumentation.java:52
    result[359] = declares(method(nameStartsWith("execute")).parameters(0).access(PUBLIC));
  }

  private static void fill360(ClassMatcher[] result) {
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/DataSourceInstrumentation.java:49
    result[360] = declares(method("getConnection"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2LinkedBlockingDequeInstrumentation.java:41
    result[361] = declares(method("pollFirst").parameters(1));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2ManagedConnectionInstrumentation.java:35
    result[362] = declares(method("updateTransactionStatus"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2PerUserPoolDataSourceInstrumentation.java:35
    result[363] = declares(method("getPooledConnectionAndInfo"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2PoolingDataSourceInstrumentation.java:35
    result[364] = declares(method("getConnection"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2PoolingDriverInstrumentation.java:35
    result[365] = declares(method("connect"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/Dbcp2SharedPoolDataSourceInstrumentation.java:35
    result[366] = declares(method("getPooledConnectionAndInfo"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/DriverInstrumentation.java:52
    result[367] =
        declares(
            method(nameStartsWith("connect"))
                .parameter(0, "java.lang.String")
                .parameter(1, "java.util.Properties")
                .returning(type("java.sql.Connection")));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariConcurrentBagHandoffQueueInstrumentation.java:42
    result[368] = declares(field("handoffQueue"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariConcurrentBagInstrumentation.java:63
    result[370] = declares(method("borrow"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariDataSourceInstrumentation.java:40
    result[371] = declares(method("getConnection"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/HikariQueuedSequenceSynchronizerInstrumentation.java:30
    result[373] = declares(method("waitUntilSequenceExceeded"));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/IastResultSetInstrumentation.java:51
    result[374] = declares(method().and(method("next").parameters(0)));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/IastResultSetInstrumentation.java:54
    result[375] =
        declares(
            method()
                .and(method("getString").or(method("getNString")))
                .and(anyMethod().parameters("int").or(anyMethod().parameters("java.lang.String"))));
    // jdbc/src/main/java/datadog/trace/instrumentation/jdbc/StatementInstrumentation.java:66
    result[376] =
        declares(method(nameStartsWith("execute")).parameter(0, "java.lang.String").access(PUBLIC));
    // jedis/jedis-1.4/src/main/java/datadog/trace/instrumentation/jedis/JedisInstrumentation.java:46
    result[377] =
        declares(
            method()
                .and(method("sendCommand"))
                .parameter(0, "redis.clients.jedis.Protocol$Command"));
    // jedis/jedis-3.0/src/main/java/datadog/trace/instrumentation/jedis30/JedisInstrumentation.java:37
    result[378] =
        declares(
            method()
                .and(method("sendCommand"))
                .parameter(0, "redis.clients.jedis.commands.ProtocolCommand"));
    // jedis/jedis-4.0/src/main/java/datadog/trace/instrumentation/jedis40/JedisInstrumentation.java:39
    result[379] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("executeCommand"))
                .parameter(0, "redis.clients.jedis.CommandObject"));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/AbstractStringReaderInstrumentation.java:31
    result[380] =
        declares(
            method("fromString").and(anyMethod().access(PUBLIC).parameters("java.lang.String")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/ContainerRequestInstrumentation.java:32
    result[381] =
        declares(
            method("setProperty")
                .access(PUBLIC)
                .parameters("java.lang.String", "java.lang.Object"));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/CookieInstrumentation.java:32
    result[382] =
        declares(
            method("getName")
                .access(PUBLIC)
                .and(anyMethod().parameters(0).returning("java.lang.String")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/CookieInstrumentation.java:35
    result[383] =
        declares(
            method("getValue")
                .access(PUBLIC)
                .and(anyMethod().parameters(0).returning("java.lang.String")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/FormInstrumentation.java:34
    result[384] = declares(method("asMap").access(PUBLIC).parameters(0));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/InboundMessageContextInstrumentation.java:43
    result[385] = declares(method("getHeaders").access(PUBLIC).parameters(0));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/InboundMessageContextInstrumentation.java:45
    result[386] = declares(method("getRequestCookies").access(PUBLIC).parameters(0));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/InboundMessageContextInstrumentation.java:48
    result[387] = declares(method("readEntity").access(PUBLIC).parameters(4));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JakartaWSResponseInstrumentation.java:31
    result[388] =
        declares(
            method("header")
                .and(
                    anyMethod().access(PUBLIC).parameters("java.lang.String", "java.lang.Object")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JakartaWSResponseInstrumentation.java:34
    result[389] =
        declares(method("location").and(anyMethod().access(PUBLIC).parameters("java.net.URI")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JavaxWSResponseInstrumentation.java:31
    result[390] =
        declares(
            method("header")
                .and(
                    anyMethod().access(PUBLIC).parameters("java.lang.String", "java.lang.Object")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/JavaxWSResponseInstrumentation.java:34
    result[391] =
        declares(method("location").and(anyMethod().access(PUBLIC).parameters("java.net.URI")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/ReaderInterceptorExecutorInstrumentation.java:35
    result[392] = declares(method("getInputStream").parameters(0));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/UriRoutingContextInstrumentation.java:35
    result[393] =
        declares(method("getPathParameters").and(anyMethod().access(PUBLIC).parameters("boolean")));
    // jersey/jersey-2.0/src/main/java/datadog/trace/instrumentation/jersey/UriRoutingContextInstrumentation.java:38
    result[394] =
        declares(
            method("getQueryParameters").and(anyMethod().access(PUBLIC).parameters("boolean")));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/MessageBodyReaderInstrumentation.java:46
    result[395] = declares(method("proceed").parameters(0));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/MultiPartReaderServerSideInstrumentation.java:50
    result[396] =
        declares(
            method("readMultiPart")
                .access(PROTECTED)
                .returning(type("org.glassfish.jersey.media.multipart.MultiPart"))
                .parameters(6));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/ServerRuntimeResponderInstrumentation.java:38
    result[397] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("process"))
                .parameters(1)
                .parameter(0, "java.lang.Throwable")
                .and(not(anyMethod().access(STATIC))));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/UriRoutingContextGetPathSegmentsInstrumentation.java:34
    result[398] = declares(method("getPathSegments").parameters(1).parameter(0, "boolean"));
    // jersey/jersey-appsec/jersey-appsec-2.0/src/main/java/datadog/trace/instrumentation/jersey2/UriRoutingContextInstrumentation.java:44
    result[399] = declares(method("getPathParameters").parameters(1).parameter(0, "boolean"));
  }

  private static void fill400(ClassMatcher[] result) {
    // jersey/jersey-appsec/jersey-appsec-3.0/src/main/java/datadog/trace/instrumentation/jersey3/MessageBodyReaderInstrumentation.java:45
    result[400] = declares(method("proceed").parameters(0));
    // jersey/jersey-appsec/jersey-appsec-3.0/src/main/java/datadog/trace/instrumentation/jersey3/MultiPartReaderServerSideInstrumentation.java:50
    result[401] =
        declares(
            method("readMultiPart")
                .access(PROTECTED)
                .returning(type("org.glassfish.jersey.media.multipart.MultiPart"))
                .parameters(6));
    // jersey/jersey-appsec/jersey-appsec-3.0/src/main/java/datadog/trace/instrumentation/jersey3/UriRoutingContextGetPathSegmentsInstrumentation.java:34
    result[402] = declares(method("getPathSegments").parameters(1).parameter(0, "boolean"));
    // jersey/jersey-client-2.0/src/main/java/datadog/trace/instrumentation/connection_error/jersey/ClientRuntimeInstrumentation.java:30
    result[403] =
        declares(
            method()
                .and(method(namedOneOf("submit", "createRunnableForAsyncProcessing")))
                .and(
                    anyMethod()
                        .parameter(0, "org.glassfish.jersey.client.ClientRequest")
                        .parameter(1, "org.glassfish.jersey.client.ResponseCallback")));
    // jersey/jersey-client-2.0/src/main/java/datadog/trace/instrumentation/connection_error/jersey/ClientRuntimeInstrumentation.java:37
    result[404] =
        declares(
            method()
                .and(method("invoke"))
                .parameter(0, "org.glassfish.jersey.client.ClientRequest"));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:57
    result[405] =
        declares(field("_contentParameters").and(type("org.eclipse.jetty.util.MultiMap")))
            .and(declares(field("_dispatcherType").and(type("jakarta.servlet.DispatcherType"))));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:60
    result[406] = declares(field("_dispatcherType").and(type("jakarta.servlet.DispatcherType")));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:66
    result[407] = declares(method("extractContentParameters").parameters(0).or(method("getParts")));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:69
    result[408] = declares(method("getParts").parameters(0));
    // jetty/jetty-appsec/jetty-appsec-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestExtractContentParametersInstrumentation.java:71
    result[409] = declares(method("getParts").parameters(1));
    // jetty/jetty-appsec/jetty-appsec-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestExtractParametersInstrumentation.java:28
    result[410] = declares(method("extractParameters").parameters(0));
    // jetty/jetty-appsec/jetty-appsec-7.0/src/main/java/datadog/trace/instrumentation/jetty70/UrlEncodedInstrumentation.java:48
    result[411] =
        declares(
            method("decodeTo")
                .parameter(0, "java.io.InputStream")
                .parameter(1, "org.eclipse.jetty.util.MultiMap")
                .parameter(2, "java.lang.String")
                .access(PUBLIC));
    // jetty/jetty-appsec/jetty-appsec-8.1.3/src/main/java/datadog/trace/instrumentation/jetty8/RequestGetPartsInstrumentation.java:44
    result[412] = declares(method("getParts").parameters(0));
    // jetty/jetty-appsec/jetty-appsec-8.1.3/src/main/java/datadog/trace/instrumentation/jetty8/RequestGetPartsInstrumentation.java:46
    result[413] = declares(method("getPart").parameters(1).parameter(0, "java.lang.String"));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:45
    result[414] = declares(method("extractContentParameters").parameters(0));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:48
    result[415] =
        declares(method("getParts").parameters(1).parameter(0, "org.eclipse.jetty.util.MultiMap"));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:53
    result[416] = declares(method("getParts").parameters(0));
    // jetty/jetty-appsec/jetty-appsec-9.2/src/main/java/datadog/trace/instrumentation/jetty92/RequestExtractContentParametersInstrumentation.java:55
    result[417] = declares(method("getParts").parameters(1));
    // jetty/jetty-appsec/jetty-appsec-9.3/src/main/java/datadog/trace/instrumentation/jetty93/RequestExtractContentParametersInstrumentation.java:45
    result[418] = declares(method("extractContentParameters").parameters(0).or(method("getParts")));
    // jetty/jetty-appsec/jetty-appsec-9.3/src/main/java/datadog/trace/instrumentation/jetty93/RequestExtractContentParametersInstrumentation.java:48
    result[419] = declares(method("getParts").parameters(0));
    // jetty/jetty-appsec/jetty-appsec-9.3/src/main/java/datadog/trace/instrumentation/jetty93/RequestExtractContentParametersInstrumentation.java:50
    result[420] = declares(method("getParts").parameters(1));
    // jetty/jetty-appsec/jetty-appsec-9.4/src/main/java/datadog/trace/instrumentation/jetty94/RequestExtractContentParametersInstrumentation.java:45
    result[421] = declares(method("extractContentParameters").parameters(0).or(method("getParts")));
    // jetty/jetty-appsec/jetty-appsec-9.4/src/main/java/datadog/trace/instrumentation/jetty94/RequestExtractContentParametersInstrumentation.java:48
    result[422] = declares(method("getParts").parameters(0));
    // jetty/jetty-appsec/jetty-appsec-9.4/src/main/java/datadog/trace/instrumentation/jetty94/RequestExtractContentParametersInstrumentation.java:50
    result[423] = declares(method("getParts").parameters(1));
    // jetty/jetty-client/jetty-client-12.0/src/main/java/datadog/trace/instrumentation/jetty_client12/JettyHttpClientInstrumentation.java:64
    result[425] =
        declares(
            method()
                .access(PUBLIC)
                .and(method(namedOneOf("listener", "onSuccess", "onFailure", "onComplete")))
                .parameters(1));
    // jetty/jetty-client/jetty-client-12.0/src/main/java/datadog/trace/instrumentation/jetty_client12/JettyWebSocketUpgradeInstrumentation.java:55
    result[426] =
        declares(
            method("upgrade")
                .parameters(2)
                .parameter(0, "org.eclipse.jetty.client.Response")
                .parameter(1, "org.eclipse.jetty.io.EndPoint"));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/FutureResponseListenerInstrumentation.java:49
    result[427] =
        declares(
            constructor()
                .and(
                    anyMethod()
                        .parameter(0, "org.eclipse.jetty.client.api.Request")
                        .parameters(2)));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:50
    result[428] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("listener"))
                .parameter(0, "org.eclipse.jetty.client.api.Request$RequestListener"));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:56
    result[429] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("onSuccess"))
                .parameter(0, "org.eclipse.jetty.client.api.Request$SuccessListener"));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:62
    result[430] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("onFailure"))
                .parameter(0, "org.eclipse.jetty.client.api.Request$FailureListener"));
    // jetty/jetty-client/jetty-client-9.1/src/main/java/datadog/trace/instrumentation/jetty_client91/JettyAddListenerInstrumentation.java:68
    result[431] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("onComplete"))
                .parameter(0, "org.eclipse.jetty.client.api.Response$CompleteListener"));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/DispatchableInstrumentation.java:62
    result[432] = declares(method().and(method("dispatch")).access(PUBLIC).parameters(0));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/JettyCommitResponseInstrumentation.java:92
    result[433] =
        declares(
            method("sendResponse")
                .parameters(4)
                .parameter(0, "org.eclipse.jetty.http.MetaData$Response")
                .parameter(1, "java.nio.ByteBuffer")
                .parameter(2, "boolean")
                .parameter(3, "org.eclipse.jetty.util.Callback"));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/JettyServerInstrumentation.java:96
    result[434] = declares(method("recycle").noParameters());
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/JettyServerInstrumentation.java:99
    result[435] =
        declares(method("handleException").parameters(1).parameter(0, "java.lang.Throwable"));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/RequestInstrumentation.java:29
    result[436] = declares(method("setContextPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/RequestInstrumentation.java:32
    result[437] = declares(method("setServletPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/RequestInstrumentation.java:35
    result[438] = declares(method("setRequestedSessionId").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-10.0/src/main/java/datadog/trace/instrumentation/jetty10/ServerHandleInstrumentation.java:43
    result[439] =
        declares(
            method("handle")
                .or(method("handleAsync"))
                .parameters(1)
                .parameter(0, "org.eclipse.jetty.server.HttpChannel"));
  }

  private static void fill440(ClassMatcher[] result) {
    // jetty/jetty-server/jetty-server-11.0/src/main/java/datadog/trace/instrumentation/jetty11/JettyServerInstrumentation.java:70
    result[440] = declares(method("recycle").noParameters());
    // jetty/jetty-server/jetty-server-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestInstrumentation.java:25
    result[441] =
        declares(
            method("setContext")
                .parameter(0, "org.eclipse.jetty.server.handler.ContextHandler$Context")
                .parameter(1, "java.lang.String"));
    // jetty/jetty-server/jetty-server-11.0/src/main/java/datadog/trace/instrumentation/jetty11/RequestInstrumentation.java:30
    result[442] = declares(method("setRequestedSessionId").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-12.0/src/main/java/datadog/trace/instrumentation/jetty12/AbstractSessionManagerInstrumentation.java:32
    result[443] =
        declares(
            method("resolveRequestedSessionId")
                .access(PROTECTED)
                .parameters(1)
                .parameter(0, "org.eclipse.jetty.server.Request"));
    // jetty/jetty-server/jetty-server-12.0/src/main/java/datadog/trace/instrumentation/jetty12/ContextHandlerInstrumentation.java:34
    result[444] =
        declares(method("handle").parameters(3).parameter(0, "org.eclipse.jetty.server.Request"));
    // jetty/jetty-server/jetty-server-12.0/src/main/java/datadog/trace/instrumentation/jetty12/JettyServerInstrumentation.java:50
    result[445] = declares(method("recycle").noParameters());
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/JettyCommitResponseInstrumentation.java:53
    result[446] =
        declares(
            method("commitResponse")
                .parameters(1)
                .parameter(0, "boolean")
                .or(method("completeResponse").parameters(0)));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/JettyGeneratorInstrumentation.java:39
    result[447] = declares(method("setResponse").parameter(0, "int"));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/JettyServerInstrumentation.java:91
    result[449] = declares(method("reset").parameter(0, "boolean"));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestInstrumentation.java:47
    result[450] = declares(method("setContextPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestInstrumentation.java:50
    result[451] = declares(method("setServletPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/RequestInstrumentation.java:53
    result[452] = declares(method("setRequestedSessionId").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-7.0/src/main/java/datadog/trace/instrumentation/jetty70/ServerHandleInstrumentation.java:47
    result[453] =
        declares(
            method("handle")
                .or(method("handleAsync"))
                .parameters(1)
                .parameter(0, "org.eclipse.jetty.server.HttpConnection"));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/JettyCommitResponseInstrumentation.java:64
    result[454] =
        declares(
            method("commitResponse")
                .parameters(1)
                .parameter(0, "boolean")
                .or(method("completeResponse").parameters(0)));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/JettyGeneratorInstrumentation.java:39
    result[455] = declares(method("setResponse").parameter(0, "int"));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/JettyServerInstrumentation.java:91
    result[457] = declares(method("reset").noParameters());
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/RequestInstrumentation.java:47
    result[458] = declares(method("setContextPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/RequestInstrumentation.java:50
    result[459] = declares(method("setServletPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/RequestInstrumentation.java:53
    result[460] = declares(method("setRequestedSessionId").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-7.6/src/main/java/datadog/trace/instrumentation/jetty76/ServerHandleInstrumentation.java:47
    result[461] =
        declares(
            method("handle")
                .or(method("handleAsync"))
                .parameters(1)
                .parameter(0, "org.eclipse.jetty.server.AbstractHttpConnection"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyCommitResponseInstrumentation.java:78
    result[462] =
        declares(
            method("commitResponse")
                .parameters(3)
                .parameter(0, "org.eclipse.jetty.http.HttpGenerator$ResponseInfo")
                .parameter(1, "java.nio.ByteBuffer")
                .parameter(2, "boolean"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyServerInstrumentation.java:101
    result[463] = declares(method("handle"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyServerInstrumentation.java:104
    result[464] = declares(method(namedOneOf("reset", "recycle")).noParameters());
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/JettyServerInstrumentation.java:110
    result[465] =
        declares(method("handleException").parameters(1).parameter(0, "java.lang.Throwable"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/RequestInstrumentation.java:54
    result[466] = declares(method("setContextPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/RequestInstrumentation.java:57
    result[467] = declares(method("setServletPath").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/RequestInstrumentation.java:60
    result[468] = declares(method("setRequestedSessionId").parameter(0, "java.lang.String"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/ServerHandleInstrumentation.java:63
    result[469] =
        declares(
            method("handle")
                .or(method("handleAsync"))
                .parameters(1)
                .parameter(0, "org.eclipse.jetty.server.HttpChannel"));
    // jetty/jetty-server/jetty-server-9.0/src/main/java/datadog/trace/instrumentation/jetty9/WebSocketSessionInstrumentation.java:58
    result[470] = declares(method("close").noParameters());
    // jetty/jetty-server/jetty-server-9.0.4/src/main/java/datadog/trace/instrumentation/jetty904/JettyCommitResponseInstrumentation.java:81
    result[471] =
        declares(
            method("sendResponse")
                .parameters(4)
                .parameter(0, "org.eclipse.jetty.http.HttpGenerator$ResponseInfo")
                .parameter(1, "java.nio.ByteBuffer")
                .parameter(2, "boolean")
                .parameter(3, "org.eclipse.jetty.util.Callback"));
    // jetty/jetty-server/jetty-server-9.3/src/main/java/datadog/trace/instrumentation/jetty93/JettyCommitResponseInstrumentation.java:87
    result[472] =
        declares(
            method("sendResponse")
                .parameters(4)
                .parameter(0, "org.eclipse.jetty.http.MetaData$Response")
                .parameter(1, "java.nio.ByteBuffer")
                .parameter(2, "boolean")
                .parameter(3, "org.eclipse.jetty.util.Callback"));
    // jetty/jetty-server/jetty-server-9.4.21/src/main/java/datadog/trace/instrumentation/jetty9421/JettyCommitResponseInstrumentation.java:94
    result[473] =
        declares(
            method("sendResponse")
                .parameters(4)
                .parameter(0, "org.eclipse.jetty.http.MetaData$Response")
                .parameter(1, "java.nio.ByteBuffer")
                .parameter(2, "boolean")
                .parameter(3, "org.eclipse.jetty.util.Callback"));
    // jetty/jetty-util-9.4.31/src/main/java/datadog/trace/instrumentation/jetty_util/QueuedThreadPoolInstrumentation.java:25
    result[474] = declares(method(namedOneOf("dispatch", "execute")));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/JMSMessageConsumerInstrumentation.java:66
    result[475] =
        declares(
            method("receive")
                .and(anyMethod().parameters(0).or(anyMethod().parameters(1)))
                .access(PUBLIC));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/JMSMessageConsumerInstrumentation.java:69
    result[476] = declares(method("receiveNoWait").parameters(0).access(PUBLIC));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/JMSMessageConsumerInstrumentation.java:72
    result[477] = declares(method("close").parameters(0).access(PUBLIC));
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/MessageInstrumentation.java:38
    result[478] =
        declares(method(nameStartsWith("acknowledge")).and(method()).access(PUBLIC).noParameters());
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/SessionInstrumentation.java:89
    result[479] = declares(method(namedOneOf("recover")).noParameters());
  }

  private static void fill480(ClassMatcher[] result) {
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/SessionInstrumentation.java:91
    result[480] = declares(method(namedOneOf("commit", "rollback")).noParameters());
    // jms/javax-jms-1.1/src/main/java/datadog/trace/instrumentation/jms/SessionInstrumentation.java:93
    result[481] = declares(method("close").noParameters());
    // jose-jwt-4.0/src/main/java/datadog/trace/instrumentation/josejwt/JSONObjectUtilsInstrumentation.java:32
    result[482] =
        declares(method("parse").and(anyMethod().access(PUBLIC).parameters("java.lang.String")));
    // jose-jwt-4.0/src/main/java/datadog/trace/instrumentation/josejwt/JWTParserInstrumentation.java:31
    result[483] =
        declares(
            method("parsePayload").and(anyMethod().access(PUBLIC).parameters("java.lang.String")));
    // jsp-2.3/src/main/java/datadog/trace/instrumentation/jsp/JSPInstrumentation.java:44
    result[484] =
        declares(
            method("_jspService")
                .parameter(0, "javax.servlet.http.HttpServletRequest")
                .parameter(1, "javax.servlet.http.HttpServletResponse")
                .access(PUBLIC));
    // jsp-2.3/src/main/java/datadog/trace/instrumentation/jsp/JasperJSPCompilationContextInstrumentation.java:36
    result[485] = declares(method("compile").parameters(0).access(PUBLIC));
    // junit/junit-4/junit-4-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit4/JUnit4CucumberInstrumentation.java:60
    result[486] =
        declares(
            method("childrenInvoker").parameter(0, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit4/JUnit4CucumberSkipInstrumentation.java:70
    result[487] = declares(method("run").parameter(0, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit4/execution/Cucumber4ExecutionInstrumentation.java:76
    result[488] =
        declares(
            method("runChild")
                .parameter(0, "io.cucumber.junit.PickleRunners$PickleRunner")
                .parameter(1, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4-munit-0.7.28/src/main/java/datadog/trace/instrumentation/junit4/MUnitInstrumentation.java:53
    result[489] = declares(method("run").parameter(0, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4-munit-0.7.28/src/main/java/datadog/trace/instrumentation/junit4/execution/MUnitExecutionInstrumentation.java:70
    result[490] =
        declares(method("runTest").parameter(0, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnit4Instrumentation.java:80
    result[492] = declares(method("run").parameter(0, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnit4SkipInstrumentation.java:58
    result[493] =
        declares(
            method("runChild")
                .parameters(2)
                .parameter(1, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnit4SuiteEventsInstrumentation.java:47
    result[494] = declares(method("fireTestSuiteStarted"));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/JUnitLegacySuiteEventsInstrumentation.java:46
    result[495] = declares(method("run").parameter(0, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4.10/src/main/java/datadog/trace/instrumentation/junit4/execution/JUnit4ExecutionInstrumentation.java:67
    result[496] =
        declares(
            method("runLeaf")
                .parameter(0, "org.junit.runners.model.Statement")
                .parameter(1, "org.junit.runner.Description")
                .parameter(2, "org.junit.runner.notification.RunNotifier"));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/BazelRunNotifierWrapperInstrumentation.java:56
    result[497] =
        declares(method("fireTestSuiteStarted").parameter(0, "org.junit.runner.Description"));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/BazelRunNotifierWrapperInstrumentation.java:59
    result[498] =
        declares(method("fireTestSuiteFinished").parameter(0, "org.junit.runner.Description"));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/JUnit4BeforeAfterInstrumentation.java:35
    result[499] =
        declares(method("invokeMethod").parameter(0, "org.junit.runners.model.FrameworkMethod"));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/order/JUnit4TestOrdererInstrumentation.java:56
    result[500] = declares(method("order"));
    // junit/junit-4/junit-4.13/src/main/java/datadog/trace/instrumentation/junit4/order/JUnit4TestSorterInstrumentation.java:64
    result[501] = declares(method("sort").parameter(0, "org.junit.runner.manipulation.Sorter"));
    // junit/junit-5/junit-5-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit5/JUnit5CucumberInstrumentation.java:55
    result[502] =
        declares(method("execute").parameter(0, "org.junit.platform.engine.ExecutionRequest"));
    // junit/junit-5/junit-5-cucumber-5.4/src/main/java/datadog/trace/instrumentation/junit5/JUnit5CucumberSkipInstrumentation.java:79
    result[503] = declares(method("shouldBeSkipped").parameters(1));
    // junit/junit-5/junit-5-spock-2.0/src/main/java/datadog/trace/instrumentation/junit5/JUnit5SpockInstrumentation.java:54
    result[504] =
        declares(method("execute").parameter(0, "org.junit.platform.engine.ExecutionRequest"));
    // junit/junit-5/junit-5-spock-2.0/src/main/java/datadog/trace/instrumentation/junit5/JUnit5SpockSkipInstrumentation.java:72
    result[505] = declares(method("shouldBeSkipped").parameters(1));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/JUnit5Instrumentation.java:54
    result[507] =
        declares(
            method("discover")
                .and(
                    anyMethod()
                        .parameter(0, "org.junit.platform.engine.EngineDiscoveryRequest")
                        .parameter(1, "org.junit.platform.engine.UniqueId")));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/JUnit5Instrumentation.java:60
    result[508] =
        declares(method("execute").parameter(0, "org.junit.platform.engine.ExecutionRequest"));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/JUnit5SkipInstrumentation.java:59
    result[509] = declares(method("shouldBeSkipped").parameters(1));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/execution/JUnit5ExecutionInstrumentation.java:72
    result[510] =
        declares(
            constructor()
                .parameter(
                    3,
                    "org.junit.platform.engine.support.hierarchical.ThrowableCollector.Factory"));
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/execution/JUnit5ExecutionInstrumentation.java:80
    result[511] = declares(method("execute").noParameters());
    // junit/junit-5/junit-5.3/src/main/java/datadog/trace/instrumentation/junit5/execution/JUnit5ExecutionStoreInstrumentation.java:59
    result[512] =
        declares(
            method("discover")
                .and(
                    anyMethod()
                        .parameter(0, "org.junit.platform.engine.EngineDiscoveryRequest")
                        .parameter(1, "org.junit.platform.engine.UniqueId")));
    // junit/junit-5/junit-5.8/src/main/java/datadog/trace/instrumentation/junit5/order/JUnit5TestOrderInstrumentation.java:63
    result[515] = declares(method("getDefaultTestClassOrderer"));
    // junit/junit-5/junit-5.8/src/main/java/datadog/trace/instrumentation/junit5/order/JUnit5TestOrderInstrumentation.java:66
    result[516] = declares(method("getDefaultTestMethodOrderer"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/ConsumerCoordinatorInstrumentation.java:75
    result[517] = declares(method().and(method("sendOffsetCommitRequest")).parameters(1));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/ConsumerCoordinatorInstrumentation.java:78
    result[518] = declares(method().and(method("onJoinComplete")).parameters(4));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInfoInstrumentation.java:100
    result[519] =
        declares(
            constructor()
                .parameter(0, "org.apache.kafka.clients.consumer.ConsumerConfig")
                .parameter(1, "org.apache.kafka.common.serialization.Deserializer")
                .parameter(2, "org.apache.kafka.common.serialization.Deserializer"));
  }

  private static void fill520(ClassMatcher[] result) {
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInfoInstrumentation.java:110
    result[520] =
        declares(
            constructor()
                .parameter(0, "java.util.Map")
                .parameter(1, "org.apache.kafka.common.serialization.Deserializer")
                .parameter(2, "org.apache.kafka.common.serialization.Deserializer"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInfoInstrumentation.java:117
    result[521] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("poll"))
                .parameters(1)
                .returning(type("org.apache.kafka.clients.consumer.ConsumerRecords")));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInstrumentation.java:86
    result[522] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("records"))
                .parameter(0, "java.lang.String")
                .returning("java.lang.Iterable"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInstrumentation.java:93
    result[523] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("records"))
                .parameter(0, "org.apache.kafka.common.TopicPartition")
                .returning("java.util.List"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaConsumerInstrumentation.java:100
    result[524] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("iterator"))
                .parameters(0)
                .returning("java.util.Iterator"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:75
    result[525] = declares(method("configure").parameters("java.util.Map", "boolean"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:78
    result[526] = declares(method("deserialize").parameters(2).parameter(1, "byte[]"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:81
    result[527] = declares(method("deserialize").parameters(3).parameter(2, "byte[]"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaDeserializerInstrumentation.java:84
    result[528] = declares(method("deserialize").parameters(3).parameter(2, "java.nio.ByteBuffer"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaProducerInstrumentation.java:111
    result[529] =
        declares(
            constructor()
                .parameter(0, "org.apache.kafka.clients.producer.ProducerConfig")
                .parameter(1, "org.apache.kafka.common.serialization.Serializer")
                .parameter(2, "org.apache.kafka.common.serialization.Serializer"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/KafkaProducerInstrumentation.java:127
    result[530] =
        declares(method().access(PRIVATE).parameter(0, "int").and(method("ensureValidRecordSize")));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/MetadataInstrumentation.java:72
    result[531] =
        declares(method().and(method("update")).parameter(0, "org.apache.kafka.common.Cluster"));
    // kafka/kafka-clients-0.11/src/main/java/datadog/trace/instrumentation/kafka_clients/MetadataInstrumentation.java:77
    result[532] =
        declares(
            method()
                .and(method("update"))
                .parameter(1, "org.apache.kafka.common.requests.MetadataResponse"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/ConsumerCoordinatorInstrumentation.java:57
    result[533] = declares(method().and(method("sendOffsetCommitRequest")).parameters(1));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/ConsumerCoordinatorInstrumentation.java:60
    result[534] = declares(method().and(method("onJoinComplete")).parameters(4));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInfoInstrumentation.java:72
    result[535] = declares(field("offsetCommitCallbackInvoker"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInfoInstrumentation.java:90
    result[536] =
        declares(
            constructor()
                .parameter(0, "org.apache.kafka.clients.consumer.ConsumerConfig")
                .parameter(1, "org.apache.kafka.common.serialization.Deserializer")
                .parameter(2, "org.apache.kafka.common.serialization.Deserializer"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInfoInstrumentation.java:96
    result[537] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("poll"))
                .parameters(1)
                .returning(type("org.apache.kafka.clients.consumer.ConsumerRecords")));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInstrumentation.java:74
    result[538] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("records"))
                .parameter(0, "java.lang.String")
                .returning("java.lang.Iterable"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInstrumentation.java:81
    result[539] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("records"))
                .parameter(0, "org.apache.kafka.common.TopicPartition")
                .returning("java.util.List"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaConsumerInstrumentation.java:88
    result[540] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("iterator"))
                .parameters(0)
                .returning("java.util.Iterator"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaProducerInstrumentation.java:63
    result[541] =
        declares(
            constructor()
                .parameter(0, "org.apache.kafka.clients.producer.ProducerConfig")
                .parameter(1, "org.apache.kafka.common.serialization.Serializer")
                .parameter(2, "org.apache.kafka.common.serialization.Serializer"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/KafkaProducerInstrumentation.java:79
    result[542] =
        declares(method().access(PRIVATE).parameter(0, "int").and(method("ensureValidRecordSize")));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/LegacyKafkaConsumerInfoInstrumentation.java:72
    result[543] = declares(field("coordinator"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/LegacyKafkaConsumerInfoInstrumentation.java:90
    result[544] =
        declares(
            constructor()
                .parameter(0, "org.apache.kafka.clients.consumer.ConsumerConfig")
                .parameter(1, "org.apache.kafka.common.serialization.Deserializer")
                .parameter(2, "org.apache.kafka.common.serialization.Deserializer"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/LegacyKafkaConsumerInfoInstrumentation.java:96
    result[545] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("poll"))
                .parameters(1)
                .returning(type("org.apache.kafka.clients.consumer.ConsumerRecords")));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/MessageListenerInstrumentation.java:45
    result[546] = declares(method().and(method("onMessage")));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/MetadataInstrumentation.java:58
    result[547] =
        declares(method().and(method("update")).parameter(0, "org.apache.kafka.common.Cluster"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/MetadataInstrumentation.java:63
    result[548] =
        declares(
            method()
                .and(method("update"))
                .parameter(1, "org.apache.kafka.common.requests.MetadataResponse"));
    // kafka/kafka-clients-3.8/src/main/java/datadog/trace/instrumentation/kafka_clients38/OffsetCommitCallbackInvokerInstrumentation.java:31
    result[549] = declares(method().and(method("enqueueUserCallbackInvocation")));
    // kafka/kafka-connect-0.11/src/main/java/datadog/trace/instrumentation/kafka_connect/ConnectWorkerInstrumentation.java:40
    result[550] =
        declares(
            constructor()
                .parameter(0, "org.apache.kafka.connect.util.ConnectorTaskId")
                .parameter(1, "org.apache.kafka.connect.runtime.TaskStatus$Listener"));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:100
    result[551] = declares(constructor().parameter(4, "org.apache.kafka.streams.StreamsConfig"));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:106
    result[552] = declares(constructor().parameter(5, "org.apache.kafka.streams.StreamsConfig"));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:112
    result[553] = declares(constructor().parameter(6, "org.apache.kafka.streams.StreamsConfig"));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:116
    result[554] = declares(method().and(method("addRecords")).parameter(1, "java.lang.Iterable"));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamTaskInstrumentation.java:146
    result[555] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("process"))
                .and(anyMethod().parameters(0).or(anyMethod().parameters(1).parameter(0, "long"))));
    // kafka/kafka-streams-0.11/src/main/java/datadog/trace/instrumentation/kafka_streams/KafkaStreamsSourceNodeRecordDeserializerInstrumentation.java:33
    result[556] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("deserialize"))
                .parameter(0, "org.apache.kafka.clients.consumer.ConsumerRecord")
                .returning(type("org.apache.kafka.clients.consumer.ConsumerRecord")));
    // kafka/kafka-streams-1.0/src/main/java/datadog/trace/instrumentation/kafka_streams10/InternalTopologyBuilderInstrumentation.java:37
    result[557] = declares(method().and(method("build")).access(PRIVATE).parameters(1));
    // karate/karate-1.0/src/main/java/datadog/trace/instrumentation/karate/KarateExecutionInstrumentation.java:54
    result[558] = declares(method("run").noParameters());
    // karate/karate-1.0/src/main/java/datadog/trace/instrumentation/karate/KarateExecutionInstrumentation.java:59
    result[559] =
        declares(
            method("addStepResult")
                .parameters(1)
                .parameter(0, "com.intuit.karate.core.StepResult"));
  }

  private static void fill560(ClassMatcher[] result) {
    // karate/karate-2.0/src/main/java/datadog/trace/instrumentation/karate2/KarateExecutionInstrumentation.java:64
    result[561] =
        declares(
            method("call")
                .noParameters()
                .returning(type("io.karatelabs.core.ScenarioResult"))
                .and(not(anyMethod().access((AccessMatcher) (acc -> (acc & 64) != 0)))));
    // karate/karate-2.0/src/main/java/datadog/trace/instrumentation/karate2/KarateExecutionInstrumentation.java:72
    result[562] =
        declares(
            method("addStepResult").parameters(1).parameter(0, "io.karatelabs.core.StepResult"));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineContextInstrumentation.java:21
    result[564] =
        declares(
            method("newCoroutineContext")
                .parameter(0, "kotlinx.coroutines.CoroutineScope")
                .parameter(1, "kotlin.coroutines.CoroutineContext"));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineContextInstrumentation.java:27
    result[565] =
        declares(
            method("newCoroutineContext")
                .parameter(0, "kotlin.coroutines.CoroutineContext")
                .parameter(1, "kotlin.coroutines.CoroutineContext"));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineFlowContextCheckInstrumentation.java:20
    result[566] =
        declares(method("checkContext").parameter(0, "kotlin.coroutines.CoroutineContext"));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineFlowInstrumentation.java:22
    result[567] =
        declares(
            method("collectWithContextUndispatched")
                .parameters(3)
                .parameter(1, "kotlin.coroutines.CoroutineContext"));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/CoroutineInstrumentation.java:26
    result[569] = declares(method("onCompletionInternal"));
    // kotlin-coroutines-1.3/src/main/java/datadog/trace/instrumentation/kotlin/coroutines/LazyCoroutineInstrumentation.java:30
    result[570] = declares(method().and(method("onStart")).noParameters());
    // lettuce/lettuce-4.0/src/main/java/datadog/trace/instrumentation/lettuce4/LettuceAsyncCommandsInstrumentation.java:34
    result[571] =
        declares(
            method()
                .and(method("dispatch"))
                .parameter(0, "com.lambdaworks.redis.protocol.RedisCommand"));
    // lettuce/lettuce-4.0/src/main/java/datadog/trace/instrumentation/lettuce4/LettuceClientInstrumentation.java:33
    result[572] = declares(method().and(method("connectStandalone")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/AsyncCommandInstrumentation.java:49
    result[573] =
        declares(
            constructor()
                .and(
                    anyMethod()
                        .parameters(1)
                        .parameter(0, "io.lettuce.core.protocol.RedisCommand")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/AsyncCommandInstrumentation.java:55
    result[574] =
        declares(
            method()
                .and(
                    method(
                        namedOneOf("complete", "completeExceptionally", "onComplete", "encode"))));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/AsyncCommandInstrumentation.java:58
    result[575] = declares(method().and(method("cancel")).parameters("boolean"));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/CommandHandlerInstrumentation.java:45
    result[576] =
        declares(
            method()
                .and(method("decode"))
                .parameter(0, "io.netty.channel.ChannelHandlerContext")
                .parameter(1, "io.netty.buffer.ByteBuf")
                .parameter(2, "io.lettuce.core.protocol.RedisCommand"));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceAsyncCommandsInstrumentation.java:34
    result[577] =
        declares(
            method().and(method("dispatch")).parameter(0, "io.lettuce.core.protocol.RedisCommand"));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceClientInstrumentation.java:38
    result[578] =
        declares(
            method()
                .access(PRIVATE)
                .returning(type("io.lettuce.core.ConnectionFuture"))
                .and(method(nameStartsWith("connect")))
                .and(method(nameEndsWith("Async")))
                .parameter(1, "io.lettuce.core.RedisURI"));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceReactiveClientInstrumentation.java:88
    result[579] = declares(method().and(method("subscribe")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/LettuceReactiveClientInstrumentation.java:95
    result[580] = declares(method().and(method("onNext")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/MasterReplicaConnectionProviderInstrumentation.java:59
    result[581] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("getConnection"))
                .parameters(1)
                .returning(type("io.lettuce.core.api.StatefulRedisConnection")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/MasterReplicaConnectionProviderInstrumentation.java:66
    result[582] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("getConnectionAsync"))
                .parameters(1)
                .returning(type("java.util.concurrent.CompletableFuture")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/PooledClusterConnectionProviderInstrumentation.java:51
    result[583] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("getConnectionAsync"))
                .parameters(2)
                .parameter(1, "int")
                .returning(type("java.util.concurrent.CompletableFuture")));
    // lettuce/lettuce-5.0/src/main/java/datadog/trace/instrumentation/lettuce5/RedisClusterClientInstrumentation.java:41
    result[584] =
        declares(
            method()
                .and(method("connectToNodeAsync"))
                .parameters(4)
                .parameter(1, "java.lang.String")
                .returning(type("io.lettuce.core.ConnectionFuture")));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/GetPartsInstrumentation.java:54
    result[585] = declares(method().and(method("getParts")).access(PUBLIC).parameters(0));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ParseParametersInstrumentation.java:70
    result[586] =
        declares(
            method()
                .and(method("parseParameters"))
                .and(anyMethod().access(PUBLIC).or(anyMethod().access(PROTECTED)))
                .parameters(0)
                .returning("void"));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ParsePostDataInstrumentation.java:53
    result[587] =
        declares(
            method()
                .and(method("parsePostData"))
                .and(anyMethod().access(PUBLIC).or(anyMethod().access(PROTECTED)))
                .parameters(0)
                .returning("java.util.Hashtable"));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/RequestFinishInstrumentation.java:35
    result[588] = declares(method("finish").noParameters());
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ResponseFinishInstrumentation.java:44
    result[589] = declares(method("finish").noParameters());
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/ResponseFinishInstrumentation.java:47
    result[590] = declares(method("closeResponseOutput").parameters(1).parameter(0, "boolean"));
    // liberty/liberty-20.0/src/main/java/datadog/trace/instrumentation/liberty20/WebAppHandleExceptionInstrumentation.java:35
    result[592] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("handleException"))
                .parameters(4)
                .parameter(0, "java.lang.Throwable")
                .returning("void"));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ParseParametersInstrumentation.java:70
    result[593] =
        declares(
            method()
                .and(method("parseParameters"))
                .and(anyMethod().access(PUBLIC).or(anyMethod().access(PROTECTED)))
                .parameters(0)
                .returning("void"));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ParsePostDataInstrumentation.java:53
    result[594] =
        declares(
            method()
                .and(method("parsePostData"))
                .and(anyMethod().access(PUBLIC).or(anyMethod().access(PROTECTED)))
                .parameters(0)
                .returning("java.util.Hashtable"));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/RequestFinishInstrumentation.java:35
    result[595] = declares(method("finish").noParameters());
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ResponseFinishInstrumentation.java:44
    result[596] = declares(method("finish").noParameters());
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/ResponseFinishInstrumentation.java:47
    result[597] = declares(method("closeResponseOutput").parameters(1).parameter(0, "boolean"));
    // liberty/liberty-23.0/src/main/java/datadog/trace/instrumentation/liberty23/WebAppHandleExceptionInstrumentation.java:35
    result[598] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("handleException"))
                .parameters(4)
                .parameter(0, "java.lang.Throwable")
                .returning("void"));
    // log4j/log4j-1.2.4/src/main/java/datadog/trace/instrumentation/log4j1/CategoryInstrumentation.java:46
    result[599] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("callAppenders"))
                .parameters(1)
                .parameter(0, "org.apache.log4j.spi.LoggingEvent"));
  }

  private static void fill600(ClassMatcher[] result) {
    // log4j/log4j-1.2.4/src/main/java/datadog/trace/instrumentation/log4j1/LoggingEventInstrumentation.java:44
    result[600] = declares(method().and(method("getMDC")).parameter(0, "java.lang.String"));
    // log4j/log4j-1.2.4/src/main/java/datadog/trace/instrumentation/log4j1/LoggingEventInstrumentation.java:48
    result[601] = declares(method().and(method("getMDCCopy")).parameters(0));
    // log4j/log4j-2.7/src/main/java/datadog/trace/instrumentation/log4j27/ContextDataInjectorFactoryInstrumentation.java:35
    result[603] =
        declares(
            method()
                .access(PUBLIC)
                .access(STATIC)
                .and(method("createInjector"))
                .returning(type("org.apache.logging.log4j.core.ContextDataInjector")));
    // logback-1.0/src/main/java/datadog/trace/instrumentation/logback/LogbackLoggerInstrumentation.java:54
    result[604] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("callAppenders"))
                .parameters(1)
                .parameter(0, "ch.qos.logback.classic.spi.ILoggingEvent"));
    // logback-1.0/src/main/java/datadog/trace/instrumentation/logback/LogbackLoggerInstrumentation.java:64
    result[605] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("callAppenders"))
                .parameters(1)
                .parameter(0, "ch.qos.logback.classic.spi.ILoggingEvent"));
    // logback-1.0/src/main/java/datadog/trace/instrumentation/logback/LoggingEventInstrumentation.java:54
    result[606] =
        declares(method().and(method("getMDCPropertyMap").or(method("getMdc"))).parameters(0));
    // mail/jakarta-mail-2.0.1/src/main/java/datadog/trace/instrumentation/jakarta/mail/JakartaMailInstrumentation.java:34
    result[607] = declares(method("send0").parameter(0, "jakarta.mail.Message"));
    // mail/jakarta-mail-2.0.1/src/main/java/datadog/trace/instrumentation/jakarta/mail/JakartaMailPartInstrumentation.java:28
    result[608] = declares(method("setContent").parameter(0, "java.lang.Object"));
    // mail/jakarta-mail-2.0.1/src/main/java/datadog/trace/instrumentation/jakarta/mail/JakartaMailPartInstrumentation.java:31
    result[609] = declares(method("setText").parameter(0, "java.lang.String"));
    // mail/javax-mail-1.4.4/src/main/java/datadog/trace/instrumentation/javax/mail/JavaxMailInstrumentation.java:34
    result[610] = declares(method("send0").parameter(0, "javax.mail.Message"));
    // mail/javax-mail-1.4.4/src/main/java/datadog/trace/instrumentation/javax/mail/JavaxMailPartInstrumentation.java:28
    result[611] = declares(method("setContent").parameter(0, "java.lang.Object"));
    // mail/javax-mail-1.4.4/src/main/java/datadog/trace/instrumentation/javax/mail/JavaxMailPartInstrumentation.java:31
    result[612] = declares(method("setText").parameter(0, "java.lang.String"));
    // maven/maven-3.2.1/src/main/java/datadog/trace/instrumentation/maven3/MavenInstrumentation.java:42
    result[613] =
        declares(method("customizeContainer").parameter(0, "org.codehaus.plexus.PlexusContainer"));
    // maven/maven-surefire-3.0/src/main/java/datadog/trace/instrumentation/maven/surefire/junit4/JUnit4ClassOrderInstrumentation.java:55
    result[614] = declares(method("setTestsToRun"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:26
    result[615] =
        declares(
            method()
                .and(method("channelRead0"))
                .parameter(0, "io.netty.channel.ChannelHandlerContext")
                .parameter(1, "io.micronaut.http.HttpRequest"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:33
    result[616] =
        declares(
            method()
                .and(method("handleRouteMatch"))
                .parameter(0, "io.micronaut.web.router.RouteMatch")
                .parameter(1, "io.micronaut.http.server.netty.NettyHttpRequest")
                .parameter(2, "io.netty.channel.ChannelHandlerContext")
                .parameter(3, "boolean"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:42
    result[617] =
        declares(
            method()
                .and(method("writeFinalNettyResponse"))
                .parameter(0, "io.micronaut.http.MutableHttpResponse")
                .parameter(1, "io.micronaut.http.HttpRequest")
                .parameter(2, "io.netty.channel.ChannelHandlerContext"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-2.0/src/main/java/datadog/trace/instrumentation/micronaut/v2_0/MicronautInstrumentation.java:50
    result[618] =
        declares(
            method()
                .and(method("writeDefaultErrorResponse"))
                .parameter(0, "io.netty.channel.ChannelHandlerContext")
                .parameter(1, "io.micronaut.http.server.netty.NettyHttpRequest")
                .parameter(2, "java.lang.Throwable")
                .parameter(3, "boolean"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:29
    result[619] =
        declares(
            method()
                .and(method("channelRead0"))
                .parameter(0, "io.netty.channel.ChannelHandlerContext")
                .parameter(1, "io.micronaut.http.HttpRequest"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:36
    result[620] =
        declares(
            method()
                .and(method("handleRouteMatch"))
                .parameter(0, "io.micronaut.web.router.RouteMatch")
                .parameter(1, "io.micronaut.http.server.netty.NettyHttpRequest")
                .parameter(2, "io.netty.channel.ChannelHandlerContext"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:44
    result[621] =
        declares(
            method()
                .and(method("writeFinalNettyResponse"))
                .parameter(0, "io.micronaut.http.MutableHttpResponse")
                .parameter(1, "io.micronaut.http.HttpRequest")
                .parameter(2, "io.netty.channel.ChannelHandlerContext"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-3.0/src/main/java/datadog/trace/instrumentation/micronaut/v3_0/MicronautInstrumentation.java:52
    result[622] =
        declares(
            method()
                .and(method("createDefaultErrorResponse"))
                .parameter(0, "io.micronaut.http.HttpRequest")
                .parameter(1, "java.lang.Throwable"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:35
    result[623] = declares(method().and(method("handleNormal")).noParameters());
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:38
    result[624] =
        declares(
            method()
                .and(method("handleNormal"))
                .parameter(0, "io.micronaut.http.server.netty.NettyHttpRequest"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:44
    result[625] =
        declares(
            method()
                .and(method("findRouteMatch"))
                .parameter(0, "io.micronaut.http.HttpRequest")
                .returning(type("io.micronaut.web.router.UriRouteMatch")));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:51
    result[626] =
        declares(
            method()
                .and(method("createDefaultErrorResponse"))
                .parameter(0, "io.micronaut.http.HttpRequest")
                .parameter(1, "java.lang.Throwable"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:58
    result[627] =
        declares(
            method()
                .and(method("encodeHttpResponse"))
                .parameter(1, "io.micronaut.http.server.netty.NettyHttpRequest")
                .parameter(
                    2,
                    typeOneOf(
                        "io.micronaut.http.MutableHttpResponse",
                        "io.micronaut.http.HttpResponse")));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:69
    result[628] =
        declares(
            method()
                .and(method("encodeHttpResponse"))
                .parameter(0, "io.micronaut.http.server.netty.NettyHttpRequest")
                .parameter(1, "io.micronaut.http.HttpResponse"));
    // micronaut/micronaut-http-server-netty/micronaut-http-server-netty-4.0/src/main/java/datadog/trace/instrumentation/micronaut/v4_0/MicronautInstrumentation.java:76
    result[629] =
        declares(
            method()
                .and(method("encodeHttpResponse"))
                .parameter(0, "io.micronaut.http.HttpRequest")
                .parameter(1, "io.micronaut.http.HttpResponse"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/ByteBufBsonDocumentInstrumentation.java:37
    result[630] = declares(field("byteBuf"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/MongoClient31Instrumentation.java:47
    result[632] = declares(field("commandListeners"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/MongoClient31Instrumentation.java:80
    result[633] = declares(field("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.1/src/main/java/datadog/trace/instrumentation/mongo/MongoClient31Instrumentation.java:87
    result[634] = declares(field("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.4/src/main/java/datadog/trace/instrumentation/mongo/MongoClient34Instrumentation.java:59
    result[635] = declares(field("commandListeners"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.4/src/main/java/datadog/trace/instrumentation/mongo/MongoClient34Instrumentation.java:92
    result[636] = declares(field("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.4/src/main/java/datadog/trace/instrumentation/mongo/MongoClient34Instrumentation.java:99
    result[637] = declares(field("applicationName"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.6/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection36Instrumentation.java:43
    result[638] =
        declares(
            method()
                .and(method("command"))
                .parameter(0, "java.lang.String")
                .parameter(1, "org.bson.BsonDocument"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.6/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection36Instrumentation.java:50
    result[639] =
        declares(
            method()
                .and(method("commandAsync"))
                .parameter(0, "java.lang.String")
                .parameter(1, "org.bson.BsonDocument"));
  }

  private static void fill640(ClassMatcher[] result) {
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.8/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection38Instrumentation.java:43
    result[640] =
        declares(
            method()
                .and(method("command"))
                .parameter(0, "java.lang.String")
                .parameter(1, "org.bson.BsonDocument"));
    // mongo/mongo-driver/mongo-driver-3/mongo-driver-3.8/src/main/java/datadog/trace/instrumentation/mongo/DefaultServerConnection38Instrumentation.java:50
    result[641] =
        declares(
            method()
                .and(method("commandAsync"))
                .parameter(0, "java.lang.String")
                .parameter(1, "org.bson.BsonDocument"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/BaseCluster410Instrumentation.java:25
    result[642] =
        declares(
            method()
                .and(method("selectServerAsync"))
                .parameter(2, "com.mongodb.internal.async.SingleResultCallback"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/BaseClusterInstrumentation.java:25
    result[643] =
        declares(
            method()
                .and(method("selectServerAsync"))
                .parameter(1, "com.mongodb.internal.async.SingleResultCallback"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/DefaultConnectionPool410Instrumentation.java:25
    result[644] =
        declares(
            method()
                .and(method("getAsync"))
                .parameter(1, "com.mongodb.internal.async.SingleResultCallback"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/DefaultConnectionPoolInstrumentation.java:25
    result[645] =
        declares(
            method()
                .and(method("getAsync"))
                .parameter(0, "com.mongodb.internal.async.SingleResultCallback"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:27
    result[646] =
        declares(
            method()
                .and(method("readAsync"))
                .parameter(1, "com.mongodb.internal.async.SingleResultCallback"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:34
    result[647] =
        declares(
            method()
                .and(method("readAsync"))
                .parameter(2, "com.mongodb.internal.async.SingleResultCallback"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:44
    result[648] =
        declares(
            method()
                .and(method("openAsync"))
                .parameter(0, "com.mongodb.internal.async.SingleResultCallback"));
    // mongo/mongo-driver/mongo-driver-4.0/src/main/java/datadog/trace/instrumentation/mongo/InternalStreamConnectionInstrumentation.java:49
    result[649] =
        declares(
            method()
                .and(method("writeAsync"))
                .parameter(1, "com.mongodb.internal.async.SingleResultCallback"));
    // mule-4.5/src/main/java/datadog/trace/instrumentation/mule4/ComponentMessageProcessorInstrumentation.java:36
    result[650] =
        declares(
            method()
                .and(
                    method(
                        namedOneOf("onEvent", "onEventSynchronous", "prepareAndExecuteOperation")))
                .parameter(0, "org.mule.runtime.core.api.event.CoreEvent"));
    // mule-4.5/src/main/java/datadog/trace/instrumentation/mule4/EventTracerInstrumentation.java:26
    result[652] = declares(method("updateSelectedCoreEventTracer"));
    // mule-4.5/src/main/java/datadog/trace/instrumentation/mule4/ExecutionInitialSpanInfoInstrumentation.java:27
    result[653] = declares(constructor().parameter(0, "org.mule.runtime.api.component.Component"));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/ChannelFutureListenerInstrumentation.java:71
    result[654] =
        declares(
            method()
                .and(method("operationComplete"))
                .parameter(0, "org.jboss.netty.channel.ChannelFuture"));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/NettyChannelInstrumentation.java:53
    result[655] =
        declares(
            method()
                .and(method("connect"))
                .returning(type("org.jboss.netty.channel.ChannelFuture")));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/NettyChannelPipelineInstrumentation.java:79
    result[656] =
        declares(
            method()
                .and(method(nameStartsWith("add")))
                .parameter(1, "org.jboss.netty.channel.ChannelHandler"));
    // netty/netty-3.8/src/main/java/datadog/trace/instrumentation/netty38/NettyChannelPipelineInstrumentation.java:84
    result[657] =
        declares(
            method()
                .and(method(nameStartsWith("add")))
                .parameter(2, "org.jboss.netty.channel.ChannelHandler"));
    // netty/netty-4.0/src/main/java/datadog/trace/instrumentation/netty40/ChannelFutureListenerInstrumentation.java:47
    result[658] =
        declares(
            method()
                .and(method("operationComplete"))
                .parameter(0, "io.netty.channel.ChannelFuture"));
    // netty/netty-4.0/src/main/java/datadog/trace/instrumentation/netty40/NettyChannelHandlerContextInstrumentation.java:48
    result[659] = declares(method().and(method(nameStartsWith("fire"))).access(PUBLIC));
    // netty/netty-4.0/src/main/java/datadog/trace/instrumentation/netty40/NettyChannelPipelineInstrumentation.java:81
    result[660] =
        declares(method().and(method("connect")).returning(type("io.netty.channel.ChannelFuture")));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/ChannelFutureListenerInstrumentation.java:65
    result[661] =
        declares(
            method()
                .and(method("operationComplete"))
                .parameter(0, "io.netty.channel.ChannelFuture"));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/HttpPostRequestDecoderInstrumentation.java:69
    result[663] = declares(method("parseBody").parameters(0).access(PRIVATE));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/NettyChannelHandlerContextInstrumentation.java:65
    result[664] = declares(method().and(method(nameStartsWith("fire"))).access(PUBLIC));
    // netty/netty-4.1/src/main/java/datadog/trace/instrumentation/netty41/NettyChannelPipelineInstrumentation.java:117
    result[665] =
        declares(method().and(method("connect")).returning(type("io.netty.channel.ChannelFuture")));
    // netty/netty-buffer-4.0/src/main/java/datadog/trace/instrumentation/netty40/buffer/ByteBufInputStreamInstrumentation.java:40
    result[666] =
        declares(
            constructor()
                .access(PUBLIC)
                .parameters(3)
                .parameter(0, "io.netty.buffer.ByteBuf")
                .parameter(1, "int")
                .parameter(2, "boolean"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/NettyPromiseTaskInstrumentation.java:72
    result[667] = declares(method("runTask"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/NettyPromiseTaskInstrumentation.java:74
    result[668] = declares(method("runTask"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/NettyPromiseTaskInstrumentation.java:90
    result[669] = declares(method().and(method("runTask")).noParameters());
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorInstrumentation.java:64
    result[670] = declares(field("taskQueue"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorInstrumentation.java:71
    result[671] = declares(field("delayedTaskQueue"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorInstrumentation.java:78
    result[672] = declares(field("scheduledTaskQueue"));
    // netty/netty-concurrent-4.0/src/main/java/datadog/trace/instrumentation/netty40/concurrent/SingleThreadEventExecutorStartThreadInstrumentation.java:37
    result[673] = declares(method("startThread").noParameters().returning("void").access(PRIVATE));
    // netty/netty-promise-4.0/src/main/java/datadog/trace/instrumentation/netty4/promise/NettyPromiseInstrumentation.java:33
    result[674] =
        declares(
            method("addListener").parameter(0, "io.netty.util.concurrent.GenericFutureListener"));
    // netty/netty-promise-4.0/src/main/java/datadog/trace/instrumentation/netty4/promise/NettyPromiseInstrumentation.java:37
    result[675] =
        declares(
            method("addListeners")
                .parameter(0, "io.netty.util.concurrent.GenericFutureListener[]"));
    // ognl-appsec-3.3.2/src/main/java/datadog/trace/instrumentation/ognl/OgnlInstrumentation.java:31
    result[676] =
        declares(
            method().and(method("parseExpression")).access(STATIC).parameters("java.lang.String"));
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/AppSecHttpEngineInstrumentation.java:33
    result[677] = declares(method().and(method("sendRequest")).parameters(0));
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/IastHttpUrlInstrumentation.java:48
    result[678] =
        declares(
            method()
                .access(STATIC)
                .and(method("parse"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/IastHttpUrlInstrumentation.java:55
    result[679] =
        declares(
            method().access(STATIC).and(method("get")).parameters(1).parameter(0, "java.net.URL"));
  }

  private static void fill680(ClassMatcher[] result) {
    // okhttp/okhttp-2.2/src/main/java/datadog/trace/instrumentation/okhttp2/IastHttpUrlInstrumentation.java:62
    result[680] = declares(method().and(method("url")).parameters(0));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/AppSecHttpEngineInstrumentation.java:33
    result[682] = declares(method().and(method("sendRequest")).parameters(0));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/IastHttpUrlInstrumentation.java:43
    result[683] =
        declares(
            method()
                .access(STATIC)
                .and(method("parse"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/IastHttpUrlInstrumentation.java:50
    result[684] =
        declares(
            method().access(STATIC).and(method("get")).parameters(1).parameter(0, "java.net.URL"));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/IastHttpUrlInstrumentation.java:57
    result[685] =
        declares(
            method()
                .access(STATIC)
                .and(method("get"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // okhttp/okhttp-3.0/src/main/java/datadog/trace/instrumentation/okhttp3/OkHttp3Instrumentation.java:30
    result[686] = declares(constructor().parameter(0, "okhttp3.OkHttpClient$Builder"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceAsyncInstrumentation.java:37
    result[687] =
        declares(
            method()
                .and(method("create"))
                .parameter(0, "com.openai.models.chat.completions.ChatCompletionCreateParams")
                .returning(type("java.util.concurrent.CompletableFuture")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceAsyncInstrumentation.java:46
    result[688] =
        declares(
            method()
                .and(method("createStreaming"))
                .parameter(0, "com.openai.models.chat.completions.ChatCompletionCreateParams")
                .returning(type("java.util.concurrent.CompletableFuture")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceAsyncInstrumentation.java:58
    result[689] = declares(field("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceInstrumentation.java:36
    result[690] =
        declares(
            method()
                .and(method("create"))
                .parameter(0, "com.openai.models.chat.completions.ChatCompletionCreateParams")
                .returning(type("com.openai.core.http.HttpResponseFor")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceInstrumentation.java:45
    result[691] =
        declares(
            method()
                .and(method("createStreaming"))
                .parameter(0, "com.openai.models.chat.completions.ChatCompletionCreateParams")
                .returning(type("com.openai.core.http.HttpResponseFor")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ChatCompletionServiceInstrumentation.java:57
    result[692] = declares(field("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceAsyncInstrumentation.java:36
    result[693] =
        declares(
            method()
                .and(method("create"))
                .parameter(0, "com.openai.models.completions.CompletionCreateParams")
                .returning(type("java.util.concurrent.CompletableFuture")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceAsyncInstrumentation.java:43
    result[694] =
        declares(
            method()
                .and(method("createStreaming"))
                .parameter(0, "com.openai.models.completions.CompletionCreateParams")
                .returning(type("java.util.concurrent.CompletableFuture")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceAsyncInstrumentation.java:53
    result[695] = declares(field("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceInstrumentation.java:35
    result[696] =
        declares(
            method()
                .and(method("create"))
                .parameter(0, "com.openai.models.completions.CompletionCreateParams")
                .returning(type("com.openai.core.http.HttpResponseFor")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceInstrumentation.java:42
    result[697] =
        declares(
            method()
                .and(method("createStreaming"))
                .parameter(0, "com.openai.models.completions.CompletionCreateParams")
                .returning(type("com.openai.core.http.HttpResponseFor")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/CompletionServiceInstrumentation.java:52
    result[698] = declares(field("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/EmbeddingServiceInstrumentation.java:34
    result[699] =
        declares(
            method()
                .and(method("create"))
                .parameter(0, "com.openai.models.embeddings.EmbeddingCreateParams")
                .returning(type("com.openai.core.http.HttpResponseFor")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/EmbeddingServiceInstrumentation.java:44
    result[700] = declares(field("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceAsyncInstrumentation.java:37
    result[701] =
        declares(
            method()
                .and(method("create"))
                .parameter(0, "com.openai.models.responses.ResponseCreateParams")
                .returning(type("java.util.concurrent.CompletableFuture")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceAsyncInstrumentation.java:44
    result[702] =
        declares(
            method()
                .and(method("createStreaming"))
                .parameter(0, "com.openai.models.responses.ResponseCreateParams")
                .returning(type("java.util.concurrent.CompletableFuture")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceAsyncInstrumentation.java:54
    result[703] = declares(field("clientOptions"));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceInstrumentation.java:36
    result[704] =
        declares(
            method()
                .and(method("create"))
                .parameter(0, "com.openai.models.responses.ResponseCreateParams")
                .returning(type("com.openai.core.http.HttpResponseFor")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceInstrumentation.java:43
    result[705] =
        declares(
            method()
                .and(method("createStreaming"))
                .parameter(0, "com.openai.models.responses.ResponseCreateParams")
                .returning(type("com.openai.core.http.HttpResponseFor")));
    // openai-java/openai-java-3.0/src/main/java/datadog/trace/instrumentation/openai_java/ResponseServiceInstrumentation.java:53
    result[706] = declares(field("clientOptions"));
    // opensearch/opensearch-rest-1.0/src/main/java/datadog/trace/instrumentation/opensearch/OpensearchRestClientInstrumentation.java:48
    result[707] =
        declares(
            method()
                .and(method("performRequest"))
                .parameters(1)
                .parameter(0, "org.opensearch.client.Request"));
    // opensearch/opensearch-rest-1.0/src/main/java/datadog/trace/instrumentation/opensearch/OpensearchRestClientInstrumentation.java:54
    result[708] =
        declares(
            method()
                .and(method("performRequestAsync"))
                .parameters(2)
                .parameter(0, "org.opensearch.client.Request")
                .parameter(1, "org.opensearch.client.ResponseListener"));
    // opensearch/opensearch-transport-1.0/src/main/java/datadog/trace/instrumentation/opensearch/OpensearchTransportClientInstrumentation.java:55
    result[709] =
        declares(
            method()
                .and(method("execute"))
                .parameters(3)
                .parameter(0, "org.opensearch.action.ActionType")
                .parameter(1, "org.opensearch.action.ActionRequest")
                .parameter(2, "org.opensearch.action.ActionListener"));
    // opensearch/opensearch-transport-1.0/src/main/java/datadog/trace/instrumentation/opensearch/ThreadedActionListenerInstrumentation.java:48
    result[711] = declares(method(namedOneOf("onResponse", "onFailure")).parameters(1));
    // opentelemetry/opentelemetry-0.3/src/main/java/datadog/trace/instrumentation/opentelemetry/OpenTelemetryInstrumentation.java:35
    result[712] =
        declares(
            method("getTracerProvider").returning(type("io.opentelemetry.trace.TracerProvider")));
    // opentelemetry/opentelemetry-0.3/src/main/java/datadog/trace/instrumentation/opentelemetry/OpenTelemetryInstrumentation.java:38
    result[713] =
        declares(
            method("getPropagators")
                .returning(type("io.opentelemetry.context.propagation.ContextPropagators")));
    // opentelemetry/opentelemetry-1.27/src/main/java/datadog/trace/instrumentation/opentelemetry127/OpenTelemetryLogsInstrumentation.java:64
    result[714] =
        declares(
            method()
                .and(method("getLogsBridge"))
                .noParameters()
                .returning(type("io.opentelemetry.api.logs.LoggerProvider")));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/OpenTelemetryInstrumentation.java:60
    result[715] =
        declares(
            method()
                .and(method("getTracerProvider"))
                .noParameters()
                .returning(type("io.opentelemetry.api.trace.TracerProvider")));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/OpenTelemetryInstrumentation.java:67
    result[716] =
        declares(
            method()
                .and(method("getPropagators"))
                .noParameters()
                .returning(type("io.opentelemetry.context.propagation.ContextPropagators")));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/context/OpenTelemetryContextInstrumentation.java:57
    result[717] =
        declares(
            method()
                .and(method("root"))
                .noParameters()
                .returning(type("io.opentelemetry.context.Context")));
    // opentelemetry/opentelemetry-1.4/src/main/java/datadog/trace/instrumentation/opentelemetry14/context/OpenTelemetryContextStorageInstrumentation.java:58
    result[718] =
        declares(
            method()
                .and(method("current"))
                .noParameters()
                .returning(type("io.opentelemetry.context.Context")));
    // opentelemetry/opentelemetry-1.47/src/main/java/datadog/trace/instrumentation/opentelemetry147/OpenTelemetryMetricsInstrumentation.java:65
    result[719] =
        declares(
            method()
                .and(method("getMeterProvider"))
                .noParameters()
                .returning(type("io.opentelemetry.api.metrics.MeterProvider")));
  }

  private static void fill720(ClassMatcher[] result) {
    // opentelemetry/opentelemetry-annotations-1.20/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/WithSpanAnnotationInstrumentation.java:38
    result[720] =
        declares(
            anyMethod().annotatedWith("io.opentelemetry.instrumentation.annotations.WithSpan"));
    // opentelemetry/opentelemetry-annotations-1.26/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/AddingSpanAttributesInstrumentation.java:37
    result[721] =
        declares(
            anyMethod()
                .annotatedWith(
                    "io.opentelemetry.instrumentation.annotations.AddingSpanAttributes"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONArrayInstrumentation.java:39
    result[722] = declares(constructor().parameters(0).parameter(0, "org.json.JSONTokener"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONArrayInstrumentation.java:42
    result[723] =
        declares(method().access(PUBLIC).returning("java.lang.Object").and(method("opt")));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONCookieInstrumentation.java:34
    result[724] = declares(method("toJSONObject").parameters("java.lang.String"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObject20241224Instrumentation.java:50
    result[725] =
        declares(
            constructor()
                .parameters(2)
                .parameter(0, "org.json.JSONTokener")
                .parameter(1, "org.json.JSONParserConfiguration"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObject20241224Instrumentation.java:58
    result[726] =
        declares(
            constructor()
                .parameters(3)
                .parameter(0, "java.util.Map")
                .parameter(1, "int")
                .parameter(2, "org.json.JSONParserConfiguration"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObject20241224Instrumentation.java:65
    result[727] =
        declares(
            method()
                .access(PUBLIC)
                .returning("java.lang.Object")
                .and(method("opt"))
                .parameters("java.lang.String"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObjectInstrumentation.java:51
    result[728] = declares(constructor().parameters(1).parameter(0, "org.json.JSONTokener"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObjectInstrumentation.java:55
    result[729] = declares(constructor().parameters(1).parameter(0, "java.util.Map"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONObjectInstrumentation.java:58
    result[730] =
        declares(
            method()
                .access(PUBLIC)
                .returning("java.lang.Object")
                .and(method("opt"))
                .parameters("java.lang.String"));
    // org-json-20230227/src/main/java/datadog/trace/instrumentation/json/JSONTokenerInstrumentation.java:35
    result[731] = declares(constructor().parameters("java.io.Reader"));
    // osgi-4.3/src/main/java/datadog/trace/instrumentation/osgi43/BundleReferenceInstrumentation.java:50
    result[732] =
        declares(
            method()
                .and(method("getResource"))
                .and(anyMethod().parameters(1).parameter(0, "java.lang.String")));
    // osgi-4.3/src/main/java/datadog/trace/instrumentation/osgi43/BundleReferenceInstrumentation.java:55
    result[733] =
        declares(
            method()
                .and(method("getResourceAsStream"))
                .and(anyMethod().parameters(1).parameter(0, "java.lang.String")));
    // osgi-4.3/src/main/java/datadog/trace/instrumentation/osgi43/BundleReferenceInstrumentation.java:60
    result[734] =
        declares(
            method()
                .and(method("loadClass"))
                .and(
                    anyMethod()
                        .parameters(1)
                        .parameter(0, "java.lang.String")
                        .or(
                            anyMethod()
                                .parameters(2)
                                .parameter(0, "java.lang.String")
                                .parameter(1, "boolean"))));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoActorCellInstrumentation.java:43
    result[735] = declares(method().and(method("invoke")));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoForkJoinExecutorTaskInstrumentation.java:46
    result[737] = declares(constructor().parameter(0, "java.lang.Runnable"));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoForkJoinExecutorTaskInstrumentation.java:49
    result[738] = declares(method().and(method("run")));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoMailboxInstrumentation.java:31
    result[739] = declares(method().and(method("run")));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoRoutedActorCellInstrumentation.java:40
    result[740] =
        declares(
            method().and(method("sendMessage").parameter(0, "org.apache.pekko.dispatch.Envelope")));
    // pekko/pekko-concurrent-1.0/src/main/java/datadog/trace/instrumentation/pekko/concurrent/PekkoSchedulerInstrumentation.java:45
    result[741] =
        declares(
            method()
                .and(method(nameEndsWith("schedule")))
                .parameter(0, "scala.concurrent.ExecutionContext")
                .parameter(1, "java.lang.Runnable")
                .parameter(2, "scala.concurrent.duration.FiniteDuration"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttp2ServerInstrumentation.java:43
    result[742] =
        declares(
            anyMethod()
                .parameters(8)
                .and(method("bindAndHandleAsync"))
                .parameter(0, "scala.Function1")
                .parameter(7, "org.apache.pekko.stream.Materializer"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttp2ServerInstrumentation.java:49
    result[743] =
        declares(
            anyMethod()
                .parameters(7)
                .and(method("bindAndHandleAsync"))
                .parameter(0, "scala.Function1")
                .parameter(6, "org.apache.pekko.stream.Materializer"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttp2ServerInstrumentation.java:55
    result[744] =
        declares(
            anyMethod()
                .parameters(6)
                .and(method("bindAndHandleAsync"))
                .parameter(0, "scala.Function1")
                .parameter(5, "org.apache.pekko.stream.Materializer"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoHttpServerInstrumentation.java:66
    result[745] =
        declares(method("bindAndHandle").parameter(0, "org.apache.pekko.stream.scaladsl.Flow"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/PekkoPoolMasterActorInstrumentation.java:28
    result[746] =
        declares(
            method("org$apache$pekko$http$impl$engine$client$PoolMasterActor$$startPoolInterface"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/CookieHeaderInstrumentation.java:48
    result[747] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("cookies"))
                .returning(type("scala.collection.immutable.Seq"))
                .parameters(0));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/FormDataInstrumentation.java:39
    result[748] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("fields"))
                .parameters(0)
                .returning(type("org.apache.pekko.http.scaladsl.model.Uri$Query")));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/FormFieldDirectivesInstrumentation.java:56
    result[749] =
        declares(
            method()
                .access(STATIC)
                .and(method("formField").or(method("formFields")))
                .returning("java.lang.Object")
                .parameters(2)
                .parameter(
                    0, "org.apache.pekko.http.scaladsl.server.directives.FormFieldDirectives")
                .parameter(
                    1,
                    "org.apache.pekko.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/FormFieldDirectivesInstrumentation.java:74
    result[750] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("formField").or(method("formFields")))
                .and(
                    anyMethod()
                        .returning("java.lang.Object")
                        .or(
                            anyMethod()
                                .returning(
                                    type("org.apache.pekko.http.scaladsl.server.Directive"))))
                .parameters(1)
                .parameter(
                    0,
                    "org.apache.pekko.http.scaladsl.server.directives.FormFieldDirectives$FieldMagnet"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/HttpHeaderSubclassesInstrumentation.java:56
    result[751] =
        declares(method().and(method("value")).parameters(0).returning("java.lang.String"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/HttpRequestInstrumentation.java:50
    result[752] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("headers"))
                .returning(type("scala.collection.immutable.Seq"))
                .parameters(0));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/HttpRequestInstrumentation.java:58
    result[753] =
        declares(
            method()
                .access(PUBLIC)
                .and(not(anyMethod().access(STATIC)))
                .and(method("entity"))
                .parameters(0));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/MarshallingDirectivesInstrumentation.java:47
    result[754] =
        declares(
            method()
                .access(STATIC)
                .and(method("entity"))
                .returning(type("org.apache.pekko.http.scaladsl.server.Directive"))
                .parameters(2)
                .parameter(
                    0, "org.apache.pekko.http.scaladsl.server.directives.MarshallingDirectives")
                .parameter(1, "org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/MarshallingDirectivesInstrumentation.java:64
    result[755] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("entity"))
                .returning(type("org.apache.pekko.http.scaladsl.server.Directive"))
                .parameters(1)
                .parameter(1, "org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesImplInstrumentation.java:42
    result[756] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("filter"))
                .parameters(2)
                .parameter(0, "java.lang.String")
                .parameter(1, "org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller")
                .returning(type("org.apache.pekko.http.scaladsl.server.Directive")));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesImplInstrumentation.java:55
    result[757] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("repeatedFilter"))
                .parameters(2)
                .parameter(0, "java.lang.String")
                .parameter(1, "org.apache.pekko.http.scaladsl.unmarshalling.Unmarshaller")
                .returning(type("org.apache.pekko.http.scaladsl.server.Directive")));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesInstrumentation.java:55
    result[758] =
        declares(
            method()
                .access(STATIC)
                .and(method("parameter").or(method("parameters")))
                .returning("java.lang.Object")
                .parameters(2)
                .parameter(
                    0, "org.apache.pekko.http.scaladsl.server.directives.ParameterDirectives")
                .parameter(
                    1,
                    "org.apache.pekko.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/ParameterDirectivesInstrumentation.java:73
    result[759] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("parameter").or(method("parameters")))
                .and(
                    anyMethod()
                        .returning("java.lang.Object")
                        .or(
                            anyMethod()
                                .returning(
                                    type("org.apache.pekko.http.scaladsl.server.Directive"))))
                .parameters(1)
                .parameter(
                    0,
                    "org.apache.pekko.http.scaladsl.server.directives.ParameterDirectives$ParamMagnet"));
  }

  private static void fill760(ClassMatcher[] result) {
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/PathMatcherInstrumentation.java:40
    result[760] =
        declares(
            constructor()
                .parameters(3)
                .parameter(0, "org.apache.pekko.http.scaladsl.model.Uri$Path")
                .parameter(1, "java.lang.Object")
                .parameter(2, "org.apache.pekko.http.scaladsl.server.util.Tuple"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/RequestContextInstrumentation.java:39
    result[761] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("request"))
                .returning(type("org.apache.pekko.http.scaladsl.model.HttpRequest"))
                .parameters(0));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UnmarshallerInstrumentation.java:55
    result[762] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("apply"))
                .returning(type("scala.concurrent.Future"))
                .parameters(3)
                .parameter(0, "java.lang.Object")
                .parameter(1, "scala.concurrent.ExecutionContext")
                .parameter(2, "org.apache.pekko.stream.Materializer"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UriInstrumentation.java:44
    result[763] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("queryString"))
                .returning(type("scala.Option"))
                .parameters(1)
                .parameter(0, "java.nio.charset.Charset"));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UriInstrumentation.java:52
    result[764] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("rawQueryString"))
                .returning(type("scala.Option"))
                .parameters(0));
    // pekko/pekko-http-1.0/src/main/java/datadog/trace/instrumentation/pekkohttp/iast/UriInstrumentation.java:59
    result[765] =
        declares(
            method()
                .and(not(anyMethod().access(STATIC)))
                .and(method("query"))
                .returning(type("org.apache.pekko.http.scaladsl.model.Uri$Query"))
                .parameters(2)
                .parameter(0, "java.nio.charset.Charset")
                .parameter(1, "org.apache.pekko.http.scaladsl.model.Uri$ParsingMode"));
    // play/play-2.3/src/main/java/datadog/trace/instrumentation/play23/PlayInstrumentation.java:47
    result[766] =
        declares(
            method("apply")
                .parameter(0, "play.api.mvc.Request")
                .returning(type("scala.concurrent.Future")));
    // play/play-2.4/src/main/java/datadog/trace/instrumentation/play24/PlayInstrumentation.java:49
    result[767] =
        declares(
            method("apply")
                .parameter(0, "play.api.mvc.Request")
                .returning(type("scala.concurrent.Future")));
    // play/play-2.6/src/main/java/datadog/trace/instrumentation/play26/PlayInstrumentation.java:53
    result[768] =
        declares(
            method("apply")
                .parameter(0, "play.api.mvc.Request")
                .returning(type("scala.concurrent.Future")));
    // play/play-2.6/src/main/java/datadog/trace/instrumentation/play26/SaveRawRemoteConnectionInstrumentation.java:35
    result[769] =
        declares(
            method("forwardedConnection")
                .and(not(anyMethod().access(STATIC)))
                .parameters(2)
                .parameter(0, "play.api.mvc.request.RemoteConnection")
                .parameter(1, "play.api.mvc.Headers")
                .returning(type("play.api.mvc.request.RemoteConnection")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/DelegatingBodyParserInstrumentation.java:32
    result[770] =
        declares(
            method("apply")
                .parameters(1)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .returning(type("play.libs.streams.Accumulator")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/FormUrlEncodedInstrumentation.java:33
    result[771] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .parameter(1, "akka.util.ByteString")
                .returning("java.util.Map"));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/HttpErrorHandlerInstrumentation.java:41
    result[772] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("onServerError"))
                .parameters(2)
                .parameter(0, "play.api.mvc.RequestHeader")
                .parameter(1, "java.lang.Throwable")
                .returning(type("scala.concurrent.Future")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PathPatternInstrumentation.java:44
    result[773] =
        declares(
            method("apply")
                .and(not(anyMethod().access(STATIC)))
                .parameters(1)
                .parameter(0, "java.lang.String")
                .returning(type("scala.Option")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:40
    result[774] =
        declares(
            method("tolerantText")
                .and(not(anyMethod().access(STATIC)))
                .parameters(1)
                .parameter(0, "long")
                .returning(type("play.api.mvc.BodyParser")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:47
    result[775] =
        declares(
            method("tolerantJson")
                .and(not(anyMethod().access(STATIC)))
                .parameters(1)
                .parameter(0, "int")
                .returning(type("play.api.mvc.BodyParser")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:54
    result[776] =
        declares(
            method("tolerantFormUrlEncoded")
                .and(not(anyMethod().access(STATIC)))
                .parameters(1)
                .parameter(0, "int")
                .returning(type("play.api.mvc.BodyParser")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/PlayBodyParsersInstrumentation.java:61
    result[777] =
        declares(
            method("multipartFormData")
                .and(not(anyMethod().access(STATIC)))
                .parameters(2)
                .parameter(0, "scala.Function1")
                .parameter(1, "long")
                .returning(type("play.api.mvc.BodyParser")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/ResultsStatusInstrumentation.java:30
    result[778] = declares(method("apply"));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/RoutingDslInstrumentation.java:45
    result[779] =
        declares(
            constructor()
                .parameters(5)
                .parameter(3, "java.lang.Object")
                .parameter(4, "java.lang.reflect.Method"));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/SirdPathExtractorInstrumentation.java:36
    result[780] =
        declares(
            method(namedOneOf("extract", "play$api$routing$sird$PathExtractor$$extract"))
                .parameters(1)
                .parameter(0, "java.lang.String")
                .returning(type("scala.Option")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/StatusHeaderInstrumentation.java:31
    result[781] =
        declares(method("sendJson").parameter(0, "com.fasterxml.jackson.databind.JsonNode"));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/TolerantJsonInstrumentation.java:37
    result[782] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .parameter(1, "akka.util.ByteString")
                .returning(type("com.fasterxml.jackson.databind.JsonNode")));
    // play/play-appsec-2.5/src/main/java/datadog/trace/instrumentation/play25/appsec/TolerantTextInstrumentation.java:32
    result[783] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .parameter(1, "akka.util.ByteString")
                .returning("java.lang.String"));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/DelegatingBodyParserInstrumentation.java:45
    result[784] =
        declares(
            method("apply")
                .parameters(1)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .returning(type("play.libs.streams.Accumulator")));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/FormUrlEncodedInstrumentation.java:39
    result[785] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .parameter(1, "akka.util.ByteString")
                .returning("java.util.Map"));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/HttpErrorHandlerInstrumentation.java:47
    result[786] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("onServerError"))
                .parameters(2)
                .parameter(0, "play.api.mvc.RequestHeader")
                .parameter(1, "java.lang.Throwable")
                .returning(type("scala.concurrent.Future")));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/PathPatternInstrumentation.java:60
    result[787] =
        declares(
            method("apply")
                .and(not(anyMethod().access(STATIC)))
                .parameters(1)
                .parameter(0, "java.lang.String")
                .returning(type("scala.Option")));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/ResultsStatusInstrumentation.java:42
    result[788] = declares(method("apply"));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/RoutingDslInstrumentation.java:67
    result[789] =
        declares(
            constructor()
                .parameters(5)
                .parameter(3, "java.lang.Object")
                .parameter(4, "java.lang.reflect.Method"));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/SirdPathExtractorInstrumentation.java:50
    result[790] =
        declares(
            method("extract")
                .parameters(1)
                .parameter(0, "java.lang.String")
                .returning(type("scala.Option")));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/StatusHeaderInstrumentation.java:45
    result[791] =
        declares(method("sendJson").parameter(0, "com.fasterxml.jackson.databind.JsonNode"));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/TolerantJsonInstrumentation.java:48
    result[792] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .parameter(1, "akka.util.ByteString")
                .returning(type("com.fasterxml.jackson.databind.JsonNode")));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/TolerantTextInstrumentation.java:45
    result[793] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .parameter(1, "akka.util.ByteString")
                .returning("java.lang.String"));
    // play/play-appsec-2.6/src/main/java/datadog/trace/instrumentation/play26/appsec/TolerantXmlInstrumentation.java:45
    result[794] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "play.mvc.Http$RequestHeader")
                .parameter(1, "akka.util.ByteString")
                .returning(type("org.w3c.dom.Document")));
    // play/play-appsec-2.7/src/main/java/datadog/trace/instrumentation/play27/appsec/RoutingDsl27Instrumentation.java:60
    result[795] =
        declares(
            constructor()
                .parameters(5)
                .parameter(3, "java.lang.Object")
                .parameter(4, "java.lang.reflect.Method"));
    // quartz-2.0/src/main/java/datadog/trace/instrumentation/quartz/QuartzSchedulingInstrumentation.java:44
    result[796] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("execute"))
                .parameter(0, "org.quartz.JobExecutionContext"));
    // r2dbc/r2dbc-1.0/src/main/java/datadog/trace/instrumentation/r2dbc/R2dbcConnectionInstrumentation.java:50
    result[797] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("createStatement"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // r2dbc/r2dbc-1.0/src/main/java/datadog/trace/instrumentation/r2dbc/R2dbcInstrumentation.java:27
    result[798] =
        declares(
            method()
                .access(STATIC)
                .and(method("find"))
                .parameters(1)
                .parameter(0, "io.r2dbc.spi.ConnectionFactoryOptions"));
    // rabbitmq-amqp-2.7/src/main/java/datadog/trace/instrumentation/rabbitmq/amqp/RabbitChannelInstrumentation.java:106
    result[799] = declares(method().and(method("basicGet")).parameter(0, "java.lang.String"));
  }

  private static void fill800(ClassMatcher[] result) {
    // rabbitmq-amqp-2.7/src/main/java/datadog/trace/instrumentation/rabbitmq/amqp/RabbitChannelInstrumentation.java:109
    result[800] =
        declares(
            method()
                .and(method("basicConsume"))
                .parameter(0, "java.lang.String")
                .parameter(6, "com.rabbitmq.client.Consumer"));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ContextParseInstrumentation.java:35
    result[802] =
        declares(
            method("parse")
                .parameters(2)
                .parameter(0, "ratpack.http.TypedData")
                .parameter(1, "ratpack.parse.Parse"));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ContinuationInstrumentation.java:39
    result[803] = declares(method("resume").parameter(0, "ratpack.func.Block"));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/DefaultExecutionInstrumentation.java:33
    result[804] =
        declares(
            method(nameStartsWith("delimit"))
                .parameter(0, "ratpack.func.Action")
                .parameter(1, "ratpack.func.Action"));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/JsonRendererInstrumentation.java:36
    result[805] =
        declares(
            method()
                .and(method("render"))
                .parameters(2)
                .parameter(0, "ratpack.handling.Context")
                .parameter(1, "ratpack.jackson.JsonRender"));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/PathHandlerInstrumentation.java:45
    result[806] =
        declares(
            constructor()
                .parameters(2)
                .parameter(0, "ratpack.path.PathBinder")
                .parameter(1, "ratpack.handling.Handler"));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/RatpackRequestBodyInstrumentation.java:28
    result[807] = declares(method("readStream").parameters(0));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/RatpackTypedDataInstrumentation.java:34
    result[808] =
        declares(
            method("getBuffer")
                .parameters(0)
                .or(method("getBytes").parameters(0))
                .or(method("writeTo").parameters("java.io.OutputStream"))
                .or(method("getInputStream").parameters(0)));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/RatpackTypedDataInstrumentation.java:41
    result[809] =
        declares(
            method("getText")
                .and(
                    anyMethod()
                        .parameters(0)
                        .or(anyMethod().parameters("java.nio.charset.Charset"))));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ServerErrorHandlerInstrumentation.java:34
    result[810] =
        declares(
            method("error")
                .parameter(0, "ratpack.handling.Context")
                .parameter(1, "java.lang.Throwable"));
    // ratpack-1.5/src/main/java/datadog/trace/instrumentation/ratpack/ServerRegistryInstrumentation.java:26
    result[811] = declares(method().access(STATIC).and(method("buildBaseRegistry")));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/PublisherInstrumentation.java:46
    result[812] = declares(method().and(method("subscribe")));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/SubscriberInstrumentation.java:30
    result[813] = declares(method().and(method(namedOneOf("onNext", "onError", "onComplete"))));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/SubscriberInstrumentation.java:35
    result[814] = declares(method().and(method(namedOneOf("onNext", "onError"))));
    // reactive-streams-1.0/src/main/java/datadog/trace/instrumentation/reactivestreams/SubscriberInstrumentation.java:38
    result[815] = declares(method().and(method("onComplete")));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/BlockingPublisherInstrumentation.java:40
    result[816] = declares(method().and(method(nameStartsWith("block"))));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/BlockingPublisherInstrumentation.java:45
    result[817] = declares(method().and(method(nameStartsWith("block"))));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/ContextWritingSubscriberInstrumentation.java:44
    result[819] = declares(method(namedOneOf("onNext", "onComplete", "onError")));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/CorePublisherInstrumentation.java:46
    result[820] = declares(method("subscribe"));
    // reactor-core-3.1/src/main/java/datadog/trace/instrumentation/reactor/core/CorePublisherInstrumentation.java:51
    result[821] =
        declares(
            method("subscribe")
                .and(not(anyMethod().access(STATIC)))
                .parameters(1)
                .parameter(0, "reactor.core.CoreSubscriber"));
    // reactor-netty-1.0/src/main/java/datadog/trace/instrumentation/reactor/netty/HttpClientInstrumentation.java:60
    result[822] =
        declares(method().access(STATIC).and(method(namedOneOf("create", "newConnection"))));
    // rediscala-1.8/src/main/java/datadog/trace/instrumentation/rediscala/RedisClientActorInstrumentation.java:43
    result[823] = declares(method().and(method("onConnect")));
    // rediscala-1.8/src/main/java/datadog/trace/instrumentation/rediscala/RediscalaInstrumentation.java:66
    result[824] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "redis.RedisCommand")
                .returning(type("scala.concurrent.Future")));
    // redisson/redisson-2.0.0/src/main/java/datadog/trace/instrumentation/redisson/RedissonInstrumentation.java:38
    result[825] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "org.redisson.client.protocol.CommandData"));
    // redisson/redisson-2.0.0/src/main/java/datadog/trace/instrumentation/redisson/RedissonInstrumentation.java:45
    result[826] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "org.redisson.client.protocol.CommandsData"));
    // redisson/redisson-2.3.0/src/main/java/datadog/trace/instrumentation/redisson23/RedissonInstrumentation.java:38
    result[827] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "org.redisson.client.protocol.CommandData"));
    // redisson/redisson-2.3.0/src/main/java/datadog/trace/instrumentation/redisson23/RedissonInstrumentation.java:45
    result[828] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "org.redisson.client.protocol.CommandsData"));
    // redisson/redisson-3.10.3/src/main/java/datadog/trace/instrumentation/redisson30/RedissonInstrumentation.java:39
    result[829] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "org.redisson.client.protocol.CommandData"));
    // redisson/redisson-3.10.3/src/main/java/datadog/trace/instrumentation/redisson30/RedissonInstrumentation.java:46
    result[830] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "org.redisson.client.protocol.CommandsData"));
    // renaissance-0.7/src/main/java/datadog/trace/instrumentation/renaissance/RenaissanceInstrumentation.java:35
    result[831] = declares(method().and(method("executeOperation")).parameter(0, "int"));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:36
    result[832] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCheckedSupplier"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("io.github.resilience4j.core.functions.CheckedSupplier")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:43
    result[833] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCheckedFunction"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("io.github.resilience4j.core.functions.CheckedFunction")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:50
    result[834] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCheckedConsumer"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("io.github.resilience4j.core.functions.CheckedConsumer")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:57
    result[835] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCompletionStage"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("java.util.function.Supplier")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:64
    result[836] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateFuture"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("java.util.function.Supplier")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:71
    result[837] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateConsumer"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("java.util.function.Consumer")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:78
    result[838] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCheckedRunnable"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("io.github.resilience4j.core.functions.CheckedRunnable")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:85
    result[839] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCallable"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("java.util.concurrent.Callable")));
  }

  private static void fill840(ClassMatcher[] result) {
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:92
    result[840] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateRunnable"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("java.lang.Runnable")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:99
    result[841] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateSupplier"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("java.util.function.Supplier")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerInstrumentation.java:106
    result[842] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateFunction"))
                .parameter(0, "io.github.resilience4j.circuitbreaker.CircuitBreaker")
                .returning(type("java.util.function.Function")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackCallableInstrumentation.java:21
    result[843] = declares(method().and(method("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackCheckedSupplierInstrumentation.java:20
    result[844] = declares(method().and(method("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackCompletionStageInstrumentation.java:22
    result[845] = declares(method().and(method("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackSupplierInstrumentation.java:21
    result[846] = declares(method().and(method("withFallback")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:32
    result[847] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCompletionStage"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("java.util.function.Supplier")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:39
    result[848] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCheckedSupplier"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("io.github.resilience4j.core.functions.CheckedSupplier")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:46
    result[849] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCheckedRunnable"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("io.github.resilience4j.core.functions.CheckedRunnable")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:53
    result[850] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCallable"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("java.util.concurrent.Callable")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:60
    result[851] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateSupplier"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("java.util.function.Supplier")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:67
    result[852] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateFunction"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("java.util.function.Function")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:74
    result[853] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateCheckedFunction"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("io.github.resilience4j.core.functions.CheckedFunction")));
    // resilience4j/resilience4j-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryInstrumentation.java:81
    result[854] =
        declares(
            method()
                .access(STATIC)
                .and(method("decorateRunnable"))
                .parameter(0, "io.github.resilience4j.retry.Retry")
                .returning(type("java.lang.Runnable")));
    // resilience4j/resilience4j-reactor-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/CircuitBreakerOperatorInstrumentation.java:24
    result[855] =
        declares(method().and(method("apply")).parameter(0, "org.reactivestreams.Publisher"));
    // resilience4j/resilience4j-reactor-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/FallbackOperatorInstrumentation.java:26
    result[856] =
        declares(
            method()
                .and(method("decorate"))
                .and(
                    anyMethod()
                        .parameter(0, "java.util.function.UnaryOperator")
                        .returning(type("java.util.function.Function"))));
    // resilience4j/resilience4j-reactor-2.0/src/main/java/datadog/trace/instrumentation/resilience4j/RetryOperatorInstrumentation.java:24
    result[857] =
        declares(method().and(method("apply")).parameter(0, "org.reactivestreams.Publisher"));
    // resteasy/resteasy-3.0/src/main/java/datadog/trace/instrumentation/connection_error/resteasy/ResteasyClientConnectionErrorInstrumentation.java:37
    result[858] = declares(method().access(PUBLIC).and(method("invoke")));
    // resteasy/resteasy-3.0/src/main/java/datadog/trace/instrumentation/connection_error/resteasy/ResteasyClientConnectionErrorInstrumentation.java:41
    result[859] =
        declares(
            method().access(PUBLIC).and(method("submit")).returning("java.util.concurrent.Future"));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/CookieParamInjectorInstrumentation.java:26
    result[860] = declares(method("inject").access(PUBLIC).parameters(2));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/DecodedFormParametersInstrumentation.java:57
    result[861] = declares(method("getDecodedFormParameters").parameters(0));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/FormParamInjectorInstrumentation.java:26
    result[862] = declares(method("inject").access(PUBLIC).parameters(2));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/HeaderParamInjectorInstrumentation.java:26
    result[863] = declares(method("inject").access(PUBLIC).parameters(2));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/JavaxWSResponseInstrumentation.java:31
    result[864] =
        declares(
            method("header")
                .and(
                    anyMethod().access(PUBLIC).parameters("java.lang.String", "java.lang.Object")));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/JavaxWSResponseInstrumentation.java:34
    result[865] =
        declares(method("location").and(anyMethod().access(PUBLIC).parameters("java.net.URI")));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/MessageBodyReaderInvocationInstrumentation.java:44
    result[866] =
        declares(method("readFrom").parameters(1).parameter(0, typeEndsWith(".MessageBodyReader")));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/MethodExpressionInstrumentation.java:46
    result[867] =
        declares(
            method("populatePathParams")
                .parameters(3)
                .parameter(0, "org.jboss.resteasy.spi.HttpRequest")
                .parameter(1, "java.util.regex.Matcher")
                .parameter(2, "java.lang.String"));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/MultipartFormDataReaderInstrumentation.java:45
    result[868] =
        declares(
            method("readFrom")
                .parameters(6)
                .returning(
                    type("org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput")));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/PathParamInjectorInstrumentation.java:26
    result[869] = declares(method("inject").access(PUBLIC).parameters(2));
    // resteasy/resteasy-appsec-3.0/src/main/java/datadog/trace/instrumentation/resteasy/QueryParamInjectorInstrumentation.java:26
    result[870] = declares(method("inject").access(PUBLIC).parameters(2));
    // restlet-2.2/src/main/java/datadog/trace/instrumentation/restlet/RouteInstrumentation.java:32
    result[871] =
        declares(
            method()
                .and(method("beforeHandle"))
                .parameter(0, "org.restlet.Request")
                .parameter(1, "org.restlet.Response"));
    // robolectric-4.13/src/main/java/datadog/trace/instrumentation/robolectric/RobolectricInstrumentation.java:40
    result[872] = declares(method("setUpApplicationState"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/AbstractRequestContextInstrumentation.java:32
    result[873] =
        declares(
            method()
                .and(method("abortWith"))
                .parameters(1)
                .parameter(0, "jakarta.ws.rs.core.Response"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/ContainerRequestFilterInstrumentation.java:42
    result[874] =
        declares(
            method()
                .and(method("filter"))
                .parameters(1)
                .parameter(0, "jakarta.ws.rs.container.ContainerRequestContext"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAnnotationsInstrumentation.java:75
    result[875] = declares(anyMethod().annotatedWith("jakarta.ws.rs.Path"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAsyncResponseInstrumentation.java:48
    result[876] = declares(method("resume").parameter(0, "java.lang.Object").access(PUBLIC));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAsyncResponseInstrumentation.java:51
    result[877] = declares(method("resume").parameter(0, "java.lang.Throwable").access(PUBLIC));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAsyncResponseInstrumentation.java:54
    result[878] = declares(method("cancel"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/MessageBodyWriterInstrumentation.java:46
    result[879] = declares(method("writeTo").parameters(7));
  }

  private static void fill880(ClassMatcher[] result) {
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-1.1.1/src/main/java/datadog/trace/instrumentation/jaxrs1/JaxRsAnnotationsInstrumentation.java:73
    result[880] = declares(anyMethod().annotatedWith("javax.ws.rs.Path"));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/AbstractRequestContextInstrumentation.java:39
    result[881] =
        declares(
            method()
                .and(method("abortWith"))
                .parameters(1)
                .parameter(0, "javax.ws.rs.core.Response"));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/ContainerRequestFilterInstrumentation.java:42
    result[882] =
        declares(
            method()
                .and(method("filter"))
                .parameters(1)
                .parameter(0, "javax.ws.rs.container.ContainerRequestContext"));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAnnotationsInstrumentation.java:83
    result[883] = declares(anyMethod().annotatedWith("javax.ws.rs.Path"));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAsyncResponseInstrumentation.java:48
    result[884] = declares(method("resume").parameter(0, "java.lang.Object").access(PUBLIC));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAsyncResponseInstrumentation.java:51
    result[885] = declares(method("resume").parameter(0, "java.lang.Throwable").access(PUBLIC));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/JaxRsAsyncResponseInstrumentation.java:54
    result[886] = declares(method("cancel"));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxrs2/MessageBodyWriterInstrumentation.java:51
    result[887] = declares(method("writeTo").parameters(7));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/CompletableInstrumentation.java:30
    result[889] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.CompletableObserver"));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/FlowableInstrumentation.java:30
    result[891] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "org.reactivestreams.Subscriber"));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/MaybeInstrumentation.java:29
    result[893] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.MaybeObserver"));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/ObservableInstrumentation.java:29
    result[895] =
        declares(
            method().and(method("subscribe")).parameters(1).parameter(0, "io.reactivex.Observer"));
    // rxjava/rxjava-2.0/src/main/java/datadog/trace/instrumentation/rxjava2/SingleInstrumentation.java:30
    result[897] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.SingleObserver"));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/CompletableInstrumentation.java:30
    result[899] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.rxjava3.core.CompletableObserver"));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/FlowableInstrumentation.java:30
    result[901] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.rxjava3.core.FlowableSubscriber"));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/MaybeInstrumentation.java:29
    result[903] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.rxjava3.core.MaybeObserver"));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/ObservableInstrumentation.java:29
    result[905] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.rxjava3.core.Observer"));
    // rxjava/rxjava-3.0/src/main/java/datadog/trace/instrumentation/rxjava3/SingleInstrumentation.java:30
    result[907] =
        declares(
            method()
                .and(method("subscribe"))
                .parameters(1)
                .parameter(0, "io.reactivex.rxjava3.core.SingleObserver"));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinPoolInstrumentation.java:28
    result[908] =
        declares(
            method()
                .and(method(namedOneOf("doSubmit", "externalPush")))
                .parameter(0, "scala.concurrent.forkjoin.ForkJoinTask"));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:44
    result[909] = declares(method(namedOneOf("exec", "fork", "cancel")));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:50
    result[910] = declares(method().and(method(namedOneOf("doExec", "exec"))));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:52
    result[911] = declares(method().and(method("fork")));
    // scala/scala-concurrent-2.8/src/main/java/datadog/trace/instrumentation/scala/concurrent/ScalaForkJoinTaskInstrumentation.java:53
    result[912] = declares(method().and(method("cancel")));
    // scala/scala-promise/scala-promise-2.10/src/main/java/datadog/trace/instrumentation/scala210/concurrent/CallbackRunnableInstrumentation.java:31
    result[914] = declares(method().and(method("run")));
    // scala/scala-promise/scala-promise-2.10/src/main/java/datadog/trace/instrumentation/scala210/concurrent/CallbackRunnableInstrumentation.java:32
    result[915] = declares(method().and(method("executeWithValue")));
    // scala/scala-promise/scala-promise-2.10/src/main/java/datadog/trace/instrumentation/scala210/concurrent/PromiseObjectInstrumentation.java:33
    result[916] = declares(method().and(method("scala$concurrent$impl$Promise$$resolveTry")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/DefaultPromiseInstrumentation.java:33
    result[917] = declares(method().and(method("tryComplete0")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseObjectInstrumentation.java:33
    result[918] = declares(method().and(method("scala$concurrent$impl$Promise$$resolve")));
  }

  private static void fill920(ClassMatcher[] result) {
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseTransformationInstrumentation.java:33
    result[920] = declares(method().and(method("submitWithValue")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseTransformationInstrumentation.java:35
    result[921] = declares(method().and(method("run")));
    // scala/scala-promise/scala-promise-2.13/src/main/java/datadog/trace/instrumentation/scala213/concurrent/PromiseTransformationInstrumentation.java:36
    result[922] = declares(method().and(method("cancel")));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestForkInstrumentation.java:35
    result[923] = declares(method("runner").parameter(1, "java.lang.String[]"));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestInstrumentation.java:34
    result[924] =
        declares(method("apply").parameters(1).parameter(0, "org.scalatest.events.Event"));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestInstrumentation.java:39
    result[925] = declares(method("fireReadyEvents"));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestSkipInstrumentation.java:41
    result[926] =
        declares(
            constructor()
                .parameter(2, "org.scalatest.Filter")
                .parameter(5, "org.scalatest.Tracker"));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestSkipInstrumentation.java:47
    result[927] =
        declares(
            method("apply")
                .parameters(3)
                .parameter(0, "java.lang.String")
                .parameter(1, "scala.collection.immutable.Map")
                .parameter(2, "java.lang.String"));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/ScalatestSkipInstrumentation.java:54
    result[928] =
        declares(
            method("apply")
                .parameters(3)
                .parameter(0, "scala.collection.immutable.Set")
                .parameter(1, "scala.collection.immutable.Map")
                .parameter(2, "java.lang.String"));
    // scalatest-3.0.8/src/main/java/datadog/trace/instrumentation/scalatest/execution/ScalatestExecutionInstrumentation.java:54
    result[929] =
        declares(
            method("runTestImpl")
                .parameter(0, "org.scalatest.Suite")
                .parameter(1, "java.lang.String")
                .parameter(2, "org.scalatest.Args"));
    // selenium-3.13/src/main/java/datadog/trace/instrumentation/selenium/SeleniumInstrumentation.java:42
    result[931] = declares(method("get").parameters("java.lang.String"));
    // selenium-3.13/src/main/java/datadog/trace/instrumentation/selenium/SeleniumInstrumentation.java:45
    result[932] = declares(method("close").noParameters());
    // selenium-3.13/src/main/java/datadog/trace/instrumentation/selenium/SeleniumInstrumentation.java:48
    result[933] = declares(method("quit").noParameters());
    // servicetalk/servicetalk-0.42.0/src/main/java/datadog/trace/instrumentation/servicetalk0_42_0/ContextMapInstrumentation.java:29
    result[934] =
        declares(
            constructor()
                .access(PRIVATE)
                .parameters(1)
                .parameter(
                    0, "io.servicetalk.concurrent.api.CopyOnWriteContextMap$CopyContextMap"));
    // servicetalk/servicetalk-0.42.0/src/main/java/datadog/trace/instrumentation/servicetalk0_42_0/ContextPreservingInstrumentation.java:38
    result[935] =
        declares(
            method(
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
                    "run")));
    // servicetalk/servicetalk-0.42.56/src/main/java/datadog/trace/instrumentation/servicetalk0_42_56/CapturedContextProvidersInstrumentation.java:29
    result[936] = declares(method("loadProviders"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/IastJakartaServletInstrumentation.java:48
    result[937] =
        declares(
            method()
                .and(method("service"))
                .access(PUBLIC)
                .parameters(2)
                .parameter(0, "jakarta.servlet.ServletRequest")
                .parameter(1, "jakarta.servlet.ServletResponse"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/IastOptOutJakartaHttpServletRequestInstrumentation.java:63
    result[938] =
        declares(
            method("getSession")
                .returning(type("jakarta.servlet.http.HttpSession"))
                .access(PUBLIC));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:61
    result[939] = declares(method().and(method("getHeader")).parameters("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:64
    result[940] = declares(method().and(method("getHeaders")).parameters("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:67
    result[941] = declares(method().and(method("getHeaderNames")).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:70
    result[942] = declares(method().and(method("getParameter")).parameters("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:73
    result[943] =
        declares(method().and(method("getParameterValues")).parameters("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:76
    result[944] = declares(method().and(method("getParameterMap")).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:79
    result[945] = declares(method().and(method("getParameterNames")).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:82
    result[946] = declares(method().and(method("getCookies")).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:85
    result[947] = declares(method().and(method("getQueryString")).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:88
    result[948] =
        declares(method().and(method(namedOneOf("getInputStream", "getReader"))).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletRequestInstrumentation.java:91
    result[949] =
        declares(method().and(method("getRequestDispatcher")).parameters("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:52
    result[950] =
        declares(method("addCookie").parameters(1).parameter(0, "jakarta.servlet.http.Cookie"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:57
    result[951] =
        declares(
            method(namedOneOf("setHeader", "addHeader"))
                .parameters("java.lang.String", "java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:60
    result[952] =
        declares(
            method(namedOneOf("encodeRedirectURL", "encodeURL"))
                .parameter(0, "java.lang.String")
                .returning("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpServletResponseInstrumentation.java:65
    result[953] = declares(method("sendRedirect").parameter(0, "java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaHttpSessionInstrumentation.java:39
    result[954] =
        declares(
            method(namedOneOf("setAttribute", "putValue"))
                .and(
                    anyMethod().parameters("java.lang.String", "java.lang.Object").access(PUBLIC)));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:46
    result[955] = declares(method("getName").access(PUBLIC).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:49
    result[956] = declares(method("getHeader").access(PUBLIC).parameters("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:52
    result[957] = declares(method("getHeaders").access(PUBLIC).parameters("java.lang.String"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:55
    result[958] = declares(method("getHeaderNames").access(PUBLIC).parameters(0));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaMultipartInstrumentation.java:58
    result[959] = declares(method("getInputStream").access(PUBLIC).parameters(0));
  }

  private static void fill960(ClassMatcher[] result) {
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/JakartaServletInstrumentation.java:53
    result[960] =
        declares(
            method()
                .and(method("service"))
                .access(PUBLIC)
                .parameters(2)
                .parameter(0, "jakarta.servlet.ServletRequest")
                .parameter(1, "jakarta.servlet.ServletResponse"));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/RumAsyncContextInstrumentation.java:44
    result[961] = declares(method().and(method(namedOneOf("complete", "dispatch"))));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/Servlet5RequestBodyInstrumentation.java:40
    result[962] =
        declares(
            method("getInputStream")
                .noParameters()
                .returning(type("jakarta.servlet.ServletInputStream"))
                .access(PUBLIC));
    // servlet/jakarta-servlet-5.0/src/main/java/datadog/trace/instrumentation/servlet5/Servlet5RequestBodyInstrumentation.java:46
    result[963] =
        declares(
            method("getReader")
                .noParameters()
                .returning(type("java.io.BufferedReader"))
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/IastServlet2Instrumentation.java:60
    result[964] =
        declares(
            method(namedOneOf("doFilter", "service"))
                .parameter(0, "javax.servlet.ServletRequest")
                .parameter(1, "javax.servlet.ServletResponse")
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/Servlet2Instrumentation.java:75
    result[965] =
        declares(
            method(namedOneOf("doFilter", "service"))
                .parameter(0, "javax.servlet.ServletRequest")
                .parameter(1, "javax.servlet.ServletResponse")
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/Servlet2ResponseStatusInstrumentation.java:54
    result[966] = declares(method(namedOneOf("sendError", "setStatus")).parameter(0, "int"));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/Servlet2ResponseStatusInstrumentation.java:57
    result[967] = declares(method("sendRedirect"));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/ServletRequestBodyInstrumentation.java:76
    result[968] =
        declares(
            method("getInputStream")
                .noParameters()
                .returning(type("javax.servlet.ServletInputStream"))
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-2.2/src/main/java/datadog/trace/instrumentation/servlet2/ServletRequestBodyInstrumentation.java:82
    result[969] =
        declares(
            method("getReader")
                .noParameters()
                .returning(type("java.io.BufferedReader"))
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/AsyncContextInstrumentation.java:56
    result[970] = declares(method().access(PUBLIC).and(method("dispatch")));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/IastOptOutHttpServletRequest3Instrumentation.java:66
    result[971] =
        declares(
            method("getSession").returning(type("javax.servlet.http.HttpSession")).access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/IastServlet3Instrumentation.java:55
    result[972] =
        declares(
            method(namedOneOf("doFilter", "service"))
                .parameter(0, "javax.servlet.ServletRequest")
                .parameter(1, "javax.servlet.ServletResponse")
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:50
    result[973] = declares(method("getName").access(PUBLIC).parameters(0));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:53
    result[974] = declares(method("getHeader").access(PUBLIC).parameters("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:56
    result[975] = declares(method("getHeaders").access(PUBLIC).parameters("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:59
    result[976] = declares(method("getHeaderNames").access(PUBLIC).parameters(0));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/MultipartInstrumentation.java:62
    result[977] = declares(method("getInputStream").access(PUBLIC).parameters(0));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/RumAsyncContextInstrumentation.java:49
    result[978] = declares(method().and(method(namedOneOf("complete", "dispatch"))));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/Servlet31RequestBodyInstrumentation.java:56
    result[979] =
        declares(
            method("getInputStream")
                .noParameters()
                .returning(type("javax.servlet.ServletInputStream"))
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/Servlet31RequestBodyInstrumentation.java:62
    result[980] =
        declares(
            method("getReader")
                .noParameters()
                .returning(type("java.io.BufferedReader"))
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-3.0/src/main/java/datadog/trace/instrumentation/servlet3/Servlet3Instrumentation.java:69
    result[981] =
        declares(
            method(namedOneOf("doFilter", "service"))
                .parameter(0, "javax.servlet.ServletRequest")
                .parameter(1, "javax.servlet.ServletResponse")
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/dispatcher/RequestDispatcherInstrumentation.java:66
    result[982] =
        declares(
            method(namedOneOf("forward", "include", "error"))
                .parameters(2)
                .parameter(0, "javax.servlet.ServletRequest")
                .parameter(1, "javax.servlet.ServletResponse")
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/dispatcher/ServletContextInstrumentation.java:44
    result[983] =
        declares(
            anyMethod()
                .returning(type("javax.servlet.RequestDispatcher"))
                .parameter(0, "java.lang.String")
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/filter/FilterInstrumentation.java:49
    result[984] =
        declares(
            method("doFilter")
                .parameter(0, "javax.servlet.ServletRequest")
                .parameter(1, "javax.servlet.ServletResponse")
                .access(PUBLIC));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/CookieInstrumentation.java:44
    result[985] = declares(method().and(method("getName")).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/CookieInstrumentation.java:47
    result[986] = declares(method().and(method("getValue")).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletInstrumentation.java:55
    result[987] =
        declares(
            method("service")
                .or(method(nameStartsWith("do")))
                .parameter(0, "javax.servlet.http.HttpServletRequest")
                .parameter(1, "javax.servlet.http.HttpServletResponse")
                .and(anyMethod().access(PROTECTED).or(anyMethod().access(PUBLIC))));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:61
    result[988] = declares(method().and(method("getHeader")).parameters("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:64
    result[989] = declares(method().and(method("getHeaders")).parameters("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:67
    result[990] = declares(method().and(method("getHeaderNames")).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:70
    result[991] = declares(method().and(method("getParameter")).parameters("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:73
    result[992] =
        declares(method().and(method("getParameterValues")).parameters("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:76
    result[993] = declares(method().and(method("getParameterMap")).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:79
    result[994] = declares(method().and(method("getParameterNames")).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:82
    result[995] = declares(method().and(method("getCookies")).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:85
    result[996] = declares(method().and(method("getQueryString")).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:88
    result[997] =
        declares(method().and(method(namedOneOf("getInputStream", "getReader"))).parameters(0));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletRequestInstrumentation.java:91
    result[998] =
        declares(method().and(method("getRequestDispatcher")).parameters("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-common/src/main/java/datadog/trace/instrumentation/servlet/http/HttpServletResponseInstrumentation.java:44
    result[999] = declares(method(namedOneOf("sendError", "sendRedirect")));
  }

  private static void fill1000(ClassMatcher[] result) {
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:57
    result[1000] = declares(method("addCookie").parameter(0, "javax.servlet.http.Cookie"));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:60
    result[1001] =
        declares(
            method(namedOneOf("setHeader", "addHeader"))
                .parameters("java.lang.String", "java.lang.String"));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:63
    result[1002] =
        declares(
            method(namedOneOf("encodeRedirectURL", "encodeURL"))
                .parameter(0, "java.lang.String")
                .returning("java.lang.String"));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpServletResponseInstrumentation.java:68
    result[1003] = declares(method("sendRedirect").parameter(0, "java.lang.String"));
    // servlet/javax-servlet/javax-servlet-iast/src/main/java/datadog/trace/instrumentation/servlet/HttpSessionInstrumentation.java:50
    result[1004] =
        declares(
            method(namedOneOf("setAttribute", "putValue"))
                .and(
                    anyMethod().parameters("java.lang.String", "java.lang.Object").access(PUBLIC)));
    // slick-3.2/src/main/java/datadog/trace/instrumentation/slick/SlickRunnableInstrumentation.java:49
    result[1006] = declares(method("run").noParameters());
    // snakeyaml-1.33/src/main/java/datadog/trace/instrumentation/snakeyaml/SnakeYamlInstrumentation.java:52
    result[1007] =
        declares(
            method("load")
                .and(method())
                .and(
                    anyMethod()
                        .parameters("java.lang.String")
                        .or(anyMethod().parameters("java.io.InputStream"))
                        .or(anyMethod().parameters("java.io.Reader"))));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/AbstractClusterInstrumentation.java:34
    result[1008] =
        declares(
            method()
                .and(method("invoke"))
                .parameters(1)
                .parameter(0, "com.alipay.sofa.rpc.core.request.SofaRequest"));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/BoltServerProcessorInstrumentation.java:21
    result[1009] =
        declares(
            method()
                .and(method("handleRequest"))
                .parameters(3)
                .parameter(2, "com.alipay.sofa.rpc.core.request.SofaRequest"));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/H2cServerTaskInstrumentation.java:20
    result[1010] = declares(method().and(method("run")).noParameters());
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/ProviderProxyInvokerInstrumentation.java:33
    result[1011] =
        declares(
            method()
                .and(method("invoke"))
                .parameters(1)
                .parameter(0, "com.alipay.sofa.rpc.core.request.SofaRequest"));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/RestServerHandlerInstrumentation.java:20
    result[1012] = declares(method().and(method("channelRead0")).parameters(2));
    // sofarpc/sofarpc-5.0/src/main/java/datadog/trace/instrumentation/sofarpc/TripleServerInstrumentation.java:21
    result[1013] =
        declares(
            method()
                .and(method("invoke"))
                .parameters(1)
                .parameter(0, "com.alipay.sofa.rpc.core.request.SofaRequest"));
    // spark/sparkjava-2.3/src/main/java/datadog/trace/instrumentation/sparkjava/RoutesInstrumentation.java:38
    result[1014] =
        declares(
            method("find")
                .parameter(0, "spark.route.HttpMethod")
                .returning(type("spark.routematch.RouteMatch"))
                .access(PUBLIC));
    // spring/spring-beans-3.1/src/main/java/datadog/trace/instrumentation/springbeans/BeanFactoryInstrumentation.java:47
    result[1015] =
        declares(
            method()
                .and(method("registerBeanDefinition"))
                .parameter(1, "org.springframework.beans.factory.config.BeanDefinition"));
    // spring/spring-beans-3.1/src/main/java/datadog/trace/instrumentation/springbeans/BeanFactoryInstrumentation.java:53
    result[1016] =
        declares(
            method()
                .and(method("resolveBeanClass"))
                .parameter(0, "org.springframework.beans.factory.support.RootBeanDefinition"));
    // spring/spring-boot-1.3/src/main/java/datadog/trace/instrumentation/springboot/SpringApplicationInstrumentation.java:34
    result[1017] =
        declares(
            method("environmentPrepared")
                .parameter(0, "org.springframework.core.env.ConfigurableEnvironment"));
    // spring/spring-boot-1.3/src/main/java/datadog/trace/instrumentation/springboot/SpringApplicationInstrumentation.java:39
    result[1018] =
        declares(
            method("environmentPrepared")
                .parameter(1, "org.springframework.core.env.ConfigurableEnvironment"));
    // spring/spring-boot-1.3/src/main/java/datadog/trace/instrumentation/springboot/SpringServletInitializerInstrumentation.java:25
    result[1019] = declares(method("onStartup").parameters(1));
    // spring/spring-cloud-zuul-2.0/src/main/java/datadog/trace/instrumentation/springcloudzuul2/ZuulProxyRequestHelperInstrumentation.java:30
    result[1020] =
        declares(method().and(method("isIncludedHeader")).parameter(0, "java.lang.String"));
    // spring/spring-cloud-zuul-2.0/src/main/java/datadog/trace/instrumentation/springcloudzuul2/ZuulSendForwardFilterInstrumentation.java:33
    result[1021] = declares(method().and(method("run")).noParameters());
    // spring/spring-core-3.2.2/src/main/java/datadog/trace/instrumentation/springcore/StreamUtilsInstrumentation.java:32
    result[1022] =
        declares(
            method()
                .and(method("copyToString"))
                .parameters(2)
                .parameter(0, "java.io.InputStream")
                .parameter(1, "java.nio.charset.Charset"));
    // spring/spring-jms-3.1/src/main/java/datadog/trace/instrumentation/springjms/AbstractPollingMessageListenerContainerInstrumentation.java:42
    result[1024] = declares(method("receiveAndExecute").parameter(2, "javax.jms.MessageConsumer"));
    // spring/spring-messaging-4.0/src/main/java/datadog/trace/instrumentation/springmessaging/KotlinAwareHandlerInstrumentation.java:54
    result[1025] = declares(method().and(method("doInvoke")));
    // spring/spring-rabbit-1.5/src/main/java/datadog/trace/instrumentation/springamqp/AbstractMessageListenerContainerInstrumentation.java:57
    result[1026] = declares(method("executeListener").parameter(1, "java.lang.Object"));
    // spring/spring-rabbit-1.5/src/main/java/datadog/trace/instrumentation/springamqp/BlockingQueueConsumerInstrumentation.java:32
    result[1027] =
        declares(method("handle").parameter(0, "org.springframework.amqp.rabbit.support.Delivery"));
    // spring/spring-scheduling-3.1/src/main/java/datadog/trace/instrumentation/springscheduling/SpringAsyncInstrumentation.java:26
    result[1029] =
        declares(
            method()
                .and(method("invoke").parameter(0, "org.aopalliance.intercept.MethodInvocation")));
    // spring/spring-scheduling-3.1/src/main/java/datadog/trace/instrumentation/springscheduling/SpringSchedulingInstrumentation.java:32
    result[1030] = declares(constructor().parameter(0, "java.lang.Runnable"));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/AuthenticationManagerInstrumentation.java:40
    result[1031] =
        declares(
            method()
                .and(method("authenticate"))
                .parameter(0, "org.springframework.security.core.Authentication")
                .returning(type("org.springframework.security.core.Authentication"))
                .access(PUBLIC));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/SecurityContextHolderInstrumentation.java:40
    result[1032] =
        declares(
            method()
                .and(method("setContext"))
                .parameters(1)
                .parameter(0, "org.springframework.security.core.context.SecurityContext")
                .access(PUBLIC));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/SecurityContextHolderInstrumentation.java:49
    result[1033] =
        declares(method().and(method("setDeferredContext")).parameters(1).access(PUBLIC));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/UserDetailsManagerInstrumentation.java:38
    result[1034] =
        declares(
            method()
                .and(method("createUser"))
                .parameter(0, "org.springframework.security.core.userdetails.UserDetails")
                .access(PUBLIC));
    // spring/spring-security/spring-security-5.0/src/main/java/datadog/trace/instrumentation/springsecurity5/UsernameNotFoundExceptionInstrumentation.java:36
    result[1035] = declares(constructor().parameter(0, "java.lang.String").access(PUBLIC));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/client/WebClientFilterInstrumentation.java:33
    result[1037] = declares(method().access(PUBLIC).and(method("filter").or(method("filters"))));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/DispatcherHandlerInstrumentation.java:33
    result[1038] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("handle"))
                .parameter(0, "org.springframework.web.server.ServerWebExchange")
                .parameters(1));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/DispatcherHandlerInstrumentation.java:41
    result[1039] =
        declares(
            method()
                .and(method("handleResult"))
                .parameter(0, "org.springframework.web.server.ServerWebExchange"));
  }

  private static void fill1040(ClassMatcher[] result) {
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/HandlerAdapterInstrumentation.java:42
    result[1040] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("handle"))
                .parameter(0, "org.springframework.web.server.ServerWebExchange")
                .parameter(1, "java.lang.Object")
                .parameters(2));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/RouterFunctionInstrumentation.java:38
    result[1041] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("route"))
                .parameter(0, "org.springframework.web.reactive.function.server.ServerRequest")
                .parameters(1));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/AbstractServerHttpRequestInstrumentation.java:37
    result[1042] = declares(method().access(PUBLIC).and(method("getHeaders")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/DataBufferInstrumentation.java:35
    result[1043] = declares(method().and(method("asInputStream")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:28
    result[1044] = declares(method().and(method("get")).parameters("java.lang.Object"));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:31
    result[1045] = declares(method().and(method("getFirst")).parameters("java.lang.String"));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:34
    result[1046] = declares(method().and(method("toSingleValueMap")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpHeadersInstrumentation.java:37
    result[1047] = declares(method().and(method("readOnlyHttpHeaders")).parameters(1));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/HttpMessageInstrumentation.java:45
    result[1048] = declares(method().access(PUBLIC).and(method("getHeaders")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/Json2TokenizerInstrumentation.java:35
    result[1049] =
        declares(
            method()
                .and(method("apply").or(method("tokenize")))
                .parameter(0, "org.springframework.core.io.buffer.DataBuffer")
                .parameters(1)
                .returning(type("reactor.core.publisher.Flux")));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ReactorServerHttpRequestInstrumentation.java:36
    result[1050] = declares(method().access(PUBLIC).and(method("initHeaders")).parameters(1));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ReadOnlyHttpHeadersInstrumentation.java:30
    result[1051] = declares(method().and(method("get")).parameters("java.lang.Object"));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/RequestHeaderMapResolverInstrumentation.java:34
    result[1052] =
        declares(
            method()
                .and(method("resolveArgumentValue"))
                .parameter(0, "org.springframework.core.MethodParameter")
                .parameter(1, "org.springframework.web.reactive.BindingContext")
                .parameter(2, "org.springframework.web.server.ServerWebExchange")
                .parameters(3));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerHttpRequestInstrumentation.java:37
    result[1053] = declares(method().and(method("getQueryParams")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerHttpRequestInstrumentation.java:40
    result[1054] = declares(method().and(method("getCookies")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerHttpRequestInstrumentation.java:43
    result[1055] = declares(method().and(method("getBody")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/ServerServletHttpRequestInstrumentation.java:36
    result[1056] = declares(method().access(PUBLIC).and(method("getHeaders")).parameters(0));
    // spring/spring-webflux/spring-webflux-5.0/src/main/java/datadog/trace/instrumentation/springwebflux/server/iast/TemplateAndMatrixVariablesInstrumentation.java:28
    result[1057] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("handleMatch"))
                .parameter(0, "org.springframework.web.reactive.result.method.RequestMappingInfo")
                .parameter(1, "org.springframework.web.method.HandlerMethod")
                .parameter(2, "org.springframework.web.server.ServerWebExchange")
                .parameters(3));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/AppSecDispatcherServletInstrumentation.java:52
    result[1058] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("onRefresh"))
                .parameter(0, "org.springframework.context.ApplicationContext")
                .parameters(1));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/DispatcherServletInstrumentation.java:43
    result[1059] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("onRefresh"))
                .parameter(0, "org.springframework.context.ApplicationContext")
                .parameters(1));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/DispatcherServletInstrumentation.java:50
    result[1060] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("render"))
                .parameter(0, "org.springframework.web.servlet.ModelAndView"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/DispatcherServletInstrumentation.java:56
    result[1061] =
        declares(
            method()
                .access(PROTECTED)
                .and(method(nameStartsWith("processHandlerException")))
                .parameter(3, "java.lang.Exception"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HandlerAdapterInstrumentation.java:51
    result[1062] =
        declares(
            method()
                .access(PUBLIC)
                .and(method(nameStartsWith("handle")))
                .parameter(0, "javax.servlet.http.HttpServletRequest")
                .parameters(3));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HandlerMethodReturnValueHandlerCompositeInstrumentation.java:32
    result[1063] = declares(method().and(method("selectHandler")));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:64
    result[1064] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("read"))
                .parameters(2)
                .parameter(0, "java.lang.Class")
                .parameter(1, "org.springframework.http.HttpInputMessage"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:72
    result[1065] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("read"))
                .parameters(3)
                .parameter(0, "java.lang.reflect.Type")
                .parameter(1, "java.lang.Class")
                .parameter(2, "org.springframework.http.HttpInputMessage"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:82
    result[1066] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("write"))
                .parameters(3)
                .parameter(0, "java.lang.Object")
                .parameter(1, "org.springframework.http.MediaType")
                .parameter(2, "org.springframework.http.HttpOutputMessage"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/HttpMessageConverterInstrumentation.java:92
    result[1067] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("write"))
                .parameters(4)
                .parameter(0, "java.lang.Object")
                .parameter(1, "java.lang.reflect.Type")
                .parameter(2, "org.springframework.http.MediaType")
                .parameter(3, "org.springframework.http.HttpOutputMessage"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/InvocableHandlerMethodInstrumentation.java:33
    result[1068] = declares(method("invokeForRequest"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/SpringBeanProcessorInstrumentation.java:33
    result[1069] =
        declares(
            method()
                .and(method("processBean"))
                .parameter(3, "org.springframework.beans.factory.config.BeanDefinition"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/TemplateAndMatrixVariablesInstrumentation.java:69
    result[1070] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("handleMatch"))
                .parameter(0, "org.springframework.web.servlet.mvc.method.RequestMappingInfo")
                .parameter(1, "java.lang.String")
                .parameter(2, "javax.servlet.http.HttpServletRequest")
                .parameters(3));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/TemplateVariablesUrlHandlerInstrumentation.java:68
    result[1071] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("preHandle"))
                .parameters(3)
                .parameter(0, "javax.servlet.http.HttpServletRequest")
                .parameter(1, "javax.servlet.http.HttpServletResponse")
                .parameter(2, "java.lang.Object"));
    // spring/spring-webmvc/spring-webmvc-3.1/src/main/java/datadog/trace/instrumentation/springweb/WebApplicationContextInstrumentation.java:42
    result[1072] =
        declares(
            method()
                .and(method("postProcessBeanFactory"))
                .parameter(
                    0, "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"));
    // spring/spring-webmvc/spring-webmvc-5.3/src/main/java/datadog/trace/instrumentation/springweb/AppSecDispatcherServletWithPathPatternsInstrumentation.java:46
    result[1073] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("onRefresh"))
                .parameter(0, "org.springframework.context.ApplicationContext")
                .parameters(1));
    // spring/spring-webmvc/spring-webmvc-5.3/src/main/java/datadog/trace/instrumentation/springweb/ServletPathRequestFilterInstrumentation.java:49
    result[1074] =
        declares(
            method()
                .and(method("postProcessBeanFactory"))
                .parameter(
                    0, "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/DispatcherServletInstrumentation.java:37
    result[1075] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("getHandler"))
                .parameter(0, "jakarta.servlet.http.HttpServletRequest")
                .parameters(1)
                .returning(type("org.springframework.web.servlet.HandlerExecutionChain")));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/DispatcherServletInstrumentation.java:45
    result[1076] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("render"))
                .parameter(0, "org.springframework.web.servlet.ModelAndView"));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/DispatcherServletInstrumentation.java:51
    result[1077] =
        declares(
            method()
                .access(PROTECTED)
                .and(method(nameStartsWith("processHandlerException")))
                .parameter(3, "java.lang.Exception"));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/HandlerAdapterInstrumentation.java:44
    result[1078] =
        declares(
            method()
                .access(PUBLIC)
                .and(method(nameStartsWith("handle")))
                .parameter(0, "jakarta.servlet.http.HttpServletRequest")
                .parameters(3));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/InvocableHandlerMethodInstrumentation.java:23
    result[1079] = declares(method("invokeForRequest"));
  }

  private static void fill1080(ClassMatcher[] result) {
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/ResourceNameFilterMappingInstrumentation.java:50
    result[1080] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("onRefresh"))
                .parameter(0, "org.springframework.context.ApplicationContext")
                .parameters(1));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/ServletPathRequestFilterInstrumentation.java:61
    result[1081] =
        declares(
            method()
                .and(method("postProcessBeanFactory"))
                .parameter(
                    0, "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/TemplateAndMatrixVariablesInstrumentation.java:51
    result[1082] =
        declares(
            method()
                .access(PROTECTED)
                .and(method("handleMatch"))
                .parameter(0, "org.springframework.web.servlet.mvc.method.RequestMappingInfo")
                .parameter(1, "java.lang.String")
                .parameter(2, "jakarta.servlet.http.HttpServletRequest")
                .parameters(3));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/TemplateVariablesUrlHandlerInstrumentation.java:51
    result[1083] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("preHandle"))
                .parameters(3)
                .parameter(0, "jakarta.servlet.http.HttpServletRequest")
                .parameter(1, "jakarta.servlet.http.HttpServletResponse")
                .parameter(2, "java.lang.Object"));
    // spring/spring-webmvc/spring-webmvc-6.0/src/main/java/datadog/trace/instrumentation/springweb6/WebApplicationContextInstrumentation.java:57
    result[1084] =
        declares(
            method()
                .and(method("postProcessBeanFactory"))
                .parameter(
                    0, "org.springframework.beans.factory.config.ConfigurableListableBeanFactory"));
    // spring/spring-ws-2.0/src/main/java/datadog/trace/instrumentation/springws2/MethodEndpointInstrumentation.java:29
    result[1085] = declares(method().and(method("invoke")));
    // spymemcached-2.10/src/main/java/datadog/trace/instrumentation/spymemcached/MemcachedClientInstrumentation.java:59
    result[1086] = declares(method().access(PUBLIC).and(method(namedOneOf("incr", "decr"))));
    // spymemcached-2.10/src/main/java/datadog/trace/instrumentation/spymemcached/MemcachedConnectionInstrumentation.java:28
    result[1087] =
        declares(
            method()
                .and(method("addOperation"))
                .access(PROTECTED)
                .parameter(0, "net.spy.memcached.MemcachedNode"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientInstrumentation.java:54
    result[1088] =
        declares(
            method()
                .and(method("responseReceived"))
                .parameter(0, "org.apache.http.nio.NHttpClientConnection"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientInstrumentation.java:59
    result[1089] =
        declares(
            method()
                .and(method(namedOneOf("closed", "exception", "timeout")))
                .parameter(0, "org.apache.http.nio.NHttpClientConnection"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientWorkerInstrumentation.java:39
    result[1090] =
        declares(
            constructor().parameter(2, "org.apache.synapse.transport.passthru.TargetResponse"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseClientWorkerInstrumentation.java:43
    result[1091] = declares(method().and(method("run")).noParameters());
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapsePassthruInstrumentation.java:38
    result[1092] =
        declares(
            method().and(method("submit")).parameter(0, "org.apache.axis2.context.MessageContext"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerInstrumentation.java:47
    result[1093] =
        declares(
            method()
                .and(method("responseReady"))
                .parameter(0, "org.apache.http.nio.NHttpServerConnection"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerInstrumentation.java:52
    result[1094] =
        declares(
            method()
                .and(method(namedOneOf("closed", "exception", "timeout")))
                .parameter(0, "org.apache.http.nio.NHttpServerConnection"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerWorkerInstrumentation.java:40
    result[1095] =
        declares(constructor().parameter(0, "org.apache.synapse.transport.passthru.SourceRequest"));
    // synapse-3.0/src/main/java/datadog/trace/instrumentation/synapse3/SynapseServerWorkerInstrumentation.java:44
    result[1096] = declares(method().and(method("run")).noParameters());
    // testng/testng-6.4/src/main/java/datadog/trace/instrumentation/testng6/TestNGClassListenerInstrumentation.java:46
    result[1097] =
        declares(
            method("invokeBeforeClassMethods")
                .parameter(0, "org.testng.ITestClass")
                .parameter(1, "org.testng.IMethodInstance"));
    // testng/testng-6.4/src/main/java/datadog/trace/instrumentation/testng6/TestNGClassListenerInstrumentation.java:52
    result[1098] =
        declares(
            method("invokeAfterClassMethods")
                .parameter(0, "org.testng.ITestClass")
                .parameter(1, "org.testng.IMethodInstance"));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGClassListenerInstrumentation.java:41
    result[1099] =
        declares(
            method("invokeBeforeClassMethods")
                .parameter(0, "org.testng.ITestClass")
                .parameter(1, "org.testng.IMethodInstance"));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGClassListenerInstrumentation.java:47
    result[1100] =
        declares(
            method("invokeAfterClassMethods")
                .parameter(0, "org.testng.ITestClass")
                .parameter(1, "org.testng.IMethodInstance"));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGExecutionInstrumentation.java:44
    result[1101] = declares(method("shouldRetryTestMethod").parameter(1, "org.testng.ITestResult"));
    // testng/testng-7.0/src/main/java/datadog/trace/instrumentation/testng7/TestNGExecutionInstrumentation.java:48
    result[1102] = declares(method("runTestResultListener").parameter(0, "org.testng.ITestResult"));
    // testng/testng-common/src/main/java/datadog/trace/instrumentation/testng/TestNGSkipInstrumentation.java:44
    result[1103] =
        declares(
            method("invokeMethod")
                .parameters(3)
                .parameter(0, "java.lang.reflect.Method")
                .parameter(1, "java.lang.Object")
                .parameter(2, "java.lang.Object[]"));
    // thymeleaf-3.0/src/main/java/datadog/trace/instrumentation/thymeleaf/ElementTagStructureHandlerInstrumentation.java:28
    result[1104] = declares(method().and(method("setBody")).parameter(0, "java.lang.CharSequence"));
    // thymeleaf-3.0/src/main/java/datadog/trace/instrumentation/thymeleaf/StandardUtextTagProcessorInstrumentation.java:22
    result[1105] = declares(method().and(method("doProcess")));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/JobInstrumentation.java:28
    result[1106] = declares(method("callProcess"));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/TaskInstrumentation.java:35
    result[1107] = declares(method("eval"));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/TaskInstrumentation.java:36
    result[1108] = declares(method("handleError"));
    // tibco-businessworks/tibco-businessworks-5.14/src/main/java/datadog/trace/instrumentation/tibcobw5/ThreadPoolInstrumentation.java:27
    result[1109] = declares(method("schedule"));
    // tibco-businessworks/tibco-businessworks-6.5/src/main/java/datadog/trace/instrumentation/tibcobw6/BehaviorInstrumentation.java:67
    result[1110] = declares(method().and(method("handleModelEvent")));
    // tibco-businessworks/tibco-businessworks-6.5/src/main/java/datadog/trace/instrumentation/tibcobw6/CallbackHandlerInstrumentation.java:35
    result[1111] = declares(method().and(method("onMessage")));
    // tibco-businessworks/tibco-businessworks-6.5/src/main/java/datadog/trace/instrumentation/tibcobw6/ProcessInstrumentation.java:41
    result[1113] = declares(method().and(method("createInstance")));
    // tinylog-2.0/src/main/java/datadog/trace/instrumentation/tinylog2/LogEntryInstrumentation.java:45
    result[1114] = declares(method().and(method("getContext")).parameters(0));
    // tinylog-2.0/src/main/java/datadog/trace/instrumentation/tinylog2/TinylogLoggingProviderInstrumentation.java:41
    result[1115] =
        declares(
            method()
                .access(PRIVATE)
                .and(method("output"))
                .parameters(2)
                .parameter(0, "org.tinylog.core.LogEntry"));
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/ContainerBaseInstrumentation.java:28
    result[1116] = declares(method().and(method("setName")));
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/RequestInstrumentation.java:71
    result[1117] = declares(method("recycle").noParameters());
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/ResponseInstrumentation.java:48
    result[1118] = declares(method("recycle").noParameters());
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/TomcatServerInstrumentation.java:92
    result[1119] =
        declares(
            method("postParseRequest")
                .parameter(0, "org.apache.coyote.Request")
                .parameter(1, "org.apache.catalina.connector.Request")
                .parameter(2, "org.apache.coyote.Response")
                .parameter(3, "org.apache.catalina.connector.Response"));
  }

  private static void fill1120(ClassMatcher[] result) {
    // tomcat/tomcat-5.5/src/main/java/datadog/trace/instrumentation/tomcat/WsHttpUpgradeHandlerInstrumentation.java:42
    result[1121] = declares(method("init"));
    // tomcat/tomcat-9.0/src/main/java/datadog/trace/instrumentation/tomcat9/WebappClassLoaderInstrumentation.java:36
    result[1122] = declares(method().and(method("setResources")));
    // tomcat/tomcat-appsec/tomcat-appsec-5.5/src/main/java/datadog/trace/instrumentation/tomcat55/CommitActionInstrumentation.java:59
    result[1123] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("action"))
                .parameters(2)
                .parameter(0, "org.apache.coyote.ActionCode")
                .parameter(1, "java.lang.Object"));
    // tomcat/tomcat-appsec/tomcat-appsec-5.5/src/main/java/datadog/trace/instrumentation/tomcat55/ParsedBodyParametersInstrumentation.java:76
    result[1124] =
        declares(
            method("processParameters")
                .parameter(0, "byte[]")
                .parameter(1, "int")
                .parameter(2, "int"));
    // tomcat/tomcat-appsec/tomcat-appsec-5.5/src/main/java/datadog/trace/instrumentation/tomcat55/ParsedBodyParametersInstrumentation.java:84
    result[1125] = declares(method("handleQueryParameters").parameters(0));
    // tomcat/tomcat-appsec/tomcat-appsec-6.0/src/main/java/datadog/trace/instrumentation/tomcat6/ParsedBodyParametersInstrumentation.java:63
    result[1126] =
        declares(
            method("processParameters")
                .parameter(0, "byte[]")
                .parameter(1, "int")
                .parameter(2, "int"));
    // tomcat/tomcat-appsec/tomcat-appsec-6.0/src/main/java/datadog/trace/instrumentation/tomcat6/ParsedBodyParametersInstrumentation.java:71
    result[1127] = declares(method("handleQueryParameters").parameters(0));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/CommitActionInstrumentation.java:60
    result[1128] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("action"))
                .parameters(2)
                .parameter(0, "org.apache.coyote.ActionCode")
                .parameter(1, "java.lang.Object"));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/ErrorReportValueInstrumentation.java:37
    result[1129] =
        declares(
            method()
                .and(method("report"))
                .parameter(0, "org.apache.catalina.connector.Request")
                .parameter(1, "org.apache.catalina.connector.Response")
                .parameter(2, "java.lang.Throwable")
                .access(PROTECTED));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/GlassFishMultipartInstrumentation.java:52
    result[1130] = declares(method("getParts").parameters(0).access(PUBLIC));
    // tomcat/tomcat-appsec/tomcat-appsec-7.0/src/main/java/datadog/trace/instrumentation/tomcat7/ParsePartsInstrumentation.java:76
    result[1131] =
        declares(
            method("parseParts")
                .and(
                    anyMethod().parameters(0).or(anyMethod().parameters(1).parameter(0, "boolean")))
                .access(PRIVATE));
    // twilio-0.0.1/src/main/java/datadog/trace/instrumentation/twilio/TwilioAsyncInstrumentation.java:72
    result[1132] =
        declares(
            method()
                .and(
                    method(
                        namedOneOf(
                            "createAsync",
                            "deleteAsync",
                            "readAsync",
                            "fetchAsync",
                            "updateAsync")))
                .access(PUBLIC)
                .returning(type("com.google.common.util.concurrent.ListenableFuture")));
    // twilio-0.0.1/src/main/java/datadog/trace/instrumentation/twilio/TwilioSyncInstrumentation.java:61
    result[1133] =
        declares(
            method()
                .access(PUBLIC)
                .and(method(namedOneOf("create", "delete", "read", "fetch", "update"))));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/FormDataParserInstrumentation.java:58
    result[1134] =
        declares(
            method("doParse")
                .parameter(0, "org.xnio.channels.StreamSourceChannel")
                .parameters(1)
                .access(PRIVATE));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/HttpRequestParserInstrumentation.java:57
    result[1135] =
        declares(
            method().and(method("handle")).parameter(2, "io.undertow.server.HttpServerExchange"));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/HttpServerExchangeSenderInstrumentation.java:49
    result[1136] =
        declares(not(anyMethod().access(PRIVATE)).and(method("getResponseChannel")).parameters(0));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/MultiPartUploadHandlerInstrumentation.java:55
    result[1137] =
        declares(
            method("parseBlocking")
                .parameters(0)
                .returning(type("io.undertow.server.handlers.form.FormData"))
                .access(PUBLIC));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/RequestParserInstrumentation.java:30
    result[1138] =
        declares(
            method().and(method("handle")).parameter(2, "io.undertow.server.HttpServerExchange"));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/ServletInstrumentation.java:40
    result[1139] = declares(method().and(method("dispatchRequest")));
    // undertow/undertow-2.0/src/main/java/datadog/trace/instrumentation/undertow/UndertowInstrumentation.java:28
    result[1140] =
        declares(
            method()
                .and(method("dispatch"))
                .parameter(0, "java.util.concurrent.Executor")
                .parameter(1, "java.lang.Runnable"));
    // undertow/undertow-2.2/src/main/java/datadog/trace/instrumentation/undertow/JakartaServletInstrumentation.java:37
    result[1141] = declares(method().and(method("dispatchRequest")));
    // valkey-java-5.3/src/main/java/datadog/trace/instrumentation/valkey/ValkeyInstrumentation.java:39
    result[1142] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("executeCommand"))
                .parameter(0, "io.valkey.CommandObject"));
    // velocity-1.5/src/main/java/datadog/trace/instrumentation/velocity/ASTReferenceInstrumentation.java:32
    result[1143] =
        declares(
            method("render")
                .and(method())
                .and(
                    anyMethod()
                        .parameter(0, "org.apache.velocity.context.InternalContextAdapter")
                        .parameter(1, "java.io.Writer")));
    // vertx/vertx-mysql-client/vertx-mysql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client/MySQLConnectionFactoryInstrumentation.java:33
    result[1144] = declares(constructor().parameter(2, "io.vertx.mysqlclient.MySQLConnectOptions"));
    // vertx/vertx-mysql-client/vertx-mysql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client/MySQLConnectionImplInstrumentation.java:36
    result[1145] =
        declares(constructor().parameter(0, "io.vertx.mysqlclient.impl.MySQLConnectionFactory"));
    // vertx/vertx-mysql-client/vertx-mysql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client/MySQLPoolImplInstrumentation.java:33
    result[1146] = declares(constructor().parameter(2, "io.vertx.mysqlclient.MySQLConnectOptions"));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4/MySQLConnectionFactoryInstrumentation.java:34
    result[1147] =
        declares(
            constructor().parameters(2).parameter(1, "io.vertx.mysqlclient.MySQLConnectOptions"));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4/MySQLConnectionImplInstrumentation.java:36
    result[1148] =
        declares(constructor().parameter(0, "io.vertx.mysqlclient.impl.MySQLConnectionFactory"));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4/MySQLPoolImplInstrumentation.java:36
    result[1149] =
        declares(
            anyMethod()
                .access(STATIC)
                .access(PUBLIC)
                .and(method())
                .and(method("create"))
                .parameters(4)
                .parameter(2, "io.vertx.mysqlclient.MySQLConnectOptions"));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4_4_2/MySQLConnectionFactoryInstrumentation.java:34
    result[1150] =
        declares(constructor().parameters(2).parameter(1, "java.util.function.Supplier"));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4_4_2/MySQLDriverInstrumentation.java:35
    result[1151] =
        declares(
            anyMethod()
                .access(PRIVATE)
                .and(method("newPoolImpl"))
                .and(anyMethod().parameters(4).parameter(1, "java.util.function.Supplier")));
    // vertx/vertx-mysql-client/vertx-mysql-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_sql_client_4_4_2/SqlConnectionBaseInstrumentation.java:38
    result[1152] =
        declares(
            constructor().parameters(4).parameter(1, "io.vertx.sqlclient.spi.ConnectionFactory"));
    // vertx/vertx-pg-client/vertx-pg-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4/PgConnectionFactoryInstrumentation.java:34
    result[1153] =
        declares(constructor().parameters(2).parameter(1, "io.vertx.pgclient.PgConnectOptions"));
    // vertx/vertx-pg-client/vertx-pg-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4/PgConnectionImplInstrumentation.java:36
    result[1154] =
        declares(constructor().parameter(0, "io.vertx.pgclient.impl.PgConnectionFactory"));
    // vertx/vertx-pg-client/vertx-pg-client-4.0/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4/PgPoolImplInstrumentation.java:36
    result[1155] =
        declares(
            anyMethod()
                .access(STATIC)
                .access(PUBLIC)
                .and(method())
                .and(method("create"))
                .parameters(4)
                .parameter(2, "io.vertx.pgclient.PgConnectOptions"));
    // vertx/vertx-pg-client/vertx-pg-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4_4_2/PgConnectionFactoryInstrumentation.java:34
    result[1156] =
        declares(constructor().parameters(2).parameter(1, "java.util.function.Supplier"));
    // vertx/vertx-pg-client/vertx-pg-client-4.4.2/src/main/java/datadog/trace/instrumentation/vertx_pg_client_4_4_2/PgDriverInstrumentation.java:35
    result[1157] =
        declares(
            anyMethod()
                .access(PRIVATE)
                .and(method("newPoolImpl"))
                .and(anyMethod().parameters(4).parameter(1, "java.util.function.Supplier")));
    // vertx/vertx-redis-client/vertx-redis-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_redis_client/CommandImplInstrumentation.java:33
    result[1158] = declares(constructor().parameter(0, "java.lang.String"));
    // vertx/vertx-redis-client/vertx-redis-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_redis_client/RedisInstrumentation.java:52
    result[1159] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("send"))
                .parameter(0, "io.vertx.redis.client.Request")
                .parameter(1, "io.vertx.core.Handler"));
  }

  private static void fill1160(ClassMatcher[] result) {
    // vertx/vertx-redis-client/vertx-redis-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_redis_client/RedisInstrumentation.java:69
    result[1160] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("send"))
                .parameters(1)
                .parameter(0, "io.vertx.redis.client.Request")
                .returning(type("io.vertx.core.Future")));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/CursorImplInstrumentation.java:33
    result[1161] =
        declares(method().access(PUBLIC).and(method("read")).parameter(1, "io.vertx.core.Handler"));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/PreparedQueryInstrumentation.java:44
    result[1162] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("execute"))
                .parameters(2)
                .parameter(1, "io.vertx.core.Handler"));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/PreparedQueryInstrumentation.java:51
    result[1163] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("executeBatch"))
                .parameters(2)
                .parameter(1, "io.vertx.core.Handler"));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/PreparedStatementImplInstrumentation.java:36
    result[1164] = declares(method().access(PUBLIC).and(method("query")).noParameters());
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/QueryImplInstrumentation.java:36
    result[1165] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("execute"))
                .parameters(1)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/SqlClientBaseInstrumentation.java:38
    result[1166] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("query"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/SqlClientBaseInstrumentation.java:45
    result[1167] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("preparedQuery"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // vertx/vertx-sql-client-3.9/src/main/java/datadog/trace/instrumentation/vertx_sql_client_39/SqlConnectionBaseInstrumentation.java:38
    result[1168] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("prepare"))
                .parameters(2)
                .parameter(0, "java.lang.String")
                .parameter(1, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/AbstractHttpServerRequestInstrumentation.java:44
    result[1169] =
        declares(anyMethod().access(PUBLIC).and(method()).and(method("params")).noParameters());
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/AbstractHttpServerRequestInstrumentation.java:50
    result[1170] =
        declares(
            method()
                .and(method("handleData").or(method("onData")))
                .and(anyMethod().parameters(1).parameter(0, "io.vertx.core.buffer.Buffer")));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/BufferInstrumentation.java:51
    result[1171] = declares(method().access(PUBLIC).and(method("toString")));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/BufferInstrumentation.java:53
    result[1172] = declares(method().access(PUBLIC).and(method("getByteBuf")).noParameters());
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/BufferInstrumentation.java:56
    result[1173] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("appendBuffer"))
                .parameter(0, "io.vertx.core.buffer.Buffer"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:62
    result[1174] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("get"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:69
    result[1175] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("getAll"))
                .parameters(1)
                .parameter(0, "java.lang.String"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:76
    result[1176] = declares(method().access(PUBLIC).and(method("entries")).noParameters());
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/CaseInsensitiveHeadersInstrumentation.java:79
    result[1177] = declares(method().access(PUBLIC).and(method("names")).noParameters());
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:62
    result[1178] = declares(method().access(PUBLIC).and(method("get")).parameters(1));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:65
    result[1179] = declares(method().access(PUBLIC).and(method("getAll")).parameters(1));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:68
    result[1180] = declares(method().access(PUBLIC).and(method("entries")).parameters(0));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HeadersAdaptorInstrumentation.java:71
    result[1181] = declares(method().access(PUBLIC).and(method("names")).parameters(0));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/Http2ServerRequestInstrumentation.java:39
    result[1182] =
        declares(anyMethod().access(PUBLIC).and(method()).and(method("headers")).noParameters());
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/core/HttpServerRequestInstrumentation.java:40
    result[1183] =
        declares(anyMethod().access(PUBLIC).and(method()).and(method("headers")).noParameters());
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/CookieImplInstrumentation.java:55
    result[1184] = declares(method().and(method("getName")).parameters(0));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/CookieImplInstrumentation.java:57
    result[1185] = declares(method().and(method("getValue")).parameters(0));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerRequestInstrumentation.java:38
    result[1186] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("bodyHandler"))
                .parameters(1)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerResponseEndHandlerInstrumentation.java:26
    result[1187] =
        declares(
            method()
                .and(method("endHandler"))
                .access(PUBLIC)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerResponseInstrumentation.java:35
    result[1188] =
        declares(
            method("putHeader")
                .and(
                    anyMethod()
                        .parameters("java.lang.CharSequence", "java.lang.CharSequence")
                        .or(anyMethod().parameters("java.lang.String", "java.lang.String"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/HttpServerResponseInstrumentation.java:41
    result[1189] =
        declares(
            method("putHeader")
                .and(
                    anyMethod()
                        .parameters("java.lang.CharSequence", "java.lang.Iterable")
                        .or(anyMethod().parameters("java.lang.String", "java.lang.Iterable"))));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/IastRoutingContextImplInstrumentation.java:50
    result[1190] = declares(method("cookies").parameters(0));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/IastRoutingContextImplInstrumentation.java:51
    result[1191] = declares(method("getCookie").parameters(1).parameter(0, "java.lang.String"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/IastRoutingContextImplInstrumentation.java:54
    result[1192] = declares(method("reroute").parameters(2).parameter(1, "java.lang.String"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RouteHandlerInstrumentation.java:27
    result[1193] =
        declares(
            method()
                .and(method(namedOneOf("handler", "blockingHandler")))
                .access(PUBLIC)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RouteImplInstrumentation.java:52
    result[1194] =
        declares(
            method("matches")
                .parameters(3)
                .parameter(0, "io.vertx.ext.web.impl.RoutingContextImplBase")
                .parameter(1, "java.lang.String")
                .parameter(2, "boolean")
                .access(PUBLIC)
                .returning("int"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RouteImplInstrumentation.java:62
    result[1195] =
        declares(
            method("matches")
                .parameters(3)
                .parameter(
                    0,
                    type("io.vertx.ext.web.impl.RoutingContextImplBase")
                        .or(type("io.vertx.ext.web.RoutingContext")))
                .parameter(1, "java.lang.String")
                .parameter(2, "boolean")
                .returning("boolean"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RoutingContextImplInstrumentation.java:42
    result[1196] = declares(method("getBodyAsJson").or(method("getBodyAsJsonArray")).parameters(0));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RoutingContextImplInstrumentation.java:45
    result[1197] = declares(method("setSession").parameter(0, "io.vertx.ext.web.Session"));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/RoutingContextImplInstrumentation.java:48
    result[1198] = declares(method("fileUploads").parameters(0));
    // vertx/vertx-web/vertx-web-3.4/src/main/java/datadog/trace/instrumentation/vertx_3_4/server/VertxImplInstrumentation.java:30
    result[1199] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("exceptionHandler"))
                .parameters(0)
                .returning(type("io.vertx.core.Handler")));
  }

  private static void fill1200(ClassMatcher[] result) {
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:62
    result[1200] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("get"))
                .and(anyMethod().parameters(1).parameter(0, "java.lang.CharSequence")));
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:68
    result[1201] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("getAll"))
                .and(anyMethod().parameters(1).parameter(0, "java.lang.CharSequence")));
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:74
    result[1202] = declares(method().access(PUBLIC).and(method("entries")).parameters(0));
    // vertx/vertx-web/vertx-web-3.5/src/main/java/datadog/trace/instrumentation/vertx_3_5/core/VertxHttpHeadersInstrumentation.java:77
    result[1203] = declares(method().access(PUBLIC).and(method("names")).parameters(0));
    // vertx/vertx-web/vertx-web-3.9/src/main/java/datadog/trace/instrumentation/vertx_3_9/Vertx39HttpServertResponseInstrumentation.java:29
    result[1204] =
        declares(method("addCookie").parameter(0, "io.vertx.core.http.Cookie").access(PUBLIC));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:47
    result[1205] =
        declares(anyMethod().access(PUBLIC).and(method()).and(method("headers")).noParameters());
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:50
    result[1206] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method())
                .and(method("params"))
                .and(anyMethod().noParameters().or(anyMethod().parameters("boolean"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:59
    result[1207] =
        declares(
            method()
                .and(method("handleData").or(method("onData")))
                .and(anyMethod().parameters(1).parameter(0, "io.vertx.core.buffer.Buffer")));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:64
    result[1208] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method())
                .and(method("cookies"))
                .returning("java.util.Set"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/AbstractHttpServerRequestInstrumentation.java:67
    result[1209] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method())
                .and(method("getCookie"))
                .parameter(0, "java.lang.String"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/BufferInstrumentation.java:50
    result[1210] = declares(method().access(PUBLIC).and(method("toString")));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/BufferInstrumentation.java:52
    result[1211] = declares(method().access(PUBLIC).and(method("getByteBuf")).noParameters());
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/BufferInstrumentation.java:55
    result[1212] =
        declares(
            method()
                .access(PUBLIC)
                .and(method("appendBuffer"))
                .parameter(0, "io.vertx.core.buffer.Buffer"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/HttpServerResponseInstrumentation.java:32
    result[1213] =
        declares(
            method("putHeader")
                .and(
                    anyMethod()
                        .parameters("java.lang.CharSequence", "java.lang.CharSequence")
                        .or(anyMethod().parameters("java.lang.String", "java.lang.String"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/HttpServerResponseInstrumentation.java:38
    result[1214] =
        declares(
            method("putHeader")
                .and(
                    anyMethod()
                        .parameters("java.lang.CharSequence", "java.lang.Iterable")
                        .or(anyMethod().parameters("java.lang.String", "java.lang.Iterable"))));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/MultiMapInstrumentation.java:67
    result[1215] = declares(method().access(PUBLIC).and(method("entries")).noParameters());
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/core/MultiMapInstrumentation.java:70
    result[1216] = declares(method().access(PUBLIC).and(method("names")).noParameters());
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/CookieImplInstrumentation.java:53
    result[1217] = declares(method().and(method("getName")).parameters(0));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/CookieImplInstrumentation.java:55
    result[1218] = declares(method().and(method("getValue")).parameters(0));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/HttpServerRequestInstrumentation.java:36
    result[1219] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("bodyHandler"))
                .parameters(1)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/HttpServerResponseEndHandlerInstrumentation.java:37
    result[1220] =
        declares(
            method()
                .and(method("endHandler"))
                .access(PUBLIC)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/IastRoutingContextImplInstrumentation.java:40
    result[1221] = declares(method("reroute").parameters(2).parameter(1, "java.lang.String"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/LocalSessionStoreInstrumentation.java:41
    result[1222] =
        declares(
            method("init")
                .access(PUBLIC)
                .parameters(2)
                .parameter(0, "io.vertx.core.Vertx")
                .parameter(1, "io.vertx.core.json.JsonObject"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/LocalSessionStoreInstrumentation.java:48
    result[1223] = declares(method("setTimer").access(PRIVATE).noParameters().returning("void"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RequestBodyInstrumentation.java:32
    result[1224] =
        declares(
            method("asJsonObject").or(method("asJsonArray")).parameters(1).parameter(0, "int"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RouteHandlerInstrumentation.java:33
    result[1225] =
        declares(
            method()
                .and(method(namedOneOf("handler", "blockingHandler")))
                .access(PUBLIC)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RouteImplInstrumentation.java:50
    result[1226] =
        declares(
            method("matches")
                .parameters(3)
                .parameter(0, "io.vertx.ext.web.impl.RoutingContextImplBase")
                .parameter(1, "java.lang.String")
                .parameter(2, "boolean")
                .access(PUBLIC)
                .returning("int"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RouteImplInstrumentation.java:60
    result[1227] =
        declares(
            method("matches")
                .parameters(3)
                .parameter(
                    0,
                    type("io.vertx.ext.web.impl.RoutingContextImplBase")
                        .or(type("io.vertx.ext.web.RoutingContext")))
                .parameter(1, "java.lang.String")
                .parameter(2, "boolean")
                .returning("boolean"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextImplInstrumentation.java:45
    result[1228] =
        declares(
            method("getBodyAsJson")
                .or(method("getBodyAsJsonArray"))
                .parameters(1)
                .parameter(0, "int"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextImplInstrumentation.java:51
    result[1229] = declares(method("setSession").parameter(0, "io.vertx.ext.web.Session"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextImplInstrumentation.java:54
    result[1230] = declares(method("fileUploads").parameters(0));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/RoutingContextInstrumentation.java:37
    result[1231] = declares(method("json").parameters(1).parameter(0, "java.lang.Object"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/VertxHandlerInstrumentation.java:39
    result[1232] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("exceptionCaught"))
                .parameters(2)
                .parameter(0, "io.netty.channel.ChannelHandlerContext")
                .parameter(1, "java.lang.Throwable"));
    // vertx/vertx-web/vertx-web-4.0/src/main/java/datadog/trace/instrumentation/vertx_4_0/server/VertxImplInstrumentation.java:36
    result[1233] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("exceptionHandler"))
                .parameters(0)
                .returning(type("io.vertx.core.Handler")));
    // vertx/vertx-web/vertx-web-5.0/src/main/java/datadog/trace/instrumentation/vertx_5_0/server/HttpServerRequestInstrumentation.java:36
    result[1234] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("bodyHandler"))
                .parameters(1)
                .parameter(0, "io.vertx.core.Handler"));
    // vertx/vertx-web/vertx-web-5.0/src/main/java/datadog/trace/instrumentation/vertx_5_0/server/RoutingContextImplInstrumentation.java:39
    result[1235] = declares(method("fileUploads").parameters(0));
    // weaver-0.9/src/main/java/datadog/trace/instrumentation/weaver/WeaverInstrumentation.java:32
    result[1236] =
        declares(constructor().parameter(5, "java.util.concurrent.ConcurrentLinkedQueue"));
    // weaver-0.9/src/main/java/datadog/trace/instrumentation/weaver/WeaverInstrumentation.java:36
    result[1237] = declares(constructor().parameter(5, "java.util.concurrent.LinkedBlockingQueue"));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:49
    result[1238] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("sendText"))
                .and(
                    anyMethod().parameters(1).or(anyMethod().parameters(2).parameter(1, "boolean")))
                .parameter(0, "java.lang.String")
                .returning("void"));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:56
    result[1239] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("sendBinary"))
                .and(
                    anyMethod().parameters(1).or(anyMethod().parameters(2).parameter(1, "boolean")))
                .parameter(0, "java.nio.ByteBuffer")
                .returning("void"));
  }

  private static void fill1240(ClassMatcher[] result) {
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:63
    result[1240] =
        declares(
            anyMethod().access(PUBLIC).and(method("sendObject")).parameters(1).returning("void"));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:66
    result[1241] = declares(anyMethod().access(PUBLIC).and(method("getSendStream")).noParameters());
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/BasicRemoteEndpointInstrumentation.java:69
    result[1242] = declares(anyMethod().access(PUBLIC).and(method("getSendWriter")).noParameters());
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/MessageHandlerInstrumentation.java:43
    result[1243] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method("onMessage"))
                .and(
                    anyMethod()
                        .parameters(1)
                        .or(anyMethod().parameters(2).parameter(1, "boolean"))));
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/SessionInstrumentation.java:64
    result[1244] =
        declares(
            anyMethod()
                .access(PUBLIC)
                .and(method(namedOneOf("getBasicRemote", "getAsyncRemote")))
                .noParameters());
    // websocket/javax-websocket-1.0/src/main/java/datadog/trace/instrumentation/websocket/jsr256/SessionInstrumentation.java:76
    result[1245] = declares(method("close").noParameters());
    // websocket/jetty-websocket/jetty-websocket-10.0/src/main/java/datadog/trace/instrumentation/websocket/jetty10/JavaxWebSocketFrameHandlerInstrumentation.java:24
    result[1246] =
        declares(
            constructor()
                .parameters(9)
                .parameter(1, "java.lang.Object")
                .parameter(2, "java.lang.invoke.MethodHandle")
                .parameter(3, "java.lang.invoke.MethodHandle"));
    // websocket/jetty-websocket/jetty-websocket-10.0/src/main/java/datadog/trace/instrumentation/websocket/jetty10/JavaxWebSocketFrameHandlerInstrumentation.java:31
    result[1247] =
        declares(
            constructor()
                .parameters(10)
                .parameter(2, "java.lang.Object")
                .parameter(3, "java.lang.invoke.MethodHandle")
                .parameter(4, "java.lang.invoke.MethodHandle"));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeFrameHandlerInstrumentation.java:28
    result[1248] =
        declares(
            method("onOpen")
                .parameters(2)
                .parameter(0, "org.eclipse.jetty.websocket.core.CoreSession"));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeFrameHandlerInstrumentation.java:33
    result[1249] = declares(method("createMessageSink").parameters(4));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeFrameHandlerInstrumentation.java:36
    result[1250] = declares(method("notifyOnClose").parameters(2));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeSessionInstrumentation.java:30
    result[1252] =
        declares(
            method(namedOneOf("sendText", "sendBinary"))
                .parameters(2)
                .parameter(1, "org.eclipse.jetty.websocket.api.Callback"));
    // websocket/jetty-websocket/jetty-websocket-12.0/src/main/java/datadog/trace/instrumentation/websocket/jetty12/Jetty12NativeSessionInstrumentation.java:35
    result[1253] =
        declares(
            method(namedOneOf("sendPartialText", "sendPartialBinary"))
                .parameters(3)
                .parameter(1, "boolean")
                .parameter(2, "org.eclipse.jetty.websocket.api.Callback"));
    // websphere-jmx-8.5/src/main/java/datadog/trace/instrumentation/websphere_jmx/WebsphereSecurityInstrumentation.java:48
    result[1254] = declares(method().and(method("isSecurityEnabled")).returning("boolean"));
    // wildfly-9.0/src/main/java/datadog/trace/instrumentation/wildfly/ResourceReferenceProcessorInstrumentation.java:39
    result[1256] = declares(method().and(method("getEnvironmentEntries")));
    // ws/jax-ws/jax-ws-annotations-2.0/src/main/java/datadog/trace/instrumentation/jaxws2/WebServiceProviderInstrumentation.java:51
    result[1257] = declares(method().and(method("invoke")).parameters(1));
    // zio/zio-2.0/src/main/java/datadog/trace/instrumentation/zio/v2_0/ZioRuntimeInstrumentation.java:41
    result[1258] = declares(method().and(method("defaultSupervisor")));
    // azure-functions-1.2.2/src/main/java/datadog/trace/instrumentation/azure/functions/AzureFunctionsInstrumentation.java:48
    // Regular-method annotation projection.
    result[1259] =
        declares(method().annotatedWith("com.microsoft.azure.functions.annotation.FunctionName"));
    // datadog/tracing/trace-annotation/src/main/java/datadog/trace/instrumentation/trace_annotation/DoNotTraceAnnotationInstrumentation.java:30
    // Regular-method annotation projection.
    result[1260] = declares(method().annotatedWith("datadog.trace.api.DoNotTrace"));
    // opentelemetry/opentelemetry-annotations-1.26/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/AddingSpanAttributesInstrumentation.java:37
    // Regular-method annotation projection.
    result[1261] =
        declares(
            method()
                .annotatedWith(
                    "io.opentelemetry.instrumentation.annotations.AddingSpanAttributes"));
    // opentelemetry/opentelemetry-annotations-1.20/src/main/java/datadog/trace/instrumentation/opentelemetry/annotations/WithSpanAnnotationInstrumentation.java:38
    // Regular-method annotation projection.
    result[1262] =
        declares(method().annotatedWith("io.opentelemetry.instrumentation.annotations.WithSpan"));
    // rs/jakarta-rs-annotations-3.0/src/main/java/datadog/trace/instrumentation/jakarta3/JakartaRsAnnotationsInstrumentation.java:75
    // Regular-method annotation projection.
    result[1263] = declares(method().annotatedWith("jakarta.ws.rs.Path"));
    // rs/jax-rs/jax-rs-annotations/jax-rs-annotations-1.1.1/src/main/java/datadog/trace/instrumentation/jaxrs1/JaxRsAnnotationsInstrumentation.java:73
    // Regular-method annotation projection.
    result[1264] = declares(method().annotatedWith("javax.ws.rs.Path"));
  }

  static ClassMatcher create() {
    ClassMatcher[] branches = branches();
    ClassMatcher graph = branches[0];
    for (int i = 1; i < branches.length; i++) {
      graph = graph.or(branches[i]);
    }
    return graph;
  }
}
