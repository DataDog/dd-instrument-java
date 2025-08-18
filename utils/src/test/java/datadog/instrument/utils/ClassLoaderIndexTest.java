package datadog.instrument.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.jupiter.api.Test;

class ClassLoaderIndexTest {

  @Test
  @SuppressFBWarnings("DM_GC")
  @SuppressWarnings("UnusedAssignment")
  void getClassLoaderKeyId() {

    ClassLoader[] cls = new ClassLoader[500];
    for (int i = 2; i < cls.length; i++) {
      cls[i] = newCL();
    }

    ClassLoader cl500 = newCL();
    ClassLoader cl501 = newCL();
    ClassLoader cl502 = newCL();
    ClassLoader cl503 = newCL();
    ClassLoader cl504 = newCL();
    ClassLoader cl505 = newCL();

    // first fill the index to ~50%
    for (int i = 2; i < cls.length; i++) {
      assertEquals(i, ClassLoaderIndex.getClassLoaderKeyId(cls[i]));
    }

    // verify that the boot and system class-loaders have fixed ids
    assertEquals(0, ClassLoaderIndex.getClassLoaderKeyId(null));
    assertEquals(1, ClassLoaderIndex.getClassLoaderKeyId(ClassLoader.getSystemClassLoader()));

    // now index some more class-loaders and check they have unique ids
    assertEquals(500, ClassLoaderIndex.getClassLoaderKeyId(cl500));
    assertEquals(501, ClassLoaderIndex.getClassLoaderKeyId(cl501));
    assertEquals(502, ClassLoaderIndex.getClassLoaderKeyId(cl502));
    assertEquals(503, ClassLoaderIndex.getClassLoaderKeyId(cl503));
    assertEquals(504, ClassLoaderIndex.getClassLoaderKeyId(cl504));
    assertEquals(505, ClassLoaderIndex.getClassLoaderKeyId(cl505));

    // unload most of the indexed class-loaders, except the last few
    cls = null;
    System.gc();

    // verify that the boot and system class-loaders still have fixed ids
    assertEquals(0, ClassLoaderIndex.getClassLoaderKeyId(null));
    assertEquals(1, ClassLoaderIndex.getClassLoaderKeyId(ClassLoader.getSystemClassLoader()));

    // those class-loaders still alive should also have retained their ids
    assertEquals(500, ClassLoaderIndex.getClassLoaderKeyId(cl500));
    assertEquals(501, ClassLoaderIndex.getClassLoaderKeyId(cl501));
    assertEquals(502, ClassLoaderIndex.getClassLoaderKeyId(cl502));
    assertEquals(503, ClassLoaderIndex.getClassLoaderKeyId(cl503));
    assertEquals(504, ClassLoaderIndex.getClassLoaderKeyId(cl504));
    assertEquals(505, ClassLoaderIndex.getClassLoaderKeyId(cl505));

    // deliberately overflow the index; the ids handed out should still be unique
    for (int i = 0; i < 2000; i++) {
      assertEquals(506 + i, ClassLoaderIndex.getClassLoaderKeyId(newCL()));
    }

    // verify that the boot and system class-loaders still have fixed ids
    assertEquals(0, ClassLoaderIndex.getClassLoaderKeyId(null));
    assertEquals(1, ClassLoaderIndex.getClassLoaderKeyId(ClassLoader.getSystemClassLoader()));
  }

  private static ClassLoader newCL() {
    return URLClassLoader.newInstance(new URL[0]);
  }
}
