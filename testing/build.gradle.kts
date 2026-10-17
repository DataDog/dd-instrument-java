plugins {
  id("java-common")
}

// Allow test JVMs to obtain instrumentation without dynamic agent attachment.
tasks.jar {
  manifest {
    attributes(
      "Premain-Class" to "datadog.instrument.testing.TestAgent",
      "Can-Retransform-Classes" to "true")
  }
}
