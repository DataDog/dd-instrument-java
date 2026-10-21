plugins {
  id("java-common")
}

dependencies {
  implementation(project(":utils"))
  testImplementation(libs.asm)

  sampleBytecode(libs.asm.test)
  sampleBytecode("org.springframework:spring-web:7.0.9")
  jmh(project(":testing"))
  jmh(libs.asm)
}
