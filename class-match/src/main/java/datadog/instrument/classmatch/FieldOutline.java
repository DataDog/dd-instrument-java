package datadog.instrument.classmatch;

/** Outlines a field; access flags, field name, descriptor. */
public final class FieldOutline {

  public final int access;
  public final String fieldName;
  public final String descriptor;

  FieldOutline(int access, String fieldName, String descriptor) {
    this.access = access;
    this.fieldName = fieldName;
    this.descriptor = descriptor;
  }
}
