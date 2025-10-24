/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.fieldinject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Manages unique {@link ObjectStore} ids for each key-value type combination.
 *
 * <p>Only used when field-injection is not supported or enabled.
 */
final class ObjectStoreIds {

  private static final Map<String, Short> typeIds = new ConcurrentHashMap<>();
  private static final AtomicInteger nextTypeId = new AtomicInteger();

  /**
   * Returns the {@link ObjectStore} id for the given key-value type combination.
   *
   * @param keyType the key type
   * @param valueType the value type
   * @return the key-value's store id
   */
  static int objectStoreId(String keyType, String valueType) {
    // key is typically more unique so use that for LSB
    return typeId(valueType) << 16 | typeId(keyType);
  }

  /**
   * Generates a unique id for the given object store key (or value) type.
   *
   * @param type the type
   * @return the unique type id
   */
  private static short typeId(String type) {
    return typeIds.computeIfAbsent(type, unused -> (short) nextTypeId.getAndIncrement());
  }
}
