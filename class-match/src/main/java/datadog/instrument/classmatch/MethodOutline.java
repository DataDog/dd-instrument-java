package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.List;

/** Outlines a method; access flags, method name, descriptor, annotations. */
public final class MethodOutline {

  public final int access;
  public final String methodName;
  public final String descriptor;
  final String[] annotations;

  public List<String> annotations() {
    return asList(annotations);
  }

  MethodOutline(int access, String methodName, String descriptor, String[] annotations) {
    this.access = access;
    this.methodName = methodName;
    this.descriptor = descriptor;
    this.annotations = annotations;
  }
}
