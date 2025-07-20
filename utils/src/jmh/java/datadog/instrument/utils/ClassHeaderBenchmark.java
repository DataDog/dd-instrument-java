package datadog.instrument.utils;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.stream.Collectors.toList;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import org.objectweb.asm.ClassReader;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
@BenchmarkMode(AverageTime)
@OutputTimeUnit(MICROSECONDS)
@SuppressWarnings("unused")
public class ClassHeaderBenchmark {

  private List<byte[]> bytecodes;

  @Setup(Level.Trial)
  public void setup() {
    File sampleJarFile = new File("sample.jar");
    byte[] buf = new byte[16384];
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (JarFile sample = new JarFile(sampleJarFile)) {
      bytecodes =
          sample.stream()
              .filter(e -> e.getName().endsWith(".class"))
              .map(
                  e -> {
                    out.reset();
                    try (InputStream in = sample.getInputStream(e)) {
                      int nRead;
                      while ((nRead = in.read(buf, 0, buf.length)) != -1) {
                        out.write(buf, 0, nRead);
                      }
                      return out.toByteArray();
                    } catch (IOException ignore) {
                      return null;
                    }
                  })
              .filter(Objects::nonNull)
              .collect(toList());
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 1)
  public void testClassReader(Blackhole blackhole) {
    for (byte[] bytecode : bytecodes) {
      ClassReader reader = new ClassReader(bytecode);
      blackhole.consume(reader.getClassName());
      blackhole.consume(reader.getSuperName());
      blackhole.consume(reader.getInterfaces());
    }
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 1)
  public void testClassHeader(Blackhole blackhole) {
    for (byte[] bytecode : bytecodes) {
      ClassHeader header = ClassHeader.parse(bytecode);
      blackhole.consume(header.className);
      blackhole.consume(header.superName);
      blackhole.consume(header.interfaces);
    }
  }
}
