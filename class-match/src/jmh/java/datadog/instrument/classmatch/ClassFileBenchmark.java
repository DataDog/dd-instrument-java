package datadog.instrument.classmatch;

import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.stream.Collectors.toList;
import static org.objectweb.asm.ClassReader.*;
import static org.objectweb.asm.Opcodes.ASM9;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
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
public class ClassFileBenchmark {

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
  public void testAsmHeader(Blackhole blackhole) {
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
  public void testAsmOutline(Blackhole blackhole) {
    for (byte[] bytecode : bytecodes) {
      OutlineVisitor outline = new OutlineVisitor(ASM9);
      new ClassReader(bytecode).accept(outline, SKIP_CODE | SKIP_DEBUG | SKIP_FRAMES);
      blackhole.consume(outline.access);
      blackhole.consume(outline.className);
      blackhole.consume(outline.superName);
      blackhole.consume(outline.interfaces);
      blackhole.consume(outline.fields);
      blackhole.consume(outline.methods);
      blackhole.consume(outline.annotations);
    }
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 1)
  public void testClassHeader(Blackhole blackhole) {
    for (byte[] bytecode : bytecodes) {
      ClassHeader header = ClassFile.header(bytecode);
      blackhole.consume(header.className);
      blackhole.consume(header.superName);
      blackhole.consume(header.interfaces);
    }
  }

  @Benchmark
  @Fork(value = 1)
  @Threads(value = 1)
  public void testClassOutline(Blackhole blackhole) {
    for (byte[] bytecode : bytecodes) {
      ClassOutline outline = ClassFile.outline(bytecode);
      blackhole.consume(outline.access);
      blackhole.consume(outline.className);
      blackhole.consume(outline.superName);
      blackhole.consume(outline.interfaces);
      blackhole.consume(outline.fields);
      blackhole.consume(outline.methods);
      blackhole.consume(outline.annotations);
    }
  }

  static final class OutlineVisitor extends ClassVisitor {

    private static final String[] NO_ANNOTATIONS = {};

    public int access;
    public String className;
    public String superName;
    public String[] interfaces;

    public final List<FieldOutline> fields = new ArrayList<>();
    public final List<MethodOutline> methods = new ArrayList<>();
    public final List<String> annotations = new ArrayList<>();

    OutlineVisitor(int api) {
      super(api);
    }

    @Override
    public void visit(
        int version,
        int access,
        String name,
        String signature,
        String superName,
        String[] interfaces) {
      this.access = access;
      this.className = name;
      this.superName = superName;
      this.interfaces = interfaces;
      super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(
        int access, String name, String descriptor, String signature, Object value) {
      fields.add(new FieldOutline(access, name, descriptor, NO_ANNOTATIONS));
      return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(
        int access, String name, String descriptor, String signature, String[] exceptions) {
      methods.add(new MethodOutline(access, name, descriptor, NO_ANNOTATIONS));
      return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
      annotations.add(descriptor);
      return super.visitAnnotation(descriptor, visible);
    }
  }
}
