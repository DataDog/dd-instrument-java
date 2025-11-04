/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.classmatch;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses <a href="https://docs.oracle.com/javase/specs/jvms/se24/html/jvms-4.html">class-file</a>
 * content into {@link ClassHeader}s or {@link ClassOutline}s, depending on the detail required.
 *
 * <p>All class-names and field/method descriptors appear in <a
 * href="https://docs.oracle.com/javase/specs/jvms/se24/html/jvms-4.html#jvms-4.2.1">internal
 * form</a>.
 */
public final class ClassFile {

  static final String JAVA_LANG_OBJECT = "java/lang/Object";
  static final String STATIC_INITIALIZER = "<clinit>";
  static final String CONSTRUCTOR = "<init>";
  static final String SIMPLE_CALL = "()V";

  private static final String[] NO_INTERFACES = {};
  private static final FieldOutline[] NO_FIELDS = {};
  private static final MethodOutline[] NO_METHODS = {};
  private static final String[] NO_ANNOTATIONS = {};

  private static final int ACC_INTERFACE = 0x0200;
  private static final int ACC_MODULE = 0x8000;

  // attribute header for annotations that are visible at runtime
  private static final byte[] RUNTIME_ANNOTATIONS = "RuntimeVisibleAnnotations".getBytes(US_ASCII);

  // reduce size of outlines by only extracting interesting annotations
  private static final Map<String, UtfKey> annotationKeys = new HashMap<>();
  private static volatile Map<UtfKey, String> annotationsOfInterest;

  private ClassFile() {}

  /**
   * Extracts a {@link ClassHeader} from the given class-file content.
   *
   * @param bytecode the class-file content to parse
   * @return class header containing class-name, super-name, interfaces
   */
  public static ClassHeader header(byte[] bytecode) {
    return parse(bytecode, 0, true);
  }

  /**
   * Extracts a {@link ClassHeader} from the given class-file content.
   *
   * @param bytecode the class-file content to parse
   * @param offset the offset of the class-file content
   * @return class header containing class-name, super-name, interfaces
   */
  public static ClassHeader header(byte[] bytecode, int offset) {
    return parse(bytecode, offset, true);
  }

  /**
   * Extracts a {@link ClassOutline} from the given class-file content.
   *
   * @param bytecode the class-file content to parse
   * @return class outline containing header, fields, methods, annotations
   */
  public static ClassOutline outline(byte[] bytecode) {
    return (ClassOutline) parse(bytecode, 0, false);
  }

  /**
   * Extracts a {@link ClassOutline} from the given class-file content.
   *
   * @param bytecode the class-file content to parse
   * @param offset the offset of the class-file content
   * @return class outline containing header, fields, methods, annotations
   */
  public static ClassOutline outline(byte[] bytecode, int offset) {
    return (ClassOutline) parse(bytecode, offset, false);
  }

  /**
   * Flags the given annotation as interesting; to be included in outlines.
   *
   * <p>Example: {@code ClassFile.annotationOfInterest("javax/ws/rs/Path");}
   *
   * @param internalName the annotation type in internal form
   */
  public static synchronized void annotationOfInterest(String internalName) {
    if (annotationKeys.containsKey(internalName)) {
      return; // already flagged as interesting
    }
    Map<UtfKey, String> ofInterest = new HashMap<>();
    if (annotationsOfInterest != null) {
      ofInterest.putAll(annotationsOfInterest); // copy on write
    }
    ofInterest.put(
        annotationKeys.computeIfAbsent(internalName, ClassFile::annotationKey), internalName);
    annotationsOfInterest = ofInterest;
  }

  /**
   * Flags the given annotations as interesting; to be included in outlines.
   *
   * <p>Example: {@code ClassFile.annotationsOfInterest("javax/ws/rs/Path", "jakarta/ws/rs/Path");}
   *
   * @param internalNames the annotation types in internal form
   */
  public static synchronized void annotationsOfInterest(Collection<String> internalNames) {
    if (annotationKeys.keySet().containsAll(internalNames)) {
      return; // already flagged as interesting
    }
    Map<UtfKey, String> ofInterest = new HashMap<>();
    if (annotationsOfInterest != null) {
      ofInterest.putAll(annotationsOfInterest); // copy on write
    }
    for (String internalName : internalNames) {
      ofInterest.put(
          annotationKeys.computeIfAbsent(internalName, ClassFile::annotationKey), internalName);
    }
    annotationsOfInterest = ofInterest;
  }

  /** Create "modified-UTF8" key to make it easier to match annotations. */
  private static UtfKey annotationKey(String internalName) {
    // annotations are recorded in descriptor form in class-files
    byte[] descriptor = ('L' + internalName + ';').getBytes(US_ASCII);
    return new UtfKey(descriptor);
  }

