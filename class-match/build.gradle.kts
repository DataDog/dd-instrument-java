plugins {
  id("java-common")
}

dependencies {
  implementation(project(":utils"))

  sampleBytecode("org.ow2.asm:asm-test:9.8")
  sampleBytecode("org.springframework:spring-web:6.2.8")
  jmh(project(":testing"))
  jmh(libs.asm)
}
