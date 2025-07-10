val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  java

  id("com.diffplug.spotless")
  id("com.github.spotbugs")
  id("pl.allegro.tech.build.axion-release")
}

repositories {
  mavenCentral()
}

version = rootProject.version

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(8)
  }
}

dependencies {
  compileOnly(libs.spotbugs.annotations)
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.launcher)
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

spotless {
  java {
    target("src/**/*.java")

    importOrder()
    removeUnusedImports()
    googleJavaFormat()
  }
}

spotbugs {
  useJavaToolchains = true

  omitVisitors = listOf("FindReturnRef")
}
