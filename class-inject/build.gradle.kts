plugins {
  id("instrument-module")
}

// class-inject generates glue bytecode at build-time to provide access to Unsafe.defineClass
// we inline this glue as a string constant, then install it at runtime using instrumentation

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
}

tasks.register<JavaExec>("generateGlue") {
  val pkg = "datadog.instrument.clazz"
  val clz = "DefineClassGlue"

  val srcFile = generatedGlueDir.file("${pkg.replace('.', '/')}/${clz}.java")

  group = "Build"
  description = "Generate glue around Unsafe.defineClass"
  mainClass = "datadog.instrument.glue.DefineClassGlueGenerator"
  classpath = sourceSets["glue"].runtimeClasspath
  args = listOf(srcFile.toString(), pkg, clz)
  outputs.files(srcFile)
}

tasks.compileJava {
  dependsOn(tasks.named("generateGlue"))
}

tasks.jar {
  // this class only contains large string constants that get inlined into other classes
  // - the inlining means we can safely drop this class from the final jar to save space
  excludes.add("datadog/instrument/clazz/DefineClassGlue.class")
}
