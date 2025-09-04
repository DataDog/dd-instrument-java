package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.List;

/** Outlines a class; immediate class hierarchy, access flags, field, methods, annotations. */
public final class ClassOutline extends ClassHeader {

  public final int access;
  final FieldOutline[] fields;
  final MethodOutline[] methods;
  final String[] annotations;

  public List<FieldOutline> fields() {
    return asList(fields);
  }

  public List<MethodOutline> methods() {
    return asList(methods);
  }

  public List<String> annotations() {
    return asList(annotations);
  }

  ClassOutline(
      int access,
      String className,
      String superName,
      String[] interfaces,
      FieldOutline[] fields,
      MethodOutline[] methods,
      String[] annotations) {
    super(className, superName, interfaces);
    this.access = access;
    this.fields = fields;
    this.methods = methods;
    this.annotations = annotations;
  }
}
