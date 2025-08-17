plugins {
  id("java-multiversion")
  id("me.champeau.jmh")
}

tasks.test {
  forkEvery = 1
}
