plugins {
  java
  `maven-publish`
  signing
  id("com.gradleup.shadow")
  id("pl.allegro.tech.build.axion-release")
  id("io.github.gradle-nexus.publish-plugin")
}

scmVersion {
  versionCreator("simple")
}

group = "com.datadoghq"
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

  implementation(libs.asm)

  compileOnly(libs.spotbugs.annotations)
}

// collect all subproject output into a single jar
fun allSources(): List<SourceDirectorySet> {
  return subprojects.filter { it.name != "testing" }.map { it.sourceSets.main.get().allSource }
}
tasks.jar {
  dependsOn(embed)
  from(embed.map { zipTree(it) })
}
tasks.javadoc {
  dependsOn(embed)
  setSource(allSources())
  exclude("datadog/instrument/glue", "datadog/instrument/utils/JVM.java")
  var javadocOptions = (options as StandardJavadocDocletOptions)
  if (JavaVersion.current().isJava9Compatible) {
    javadocOptions.addBooleanOption("html5", true)
  }
  javadocOptions.addStringOption("-source-path",
    allSources().joinToString(File.pathSeparator) { it.sourceDirectories.asPath })
}
tasks.named<Jar>("sourcesJar") {
  dependsOn(embed)
  from(allSources())
}

// produce "-all" jar with a shaded copy of ASM
tasks.shadowJar {
  dependsOn(embed)
  from(embed.map { zipTree(it) })
  relocate("org.objectweb.asm", "datadog.instrument.asm")
}
tasks.assemble {
  dependsOn(tasks.shadowJar)
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
      pom {
        name = project.name
        description = "Datadog Instrumentation Helpers for Java"
        url = "https://github.com/datadog/dd-instrument-java"
        licenses {
          license {
            name = "The Apache Software License, Version 2.0"
            url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            distribution = "repo"
          }
        }
        developers {
          developer {
            id = "datadog"
            name = "Datadog"
          }
        }
        scm {
          connection = "scm:https://datadog@github.com/datadog/dd-instrument-java"
          developerConnection = "scm:git@github.com:datadog/dd-instrument-java.git"
          url = "https://github.com/datadog/dd-instrument-java"
        }
        withXml {
          // mark ASM dependency as optional
          var doc = asElement().ownerDocument
          var deps = doc.getElementsByTagName("dependency")
          for (i in 0 ..< deps.length) {
            var optional = doc.createElement("optional")
            optional.textContent = "true"
            deps.item(i).appendChild(optional)
          }
        }
      }
    }
  }
}

signing {
  isRequired = providers.environmentVariable("GPG_PRIVATE_KEY").isPresent && providers.environmentVariable("GPG_PASSWORD").isPresent
  useInMemoryPgpKeys(providers.environmentVariable("GPG_PRIVATE_KEY").orNull, providers.environmentVariable("GPG_PASSWORD").orNull)
  sign(publishing.publications["maven"])
}

nexusPublishing {
  repositories {
    // see https://github.com/gradle-nexus/publish-plugin#publishing-to-maven-central-via-sonatype-central
    // For official documentation:
    // staging repo publishing https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
    // snapshot publishing https://central.sonatype.org/publish/publish-portal-snapshots/#publishing-via-other-methods
    sonatype {
      nexusUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/")
      snapshotRepositoryUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
      username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME")
      password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD")
    }
  }
}
