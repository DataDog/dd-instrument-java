![Datadog logo](https://imgix.datadoghq.com/img/about/presskit/logo-h/dd_horizontal_white.png)

# Datadog Instrumentation Helpers for Java

This repository contains helpers for working with the [Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html) API on Java.

## Features

* [`ClassInjector`](class-inject/src/main/java/datadog/instrument/classinject/ClassInjector.java)
  * Supports injection of auxiliary classes, even in the bootstrap class-loader
* [`ClassFile`](class-match/src/main/java/datadog/instrument/classmatch/ClassFile.java)
  * Optimized class-file parser for extracting the header or outline of a class
* [`ClassLoaderValue`](utils/src/main/java/datadog/instrument/utils/ClassLoaderValue.java)
  * Lazily associate a computed value with class-loaders, inspired by [`ClassValue`](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassValue.html)

Optimized field injection and class matching features are currently under development.

## Getting Started

* [How to setup your development environment and build the project](BUILDING.md)

  

