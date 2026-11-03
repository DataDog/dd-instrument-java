package datadog.instrument.classmatch;

/** DD_TRACE patterns compiled at build time (generated; index order). */
final class DDTracePrecompiled {
  private DDTracePrecompiled() {}

  static MemberPattern[] compiled() {
    MemberPattern[] result = new MemberPattern[1275];
    fill0(result);
    fill200(result);
    fill400(result);
    fill600(result);
    fill800(result);
    fill1000(result);
    fill1200(result);
    return result;
  }

  private static void fill0(MemberPattern[] result) {
    result[0] =
        MemberPattern.of(
            false,
            new String[] {"getNode"},
            null,
            0,
            0,
            6,
            "(Lcom/aerospike/client/cluster/Cluster;*)Lcom/aerospike/client/cluster/Node;");
    result[1] =
        MemberPattern.of(false, new String[] {"execute"}, null, 0, 0, 2, "(Ljava/lang/Runnable;)");
    result[2] =
        MemberPattern.of(
            false,
            new String[] {"getNodeRead", "getNodeWrite"},
            null,
            0,
            0,
            6,
            "(Lcom/aerospike/client/cluster/Cluster;*)Lcom/aerospike/client/cluster/Node;");
    result[3] = MemberPattern.of(false, new String[] {"invoke"}, null, 0, 0, 0, null);
    result[4] =
        MemberPattern.of(
            false,
            new String[] {"enqueue"},
            null,
            1,
            0,
            1,
            "(Lakka/actor/ActorRef;Lakka/dispatch/Envelope;)V");
    result[5] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 5, "(Ljava/lang/Runnable;*)");
    result[6] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[7] =
        MemberPattern.of(
            false, new String[] {"externalPush", "fullExternalPush"}, null, 0, 0, 0, null);
    result[8] =
        MemberPattern.of(false, new String[] {"exec", "fork", "cancel"}, null, 0, 0, 0, null);
    result[9] = MemberPattern.of(false, new String[] {"doExec", "exec"}, null, 0, 0, 0, null);
    result[10] = MemberPattern.of(false, new String[] {"fork"}, null, 0, 0, 0, null);
    result[11] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[12] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[13] =
        MemberPattern.of(
            false, new String[] {"sendMessage"}, null, 0, 0, 5, "(Lakka/dispatch/Envelope;*)");
    result[14] = MemberPattern.of(false, new String[] {"apply"}, null, 0, 0, 0, null);
    result[15] =
        MemberPattern.of(
            false,
            new String[] {"bindAndHandleAsync"},
            null,
            0,
            0,
            4,
            "(Lscala/Function1;??????Lakka/stream/Materializer;)");
    result[16] =
        MemberPattern.of(
            false,
            new String[] {"bindAndHandleAsync"},
            null,
            0,
            0,
            4,
            "(Lscala/Function1;?????Lakka/stream/Materializer;)");
    result[17] =
        MemberPattern.of(
            false,
            new String[] {"bindAndHandleAsync"},
            null,
            0,
            0,
            4,
            "(Lscala/Function1;????Lakka/stream/Materializer;)");
    result[18] =
        MemberPattern.of(
            false, new String[] {"bindAndHandle"}, null, 0, 0, 5, "(Lakka/stream/scaladsl/Flow;*)");
    result[19] =
        MemberPattern.of(
            false,
            new String[] {"akka$http$impl$engine$client$PoolMasterActor$$startPoolInterface"},
            null,
            0,
            0,
            0,
            null);
    result[20] =
        MemberPattern.of(
            false,
            null,
            null,
            0,
            0,
            1,
            "(Lakka/http/scaladsl/settings/RoutingSettings;)Lakka/http/scaladsl/server/ExceptionHandler;");
    result[21] =
        MemberPattern.of(true, new String[] {"oneHundredContinueSent"}, null, 0, 0, 0, null);
    result[22] =
        MemberPattern.of(
            false, new String[] {"getBoolean"}, null, 1, 0, 1, "(Ljava/lang/String;)Z");
    result[23] =
        MemberPattern.of(
            false,
            new String[] {"toStrict"},
            null,
            0,
            8,
            1,
            "(Lscala/concurrent/duration/FiniteDuration;Lakka/stream/Materializer;)Lscala/concurrent/Future;");
    result[24] =
        MemberPattern.of(
            false,
            new String[] {"byteStringUnmarshaller"},
            null,
            8,
            0,
            1,
            "(Lcom/fasterxml/jackson/databind/ObjectMapper;Ljava/lang/Class;)Lakka/http/javadsl/unmarshalling/Unmarshaller;");
    result[25] =
        MemberPattern.of(
            false,
            new String[] {"unmarshaller"},
            null,
            8,
            0,
            1,
            "(Lcom/fasterxml/jackson/databind/ObjectMapper;Ljava/lang/Class;)Lakka/http/javadsl/unmarshalling/Unmarshaller;");
    result[26] =
        MemberPattern.of(
            false,
            new String[] {"unmarshaller"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/unmarshalling/Unmarshaller;Lakka/http/scaladsl/unmarshalling/Unmarshaller;)Lakka/http/scaladsl/unmarshalling/Unmarshaller;");
    result[27] =
        MemberPattern.of(
            false, new String[] {"cookies"}, null, 0, 8, 1, "()Lscala/collection/immutable/Seq;");
    result[28] =
        MemberPattern.of(
            false,
            new String[] {"fields"},
            null,
            0,
            8,
            1,
            "()Lakka/http/scaladsl/model/Uri$Query;");
    result[29] =
        MemberPattern.of(
            false,
            new String[] {"formField"},
            null,
            8,
            0,
            1,
            "(Lakka/http/scaladsl/server/directives/FormFieldDirectives;Lakka/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[30] =
        MemberPattern.of(
            false,
            new String[] {"formFields"},
            null,
            8,
            0,
            1,
            "(Lakka/http/scaladsl/server/directives/FormFieldDirectives;Lakka/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[31] =
        MemberPattern.of(
            false,
            new String[] {"formField"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[32] =
        MemberPattern.of(
            false,
            new String[] {"formField"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Lakka/http/scaladsl/server/Directive;");
    result[33] =
        MemberPattern.of(
            false,
            new String[] {"formFields"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[34] =
        MemberPattern.of(
            false,
            new String[] {"formFields"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Lakka/http/scaladsl/server/Directive;");
    result[35] =
        MemberPattern.of(false, new String[] {"value"}, null, 0, 0, 1, "()Ljava/lang/String;");
    result[36] =
        MemberPattern.of(
            false, new String[] {"headers"}, null, 0, 8, 1, "()Lscala/collection/immutable/Seq;");
    result[37] = MemberPattern.of(false, new String[] {"entity"}, null, 1, 8, 2, "()");
    result[38] =
        MemberPattern.of(
            false,
            new String[] {"entity"},
            null,
            8,
            0,
            1,
            "(Lakka/http/scaladsl/server/directives/MarshallingDirectives;Lakka/http/scaladsl/unmarshalling/Unmarshaller;)Lakka/http/scaladsl/server/Directive;");
    result[39] =
        MemberPattern.of(
            false,
            new String[] {"parameter"},
            null,
            8,
            0,
            1,
            "(Lakka/http/scaladsl/server/directives/ParameterDirectives;Lakka/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[40] =
        MemberPattern.of(
            false,
            new String[] {"parameters"},
            null,
            8,
            0,
            1,
            "(Lakka/http/scaladsl/server/directives/ParameterDirectives;Lakka/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[41] =
        MemberPattern.of(
            false,
            new String[] {"parameter"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[42] =
        MemberPattern.of(
            false,
            new String[] {"parameter"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Lakka/http/scaladsl/server/Directive;");
    result[43] =
        MemberPattern.of(
            false,
            new String[] {"parameters"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[44] =
        MemberPattern.of(
            false,
            new String[] {"parameters"},
            null,
            0,
            8,
            1,
            "(Lakka/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Lakka/http/scaladsl/server/Directive;");
    result[45] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Lakka/http/scaladsl/model/Uri$Path;Ljava/lang/Object;Lakka/http/scaladsl/server/util/Tuple;)");
    result[46] =
        MemberPattern.of(
            false,
            new String[] {"request"},
            null,
            0,
            8,
            1,
            "()Lakka/http/scaladsl/model/HttpRequest;");
    result[47] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            8,
            1,
            "(Ljava/lang/Object;Lscala/concurrent/ExecutionContext;Lakka/stream/Materializer;)Lscala/concurrent/Future;");
    result[48] =
        MemberPattern.of(
            false,
            new String[] {"queryString"},
            null,
            0,
            8,
            1,
            "(Ljava/nio/charset/Charset;)Lscala/Option;");
    result[49] =
        MemberPattern.of(false, new String[] {"rawQueryString"}, null, 0, 8, 1, "()Lscala/Option;");
    result[50] =
        MemberPattern.of(
            false,
            new String[] {"query"},
            null,
            0,
            8,
            1,
            "(Ljava/nio/charset/Charset;Lakka/http/scaladsl/model/Uri$ParsingMode;)Lakka/http/scaladsl/model/Uri$Query;");
    result[51] =
        MemberPattern.of(
            false,
            new String[] {"filter"},
            null,
            0,
            8,
            1,
            "(Ljava/lang/String;Lakka/http/scaladsl/unmarshalling/Unmarshaller;)Lakka/http/scaladsl/server/Directive;");
    result[52] =
        MemberPattern.of(
            false,
            new String[] {"repeatedFilter"},
            null,
            0,
            8,
            1,
            "(Ljava/lang/String;Lakka/http/scaladsl/unmarshalling/Unmarshaller;)Lakka/http/scaladsl/server/Directive;");
    result[53] =
        MemberPattern.of(
            false,
            new String[] {"getRedirect"},
            null,
            0,
            0,
            4,
            "(??Lorg/apache/http/protocol/HttpContext;*)");
    result[54] = MemberPattern.of(true, new String[] {"callback"}, null, 0, 0, 0, null);
    result[55] =
        MemberPattern.of(
            false, new String[] {"<init>"}, null, 0, 0, 2, "(Ljava/lang/String;Ljava/net/URI;)");
    result[56] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;ILjava/lang/String;)");
    result[57] = MemberPattern.of(false, new String[] {"toURI"}, null, 0, 0, 2, "()");
    result[58] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Ljava/net/InetAddress;Ljava/lang/String;I)");
    result[59] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lcom/linecorp/armeria/common/grpc/protocol/ArmeriaMessageDeframer$Listener;*)");
    result[60] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lcom/linecorp/armeria/internal/common/grpc/TransportStatusListener;*)");
    result[61] = MemberPattern.of(false, new String[] {"process"}, null, 0, 0, 0, null);
    result[62] = MemberPattern.of(false, new String[] {"deframe"}, null, 0, 0, 0, null);
    result[63] =
        MemberPattern.of(
            false, new String[] {"<init>"}, null, 0, 0, 4, "(????Lio/grpc/MethodDescriptor;*)");
    result[64] =
        MemberPattern.of(
            false, new String[] {"<init>"}, null, 0, 0, 4, "(??Lio/grpc/MethodDescriptor;*)");
    result[65] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[66] = MemberPattern.of(false, new String[] {"request"}, null, 0, 0, 2, "(I)");
    result[67] = MemberPattern.of(false, new String[] {"halfClose"}, null, 0, 0, 2, "()");
    result[68] = MemberPattern.of(false, new String[] {"sendMessage"}, null, 0, 0, 0, null);
    result[69] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 4, "(??)");
    result[70] =
        MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 4, "(??Ljava/lang/Throwable;)");
    result[71] = MemberPattern.of(false, new String[] {"onNext"}, null, 0, 0, 0, null);
    result[72] = MemberPattern.of(false, new String[] {"messageRead"}, null, 0, 0, 0, null);
    result[73] =
        MemberPattern.of(
            false,
            new String[] {"addService"},
            null,
            0,
            0,
            5,
            "(Lio/grpc/ServerServiceDefinition;*)");
    result[74] =
        MemberPattern.of(
            false,
            new String[] {"serve"},
            null,
            0,
            0,
            6,
            "(Lcom/linecorp/armeria/server/ServiceRequestContext;*)Lcom/linecorp/armeria/common/HttpResponse;");
    result[75] =
        MemberPattern.of(false, new String[] {"resolveExecutionInterceptors"}, null, 0, 0, 0, null);
    result[76] =
        MemberPattern.of(false, new String[] {"resolveExecutionInterceptors"}, null, 0, 0, 0, null);
    result[77] =
        MemberPattern.of(
            false,
            new String[] {"handleRequest"},
            null,
            0,
            0,
            4,
            "(??Lcom/amazonaws/services/lambda/runtime/Context;*)");
    result[78] =
        MemberPattern.of(false, new String[] {"resolveExecutionInterceptors"}, null, 0, 0, 0, null);
    result[79] = MemberPattern.of(false, new String[] {"doExecute"}, null, 0, 0, 0, null);
    result[80] =
        MemberPattern.of(false, new String[] {"newRequestHandler2Chain"}, null, 0, 0, 0, null);
    result[81] = MemberPattern.of(false, new String[] {"doExecute"}, null, 0, 0, 0, null);
    result[82] =
        MemberPattern.of(false, new String[] {"resolveExecutionInterceptors"}, null, 0, 0, 0, null);
    result[83] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            1,
            0,
            4,
            "(?Lsoftware/amazon/awssdk/core/internal/http/RequestExecutionContext;*)");
    result[84] =
        MemberPattern.of(false, new String[] {"resolveExecutionInterceptors"}, null, 0, 0, 0, null);
    result[85] =
        MemberPattern.of(false, new String[] {"newRequestHandler2Chain"}, null, 0, 0, 0, null);
    result[86] =
        MemberPattern.of(false, new String[] {"resolveExecutionInterceptors"}, null, 0, 0, 0, null);
    result[87] =
        MemberPattern.of(true, new String[] {"receiveAttributeNames"}, null, 0, 0, 0, null);
    result[88] =
        MemberPattern.of(false, new String[] {"newRequestHandler2Chain"}, null, 0, 0, 0, null);
    result[89] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lcom/amazonaws/services/sqs/model/Message;*)");
    result[90] = MemberPattern.of(false, new String[] {"getMessages"}, null, 0, 0, 0, null);
    result[91] =
        MemberPattern.of(false, new String[] {"resolveExecutionInterceptors"}, null, 0, 0, 0, null);
    result[92] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lsoftware/amazon/awssdk/services/sqs/model/Message;*)");
    result[93] = MemberPattern.of(false, new String[] {"afterExecution"}, null, 0, 0, 0, null);
    result[94] = MemberPattern.of(true, new String[] {"attributeNames"}, null, 0, 0, 0, null);
    result[95] = MemberPattern.of(false, new String[] {"build"}, null, 0, 0, 0, null);
    result[96] =
        MemberPattern.of(
            false,
            new String[] {"build"},
            null,
            0,
            0,
            1,
            "()Lsoftware/amazon/awssdk/services/sqs/model/ReceiveMessageResponse;");
    result[97] =
        MemberPattern.of(
            false,
            new String[] {"toBuilder"},
            null,
            0,
            0,
            1,
            "()Lsoftware/amazon/awssdk/services/sqs/model/ReceiveMessageResponse$Builder;");
    result[98] = MemberPattern.of(false, new String[] {"messages"}, null, 0, 0, 0, null);
    result[99] =
        MemberPattern.of(
            false,
            new String[] {"receive", "send", "sendFault"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/axis2/context/MessageContext;*)");
    result[100] =
        MemberPattern.of(
            false,
            new String[] {"resumeReceive", "resumeSend", "resumeSendFault"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/axis2/context/MessageContext;*)");
    result[101] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/axis2/context/MessageContext;*)");
    result[102] =
        MemberPattern.of(false, new String[] {"sendSOAPRequestAsync"}, null, 0, 0, 0, null);
    result[103] = MemberPattern.of(false, new String[] {"releaseBuffer"}, null, 0, 0, 0, null);
    result[104] = MemberPattern.of(false, new String[] {"invokeDispose"}, null, 1, 0, 0, null);
    result[105] = MemberPattern.of(false, new String[] {"tryTransaction"}, null, 1, 0, 0, null);
    result[106] = MemberPattern.of(false, new String[] {"sendResponse"}, null, 1, 0, 0, null);
    result[107] =
        MemberPattern.of(
            false, null, "com/microsoft/azure/functions/annotation/FunctionName", 0, 0, 0, null);
    result[108] =
        MemberPattern.of(
            false,
            new String[] {"<init>", "<clinit>"},
            "com/microsoft/azure/functions/annotation/FunctionName",
            0,
            0,
            0,
            null);
    result[109] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            1,
            0,
            2,
            "(Lcom/microsoft/azure/functions/internal/spi/middleware/MiddlewareContext;Lcom/microsoft/azure/functions/internal/spi/middleware/MiddlewareChain;)");
    result[110] =
        MemberPattern.of(
            false, new String[] {"execute"}, null, 1, 0, 5, "(Ljava/util/List;Ljava/util/List;*)");
    result[111] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            1,
            0,
            2,
            "(Lcom/microsoft/azure/functions/internal/spi/middleware/MiddlewareContext;Lcom/microsoft/azure/functions/internal/spi/middleware/MiddlewareChain;)");
    result[112] =
        MemberPattern.of(
            false, new String[] {"eval"}, null, 0, 0, 2, "(Ljava/lang/String;Lbsh/NameSpace;)");
    result[113] =
        MemberPattern.of(
            false,
            new String[] {"eval"},
            null,
            0,
            0,
            2,
            "(Ljava/io/Reader;Lbsh/NameSpace;Ljava/lang/String;)");
    result[114] =
        MemberPattern.of(
            false, new String[] {"eval"}, null, 0, 0, 2, "(Ljava/lang/String;Ljava/lang/String;)");
    result[115] =
        MemberPattern.of(false, new String[] {"scheduleDrainBuffers"}, null, 0, 0, 2, "()");
    result[116] = MemberPattern.of(false, new String[] {"execute"}, null, 0, 0, 0, null);
    result[117] = MemberPattern.of(false, new String[] {"flow"}, null, 0, 0, 0, null);
    result[118] =
        MemberPattern.of(
            false,
            new String[] {"parseRequest"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;*)");
    result[119] =
        MemberPattern.of(false, new String[] {"parse"}, null, 1, 0, 1, "([CIIC)Ljava/util/Map;");
    result[120] = MemberPattern.of(false, new String[] {"getInputStream"}, null, 1, 0, 2, "()");
    result[121] = MemberPattern.of(false, new String[] {"next"}, null, 1, 0, 2, "()");
    result[122] = MemberPattern.of(false, new String[] {"openStream"}, null, 1, 0, 2, "()");
    result[123] =
        MemberPattern.of(
            false,
            new String[] {"parseRequest"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;*)");
    result[124] =
        MemberPattern.of(
            false,
            new String[] {"parseParameterMap"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;*)");
    result[125] =
        MemberPattern.of(
            false,
            new String[] {"getItemIterator"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;*)");
    result[126] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[127] = MemberPattern.of(false, new String[] {"configure"}, null, 1, 0, 4, "(?Z)");
    result[128] =
        MemberPattern.of(
            false, new String[] {"deserialize"}, null, 1, 0, 4, "(Ljava/lang/String;?[B)");
    result[129] = MemberPattern.of(false, new String[] {"configure"}, null, 1, 0, 4, "(?Z)");
    result[130] =
        MemberPattern.of(
            false, new String[] {"serialize"}, null, 1, 0, 6, "(Ljava/lang/String;*)[B");
    result[131] = MemberPattern.of(false, null, null, 1, 0, 3, "(*)Lrx/Observable;");
    result[132] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lcom/couchbase/client/core/message/CouchbaseRequest;*)");
    result[133] =
        MemberPattern.of(
            false,
            new String[] {"encode"},
            null,
            0,
            0,
            4,
            "(Lcom/couchbase/client/deps/io/netty/channel/ChannelHandlerContext;?Ljava/util/List;)");
    result[134] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 4, "(??Ljava/util/Set;*)");
    result[135] =
        MemberPattern.of(false, new String[] {"keyValueStatusToException"}, null, 8, 0, 0, null);
    result[136] = MemberPattern.of(false, new String[] {"requestTracer"}, null, 0, 0, 0, null);
    result[137] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 4, "(??Ljava/util/Set;*)");
    result[138] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lcom/couchbase/client/core/util/ConnectionString;*)");
    result[139] =
        MemberPattern.of(false, new String[] {"keyValueStatusToException"}, null, 8, 0, 0, null);
    result[140] =
        MemberPattern.of(false, new String[] {"execute"}, null, 0, 0, 2, "([Ljava/lang/Object;)");
    result[141] = MemberPattern.of(false, new String[] {"invoke"}, null, 0, 0, 0, null);
    result[142] = MemberPattern.of(false, new String[] {"loadEvaluatorClass"}, null, 0, 0, 2, "()");
    result[143] = MemberPattern.of(false, new String[] {"initialize"}, null, 0, 0, 4, "(?)");
    result[144] = MemberPattern.of(false, new String[] {"getProviderHooks"}, null, 0, 0, 2, "()");
    result[145] =
        MemberPattern.of(
            false,
            new String[] {
              "getBooleanEvaluation",
              "getStringEvaluation",
              "getIntegerEvaluation",
              "getDoubleEvaluation",
              "getObjectEvaluation"
            },
            null,
            0,
            0,
            4,
            "(???)");
    result[146] = MemberPattern.of(false, new String[] {"detect"}, null, 8, 0, 2, "()");
    result[147] = MemberPattern.of(false, null, "datadog/trace/api/DoNotTrace", 0, 0, 0, null);
    result[148] =
        MemberPattern.of(
            false,
            new String[] {"<init>", "<clinit>"},
            "datadog/trace/api/DoNotTrace",
            0,
            0,
            0,
            null);
    result[149] = MemberPattern.of(false, null, "datadog/trace/api/DoNotTrace", 0, 0, 0, null);
    result[150] =
        MemberPattern.of(
            false,
            new String[] {"<init>", "<clinit>"},
            "datadog/trace/api/DoNotTrace",
            0,
            0,
            0,
            null);
    result[151] =
        MemberPattern.of(
            false,
            new String[] {"persistObject", "deleteObject", "refreshObject"},
            null,
            0,
            0,
            0,
            null);
    result[152] =
        MemberPattern.of(
            false,
            new String[] {"refreshAllObjects", "persistObjects", "deleteObjects", "findObjects"},
            null,
            0,
            0,
            0,
            null);
    result[153] =
        MemberPattern.of(
            false, new String[] {"findObject"}, null, 0, 0, 4, "(???Ljava/lang/String;)");
    result[154] =
        MemberPattern.of(
            false, new String[] {"findObject"}, null, 0, 0, 4, "(??Ljava/lang/Class;?)");
    result[155] =
        MemberPattern.of(
            false,
            new String[] {
              "execute",
              "executeInternal",
              "executeList",
              "executeResultList",
              "executeResultUnique",
              "executeUnique",
              "executeWithArray",
              "executeWithMap",
              "deletePersistentAll",
              "deletePersistentInternal"
            },
            null,
            0,
            0,
            0,
            null);
    result[156] = MemberPattern.of(false, new String[] {"commit", "rollback"}, null, 0, 0, 0, null);
    result[157] = MemberPattern.of(false, new String[] {"newSession"}, null, 2, 0, 2, "()");
    result[158] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 4, "(?Ljava/util/List;*)");
    result[159] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 4, "(?Ljava/util/List;*)");
    result[160] =
        MemberPattern.of(
            false,
            new String[] {"init"},
            null,
            8,
            0,
            4,
            "(?Ljava/util/Set;?)Ljava/util/concurrent/CompletionStage;");
    result[161] =
        MemberPattern.of(
            false, new String[] {"render"}, null, 1, 0, 5, "(Lio/dropwizard/views/View;*)");
    result[162] =
        MemberPattern.of(
            false,
            new String[] {"performRequestAsync", "performRequestAsyncNoCatch"},
            null,
            0,
            0,
            4,
            "(Ljava/lang/String;Ljava/lang/String;???Lorg/elasticsearch/client/ResponseListener;?)");
    result[163] =
        MemberPattern.of(
            false,
            new String[] {"performRequestAsyncNoCatch"},
            null,
            0,
            0,
            2,
            "(Lorg/elasticsearch/client/Request;Lorg/elasticsearch/client/ResponseListener;)");
    result[164] =
        MemberPattern.of(
            false,
            new String[] {"performRequest"},
            null,
            0,
            0,
            2,
            "(Lorg/elasticsearch/client/Request;)");
    result[165] =
        MemberPattern.of(
            false,
            new String[] {"performRequestAsync"},
            null,
            0,
            0,
            2,
            "(Lorg/elasticsearch/client/Request;Lorg/elasticsearch/client/ResponseListener;)");
    result[166] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            5,
            "(Lorg/elasticsearch/action/Action;Lorg/elasticsearch/action/ActionRequest;Lorg/elasticsearch/action/ActionListener;*)");
    result[167] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            5,
            "(Lorg/elasticsearch/action/Action;Lorg/elasticsearch/action/ActionRequest;Lorg/elasticsearch/action/ActionListener;*)");
    result[168] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            5,
            "(Lorg/elasticsearch/action/Action;Lorg/elasticsearch/action/ActionRequest;Lorg/elasticsearch/action/ActionListener;*)");
    result[169] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            5,
            "(Lorg/elasticsearch/action/Action;Lorg/elasticsearch/action/ActionRequest;Lorg/elasticsearch/action/ActionListener;*)");
    result[170] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            2,
            "(Lorg/elasticsearch/action/ActionType;Lorg/elasticsearch/action/ActionRequest;Lorg/elasticsearch/action/ActionListener;)");
    result[171] =
        MemberPattern.of(false, new String[] {"onResponse", "onFailure"}, null, 0, 0, 4, "(?)");
    result[172] =
        MemberPattern.of(
            false,
            new String[] {"handleMatch"},
            null,
            0,
            0,
            4,
            "(Lcom/twitter/finagle/http/Request;?)");
    result[173] =
        MemberPattern.of(
            false,
            new String[] {"wrap"},
            null,
            0,
            0,
            6,
            "(Ljava/lang/Object;*)Lfreemarker/template/TemplateModel;");
    result[174] =
        MemberPattern.of(
            false,
            new String[] {"setAttemptFuture"},
            null,
            0,
            0,
            2,
            "(Lcom/google/api/core/ApiFuture;)");
    result[175] = MemberPattern.of(false, new String[] {"addToBlackList"}, null, 0, 0, 4, "(?)");
    result[176] =
        MemberPattern.of(
            false,
            new String[] {"newBuilder"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/String;Lcom/google/cloud/pubsub/v1/MessageReceiver;*)");
    result[177] =
        MemberPattern.of(
            false, new String[] {"lookup"}, null, 0, 0, 5, "(Ljdk/vm/ci/meta/ResolvedJavaField;*)");
    result[178] = MemberPattern.of(false, new String[] {"findTargetClasses"}, null, 0, 0, 0, null);
    result[179] =
        MemberPattern.of(
            false, new String[] {"linkAtBuildTime"}, null, 0, 0, 5, "(Ljava/lang/Class;*)");
    result[180] = MemberPattern.of(false, new String[] {"main"}, null, 0, 0, 0, null);
    result[181] =
        MemberPattern.of(false, new String[] {"extractDriverArguments"}, null, 0, 0, 0, null);
    result[182] = MemberPattern.of(false, new String[] {"beforeAnalysis"}, null, 0, 0, 0, null);
    result[183] = MemberPattern.of(false, new String[] {"initialize"}, null, 0, 0, 0, null);
    result[184] =
        MemberPattern.of(
            false,
            new String[] {"checkInstrumentationDefaultState"},
            null,
            0,
            0,
            3,
            "(*)Lgraphql/execution/instrumentation/Instrumentation;");
    result[185] =
        MemberPattern.of(false, new String[] {"build"}, null, 0, 0, 3, "(*)Lgraphql/GraphQL;");
    result[186] =
        MemberPattern.of(
            false, new String[] {"getException"}, null, 0, 0, 3, "(*)Ljava/lang/Throwable;");
    result[187] =
        MemberPattern.of(
            false,
            new String[] {"notifyFailure"},
            null,
            2,
            0,
            5,
            "(Lorg/glassfish/grizzly/filterchain/FilterChainContext;Ljava/lang/Throwable;*)");
    result[188] =
        MemberPattern.of(
            false,
            new String[] {"executeFilter"},
            null,
            0,
            0,
            4,
            "(??Lorg/glassfish/grizzly/filterchain/FilterChainContext;*)");
    result[189] =
        MemberPattern.of(
            false,
            new String[] {"handleRead"},
            null,
            1,
            0,
            5,
            "(Lorg/glassfish/grizzly/filterchain/FilterChainContext;*)");
    result[190] =
        MemberPattern.of(
            false,
            new String[] {"setInputBuffer"},
            null,
            0,
            0,
            2,
            "(Lorg/glassfish/grizzly/http/io/InputBuffer;)");
    result[191] = MemberPattern.of(false, new String[] {"read"}, null, 0, 0, 2, "()");
    result[192] = MemberPattern.of(false, new String[] {"read"}, null, 0, 0, 2, "([B)");
    result[193] = MemberPattern.of(false, new String[] {"read"}, null, 0, 0, 2, "([BII)");
    result[194] = MemberPattern.of(false, new String[] {"readBuffer"}, null, 0, 0, 2, "()");
    result[195] = MemberPattern.of(false, new String[] {"readBuffer"}, null, 0, 0, 2, "(I)");
    result[196] = MemberPattern.of(false, new String[] {"isFinished"}, null, 0, 0, 2, "()");
    result[197] = MemberPattern.of(false, new String[] {"recycle"}, null, 0, 0, 2, "()");
    result[198] =
        MemberPattern.of(
            false,
            new String[] {"setInputBuffer"},
            null,
            0,
            0,
            2,
            "(Lorg/glassfish/grizzly/http/io/InputBuffer;)");
    result[199] = MemberPattern.of(false, new String[] {"read"}, null, 0, 0, 2, "()");
  }

