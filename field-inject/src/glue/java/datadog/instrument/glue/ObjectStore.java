/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.glue;

import java.util.function.Function;

public final class ObjectStore {

  /** Keys that store their associated values in bytecode-injected fields. */
  public interface InjectedKey {
    /** Retrieves value from the injected field backing the given store. */
    Object $get$__datadogObjectStore$(int storeId);

    /** Stores given value in the injected field backing the given store. */
    void $put$__datadogObjectStore$(int storeId, Object value);
  }

  private final int storeId;

  public ObjectStore(int storeId) {
    this.storeId = storeId;
  }

  public Object get(Object key) {
    if (key instanceof InjectedKey) {
      return ((InjectedKey) key).$get$__datadogObjectStore$(storeId);
    } else {
      return GlobalObjectStore.get(key, storeId);
    }
  }

  public void put(Object key, Object value) {
    if (key instanceof InjectedKey) {
      ((InjectedKey) key).$put$__datadogObjectStore$(storeId, value);
    } else {
      GlobalObjectStore.put(key, storeId, value);
    }
  }

  @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
  public Object putIfAbsent(Object key, Object value) {
    if (key instanceof InjectedKey) {
      InjectedKey access = (InjectedKey) key;
      Object existing = access.$get$__datadogObjectStore$(storeId);
      if (existing == null) {
        synchronized (access) {
          existing = access.$get$__datadogObjectStore$(storeId);
          if (existing == null) {
            access.$put$__datadogObjectStore$(storeId, (existing = value));
          }
        }
      }
      return existing;
    } else {
      return GlobalObjectStore.putIfAbsent(key, storeId, value);
    }
  }

  @SuppressWarnings({"SynchronizationOnLocalVariableOrMethodParameter", "rawtypes", "unchecked"})
  public Object computeIfAbsent(Object key, Function factory) {
    if (key instanceof InjectedKey) {
      InjectedKey access = (InjectedKey) key;
      Object existing = access.$get$__datadogObjectStore$(storeId);
      if (existing == null) {
        synchronized (access) {
          existing = access.$get$__datadogObjectStore$(storeId);
          if (existing == null) {
            access.$put$__datadogObjectStore$(storeId, (existing = factory.apply(key)));
          }
        }
      }
      return existing;
    } else {
      return GlobalObjectStore.computeIfAbsent(key, storeId, factory);
    }
  }

  @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
  public Object remove(Object key) {
    if (key instanceof InjectedKey) {
      InjectedKey access = (InjectedKey) key;
      Object existing = access.$get$__datadogObjectStore$(storeId);
      if (existing != null) {
        synchronized (access) {
          existing = access.$get$__datadogObjectStore$(storeId);
          if (existing != null) {
            access.$put$__datadogObjectStore$(storeId, null);
          }
        }
      }
      return existing;
    } else {
      return GlobalObjectStore.remove(key, storeId);
    }
  }
}
