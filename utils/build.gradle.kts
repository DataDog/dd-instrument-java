import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
  id("java-common")
  id("me.champeau.jmh")
  id("idea")
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(11)
  }
  sourceCompatibility = VERSION_1_8
  targetCompatibility = VERSION_1_8
}

idea {
  module {
    jdkName = "11"
  }
}

jmh {
  if (!project.file("sample.jar").exists()) {
    excludes.add("ClassHeaderBenchmark")
  }
}
