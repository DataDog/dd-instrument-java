/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/** Generates glue for key-value object stores. */
final class ObjectStoreGlueGenerator {

  // location of the original glue source in this module
  private static final Path GLUE_SRC_PATH = Paths.get("src/glue/java/datadog/instrument/glue");

  /**
   * Generates glue for key-value object stores and writes it to the given location.
   *
   * @param resourcePath where to write resource files
   * @param javaPath where to write Java files
   * @throws IOException if the files cannot be written
   * @see GlueGenerator#main
   */
  public static void generateGlue(Path resourcePath, Path javaPath) throws IOException {
    // create a source copy of the global store for the fieldinject package
    Path globalStoreTemplate = GLUE_SRC_PATH.resolve("GlobalObjectStore.java");
    List<String> lines = Files.readAllLines(globalStoreTemplate);
    for (int i = 0; i < lines.size(); i++) {
      if ("package datadog.instrument.glue;".equals(lines.get(i))) {
        lines.set(i, "package datadog.instrument.fieldinject;");
      }
    }
    Files.createDirectories(javaPath.resolveSibling("fieldinject"));
    Files.write(javaPath.resolveSibling("fieldinject/GlobalObjectStore.java"), lines);
  }
}
