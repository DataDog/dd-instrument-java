plugins {
  id("java-common")
  id("me.champeau.jmh")
}

jmh {
  if (!project.file("sample.jar").exists()) {
    excludes.add("ClassHeaderBenchmark")
    excludes.add("ClassOutlineBenchmark")
  }
}
