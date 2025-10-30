val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  id("java-common")
}

val generatedGlueResources = layout.buildDirectory.dir("generated/glue/resources/").get()
val generatedGlueJava = layout.buildDirectory.dir("generated/glue/java/").get()
sourceSets {
  create("glue") {
    output.dir(generatedGlueResources)
    output.dir(generatedGlueJava)
  }
  main {
    resources.srcDir(generatedGlueResources)
    java.srcDir(generatedGlueJava)
  }
}

val glueImplementation by configurations
dependencies {
  glueImplementation(libs.asm)
  glueImplementation(libs.spotbugs.annotations)
  glueImplementation(project(":utils"))
}

tasks.register<JavaExec>("generateGlue") {
  val glue: List<String> by project.extra

  // generate glue files under a consistent packaging location
  val resourcePath = generatedGlueResources.dir("datadog/instrument/glue/")
  val javaPath = generatedGlueJava.dir("datadog/instrument/glue/")

  group = "Build"
  description = "Generate Instrumentation Glue"
  mainClass = "datadog.instrument.glue.GlueGenerator"
  classpath = sourceSets["glue"].runtimeClasspath
  args = listOf(resourcePath.toString(), javaPath.toString()) + glue
  outputs.dirs(resourcePath, javaPath)
}

tasks.processResources {
  dependsOn(tasks.named("generateGlue"))
}
tasks.compileJava {
  dependsOn(tasks.named("generateGlue"))
}
tasks.named("sourcesJar") {
  dependsOn(tasks.named("generateGlue"))
}
