/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.utils;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reference key used to weakly associate a class-loader with a computed value.
 *
 * <p>Guarantees that different class-loaders will always have different key-ids, but an individual
 * class-loader may have several key-ids if it has multiple {@code ClassLoaderKey}s over its life,
 */
final class ClassLoaderKey extends WeakReference<ClassLoader> {

  static final ClassLoader BOOT_CLASS_LOADER = null;
  static final ClassLoader SYSTEM_CLASS_LOADER = ClassLoader.getSystemClassLoader();

  static final int BOOT_CLASS_LOADER_KEY_ID = 0;
  static final int SYSTEM_CLASS_LOADER_KEY_ID = 1;

  // key-ids 0 and 1 are pre-assigned to the boot and system class-loaders
  private static final AtomicInteger NEXT_KEY_ID = new AtomicInteger(2);

  // stale class-loader keys that are now eligible for collection
  private static final ReferenceQueue<ClassLoader> staleKeys = new ReferenceQueue<>();

  // registered maps of class-loader keys to values; stale keys are removed from these maps
  private static final List<Map<ClassLoaderKey, ?>> valueMaps = new CopyOnWriteArrayList<>();

  /** Registers a map of class-loader keys to values for cleaning. */
  static void registerValueMap(Map<ClassLoaderKey, ?> valueMap) {
    valueMaps.add(valueMap);
  }

  /** Checks for stale class-loader keys; stale keys are removed from the registered maps. */
  static void cleanStaleKeys() {
    ClassLoaderKey key;
    while ((key = (ClassLoaderKey) staleKeys.poll()) != null) {
      //noinspection ForLoopReplaceableByForEach - indexed loop performs better
      for (int i = 0, size = valueMaps.size(); i < size; i++) {
        valueMaps.get(i).remove(key);
      }
    }
  }

  final int hash;
  final int keyId;

  ClassLoaderKey(ClassLoader cl, int hash) {
    super(cl, staleKeys);
    this.hash = hash;
    this.keyId = NEXT_KEY_ID.getAndIncrement() & Integer.MAX_VALUE;
  }

  @Override
  public int hashCode() {
    return hash;
  }

  @Override
  @SuppressFBWarnings("Eq") // symmetric because it mirrors LookupKey.equals
  public boolean equals(Object o) {
    if (o instanceof LookupKey) {
      return get() == ((LookupKey) o).cl;
    } else if (o instanceof ClassLoaderKey) {
      return get() == ((ClassLoaderKey) o).get();
    } else {
      return false;
    }
  }

  /** Temporary key used for lookup purposes without the reference tracking overhead. */
  static final class LookupKey {
    final ClassLoader cl;
    final int hash;

    LookupKey(ClassLoader cl) {
      this.cl = cl;
      this.hash = System.identityHashCode(cl);
    }

    @Override
    public int hashCode() {
      return hash;
    }

    @Override
    @SuppressFBWarnings("Eq") // symmetric because it mirrors ClassLoaderKey.equals
    public boolean equals(Object o) {
      if (o instanceof ClassLoaderKey) {
        return cl == ((ClassLoaderKey) o).get();
      } else if (o instanceof LookupKey) {
        return cl == ((LookupKey) o).cl;
      } else {
        return false;
      }
    }
  }
}
