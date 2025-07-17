package datadog.instrument.utils;

import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class ClassLoaderIndexBenchmark extends AbstractClassLoaderBenchmark {
  ToIntFunction<ClassLoader> classLoaderFunction() {
    return ClassLoaderIndex::getClassLoaderKeyId;
  }
}
