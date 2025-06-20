import org.gradle.api.JavaVersion.VERSION_1_8

plugins {
  id("java-common")
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
