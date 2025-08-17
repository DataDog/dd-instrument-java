val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  java
  jacoco

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
    languageVersion = JavaLanguageVersion.of(17)
  }
}
tasks.withType<JavaCompile>().configureEach {
  options.release = 8
}

dependencies {
  compileOnly(libs.spotbugs.annotations)
  testCompileOnly(libs.spotbugs.annotations)
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.launcher)
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

val additionalJavaVersions = listOf(8, 11, 21)
for (javaVersion in additionalJavaVersions) {
  val testOnX = tasks.register<Test>("testOn${javaVersion}") {
    javaLauncher = javaToolchains.launcherFor {
      languageVersion = JavaLanguageVersion.of(javaVersion)
    }
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
    dependsOn(tasks.test)
  }
  tasks.check { dependsOn(testOnX) }
}

tasks.jacocoTestReport {
  executionData.setFrom(fileTree(layout.buildDirectory).include("/jacoco/*.exec"))
}
tasks.check { finalizedBy(tasks.jacocoTestReport) }

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
