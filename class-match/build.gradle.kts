plugins {
  id("java-common")
  id("me.champeau.jmh")
}

val sampleBytecode by configurations.creating {
  isTransitive = false
}

dependencies {
  implementation(project(":utils"))

  sampleBytecode("org.ow2.asm:asm-test:9.8")
  sampleBytecode("org.springframework:spring-web:6.2.8")

  jmh(libs.asm)
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
