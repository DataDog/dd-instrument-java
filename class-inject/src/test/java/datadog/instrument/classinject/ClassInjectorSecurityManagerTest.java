package datadog.instrument.classinject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import datadog.instrument.utils.JVM;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ClassInjectorSecurityManagerTest {
  private static final int TIMEOUT_SECONDS = 20;

  @TempDir Path temporary;

  @Test
  void enablesLookupFromAgentWithSecurityManager() throws Exception {
    assumeTrue(JVM.atLeastJava(15) && !JVM.atLeastJava(24));
    Path agent = temporary.resolve("security-manager-agent.jar");
    Manifest manifest = new Manifest();
    Attributes attributes = manifest.getMainAttributes();
    attributes.putValue("Manifest-Version", "1.0");
    attributes.putValue("Premain-Class", ClassInjectorSecurityManagerProbe.class.getName());
    attributes.putValue("Can-Retransform-Classes", "true");
    try (JarOutputStream ignored = new JarOutputStream(Files.newOutputStream(agent), manifest)) {}

    List<String> command = new ArrayList<>();
    command.add(Paths.get(System.getProperty("java.home"), "bin", "java").toString());
    command.add("-Djava.security.manager=allow");
    command.add("-javaagent:" + agent);
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(ClassInjectorSecurityManagerProbe.class.getName());

    Path output = temporary.resolve("security-manager.log");
    Process process =
        new ProcessBuilder(command)
            .redirectErrorStream(true)
            .redirectOutput(output.toFile())
            .start();
    try {
      assertThat(process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS))
          .withFailMessage("Child JVM timed out after %ds", TIMEOUT_SECONDS)
          .isTrue();
      String log = new String(Files.readAllBytes(output), StandardCharsets.UTF_8);
      assertThat(process.exitValue()).withFailMessage(log).isZero();
    } finally {
      process.destroyForcibly();
      process.waitFor(5, TimeUnit.SECONDS);
    }
  }
}
