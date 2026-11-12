plugins {
  id("java-common")
}

dependencies {
  implementation(project(":utils"))
  testImplementation(libs.asm)

  sampleBytecode(libs.asm.test)
  sampleBytecode("org.springframework:spring-web:7.0.10")
  sampleBytecode("org.springframework:spring-webmvc:7.0.10")
  sampleBytecode("org.springframework:spring-webflux:7.0.10")
  // other jars commonly found in Spring applications
  sampleBytecode("org.springframework:spring-core:7.0.10")
  sampleBytecode("org.springframework:spring-beans:7.0.10")
  sampleBytecode("org.springframework:spring-context:7.0.10")
  sampleBytecode("org.springframework:spring-aop:7.0.10")
  sampleBytecode("com.fasterxml.jackson.core:jackson-core:2.23.0")
  sampleBytecode("com.fasterxml.jackson.core:jackson-databind:2.23.0")
  sampleBytecode("io.projectreactor:reactor-core:3.8.8")
  sampleBytecode("org.apache.tomcat.embed:tomcat-embed-core:11.0.27")
  sampleBytecode("org.apache.httpcomponents.client5:httpclient5:5.6.5")
  sampleBytecode("org.apache.httpcomponents.core5:httpcore5:5.4.4")
  jmh(project(":testing"))
  jmh(libs.asm)
  jmh(libs.byte.buddy)
}
