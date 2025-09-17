plugins {
  java

  id("pl.allegro.tech.build.axion-release")
}

version = scmVersion.version

repositories {
  mavenCentral()
}

java {
  withJavadocJar()
  withSourcesJar()
}

val embed by configurations.creating {
  isTransitive = false
}

dependencies {
  embed(project(":utils"))
  embed(project(":class-inject"))
  embed(project(":class-match"))
}

// collect all subproject output into a single jar
tasks.jar {
  dependsOn(embed)
  from(embed.map { zipTree(it) })
}
tasks.javadoc {
  dependsOn(embed)
  setSource(subprojects.map { it.sourceSets.main.get().allSource })
  classpath = files(subprojects.flatMap { it.sourceSets.main.get().compileClasspath })
  if (JavaVersion.current().isJava9Compatible) {
    (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
  }
}
tasks.named<Jar>("sourcesJar") {
  dependsOn(embed)
  from(subprojects.map { it.sourceSets.main.get().allSource })
}
