plugins {
  id("java-common")
}

dependencies {
  jmh (group = "com.blogspot.mydailyjava", name = "weak-lock-free", version = "0.18")
}