  /** Parse class-file content, skipping over uninteresting sections, */
  private static ClassHeader parse(byte[] bytecode, int offset, boolean onlyHeader) {
    // skip preamble
    int cursor = offset + 8;

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

    int access = u2(bytecode, cursor);
    cursor += 2;

    // to turn a class-constant into a string we first need to look up the UTF8 constant
    // then find the offset to the encoded UTF8 bytes, before decoding them to a string

    String className = utf(bytecode, cp[cp[u2(bytecode, cursor)]]);
    cursor += 2;

    String superName;
    if ((access & ACC_INTERFACE) != 0) {
      superName = JAVA_LANG_OBJECT;
    } else if (access != ACC_MODULE) {
      superName = utf(bytecode, cp[cp[u2(bytecode, cursor)]]);
      if (JAVA_LANG_OBJECT.equals(superName)) {
        superName = JAVA_LANG_OBJECT;
      }
    } else {
      superName = null;
    }
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

    if (onlyHeader) {
      // stop parsing; we don't need fields/methods/annotations
      return new ClassHeader(access, className, superName, interfaces);
    }

    // optional list of fields
    FieldOutline[] fields;
    int fieldsCount = u2(bytecode, cursor);
    cursor += 2;
    if (fieldsCount > 0) {
      fields = new FieldOutline[fieldsCount];
      for (int i = 0; i < fieldsCount; i++) {
        int fieldAccess = u2(bytecode, cursor);
        cursor += 2;
        String fieldName = utf(bytecode, cp[u2(bytecode, cursor)]);
        cursor += 2;
        String descriptor = utf(bytecode, cp[u2(bytecode, cursor)]);
        cursor += 2;
        int attributesCount = u2(bytecode, cursor);
        cursor += 2;
        for (int j = 0; j < attributesCount; j++) {
          cursor += 2;
          int attributeLength = u4(bytecode, cursor);
          cursor += 4;
          cursor += attributeLength; // jump to end of attribute
        }

        fields[i] = new FieldOutline(fieldAccess, fieldName, descriptor);
      }
    } else {
      fields = NO_FIELDS;
    }

    // optional list of methods
    MethodOutline[] methods;
    int methodsCount = u2(bytecode, cursor);
    cursor += 2;
    if (methodsCount > 0) {
      methods = new MethodOutline[methodsCount];
      for (int i = 0; i < methodsCount; i++) {
        int methodAccess = u2(bytecode, cursor);
        cursor += 2;
        String methodName = utf(bytecode, cp[u2(bytecode, cursor)]);
        if (CONSTRUCTOR.equals(methodName)) {
          methodName = CONSTRUCTOR;
        }
        cursor += 2;
        String descriptor = utf(bytecode, cp[u2(bytecode, cursor)]);
        if (SIMPLE_CALL.equals(descriptor)) {
          descriptor = SIMPLE_CALL;
        }
        cursor += 2;
        String[] annotations = NO_ANNOTATIONS;
        Map<UtfKey, String> ofInterest = annotationsOfInterest;
        int attributesCount = u2(bytecode, cursor);
        cursor += 2;
        for (int j = 0; j < attributesCount; j++) {
          int nameIndex = u2(bytecode, cursor);
          cursor += 2;
          int attributeLength = u4(bytecode, cursor);
          cursor += 4;
          // only interested in the attribute that lists runtime visible annotations
          if (ofInterest != null && utfEquals(bytecode, cp[nameIndex], RUNTIME_ANNOTATIONS)) {
            annotations = parseAnnotations(ofInterest, bytecode, cursor, cp);
            ofInterest = null; // there's at most one of these attributes per-table
          }
          cursor += attributeLength; // jump to end of attribute
        }

        methods[i] = new MethodOutline(methodAccess, methodName, descriptor, annotations);
      }
    } else {
      methods = NO_METHODS;
    }

    String[] annotations = NO_ANNOTATIONS;
    Map<UtfKey, String> ofInterest = annotationsOfInterest;
    int attributesCount = u2(bytecode, cursor);
    cursor += 2;
    for (int j = 0; j < attributesCount; j++) {
      int nameIndex = u2(bytecode, cursor);
      cursor += 2;
      int attributeLength = u4(bytecode, cursor);
      cursor += 4;
      // only interested in the attribute that lists runtime visible annotations
      if (ofInterest != null && utfEquals(bytecode, cp[nameIndex], RUNTIME_ANNOTATIONS)) {
        annotations = parseAnnotations(ofInterest, bytecode, cursor, cp);
        ofInterest = null; // there's at most one of these attributes per-table
      }
      cursor += attributeLength; // jump to end of attribute
    }

    return new ClassOutline(access, className, superName, interfaces, fields, methods, annotations);
  }

  /** Reads unsigned 2-bytes (big-endian) at current cursor position. */
  private static int u2(byte[] bytecode, int cursor) {
    return (0xFF & bytecode[cursor]) << 8 | (0xFF & bytecode[cursor + 1]);
  }

  /** Reads unsigned 4-bytes (big-endian) at current cursor position. */
  private static int u4(byte[] bytecode, int cursor) {
    return (0xFF & bytecode[cursor]) << 24
        | (0xFF & bytecode[cursor + 1]) << 16
        | (0xFF & bytecode[cursor + 2]) << 8
        | (0xFF & bytecode[cursor + 3]);
  }

