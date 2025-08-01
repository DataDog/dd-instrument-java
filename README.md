![Datadog logo](https://imgix.datadoghq.com/img/about/presskit/logo-h/dd_horizontal_white.png)

# Datadog Instrumentation Helpers for Java

This repository contains helpers for working with the [Instrumentation](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html) API on Java.

## Features

* `ClassInjector`
  * Supports injection of auxiliary classes, even in the bootstrap class-loader
* `ClassFile`
  * Optimized class-file parser for extracting the header or outline of a class
* `ClassLoaderValue`
  * Lazily associate a computed value with class-loaders, inspired by `ClassValue`

Optimized field injection and class matching features are currently under development.

## Getting Started

* [How to setup your development environment and build the project](BUILDING.md)