  private static void fill200(MemberPattern[] result) {
    result[200] = MemberPattern.of(false, new String[] {"read"}, null, 0, 0, 2, "([C)");
    result[201] = MemberPattern.of(false, new String[] {"read"}, null, 0, 0, 2, "([CII)");
    result[202] =
        MemberPattern.of(false, new String[] {"read"}, null, 0, 0, 2, "(Ljava/nio/CharBuffer;)");
    result[203] = MemberPattern.of(false, new String[] {"isFinished"}, null, 0, 0, 2, "()");
    result[204] = MemberPattern.of(false, new String[] {"recycle"}, null, 0, 0, 2, "()");
    result[205] =
        MemberPattern.of(
            false,
            new String[] {"handleRead"},
            null,
            1,
            0,
            5,
            "(Lorg/glassfish/grizzly/filterchain/FilterChainContext;Lorg/glassfish/grizzly/http/HttpHeader;*)");
    result[206] =
        MemberPattern.of(
            false,
            new String[] {"prepareResponse"},
            null,
            2,
            0,
            5,
            "(Lorg/glassfish/grizzly/filterchain/FilterChainContext;Lorg/glassfish/grizzly/http/HttpRequestPacket;Lorg/glassfish/grizzly/http/HttpResponsePacket;Lorg/glassfish/grizzly/http/HttpContent;*)");
    result[207] =
        MemberPattern.of(
            false,
            new String[] {"processParameters"},
            null,
            0,
            0,
            5,
            "(Lorg/glassfish/grizzly/Buffer;IILjava/nio/charset/Charset;*)");
    result[208] =
        MemberPattern.of(
            false, new String[] {"run"}, null, 0, 0, 2, "(Lio/netty/channel/Channel;)");
    result[209] =
        MemberPattern.of(
            false,
            new String[] {"start"},
            null,
            0,
            0,
            2,
            "(Lio/grpc/internal/ClientStreamListener;)");
    result[210] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[211] = MemberPattern.of(false, new String[] {"request"}, null, 0, 0, 2, "(I)");
    result[212] = MemberPattern.of(false, new String[] {"halfClose"}, null, 0, 0, 2, "()");
    result[213] = MemberPattern.of(false, new String[] {"sendMessage"}, null, 0, 0, 0, null);
    result[214] = MemberPattern.of(false, new String[] {"closeObserver"}, null, 0, 0, 4, "(???)");
    result[215] =
        MemberPattern.of(
            false, new String[] {"exceptionThrown"}, null, 0, 0, 2, "(Lio/grpc/Status;)");
    result[216] =
        MemberPattern.of(
            false, new String[] {"messageRead", "messagesAvailable"}, null, 0, 0, 0, null);
    result[217] = MemberPattern.of(false, new String[] {"headersRead"}, null, 0, 0, 0, null);
    result[218] = MemberPattern.of(false, new String[] {"runInContext"}, null, 0, 0, 0, null);
    result[219] = MemberPattern.of(false, new String[] {"build"}, null, 1, 0, 2, "()");
    result[220] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 2, "(Ljava/io/Reader;)");
    result[221] =
        MemberPattern.of(
            false, new String[] {"nextName", "nextString"}, null, 1, 0, 1, "()Ljava/lang/String;");
    result[222] =
        MemberPattern.of(
            false,
            new String[] {"addListener"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/Runnable;Ljava/util/concurrent/Executor;)");
    result[223] = MemberPattern.of(false, new String[] {"run"}, null, 1, 0, 2, "()");
    result[224] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lcom/hazelcast/client/impl/HazelcastClientInstanceImpl;*)");
    result[225] =
        MemberPattern.of(
            false,
            new String[] {
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
              "values"
            },
            null,
            1,
            0,
            0,
            null);
    result[226] =
        MemberPattern.of(
            false,
            new String[] {
              "addAllAsync",
              "addAsync",
              "estimateAsync",
              "getAsync",
              "putAsync",
              "readManyAsync",
              "removeAsync",
              "setAsync"
            },
            null,
            1,
            0,
            3,
            "(*)Lcom/hazelcast/core/ICompletableFuture;");
    result[227] = MemberPattern.of(false, new String[] {"invokeOnSelection"}, null, 0, 0, 0, null);
    result[228] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lcom/hazelcast/client/impl/HazelcastClientInstanceImpl;*)");
    result[229] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lcom/hazelcast/client/impl/clientside/HazelcastClientInstanceImpl;*)");
    result[230] =
        MemberPattern.of(
            false, new String[] {"setOperationName"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[231] = MemberPattern.of(false, new String[] {"invokeOnSelection"}, null, 0, 0, 0, null);
    result[232] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lcom/hazelcast/client/impl/clientside/HazelcastClientInstanceImpl;*)");
    result[233] =
        MemberPattern.of(
            false,
            new String[] {"handleEventMessageOnCallingThread"},
            null,
            0,
            0,
            5,
            "(Lcom/hazelcast/client/impl/protocol/ClientMessage;*)");
    result[234] =
        MemberPattern.of(
            false, new String[] {"list", "uniqueResult", "scroll"}, null, 0, 0, 0, null);
    result[235] = MemberPattern.of(false, new String[] {"before"}, null, 0, 0, 2, "()");
    result[236] =
        MemberPattern.of(
            false,
            new String[] {"list", "executeUpdate", "uniqueResult", "scroll"},
            null,
            0,
            0,
            0,
            null);
    result[237] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 2, "()");
    result[238] =
        MemberPattern.of(
            false,
            new String[] {
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
              "internalLoad"
            },
            null,
            0,
            0,
            0,
            null);
    result[239] =
        MemberPattern.of(
            false, new String[] {"get"}, null, 0, 0, 6, "(Ljava/lang/String;*)Ljava/lang/Object;");
    result[240] =
        MemberPattern.of(
            false,
            new String[] {"beginTransaction", "getTransaction"},
            null,
            0,
            0,
            3,
            "(*)Lorg/hibernate/Transaction;");
    result[241] = MemberPattern.of(false, new String[] {"commit"}, null, 0, 0, 2, "()");
    result[242] =
        MemberPattern.of(
            false, new String[] {"list", "uniqueResult", "scroll"}, null, 0, 0, 0, null);
    result[243] = MemberPattern.of(false, new String[] {"before"}, null, 0, 0, 2, "()");
    result[244] =
        MemberPattern.of(
            false,
            new String[] {"list", "executeUpdate", "uniqueResult", "scroll"},
            null,
            0,
            0,
            0,
            null);
    result[245] =
        MemberPattern.of(
            false,
            new String[] {"openSession", "openStatelessSession"},
            null,
            0,
            0,
            1,
            "()Lorg/hibernate/Session;");
    result[246] =
        MemberPattern.of(
            false,
            new String[] {"openSession", "openStatelessSession"},
            null,
            0,
            0,
            1,
            "()Lorg/hibernate/StatelessSession;");
    result[247] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 2, "()");
    result[248] =
        MemberPattern.of(
            false,
            new String[] {
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
              "internalLoad"
            },
            null,
            0,
            0,
            0,
            null);
    result[249] =
        MemberPattern.of(
            false, new String[] {"get"}, null, 0, 0, 6, "(Ljava/lang/String;*)Ljava/lang/Object;");
    result[250] =
        MemberPattern.of(
            false,
            new String[] {"beginTransaction", "getTransaction"},
            null,
            0,
            0,
            3,
            "(*)Lorg/hibernate/Transaction;");
    result[251] = MemberPattern.of(false, new String[] {"commit"}, null, 0, 0, 2, "()");
    result[252] = MemberPattern.of(false, new String[] {"getOutputs"}, null, 0, 0, 0, null);
    result[253] =
        MemberPattern.of(
            false, new String[] {"getExecutionObservable"}, null, 0, 0, 3, "(*)Lrx/Observable;");
    result[254] =
        MemberPattern.of(
            false, new String[] {"getFallbackObservable"}, null, 0, 0, 3, "(*)Lrx/Observable;");
    result[255] =
        MemberPattern.of(
            false,
            new String[] {
              "loadCacheAsync",
              "sizeAsync",
              "sizeLongAsync",
              "invokeAllAsync",
              "getAllAsync",
              "getEntriesAsync",
              "getAllOutTxAsync",
              "containsKeysAsync",
              "putAllAsync",
              "removeAllAsync"
            },
            null,
            1,
            0,
            0,
            null);
    result[256] =
        MemberPattern.of(
            false,
            new String[] {
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
              "invokeAsync"
            },
            null,
            1,
            0,
            0,
            null);
    result[257] =
        MemberPattern.of(
            false,
            new String[] {
              "loadCache",
              "size",
              "sizeLong",
              "invokeAll",
              "getAll",
              "getEntries",
              "getAllOutTx",
              "containsKeys",
              "putAll",
              "removeAll"
            },
            null,
            1,
            0,
            0,
            null);
    result[258] =
        MemberPattern.of(
            false,
            new String[] {
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
              "invoke"
            },
            null,
            1,
            0,
            0,
            null);
    result[259] =
        MemberPattern.of(
            false,
            new String[] {"query"},
            null,
            1,
            0,
            5,
            "(Lorg/apache/ignite/cache/query/Query;*)");
    result[260] =
        MemberPattern.of(
            false,
            new String[] {"query"},
            null,
            1,
            0,
            5,
            "(Lorg/apache/ignite/cache/query/SqlFieldsQuery;*)");
    result[261] =
        MemberPattern.of(
            false, new String[] {"createJsonParser"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[262] =
        MemberPattern.of(
            false, new String[] {"createJsonParser"}, null, 0, 0, 2, "(Ljava/io/InputStream;)");
    result[263] =
        MemberPattern.of(
            false, new String[] {"createJsonParser"}, null, 0, 0, 2, "(Ljava/io/Reader;)");
    result[264] =
        MemberPattern.of(
            false, new String[] {"createJsonParser"}, null, 0, 0, 2, "(Ljava/net/URL;)");
    result[265] = MemberPattern.of(false, new String[] {"createJsonParser"}, null, 0, 0, 2, "([B)");
    result[266] =
        MemberPattern.of(false, new String[] {"createJsonParser"}, null, 1, 0, 2, "([BII)");
    result[267] =
        MemberPattern.of(false, new String[] {"getText"}, null, 1, 0, 1, "()Ljava/lang/String;");
    result[268] =
        MemberPattern.of(
            false, new String[] {"getCurrentName"}, null, 1, 0, 1, "()Ljava/lang/String;");
    result[269] =
        MemberPattern.of(false, new String[] {"getText", "getCurrentName"}, null, 0, 0, 0, null);
    result[270] =
        MemberPattern.of(
            false,
            new String[] {"getCurrentName", "nextFieldName"},
            null,
            1,
            0,
            1,
            "()Ljava/lang/String;");
    result[271] =
        MemberPattern.of(
            false,
            new String[] {"getCurrentName", "nextFieldName"},
            null,
            1,
            0,
            1,
            "()Ljava/lang/String;");
    result[272] =
        MemberPattern.of(
            false,
            new String[] {"getCurrentName", "nextFieldName"},
            null,
            1,
            0,
            1,
            "()Ljava/lang/String;");
    result[273] =
        MemberPattern.of(
            false,
            new String[] {"getCurrentName", "nextFieldName"},
            null,
            1,
            0,
            1,
            "()Ljava/lang/String;");
    result[274] =
        MemberPattern.of(
            false,
            new String[] {"getCurrentName", "nextFieldName"},
            null,
            1,
            0,
            1,
            "()Ljava/lang/String;");
    result[275] =
        MemberPattern.of(
            false, new String[] {"createParser"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[276] =
        MemberPattern.of(
            false, new String[] {"createParser"}, null, 1, 0, 2, "(Ljava/io/InputStream;)");
    result[277] =
        MemberPattern.of(false, new String[] {"createParser"}, null, 1, 0, 2, "(Ljava/io/Reader;)");
    result[278] =
        MemberPattern.of(false, new String[] {"createParser"}, null, 1, 0, 2, "(Ljava/net/URL;)");
    result[279] = MemberPattern.of(false, new String[] {"createParser"}, null, 1, 0, 2, "([B)");
    result[280] = MemberPattern.of(false, new String[] {"createParser"}, null, 1, 0, 2, "([BII)");
    result[281] =
        MemberPattern.of(
            false,
            new String[] {"getText", "getValueAsString"},
            null,
            1,
            0,
            1,
            "()Ljava/lang/String;");
    result[282] =
        MemberPattern.of(
            false,
            new String[] {"asParser"},
            null,
            1,
            0,
            3,
            "(*)Lcom/fasterxml/jackson/core/JsonParser;");
    result[283] =
        MemberPattern.of(false, new String[] {"visitTotalProbeCount"}, null, 0, 0, 0, null);
    result[284] = MemberPattern.of(false, new String[] {"instrument"}, null, 0, 0, 2, "([B)");
    result[285] =
        MemberPattern.of(
            false,
            new String[] {"visitMethodInsn"},
            null,
            0,
            0,
            2,
            "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Z)");
    result[286] = MemberPattern.of(false, new String[] {"visitInsn"}, null, 0, 0, 2, "(I)");
    result[287] = MemberPattern.of(false, new String[] {"visitIntInsn"}, null, 0, 0, 2, "(II)");
    result[288] =
        MemberPattern.of(
            false, new String[] {"visitLdcInsn"}, null, 0, 0, 2, "(Ljava/lang/Object;)");
    result[289] = MemberPattern.of(false, new String[] {"visitMaxs"}, null, 0, 0, 4, "(I?)");
    result[290] = MemberPattern.of(false, new String[] {"insertProbe"}, null, 0, 0, 2, "(I)");
    result[291] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[292] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[293] = MemberPattern.of(false, new String[] {"tryFire"}, null, 0, 0, 2, "(I)");
    result[294] =
        MemberPattern.of(false, new String[] {"execute"}, null, 0, 0, 2, "(Ljava/lang/Runnable;)");
    result[295] =
        MemberPattern.of(
            false,
            new String[] {"dispatch"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/Runnable;Lkotlinx/coroutines/scheduling/TaskContext;*)");
    result[296] =
        MemberPattern.of(false, new String[] {"dispatch"}, null, 0, 0, 2, "(Ljava/lang/Runnable;)");
    result[297] =
        MemberPattern.of(
            false,
            new String[] {"rejectedExecution", "rejected"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/Runnable;*)");
    result[298] =
        MemberPattern.of(
            false, new String[] {"beforeExecute"}, null, 0, 0, 4, "(?Ljava/lang/Runnable;*)");
    result[299] =
        MemberPattern.of(
            false, new String[] {"afterExecute"}, null, 0, 0, 5, "(Ljava/lang/Runnable;*)");
    result[300] =
        MemberPattern.of(false, new String[] {"remove"}, null, 0, 0, 3, "(*)Ljava/lang/Runnable;");
    result[301] =
        MemberPattern.of(
            false, new String[] {"externalPush", "externalSubmit"}, null, 0, 0, 0, null);
    result[302] = MemberPattern.of(false, new String[] {"poolSubmit"}, null, 0, 0, 0, null);
    result[303] =
        MemberPattern.of(false, new String[] {"scheduleDelayedTask"}, null, 0, 0, 0, null);
    result[304] =
        MemberPattern.of(
            false, new String[] {"doExec", "exec", "fork", "cancel"}, null, 0, 0, 0, null);
    result[305] = MemberPattern.of(false, new String[] {"doExec", "exec"}, null, 0, 0, 0, null);
    result[306] = MemberPattern.of(false, new String[] {"fork"}, null, 0, 0, 0, null);
    result[307] = MemberPattern.of(true, new String[] {"base"}, null, 0, 0, 1, "I");
    result[308] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[309] = MemberPattern.of(false, new String[] {"run"}, null, 1, 0, 2, "()");
    result[310] =
        MemberPattern.of(false, new String[] {"sched"}, null, 2, 0, 2, "(Ljava/util/TimerTask;JJ)");
    result[311] = MemberPattern.of(false, new String[] {"cancel"}, null, 1, 0, 2, "()");
    result[312] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[313] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 5, "(Ljava/io/InputStream;*)");
    result[314] =
        MemberPattern.of(
            false,
            new String[] {"start"},
            null,
            0,
            0,
            2,
            "([Ljava/lang/String;Ljava/util/Map;Ljava/lang/String;[LProcessBuilder/Redirect;Z)");
    result[315] =
        MemberPattern.of(
            false,
            new String[] {"exec"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;[Ljava/lang/String;Ljava/io/File;)");
    result[316] = MemberPattern.of(false, new String[] {"runHooks"}, null, 8, 0, 0, null);
    result[317] =
        MemberPattern.of(
            false,
            new String[] {"newMBeanServer"},
            null,
            0,
            0,
            3,
            "(*)Ljavax/management/MBeanServer;");
    result[318] = MemberPattern.of(false, new String[] {"start"}, null, 0, 0, 1, "()V");
    result[319] =
        MemberPattern.of(
            false, new String[] {"startReadPoller"}, null, 0, 0, 1, "()Lsun/nio/ch/Poller;");
    result[320] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Ljava/util/concurrent/Executor;Ljava/lang/String;ILjava/lang/Runnable;)");
    result[321] =
        MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 1, "(Ljava/lang/Runnable;)V");
    result[322] = MemberPattern.of(false, new String[] {"mount"}, null, 0, 0, 0, null);
    result[323] = MemberPattern.of(false, new String[] {"unmount"}, null, 0, 0, 0, null);
    result[324] = MemberPattern.of(false, new String[] {"afterDone"}, null, 0, 0, 2, "(Z)");
    result[325] = MemberPattern.of(false, new String[] {"afterTerminate"}, null, 0, 0, 2, "(ZZ)");
    result[326] =
        MemberPattern.of(
            false,
            new String[] {"downcallHandle"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/foreign/MemorySegment;*)");
    result[327] = MemberPattern.of(false, new String[] {"find"}, null, 0, 0, 3, "(*)J");
    result[328] =
        MemberPattern.of(false, new String[] {"loadClass"}, null, 1, 8, 2, "(Ljava/lang/String;)");
    result[329] =
        MemberPattern.of(false, new String[] {"loadClass"}, null, 4, 8, 2, "(Ljava/lang/String;)");
    result[330] =
        MemberPattern.of(false, new String[] {"loadClass"}, null, 1, 8, 2, "(Ljava/lang/String;Z)");
    result[331] =
        MemberPattern.of(false, new String[] {"loadClass"}, null, 4, 8, 2, "(Ljava/lang/String;Z)");
    result[332] =
        MemberPattern.of(
            false,
            new String[] {"defineClass"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)");
    result[333] =
        MemberPattern.of(
            false,
            new String[] {"connect", "getOutputStream", "getInputStream"},
            null,
            1,
            0,
            0,
            null);
    result[334] = MemberPattern.of(false, new String[] {"plainConnect"}, null, 4, 0, 0, null);
    result[335] =
        MemberPattern.of(
            false, new String[] {"connect"}, null, 0, 0, 5, "(Ljava/net/SocketAddress;I*)");
    result[336] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 2, "()");
    result[337] = MemberPattern.of(false, new String[] {"getInputStream"}, null, 0, 0, 0, null);
    result[338] = MemberPattern.of(false, new String[] {"getOutputStream"}, null, 0, 0, 0, null);
    result[339] = MemberPattern.of(false, new String[] {"openConnection"}, null, 1, 0, 0, null);
    result[340] =
        MemberPattern.of(
            false, new String[] {"send"}, null, 1, 0, 4, "(Ljava/net/http/HttpRequest;?)");
    result[341] =
        MemberPattern.of(
            false,
            new String[] {"sendAsync"},
            null,
            1,
            0,
            5,
            "(Ljava/net/http/HttpRequest;Ljava/net/http/HttpResponse$BodyHandler;*)");
    result[342] = MemberPattern.of(false, new String[] {"allocateDirect"}, null, 8, 0, 2, "(I)");
    result[343] = MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 2, "(JI)");
    result[344] =
        MemberPattern.of(
            false,
            new String[] {"map"},
            null,
            0,
            0,
            2,
            "(Ljava/nio/channels/FileChannel$MapMode;JJ)");
    result[345] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            0,
            0,
            5,
            "(Ljava/rmi/Remote;Ljava/lang/reflect/Method;*)");
    result[346] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lsun/rmi/transport/Connection;Ljava/rmi/server/ObjID;*)");
    result[347] =
        MemberPattern.of(
            false,
            new String[] {"getTarget"},
            null,
            8,
            0,
            5,
            "(Lsun/rmi/transport/ObjectEndpoint;*)");
    result[348] =
        MemberPattern.of(false, new String[] {"getMdc"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[349] = MemberPattern.of(false, new String[] {"getMdcCopy"}, null, 0, 0, 2, "()");
    result[350] =
        MemberPattern.of(
            false,
            new String[] {"publish"},
            null,
            0,
            0,
            5,
            "(Lorg/jboss/logmanager/ExtLogRecord;*)");
    result[351] =
        MemberPattern.of(
            false, new String[] {"getResource"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[352] =
        MemberPattern.of(
            false, new String[] {"getResourceAsStream"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[353] =
        MemberPattern.of(
            false, new String[] {"loadModuleClass"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[354] =
        MemberPattern.of(
            false, new String[] {"loadModuleClass"}, null, 0, 0, 2, "(Ljava/lang/String;Z)");
    result[355] = MemberPattern.of(false, new String[] {"execute*"}, null, 1, 0, 2, "()");
    result[356] = MemberPattern.of(false, new String[] {"getConnection"}, null, 0, 0, 0, null);
    result[357] = MemberPattern.of(false, new String[] {"pollFirst"}, null, 0, 0, 4, "(?)");
    result[358] =
        MemberPattern.of(false, new String[] {"updateTransactionStatus"}, null, 0, 0, 0, null);
    result[359] =
        MemberPattern.of(false, new String[] {"getPooledConnectionAndInfo"}, null, 0, 0, 0, null);
    result[360] = MemberPattern.of(false, new String[] {"getConnection"}, null, 0, 0, 0, null);
    result[361] = MemberPattern.of(false, new String[] {"connect"}, null, 0, 0, 0, null);
    result[362] =
        MemberPattern.of(false, new String[] {"getPooledConnectionAndInfo"}, null, 0, 0, 0, null);
    result[363] =
        MemberPattern.of(
            false,
            new String[] {"connect*"},
            null,
            0,
            0,
            6,
            "(Ljava/lang/String;Ljava/util/Properties;*)Ljava/sql/Connection;");
    result[364] = MemberPattern.of(true, new String[] {"handoffQueue"}, null, 0, 0, 0, null);
    result[365] = MemberPattern.of(false, new String[] {"borrow"}, null, 0, 0, 0, null);
    result[366] = MemberPattern.of(false, new String[] {"getConnection"}, null, 0, 0, 0, null);
    result[367] =
        MemberPattern.of(false, new String[] {"waitUntilSequenceExceeded"}, null, 0, 0, 0, null);
    result[368] = MemberPattern.of(false, new String[] {"next"}, null, 0, 0, 2, "()");
    result[369] = MemberPattern.of(false, new String[] {"getString"}, null, 0, 0, 2, "(I)");
    result[370] =
        MemberPattern.of(false, new String[] {"getString"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[371] = MemberPattern.of(false, new String[] {"getNString"}, null, 0, 0, 2, "(I)");
    result[372] =
        MemberPattern.of(false, new String[] {"getNString"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[373] =
        MemberPattern.of(false, new String[] {"execute*"}, null, 1, 0, 5, "(Ljava/lang/String;*)");
    result[374] =
        MemberPattern.of(
            false,
            new String[] {"sendCommand"},
            null,
            0,
            0,
            5,
            "(Lredis/clients/jedis/Protocol$Command;*)");
    result[375] =
        MemberPattern.of(
            false,
            new String[] {"sendCommand"},
            null,
            0,
            0,
            5,
            "(Lredis/clients/jedis/commands/ProtocolCommand;*)");
    result[376] =
        MemberPattern.of(
            false,
            new String[] {"executeCommand"},
            null,
            1,
            0,
            5,
            "(Lredis/clients/jedis/CommandObject;*)");
    result[377] =
        MemberPattern.of(false, new String[] {"fromString"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[378] =
        MemberPattern.of(
            false,
            new String[] {"setProperty"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Object;)");
    result[379] =
        MemberPattern.of(false, new String[] {"getName"}, null, 1, 0, 1, "()Ljava/lang/String;");
    result[380] =
        MemberPattern.of(false, new String[] {"getValue"}, null, 1, 0, 1, "()Ljava/lang/String;");
    result[381] = MemberPattern.of(false, new String[] {"asMap"}, null, 1, 0, 2, "()");
    result[382] = MemberPattern.of(false, new String[] {"getHeaders"}, null, 1, 0, 2, "()");
    result[383] = MemberPattern.of(false, new String[] {"getRequestCookies"}, null, 1, 0, 2, "()");
    result[384] = MemberPattern.of(false, new String[] {"readEntity"}, null, 1, 0, 4, "(????)");
    result[385] =
        MemberPattern.of(
            false,
            new String[] {"header"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Object;)");
    result[386] =
        MemberPattern.of(false, new String[] {"location"}, null, 1, 0, 2, "(Ljava/net/URI;)");
    result[387] =
        MemberPattern.of(
            false,
            new String[] {"header"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Object;)");
    result[388] =
        MemberPattern.of(false, new String[] {"location"}, null, 1, 0, 2, "(Ljava/net/URI;)");
    result[389] = MemberPattern.of(false, new String[] {"getInputStream"}, null, 0, 0, 2, "()");
    result[390] = MemberPattern.of(false, new String[] {"getPathParameters"}, null, 1, 0, 2, "(Z)");
    result[391] =
        MemberPattern.of(false, new String[] {"getQueryParameters"}, null, 1, 0, 2, "(Z)");
    result[392] = MemberPattern.of(false, new String[] {"proceed"}, null, 0, 0, 2, "()");
    result[393] =
        MemberPattern.of(
            false,
            new String[] {"readMultiPart"},
            null,
            4,
            0,
            4,
            "(??????)Lorg/glassfish/jersey/media/multipart/MultiPart;");
    result[394] =
        MemberPattern.of(false, new String[] {"process"}, null, 1, 8, 2, "(Ljava/lang/Throwable;)");
    result[395] = MemberPattern.of(false, new String[] {"getPathSegments"}, null, 0, 0, 2, "(Z)");
    result[396] = MemberPattern.of(false, new String[] {"getPathParameters"}, null, 0, 0, 2, "(Z)");
    result[397] = MemberPattern.of(false, new String[] {"proceed"}, null, 0, 0, 2, "()");
    result[398] =
        MemberPattern.of(
            false,
            new String[] {"readMultiPart"},
            null,
            4,
            0,
            4,
            "(??????)Lorg/glassfish/jersey/media/multipart/MultiPart;");
    result[399] = MemberPattern.of(false, new String[] {"getPathSegments"}, null, 0, 0, 2, "(Z)");
  }

  private static void fill400(MemberPattern[] result) {
    result[400] =
        MemberPattern.of(
            false,
            new String[] {"submit", "createRunnableForAsyncProcessing"},
            null,
            0,
            0,
            5,
            "(Lorg/glassfish/jersey/client/ClientRequest;Lorg/glassfish/jersey/client/ResponseCallback;*)");
    result[401] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            0,
            0,
            5,
            "(Lorg/glassfish/jersey/client/ClientRequest;*)");
    result[402] =
        MemberPattern.of(
            true,
            new String[] {"_dispatcherType"},
            null,
            0,
            0,
            1,
            "Ljakarta/servlet/DispatcherType;");
    result[403] =
        MemberPattern.of(false, new String[] {"extractContentParameters"}, null, 0, 0, 2, "()");
    result[404] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 0, null);
    result[405] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 2, "()");
    result[406] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 4, "(?)");
    result[407] = MemberPattern.of(false, new String[] {"extractParameters"}, null, 0, 0, 2, "()");
    result[408] =
        MemberPattern.of(
            false,
            new String[] {"decodeTo"},
            null,
            1,
            0,
            5,
            "(Ljava/io/InputStream;Lorg/eclipse/jetty/util/MultiMap;Ljava/lang/String;*)");
    result[409] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 2, "()");
    result[410] =
        MemberPattern.of(false, new String[] {"getPart"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[411] =
        MemberPattern.of(false, new String[] {"extractContentParameters"}, null, 0, 0, 2, "()");
    result[412] =
        MemberPattern.of(
            false, new String[] {"getParts"}, null, 0, 0, 2, "(Lorg/eclipse/jetty/util/MultiMap;)");
    result[413] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 2, "()");
    result[414] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 4, "(?)");
    result[415] =
        MemberPattern.of(false, new String[] {"extractContentParameters"}, null, 0, 0, 2, "()");
    result[416] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 0, null);
    result[417] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 2, "()");
    result[418] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 4, "(?)");
    result[419] =
        MemberPattern.of(false, new String[] {"extractContentParameters"}, null, 0, 0, 2, "()");
    result[420] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 0, null);
    result[421] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 2, "()");
    result[422] = MemberPattern.of(false, new String[] {"getParts"}, null, 0, 0, 4, "(?)");
    result[423] =
        MemberPattern.of(
            false,
            new String[] {"listener", "onSuccess", "onFailure", "onComplete"},
            null,
            1,
            0,
            4,
            "(?)");
    result[424] =
        MemberPattern.of(
            false,
            new String[] {"upgrade"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/client/Response;Lorg/eclipse/jetty/io/EndPoint;)");
    result[425] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(Lorg/eclipse/jetty/client/api/Request;?)");
    result[426] =
        MemberPattern.of(
            false,
            new String[] {"listener"},
            null,
            1,
            0,
            5,
            "(Lorg/eclipse/jetty/client/api/Request$RequestListener;*)");
    result[427] =
        MemberPattern.of(
            false,
            new String[] {"onSuccess"},
            null,
            1,
            0,
            5,
            "(Lorg/eclipse/jetty/client/api/Request$SuccessListener;*)");
    result[428] =
        MemberPattern.of(
            false,
            new String[] {"onFailure"},
            null,
            1,
            0,
            5,
            "(Lorg/eclipse/jetty/client/api/Request$FailureListener;*)");
    result[429] =
        MemberPattern.of(
            false,
            new String[] {"onComplete"},
            null,
            1,
            0,
            5,
            "(Lorg/eclipse/jetty/client/api/Response$CompleteListener;*)");
    result[430] = MemberPattern.of(false, new String[] {"dispatch"}, null, 1, 0, 2, "()");
    result[431] =
        MemberPattern.of(
            false,
            new String[] {"sendResponse"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/http/MetaData$Response;Ljava/nio/ByteBuffer;ZLorg/eclipse/jetty/util/Callback;)");
    result[432] = MemberPattern.of(false, new String[] {"recycle"}, null, 0, 0, 2, "()");
    result[433] =
        MemberPattern.of(
            false, new String[] {"handleException"}, null, 0, 0, 2, "(Ljava/lang/Throwable;)");
    result[434] =
        MemberPattern.of(
            false, new String[] {"setContextPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[435] =
        MemberPattern.of(
            false, new String[] {"setServletPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[436] =
        MemberPattern.of(
            false, new String[] {"setRequestedSessionId"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[437] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/HttpChannel;)");
    result[438] =
        MemberPattern.of(
            false,
            new String[] {"handleAsync"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/HttpChannel;)");
    result[439] = MemberPattern.of(false, new String[] {"recycle"}, null, 0, 0, 2, "()");
    result[440] =
        MemberPattern.of(
            false,
            new String[] {"setContext"},
            null,
            0,
            0,
            5,
            "(Lorg/eclipse/jetty/server/handler/ContextHandler$Context;Ljava/lang/String;*)");
    result[441] =
        MemberPattern.of(
            false, new String[] {"setRequestedSessionId"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[442] =
        MemberPattern.of(
            false,
            new String[] {"resolveRequestedSessionId"},
            null,
            4,
            0,
            2,
            "(Lorg/eclipse/jetty/server/Request;)");
    result[443] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            4,
            "(Lorg/eclipse/jetty/server/Request;??)");
    result[444] = MemberPattern.of(false, new String[] {"recycle"}, null, 0, 0, 2, "()");
    result[445] = MemberPattern.of(false, new String[] {"commitResponse"}, null, 0, 0, 2, "(Z)");
    result[446] = MemberPattern.of(false, new String[] {"completeResponse"}, null, 0, 0, 2, "()");
    result[447] = MemberPattern.of(false, new String[] {"setResponse"}, null, 0, 0, 5, "(I*)");
    result[448] = MemberPattern.of(false, new String[] {"reset"}, null, 0, 0, 5, "(Z*)");
    result[449] =
        MemberPattern.of(
            false, new String[] {"setContextPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[450] =
        MemberPattern.of(
            false, new String[] {"setServletPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[451] =
        MemberPattern.of(
            false, new String[] {"setRequestedSessionId"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[452] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/HttpConnection;)");
    result[453] =
        MemberPattern.of(
            false,
            new String[] {"handleAsync"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/HttpConnection;)");
    result[454] = MemberPattern.of(false, new String[] {"commitResponse"}, null, 0, 0, 2, "(Z)");
    result[455] = MemberPattern.of(false, new String[] {"completeResponse"}, null, 0, 0, 2, "()");
    result[456] = MemberPattern.of(false, new String[] {"setResponse"}, null, 0, 0, 5, "(I*)");
    result[457] = MemberPattern.of(false, new String[] {"reset"}, null, 0, 0, 2, "()");
    result[458] =
        MemberPattern.of(
            false, new String[] {"setContextPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[459] =
        MemberPattern.of(
            false, new String[] {"setServletPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[460] =
        MemberPattern.of(
            false, new String[] {"setRequestedSessionId"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[461] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/AbstractHttpConnection;)");
    result[462] =
        MemberPattern.of(
            false,
            new String[] {"handleAsync"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/AbstractHttpConnection;)");
    result[463] =
        MemberPattern.of(
            false,
            new String[] {"commitResponse"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/http/HttpGenerator$ResponseInfo;Ljava/nio/ByteBuffer;Z)");
    result[464] = MemberPattern.of(false, new String[] {"handle"}, null, 0, 0, 0, null);
    result[465] = MemberPattern.of(false, new String[] {"reset", "recycle"}, null, 0, 0, 2, "()");
    result[466] =
        MemberPattern.of(
            false, new String[] {"handleException"}, null, 0, 0, 2, "(Ljava/lang/Throwable;)");
    result[467] =
        MemberPattern.of(
            false, new String[] {"setContextPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[468] =
        MemberPattern.of(
            false, new String[] {"setServletPath"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[469] =
        MemberPattern.of(
            false, new String[] {"setRequestedSessionId"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[470] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/HttpChannel;)");
    result[471] =
        MemberPattern.of(
            false,
            new String[] {"handleAsync"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/server/HttpChannel;)");
    result[472] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 2, "()");
    result[473] =
        MemberPattern.of(
            false,
            new String[] {"sendResponse"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/http/HttpGenerator$ResponseInfo;Ljava/nio/ByteBuffer;ZLorg/eclipse/jetty/util/Callback;)");
    result[474] =
        MemberPattern.of(
            false,
            new String[] {"sendResponse"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/http/MetaData$Response;Ljava/nio/ByteBuffer;ZLorg/eclipse/jetty/util/Callback;)");
    result[475] =
        MemberPattern.of(
            false,
            new String[] {"sendResponse"},
            null,
            0,
            0,
            2,
            "(Lorg/eclipse/jetty/http/MetaData$Response;Ljava/nio/ByteBuffer;ZLorg/eclipse/jetty/util/Callback;)");
    result[476] =
        MemberPattern.of(false, new String[] {"dispatch", "execute"}, null, 0, 0, 0, null);
    result[477] = MemberPattern.of(false, new String[] {"receive"}, null, 1, 0, 2, "()");
    result[478] = MemberPattern.of(false, new String[] {"receive"}, null, 1, 0, 4, "(?)");
    result[479] = MemberPattern.of(false, new String[] {"receiveNoWait"}, null, 1, 0, 2, "()");
    result[480] = MemberPattern.of(false, new String[] {"close"}, null, 1, 0, 2, "()");
    result[481] = MemberPattern.of(false, new String[] {"acknowledge*"}, null, 1, 0, 2, "()");
    result[482] = MemberPattern.of(false, new String[] {"recover"}, null, 0, 0, 2, "()");
    result[483] = MemberPattern.of(false, new String[] {"commit", "rollback"}, null, 0, 0, 2, "()");
    result[484] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 2, "()");
    result[485] =
        MemberPattern.of(false, new String[] {"parse"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[486] =
        MemberPattern.of(
            false, new String[] {"parsePayload"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[487] =
        MemberPattern.of(
            false,
            new String[] {"_jspService"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;*)");
    result[488] = MemberPattern.of(false, new String[] {"compile"}, null, 1, 0, 2, "()");
    result[489] =
        MemberPattern.of(
            false,
            new String[] {"childrenInvoker"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/notification/RunNotifier;*)");
    result[490] =
        MemberPattern.of(
            false,
            new String[] {"run"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/notification/RunNotifier;*)");
    result[491] =
        MemberPattern.of(
            false,
            new String[] {"runChild"},
            null,
            0,
            0,
            5,
            "(Lio/cucumber/junit/PickleRunners$PickleRunner;Lorg/junit/runner/notification/RunNotifier;*)");
    result[492] =
        MemberPattern.of(
            false,
            new String[] {"run"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/notification/RunNotifier;*)");
    result[493] =
        MemberPattern.of(
            false,
            new String[] {"runTest"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/notification/RunNotifier;*)");
    result[494] =
        MemberPattern.of(
            false,
            new String[] {"run"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/notification/RunNotifier;*)");
    result[495] =
        MemberPattern.of(
            false,
            new String[] {"runChild"},
            null,
            0,
            0,
            4,
            "(?Lorg/junit/runner/notification/RunNotifier;)");
    result[496] =
        MemberPattern.of(false, new String[] {"fireTestSuiteStarted"}, null, 0, 0, 0, null);
    result[497] =
        MemberPattern.of(
            false,
            new String[] {"run"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/notification/RunNotifier;*)");
    result[498] =
        MemberPattern.of(
            false,
            new String[] {"runLeaf"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runners/model/Statement;Lorg/junit/runner/Description;Lorg/junit/runner/notification/RunNotifier;*)");
    result[499] =
        MemberPattern.of(
            false,
            new String[] {"fireTestSuiteStarted"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/Description;*)");
    result[500] =
        MemberPattern.of(
            false,
            new String[] {"fireTestSuiteFinished"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/Description;*)");
    result[501] =
        MemberPattern.of(
            false,
            new String[] {"invokeMethod"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runners/model/FrameworkMethod;*)");
    result[502] = MemberPattern.of(false, new String[] {"order"}, null, 0, 0, 0, null);
    result[503] =
        MemberPattern.of(
            false,
            new String[] {"sort"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/runner/manipulation/Sorter;*)");
    result[504] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/platform/engine/ExecutionRequest;*)");
    result[505] = MemberPattern.of(false, new String[] {"shouldBeSkipped"}, null, 0, 0, 4, "(?)");
    result[506] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/platform/engine/ExecutionRequest;*)");
    result[507] = MemberPattern.of(false, new String[] {"shouldBeSkipped"}, null, 0, 0, 4, "(?)");
    result[508] =
        MemberPattern.of(
            false,
            new String[] {"discover"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/platform/engine/EngineDiscoveryRequest;Lorg/junit/platform/engine/UniqueId;*)");
    result[509] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/platform/engine/ExecutionRequest;*)");
    result[510] = MemberPattern.of(false, new String[] {"shouldBeSkipped"}, null, 0, 0, 4, "(?)");
    result[511] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(???Lorg/junit/platform/engine/support/hierarchical/ThrowableCollector/Factory;*)");
    result[512] = MemberPattern.of(false, new String[] {"execute"}, null, 0, 0, 2, "()");
    result[513] =
        MemberPattern.of(
            false,
            new String[] {"discover"},
            null,
            0,
            0,
            5,
            "(Lorg/junit/platform/engine/EngineDiscoveryRequest;Lorg/junit/platform/engine/UniqueId;*)");
    result[514] =
        MemberPattern.of(false, new String[] {"getDefaultTestClassOrderer"}, null, 0, 0, 0, null);
    result[515] =
        MemberPattern.of(false, new String[] {"getDefaultTestMethodOrderer"}, null, 0, 0, 0, null);
    result[516] =
        MemberPattern.of(false, new String[] {"sendOffsetCommitRequest"}, null, 0, 0, 4, "(?)");
    result[517] = MemberPattern.of(false, new String[] {"onJoinComplete"}, null, 0, 0, 4, "(????)");
    result[518] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/kafka/clients/consumer/ConsumerConfig;Lorg/apache/kafka/common/serialization/Deserializer;Lorg/apache/kafka/common/serialization/Deserializer;*)");
    result[519] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Ljava/util/Map;Lorg/apache/kafka/common/serialization/Deserializer;Lorg/apache/kafka/common/serialization/Deserializer;*)");
    result[520] =
        MemberPattern.of(
            false,
            new String[] {"poll"},
            null,
            1,
            0,
            4,
            "(?)Lorg/apache/kafka/clients/consumer/ConsumerRecords;");
    result[521] =
        MemberPattern.of(
            false,
            new String[] {"records"},
            null,
            1,
            0,
            6,
            "(Ljava/lang/String;*)Ljava/lang/Iterable;");
    result[522] =
        MemberPattern.of(
            false,
            new String[] {"records"},
            null,
            1,
            0,
            6,
            "(Lorg/apache/kafka/common/TopicPartition;*)Ljava/util/List;");
    result[523] =
        MemberPattern.of(false, new String[] {"iterator"}, null, 1, 0, 1, "()Ljava/util/Iterator;");
    result[524] =
        MemberPattern.of(false, new String[] {"configure"}, null, 0, 0, 2, "(Ljava/util/Map;Z)");
    result[525] = MemberPattern.of(false, new String[] {"deserialize"}, null, 0, 0, 4, "(?[B)");
    result[526] = MemberPattern.of(false, new String[] {"deserialize"}, null, 0, 0, 4, "(??[B)");
    result[527] =
        MemberPattern.of(
            false, new String[] {"deserialize"}, null, 0, 0, 4, "(??Ljava/nio/ByteBuffer;)");
    result[528] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/kafka/clients/producer/ProducerConfig;Lorg/apache/kafka/common/serialization/Serializer;Lorg/apache/kafka/common/serialization/Serializer;*)");
    result[529] =
        MemberPattern.of(false, new String[] {"ensureValidRecordSize"}, null, 2, 0, 5, "(I*)");
    result[530] =
        MemberPattern.of(
            false, new String[] {"update"}, null, 0, 0, 5, "(Lorg/apache/kafka/common/Cluster;*)");
    result[531] =
        MemberPattern.of(
            false,
            new String[] {"update"},
            null,
            0,
            0,
            4,
            "(?Lorg/apache/kafka/common/requests/MetadataResponse;*)");
    result[532] =
        MemberPattern.of(false, new String[] {"sendOffsetCommitRequest"}, null, 0, 0, 4, "(?)");
    result[533] = MemberPattern.of(false, new String[] {"onJoinComplete"}, null, 0, 0, 4, "(????)");
    result[534] =
        MemberPattern.of(true, new String[] {"offsetCommitCallbackInvoker"}, null, 0, 0, 0, null);
    result[535] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/kafka/clients/consumer/ConsumerConfig;Lorg/apache/kafka/common/serialization/Deserializer;Lorg/apache/kafka/common/serialization/Deserializer;*)");
    result[536] =
        MemberPattern.of(
            false,
            new String[] {"poll"},
            null,
            1,
            0,
            4,
            "(?)Lorg/apache/kafka/clients/consumer/ConsumerRecords;");
    result[537] =
        MemberPattern.of(
            false,
            new String[] {"records"},
            null,
            1,
            0,
            6,
            "(Ljava/lang/String;*)Ljava/lang/Iterable;");
    result[538] =
        MemberPattern.of(
            false,
            new String[] {"records"},
            null,
            1,
            0,
            6,
            "(Lorg/apache/kafka/common/TopicPartition;*)Ljava/util/List;");
    result[539] =
        MemberPattern.of(false, new String[] {"iterator"}, null, 1, 0, 1, "()Ljava/util/Iterator;");
    result[540] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/kafka/clients/producer/ProducerConfig;Lorg/apache/kafka/common/serialization/Serializer;Lorg/apache/kafka/common/serialization/Serializer;*)");
    result[541] =
        MemberPattern.of(false, new String[] {"ensureValidRecordSize"}, null, 2, 0, 5, "(I*)");
    result[542] = MemberPattern.of(true, new String[] {"coordinator"}, null, 0, 0, 0, null);
    result[543] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/kafka/clients/consumer/ConsumerConfig;Lorg/apache/kafka/common/serialization/Deserializer;Lorg/apache/kafka/common/serialization/Deserializer;*)");
    result[544] =
        MemberPattern.of(
            false,
            new String[] {"poll"},
            null,
            1,
            0,
            4,
            "(?)Lorg/apache/kafka/clients/consumer/ConsumerRecords;");
    result[545] = MemberPattern.of(false, new String[] {"onMessage"}, null, 0, 0, 0, null);
    result[546] =
        MemberPattern.of(
            false, new String[] {"update"}, null, 0, 0, 5, "(Lorg/apache/kafka/common/Cluster;*)");
    result[547] =
        MemberPattern.of(
            false,
            new String[] {"update"},
            null,
            0,
            0,
            4,
            "(?Lorg/apache/kafka/common/requests/MetadataResponse;*)");
    result[548] =
        MemberPattern.of(
            false, new String[] {"enqueueUserCallbackInvocation"}, null, 0, 0, 0, null);
    result[549] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/kafka/connect/util/ConnectorTaskId;Lorg/apache/kafka/connect/runtime/TaskStatus$Listener;*)");
    result[550] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(????Lorg/apache/kafka/streams/StreamsConfig;*)");
    result[551] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(?????Lorg/apache/kafka/streams/StreamsConfig;*)");
    result[552] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??????Lorg/apache/kafka/streams/StreamsConfig;*)");
    result[553] =
        MemberPattern.of(
            false, new String[] {"addRecords"}, null, 0, 0, 4, "(?Ljava/lang/Iterable;*)");
    result[554] = MemberPattern.of(false, new String[] {"process"}, null, 1, 0, 2, "()");
    result[555] = MemberPattern.of(false, new String[] {"process"}, null, 1, 0, 2, "(J)");
    result[556] =
        MemberPattern.of(
            false,
            new String[] {"deserialize"},
            null,
            1,
            0,
            6,
            "(Lorg/apache/kafka/clients/consumer/ConsumerRecord;*)Lorg/apache/kafka/clients/consumer/ConsumerRecord;");
    result[557] = MemberPattern.of(false, new String[] {"build"}, null, 2, 0, 4, "(?)");
    result[558] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 2, "()");
    result[559] =
        MemberPattern.of(
            false,
            new String[] {"addStepResult"},
            null,
            0,
            0,
            2,
            "(Lcom/intuit/karate/core/StepResult;)");
    result[560] =
        MemberPattern.of(
            false, new String[] {"call"}, null, 0, 64, 1, "()Lio/karatelabs/core/ScenarioResult;");
    result[561] =
        MemberPattern.of(
            false,
            new String[] {"addStepResult"},
            null,
            0,
            0,
            2,
            "(Lio/karatelabs/core/StepResult;)");
    result[562] =
        MemberPattern.of(
            false,
            new String[] {"newCoroutineContext"},
            null,
            0,
            0,
            5,
            "(Lkotlinx/coroutines/CoroutineScope;Lkotlin/coroutines/CoroutineContext;*)");
    result[563] =
        MemberPattern.of(
            false,
            new String[] {"newCoroutineContext"},
            null,
            0,
            0,
            5,
            "(Lkotlin/coroutines/CoroutineContext;Lkotlin/coroutines/CoroutineContext;*)");
    result[564] =
        MemberPattern.of(
            false,
            new String[] {"checkContext"},
            null,
            0,
            0,
            5,
            "(Lkotlin/coroutines/CoroutineContext;*)");
    result[565] =
        MemberPattern.of(
            false,
            new String[] {"collectWithContextUndispatched"},
            null,
            0,
            0,
            4,
            "(?Lkotlin/coroutines/CoroutineContext;?)");
    result[566] =
        MemberPattern.of(false, new String[] {"onCompletionInternal"}, null, 0, 0, 0, null);
    result[567] = MemberPattern.of(false, new String[] {"onStart"}, null, 0, 0, 2, "()");
    result[568] =
        MemberPattern.of(
            false,
            new String[] {"dispatch"},
            null,
            0,
            0,
            5,
            "(Lcom/lambdaworks/redis/protocol/RedisCommand;*)");
    result[569] = MemberPattern.of(false, new String[] {"connectStandalone"}, null, 0, 0, 0, null);
    result[570] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Lio/lettuce/core/protocol/RedisCommand;)");
    result[571] =
        MemberPattern.of(
            false,
            new String[] {"complete", "completeExceptionally", "onComplete", "encode"},
            null,
            0,
            0,
            0,
            null);
    result[572] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 2, "(Z)");
    result[573] =
        MemberPattern.of(
            false,
            new String[] {"decode"},
            null,
            0,
            0,
            5,
            "(Lio/netty/channel/ChannelHandlerContext;Lio/netty/buffer/ByteBuf;Lio/lettuce/core/protocol/RedisCommand;*)");
    result[574] =
        MemberPattern.of(
            false,
            new String[] {"dispatch"},
            null,
            0,
            0,
            5,
            "(Lio/lettuce/core/protocol/RedisCommand;*)");
    result[575] = MemberPattern.of(false, new String[] {"subscribe"}, null, 0, 0, 0, null);
    result[576] = MemberPattern.of(false, new String[] {"onNext"}, null, 0, 0, 0, null);
    result[577] =
        MemberPattern.of(
            false,
            new String[] {"getConnection"},
            null,
            1,
            0,
            4,
            "(?)Lio/lettuce/core/api/StatefulRedisConnection;");
    result[578] =
        MemberPattern.of(
            false,
            new String[] {"getConnectionAsync"},
            null,
            1,
            0,
            4,
            "(?)Ljava/util/concurrent/CompletableFuture;");
    result[579] =
        MemberPattern.of(
            false,
            new String[] {"getConnectionAsync"},
            null,
            1,
            0,
            4,
            "(?I)Ljava/util/concurrent/CompletableFuture;");
    result[580] =
        MemberPattern.of(
            false,
            new String[] {"connectToNodeAsync"},
            null,
            0,
            0,
            4,
            "(?Ljava/lang/String;??)Lio/lettuce/core/ConnectionFuture;");
    result[581] = MemberPattern.of(false, new String[] {"getParts"}, null, 1, 0, 2, "()");
    result[582] = MemberPattern.of(false, new String[] {"parseParameters"}, null, 1, 0, 1, "()V");
    result[583] = MemberPattern.of(false, new String[] {"parseParameters"}, null, 4, 0, 1, "()V");
    result[584] =
        MemberPattern.of(
            false, new String[] {"parsePostData"}, null, 1, 0, 1, "()Ljava/util/Hashtable;");
    result[585] =
        MemberPattern.of(
            false, new String[] {"parsePostData"}, null, 4, 0, 1, "()Ljava/util/Hashtable;");
    result[586] = MemberPattern.of(false, new String[] {"finish"}, null, 0, 0, 2, "()");
    result[587] = MemberPattern.of(false, new String[] {"finish"}, null, 0, 0, 2, "()");
    result[588] =
        MemberPattern.of(false, new String[] {"closeResponseOutput"}, null, 0, 0, 2, "(Z)");
    result[589] =
        MemberPattern.of(
            false, new String[] {"handleException"}, null, 1, 0, 4, "(Ljava/lang/Throwable;???)V");
    result[590] = MemberPattern.of(false, new String[] {"parseParameters"}, null, 1, 0, 1, "()V");
    result[591] = MemberPattern.of(false, new String[] {"parseParameters"}, null, 4, 0, 1, "()V");
    result[592] =
        MemberPattern.of(
            false, new String[] {"parsePostData"}, null, 1, 0, 1, "()Ljava/util/Hashtable;");
    result[593] =
        MemberPattern.of(
            false, new String[] {"parsePostData"}, null, 4, 0, 1, "()Ljava/util/Hashtable;");
    result[594] = MemberPattern.of(false, new String[] {"finish"}, null, 0, 0, 2, "()");
    result[595] = MemberPattern.of(false, new String[] {"finish"}, null, 0, 0, 2, "()");
    result[596] =
        MemberPattern.of(false, new String[] {"closeResponseOutput"}, null, 0, 0, 2, "(Z)");
    result[597] =
        MemberPattern.of(
            false, new String[] {"handleException"}, null, 1, 0, 4, "(Ljava/lang/Throwable;???)V");
    result[598] =
        MemberPattern.of(
            false,
            new String[] {"callAppenders"},
            null,
            1,
            0,
            2,
            "(Lorg/apache/log4j/spi/LoggingEvent;)");
    result[599] =
        MemberPattern.of(false, new String[] {"getMDC"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
  }

  private static void fill600(MemberPattern[] result) {
    result[600] = MemberPattern.of(false, new String[] {"getMDCCopy"}, null, 0, 0, 2, "()");
    result[601] =
        MemberPattern.of(
            false,
            new String[] {"createInjector"},
            null,
            9,
            0,
            3,
            "(*)Lorg/apache/logging/log4j/core/ContextDataInjector;");
    result[602] =
        MemberPattern.of(
            false,
            new String[] {"callAppenders"},
            null,
            1,
            0,
            2,
            "(Lch/qos/logback/classic/spi/ILoggingEvent;)");
    result[603] =
        MemberPattern.of(
            false,
            new String[] {"callAppenders"},
            null,
            1,
            0,
            2,
            "(Lch/qos/logback/classic/spi/ILoggingEvent;)");
    result[604] = MemberPattern.of(false, new String[] {"getMDCPropertyMap"}, null, 0, 0, 2, "()");
    result[605] = MemberPattern.of(false, new String[] {"getMdc"}, null, 0, 0, 2, "()");
    result[606] =
        MemberPattern.of(false, new String[] {"send0"}, null, 0, 0, 5, "(Ljakarta/mail/Message;*)");
    result[607] =
        MemberPattern.of(
            false, new String[] {"setContent"}, null, 0, 0, 5, "(Ljava/lang/Object;*)");
    result[608] =
        MemberPattern.of(false, new String[] {"setText"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[609] =
        MemberPattern.of(false, new String[] {"send0"}, null, 0, 0, 5, "(Ljavax/mail/Message;*)");
    result[610] =
        MemberPattern.of(
            false, new String[] {"setContent"}, null, 0, 0, 5, "(Ljava/lang/Object;*)");
    result[611] =
        MemberPattern.of(false, new String[] {"setText"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[612] =
        MemberPattern.of(
            false,
            new String[] {"customizeContainer"},
            null,
            0,
            0,
            5,
            "(Lorg/codehaus/plexus/PlexusContainer;*)");
    result[613] = MemberPattern.of(false, new String[] {"setTestsToRun"}, null, 0, 0, 0, null);
    result[614] =
        MemberPattern.of(
            false,
            new String[] {"channelRead0"},
            null,
            0,
            0,
            5,
            "(Lio/netty/channel/ChannelHandlerContext;Lio/micronaut/http/HttpRequest;*)");
    result[615] =
        MemberPattern.of(
            false,
            new String[] {"handleRouteMatch"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/web/router/RouteMatch;Lio/micronaut/http/server/netty/NettyHttpRequest;Lio/netty/channel/ChannelHandlerContext;Z*)");
    result[616] =
        MemberPattern.of(
            false,
            new String[] {"writeFinalNettyResponse"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/http/MutableHttpResponse;Lio/micronaut/http/HttpRequest;Lio/netty/channel/ChannelHandlerContext;*)");
    result[617] =
        MemberPattern.of(
            false,
            new String[] {"writeDefaultErrorResponse"},
            null,
            0,
            0,
            5,
            "(Lio/netty/channel/ChannelHandlerContext;Lio/micronaut/http/server/netty/NettyHttpRequest;Ljava/lang/Throwable;Z*)");
    result[618] =
        MemberPattern.of(
            false,
            new String[] {"channelRead0"},
            null,
            0,
            0,
            5,
            "(Lio/netty/channel/ChannelHandlerContext;Lio/micronaut/http/HttpRequest;*)");
    result[619] =
        MemberPattern.of(
            false,
            new String[] {"handleRouteMatch"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/web/router/RouteMatch;Lio/micronaut/http/server/netty/NettyHttpRequest;Lio/netty/channel/ChannelHandlerContext;*)");
    result[620] =
        MemberPattern.of(
            false,
            new String[] {"writeFinalNettyResponse"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/http/MutableHttpResponse;Lio/micronaut/http/HttpRequest;Lio/netty/channel/ChannelHandlerContext;*)");
    result[621] =
        MemberPattern.of(
            false,
            new String[] {"createDefaultErrorResponse"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/http/HttpRequest;Ljava/lang/Throwable;*)");
    result[622] = MemberPattern.of(false, new String[] {"handleNormal"}, null, 0, 0, 2, "()");
    result[623] =
        MemberPattern.of(
            false,
            new String[] {"handleNormal"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/http/server/netty/NettyHttpRequest;*)");
    result[624] =
        MemberPattern.of(
            false,
            new String[] {"findRouteMatch"},
            null,
            0,
            0,
            6,
            "(Lio/micronaut/http/HttpRequest;*)Lio/micronaut/web/router/UriRouteMatch;");
    result[625] =
        MemberPattern.of(
            false,
            new String[] {"createDefaultErrorResponse"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/http/HttpRequest;Ljava/lang/Throwable;*)");
    result[626] =
        MemberPattern.of(
            false,
            new String[] {"encodeHttpResponse"},
            null,
            0,
            0,
            4,
            "(?Lio/micronaut/http/server/netty/NettyHttpRequest;Lio/micronaut/http/MutableHttpResponse;*)");
    result[627] =
        MemberPattern.of(
            false,
            new String[] {"encodeHttpResponse"},
            null,
            0,
            0,
            4,
            "(?Lio/micronaut/http/server/netty/NettyHttpRequest;Lio/micronaut/http/HttpResponse;*)");
    result[628] =
        MemberPattern.of(
            false,
            new String[] {"encodeHttpResponse"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/http/server/netty/NettyHttpRequest;Lio/micronaut/http/HttpResponse;*)");
    result[629] =
        MemberPattern.of(
            false,
            new String[] {"encodeHttpResponse"},
            null,
            0,
            0,
            5,
            "(Lio/micronaut/http/HttpRequest;Lio/micronaut/http/HttpResponse;*)");
    result[630] = MemberPattern.of(true, new String[] {"byteBuf"}, null, 0, 0, 0, null);
    result[631] = MemberPattern.of(true, new String[] {"commandListeners"}, null, 0, 0, 0, null);
    result[632] = MemberPattern.of(true, new String[] {"applicationName"}, null, 0, 0, 0, null);
    result[633] = MemberPattern.of(true, new String[] {"applicationName"}, null, 0, 0, 0, null);
    result[634] = MemberPattern.of(true, new String[] {"commandListeners"}, null, 0, 0, 0, null);
    result[635] = MemberPattern.of(true, new String[] {"applicationName"}, null, 0, 0, 0, null);
    result[636] = MemberPattern.of(true, new String[] {"applicationName"}, null, 0, 0, 0, null);
    result[637] =
        MemberPattern.of(
            false,
            new String[] {"command"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/String;Lorg/bson/BsonDocument;*)");
    result[638] =
        MemberPattern.of(
            false,
            new String[] {"commandAsync"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/String;Lorg/bson/BsonDocument;*)");
    result[639] =
        MemberPattern.of(
            false,
            new String[] {"command"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/String;Lorg/bson/BsonDocument;*)");
    result[640] =
        MemberPattern.of(
            false,
            new String[] {"commandAsync"},
            null,
            0,
            0,
            5,
            "(Ljava/lang/String;Lorg/bson/BsonDocument;*)");
    result[641] =
        MemberPattern.of(
            false,
            new String[] {"selectServerAsync"},
            null,
            0,
            0,
            4,
            "(??Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[642] =
        MemberPattern.of(
            false,
            new String[] {"selectServerAsync"},
            null,
            0,
            0,
            4,
            "(?Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[643] =
        MemberPattern.of(
            false,
            new String[] {"getAsync"},
            null,
            0,
            0,
            4,
            "(?Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[644] =
        MemberPattern.of(
            false,
            new String[] {"getAsync"},
            null,
            0,
            0,
            5,
            "(Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[645] =
        MemberPattern.of(
            false,
            new String[] {"readAsync"},
            null,
            0,
            0,
            4,
            "(?Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[646] =
        MemberPattern.of(
            false,
            new String[] {"readAsync"},
            null,
            0,
            0,
            4,
            "(??Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[647] =
        MemberPattern.of(
            false,
            new String[] {"openAsync"},
            null,
            0,
            0,
            5,
            "(Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[648] =
        MemberPattern.of(
            false,
            new String[] {"writeAsync"},
            null,
            0,
            0,
            4,
            "(?Lcom/mongodb/internal/async/SingleResultCallback;*)");
    result[649] =
        MemberPattern.of(
            false,
            new String[] {"onEvent", "onEventSynchronous", "prepareAndExecuteOperation"},
            null,
            0,
            0,
            5,
            "(Lorg/mule/runtime/core/api/event/CoreEvent;*)");
    result[650] =
        MemberPattern.of(
            false, new String[] {"updateSelectedCoreEventTracer"}, null, 0, 0, 0, null);
    result[651] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/mule/runtime/api/component/Component;*)");
    result[652] =
        MemberPattern.of(
            false,
            new String[] {"operationComplete"},
            null,
            0,
            0,
            5,
            "(Lorg/jboss/netty/channel/ChannelFuture;*)");
    result[653] =
        MemberPattern.of(
            false,
            new String[] {"connect"},
            null,
            0,
            0,
            3,
            "(*)Lorg/jboss/netty/channel/ChannelFuture;");
    result[654] =
        MemberPattern.of(
            false,
            new String[] {"add*"},
            null,
            0,
            0,
            4,
            "(?Lorg/jboss/netty/channel/ChannelHandler;*)");
    result[655] =
        MemberPattern.of(
            false,
            new String[] {"add*"},
            null,
            0,
            0,
            4,
            "(??Lorg/jboss/netty/channel/ChannelHandler;*)");
    result[656] =
        MemberPattern.of(
            false,
            new String[] {"operationComplete"},
            null,
            0,
            0,
            5,
            "(Lio/netty/channel/ChannelFuture;*)");
    result[657] = MemberPattern.of(false, new String[] {"fire*"}, null, 1, 0, 0, null);
    result[658] =
        MemberPattern.of(
            false, new String[] {"connect"}, null, 0, 0, 3, "(*)Lio/netty/channel/ChannelFuture;");
    result[659] =
        MemberPattern.of(
            false,
            new String[] {"operationComplete"},
            null,
            0,
            0,
            5,
            "(Lio/netty/channel/ChannelFuture;*)");
    result[660] = MemberPattern.of(false, new String[] {"parseBody"}, null, 2, 0, 2, "()");
    result[661] = MemberPattern.of(false, new String[] {"fire*"}, null, 1, 0, 0, null);
    result[662] =
        MemberPattern.of(
            false, new String[] {"connect"}, null, 0, 0, 3, "(*)Lio/netty/channel/ChannelFuture;");
    result[663] =
        MemberPattern.of(
            false, new String[] {"<init>"}, null, 1, 0, 2, "(Lio/netty/buffer/ByteBuf;IZ)");
    result[664] = MemberPattern.of(false, new String[] {"runTask"}, null, 0, 0, 0, null);
    result[665] = MemberPattern.of(false, new String[] {"runTask"}, null, 0, 0, 0, null);
    result[666] = MemberPattern.of(false, new String[] {"runTask"}, null, 0, 0, 2, "()");
    result[667] = MemberPattern.of(true, new String[] {"taskQueue"}, null, 0, 0, 0, null);
    result[668] = MemberPattern.of(true, new String[] {"delayedTaskQueue"}, null, 0, 0, 0, null);
    result[669] = MemberPattern.of(true, new String[] {"scheduledTaskQueue"}, null, 0, 0, 0, null);
    result[670] = MemberPattern.of(false, new String[] {"startThread"}, null, 2, 0, 1, "()V");
    result[671] =
        MemberPattern.of(
            false,
            new String[] {"addListener"},
            null,
            0,
            0,
            5,
            "(Lio/netty/util/concurrent/GenericFutureListener;*)");
    result[672] =
        MemberPattern.of(
            false,
            new String[] {"addListeners"},
            null,
            0,
            0,
            5,
            "([Lio/netty/util/concurrent/GenericFutureListener;*)");
    result[673] =
        MemberPattern.of(
            false, new String[] {"parseExpression"}, null, 8, 0, 2, "(Ljava/lang/String;)");
    result[674] = MemberPattern.of(false, new String[] {"sendRequest"}, null, 0, 0, 2, "()");
    result[675] =
        MemberPattern.of(false, new String[] {"parse"}, null, 8, 0, 2, "(Ljava/lang/String;)");
    result[676] = MemberPattern.of(false, new String[] {"get"}, null, 8, 0, 2, "(Ljava/net/URL;)");
    result[677] = MemberPattern.of(false, new String[] {"url"}, null, 0, 0, 2, "()");
    result[678] = MemberPattern.of(false, new String[] {"sendRequest"}, null, 0, 0, 2, "()");
    result[679] =
        MemberPattern.of(false, new String[] {"parse"}, null, 8, 0, 2, "(Ljava/lang/String;)");
    result[680] = MemberPattern.of(false, new String[] {"get"}, null, 8, 0, 2, "(Ljava/net/URL;)");
    result[681] =
        MemberPattern.of(false, new String[] {"get"}, null, 8, 0, 2, "(Ljava/lang/String;)");
    result[682] =
        MemberPattern.of(
            false, new String[] {"<init>"}, null, 0, 0, 5, "(Lokhttp3/OkHttpClient$Builder;*)");
    result[683] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/chat/completions/ChatCompletionCreateParams;*)Ljava/util/concurrent/CompletableFuture;");
    result[684] =
        MemberPattern.of(
            false,
            new String[] {"createStreaming"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/chat/completions/ChatCompletionCreateParams;*)Ljava/util/concurrent/CompletableFuture;");
    result[685] = MemberPattern.of(true, new String[] {"clientOptions"}, null, 0, 0, 0, null);
    result[686] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/chat/completions/ChatCompletionCreateParams;*)Lcom/openai/core/http/HttpResponseFor;");
    result[687] =
        MemberPattern.of(
            false,
            new String[] {"createStreaming"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/chat/completions/ChatCompletionCreateParams;*)Lcom/openai/core/http/HttpResponseFor;");
    result[688] = MemberPattern.of(true, new String[] {"clientOptions"}, null, 0, 0, 0, null);
    result[689] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/completions/CompletionCreateParams;*)Ljava/util/concurrent/CompletableFuture;");
    result[690] =
        MemberPattern.of(
            false,
            new String[] {"createStreaming"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/completions/CompletionCreateParams;*)Ljava/util/concurrent/CompletableFuture;");
    result[691] = MemberPattern.of(true, new String[] {"clientOptions"}, null, 0, 0, 0, null);
    result[692] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/completions/CompletionCreateParams;*)Lcom/openai/core/http/HttpResponseFor;");
    result[693] =
        MemberPattern.of(
            false,
            new String[] {"createStreaming"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/completions/CompletionCreateParams;*)Lcom/openai/core/http/HttpResponseFor;");
    result[694] = MemberPattern.of(true, new String[] {"clientOptions"}, null, 0, 0, 0, null);
    result[695] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/embeddings/EmbeddingCreateParams;*)Lcom/openai/core/http/HttpResponseFor;");
    result[696] = MemberPattern.of(true, new String[] {"clientOptions"}, null, 0, 0, 0, null);
    result[697] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/responses/ResponseCreateParams;*)Ljava/util/concurrent/CompletableFuture;");
    result[698] =
        MemberPattern.of(
            false,
            new String[] {"createStreaming"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/responses/ResponseCreateParams;*)Ljava/util/concurrent/CompletableFuture;");
    result[699] = MemberPattern.of(true, new String[] {"clientOptions"}, null, 0, 0, 0, null);
    result[700] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/responses/ResponseCreateParams;*)Lcom/openai/core/http/HttpResponseFor;");
    result[701] =
        MemberPattern.of(
            false,
            new String[] {"createStreaming"},
            null,
            0,
            0,
            6,
            "(Lcom/openai/models/responses/ResponseCreateParams;*)Lcom/openai/core/http/HttpResponseFor;");
    result[702] = MemberPattern.of(true, new String[] {"clientOptions"}, null, 0, 0, 0, null);
    result[703] =
        MemberPattern.of(
            false,
            new String[] {"performRequest"},
            null,
            0,
            0,
            2,
            "(Lorg/opensearch/client/Request;)");
    result[704] =
        MemberPattern.of(
            false,
            new String[] {"performRequestAsync"},
            null,
            0,
            0,
            2,
            "(Lorg/opensearch/client/Request;Lorg/opensearch/client/ResponseListener;)");
    result[705] =
        MemberPattern.of(
            false,
            new String[] {"execute"},
            null,
            0,
            0,
            2,
            "(Lorg/opensearch/action/ActionType;Lorg/opensearch/action/ActionRequest;Lorg/opensearch/action/ActionListener;)");
    result[706] =
        MemberPattern.of(false, new String[] {"onResponse", "onFailure"}, null, 0, 0, 4, "(?)");
    result[707] =
        MemberPattern.of(
            false,
            new String[] {"getTracerProvider"},
            null,
            0,
            0,
            3,
            "(*)Lio/opentelemetry/trace/TracerProvider;");
    result[708] =
        MemberPattern.of(
            false,
            new String[] {"getPropagators"},
            null,
            0,
            0,
            3,
            "(*)Lio/opentelemetry/context/propagation/ContextPropagators;");
    result[709] =
        MemberPattern.of(
            false,
            new String[] {"getLogsBridge"},
            null,
            0,
            0,
            1,
            "()Lio/opentelemetry/api/logs/LoggerProvider;");
    result[710] =
        MemberPattern.of(
            false,
            new String[] {"getTracerProvider"},
            null,
            0,
            0,
            1,
            "()Lio/opentelemetry/api/trace/TracerProvider;");
    result[711] =
        MemberPattern.of(
            false,
            new String[] {"getPropagators"},
            null,
            0,
            0,
            1,
            "()Lio/opentelemetry/context/propagation/ContextPropagators;");
    result[712] =
        MemberPattern.of(
            false, new String[] {"root"}, null, 0, 0, 1, "()Lio/opentelemetry/context/Context;");
    result[713] =
        MemberPattern.of(
            false, new String[] {"current"}, null, 0, 0, 1, "()Lio/opentelemetry/context/Context;");
    result[714] =
        MemberPattern.of(
            false,
            new String[] {"getMeterProvider"},
            null,
            0,
            0,
            1,
            "()Lio/opentelemetry/api/metrics/MeterProvider;");
    result[715] =
        MemberPattern.of(
            false, null, "io/opentelemetry/instrumentation/annotations/WithSpan", 0, 0, 0, null);
    result[716] =
        MemberPattern.of(
            false,
            new String[] {"<init>", "<clinit>"},
            "io/opentelemetry/instrumentation/annotations/WithSpan",
            0,
            0,
            0,
            null);
    result[717] =
        MemberPattern.of(
            false,
            null,
            "io/opentelemetry/instrumentation/annotations/AddingSpanAttributes",
            0,
            0,
            0,
            null);
    result[718] =
        MemberPattern.of(
            false,
            new String[] {"<init>", "<clinit>"},
            "io/opentelemetry/instrumentation/annotations/AddingSpanAttributes",
            0,
            0,
            0,
            null);
    result[719] =
        MemberPattern.of(false, new String[] {"opt"}, null, 1, 0, 3, "(*)Ljava/lang/Object;");
    result[720] =
        MemberPattern.of(
            false, new String[] {"toJSONObject"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[721] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Lorg/json/JSONTokener;Lorg/json/JSONParserConfiguration;)");
    result[722] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Ljava/util/Map;ILorg/json/JSONParserConfiguration;)");
    result[723] =
        MemberPattern.of(
            false, new String[] {"opt"}, null, 1, 0, 1, "(Ljava/lang/String;)Ljava/lang/Object;");
    result[724] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 2, "(Lorg/json/JSONTokener;)");
    result[725] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 2, "(Ljava/util/Map;)");
    result[726] =
        MemberPattern.of(
            false, new String[] {"opt"}, null, 1, 0, 1, "(Ljava/lang/String;)Ljava/lang/Object;");
    result[727] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 2, "(Ljava/io/Reader;)");
    result[728] =
        MemberPattern.of(
            false, new String[] {"getResource"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[729] =
        MemberPattern.of(
            false, new String[] {"getResourceAsStream"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[730] =
        MemberPattern.of(false, new String[] {"loadClass"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[731] =
        MemberPattern.of(false, new String[] {"loadClass"}, null, 0, 0, 2, "(Ljava/lang/String;Z)");
    result[732] = MemberPattern.of(false, new String[] {"invoke"}, null, 0, 0, 0, null);
    result[733] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 5, "(Ljava/lang/Runnable;*)");
    result[734] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[735] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[736] =
        MemberPattern.of(
            false,
            new String[] {"sendMessage"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/pekko/dispatch/Envelope;*)");
    result[737] =
        MemberPattern.of(
            false,
            new String[] {"*schedule"},
            null,
            0,
            0,
            5,
            "(Lscala/concurrent/ExecutionContext;Ljava/lang/Runnable;Lscala/concurrent/duration/FiniteDuration;*)");
    result[738] =
        MemberPattern.of(
            false,
            new String[] {"bindAndHandleAsync"},
            null,
            0,
            0,
            4,
            "(Lscala/Function1;??????Lorg/apache/pekko/stream/Materializer;)");
    result[739] =
        MemberPattern.of(
            false,
            new String[] {"bindAndHandleAsync"},
            null,
            0,
            0,
            4,
            "(Lscala/Function1;?????Lorg/apache/pekko/stream/Materializer;)");
    result[740] =
        MemberPattern.of(
            false,
            new String[] {"bindAndHandleAsync"},
            null,
            0,
            0,
            4,
            "(Lscala/Function1;????Lorg/apache/pekko/stream/Materializer;)");
    result[741] =
        MemberPattern.of(
            false,
            new String[] {"bindAndHandle"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/pekko/stream/scaladsl/Flow;*)");
    result[742] =
        MemberPattern.of(
            false,
            new String[] {
              "org$apache$pekko$http$impl$engine$client$PoolMasterActor$$startPoolInterface"
            },
            null,
            0,
            0,
            0,
            null);
    result[743] =
        MemberPattern.of(
            false, new String[] {"cookies"}, null, 0, 8, 1, "()Lscala/collection/immutable/Seq;");
    result[744] =
        MemberPattern.of(
            false,
            new String[] {"fields"},
            null,
            0,
            8,
            1,
            "()Lorg/apache/pekko/http/scaladsl/model/Uri$Query;");
    result[745] =
        MemberPattern.of(
            false,
            new String[] {"formField"},
            null,
            8,
            0,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives;Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[746] =
        MemberPattern.of(
            false,
            new String[] {"formFields"},
            null,
            8,
            0,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives;Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[747] =
        MemberPattern.of(
            false,
            new String[] {"formField"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[748] =
        MemberPattern.of(
            false,
            new String[] {"formField"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Lorg/apache/pekko/http/scaladsl/server/Directive;");
    result[749] =
        MemberPattern.of(
            false,
            new String[] {"formFields"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Ljava/lang/Object;");
    result[750] =
        MemberPattern.of(
            false,
            new String[] {"formFields"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/FormFieldDirectives$FieldMagnet;)Lorg/apache/pekko/http/scaladsl/server/Directive;");
    result[751] =
        MemberPattern.of(false, new String[] {"value"}, null, 0, 0, 1, "()Ljava/lang/String;");
    result[752] =
        MemberPattern.of(
            false, new String[] {"headers"}, null, 0, 8, 1, "()Lscala/collection/immutable/Seq;");
    result[753] = MemberPattern.of(false, new String[] {"entity"}, null, 1, 8, 2, "()");
    result[754] =
        MemberPattern.of(
            false,
            new String[] {"entity"},
            null,
            8,
            0,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/MarshallingDirectives;Lorg/apache/pekko/http/scaladsl/unmarshalling/Unmarshaller;)Lorg/apache/pekko/http/scaladsl/server/Directive;");
    result[755] =
        MemberPattern.of(
            false,
            new String[] {"filter"},
            null,
            0,
            8,
            1,
            "(Ljava/lang/String;Lorg/apache/pekko/http/scaladsl/unmarshalling/Unmarshaller;)Lorg/apache/pekko/http/scaladsl/server/Directive;");
    result[756] =
        MemberPattern.of(
            false,
            new String[] {"repeatedFilter"},
            null,
            0,
            8,
            1,
            "(Ljava/lang/String;Lorg/apache/pekko/http/scaladsl/unmarshalling/Unmarshaller;)Lorg/apache/pekko/http/scaladsl/server/Directive;");
    result[757] =
        MemberPattern.of(
            false,
            new String[] {"parameter"},
            null,
            8,
            0,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives;Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[758] =
        MemberPattern.of(
            false,
            new String[] {"parameters"},
            null,
            8,
            0,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives;Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[759] =
        MemberPattern.of(
            false,
            new String[] {"parameter"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[760] =
        MemberPattern.of(
            false,
            new String[] {"parameter"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Lorg/apache/pekko/http/scaladsl/server/Directive;");
    result[761] =
        MemberPattern.of(
            false,
            new String[] {"parameters"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Ljava/lang/Object;");
    result[762] =
        MemberPattern.of(
            false,
            new String[] {"parameters"},
            null,
            0,
            8,
            1,
            "(Lorg/apache/pekko/http/scaladsl/server/directives/ParameterDirectives$ParamMagnet;)Lorg/apache/pekko/http/scaladsl/server/Directive;");
    result[763] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Lorg/apache/pekko/http/scaladsl/model/Uri$Path;Ljava/lang/Object;Lorg/apache/pekko/http/scaladsl/server/util/Tuple;)");
    result[764] =
        MemberPattern.of(
            false,
            new String[] {"request"},
            null,
            0,
            8,
            1,
            "()Lorg/apache/pekko/http/scaladsl/model/HttpRequest;");
    result[765] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            8,
            1,
            "(Ljava/lang/Object;Lscala/concurrent/ExecutionContext;Lorg/apache/pekko/stream/Materializer;)Lscala/concurrent/Future;");
    result[766] =
        MemberPattern.of(
            false,
            new String[] {"queryString"},
            null,
            0,
            8,
            1,
            "(Ljava/nio/charset/Charset;)Lscala/Option;");
    result[767] =
        MemberPattern.of(false, new String[] {"rawQueryString"}, null, 0, 8, 1, "()Lscala/Option;");
    result[768] =
        MemberPattern.of(
            false,
            new String[] {"query"},
            null,
            0,
            8,
            1,
            "(Ljava/nio/charset/Charset;Lorg/apache/pekko/http/scaladsl/model/Uri$ParsingMode;)Lorg/apache/pekko/http/scaladsl/model/Uri$Query;");
    result[769] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            6,
            "(Lplay/api/mvc/Request;*)Lscala/concurrent/Future;");
    result[770] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            6,
            "(Lplay/api/mvc/Request;*)Lscala/concurrent/Future;");
    result[771] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            6,
            "(Lplay/api/mvc/Request;*)Lscala/concurrent/Future;");
    result[772] =
        MemberPattern.of(
            false,
            new String[] {"forwardedConnection"},
            null,
            0,
            8,
            1,
            "(Lplay/api/mvc/request/RemoteConnection;Lplay/api/mvc/Headers;)Lplay/api/mvc/request/RemoteConnection;");
    result[773] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;)Lplay/libs/streams/Accumulator;");
    result[774] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;Lakka/util/ByteString;)Ljava/util/Map;");
    result[775] =
        MemberPattern.of(
            false,
            new String[] {"onServerError"},
            null,
            1,
            0,
            1,
            "(Lplay/api/mvc/RequestHeader;Ljava/lang/Throwable;)Lscala/concurrent/Future;");
    result[776] =
        MemberPattern.of(
            false, new String[] {"apply"}, null, 0, 8, 1, "(Ljava/lang/String;)Lscala/Option;");
    result[777] =
        MemberPattern.of(
            false, new String[] {"tolerantText"}, null, 0, 8, 1, "(J)Lplay/api/mvc/BodyParser;");
    result[778] =
        MemberPattern.of(
            false, new String[] {"tolerantJson"}, null, 0, 8, 1, "(I)Lplay/api/mvc/BodyParser;");
    result[779] =
        MemberPattern.of(
            false,
            new String[] {"tolerantFormUrlEncoded"},
            null,
            0,
            8,
            1,
            "(I)Lplay/api/mvc/BodyParser;");
    result[780] =
        MemberPattern.of(
            false,
            new String[] {"multipartFormData"},
            null,
            0,
            8,
            1,
            "(Lscala/Function1;J)Lplay/api/mvc/BodyParser;");
    result[781] = MemberPattern.of(false, new String[] {"apply"}, null, 0, 0, 0, null);
    result[782] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(???Ljava/lang/Object;Ljava/lang/reflect/Method;)");
    result[783] =
        MemberPattern.of(
            false,
            new String[] {"extract", "play$api$routing$sird$PathExtractor$$extract"},
            null,
            0,
            0,
            1,
            "(Ljava/lang/String;)Lscala/Option;");
    result[784] =
        MemberPattern.of(
            false,
            new String[] {"sendJson"},
            null,
            0,
            0,
            5,
            "(Lcom/fasterxml/jackson/databind/JsonNode;*)");
    result[785] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;Lakka/util/ByteString;)Lcom/fasterxml/jackson/databind/JsonNode;");
    result[786] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;Lakka/util/ByteString;)Ljava/lang/String;");
    result[787] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;)Lplay/libs/streams/Accumulator;");
    result[788] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;Lakka/util/ByteString;)Ljava/util/Map;");
    result[789] =
        MemberPattern.of(
            false,
            new String[] {"onServerError"},
            null,
            1,
            0,
            1,
            "(Lplay/api/mvc/RequestHeader;Ljava/lang/Throwable;)Lscala/concurrent/Future;");
    result[790] =
        MemberPattern.of(
            false, new String[] {"apply"}, null, 0, 8, 1, "(Ljava/lang/String;)Lscala/Option;");
    result[791] = MemberPattern.of(false, new String[] {"apply"}, null, 0, 0, 0, null);
    result[792] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(???Ljava/lang/Object;Ljava/lang/reflect/Method;)");
    result[793] =
        MemberPattern.of(
            false, new String[] {"extract"}, null, 0, 0, 1, "(Ljava/lang/String;)Lscala/Option;");
    result[794] =
        MemberPattern.of(
            false,
            new String[] {"sendJson"},
            null,
            0,
            0,
            5,
            "(Lcom/fasterxml/jackson/databind/JsonNode;*)");
    result[795] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;Lakka/util/ByteString;)Lcom/fasterxml/jackson/databind/JsonNode;");
    result[796] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;Lakka/util/ByteString;)Ljava/lang/String;");
    result[797] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            1,
            "(Lplay/mvc/Http$RequestHeader;Lakka/util/ByteString;)Lorg/w3c/dom/Document;");
    result[798] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(???Ljava/lang/Object;Ljava/lang/reflect/Method;)");
    result[799] =
        MemberPattern.of(
            false, new String[] {"execute"}, null, 1, 0, 5, "(Lorg/quartz/JobExecutionContext;*)");
  }

  private static void fill800(MemberPattern[] result) {
    result[800] =
        MemberPattern.of(
            false, new String[] {"createStatement"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[801] =
        MemberPattern.of(
            false,
            new String[] {"find"},
            null,
            8,
            0,
            2,
            "(Lio/r2dbc/spi/ConnectionFactoryOptions;)");
    result[802] =
        MemberPattern.of(false, new String[] {"basicGet"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[803] =
        MemberPattern.of(
            false,
            new String[] {"basicConsume"},
            null,
            0,
            0,
            4,
            "(Ljava/lang/String;?????Lcom/rabbitmq/client/Consumer;*)");
    result[804] =
        MemberPattern.of(
            false,
            new String[] {"parse"},
            null,
            0,
            0,
            2,
            "(Lratpack/http/TypedData;Lratpack/parse/Parse;)");
    result[805] =
        MemberPattern.of(false, new String[] {"resume"}, null, 0, 0, 5, "(Lratpack/func/Block;*)");
    result[806] =
        MemberPattern.of(
            false,
            new String[] {"delimit*"},
            null,
            0,
            0,
            5,
            "(Lratpack/func/Action;Lratpack/func/Action;*)");
    result[807] =
        MemberPattern.of(
            false,
            new String[] {"render"},
            null,
            0,
            0,
            2,
            "(Lratpack/handling/Context;Lratpack/jackson/JsonRender;)");
    result[808] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            2,
            "(Lratpack/path/PathBinder;Lratpack/handling/Handler;)");
    result[809] = MemberPattern.of(false, new String[] {"readStream"}, null, 0, 0, 2, "()");
    result[810] = MemberPattern.of(false, new String[] {"getBuffer"}, null, 0, 0, 2, "()");
    result[811] = MemberPattern.of(false, new String[] {"getBytes"}, null, 0, 0, 2, "()");
    result[812] =
        MemberPattern.of(
            false, new String[] {"writeTo"}, null, 0, 0, 2, "(Ljava/io/OutputStream;)");
    result[813] = MemberPattern.of(false, new String[] {"getInputStream"}, null, 0, 0, 2, "()");
    result[814] = MemberPattern.of(false, new String[] {"getText"}, null, 0, 0, 2, "()");
    result[815] =
        MemberPattern.of(
            false, new String[] {"getText"}, null, 0, 0, 2, "(Ljava/nio/charset/Charset;)");
    result[816] =
        MemberPattern.of(
            false,
            new String[] {"error"},
            null,
            0,
            0,
            5,
            "(Lratpack/handling/Context;Ljava/lang/Throwable;*)");
    result[817] = MemberPattern.of(false, new String[] {"buildBaseRegistry"}, null, 8, 0, 0, null);
    result[818] = MemberPattern.of(false, new String[] {"subscribe"}, null, 0, 0, 0, null);
    result[819] =
        MemberPattern.of(
            false, new String[] {"onNext", "onError", "onComplete"}, null, 0, 0, 0, null);
    result[820] = MemberPattern.of(false, new String[] {"onNext", "onError"}, null, 0, 0, 0, null);
    result[821] = MemberPattern.of(false, new String[] {"onComplete"}, null, 0, 0, 0, null);
    result[822] = MemberPattern.of(false, new String[] {"block*"}, null, 0, 0, 0, null);
    result[823] = MemberPattern.of(false, new String[] {"block*"}, null, 0, 0, 0, null);
    result[824] =
        MemberPattern.of(
            false, new String[] {"onNext", "onComplete", "onError"}, null, 0, 0, 0, null);
    result[825] = MemberPattern.of(false, new String[] {"subscribe"}, null, 0, 0, 0, null);
    result[826] =
        MemberPattern.of(
            false, new String[] {"subscribe"}, null, 0, 8, 2, "(Lreactor/core/CoreSubscriber;)");
    result[827] =
        MemberPattern.of(false, new String[] {"create", "newConnection"}, null, 8, 0, 0, null);
    result[828] = MemberPattern.of(false, new String[] {"onConnect"}, null, 0, 0, 0, null);
    result[829] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            6,
            "(Lredis/RedisCommand;*)Lscala/concurrent/Future;");
    result[830] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lorg/redisson/client/protocol/CommandData;*)");
    result[831] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lorg/redisson/client/protocol/CommandsData;*)");
    result[832] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lorg/redisson/client/protocol/CommandData;*)");
    result[833] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lorg/redisson/client/protocol/CommandsData;*)");
    result[834] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lorg/redisson/client/protocol/CommandData;*)");
    result[835] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lorg/redisson/client/protocol/CommandsData;*)");
    result[836] = MemberPattern.of(false, new String[] {"executeOperation"}, null, 0, 0, 5, "(I*)");
    result[837] =
        MemberPattern.of(
            false,
            new String[] {"decorateCheckedSupplier"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Lio/github/resilience4j/core/functions/CheckedSupplier;");
    result[838] =
        MemberPattern.of(
            false,
            new String[] {"decorateCheckedFunction"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Lio/github/resilience4j/core/functions/CheckedFunction;");
    result[839] =
        MemberPattern.of(
            false,
            new String[] {"decorateCheckedConsumer"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Lio/github/resilience4j/core/functions/CheckedConsumer;");
    result[840] =
        MemberPattern.of(
            false,
            new String[] {"decorateCompletionStage"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Ljava/util/function/Supplier;");
    result[841] =
        MemberPattern.of(
            false,
            new String[] {"decorateFuture"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Ljava/util/function/Supplier;");
    result[842] =
        MemberPattern.of(
            false,
            new String[] {"decorateConsumer"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Ljava/util/function/Consumer;");
    result[843] =
        MemberPattern.of(
            false,
            new String[] {"decorateCheckedRunnable"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Lio/github/resilience4j/core/functions/CheckedRunnable;");
    result[844] =
        MemberPattern.of(
            false,
            new String[] {"decorateCallable"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Ljava/util/concurrent/Callable;");
    result[845] =
        MemberPattern.of(
            false,
            new String[] {"decorateRunnable"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Ljava/lang/Runnable;");
    result[846] =
        MemberPattern.of(
            false,
            new String[] {"decorateSupplier"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Ljava/util/function/Supplier;");
    result[847] =
        MemberPattern.of(
            false,
            new String[] {"decorateFunction"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/circuitbreaker/CircuitBreaker;*)Ljava/util/function/Function;");
    result[848] = MemberPattern.of(false, new String[] {"withFallback"}, null, 0, 0, 0, null);
    result[849] = MemberPattern.of(false, new String[] {"withFallback"}, null, 0, 0, 0, null);
    result[850] = MemberPattern.of(false, new String[] {"withFallback"}, null, 0, 0, 0, null);
    result[851] = MemberPattern.of(false, new String[] {"withFallback"}, null, 0, 0, 0, null);
    result[852] =
        MemberPattern.of(
            false,
            new String[] {"decorateCompletionStage"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Ljava/util/function/Supplier;");
    result[853] =
        MemberPattern.of(
            false,
            new String[] {"decorateCheckedSupplier"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Lio/github/resilience4j/core/functions/CheckedSupplier;");
    result[854] =
        MemberPattern.of(
            false,
            new String[] {"decorateCheckedRunnable"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Lio/github/resilience4j/core/functions/CheckedRunnable;");
    result[855] =
        MemberPattern.of(
            false,
            new String[] {"decorateCallable"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Ljava/util/concurrent/Callable;");
    result[856] =
        MemberPattern.of(
            false,
            new String[] {"decorateSupplier"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Ljava/util/function/Supplier;");
    result[857] =
        MemberPattern.of(
            false,
            new String[] {"decorateFunction"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Ljava/util/function/Function;");
    result[858] =
        MemberPattern.of(
            false,
            new String[] {"decorateCheckedFunction"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Lio/github/resilience4j/core/functions/CheckedFunction;");
    result[859] =
        MemberPattern.of(
            false,
            new String[] {"decorateRunnable"},
            null,
            8,
            0,
            6,
            "(Lio/github/resilience4j/retry/Retry;*)Ljava/lang/Runnable;");
    result[860] =
        MemberPattern.of(
            false, new String[] {"apply"}, null, 0, 0, 5, "(Lorg/reactivestreams/Publisher;*)");
    result[861] =
        MemberPattern.of(
            false,
            new String[] {"decorate"},
            null,
            0,
            0,
            6,
            "(Ljava/util/function/UnaryOperator;*)Ljava/util/function/Function;");
    result[862] =
        MemberPattern.of(
            false, new String[] {"apply"}, null, 0, 0, 5, "(Lorg/reactivestreams/Publisher;*)");
    result[863] = MemberPattern.of(false, new String[] {"invoke"}, null, 1, 0, 0, null);
    result[864] =
        MemberPattern.of(
            false, new String[] {"submit"}, null, 1, 0, 3, "(*)Ljava/util/concurrent/Future;");
    result[865] = MemberPattern.of(false, new String[] {"inject"}, null, 1, 0, 4, "(??)");
    result[866] =
        MemberPattern.of(false, new String[] {"getDecodedFormParameters"}, null, 0, 0, 2, "()");
    result[867] = MemberPattern.of(false, new String[] {"inject"}, null, 1, 0, 4, "(??)");
    result[868] = MemberPattern.of(false, new String[] {"inject"}, null, 1, 0, 4, "(??)");
    result[869] =
        MemberPattern.of(
            false,
            new String[] {"header"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Object;)");
    result[870] =
        MemberPattern.of(false, new String[] {"location"}, null, 1, 0, 2, "(Ljava/net/URI;)");
    result[871] =
        MemberPattern.of(
            false,
            new String[] {"populatePathParams"},
            null,
            0,
            0,
            2,
            "(Lorg/jboss/resteasy/spi/HttpRequest;Ljava/util/regex/Matcher;Ljava/lang/String;)");
    result[872] =
        MemberPattern.of(
            false,
            new String[] {"readFrom"},
            null,
            0,
            0,
            4,
            "(??????)Lorg/jboss/resteasy/plugins/providers/multipart/MultipartFormDataInput;");
    result[873] = MemberPattern.of(false, new String[] {"inject"}, null, 1, 0, 4, "(??)");
    result[874] = MemberPattern.of(false, new String[] {"inject"}, null, 1, 0, 4, "(??)");
    result[875] =
        MemberPattern.of(
            false,
            new String[] {"beforeHandle"},
            null,
            0,
            0,
            5,
            "(Lorg/restlet/Request;Lorg/restlet/Response;*)");
    result[876] =
        MemberPattern.of(false, new String[] {"setUpApplicationState"}, null, 0, 0, 0, null);
    result[877] =
        MemberPattern.of(
            false, new String[] {"abortWith"}, null, 0, 0, 2, "(Ljakarta/ws/rs/core/Response;)");
    result[878] =
        MemberPattern.of(
            false,
            new String[] {"filter"},
            null,
            0,
            0,
            2,
            "(Ljakarta/ws/rs/container/ContainerRequestContext;)");
    result[879] = MemberPattern.of(false, null, "jakarta/ws/rs/Path", 0, 0, 0, null);
    result[880] =
        MemberPattern.of(
            false, new String[] {"<init>", "<clinit>"}, "jakarta/ws/rs/Path", 0, 0, 0, null);
    result[881] =
        MemberPattern.of(false, new String[] {"resume"}, null, 1, 0, 5, "(Ljava/lang/Object;*)");
    result[882] =
        MemberPattern.of(false, new String[] {"resume"}, null, 1, 0, 5, "(Ljava/lang/Throwable;*)");
    result[883] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[884] = MemberPattern.of(false, new String[] {"writeTo"}, null, 0, 0, 4, "(???????)");
    result[885] = MemberPattern.of(false, null, "javax/ws/rs/Path", 0, 0, 0, null);
    result[886] =
        MemberPattern.of(
            false, new String[] {"<init>", "<clinit>"}, "javax/ws/rs/Path", 0, 0, 0, null);
    result[887] =
        MemberPattern.of(
            false, new String[] {"abortWith"}, null, 0, 0, 2, "(Ljavax/ws/rs/core/Response;)");
    result[888] =
        MemberPattern.of(
            false,
            new String[] {"filter"},
            null,
            0,
            0,
            2,
            "(Ljavax/ws/rs/container/ContainerRequestContext;)");
    result[889] = MemberPattern.of(false, null, "javax/ws/rs/Path", 0, 0, 0, null);
    result[890] =
        MemberPattern.of(
            false, new String[] {"<init>", "<clinit>"}, "javax/ws/rs/Path", 0, 0, 0, null);
    result[891] =
        MemberPattern.of(false, new String[] {"resume"}, null, 1, 0, 5, "(Ljava/lang/Object;*)");
    result[892] =
        MemberPattern.of(false, new String[] {"resume"}, null, 1, 0, 5, "(Ljava/lang/Throwable;*)");
    result[893] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[894] = MemberPattern.of(false, new String[] {"writeTo"}, null, 0, 0, 4, "(???????)");
    result[895] =
        MemberPattern.of(
            false,
            new String[] {"subscribe"},
            null,
            0,
            0,
            2,
            "(Lio/reactivex/CompletableObserver;)");
    result[896] =
        MemberPattern.of(
            false, new String[] {"subscribe"}, null, 0, 0, 2, "(Lorg/reactivestreams/Subscriber;)");
    result[897] =
        MemberPattern.of(
            false, new String[] {"subscribe"}, null, 0, 0, 2, "(Lio/reactivex/MaybeObserver;)");
    result[898] =
        MemberPattern.of(
            false, new String[] {"subscribe"}, null, 0, 0, 2, "(Lio/reactivex/Observer;)");
    result[899] =
        MemberPattern.of(
            false, new String[] {"subscribe"}, null, 0, 0, 2, "(Lio/reactivex/SingleObserver;)");
    result[900] =
        MemberPattern.of(
            false,
            new String[] {"subscribe"},
            null,
            0,
            0,
            2,
            "(Lio/reactivex/rxjava3/core/CompletableObserver;)");
    result[901] =
        MemberPattern.of(
            false,
            new String[] {"subscribe"},
            null,
            0,
            0,
            2,
            "(Lio/reactivex/rxjava3/core/FlowableSubscriber;)");
    result[902] =
        MemberPattern.of(
            false,
            new String[] {"subscribe"},
            null,
            0,
            0,
            2,
            "(Lio/reactivex/rxjava3/core/MaybeObserver;)");
    result[903] =
        MemberPattern.of(
            false,
            new String[] {"subscribe"},
            null,
            0,
            0,
            2,
            "(Lio/reactivex/rxjava3/core/Observer;)");
    result[904] =
        MemberPattern.of(
            false,
            new String[] {"subscribe"},
            null,
            0,
            0,
            2,
            "(Lio/reactivex/rxjava3/core/SingleObserver;)");
    result[905] =
        MemberPattern.of(
            false,
            new String[] {"doSubmit", "externalPush"},
            null,
            0,
            0,
            5,
            "(Lscala/concurrent/forkjoin/ForkJoinTask;*)");
    result[906] =
        MemberPattern.of(false, new String[] {"exec", "fork", "cancel"}, null, 0, 0, 0, null);
    result[907] = MemberPattern.of(false, new String[] {"doExec", "exec"}, null, 0, 0, 0, null);
    result[908] = MemberPattern.of(false, new String[] {"fork"}, null, 0, 0, 0, null);
    result[909] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[910] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[911] = MemberPattern.of(false, new String[] {"executeWithValue"}, null, 0, 0, 0, null);
    result[912] =
        MemberPattern.of(
            false, new String[] {"scala$concurrent$impl$Promise$$resolveTry"}, null, 0, 0, 0, null);
    result[913] = MemberPattern.of(false, new String[] {"tryComplete0"}, null, 0, 0, 0, null);
    result[914] =
        MemberPattern.of(
            false, new String[] {"scala$concurrent$impl$Promise$$resolve"}, null, 0, 0, 0, null);
    result[915] = MemberPattern.of(false, new String[] {"submitWithValue"}, null, 0, 0, 0, null);
    result[916] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 0, null);
    result[917] = MemberPattern.of(false, new String[] {"cancel"}, null, 0, 0, 0, null);
    result[918] =
        MemberPattern.of(false, new String[] {"runner"}, null, 0, 0, 4, "(?[Ljava/lang/String;*)");
    result[919] =
        MemberPattern.of(
            false, new String[] {"apply"}, null, 0, 0, 2, "(Lorg/scalatest/events/Event;)");
    result[920] = MemberPattern.of(false, new String[] {"fireReadyEvents"}, null, 0, 0, 0, null);
    result[921] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lorg/scalatest/Filter;??Lorg/scalatest/Tracker;*)");
    result[922] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Lscala/collection/immutable/Map;Ljava/lang/String;)");
    result[923] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            2,
            "(Lscala/collection/immutable/Set;Lscala/collection/immutable/Map;Ljava/lang/String;)");
    result[924] =
        MemberPattern.of(
            false,
            new String[] {"runTestImpl"},
            null,
            0,
            0,
            5,
            "(Lorg/scalatest/Suite;Ljava/lang/String;Lorg/scalatest/Args;*)");
    result[925] =
        MemberPattern.of(false, new String[] {"get"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[926] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 2, "()");
    result[927] = MemberPattern.of(false, new String[] {"quit"}, null, 0, 0, 2, "()");
    result[928] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            2,
            0,
            2,
            "(Lio/servicetalk/concurrent/api/CopyOnWriteContextMap$CopyContextMap;)");
    result[929] =
        MemberPattern.of(
            false,
            new String[] {
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
              "run"
            },
            null,
            0,
            0,
            0,
            null);
    result[930] = MemberPattern.of(false, new String[] {"loadProviders"}, null, 0, 0, 0, null);
    result[931] =
        MemberPattern.of(
            false,
            new String[] {"service"},
            null,
            1,
            0,
            2,
            "(Ljakarta/servlet/ServletRequest;Ljakarta/servlet/ServletResponse;)");
    result[932] =
        MemberPattern.of(
            false,
            new String[] {"getSession"},
            null,
            1,
            0,
            3,
            "(*)Ljakarta/servlet/http/HttpSession;");
    result[933] =
        MemberPattern.of(false, new String[] {"getHeader"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[934] =
        MemberPattern.of(false, new String[] {"getHeaders"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[935] = MemberPattern.of(false, new String[] {"getHeaderNames"}, null, 0, 0, 2, "()");
    result[936] =
        MemberPattern.of(
            false, new String[] {"getParameter"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[937] =
        MemberPattern.of(
            false, new String[] {"getParameterValues"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[938] = MemberPattern.of(false, new String[] {"getParameterMap"}, null, 0, 0, 2, "()");
    result[939] = MemberPattern.of(false, new String[] {"getParameterNames"}, null, 0, 0, 2, "()");
    result[940] = MemberPattern.of(false, new String[] {"getCookies"}, null, 0, 0, 2, "()");
    result[941] = MemberPattern.of(false, new String[] {"getQueryString"}, null, 0, 0, 2, "()");
    result[942] =
        MemberPattern.of(false, new String[] {"getInputStream", "getReader"}, null, 0, 0, 2, "()");
    result[943] =
        MemberPattern.of(
            false, new String[] {"getRequestDispatcher"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[944] =
        MemberPattern.of(
            false, new String[] {"addCookie"}, null, 0, 0, 2, "(Ljakarta/servlet/http/Cookie;)");
    result[945] =
        MemberPattern.of(
            false,
            new String[] {"setHeader", "addHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/String;)");
    result[946] =
        MemberPattern.of(
            false,
            new String[] {"encodeRedirectURL", "encodeURL"},
            null,
            0,
            0,
            6,
            "(Ljava/lang/String;*)Ljava/lang/String;");
    result[947] =
        MemberPattern.of(
            false, new String[] {"sendRedirect"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[948] =
        MemberPattern.of(
            false,
            new String[] {"setAttribute", "putValue"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Object;)");
    result[949] = MemberPattern.of(false, new String[] {"getName"}, null, 1, 0, 2, "()");
    result[950] =
        MemberPattern.of(false, new String[] {"getHeader"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[951] =
        MemberPattern.of(false, new String[] {"getHeaders"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[952] = MemberPattern.of(false, new String[] {"getHeaderNames"}, null, 1, 0, 2, "()");
    result[953] = MemberPattern.of(false, new String[] {"getInputStream"}, null, 1, 0, 2, "()");
    result[954] =
        MemberPattern.of(
            false,
            new String[] {"service"},
            null,
            1,
            0,
            2,
            "(Ljakarta/servlet/ServletRequest;Ljakarta/servlet/ServletResponse;)");
    result[955] =
        MemberPattern.of(false, new String[] {"complete", "dispatch"}, null, 0, 0, 0, null);
    result[956] =
        MemberPattern.of(
            false,
            new String[] {"getInputStream"},
            null,
            1,
            0,
            1,
            "()Ljakarta/servlet/ServletInputStream;");
    result[957] =
        MemberPattern.of(
            false, new String[] {"getReader"}, null, 1, 0, 1, "()Ljava/io/BufferedReader;");
    result[958] =
        MemberPattern.of(
            false,
            new String[] {"doFilter", "service"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;*)");
    result[959] =
        MemberPattern.of(
            false,
            new String[] {"doFilter", "service"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;*)");
    result[960] =
        MemberPattern.of(false, new String[] {"sendError", "setStatus"}, null, 0, 0, 5, "(I*)");
    result[961] = MemberPattern.of(false, new String[] {"sendRedirect"}, null, 0, 0, 0, null);
    result[962] =
        MemberPattern.of(
            false,
            new String[] {"getInputStream"},
            null,
            1,
            0,
            1,
            "()Ljavax/servlet/ServletInputStream;");
    result[963] =
        MemberPattern.of(
            false, new String[] {"getReader"}, null, 1, 0, 1, "()Ljava/io/BufferedReader;");
    result[964] = MemberPattern.of(false, new String[] {"dispatch"}, null, 1, 0, 0, null);
    result[965] =
        MemberPattern.of(
            false,
            new String[] {"getSession"},
            null,
            1,
            0,
            3,
            "(*)Ljavax/servlet/http/HttpSession;");
    result[966] =
        MemberPattern.of(
            false,
            new String[] {"doFilter", "service"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;*)");
    result[967] = MemberPattern.of(false, new String[] {"getName"}, null, 1, 0, 2, "()");
    result[968] =
        MemberPattern.of(false, new String[] {"getHeader"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[969] =
        MemberPattern.of(false, new String[] {"getHeaders"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[970] = MemberPattern.of(false, new String[] {"getHeaderNames"}, null, 1, 0, 2, "()");
    result[971] = MemberPattern.of(false, new String[] {"getInputStream"}, null, 1, 0, 2, "()");
    result[972] =
        MemberPattern.of(false, new String[] {"complete", "dispatch"}, null, 0, 0, 0, null);
    result[973] =
        MemberPattern.of(
            false,
            new String[] {"getInputStream"},
            null,
            1,
            0,
            1,
            "()Ljavax/servlet/ServletInputStream;");
    result[974] =
        MemberPattern.of(
            false, new String[] {"getReader"}, null, 1, 0, 1, "()Ljava/io/BufferedReader;");
    result[975] =
        MemberPattern.of(
            false,
            new String[] {"doFilter", "service"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;*)");
    result[976] =
        MemberPattern.of(
            false,
            new String[] {"forward", "include", "error"},
            null,
            1,
            0,
            2,
            "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;)");
    result[977] =
        MemberPattern.of(
            false, null, null, 1, 0, 6, "(Ljava/lang/String;*)Ljavax/servlet/RequestDispatcher;");
    result[978] =
        MemberPattern.of(
            false,
            new String[] {"<init>", "<clinit>"},
            null,
            1,
            0,
            6,
            "(Ljava/lang/String;*)Ljavax/servlet/RequestDispatcher;");
    result[979] =
        MemberPattern.of(
            false,
            new String[] {"doFilter"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/ServletRequest;Ljavax/servlet/ServletResponse;*)");
    result[980] = MemberPattern.of(false, new String[] {"getName"}, null, 0, 0, 2, "()");
    result[981] = MemberPattern.of(false, new String[] {"getValue"}, null, 0, 0, 2, "()");
    result[982] =
        MemberPattern.of(
            false,
            new String[] {"service"},
            null,
            4,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;*)");
    result[983] =
        MemberPattern.of(
            false,
            new String[] {"service"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;*)");
    result[984] =
        MemberPattern.of(
            false,
            new String[] {"do*"},
            null,
            4,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;*)");
    result[985] =
        MemberPattern.of(
            false,
            new String[] {"do*"},
            null,
            1,
            0,
            5,
            "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;*)");
    result[986] =
        MemberPattern.of(false, new String[] {"getHeader"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[987] =
        MemberPattern.of(false, new String[] {"getHeaders"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[988] = MemberPattern.of(false, new String[] {"getHeaderNames"}, null, 0, 0, 2, "()");
    result[989] =
        MemberPattern.of(
            false, new String[] {"getParameter"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[990] =
        MemberPattern.of(
            false, new String[] {"getParameterValues"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[991] = MemberPattern.of(false, new String[] {"getParameterMap"}, null, 0, 0, 2, "()");
    result[992] = MemberPattern.of(false, new String[] {"getParameterNames"}, null, 0, 0, 2, "()");
    result[993] = MemberPattern.of(false, new String[] {"getCookies"}, null, 0, 0, 2, "()");
    result[994] = MemberPattern.of(false, new String[] {"getQueryString"}, null, 0, 0, 2, "()");
    result[995] =
        MemberPattern.of(false, new String[] {"getInputStream", "getReader"}, null, 0, 0, 2, "()");
    result[996] =
        MemberPattern.of(
            false, new String[] {"getRequestDispatcher"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[997] =
        MemberPattern.of(false, new String[] {"sendError", "sendRedirect"}, null, 0, 0, 0, null);
    result[998] =
        MemberPattern.of(
            false, new String[] {"addCookie"}, null, 0, 0, 5, "(Ljavax/servlet/http/Cookie;*)");
    result[999] =
        MemberPattern.of(
            false,
            new String[] {"setHeader", "addHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/String;)");
  }

  private static void fill1000(MemberPattern[] result) {
    result[1000] =
        MemberPattern.of(
            false,
            new String[] {"encodeRedirectURL", "encodeURL"},
            null,
            0,
            0,
            6,
            "(Ljava/lang/String;*)Ljava/lang/String;");
    result[1001] =
        MemberPattern.of(
            false, new String[] {"sendRedirect"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[1002] =
        MemberPattern.of(
            false,
            new String[] {"setAttribute", "putValue"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Object;)");
    result[1003] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 2, "()");
    result[1004] =
        MemberPattern.of(false, new String[] {"load"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[1005] =
        MemberPattern.of(false, new String[] {"load"}, null, 0, 0, 2, "(Ljava/io/InputStream;)");
    result[1006] =
        MemberPattern.of(false, new String[] {"load"}, null, 0, 0, 2, "(Ljava/io/Reader;)");
    result[1007] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            0,
            0,
            2,
            "(Lcom/alipay/sofa/rpc/core/request/SofaRequest;)");
    result[1008] =
        MemberPattern.of(
            false,
            new String[] {"handleRequest"},
            null,
            0,
            0,
            4,
            "(??Lcom/alipay/sofa/rpc/core/request/SofaRequest;)");
    result[1009] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 2, "()");
    result[1010] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            0,
            0,
            2,
            "(Lcom/alipay/sofa/rpc/core/request/SofaRequest;)");
    result[1011] = MemberPattern.of(false, new String[] {"channelRead0"}, null, 0, 0, 4, "(??)");
    result[1012] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            0,
            0,
            2,
            "(Lcom/alipay/sofa/rpc/core/request/SofaRequest;)");
    result[1013] =
        MemberPattern.of(
            false,
            new String[] {"find"},
            null,
            1,
            0,
            6,
            "(Lspark/route/HttpMethod;*)Lspark/routematch/RouteMatch;");
    result[1014] =
        MemberPattern.of(
            false,
            new String[] {"registerBeanDefinition"},
            null,
            0,
            0,
            4,
            "(?Lorg/springframework/beans/factory/config/BeanDefinition;*)");
    result[1015] =
        MemberPattern.of(
            false,
            new String[] {"resolveBeanClass"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/beans/factory/support/RootBeanDefinition;*)");
    result[1016] =
        MemberPattern.of(
            false,
            new String[] {"environmentPrepared"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/core/env/ConfigurableEnvironment;*)");
    result[1017] =
        MemberPattern.of(
            false,
            new String[] {"environmentPrepared"},
            null,
            0,
            0,
            4,
            "(?Lorg/springframework/core/env/ConfigurableEnvironment;*)");
    result[1018] = MemberPattern.of(false, new String[] {"onStartup"}, null, 0, 0, 4, "(?)");
    result[1019] =
        MemberPattern.of(
            false, new String[] {"isIncludedHeader"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[1020] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 2, "()");
    result[1021] =
        MemberPattern.of(
            false,
            new String[] {"copyToString"},
            null,
            0,
            0,
            2,
            "(Ljava/io/InputStream;Ljava/nio/charset/Charset;)");
    result[1022] =
        MemberPattern.of(
            false,
            new String[] {"receiveAndExecute"},
            null,
            0,
            0,
            4,
            "(??Ljavax/jms/MessageConsumer;*)");
    result[1023] = MemberPattern.of(false, new String[] {"doInvoke"}, null, 0, 0, 0, null);
    result[1024] =
        MemberPattern.of(
            false, new String[] {"executeListener"}, null, 0, 0, 4, "(?Ljava/lang/Object;*)");
    result[1025] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/amqp/rabbit/support/Delivery;*)");
    result[1026] =
        MemberPattern.of(
            false,
            new String[] {"invoke"},
            null,
            0,
            0,
            5,
            "(Lorg/aopalliance/intercept/MethodInvocation;*)");
    result[1027] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 5, "(Ljava/lang/Runnable;*)");
    result[1028] =
        MemberPattern.of(
            false,
            new String[] {"authenticate"},
            null,
            1,
            0,
            6,
            "(Lorg/springframework/security/core/Authentication;*)Lorg/springframework/security/core/Authentication;");
    result[1029] =
        MemberPattern.of(
            false,
            new String[] {"setContext"},
            null,
            1,
            0,
            2,
            "(Lorg/springframework/security/core/context/SecurityContext;)");
    result[1030] =
        MemberPattern.of(false, new String[] {"setDeferredContext"}, null, 1, 0, 4, "(?)");
    result[1031] =
        MemberPattern.of(
            false,
            new String[] {"createUser"},
            null,
            1,
            0,
            5,
            "(Lorg/springframework/security/core/userdetails/UserDetails;*)");
    result[1032] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 1, 0, 5, "(Ljava/lang/String;*)");
    result[1033] = MemberPattern.of(false, new String[] {"filter"}, null, 1, 0, 0, null);
    result[1034] = MemberPattern.of(false, new String[] {"filters"}, null, 1, 0, 0, null);
    result[1035] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            1,
            0,
            2,
            "(Lorg/springframework/web/server/ServerWebExchange;)");
    result[1036] =
        MemberPattern.of(
            false,
            new String[] {"handleResult"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/web/server/ServerWebExchange;*)");
    result[1037] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            1,
            0,
            2,
            "(Lorg/springframework/web/server/ServerWebExchange;Ljava/lang/Object;)");
    result[1038] =
        MemberPattern.of(
            false,
            new String[] {"route"},
            null,
            1,
            0,
            2,
            "(Lorg/springframework/web/reactive/function/server/ServerRequest;)");
    result[1039] = MemberPattern.of(false, new String[] {"getHeaders"}, null, 1, 0, 2, "()");
    result[1040] = MemberPattern.of(false, new String[] {"asInputStream"}, null, 0, 0, 2, "()");
    result[1041] =
        MemberPattern.of(false, new String[] {"get"}, null, 0, 0, 2, "(Ljava/lang/Object;)");
    result[1042] =
        MemberPattern.of(false, new String[] {"getFirst"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[1043] = MemberPattern.of(false, new String[] {"toSingleValueMap"}, null, 0, 0, 2, "()");
    result[1044] =
        MemberPattern.of(false, new String[] {"readOnlyHttpHeaders"}, null, 0, 0, 4, "(?)");
    result[1045] = MemberPattern.of(false, new String[] {"getHeaders"}, null, 1, 0, 2, "()");
    result[1046] =
        MemberPattern.of(
            false,
            new String[] {"apply"},
            null,
            0,
            0,
            1,
            "(Lorg/springframework/core/io/buffer/DataBuffer;)Lreactor/core/publisher/Flux;");
    result[1047] =
        MemberPattern.of(
            false,
            new String[] {"tokenize"},
            null,
            0,
            0,
            1,
            "(Lorg/springframework/core/io/buffer/DataBuffer;)Lreactor/core/publisher/Flux;");
    result[1048] = MemberPattern.of(false, new String[] {"initHeaders"}, null, 1, 0, 4, "(?)");
    result[1049] =
        MemberPattern.of(false, new String[] {"get"}, null, 0, 0, 2, "(Ljava/lang/Object;)");
    result[1050] =
        MemberPattern.of(
            false,
            new String[] {"resolveArgumentValue"},
            null,
            0,
            0,
            2,
            "(Lorg/springframework/core/MethodParameter;Lorg/springframework/web/reactive/BindingContext;Lorg/springframework/web/server/ServerWebExchange;)");
    result[1051] = MemberPattern.of(false, new String[] {"getQueryParams"}, null, 0, 0, 2, "()");
    result[1052] = MemberPattern.of(false, new String[] {"getCookies"}, null, 0, 0, 2, "()");
    result[1053] = MemberPattern.of(false, new String[] {"getBody"}, null, 0, 0, 2, "()");
    result[1054] = MemberPattern.of(false, new String[] {"getHeaders"}, null, 1, 0, 2, "()");
    result[1055] =
        MemberPattern.of(
            false,
            new String[] {"handleMatch"},
            null,
            4,
            0,
            2,
            "(Lorg/springframework/web/reactive/result/method/RequestMappingInfo;Lorg/springframework/web/method/HandlerMethod;Lorg/springframework/web/server/ServerWebExchange;)");
    result[1056] =
        MemberPattern.of(
            false,
            new String[] {"onRefresh"},
            null,
            4,
            0,
            2,
            "(Lorg/springframework/context/ApplicationContext;)");
    result[1057] =
        MemberPattern.of(
            false,
            new String[] {"onRefresh"},
            null,
            4,
            0,
            2,
            "(Lorg/springframework/context/ApplicationContext;)");
    result[1058] =
        MemberPattern.of(
            false,
            new String[] {"render"},
            null,
            4,
            0,
            5,
            "(Lorg/springframework/web/servlet/ModelAndView;*)");
    result[1059] =
        MemberPattern.of(
            false,
            new String[] {"processHandlerException*"},
            null,
            4,
            0,
            4,
            "(???Ljava/lang/Exception;*)");
    result[1060] =
        MemberPattern.of(
            false,
            new String[] {"handle*"},
            null,
            1,
            0,
            4,
            "(Ljavax/servlet/http/HttpServletRequest;??)");
    result[1061] = MemberPattern.of(false, new String[] {"selectHandler"}, null, 0, 0, 0, null);
    result[1062] =
        MemberPattern.of(
            false,
            new String[] {"read"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/Class;Lorg/springframework/http/HttpInputMessage;)");
    result[1063] =
        MemberPattern.of(
            false,
            new String[] {"read"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/reflect/Type;Ljava/lang/Class;Lorg/springframework/http/HttpInputMessage;)");
    result[1064] =
        MemberPattern.of(
            false,
            new String[] {"write"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/Object;Lorg/springframework/http/MediaType;Lorg/springframework/http/HttpOutputMessage;)");
    result[1065] =
        MemberPattern.of(
            false,
            new String[] {"write"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/Object;Ljava/lang/reflect/Type;Lorg/springframework/http/MediaType;Lorg/springframework/http/HttpOutputMessage;)");
    result[1066] = MemberPattern.of(false, new String[] {"invokeForRequest"}, null, 0, 0, 0, null);
    result[1067] =
        MemberPattern.of(
            false,
            new String[] {"processBean"},
            null,
            0,
            0,
            4,
            "(???Lorg/springframework/beans/factory/config/BeanDefinition;*)");
    result[1068] =
        MemberPattern.of(
            false,
            new String[] {"handleMatch"},
            null,
            4,
            0,
            2,
            "(Lorg/springframework/web/servlet/mvc/method/RequestMappingInfo;Ljava/lang/String;Ljavax/servlet/http/HttpServletRequest;)");
    result[1069] =
        MemberPattern.of(
            false,
            new String[] {"preHandle"},
            null,
            1,
            0,
            2,
            "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/Object;)");
    result[1070] =
        MemberPattern.of(
            false,
            new String[] {"postProcessBeanFactory"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/beans/factory/config/ConfigurableListableBeanFactory;*)");
    result[1071] =
        MemberPattern.of(
            false,
            new String[] {"onRefresh"},
            null,
            4,
            0,
            2,
            "(Lorg/springframework/context/ApplicationContext;)");
    result[1072] =
        MemberPattern.of(
            false,
            new String[] {"postProcessBeanFactory"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/beans/factory/config/ConfigurableListableBeanFactory;*)");
    result[1073] =
        MemberPattern.of(
            false,
            new String[] {"getHandler"},
            null,
            4,
            0,
            1,
            "(Ljakarta/servlet/http/HttpServletRequest;)Lorg/springframework/web/servlet/HandlerExecutionChain;");
    result[1074] =
        MemberPattern.of(
            false,
            new String[] {"render"},
            null,
            4,
            0,
            5,
            "(Lorg/springframework/web/servlet/ModelAndView;*)");
    result[1075] =
        MemberPattern.of(
            false,
            new String[] {"processHandlerException*"},
            null,
            4,
            0,
            4,
            "(???Ljava/lang/Exception;*)");
    result[1076] =
        MemberPattern.of(
            false,
            new String[] {"handle*"},
            null,
            1,
            0,
            4,
            "(Ljakarta/servlet/http/HttpServletRequest;??)");
    result[1077] = MemberPattern.of(false, new String[] {"invokeForRequest"}, null, 0, 0, 0, null);
    result[1078] =
        MemberPattern.of(
            false,
            new String[] {"onRefresh"},
            null,
            4,
            0,
            2,
            "(Lorg/springframework/context/ApplicationContext;)");
    result[1079] =
        MemberPattern.of(
            false,
            new String[] {"postProcessBeanFactory"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/beans/factory/config/ConfigurableListableBeanFactory;*)");
    result[1080] =
        MemberPattern.of(
            false,
            new String[] {"handleMatch"},
            null,
            4,
            0,
            2,
            "(Lorg/springframework/web/servlet/mvc/method/RequestMappingInfo;Ljava/lang/String;Ljakarta/servlet/http/HttpServletRequest;)");
    result[1081] =
        MemberPattern.of(
            false,
            new String[] {"preHandle"},
            null,
            1,
            0,
            2,
            "(Ljakarta/servlet/http/HttpServletRequest;Ljakarta/servlet/http/HttpServletResponse;Ljava/lang/Object;)");
    result[1082] =
        MemberPattern.of(
            false,
            new String[] {"postProcessBeanFactory"},
            null,
            0,
            0,
            5,
            "(Lorg/springframework/beans/factory/config/ConfigurableListableBeanFactory;*)");
    result[1083] = MemberPattern.of(false, new String[] {"invoke"}, null, 0, 0, 0, null);
    result[1084] = MemberPattern.of(false, new String[] {"incr", "decr"}, null, 1, 0, 0, null);
    result[1085] =
        MemberPattern.of(
            false,
            new String[] {"addOperation"},
            null,
            4,
            0,
            5,
            "(Lnet/spy/memcached/MemcachedNode;*)");
    result[1086] =
        MemberPattern.of(
            false,
            new String[] {"responseReceived"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/http/nio/NHttpClientConnection;*)");
    result[1087] =
        MemberPattern.of(
            false,
            new String[] {"closed", "exception", "timeout"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/http/nio/NHttpClientConnection;*)");
    result[1088] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lorg/apache/synapse/transport/passthru/TargetResponse;*)");
    result[1089] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 2, "()");
    result[1090] =
        MemberPattern.of(
            false,
            new String[] {"submit"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/axis2/context/MessageContext;*)");
    result[1091] =
        MemberPattern.of(
            false,
            new String[] {"responseReady"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/http/nio/NHttpServerConnection;*)");
    result[1092] =
        MemberPattern.of(
            false,
            new String[] {"closed", "exception", "timeout"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/http/nio/NHttpServerConnection;*)");
    result[1093] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/synapse/transport/passthru/SourceRequest;*)");
    result[1094] = MemberPattern.of(false, new String[] {"run"}, null, 0, 0, 2, "()");
    result[1095] =
        MemberPattern.of(
            false,
            new String[] {"invokeBeforeClassMethods"},
            null,
            0,
            0,
            5,
            "(Lorg/testng/ITestClass;Lorg/testng/IMethodInstance;*)");
    result[1096] =
        MemberPattern.of(
            false,
            new String[] {"invokeAfterClassMethods"},
            null,
            0,
            0,
            5,
            "(Lorg/testng/ITestClass;Lorg/testng/IMethodInstance;*)");
    result[1097] =
        MemberPattern.of(
            false,
            new String[] {"invokeBeforeClassMethods"},
            null,
            0,
            0,
            5,
            "(Lorg/testng/ITestClass;Lorg/testng/IMethodInstance;*)");
    result[1098] =
        MemberPattern.of(
            false,
            new String[] {"invokeAfterClassMethods"},
            null,
            0,
            0,
            5,
            "(Lorg/testng/ITestClass;Lorg/testng/IMethodInstance;*)");
    result[1099] =
        MemberPattern.of(
            false,
            new String[] {"shouldRetryTestMethod"},
            null,
            0,
            0,
            4,
            "(?Lorg/testng/ITestResult;*)");
    result[1100] =
        MemberPattern.of(
            false,
            new String[] {"runTestResultListener"},
            null,
            0,
            0,
            5,
            "(Lorg/testng/ITestResult;*)");
    result[1101] =
        MemberPattern.of(
            false,
            new String[] {"invokeMethod"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)");
    result[1102] =
        MemberPattern.of(
            false, new String[] {"setBody"}, null, 0, 0, 5, "(Ljava/lang/CharSequence;*)");
    result[1103] = MemberPattern.of(false, new String[] {"doProcess"}, null, 0, 0, 0, null);
    result[1104] = MemberPattern.of(false, new String[] {"callProcess"}, null, 0, 0, 0, null);
    result[1105] = MemberPattern.of(false, new String[] {"eval"}, null, 0, 0, 0, null);
    result[1106] = MemberPattern.of(false, new String[] {"handleError"}, null, 0, 0, 0, null);
    result[1107] = MemberPattern.of(false, new String[] {"schedule"}, null, 0, 0, 0, null);
    result[1108] = MemberPattern.of(false, new String[] {"handleModelEvent"}, null, 0, 0, 0, null);
    result[1109] = MemberPattern.of(false, new String[] {"onMessage"}, null, 0, 0, 0, null);
    result[1110] = MemberPattern.of(false, new String[] {"createInstance"}, null, 0, 0, 0, null);
    result[1111] = MemberPattern.of(false, new String[] {"getContext"}, null, 0, 0, 2, "()");
    result[1112] =
        MemberPattern.of(
            false, new String[] {"output"}, null, 2, 0, 4, "(Lorg/tinylog/core/LogEntry;?)");
    result[1113] = MemberPattern.of(false, new String[] {"setName"}, null, 0, 0, 0, null);
    result[1114] = MemberPattern.of(false, new String[] {"recycle"}, null, 0, 0, 2, "()");
    result[1115] = MemberPattern.of(false, new String[] {"recycle"}, null, 0, 0, 2, "()");
    result[1116] =
        MemberPattern.of(
            false,
            new String[] {"postParseRequest"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/coyote/Request;Lorg/apache/catalina/connector/Request;Lorg/apache/coyote/Response;Lorg/apache/catalina/connector/Response;*)");
    result[1117] = MemberPattern.of(false, new String[] {"init"}, null, 0, 0, 0, null);
    result[1118] = MemberPattern.of(false, new String[] {"setResources"}, null, 0, 0, 0, null);
    result[1119] =
        MemberPattern.of(
            false,
            new String[] {"action"},
            null,
            1,
            0,
            2,
            "(Lorg/apache/coyote/ActionCode;Ljava/lang/Object;)");
    result[1120] =
        MemberPattern.of(false, new String[] {"processParameters"}, null, 0, 0, 5, "([BII*)");
    result[1121] =
        MemberPattern.of(false, new String[] {"handleQueryParameters"}, null, 0, 0, 2, "()");
    result[1122] =
        MemberPattern.of(false, new String[] {"processParameters"}, null, 0, 0, 5, "([BII*)");
    result[1123] =
        MemberPattern.of(false, new String[] {"handleQueryParameters"}, null, 0, 0, 2, "()");
    result[1124] =
        MemberPattern.of(
            false,
            new String[] {"action"},
            null,
            1,
            0,
            2,
            "(Lorg/apache/coyote/ActionCode;Ljava/lang/Object;)");
    result[1125] =
        MemberPattern.of(
            false,
            new String[] {"report"},
            null,
            4,
            0,
            5,
            "(Lorg/apache/catalina/connector/Request;Lorg/apache/catalina/connector/Response;Ljava/lang/Throwable;*)");
    result[1126] = MemberPattern.of(false, new String[] {"getParts"}, null, 1, 0, 2, "()");
    result[1127] = MemberPattern.of(false, new String[] {"parseParts"}, null, 2, 0, 2, "()");
    result[1128] = MemberPattern.of(false, new String[] {"parseParts"}, null, 2, 0, 2, "(Z)");
    result[1129] =
        MemberPattern.of(
            false,
            new String[] {"createAsync", "deleteAsync", "readAsync", "fetchAsync", "updateAsync"},
            null,
            1,
            0,
            3,
            "(*)Lcom/google/common/util/concurrent/ListenableFuture;");
    result[1130] =
        MemberPattern.of(
            false,
            new String[] {"create", "delete", "read", "fetch", "update"},
            null,
            1,
            0,
            0,
            null);
    result[1131] =
        MemberPattern.of(
            false,
            new String[] {"doParse"},
            null,
            2,
            0,
            2,
            "(Lorg/xnio/channels/StreamSourceChannel;)");
    result[1132] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            4,
            "(??Lio/undertow/server/HttpServerExchange;*)");
    result[1133] =
        MemberPattern.of(false, new String[] {"getResponseChannel"}, null, 0, 2, 2, "()");
    result[1134] =
        MemberPattern.of(
            false,
            new String[] {"parseBlocking"},
            null,
            1,
            0,
            1,
            "()Lio/undertow/server/handlers/form/FormData;");
    result[1135] =
        MemberPattern.of(
            false,
            new String[] {"handle"},
            null,
            0,
            0,
            4,
            "(??Lio/undertow/server/HttpServerExchange;*)");
    result[1136] = MemberPattern.of(false, new String[] {"dispatchRequest"}, null, 0, 0, 0, null);
    result[1137] =
        MemberPattern.of(
            false,
            new String[] {"dispatch"},
            null,
            0,
            0,
            5,
            "(Ljava/util/concurrent/Executor;Ljava/lang/Runnable;*)");
    result[1138] = MemberPattern.of(false, new String[] {"dispatchRequest"}, null, 0, 0, 0, null);
    result[1139] =
        MemberPattern.of(
            false, new String[] {"executeCommand"}, null, 1, 0, 5, "(Lio/valkey/CommandObject;*)");
    result[1140] =
        MemberPattern.of(
            false,
            new String[] {"render"},
            null,
            0,
            0,
            5,
            "(Lorg/apache/velocity/context/InternalContextAdapter;Ljava/io/Writer;*)");
    result[1141] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lio/vertx/mysqlclient/MySQLConnectOptions;*)");
    result[1142] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lio/vertx/mysqlclient/impl/MySQLConnectionFactory;*)");
    result[1143] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Lio/vertx/mysqlclient/MySQLConnectOptions;*)");
    result[1144] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(?Lio/vertx/mysqlclient/MySQLConnectOptions;)");
    result[1145] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lio/vertx/mysqlclient/impl/MySQLConnectionFactory;*)");
    result[1146] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            9,
            0,
            4,
            "(??Lio/vertx/mysqlclient/MySQLConnectOptions;?)");
    result[1147] =
        MemberPattern.of(
            false, new String[] {"<init>"}, null, 0, 0, 4, "(?Ljava/util/function/Supplier;)");
    result[1148] =
        MemberPattern.of(
            false,
            new String[] {"newPoolImpl"},
            null,
            2,
            0,
            4,
            "(?Ljava/util/function/Supplier;??)");
    result[1149] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(?Lio/vertx/sqlclient/spi/ConnectionFactory;??)");
    result[1150] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(?Lio/vertx/pgclient/PgConnectOptions;)");
    result[1151] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            5,
            "(Lio/vertx/pgclient/impl/PgConnectionFactory;*)");
    result[1152] =
        MemberPattern.of(
            false,
            new String[] {"create"},
            null,
            9,
            0,
            4,
            "(??Lio/vertx/pgclient/PgConnectOptions;?)");
    result[1153] =
        MemberPattern.of(
            false, new String[] {"<init>"}, null, 0, 0, 4, "(?Ljava/util/function/Supplier;)");
    result[1154] =
        MemberPattern.of(
            false,
            new String[] {"newPoolImpl"},
            null,
            2,
            0,
            4,
            "(?Ljava/util/function/Supplier;??)");
    result[1155] =
        MemberPattern.of(false, new String[] {"<init>"}, null, 0, 0, 5, "(Ljava/lang/String;*)");
    result[1156] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            5,
            "(Lio/vertx/redis/client/Request;Lio/vertx/core/Handler;*)");
    result[1157] =
        MemberPattern.of(
            false,
            new String[] {"send"},
            null,
            1,
            0,
            1,
            "(Lio/vertx/redis/client/Request;)Lio/vertx/core/Future;");
    result[1158] =
        MemberPattern.of(
            false, new String[] {"read"}, null, 1, 0, 4, "(?Lio/vertx/core/Handler;*)");
    result[1159] =
        MemberPattern.of(
            false, new String[] {"execute"}, null, 1, 0, 4, "(?Lio/vertx/core/Handler;)");
    result[1160] =
        MemberPattern.of(
            false, new String[] {"executeBatch"}, null, 1, 0, 4, "(?Lio/vertx/core/Handler;)");
    result[1161] = MemberPattern.of(false, new String[] {"query"}, null, 1, 0, 2, "()");
    result[1162] =
        MemberPattern.of(
            false, new String[] {"execute"}, null, 1, 0, 2, "(Lio/vertx/core/Handler;)");
    result[1163] =
        MemberPattern.of(false, new String[] {"query"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[1164] =
        MemberPattern.of(
            false, new String[] {"preparedQuery"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[1165] =
        MemberPattern.of(
            false,
            new String[] {"prepare"},
            null,
            1,
            0,
            2,
            "(Ljava/lang/String;Lio/vertx/core/Handler;)");
    result[1166] = MemberPattern.of(false, new String[] {"params"}, null, 1, 0, 2, "()");
    result[1167] =
        MemberPattern.of(
            false, new String[] {"handleData"}, null, 0, 0, 2, "(Lio/vertx/core/buffer/Buffer;)");
    result[1168] =
        MemberPattern.of(
            false, new String[] {"onData"}, null, 0, 0, 2, "(Lio/vertx/core/buffer/Buffer;)");
    result[1169] = MemberPattern.of(false, new String[] {"toString"}, null, 1, 0, 0, null);
    result[1170] = MemberPattern.of(false, new String[] {"getByteBuf"}, null, 1, 0, 2, "()");
    result[1171] =
        MemberPattern.of(
            false,
            new String[] {"appendBuffer"},
            null,
            1,
            0,
            5,
            "(Lio/vertx/core/buffer/Buffer;*)");
    result[1172] =
        MemberPattern.of(false, new String[] {"get"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[1173] =
        MemberPattern.of(false, new String[] {"getAll"}, null, 1, 0, 2, "(Ljava/lang/String;)");
    result[1174] = MemberPattern.of(false, new String[] {"entries"}, null, 1, 0, 2, "()");
    result[1175] = MemberPattern.of(false, new String[] {"names"}, null, 1, 0, 2, "()");
    result[1176] = MemberPattern.of(false, new String[] {"get"}, null, 1, 0, 4, "(?)");
    result[1177] = MemberPattern.of(false, new String[] {"getAll"}, null, 1, 0, 4, "(?)");
    result[1178] = MemberPattern.of(false, new String[] {"entries"}, null, 1, 0, 2, "()");
    result[1179] = MemberPattern.of(false, new String[] {"names"}, null, 1, 0, 2, "()");
    result[1180] = MemberPattern.of(false, new String[] {"headers"}, null, 1, 0, 2, "()");
    result[1181] = MemberPattern.of(false, new String[] {"headers"}, null, 1, 0, 2, "()");
    result[1182] = MemberPattern.of(false, new String[] {"getName"}, null, 0, 0, 2, "()");
    result[1183] = MemberPattern.of(false, new String[] {"getValue"}, null, 0, 0, 2, "()");
    result[1184] =
        MemberPattern.of(
            false, new String[] {"bodyHandler"}, null, 1, 0, 2, "(Lio/vertx/core/Handler;)");
    result[1185] =
        MemberPattern.of(
            false, new String[] {"endHandler"}, null, 1, 0, 5, "(Lio/vertx/core/Handler;*)");
    result[1186] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)");
    result[1187] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/String;)");
    result[1188] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/CharSequence;Ljava/lang/Iterable;)");
    result[1189] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Iterable;)");
    result[1190] = MemberPattern.of(false, new String[] {"cookies"}, null, 0, 0, 2, "()");
    result[1191] =
        MemberPattern.of(false, new String[] {"getCookie"}, null, 0, 0, 2, "(Ljava/lang/String;)");
    result[1192] =
        MemberPattern.of(false, new String[] {"reroute"}, null, 0, 0, 4, "(?Ljava/lang/String;)");
    result[1193] =
        MemberPattern.of(
            false,
            new String[] {"handler", "blockingHandler"},
            null,
            1,
            0,
            5,
            "(Lio/vertx/core/Handler;*)");
    result[1194] =
        MemberPattern.of(
            false,
            new String[] {"matches"},
            null,
            1,
            0,
            1,
            "(Lio/vertx/ext/web/impl/RoutingContextImplBase;Ljava/lang/String;Z)I");
    result[1195] =
        MemberPattern.of(
            false,
            new String[] {"matches"},
            null,
            0,
            0,
            1,
            "(Lio/vertx/ext/web/impl/RoutingContextImplBase;Ljava/lang/String;Z)Z");
    result[1196] =
        MemberPattern.of(
            false,
            new String[] {"matches"},
            null,
            0,
            0,
            1,
            "(Lio/vertx/ext/web/RoutingContext;Ljava/lang/String;Z)Z");
    result[1197] = MemberPattern.of(false, new String[] {"getBodyAsJson"}, null, 0, 0, 2, "()");
    result[1198] =
        MemberPattern.of(false, new String[] {"getBodyAsJsonArray"}, null, 0, 0, 2, "()");
    result[1199] =
        MemberPattern.of(
            false, new String[] {"setSession"}, null, 0, 0, 5, "(Lio/vertx/ext/web/Session;*)");
  }

  private static void fill1200(MemberPattern[] result) {
    result[1200] = MemberPattern.of(false, new String[] {"fileUploads"}, null, 0, 0, 2, "()");
    result[1201] =
        MemberPattern.of(
            false, new String[] {"exceptionHandler"}, null, 1, 0, 1, "()Lio/vertx/core/Handler;");
    result[1202] =
        MemberPattern.of(false, new String[] {"get"}, null, 1, 0, 2, "(Ljava/lang/CharSequence;)");
    result[1203] =
        MemberPattern.of(
            false, new String[] {"getAll"}, null, 1, 0, 2, "(Ljava/lang/CharSequence;)");
    result[1204] = MemberPattern.of(false, new String[] {"entries"}, null, 1, 0, 2, "()");
    result[1205] = MemberPattern.of(false, new String[] {"names"}, null, 1, 0, 2, "()");
    result[1206] =
        MemberPattern.of(
            false, new String[] {"addCookie"}, null, 1, 0, 5, "(Lio/vertx/core/http/Cookie;*)");
    result[1207] = MemberPattern.of(false, new String[] {"headers"}, null, 1, 0, 2, "()");
    result[1208] = MemberPattern.of(false, new String[] {"params"}, null, 1, 0, 2, "()");
    result[1209] = MemberPattern.of(false, new String[] {"params"}, null, 1, 0, 2, "(Z)");
    result[1210] =
        MemberPattern.of(
            false, new String[] {"handleData"}, null, 0, 0, 2, "(Lio/vertx/core/buffer/Buffer;)");
    result[1211] =
        MemberPattern.of(
            false, new String[] {"onData"}, null, 0, 0, 2, "(Lio/vertx/core/buffer/Buffer;)");
    result[1212] =
        MemberPattern.of(false, new String[] {"cookies"}, null, 1, 0, 3, "(*)Ljava/util/Set;");
    result[1213] =
        MemberPattern.of(false, new String[] {"getCookie"}, null, 1, 0, 5, "(Ljava/lang/String;*)");
    result[1214] = MemberPattern.of(false, new String[] {"toString"}, null, 1, 0, 0, null);
    result[1215] = MemberPattern.of(false, new String[] {"getByteBuf"}, null, 1, 0, 2, "()");
    result[1216] =
        MemberPattern.of(
            false,
            new String[] {"appendBuffer"},
            null,
            1,
            0,
            5,
            "(Lio/vertx/core/buffer/Buffer;*)");
    result[1217] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)");
    result[1218] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/String;)");
    result[1219] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/CharSequence;Ljava/lang/Iterable;)");
    result[1220] =
        MemberPattern.of(
            false,
            new String[] {"putHeader"},
            null,
            0,
            0,
            2,
            "(Ljava/lang/String;Ljava/lang/Iterable;)");
    result[1221] = MemberPattern.of(false, new String[] {"entries"}, null, 1, 0, 2, "()");
    result[1222] = MemberPattern.of(false, new String[] {"names"}, null, 1, 0, 2, "()");
    result[1223] = MemberPattern.of(false, new String[] {"getName"}, null, 0, 0, 2, "()");
    result[1224] = MemberPattern.of(false, new String[] {"getValue"}, null, 0, 0, 2, "()");
    result[1225] =
        MemberPattern.of(
            false, new String[] {"bodyHandler"}, null, 1, 0, 2, "(Lio/vertx/core/Handler;)");
    result[1226] =
        MemberPattern.of(
            false, new String[] {"endHandler"}, null, 1, 0, 5, "(Lio/vertx/core/Handler;*)");
    result[1227] =
        MemberPattern.of(false, new String[] {"reroute"}, null, 0, 0, 4, "(?Ljava/lang/String;)");
    result[1228] =
        MemberPattern.of(
            false,
            new String[] {"init"},
            null,
            1,
            0,
            2,
            "(Lio/vertx/core/Vertx;Lio/vertx/core/json/JsonObject;)");
    result[1229] = MemberPattern.of(false, new String[] {"setTimer"}, null, 2, 0, 1, "()V");
    result[1230] = MemberPattern.of(false, new String[] {"asJsonObject"}, null, 0, 0, 2, "(I)");
    result[1231] = MemberPattern.of(false, new String[] {"asJsonArray"}, null, 0, 0, 2, "(I)");
    result[1232] =
        MemberPattern.of(
            false,
            new String[] {"handler", "blockingHandler"},
            null,
            1,
            0,
            5,
            "(Lio/vertx/core/Handler;*)");
    result[1233] =
        MemberPattern.of(
            false,
            new String[] {"matches"},
            null,
            1,
            0,
            1,
            "(Lio/vertx/ext/web/impl/RoutingContextImplBase;Ljava/lang/String;Z)I");
    result[1234] =
        MemberPattern.of(
            false,
            new String[] {"matches"},
            null,
            0,
            0,
            1,
            "(Lio/vertx/ext/web/impl/RoutingContextImplBase;Ljava/lang/String;Z)Z");
    result[1235] =
        MemberPattern.of(
            false,
            new String[] {"matches"},
            null,
            0,
            0,
            1,
            "(Lio/vertx/ext/web/RoutingContext;Ljava/lang/String;Z)Z");
    result[1236] = MemberPattern.of(false, new String[] {"getBodyAsJson"}, null, 0, 0, 2, "(I)");
    result[1237] =
        MemberPattern.of(false, new String[] {"getBodyAsJsonArray"}, null, 0, 0, 2, "(I)");
    result[1238] =
        MemberPattern.of(
            false, new String[] {"setSession"}, null, 0, 0, 5, "(Lio/vertx/ext/web/Session;*)");
    result[1239] = MemberPattern.of(false, new String[] {"fileUploads"}, null, 0, 0, 2, "()");
    result[1240] =
        MemberPattern.of(false, new String[] {"json"}, null, 0, 0, 2, "(Ljava/lang/Object;)");
    result[1241] =
        MemberPattern.of(
            false,
            new String[] {"exceptionCaught"},
            null,
            1,
            0,
            2,
            "(Lio/netty/channel/ChannelHandlerContext;Ljava/lang/Throwable;)");
    result[1242] =
        MemberPattern.of(
            false, new String[] {"exceptionHandler"}, null, 1, 0, 1, "()Lio/vertx/core/Handler;");
    result[1243] =
        MemberPattern.of(
            false, new String[] {"bodyHandler"}, null, 1, 0, 2, "(Lio/vertx/core/Handler;)");
    result[1244] = MemberPattern.of(false, new String[] {"fileUploads"}, null, 0, 0, 2, "()");
    result[1245] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(?????Ljava/util/concurrent/ConcurrentLinkedQueue;*)");
    result[1246] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(?????Ljava/util/concurrent/LinkedBlockingQueue;*)");
    result[1247] =
        MemberPattern.of(false, new String[] {"sendText"}, null, 1, 0, 1, "(Ljava/lang/String;)V");
    result[1248] =
        MemberPattern.of(false, new String[] {"sendText"}, null, 1, 0, 1, "(Ljava/lang/String;Z)V");
    result[1249] =
        MemberPattern.of(
            false, new String[] {"sendBinary"}, null, 1, 0, 1, "(Ljava/nio/ByteBuffer;)V");
    result[1250] =
        MemberPattern.of(
            false, new String[] {"sendBinary"}, null, 1, 0, 1, "(Ljava/nio/ByteBuffer;Z)V");
    result[1251] = MemberPattern.of(false, new String[] {"sendObject"}, null, 1, 0, 4, "(?)V");
    result[1252] = MemberPattern.of(false, new String[] {"getSendStream"}, null, 1, 0, 2, "()");
    result[1253] = MemberPattern.of(false, new String[] {"getSendWriter"}, null, 1, 0, 2, "()");
    result[1254] = MemberPattern.of(false, new String[] {"onMessage"}, null, 1, 0, 4, "(?)");
    result[1255] = MemberPattern.of(false, new String[] {"onMessage"}, null, 1, 0, 4, "(?Z)");
    result[1256] =
        MemberPattern.of(
            false, new String[] {"getBasicRemote", "getAsyncRemote"}, null, 1, 0, 2, "()");
    result[1257] = MemberPattern.of(false, new String[] {"close"}, null, 0, 0, 2, "()");
    result[1258] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(?Ljava/lang/Object;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodHandle;?????)");
    result[1259] =
        MemberPattern.of(
            false,
            new String[] {"<init>"},
            null,
            0,
            0,
            4,
            "(??Ljava/lang/Object;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodHandle;?????)");
    result[1260] =
        MemberPattern.of(
            false,
            new String[] {"onOpen"},
            null,
            0,
            0,
            4,
            "(Lorg/eclipse/jetty/websocket/core/CoreSession;?)");
    result[1261] =
        MemberPattern.of(false, new String[] {"createMessageSink"}, null, 0, 0, 4, "(????)");
    result[1262] = MemberPattern.of(false, new String[] {"notifyOnClose"}, null, 0, 0, 4, "(??)");
    result[1263] =
        MemberPattern.of(
            false,
            new String[] {"sendText", "sendBinary"},
            null,
            0,
            0,
            4,
            "(?Lorg/eclipse/jetty/websocket/api/Callback;)");
    result[1264] =
        MemberPattern.of(
            false,
            new String[] {"sendPartialText", "sendPartialBinary"},
            null,
            0,
            0,
            4,
            "(?ZLorg/eclipse/jetty/websocket/api/Callback;)");
    result[1265] =
        MemberPattern.of(false, new String[] {"isSecurityEnabled"}, null, 0, 0, 3, "(*)Z");
    result[1266] =
        MemberPattern.of(false, new String[] {"getEnvironmentEntries"}, null, 0, 0, 0, null);
    result[1267] = MemberPattern.of(false, new String[] {"invoke"}, null, 0, 0, 4, "(?)");
    result[1268] = MemberPattern.of(false, new String[] {"defaultSupervisor"}, null, 0, 0, 0, null);
    result[1269] =
        MemberPattern.of(
            false, null, "com/microsoft/azure/functions/annotation/FunctionName", 0, 0, 0, null);
    result[1270] = MemberPattern.of(false, null, "datadog/trace/api/DoNotTrace", 0, 0, 0, null);
    result[1271] =
        MemberPattern.of(
            false,
            null,
            "io/opentelemetry/instrumentation/annotations/AddingSpanAttributes",
            0,
            0,
            0,
            null);
    result[1272] =
        MemberPattern.of(
            false, null, "io/opentelemetry/instrumentation/annotations/WithSpan", 0, 0, 0, null);
    result[1273] = MemberPattern.of(false, null, "jakarta/ws/rs/Path", 0, 0, 0, null);
    result[1274] = MemberPattern.of(false, null, "javax/ws/rs/Path", 0, 0, 0, null);
  }
}
