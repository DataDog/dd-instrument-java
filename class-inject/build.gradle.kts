plugins {
  id("instrument-glue")
  id("instrument-module")
}

// class-inject generates glue bytecode at build-time to provide access to Unsafe.defineClass
// we inline this glue as a string constant, then install it at runtime using instrumentation
extra["glue"] = listOf("DefineClassGlue")

tasks.jar {
  // DefineClassGlue only contains large string constants that get inlined into ClassInjector
  // - the inlining means we can safely drop DefineClassGlue from the final jar to save space
  excludes.add("datadog/instrument/glue/DefineClassGlue.class")
}
