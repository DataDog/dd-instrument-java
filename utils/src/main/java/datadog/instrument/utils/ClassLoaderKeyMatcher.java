/*
 * Unless explicitly stated otherwise all files in this repository are licensed under the Apache-2.0 License.
 * This product includes software developed at Datadog (https://www.datadoghq.com/).
 * Copyright 2025-Present Datadog, Inc.
 */

package datadog.instrument.utils;

/**
 * Matcher used to partition class-loaders by their indexed key-id.
 *
 * @see ClassLoaderIndex#getClassLoaderKeyId(ClassLoader)
 * @see ClassInfoCache#find(CharSequence, ClassLoaderKeyMatcher)
 */
@FunctionalInterface
public interface ClassLoaderKeyMatcher {

  /**
   * Evaluates this matcher against the given class-loader key-id.
   *
   * @param classLoaderKeyId the class-loader key-id
   * @return {@code true} if the key-id matched; otherwise {@code false}
   */
  boolean test(int classLoaderKeyId);
}
