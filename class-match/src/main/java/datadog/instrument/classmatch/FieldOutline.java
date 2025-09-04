package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.List;

/** Outlines a field; access flags, field name, descriptor, annotations. */
public final class FieldOutline {

  public final int access;
  public final String fieldName;
  public final String descriptor;
  final String[] annotations;

  public List<String> annotations() {
    return asList(annotations);
  }

  FieldOutline(int access, String fieldName, String descriptor, String[] annotations) {
    this.access = access;
    this.fieldName = fieldName;
    this.descriptor = descriptor;
    this.annotations = annotations;
  }
}
