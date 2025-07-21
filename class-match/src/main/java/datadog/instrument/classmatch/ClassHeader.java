package datadog.instrument.classmatch;

/** Minimal class header that describes the immediate class hierarchy. */
public class ClassHeader {

  public final String className;
  public final String superName;
  public final String[] interfaces;

  ClassHeader(String className, String superName, String[] interfaces) {
    this.className = className;
    this.superName = superName;
    this.interfaces = interfaces;
  }
}
