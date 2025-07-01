plugins {
  id("instrument-glue")
  id("instrument-module")
}

// class-inject generates glue bytecode at build-time to provide access to Unsafe.defineClass
// we inline this glue as a string constant, then install it at runtime using instrumentation
extra["glueClass"] = "datadog.instrument.clazz.DefineClassGlue"
