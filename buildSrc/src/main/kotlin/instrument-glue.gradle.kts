val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  id("java-common")
}

val generatedGlueDir = layout.buildDirectory.dir("generated/glue/java/").get()
sourceSets {
  create("glue") {
    output.dir(generatedGlueDir)
  }
  main {
    java.srcDir(generatedGlueDir)
  }
}

val glueImplementation by configurations
dependencies {
  glueImplementation(libs.asm)
  glueImplementation(project(":utils"))
}

tasks.register<JavaExec>("generateGlue") {
  val glue: String by project.extra

  val javaFile = generatedGlueDir.file("datadog/instrument/glue/${glue}.java")

  group = "Build"
  description = "Generate ${glue}"
  mainClass = "datadog.instrument.glue.${glue}Generator"
  classpath = sourceSets["glue"].runtimeClasspath
  args = listOf(javaFile.toString())
  outputs.files(javaFile)
}

tasks.compileJava {
  dependsOn(tasks.named("generateGlue"))
}
tasks.named("sourcesJar") {
  dependsOn(tasks.named("generateGlue"))
}

tasks.jar {
  // glue classes only contain large string constants that get inlined into other classes
  // - the inlining means we can safely drop these classes from the final jar to save space
  excludes.add("datadog/instrument/glue/**")
}
