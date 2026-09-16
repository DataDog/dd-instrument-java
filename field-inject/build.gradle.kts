plugins {
  id("java-common")
}

dependencies {
  jmh("com.blogspot.mydailyjava:weak-lock-free:0.18")
}

// include GlobalObjectStore in glue generation, so we can repackage it for injection
sourceSets["glue"].java {
  srcDir("src/main/java")
  include("**/GlobalObjectStore.java")
}

// field-inject generates dispatch + access glue for injection into the bootstrap classpath
extra["glue"] = listOf("ObjectStoreGlue")

tasks.jar {
  // ObjectStoreGlue only contains large string constants that get inlined into FieldInjector
  // - the inlining means we can safely drop ObjectStoreGlue from the final jar to save space
  excludes.add("datadog/instrument/glue/ObjectStoreGlue.class")
}
