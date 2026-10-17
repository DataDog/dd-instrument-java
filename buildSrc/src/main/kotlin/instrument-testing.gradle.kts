plugins {
  id("java-common")
}

val testAgent = configurations.create("testAgent") {
  isCanBeConsumed = false
  isTransitive = false
}

dependencies {
  testImplementation(project(":testing"))
  testAgent(project(":testing"))
}

class TestAgentArguments(@get:Classpath val agentClasspath: FileCollection) : CommandLineArgumentProvider {
  override fun asArguments() = listOf("-javaagent:${agentClasspath.singleFile.absolutePath}")
}

// Supply instrumentation to every test JVM, including the additional Java version tasks.
tasks.withType<Test>().configureEach {
  jvmArgumentProviders.add(TestAgentArguments(testAgent))
}
