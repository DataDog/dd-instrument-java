plugins {
  id("java-multiversion")
}

dependencies {
  sampleBytecode("org.springframework:spring-web:7.0.7")
  jmh(project(":testing"))
}

tasks.withType<Test>().configureEach {
  forkEvery = 1
}