  /** Decodes the "modified-UTF8" at the given offset to string form. */
  private static String utf(byte[] bytecode, int utfOffset) {
    int utfLen = u2(bytecode, utfOffset);
    int utfStart = utfOffset + 2;
    int utfEnd = utfStart + utfLen;

    char[] chars = null;

    // most class-names will be ASCII, confirm with a quick scan
    for (int u = utfStart; u < utfEnd; u++) {
      if ((bytecode[u] & 0x80) != 0) {
        // found non-ASCII byte, prepare char array for decoding
        chars = new char[utfLen];
        break;
      }
    }

    if (chars == null) {
      // fast-path for ASCII-only, avoids intermediate char array
      return new String(bytecode, utfStart, utfLen, US_ASCII);
    }

    int charLen = 0;
    for (int u = utfStart; u < utfEnd; u++) {
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

  /** Returns {@code true} if the "modified-UTF8" at the offset has the expected content. */
  @SuppressWarnings("SameParameterValue")
  private static boolean utfEquals(byte[] bytecode, int utfOffset, byte[] expected) {
    int expectedLen = expected.length;
    if (u2(bytecode, utfOffset) == expectedLen) {
      return sameBytes(expected, 0, expectedLen, bytecode, utfOffset + 2);
    }
    return false;
  }

  /** Parses interesting annotations from the given attribute location. */
  private static String[] parseAnnotations(
      Map<UtfKey, String> ofInterest, byte[] bytecode, int cursor, int[] cp) {
    int annotationsCount = u2(bytecode, cursor);
    cursor += 2;
    String[] annotations = NO_ANNOTATIONS;
    for (int i = 0; i < annotationsCount; i++) {
      // first 2 bytes point to the annotation descriptor
      int utfOffset = cp[u2(bytecode, cursor)];
      int utfLen = u2(bytecode, utfOffset);
      String annotation = ofInterest.get(new UtfKey(bytecode, utfOffset + 2, utfLen));
      if (annotation != null) {
        // grow as needed; it'll be rare to have many annotations at the same location
        int oldLen = annotations.length;
        annotations = Arrays.copyOf(annotations, oldLen + 1);
        annotations[oldLen] = annotation;
      }
      cursor = nextAnnotationOffset(bytecode, cursor); // jump to the next annotation
    }
    return annotations;
  }

  /** Returns the offset of the next annotation in the attribute. */
  private static int nextAnnotationOffset(byte[] bytecode, int cursor) {
    cursor += 2; // skip annotation descriptor
    int elementPairCount = u2(bytecode, cursor);
    cursor += 2;
    for (int i = 0; i < elementPairCount; i++) {
      cursor += 2; // skip element name index
      cursor = nextAnnotationElementOffset(bytecode, cursor);
    }
    return cursor;
  }

  /** Returns the offset of the next element in the annotation. */
  private static int nextAnnotationElementOffset(byte[] bytecode, int cursor) {
    switch (bytecode[cursor++]) {
      case 'B': // const_value_index (byte)
      case 'C': // const_value_index (char)
      case 'D': // const_value_index (double)
      case 'F': // const_value_index (float)
      case 'I': // const_value_index (integer)
      case 'J': // const_value_index (long)
      case 'S': // const_value_index (short)
      case 'Z': // const_value_index (boolean)
      case 's': // const_value_index (String)
      case 'c': // class_info_index
        return cursor + 2;
      case 'e': // enum_const_value
        return cursor + 4;
      case '@': // annotation_value
        return nextAnnotationOffset(bytecode, cursor);
      case '[': // array_value
        int elementCount = u2(bytecode, cursor);
        cursor += 2;
        for (int i = 0; i < elementCount; i++) {
          cursor = nextAnnotationElementOffset(bytecode, cursor);
        }
        return cursor;
      default:
        throw new IllegalArgumentException();
    }
  }

  /** Returns {@code true} if the two byte arrays have the same content over a given range. */
  static boolean sameBytes(byte[] bytes, int start, int end, byte[] otherBytes, int otherStart) {
    for (int i = start, j = otherStart; i < end; i++, j++) {
      if (bytes[i] != otherBytes[j]) {
        return false;
      }
    }
    return true;
  }

  /** Returns the hash for a given range of the byte array. */
  static int hashBytes(byte[] bytes, int start, int end) {
    int hash = 1;
    for (int i = start; i < end; i++) {
      hash = 31 * hash + bytes[i];
    }
    return hash;
  }

  /** Wraps "modified-UTF8" content so it can be used to lookup associated values. */
  static final class UtfKey {
    private final byte[] bytes;
    private final int start;
    private final int len;
    private final int hash;

    UtfKey(byte[] bytes) {
      this(bytes, 0, bytes.length);
    }

    UtfKey(byte[] bytes, int start, int len) {
      this.bytes = bytes;
      this.start = start;
      this.len = len;
      this.hash = hashBytes(bytes, start, start + len);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof UtfKey) {
        UtfKey other = (UtfKey) obj;
        if (len == other.len) {
          return sameBytes(bytes, start, start + len, other.bytes, other.start);
        }
      }
      return false;
    }
  }
}
