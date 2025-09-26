val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  java
  jacoco

  id("me.champeau.jmh")
  id("com.diffplug.spotless")
  id("com.github.spotbugs")
  id("pl.allegro.tech.build.axion-release")
}

repositories {
  mavenCentral()
}

version = rootProject.version

dependencies {
  compileOnly(libs.spotbugs.annotations)
  testCompileOnly(libs.spotbugs.annotations)
  testImplementation(libs.junit.jupiter)
  testImplementation(libs.assertj.core)
  testRuntimeOnly(libs.junit.launcher)
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
  withJavadocJar()
  withSourcesJar()
}
tasks.withType<JavaCompile>().configureEach {
  options.release = 8
}

tasks.javadoc {
  if (JavaVersion.current().isJava9Compatible) {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
  }
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

val additionalJavaVersions = listOf(8, 11, 21, 25)
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
  reports {
    csv.required = true
    xml.required = true
  }
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

// dependency configuration to help pull sample bytecode in for testing
val sampleBytecode by configurations.creating {
  isTransitive = false
}

// copy sample bytecode jars to a known location for testing/benchmarking
val sampleBytecodeDir = layout.buildDirectory.dir("sampleBytecode")
val copySampleBytecode = tasks.register<Copy>("sampleBytecode") {
  val versionJarSuffix = "-[0-9.]*\\.jar$".toRegex()
  rename { name -> name.replace(versionJarSuffix, ".jar") }
  into(sampleBytecodeDir)
  from(sampleBytecode)
}
tasks.test { dependsOn(copySampleBytecode) }
tasks.jmh { dependsOn(copySampleBytecode) }
