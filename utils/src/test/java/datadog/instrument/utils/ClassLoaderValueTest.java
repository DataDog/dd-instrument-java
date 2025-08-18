package datadog.instrument.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@SuppressFBWarnings("DM_GC")
@SuppressWarnings({"UnusedAssignment", "ConstantValue"})
class ClassLoaderValueTest {

  private final AtomicInteger count = new AtomicInteger();

  private final ClassLoaderValue<String> classLoaderValue =
      new ClassLoaderValue<String>() {
        @Override
        protected String computeValue(ClassLoader cl) {
          return count.getAndIncrement() + "=" + cl;
        }
      };

  @Test
  void get() {
    ClassLoader cl0 = null;
    ClassLoader cl1 = ClassLoader.getSystemClassLoader();
    ClassLoader cl2 = newCL("CL2");
    ClassLoader cl3 = newCL("CL3");
    ClassLoader cl4 = newCL("CL4");
    ClassLoader cl5 = newCL("CL5");

    assertEquals(0, classLoaderValue.size());

    assertThat(classLoaderValue.get(cl0)).isEqualTo("0=null");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl2)).matches("2=CL2");
    assertThat(classLoaderValue.get(cl3)).matches("3=CL3");
    assertThat(classLoaderValue.get(cl4)).matches("4=CL4");
    assertThat(classLoaderValue.get(cl5)).matches("5=CL5");

    assertEquals(6, classLoaderValue.size());

    // repeated requests should return the same values
    assertThat(classLoaderValue.get(cl5)).matches("5=CL5");
    assertThat(classLoaderValue.get(cl4)).matches("4=CL4");
    assertThat(classLoaderValue.get(cl3)).matches("3=CL3");
    assertThat(classLoaderValue.get(cl2)).matches("2=CL2");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl0)).isEqualTo("0=null");

    assertEquals(6, classLoaderValue.size());
  }

  @Test
  void remove() {
    ClassLoader cl0 = null;
    ClassLoader cl1 = ClassLoader.getSystemClassLoader();
    ClassLoader cl2 = newCL("CL2");
    ClassLoader cl3 = newCL("CL3");
    ClassLoader cl4 = newCL("CL4");
    ClassLoader cl5 = newCL("CL5");

    assertEquals(0, classLoaderValue.size());

    assertThat(classLoaderValue.get(cl0)).isEqualTo("0=null");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl2)).matches("2=CL2");
    assertThat(classLoaderValue.get(cl3)).matches("3=CL3");
    assertThat(classLoaderValue.get(cl4)).matches("4=CL4");
    assertThat(classLoaderValue.get(cl5)).matches("5=CL5");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.remove(cl0);

    assertEquals(5, classLoaderValue.size());

    // only the removed value should be replaced
    assertThat(classLoaderValue.get(cl0)).isEqualTo("6=null");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl2)).matches("2=CL2");
    assertThat(classLoaderValue.get(cl3)).matches("3=CL3");
    assertThat(classLoaderValue.get(cl4)).matches("4=CL4");
    assertThat(classLoaderValue.get(cl5)).matches("5=CL5");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.remove(cl1);

    assertEquals(5, classLoaderValue.size());

    // only the removed value should be replaced
    assertThat(classLoaderValue.get(cl0)).isEqualTo("6=null");
    assertThat(classLoaderValue.get(cl1)).matches("7=.*App.*");
    assertThat(classLoaderValue.get(cl2)).matches("2=CL2");
    assertThat(classLoaderValue.get(cl3)).matches("3=CL3");
    assertThat(classLoaderValue.get(cl4)).matches("4=CL4");
    assertThat(classLoaderValue.get(cl5)).matches("5=CL5");

    assertEquals(6, classLoaderValue.size());

    classLoaderValue.remove(cl3);

    assertEquals(5, classLoaderValue.size());

    // only the removed value should be replaced
    assertThat(classLoaderValue.get(cl0)).isEqualTo("6=null");
    assertThat(classLoaderValue.get(cl1)).matches("7=.*App.*");
    assertThat(classLoaderValue.get(cl2)).matches("2=CL2");
    assertThat(classLoaderValue.get(cl3)).matches("8=CL3");
    assertThat(classLoaderValue.get(cl4)).matches("4=CL4");
    assertThat(classLoaderValue.get(cl5)).matches("5=CL5");

    assertEquals(6, classLoaderValue.size());
  }

  @Test
  @Timeout(30)
  void removeStaleEntries() {
    ClassLoader cl0 = null;
    ClassLoader cl1 = ClassLoader.getSystemClassLoader();
    ClassLoader cl2 = newCL("CL2");
    ClassLoader cl3 = newCL("CL3");
    ClassLoader cl4 = newCL("CL4");
    ClassLoader cl5 = newCL("CL5");

    assertEquals(0, classLoaderValue.size());

    assertThat(classLoaderValue.get(cl0)).isEqualTo("0=null");
    assertThat(classLoaderValue.get(cl1)).matches("1=.*App.*");
    assertThat(classLoaderValue.get(cl2)).matches("2=CL2");
    assertThat(classLoaderValue.get(cl3)).matches("3=CL3");
    assertThat(classLoaderValue.get(cl4)).matches("4=CL4");
    assertThat(classLoaderValue.get(cl5)).matches("5=CL5");

    assertEquals(6, classLoaderValue.size());

    cl2 = null;
    cl4 = null;

    while (classLoaderValue.size() > 4) {
      System.gc();
      System.runFinalization();
      ClassLoaderValue.removeStaleEntries();
    }
  }

  private static ClassLoader newCL(String name) {
    return new ClassLoader() {
      @Override
      public String toString() {
        return name;
      }
    };
  }
}
