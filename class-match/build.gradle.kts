plugins {
  id("java-common")
  id("me.champeau.jmh")
}

dependencies {
  implementation(project(":utils"))

  jmh(libs.asm)
}

jmh {
  if (!project.file("sample.jar").exists()) {
    excludes.add("ClassFileBenchmark")
    excludes.add("ClassInfoCacheBenchmark")
  }
}
