/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.testing;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Sample class names, plus bytecode declared using the {@code sampleBytecode} gradle dependency.
 */
public final class SampleClasses {

  /** The directory containing the sample bytecode jars. */
  private static final String SAMPLE_BYTECODE_DIRECTORY = "build/sampleBytecode/";

  /**
   * Loads shared corpus of dotted class names for tests and benchmarks.
   *
   * @return large list of dotted class names
   */
  public static List<String> loadClassNames() {
    try (InputStream in =
            requireNonNull(
                SampleClasses.class.getResourceAsStream("sample_class_names.txt"),
                "sample_class_names.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, UTF_8))) {
      return reader.lines().collect(Collectors.toList());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
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
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
