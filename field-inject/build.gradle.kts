plugins {
  id("instrument-glue")
  id("instrument-module")
}

// field-inject generates build-time glue defining an ObjectStore API around injected fields
extra["glue"] = "ObjectStoreGlue"
