plugins {
  id("java-common")
}

dependencies {
  implementation(project(":utils"))

  sampleBytecode("org.ow2.asm:asm-test:9.9.1")
  sampleBytecode("org.springframework:spring-web:7.0.7")
  jmh(project(":testing"))
  jmh(libs.asm)
}
