package datadog.instrument.classinject;

import static datadog.instrument.classinject.ClassInjectorTestSupport.observeRetransforms;
import static datadog.instrument.classinject.ClassInjectorTestSupport.sampleClass;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.ReflectPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("removal")
public class ClassInjectorSecurityManagerProbe {
  public static void premain(String arguments, Instrumentation inst) {
    AtomicInteger retransforms = new AtomicInteger();
    ClassFileTransformer observer = observeRetransforms(retransforms);
    inst.addTransformer(observer, true);
    Policy.setPolicy(
        new Policy() {
          @Override
          public boolean implies(ProtectionDomain domain, Permission permission) {
            CodeSource source = domain.getCodeSource();
            if (source == null || source.getLocation() == null) {
              return false;
            }
            String name = permission.getName();
            return (permission instanceof ReflectPermission && name.equals("suppressAccessChecks"))
                || (permission instanceof RuntimePermission
                    && (name.equals("createClassLoader")
                        || name.equals("getProtectionDomain")
                        || name.equals("accessDeclaredMembers")));
          }
        });
    System.setSecurityManager(new SecurityManager());
    try {
      ClassInjector.enableClassInjection(inst);
    } finally {
      inst.removeTransformer(observer);
    }
    if (retransforms.get() != 0) {
      throw new AssertionError("Lookup failed and used the transformation fallback");
    }
  }

  public static void main(String[] args) throws Exception {
    if (System.getSecurityManager() == null) {
      throw new AssertionError("SecurityManager is not installed");
    }
    ClassLoader target = new ClassLoader(null) {};
    ProtectionDomain domain = new ProtectionDomain(null, null, target, null);
    String name = "datadog.injected.SecurityManagerSample";
    Class<?> injected = ClassInjector.injectClass(name, sampleClass(name), domain);
    if (injected.getProtectionDomain() != domain) {
      throw new AssertionError("Injected class has the wrong protection domain");
    }
    if (!Integer.valueOf(42).equals(injected.getMethod("value").invoke(null))) {
      throw new AssertionError("Injected class did not execute correctly");
    }
  }
}
