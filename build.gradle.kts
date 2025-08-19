plugins {
  java

  id("pl.allegro.tech.build.axion-release")
}

version = scmVersion.version

repositories {
  mavenCentral()
}

// collect all subproject output into a single jar
val embed by configurations.creating {
  isTransitive = false
}
dependencies {
  embed(project(":utils"))
  embed(project(":class-inject"))
  embed(project(":class-match"))
}
tasks.jar {
  dependsOn(embed)
  from(embed.map { zipTree(it) })
}
