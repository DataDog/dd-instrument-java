plugins {
  id("java-multiversion")
}

dependencies {
  jmh(project(":testing"))
}

tasks.withType<Test>().configureEach {
  forkEvery = 1
}
