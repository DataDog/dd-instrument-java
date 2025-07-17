package datadog.instrument.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class ClassLoaderValueBenchmark extends AbstractClassLoaderBenchmark {
  ToIntFunction<ClassLoader> classLoaderFunction() {
    AtomicInteger nextId = new AtomicInteger();
    ClassLoaderValue<Integer> classLoaderTestIds =
        new ClassLoaderValue<Integer>() {
          @Override
          protected Integer computeValue(ClassLoader cl) {
            return nextId.getAndIncrement();
          }
        };
    return classLoaderTestIds::get;
  }
}
