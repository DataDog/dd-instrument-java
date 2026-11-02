package datadog.instrument.classmatch;

import datadog.instrument.testing.SampleClasses;
import java.util.LinkedHashMap;
import java.util.Map;

/** Same application corpus as {@link MatchingApiBenchmark}: Spring-stack jars plus fixtures. */
final class PatternCorpus {
  private PatternCorpus() {}

  static byte[][] bytecode() {
    Map<String, byte[]> corpus = new LinkedHashMap<>();
    for (String jar :
        new String[] {
          "spring-web.jar",
          "spring-webmvc.jar",
          "spring-webflux.jar",
          "spring-core.jar",
          "spring-beans.jar",
          "spring-context.jar",
          "spring-aop.jar",
          "jackson-core.jar",
          "jackson-databind.jar",
          "reactor-core.jar",
          "tomcat-embed-core.jar",
          "httpclient5.jar",
          "httpcore5.jar"
        }) {
      for (byte[] bytecode : SampleClasses.loadBytecode(jar)) {
        String name = ClassFile.header(bytecode).className.replace('/', '.');
        if (!name.equals("module-info")) {
          corpus.putIfAbsent(name, bytecode);
        }
      }
    }
    ControllerFixtures.addTo(corpus);
    SelectionFixtures.addTo(corpus);
    return corpus.values().toArray(new byte[0][]);
  }
}
