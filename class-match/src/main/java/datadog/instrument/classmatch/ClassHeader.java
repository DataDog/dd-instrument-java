package datadog.instrument.classmatch;

import static java.nio.charset.StandardCharsets.US_ASCII;

/** Minimal class header that describes the immediate class hierarchy. */
public final class ClassHeader {

  public final String className;
  public final String superName;
  public final String[] interfaces;

  ClassHeader(String className, String superName, String[] interfaces) {
    this.className = className;
    this.superName = superName;
    this.interfaces = interfaces;
  }

  private static final String[] NO_INTERFACES = {};

  /** Class-file parser optimized to only extract the class header. */
  public static ClassHeader parse(byte[] bytecode) {
    // skip preamble
    int cursor = 8;

    int cpLen = u2(bytecode, cursor);
    cursor += 2;

    // loop over constant pool, capturing only UTF8 offsets and class indices
    int[] cp = new int[cpLen];
    for (int i = 1; i < cpLen; i++) {
      int tag = bytecode[cursor++];
      // all entries are at least two bytes long; add that at end of each loop
      if (tag == 1) { // CONSTANT_Utf8
        // record start of the UTF8 bytes
        cp[i] = cursor;
        // skip over the actual UTF8 bytes
        cursor += u2(bytecode, cursor);
      } else if (tag == 7) { // CONSTANT_Class
        // record CP index of class name
        cp[i] = u2(bytecode, cursor);
      } else {
        switch (tag) {
          case 8: // CONSTANT_String
          case 16: // CONSTANT_MethodType
          case 19: // CONSTANT_Module
          case 20: // CONSTANT_Package
            // entries that are exactly two bytes, excluding tag
            break;
          case 15: // CONSTANT_MethodHandle
            cursor++;
            break;
          case 3: // CONSTANT_Integer
          case 4: // CONSTANT_Float
          case 9: // CONSTANT_Fieldref
          case 10: // CONSTANT_Methodref
          case 11: // CONSTANT_InterfaceMethodref
          case 12: // CONSTANT_NameAndType
          case 17: // CONSTANT_Dynamic
          case 18: // CONSTANT_InvokeDynamic
            cursor += 2;
            break;
          case 5: // CONSTANT_Long
          case 6: // CONSTANT_Double
            cursor += 6;
            // longs and doubles take up two pool entries
            i++;
            break;
          default:
            throw new IllegalArgumentException();
        }
      }
      cursor += 2; // all entries are at least two bytes long
    }

    cursor += 2; // skip class access flags

    // to turn a class-constant into a string we first need to look up the UTF8 constant
    // then find the offset to the encoded UTF8 bytes, before decoding them to a string

    String className = utf(bytecode, cp[cp[u2(bytecode, cursor)]]);
    cursor += 2;

    String superName = utf(bytecode, cp[cp[u2(bytecode, cursor)]]);
    cursor += 2;

    // optional list of implemented/extended interfaces
    String[] interfaces;
    int interfacesCount = u2(bytecode, cursor);
    cursor += 2;
    if (interfacesCount > 0) {
      interfaces = new String[interfacesCount];
      for (int i = 0; i < interfacesCount; i++) {
        interfaces[i] = utf(bytecode, cp[cp[u2(bytecode, cursor)]]);
        cursor += 2;
      }
    } else {
      interfaces = NO_INTERFACES;
    }

    return new ClassHeader(className, superName, interfaces);
  }

  /** Reads unsigned 2-bytes (big-endian) at current cursor position. */
  private static int u2(byte[] bytecode, int cursor) {
    return (0xFF & bytecode[cursor]) << 8 | (0xFF & bytecode[cursor + 1]);
  }

  /** Decodes "modified-UTF8" bytes to string form. */
  private static String utf(byte[] bytecode, int utfStart) {
    int utfLen = u2(bytecode, utfStart);
    utfStart += 2;

    char[] chars = null;

    // most class-names will be ASCII, confirm with a quick scan
    for (int u = utfStart, utfEnd = utfStart + utfLen; u < utfEnd; u++) {
      if ((bytecode[u] & 0x80) != 0) {
        chars = new char[utfLen];
        break;
      }
    }

    if (chars == null) {
      // fast-path for ASCII-only, avoids intermediate char array
      return new String(bytecode, utfStart, utfLen, US_ASCII);
    }

    int charLen = 0;
    for (int u = utfStart, utfEnd = utfStart + utfLen; u < utfEnd; u++) {
      int b = bytecode[u];
      int c;
      // "modified-UTF8" does not have built-in charset, must decode it ourselves
      // see https://docs.oracle.com/javase/8/docs/api/java/io/DataInput.html
      if ((b & 0x80) == 0) {
        c = (b & 0x7F);
      } else if ((b & 0xE0) == 0xC0) {
        c = ((b & 0x1F) << 6) + (bytecode[++u] & 0x3F);
      } else {
        c = ((b & 0xF) << 12) + ((bytecode[++u] & 0x3F) << 6) + (bytecode[++u] & 0x3F);
      }
      chars[charLen++] = (char) c;
    }

    return new String(chars, 0, charLen);
  }
}
