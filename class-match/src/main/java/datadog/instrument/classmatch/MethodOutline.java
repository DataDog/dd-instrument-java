package datadog.instrument.classmatch;

/** Outlines a method; access flags, method name, descriptor, annotations. */
public final class MethodOutline {

  public final int access;
  public final String methodName;
  public final String descriptor;
  public final String[] annotations;

  MethodOutline(int access, String methodName, String descriptor, String[] annotations) {
    this.access = access;
    this.methodName = methodName;
    this.descriptor = descriptor;
    this.annotations = annotations;
  }
}
