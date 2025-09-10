package datadog.instrument.classmatch;

import static java.util.Arrays.asList;

import java.util.BitSet;
import java.util.List;

/** Outlines a method; access flags, method name, descriptor, annotations. */
public final class MethodOutline {

  private static final int[] NO_PARAMETERS = new int[0];

  public final int access;
  public final String methodName;
  public final String descriptor;
  final String[] annotations;

  private int[] parameterOffsets;

  public List<String> annotations() {
    return asList(annotations);
  }

  MethodOutline(int access, String methodName, String descriptor, String[] annotations) {
    this.access = access;
    this.methodName = methodName;
    this.descriptor = descriptor;
    this.annotations = annotations;
  }

  /** Returns the offsets of each parameter descriptor in the method descriptor string. */
  int[] parameterOffsets() {
    if (parameterOffsets == null) {
      parameterOffsets = parseParameters(descriptor);
    }
    return parameterOffsets;
  }

  /** Parses the method descriptor string to find the offset of each parameter descriptor. */
  private static int[] parseParameters(String descriptor) {
    char c = descriptor.charAt(1);
    if (c == ')') {
      return NO_PARAMETERS; // no parsing required
    }
    BitSet params = new BitSet();
    params.set(1); // first parameter always starts at offset 1 after the '('
    try {
      int i = 1;
      while (true) {
        while (c == '[') {
          // skip over array marker(s)
          c = descriptor.charAt(++i);
        }
        if (c == 'L') {
          // skip over class-name
          i = descriptor.indexOf(';', i + 2);
          if (i < 0) {
            break; // malformed descriptor; short-circuit parsing
          }
        }
        // either reached next parameter or end of parameters
        c = descriptor.charAt(++i);
        if (c == ')') {
          break; // end of parameters
        }
        // record start of next parameter
        params.set(i);
      }
    } catch (IndexOutOfBoundsException e) {
      // malformed descriptor; short-circuit parsing
    }
    // flatten bit-set into primitive array of offsets
    int[] offsets = new int[params.cardinality()];
    for (int i = 0, p = 0, len = offsets.length; i < len; i++, p++) {
      offsets[i] = p = params.nextSetBit(p);
    }
    return offsets;
  }
}
