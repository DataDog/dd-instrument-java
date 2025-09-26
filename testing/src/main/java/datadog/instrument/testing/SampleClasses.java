/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.testing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Provides access to external bytecode declared using the {@code sampleBytecode} gradle dependency.
 */
public final class SampleClasses {

  /** The directory containing the sample bytecode jars. */
  public static final String SAMPLE_BYTECODE_DIRECTORY = "build/sampleBytecode/";

  /**
   * Finds all class names in the named sample bytecode jar.
   *
   * @param sampleJar the name of the sample jar (without version)
   * @return list of class names contained in the sample jar
   */
  public static List<String> loadClassNames(String sampleJar) {
    File sampleJarFile = new File(SAMPLE_BYTECODE_DIRECTORY + sampleJar);
    try (JarFile sample = new JarFile(sampleJarFile)) {
      return sample.stream()
          .filter(e -> e.getName().endsWith(".class"))
          .map(e -> e.getName().replace(".class", "").replace('/', '.'))
          .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  /**
   * Loads all class bytecode from the named sample bytecode jar.
   *
   * @param sampleJar the name of the sample jar (without version)
   * @return list of bytecode contained in the sample jar
   */
  public static List<byte[]> loadBytecode(String sampleJar) {
    File sampleJarFile = new File(SAMPLE_BYTECODE_DIRECTORY + sampleJar);
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
