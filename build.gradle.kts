plugins {
    id("com.diffplug.spotless") version "6.13.0"
    id("com.github.spotbugs") version "5.0.14"
    id("pl.allegro.tech.build.axion-release") version "1.14.4"
}

version = scmVersion.version

subprojects {
    plugins.apply("java")
    plugins.apply("com.diffplug.spotless")
    plugins.apply("com.github.spotbugs")

    project.version = rootProject.version

    repositories {
        mavenCentral()
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
    }

    spotless {
        java {
            target("src/**/*.java")

            importOrder()
            removeUnusedImports()
            googleJavaFormat()
        }
    }
}
