plugins {
  id("java-multiversion")
}

dependencies {
  sampleBytecode("org.springframework:spring-web:6.2.8")
  jmh(project(":testing"))
}

tasks.withType<Test>().configureEach {
  forkEvery = 1
}
