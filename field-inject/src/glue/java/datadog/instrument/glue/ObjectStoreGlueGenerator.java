/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/** Generates glue bytecode for. */
final class ObjectStoreGlueGenerator {

  private ObjectStoreGlueGenerator() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Expected: resources-dir java-dir");
    }

    Files.copy(
        Paths.get("src/glue/java/datadog/instrument/glue/GlobalObjectStore.java"),
        Paths.get(args[1], "GlobalObjectStore.java"),
        REPLACE_EXISTING);

    //    if (args.length < 1 || !args[0].endsWith(".java")) {
    //      throw new IllegalArgumentException("Expected: java-file");
    //    }
    //    File file = new File(args[0]);
    //    String name = file.getName();
    //    List<String> lines = new ArrayList<>();
    //    classHeader(lines, name.substring(0, name.length() - 5));
    //    byte[] buf;
    //    ClassReader cr;
    //    ClassWriter cw;
    //    lines.add("  /**  */");
    //    lines.add("  String OBJECT_STORE_KEY = ");
    //    buf =
    //        Files.readAllBytes(
    //            Paths.get(
    //
    // "/Users/stuart.mcculloch/dd/dd-instrument-java/field-inject/build/classes/java/glue/datadog/instrument/glue/$Datadog$ObjectStore$InjectedKey.class"));
    //    cr = new ClassReader(buf);
    //    cw = new ClassWriter(0);
    //    cr.accept(cw, ClassReader.SKIP_DEBUG);
    //    buf = cw.toByteArray();
    //    packBytecode(lines, buf);
    //    lines.add(";");
    //    lines.add("  /**  */");
    //    lines.add("  String OBJECT_STORE_LOOKUP_KEY = ");
    //    buf =
    //        Files.readAllBytes(
    //            Paths.get(
    //
    // "/Users/stuart.mcculloch/dd/dd-instrument-java/field-inject/build/classes/java/glue/datadog/instrument/glue/$Datadog$ObjectStore$LookupKey.class"));
    //    cr = new ClassReader(buf);
    //    cw = new ClassWriter(0);
    //    cr.accept(cw, ClassReader.SKIP_DEBUG);
    //    buf = cw.toByteArray();
    //    packBytecode(lines, buf);
    //    lines.add(";");
    //    lines.add("  /**  */");
    //    lines.add("  String OBJECT_STORE_WEAK_KEY = ");
    //    buf =
    //        Files.readAllBytes(
    //            Paths.get(
    //
    // "/Users/stuart.mcculloch/dd/dd-instrument-java/field-inject/build/classes/java/glue/datadog/instrument/glue/$Datadog$ObjectStore$GlobalKey.class"));
    //    cr = new ClassReader(buf);
    //    cw = new ClassWriter(0);
    //    cr.accept(cw, ClassReader.SKIP_DEBUG);
    //    buf = cw.toByteArray();
    //    packBytecode(lines, buf);
    //    lines.add(";");
    //    lines.add("  /**  */");
    //    lines.add("  String OBJECT_STORE = ");
    //    buf =
    //        Files.readAllBytes(
    //            Paths.get(
    //
    // "/Users/stuart.mcculloch/dd/dd-instrument-java/field-inject/build/classes/java/glue/datadog/instrument/glue/$Datadog$ObjectStore.class"));
    //    cr = new ClassReader(buf);
    //    cw = new ClassWriter(0);
    //    cr.accept(cw, ClassReader.SKIP_DEBUG);
    //    buf = cw.toByteArray();
    //    packBytecode(lines, buf);
    //    lines.add(";");
    //    lines.add("}");
    //    Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
  }
}
