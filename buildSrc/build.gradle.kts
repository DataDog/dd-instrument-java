plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
}

dependencies {
  // needed to re-use the 'libs' version catalog between the main project and buildSrc
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.asm.commons)
  implementation(libs.spotless)
  implementation(libs.spotbugs)
  implementation(libs.axion.release)
  implementation(libs.nexus.publish)
  implementation(libs.jmh.plugin)
}
