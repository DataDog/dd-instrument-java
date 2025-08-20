package datadog.instrument.classmatch;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

final class SampleClasses {
  public static List<byte[]> load(String sampleJar) {
    File sampleJarFile = new File("build/sampleBytecode/" + sampleJar);
    byte[] buf = new byte[16384];
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (JarFile sample = new JarFile(sampleJarFile)) {
      return sample.stream()
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
          .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
