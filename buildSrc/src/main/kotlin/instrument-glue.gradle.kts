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
  val glueClass: String by project.extra

  val gluePackage = glueClass.substringBeforeLast('.')
  val glueName = glueClass.substringAfterLast('.')

  val srcFile = generatedGlueDir.file("${glueClass.replace('.', '/')}.java")

  group = "Build"
  description = "Generate glue"
  mainClass = "datadog.instrument.glue.${glueName}Generator"
  classpath = sourceSets["glue"].runtimeClasspath
  args = listOf(srcFile.toString(), gluePackage, glueName)
  outputs.files(srcFile)
}

tasks.compileJava {
  dependsOn(tasks.named("generateGlue"))
}

tasks.jar {
  val glueClass: String by project.extra

  // glue classes only contain large string constants that get inlined into other classes
  // - the inlining means we can safely drop these classes from the final jar to save space
  excludes.add("${glueClass.replace('.', '/')}.class")
}
