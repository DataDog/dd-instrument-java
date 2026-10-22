package datadog.instrument.classmatch;

import static datadog.instrument.classmatch.AccessMatcher.ABSTRACT;
import static datadog.instrument.classmatch.AccessMatcher.FINAL;
import static datadog.instrument.classmatch.AccessMatcher.INTERFACE;
import static datadog.instrument.classmatch.AccessMatcher.PRIVATE;
import static datadog.instrument.classmatch.AccessMatcher.PROTECTED;
import static datadog.instrument.classmatch.AccessMatcher.PUBLIC;
import static datadog.instrument.classmatch.AccessMatcher.STATIC;
import static datadog.instrument.classmatch.AccessMatcher.SYNCHRONIZED;
import static datadog.instrument.classmatch.AccessMatcher.TRANSIENT;
import static datadog.instrument.classmatch.AccessMatcher.VARARGS;
import static datadog.instrument.classmatch.AccessMatcher.VOLATILE;
import static datadog.instrument.classmatch.ClassMatcher.annotatedWith;
import static datadog.instrument.classmatch.ClassMatcher.declares;
import static datadog.instrument.classmatch.FieldMatcher.field;
import static datadog.instrument.classmatch.MatcherTestSupport.assertMatches;
import static datadog.instrument.classmatch.MatcherTestSupport.classOutline;
import static datadog.instrument.classmatch.MatcherTestSupport.emptyClass;
import static datadog.instrument.classmatch.MatcherTestSupport.fieldOutline;
import static datadog.instrument.classmatch.MatcherTestSupport.methodOutline;
import static datadog.instrument.classmatch.MatcherTestSupport.parse;
import static datadog.instrument.classmatch.MethodMatcher.constructor;
import static datadog.instrument.classmatch.MethodMatcher.method;
import static datadog.instrument.classmatch.MethodMatcher.staticInitializer;
import static datadog.instrument.classmatch.StandardMatchers.not;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class ClassMatcherTest {
  @Test
  void emptyClassNeverInvokesMemberMatchers() {
    FieldMatcher unexpectedField =
        f -> {
          throw new AssertionError("no fields");
        };
    MethodMatcher unexpectedMethod =
        m -> {
          throw new AssertionError("no methods");
        };
    assertMatches(declares(unexpectedField), emptyClass(), false);
    assertMatches(declares(unexpectedMethod), emptyClass(), false);
    assertMatches(declares(PUBLIC, unexpectedField), emptyClass(), false);
    assertMatches(declares(PUBLIC, unexpectedMethod), emptyClass(), false);
  }

  @Test
  void declarationsSearchPastNonmatchingMembersAndShortCircuit() {
    FieldOutline[] fields = {
      fieldOutline(0, "other", "I"), fieldOutline(Modifier.PUBLIC, "value", "I"), null
    };
    MethodOutline[] methods = {
      methodOutline(0, "other", "()V"), methodOutline(Modifier.PUBLIC, "execute", "()V"), null
    };
    ClassOutline candidate = classOutline(fields, methods);
    assertMatches(declares(field("value")), candidate, true);
    assertMatches(declares(PUBLIC, field("value")), candidate, true);
    assertMatches(declares(method("execute")), candidate, true);
    assertMatches(declares(PUBLIC, method("execute")), candidate, true);
  }

  @Test
  void accessAndSignatureMustMatchTheSameMember() {
    ClassOutline candidate =
        classOutline(
            new FieldOutline[] {
              fieldOutline(Modifier.PRIVATE, "value", "I"),
              fieldOutline(Modifier.PUBLIC, "other", "I")
            },
            new MethodOutline[] {
              methodOutline(Modifier.PRIVATE, "execute", "()V"),
              methodOutline(Modifier.PUBLIC, "other", "()V")
            });
    assertMatches(declares(PUBLIC, field("value")), candidate, false);
    assertMatches(declares(PRIVATE, field("value")), candidate, true);
    assertMatches(declares(PUBLIC, method("execute")), candidate, false);
    assertMatches(declares(PRIVATE, method("execute")), candidate, true);
    assertMatches(declares(field("missing")), candidate, false);
    assertMatches(declares(method("missing")), candidate, false);
  }

  @Test
  void annotationOverloadsNormalizeNamesAndHandleNoCandidates() {
    ClassOutline candidate =
        classOutline(new FieldOutline[0], new MethodOutline[0], "sample/Other", "sample/Marker");
    assertMatches(annotatedWith("sample.Marker"), candidate, true);
    assertMatches(annotatedWith("sample/Marker"), candidate, true);
    assertMatches(annotatedWith("sample.Missing", "sample.Marker"), candidate, true);
    assertMatches(annotatedWith(Arrays.asList("sample.Missing", "sample.Marker")), candidate, true);
    assertMatches(annotatedWith("sample.Missing"), candidate, false);
    assertMatches(annotatedWith(new String[0]), candidate, false);
    assertMatches(annotatedWith(Collections.emptyList()), candidate, false);
    assertMatches(annotatedWith("sample.Marker"), emptyClass(), false);
  }

  @Test
  void fluentMatchersWorkWithParsedBytecode() {
    // Construct annotation matchers before parsing: they register annotations of interest.
    ClassMatcher matcher =
        annotatedWith(RuntimeMarker.class.getName())
            .and(declares(PUBLIC.and(STATIC).and(FINAL), field("CONSTANT").type(long.class)))
            .and(declares(PRIVATE.and(VOLATILE), field("state").type(String.class)))
            .and(declares(PROTECTED.and(TRANSIENT), field("values").type(int[][].class)))
            .and(
                declares(
                    PUBLIC.and(SYNCHRONIZED),
                    method("execute")
                        .parameters(String.class, int.class)
                        .returning(String.class)
                        .annotatedWith(RuntimeMarker.class.getName())))
            .and(declares(VARARGS, method("collect").parameters(String[].class)))
            .and(declares(constructor().noParameters()))
            .and(declares(STATIC, staticInitializer()))
            .and(not(declares(field("missing"))));
    ClassOutline outline = parse(Fixture.class);
    assertMatches(matcher, outline, true);
    assertEquals(Fixture.class.getName().replace('.', '/'), outline.className);
    assertEquals(Parent.class.getName().replace('.', '/'), outline.superName);
    assertArrayEquals(new String[] {"java/lang/Runnable"}, outline.interfaces);
    assertTrue(PUBLIC.test(outline.access));
  }

  @Test
  void declarationsAndAnnotationsDoNotSearchParents() {
    ClassMatcher inheritedAnnotation = annotatedWith(ParentMarker.class.getName());
    ClassOutline outline = parse(Fixture.class);
    assertMatches(declares(field("inheritedField")), outline, false);
    assertMatches(declares(method("inheritedMethod")), outline, false);
    assertMatches(inheritedAnnotation, outline, false);
    ClassOutline parent = parse(Parent.class);
    assertMatches(declares(field("inheritedField")), parent, true);
    assertMatches(declares(method("inheritedMethod")), parent, true);
    assertMatches(inheritedAnnotation, parent, true);
  }

  @Test
  void onlyRegisteredRuntimeAnnotationsAreExtracted() {
    ClassMatcher marker = annotatedWith(RuntimeMarker.class.getName());
    // CLASS retention must remain invisible even when explicitly registered.
    ClassMatcher invisible = annotatedWith(ClassMarker.class.getName());
    MethodMatcher methodMarker = method("execute").annotatedWith(RuntimeMarker.class.getName());
    ClassOutline outline = parse(Fixture.class);
    String markerName = RuntimeMarker.class.getName().replace('.', '/');
    // The unregistered runtime annotation contains every annotation element shape, ensuring
    // skipping its values still finds the interesting annotation that follows it.
    assertArrayEquals(new String[] {markerName}, outline.annotations);
    assertMatches(marker, outline, true);
    assertMatches(invisible, outline, false);
    assertMatches(declares(methodMarker), outline, true);
    MethodOutline execute =
        Arrays.stream(outline.methods)
            .filter(m -> m.methodName.equals("execute"))
            .findFirst()
            .get();
    assertArrayEquals(new String[] {markerName}, execute.annotations);
  }

  @Test
  void annotationFactoryOverloadsRegisterInterestForParsing() {
    ClassMatcher varargs = annotatedWith("missing.Annotation", VarargsMarker.class.getName());
    ClassMatcher collection =
        annotatedWith(Collections.singletonList(CollectionMarker.class.getName()));
    MethodMatcher methodVarargs =
        method("marked").annotatedWith("missing.Annotation", VarargsMarker.class.getName());
    MethodMatcher methodCollection =
        method("marked").annotatedWith(Collections.singletonList(CollectionMarker.class.getName()));
    ClassOutline outline = parse(MultipleAnnotations.class);
    assertMatches(
        varargs.and(collection).and(declares(methodVarargs.and(methodCollection))), outline, true);
  }

  @Test
  void bridgeMethodsAndInterfacesArePreserved() {
    ClassOutline implementation = parse(StringSupplier.class);
    assertMatches(
        declares(method("get").noParameters().returning(String.class)), implementation, true);
    assertMatches(
        declares(method("get").noParameters().returning(Object.class)), implementation, true);
    assertEquals(
        2, Arrays.stream(implementation.methods).filter(m -> m.methodName.equals("get")).count());
    ClassOutline iface = parse(Supplier.class);
    assertTrue(INTERFACE.and(ABSTRACT).test(iface.access));
    assertEquals("java/lang/Object", iface.superName);
    assertMatches(
        declares(PUBLIC.and(ABSTRACT), method("get").returning(Object.class)), iface, true);
    assertMatches(declares(constructor()), iface, false);
  }

  @Retention(RetentionPolicy.RUNTIME)
  @interface RuntimeMarker {}

  @Retention(RetentionPolicy.CLASS)
  @interface ClassMarker {}

  @Retention(RetentionPolicy.RUNTIME)
  @Inherited
  @interface ParentMarker {}

  @Retention(RetentionPolicy.RUNTIME)
  @interface VarargsMarker {}

  @Retention(RetentionPolicy.RUNTIME)
  @interface CollectionMarker {}

  enum Choice {
    ONE
  }

  @interface Nested {
    String value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @interface Unregistered {
    byte b();

    char c();

    double d();

    float f();

    int i();

    long l();

    short s();

    boolean z();

    String text();

    Class<?> type();

    Choice choice();

    Nested nested();

    int[] array();
  }

  @ParentMarker
  public static class Parent {
    public int inheritedField;

    public void inheritedMethod() {}
  }

  @Unregistered(
      b = 1,
      c = 'x',
      d = 1.5,
      f = 2.5f,
      i = 3,
      l = 4L,
      s = 5,
      z = true,
      text = "hello",
      type = String.class,
      choice = Choice.ONE,
      nested = @Nested("nested"),
      array = {1, 2})
  @RuntimeMarker
  @ClassMarker
  public static class Fixture extends Parent implements Runnable {
    public static final long CONSTANT = 1234567890123L;
    public static final double DOUBLE = 1.25;
    private volatile String state;
    protected transient int[][] values;
    private static Object initialized;

    static {
      initialized = new Object();
    }

    @Unregistered(
        b = 1,
        c = 'x',
        d = 1.5,
        f = 2.5f,
        i = 3,
        l = 4L,
        s = 5,
        z = true,
        text = "hello",
        type = String.class,
        choice = Choice.ONE,
        nested = @Nested("nested"),
        array = {1, 2})
    @RuntimeMarker
    @ClassMarker
    public synchronized String execute(String value, int count) {
      return value;
    }

    public void collect(String... values) {}

    public void run() {}
  }

  @VarargsMarker
  @CollectionMarker
  static class MultipleAnnotations {
    @VarargsMarker
    @CollectionMarker
    void marked() {}
  }

  interface Supplier<T> {
    T get();
  }

  static class StringSupplier implements Supplier<String> {
    public String get() {
      return "value";
    }
  }
}
