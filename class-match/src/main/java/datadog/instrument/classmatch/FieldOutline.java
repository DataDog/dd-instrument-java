package datadog.instrument.classmatch;

/** Outlines a field; access flags, field name, descriptor, annotations. */
public final class FieldOutline {

  public final int access;
  public final String fieldName;
  public final String descriptor;
  public final String[] annotations;

  FieldOutline(int access, String fieldName, String descriptor, String[] annotations) {
    this.access = access;
    this.fieldName = fieldName;
    this.descriptor = descriptor;
    this.annotations = annotations;
  }
}
