plugins {
  id("java-common")
  id("me.champeau.jmh")
}

dependencies {
  jmh(libs.asm)
}

jmh {
  if (!project.file("sample.jar").exists()) {
    excludes.add("ClassFileBenchmark")
  }
}
