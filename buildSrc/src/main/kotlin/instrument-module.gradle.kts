val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
  id("java-common")
}

dependencies {
  implementation(libs.asm)
  implementation(project(":utils"))
}
