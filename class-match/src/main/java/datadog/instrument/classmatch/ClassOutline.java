package datadog.instrument.classmatch;

public final class ClassOutline extends ClassHeader {

  public final int access;
  public final FieldOutline[] fields;
  public final MethodOutline[] methods;
  public final String[] annotations;

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
