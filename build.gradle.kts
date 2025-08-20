plugins {
  java

  id("pl.allegro.tech.build.axion-release")
}

version = scmVersion.version

repositories {
  mavenCentral()
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
