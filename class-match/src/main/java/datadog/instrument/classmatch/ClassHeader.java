package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.List;

/** Minimal class header that describes its access flags and immediate class hierarchy. */
public class ClassHeader {

  public final int access;
  public final String className;
  public final String superName;
  final String[] interfaces;

  public List<String> interfaces() {
    return asList(interfaces);
  }

  ClassHeader(int access, String className, String superName, String[] interfaces) {
    this.access = access;
    this.className = className;
    this.superName = superName;
    this.interfaces = interfaces;
  }
}
