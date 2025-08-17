plugins {
  id("java-multiversion")
  id("me.champeau.jmh")
}

tasks.withType<Test>().configureEach {
  forkEvery = 1
}
