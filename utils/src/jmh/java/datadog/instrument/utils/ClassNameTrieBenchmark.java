package datadog.instrument.utils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.openjdk.jmh.annotations.Mode.SingleShotTime;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(SingleShotTime)
@OutputTimeUnit(MILLISECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@SuppressWarnings("unused")
public class ClassNameTrieBenchmark {

  private List<String> classNames;
  private ClassNameTrie classNameTrie;
  private RadixTrieNode radixTrie;

  @Setup(Level.Trial)
  public void setup() {
    try {
      classNames = Files.readAllLines(Paths.get("src/test/resources/sample_class_names.txt"));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    classNameTrie = buildClassNameTrie();
    radixTrie = buildRadixTrie();
  }

  @Benchmark
  @Fork(value = 20)
  @Threads(value = 1)
  public void classNameTrie(Blackhole blackhole) {
    testClassNameTrie(blackhole);
  }

  @Benchmark
  @Fork(value = 20)
  @Threads(value = 1)
  public void radixTrie(Blackhole blackhole) {
    testRadixTrie(blackhole);
  }

  @Benchmark
  @Fork(value = 20)
  @Threads(value = 1)
  public void legacyCode(Blackhole blackhole) {
    testLegacyCode(blackhole);
  }

  private void testClassNameTrie(Blackhole blackhole) {
    for (String name : classNames) {
      blackhole.consume(classNameTrie.apply(name));
    }
  }

  private void testRadixTrie(Blackhole blackhole) {
    for (String name : classNames) {
      blackhole.consume(applyRadixTrie(radixTrie, name));
    }
  }

  private void testLegacyCode(Blackhole blackhole) {
    for (String name : classNames) {
      blackhole.consume(match(name));
    }
  }

  private static ClassNameTrie buildClassNameTrie() {
    ClassNameTrie.Builder builder = new ClassNameTrie.Builder();
    buildTrieEntries(builder::put);
    return builder.buildTrie();
  }

  private static void putArrayNode(ArrayTrieNode root, String className, int number) {
    boolean isGlob = className.charAt(className.length() - 1) == '*';
    String key = isGlob ? className.substring(0, className.length() - 1) : className;
    ArrayTrieNode node = root;
    for (int i = 0, len = key.length(); i < len; i++) {
      char c = normalize(key.charAt(i));
      int pos = Arrays.binarySearch(node.keys, 0, node.size, c);
      if (pos < 0) {
        int insertAt = -(pos + 1);
        if (node.size == node.keys.length) {
          int newCap = node.size == 0 ? 4 : node.size * 2;
          node.keys = Arrays.copyOf(node.keys, newCap);
          node.children = Arrays.copyOf(node.children, newCap);
        }
        System.arraycopy(node.keys, insertAt, node.keys, insertAt + 1, node.size - insertAt);
        System.arraycopy(
            node.children, insertAt, node.children, insertAt + 1, node.size - insertAt);
        node.keys[insertAt] = c;
        node.children[insertAt] = new ArrayTrieNode();
        node.size++;
        pos = insertAt;
      }
      node = node.children[pos];
    }
    node.result = number;
    node.glob = isGlob;
  }

  private static RadixTrieNode buildRadixTrie() {
    ArrayTrieNode regular = new ArrayTrieNode();
    buildTrieEntries((name, value) -> putArrayNode(regular, name, value));
    return compressArrayNodeTrie(regular, new StringBuilder());
  }

  private static RadixTrieNode compressArrayNodeTrie(ArrayTrieNode source, StringBuilder scratch) {
    RadixTrieNode dest = new RadixTrieNode();
    dest.result = source.result;
    dest.glob = source.glob;
    if (source.size == 0) {
      return dest;
    }
    dest.keys = new char[source.size];
    dest.segments = new String[source.size];
    dest.children = new RadixTrieNode[source.size];
    dest.size = source.size;
    for (int i = 0; i < source.size; i++) {
      ArrayTrieNode node = source.children[i];
      scratch.setLength(0);
      while (node.size == 1 && node.result < 0 && !node.glob) {
        scratch.append(node.keys[0]);
        node = node.children[0];
      }
      dest.keys[i] = source.keys[i];
      dest.segments[i] = scratch.toString();
      dest.children[i] = compressArrayNodeTrie(node, scratch);
    }
    return dest;
  }

  private static int applyRadixTrie(RadixTrieNode root, String key) {
    RadixTrieNode node = root;
    int result = -1;
    int keyLen = key.length();
    int keyIdx = 0;
    while (keyIdx < keyLen) {
      char c = normalize(key.charAt(keyIdx++));
      int pos = Arrays.binarySearch(node.keys, 0, node.size, c);
      if (pos < 0) return result;
      String segment = node.segments[pos];
      int segmentLen = segment.length();
      if (keyLen - keyIdx < segmentLen) return result;
      for (int t = 0; t < segmentLen; t++) {
        if (normalize(key.charAt(keyIdx + t)) != segment.charAt(t)) return result;
      }
      keyIdx += segmentLen;
      node = node.children[pos];
      if (node.glob) result = node.result;
    }
    if (!node.glob && node.result >= 0) result = node.result;
    return result;
  }

  private static char normalize(char c) {
    return c == '/' ? '.' : c;
  }

  private static void buildTrieEntries(BiConsumer<String, Integer> builder) {
    builder.accept("cinnamon.*", 1);
    builder.accept("clojure.*", 1);
    builder.accept("co.elastic.apm.*", 1);
    builder.accept("com.appdynamics.*", 1);
    builder.accept("com.contrastsecurity.*", 1);
    builder.accept("com.dynatrace.*", 1);
    builder.accept("com.intellij.rt.debugger.*", 1);
    builder.accept("com.jinspired.*", 1);
    builder.accept("com.jloadtrace.*", 1);
    builder.accept("com.newrelic.*", 1);
    builder.accept("com.p6spy.*", 1);
    builder.accept("com.singularity.*", 1);
    builder.accept("com.sun.*", 1);
    builder.accept("com.sun.jersey.api.client.*", 0);
    builder.accept("com.sun.messaging.*", 0);
    builder.accept("datadog.opentracing.*", 1);
    builder.accept("datadog.slf4j.*", 1);
    builder.accept("datadog.trace.*", 1);
    builder.accept("datadog.trace.bootstrap.instrumentation.java.concurrent.RunnableWrapper", 0);
    builder.accept("datadog.trace.core.*", 1);
    builder.accept("io.micrometer.*", 1);
    builder.accept("io.micronaut.tracing.*", 1);
    builder.accept("java.*", 1);
    builder.accept("java.lang.Throwable", 0);
    builder.accept("java.net.HttpURLConnection", 0);
    builder.accept("java.net.URL", 0);
    builder.accept("java.rmi.*", 0);
    builder.accept("java.util.concurrent.*", 0);
    builder.accept("java.util.logging.*", 0);
    builder.accept("java.util.logging.LogManager$Cleaner", 1);
    builder.accept("jdk.*", 1);
    builder.accept("net.bytebuddy.*", 1);
    builder.accept("org.apache.felix.framework.URLHandlers", 1);
    builder.accept("org.apache.felix.framework.URLHandlersActivator", 1);
    builder.accept("org.apache.felix.framework.URLHandlersBundleStreamHandler", 1);
    builder.accept("org.apache.felix.framework.URLHandlersBundleURLConnection", 1);
    builder.accept("org.apache.felix.framework.URLHandlersContentHandlerProxy", 1);
    builder.accept("org.apache.felix.framework.URLHandlersStreamHandlerProxy", 1);
    builder.accept("org.apache.groovy.*", 1);
    builder.accept("org.aspectj.*", 1);
    builder.accept("org.codehaus.groovy.*", 1);
    builder.accept("org.codehaus.groovy.runtime.*", 0);
    builder.accept("org.eclipse.osgi.internal.url.*", 1);
    builder.accept("org.groovy.*", 1);
    builder.accept("org.jinspired.*", 1);
    builder.accept("org.springframework.context.support.ContextTypeMatchClassLoader", 1);
    builder.accept("org.springframework.core.DecoratingClassLoader", 1);
    builder.accept("org.springframework.core.OverridingClassLoader", 1);
    builder.accept("org.springframework.core.$Proxy", 1);
    builder.accept("org.springframework.instrument.classloading.ShadowingClassLoader", 1);
    builder.accept("org.springframework.instrument.classloading.SimpleThrowawayClassLoader", 1);
    builder.accept("org.eclipse.osgi.framework.internal.protocol.*", 1);
    builder.accept("sun.*", 1);
    builder.accept("sun.net.www.http.HttpClient", 0);
    builder.accept("sun.net.www.protocol.*", 0);
    builder.accept("sun.net.www.protocol.jrt", 1);
    builder.accept("sun.rmi.server.*", 0);
    builder.accept("sun.rmi.transport.*", 0);
    builder.accept("akka.actor.*", 2);
    builder.accept("akka.actor.ActorCell", 0);
    builder.accept("akka.actor.ActorSystem$*", 0);
    builder.accept("akka.actor.ActorSystemImpl$*", 0);
    builder.accept("akka.actor.CoordinatedShutdown$*", 0);
    builder.accept("akka.actor.LightArrayRevolverScheduler$*", 0);
    builder.accept("akka.actor.Scheduler$*", 0);
    builder.accept("akka.http.impl.*", 2);
    builder.accept("akka.http.impl.engine.client.PoolMasterActor", 0);
    builder.accept("akka.http.impl.engine.client.pool.NewHostConnectionPool$*", 0);
    builder.accept("akka.http.impl.engine.http2.Http2Ext", 0);
    builder.accept("akka.http.impl.engine.server.HttpServerBluePrint$TimeoutAccessImpl$*", 0);
    builder.accept("akka.http.impl.util.StreamUtils$*", 0);
    builder.accept("akka.http.scaladsl.*", 2);
    builder.accept("akka.http.scaladsl.Http2Ext", 0);
    builder.accept("akka.http.scaladsl.HttpExt", 0);
    builder.accept("akka.stream.*", 2);
    builder.accept("akka.stream.impl.FanIn$SubInput", 0);
    builder.accept("akka.stream.impl.FanOut$SubstreamSubscription", 0);
    builder.accept("akka.stream.impl.fusing.ActorGraphInterpreter$*", 0);
    builder.accept("akka.stream.stage.GraphStageLogic$*", 0);
    builder.accept("akka.stream.stage.TimerGraphStageLogic$*", 0);
    builder.accept("ch.qos.logback.*", 2);
    builder.accept("ch.qos.logback.classic.Logger", 0);
    builder.accept("ch.qos.logback.classic.spi.LoggingEvent", 0);
    builder.accept("ch.qos.logback.classic.spi.LoggingEventVO", 0);
    builder.accept("ch.qos.logback.core.AsyncAppenderBase$Worker", 0);
    builder.accept("com.beust.jcommander.*", 2);
    builder.accept("com.carrotsearch.hppc.*", 2);
    builder.accept("com.carrotsearch.hppc.HashOrderMixing$*", 0);
    builder.accept("com.codahale.metrics.*", 2);
    builder.accept("com.codahale.metrics.servlets.*", 0);
    builder.accept("com.couchbase.client.deps.*", 2);
    builder.accept("com.couchbase.client.deps.com.lmax.disruptor.*", 0);
    builder.accept("com.couchbase.client.deps.io.netty.*", 0);
    builder.accept("com.couchbase.client.deps.org.LatencyUtils.*", 0);
    builder.accept("com.fasterxml.classmate.*", 2);
    builder.accept("com.fasterxml.jackson.*", 2);
    builder.accept("com.fasterxml.jackson.module.afterburner.util.MyClassLoader", 0);
    builder.accept("com.github.mustachejava.*", 2);
    builder.accept("com.google.api.*", 2);
    builder.accept("com.google.api.client.http.HttpRequest", 0);
    builder.accept("com.google.cloud.*", 2);
    builder.accept("com.google.common.*", 2);
    builder.accept("com.google.common.base.internal.Finalizer", 0);
    builder.accept("com.google.common.util.concurrent.*", 0);
    builder.accept("com.google.gson.*", 2);
    builder.accept("com.google.inject.*", 2);
    builder.accept("com.google.inject.internal.AbstractBindingProcessor$*", 0);
    builder.accept("com.google.inject.internal.BytecodeGen$*", 0);
    builder.accept("com.google.inject.internal.cglib.core.internal.$LoadingCache", 0);
    builder.accept("com.google.inject.internal.cglib.core.internal.$LoadingCache$*", 0);
    builder.accept("com.google.instrumentation.*", 2);
    builder.accept("com.google.j2objc.*", 2);
    builder.accept("com.google.logging.*", 2);
    builder.accept("com.google.longrunning.*", 2);
    builder.accept("com.google.protobuf.*", 2);
    builder.accept("com.google.rpc.*", 2);
    builder.accept("com.google.thirdparty.*", 2);
    builder.accept("com.google.type.*", 2);
    builder.accept("com.jayway.jsonpath.*", 2);
    builder.accept("com.lightbend.lagom.*", 2);
    builder.accept("javax.el.*", 2);
    builder.accept("javax.xml.*", 2);
    builder.accept("kotlin.*", 2);
    builder.accept("net.sf.cglib.*", 2);
    builder.accept("org.apache.bcel.*", 2);
    builder.accept("org.apache.html.*", 2);
    builder.accept("org.apache.log4j.*", 2);
    builder.accept("org.apache.log4j.Category", 0);
    builder.accept("org.apache.log4j.MDC", 0);
    builder.accept("org.apache.log4j.spi.LoggingEvent", 0);
    builder.accept("org.apache.lucene.*", 2);
    builder.accept("org.apache.regexp.*", 2);
    builder.accept("org.apache.tartarus.*", 2);
    builder.accept("org.apache.wml.*", 2);
    builder.accept("org.apache.xalan.*", 2);
    builder.accept("org.apache.xerces.*", 2);
    builder.accept("org.apache.xml.*", 2);
    builder.accept("org.apache.xpath.*", 2);
    builder.accept("org.h2.*", 2);
    builder.accept("org.h2.Driver", 0);
    builder.accept("org.h2.engine.DatabaseCloser", 0);
    builder.accept("org.h2.engine.OnExitDatabaseCloser", 0);
    builder.accept("org.h2.jdbc.*", 0);
    builder.accept("org.h2.jdbcx.*", 0);
    builder.accept("org.h2.store.FileLock", 0);
    builder.accept("org.h2.store.WriterThread", 0);
    builder.accept("org.h2.tools.Server", 0);
    builder.accept("org.h2.util.MathUtils$1", 0);
    builder.accept("org.h2.util.Task", 0);
    builder.accept("org.json.simple.*", 2);
    builder.accept("org.springframework.*", 0);
    builder.accept("org.springframework.amqp.*", 0);
    builder.accept("org.springframework.aop.*", 2);
    builder.accept("org.springframework.aop.interceptor.AsyncExecutionInterceptor", 0);
    builder.accept("org.springframework.beans.*", 2);
    builder.accept("org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader$*", 0);
    builder.accept(
        "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory", 0);
    builder.accept("org.springframework.beans.factory.support.AbstractBeanFactory", 0);
    builder.accept("org.springframework.beans.factory.support.DefaultListableBeanFactory", 0);
    builder.accept("org.springframework.beans.factory.support.DisposableBeanAdapter", 0);
    builder.accept("org.springframework.boot.*", 2);
    builder.accept("org.springframework.boot.autoconfigure.BackgroundPreinitializer$*", 0);
    builder.accept("org.springframework.boot.autoconfigure.condition.OnClassCondition$*", 0);
    builder.accept(
        "org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext",
        0);
    builder.accept("org.springframework.boot.context.embedded.EmbeddedWebApplicationContext", 0);
    builder.accept(
        "org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer$*", 0);
    builder.accept(
        "org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedWebappClassLoader", 0);
    builder.accept("org.springframework.boot.web.embedded.netty.NettyWebServer$*", 0);
    builder.accept(
        "org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader", 0);
    builder.accept("org.springframework.boot.web.embedded.tomcat.TomcatWebServer$1", 0);
    builder.accept(
        "org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext",
        0);
    builder.accept(
        "org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext", 0);
    builder.accept("org.springframework.cache.*", 2);
    builder.accept("org.springframework.cglib.*", 2);
    builder.accept("org.springframework.cglib.core.internal.LoadingCache", 0);
    builder.accept("org.springframework.cglib.core.internal.LoadingCache$*", 0);
    builder.accept("org.springframework.context.*", 2);
    builder.accept("org.springframework.context.support.AbstractApplicationContext$*", 0);
    builder.accept("org.springframework.core.*", 2);
    builder.accept("org.springframework.core.task.*", 0);
    builder.accept("org.springframework.dao.*", 2);
    builder.accept("org.springframework.data.*", 2);
    builder.accept("org.springframework.data.convert.ClassGeneratingEntityInstantiator$*", 0);
    builder.accept("org.springframework.data.jpa.repository.config.InspectionClassLoader", 0);
    builder.accept("org.springframework.data.repository.core.support.RepositoryFactorySupport", 0);
    builder.accept("org.springframework.ejb.*", 2);
    builder.accept("org.springframework.expression.*", 2);
    builder.accept("org.springframework.format.*", 2);
    builder.accept("org.springframework.http.*", 2);
    builder.accept("org.springframework.http.server.reactive.*", 0);
    builder.accept("org.springframework.instrument.*", 2);
    builder.accept("org.springframework.jca.*", 2);
    builder.accept("org.springframework.jdbc.*", 2);
    builder.accept("org.springframework.jms.*", 2);
    builder.accept("org.springframework.jms.listener.*", 0);
    builder.accept("org.springframework.jmx.*", 2);
    builder.accept("org.springframework.jndi.*", 2);
    builder.accept("org.springframework.lang.*", 2);
    builder.accept("org.springframework.messaging.*", 2);
    builder.accept("org.springframework.objenesis.*", 2);
    builder.accept("org.springframework.orm.*", 2);
    builder.accept("org.springframework.remoting.*", 2);
    builder.accept("org.springframework.scripting.*", 2);
    builder.accept("org.springframework.stereotype.*", 2);
    builder.accept("org.springframework.transaction.*", 2);
    builder.accept("org.springframework.ui.*", 2);
    builder.accept("org.springframework.util.*", 2);
    builder.accept("org.springframework.util.concurrent.*", 0);
    builder.accept("org.springframework.validation.*", 2);
    builder.accept("org.springframework.web.*", 2);
    builder.accept("org.springframework.web.context.request.async.*", 0);
    builder.accept(
        "org.springframework.web.context.support.AbstractRefreshableWebApplicationContext", 0);
    builder.accept("org.springframework.web.context.support.GenericWebApplicationContext", 0);
    builder.accept("org.springframework.web.context.support.XmlWebApplicationContext", 0);
    builder.accept("org.springframework.web.reactive.*", 0);
    builder.accept("org.springframework.web.servlet.*", 0);
    builder.accept("org.xml.*", 2);
    builder.accept("org.yaml.snakeyaml.*", 2);
    builder.accept("scala.collection.*", 2);
  }

  private static final class ArrayTrieNode {
    private static final char[] EMPTY_KEYS = new char[0];
    private static final ArrayTrieNode[] EMPTY_CHILDREN = new ArrayTrieNode[0];

    private char[] keys = EMPTY_KEYS;
    private ArrayTrieNode[] children = EMPTY_CHILDREN;
    private int size;
    private int result = -1;
    private boolean glob;
  }

  private static final class RadixTrieNode {
    private static final char[] EMPTY_KEYS = new char[0];
    private static final String[] EMPTY_SEGMENTS = new String[0];
    private static final RadixTrieNode[] EMPTY_CHILDREN = new RadixTrieNode[0];

    private char[] keys = EMPTY_KEYS;
    private String[] segments = EMPTY_SEGMENTS;
    private RadixTrieNode[] children = EMPTY_CHILDREN;
    private int size;
    private int result = -1;
    private boolean glob;
  }

  private static final Set<String> SPRING_CLASSLOADER_IGNORES =
      new HashSet<>(
          Arrays.asList(
              "org.springframework.context.support.ContextTypeMatchClassLoader",
              "org.springframework.core.OverridingClassLoader",
              "org.springframework.core.DecoratingClassLoader",
              "org.springframework.instrument.classloading.SimpleThrowawayClassLoader",
              "org.springframework.instrument.classloading.ShadowingClassLoader"));

  private static int match(String name) {
    return primaryMatch(name) ? 1 : secondaryMatch(name) ? 2 : 0;
  }

  @SuppressWarnings({"RedundantIfStatement", "PointlessArithmeticExpression"})
  private static boolean primaryMatch(String name) {
    switch (name.charAt(0) - 'a') {
      // starting at zero to get a tableswitch from javac, though it looks horrendous
      case 'a' - 'a':
        break;
      case 'b' - 'a':
        break;
      case 'c' - 'a':
        if (name.startsWith("com.")) {
          if (name.startsWith("com.p6spy.")
              || name.startsWith("com.newrelic.")
              || name.startsWith("com.dynatrace.")
              || name.startsWith("com.jloadtrace.")
              || name.startsWith("com.appdynamics.")
              || name.startsWith("com.singularity.")
              || name.startsWith("com.jinspired.")
              || name.startsWith("com.intellij.rt.debugger.")
              || name.startsWith("com.contrastsecurity.")) {
            return true;
          }
          if (name.startsWith("com.sun.")) {
            return !name.startsWith("com.sun.messaging.")
                && !name.startsWith("com.sun.jersey.api.client");
          }
        }
        if (name.startsWith("clojure.")) {
          return true;
        }
        if (name.startsWith("cinnamon.")) {
          return true;
        }
        if (name.startsWith("co.elastic.apm.")) {
          return true;
        }
        break;
      case 'd' - 'a':
        if (name.startsWith("datadog.")) {
          if (name.startsWith("datadog.opentracing.")
              || name.startsWith("datadog.trace.core.")
              || name.startsWith("datadog.slf4j.")) {
            return true;
          }
          if (name.startsWith("datadog.trace.")) {
            return !name.equals(
                "datadog.trace.bootstrap.instrumentation.java.concurrent.RunnableWrapper");
          }
        }
        break;
      case 'e' - 'a':
        break;
      case 'f' - 'a':
        break;
      case 'g' - 'a':
        break;
      case 'h' - 'a':
        break;
      case 'i' - 'a':
        if (name.startsWith("io.micronaut.tracing.") || name.startsWith("io.micrometer.")) {
          return true;
        }
        break;
      case 'j' - 'a':
        if (name.startsWith("jdk.")) {
          return true;
        }
        if (name.startsWith("java.")) {
          if (name.equals("java.lang.Throwable")) {
            return false;
          }
          if (name.equals("java.net.URL") || name.equals("java.net.HttpURLConnection")) {
            return false;
          }
          if (name.startsWith("java.rmi.") || name.startsWith("java.util.concurrent.")) {
            return false;
          }
          return !name.startsWith("java.util.logging.")
              || name.equals("java.util.logging.LogManager$Cleaner");
        }
        break;
      case 'k' - 'a':
        break;
      case 'l' - 'a':
        break;
      case 'm' - 'a':
        break;
      case 'n' - 'a':
        if (name.startsWith("net.bytebuddy.")) {
          return true;
        }
        break;
      case 'o' - 'a':
        if (name.startsWith("org.")) {
          if (name.startsWith("org.apache.felix.framework.URLHandlers")
              || name.startsWith("org.eclipse.osgi.framework.internal.protocol.")
              || name.startsWith("org.eclipse.osgi.internal.url.")) {
            return true;
          }
          if (name.startsWith("org.aspectj.") || name.startsWith("org.jinspired.")) {
            return true;
          }
          if (name.startsWith("org.groovy.") || name.startsWith("org.apache.groovy.")) {
            return true;
          }
          if (name.startsWith("org.codehaus.groovy.")) {
            return !name.startsWith("org.codehaus.groovy.runtime.");
          }
          if (name.startsWith("org.springframework.")) {
            if (SPRING_CLASSLOADER_IGNORES.contains(name)) {
              return true;
            }
          }
        }
        break;
      case 'p' - 'a':
        break;
      case 'q' - 'a':
        break;
      case 'r' - 'a':
        break;
      case 's' - 'a':
        if (name.startsWith("sun.")) {
          return !name.startsWith("sun.net.www.protocol.")
              && !name.startsWith("sun.rmi.server")
              && !name.startsWith("sun.rmi.transport")
              && !name.equals("sun.net.www.http.HttpClient");
        }
        break;
      default:
    }

    final int firstDollar = name.indexOf('$');
    if (firstDollar > -1) {
      if (name.contains("$JaxbAccessor")
          || name.contains("CGLIB$$")
          || name.contains("$__sisu")
          || name.contains("$$EnhancerByGuice$$")
          || name.contains("$$EnhancerByProxool$$")
          || name.startsWith("org.springframework.core.$Proxy")) {
        return true;
      }
    }
    if (name.contains("javassist") || name.contains(".asm.")) {
      return true;
    }
    return false;
  }

  private static boolean secondaryMatch(String name) {
    if (name.startsWith("com.beust.jcommander.")
        || name.startsWith("com.fasterxml.classmate.")
        || name.startsWith("com.github.mustachejava.")
        || name.startsWith("com.jayway.jsonpath.")
        || name.startsWith("com.lightbend.lagom.")
        || name.startsWith("javax.el.")
        || name.startsWith("net.sf.cglib.")
        || name.startsWith("org.apache.lucene.")
        || name.startsWith("org.apache.tartarus.")
        || name.startsWith("org.json.simple.")
        || name.startsWith("org.yaml.snakeyaml.")) {
      return true;
    }

    if (name.startsWith("org.springframework.")) {
      if ((name.startsWith("org.springframework.aop.")
              && !name.equals("org.springframework.aop.interceptor.AsyncExecutionInterceptor"))
          || name.startsWith("org.springframework.cache.")
          || name.startsWith("org.springframework.dao.")
          || name.startsWith("org.springframework.ejb.")
          || name.startsWith("org.springframework.expression.")
          || name.startsWith("org.springframework.format.")
          || name.startsWith("org.springframework.jca.")
          || name.startsWith("org.springframework.jdbc.")
          || name.startsWith("org.springframework.jmx.")
          || name.startsWith("org.springframework.jndi.")
          || name.startsWith("org.springframework.lang.")
          || name.startsWith("org.springframework.messaging.")
          || name.startsWith("org.springframework.objenesis.")
          || name.startsWith("org.springframework.orm.")
          || name.startsWith("org.springframework.remoting.")
          || name.startsWith("org.springframework.scripting.")
          || name.startsWith("org.springframework.stereotype.")
          || name.startsWith("org.springframework.transaction.")
          || name.startsWith("org.springframework.ui.")
          || name.startsWith("org.springframework.validation.")) {
        return true;
      }

      if (name.startsWith("org.springframework.data.")) {
        if (name.equals("org.springframework.data.repository.core.support.RepositoryFactorySupport")
            || name.startsWith(
                "org.springframework.data.convert.ClassGeneratingEntityInstantiator$")
            || name.equals(
                "org.springframework.data.jpa.repository.config.InspectionClassLoader")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.amqp.")) {
        return false;
      }

      if (name.startsWith("org.springframework.beans.")) {
        if (name.equals("org.springframework.beans.factory.support.DisposableBeanAdapter")
            || name.startsWith(
                "org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader$")
            || name.equals("org.springframework.beans.factory.support.AbstractBeanFactory")
            || name.equals(
                "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory")
            || name.equals(
                "org.springframework.beans.factory.support.DefaultListableBeanFactory")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.boot.")) {
        if (name.startsWith("org.springframework.boot.autoconfigure.BackgroundPreinitializer$")
            || name.startsWith("org.springframework.boot.autoconfigure.condition.OnClassCondition$")
            || name.startsWith("org.springframework.boot.web.embedded.netty.NettyWebServer$")
            || name.startsWith("org.springframework.boot.web.embedded.tomcat.TomcatWebServer$1")
            || name.startsWith(
                "org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer$")
            || name.equals(
                "org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedWebappClassLoader")
            || name.equals(
                "org.springframework.boot.web.embedded.tomcat.TomcatEmbeddedWebappClassLoader")
            || name.equals(
                "org.springframework.boot.context.embedded.EmbeddedWebApplicationContext")
            || name.equals(
                "org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext")
            || name.equals(
                "org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext")
            || name.equals(
                "org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.cglib.")) {
        return !name.startsWith("org.springframework.cglib.core.internal.LoadingCache");
      }

      if (name.startsWith("org.springframework.context.")) {
        if (name.startsWith("org.springframework.context.support.AbstractApplicationContext$")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.core.")) {
        if (name.startsWith("org.springframework.core.task.")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.instrument.")) {
        return true;
      }

      if (name.startsWith("org.springframework.http.")) {
        if (name.startsWith("org.springframework.http.server.reactive.")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.jms.")) {
        if (name.startsWith("org.springframework.jms.listener.")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.util.")) {
        if (name.startsWith("org.springframework.util.concurrent.")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("org.springframework.web.")) {
        if (name.startsWith("org.springframework.web.servlet.")
            || name.startsWith("org.springframework.web.reactive.")
            || name.startsWith("org.springframework.web.context.request.async.")
            || name.equals(
                "org.springframework.web.context.support.AbstractRefreshableWebApplicationContext")
            || name.equals("org.springframework.web.context.support.GenericWebApplicationContext")
            || name.equals("org.springframework.web.context.support.XmlWebApplicationContext")) {
          return false;
        }
        return true;
      }

      return false;
    }

    if (name.startsWith("javax.xml.")
        || name.startsWith("org.apache.bcel.")
        || name.startsWith("org.apache.html.")
        || name.startsWith("org.apache.regexp.")
        || name.startsWith("org.apache.wml.")
        || name.startsWith("org.apache.xalan.")
        || name.startsWith("org.apache.xerces.")
        || name.startsWith("org.apache.xml.")
        || name.startsWith("org.apache.xpath.")
        || name.startsWith("org.xml.")) {
      return true;
    }

    if (name.startsWith("ch.qos.logback.")) {
      if (name.equals("ch.qos.logback.core.AsyncAppenderBase$Worker")) {
        return false;
      }

      if (name.startsWith("ch.qos.logback.classic.spi.LoggingEvent")) {
        return false;
      }

      if (name.equals("ch.qos.logback.classic.Logger")) {
        return false;
      }

      return true;
    }

    if (name.startsWith("org.apache.log4j.")) {
      return !name.equals("org.apache.log4j.MDC")
          && !name.equals("org.apache.log4j.spi.LoggingEvent")
          && !name.equals("org.apache.log4j.Category");
    }

    if (name.startsWith("com.codahale.metrics.")) {
      if (name.startsWith("com.codahale.metrics.servlets.")) {
        return false;
      }
      return true;
    }

    if (name.startsWith("com.couchbase.client.deps.")) {
      if (name.startsWith("com.couchbase.client.deps.io.netty.")
          || name.startsWith("com.couchbase.client.deps.org.LatencyUtils.")
          || name.startsWith("com.couchbase.client.deps.com.lmax.disruptor.")) {
        return false;
      }
      return true;
    }

    if (name.startsWith("com.google.cloud.")
        || name.startsWith("com.google.instrumentation.")
        || name.startsWith("com.google.j2objc.")
        || name.startsWith("com.google.gson.")
        || name.startsWith("com.google.logging.")
        || name.startsWith("com.google.longrunning.")
        || name.startsWith("com.google.protobuf.")
        || name.startsWith("com.google.rpc.")
        || name.startsWith("com.google.thirdparty.")
        || name.startsWith("com.google.type.")) {
      return true;
    }
    if (name.startsWith("com.google.common.")) {
      if (name.startsWith("com.google.common.util.concurrent.")
          || name.equals("com.google.common.base.internal.Finalizer")) {
        return false;
      }
      return true;
    }
    if (name.startsWith("com.google.inject.")) {
      if (name.startsWith("com.google.inject.internal.AbstractBindingProcessor$")
          || name.startsWith("com.google.inject.internal.BytecodeGen$")
          || name.startsWith("com.google.inject.internal.cglib.core.internal.$LoadingCache$")) {
        return false;
      }
      return true;
    }
    if (name.startsWith("com.google.api.")) {
      if (name.startsWith("com.google.api.client.http.HttpRequest")) {
        return false;
      }
      return true;
    }

    if (name.startsWith("org.h2.")) {
      if (name.equals("org.h2.Driver")
          || name.startsWith("org.h2.jdbc.")
          || name.startsWith("org.h2.jdbcx.")
          || name.equals("org.h2.util.Task")
          || name.equals("org.h2.util.MathUtils$1")
          || name.equals("org.h2.store.FileLock")
          || name.equals("org.h2.engine.DatabaseCloser")
          || name.equals("org.h2.engine.OnExitDatabaseCloser")
          || name.equals("org.h2.tools.Server")
          || name.equals("org.h2.store.WriterThread")) {
        return false;
      }
      return true;
    }

    if (name.startsWith("com.carrotsearch.hppc.")) {
      if (name.startsWith("com.carrotsearch.hppc.HashOrderMixing$")) {
        return false;
      }
      return true;
    }

    if (name.startsWith("com.fasterxml.jackson.")) {
      if (name.equals("com.fasterxml.jackson.module.afterburner.util.MyClassLoader")) {
        return false;
      }
      return true;
    }

    if (name.startsWith("kotlin.")) {
      return true;
    }

    if (name.startsWith("scala.collection.")) {
      return true;
    }

    if (name.startsWith("akka.")) {
      if (name.startsWith("akka.http.")) {
        if (name.startsWith("akka.http.scaladsl.")) {
          if (name.equals("akka.http.scaladsl.HttpExt")
              || name.equals("akka.http.scaladsl.Http2Ext")) {
            return false;
          }
          return true;
        }
        if (name.startsWith("akka.http.impl.")) {
          if (name.equals("akka.http.impl.engine.client.PoolMasterActor")
              || name.equals("akka.http.impl.engine.http2.Http2Ext")
              || name.startsWith(
                  "akka.http.impl.engine.server.HttpServerBluePrint$TimeoutAccessImpl$")
              || name.startsWith("akka.http.impl.engine.client.pool.NewHostConnectionPool$")
              || name.startsWith("akka.http.impl.util.StreamUtils$")) {
            return false;
          }
          return true;
        }
      }

      if (name.startsWith("akka.actor.")) {
        if (name.startsWith("akka.actor.LightArrayRevolverScheduler$")
            || name.startsWith("akka.actor.Scheduler$")
            || name.startsWith("akka.actor.ActorSystemImpl$")
            || name.startsWith("akka.actor.CoordinatedShutdown$")
            || name.startsWith("akka.actor.ActorSystem$")
            || name.equals("akka.actor.ActorCell")) {
          return false;
        }
        return true;
      }

      if (name.startsWith("akka.stream.")) {
        if (name.startsWith("akka.stream.impl.fusing.ActorGraphInterpreter$")
            || name.equals("akka.stream.impl.FanOut$SubstreamSubscription")
            || name.equals("akka.stream.impl.FanIn$SubInput")
            || name.startsWith("akka.stream.stage.TimerGraphStageLogic$")
            || name.startsWith("akka.stream.stage.GraphStageLogic$")) {
          return false;
        }
        return true;
      }
    }

    return false;
  }
}
