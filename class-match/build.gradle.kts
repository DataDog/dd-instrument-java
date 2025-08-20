plugins {
  id("java-common")
  id("me.champeau.jmh")
}

// copy sample bytecode jars to a known location
val sampleBytecode: Configuration by configurations.creating
sampleBytecode.isTransitive = false
val copySampleBytecode = tasks.register<Copy>("sampleBytecode") {
  into(layout.buildDirectory.dir("sampleBytecode"))
  from(sampleBytecode)
}
tasks.test { dependsOn(copySampleBytecode) }

dependencies {
  implementation(project(":utils"))

  sampleBytecode("org.ow2.asm:asm-test:9.8")

  jmh(libs.asm)
}

jmh {
  if (!project.file("sample.jar").exists()) {
    excludes.add("ClassFileBenchmark")
    excludes.add("ClassInfoCacheBenchmark")
    excludes.add("ClassNameFilterBenchmark")
    excludes.add("ClassNameTrieBenchmark")
  }
}
