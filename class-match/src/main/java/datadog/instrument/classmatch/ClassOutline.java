package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.List;

/** Outlines a class; access flags, immediate class hierarchy, field, methods, annotations. */
public final class ClassOutline extends ClassHeader {

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
    super(access, className, superName, interfaces);
    this.fields = fields;
    this.methods = methods;
    this.annotations = annotations;
  }
}
