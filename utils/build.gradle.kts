plugins {
  id("java-multiversion")
}

tasks.withType<Test>().configureEach {
  forkEvery = 1
}
