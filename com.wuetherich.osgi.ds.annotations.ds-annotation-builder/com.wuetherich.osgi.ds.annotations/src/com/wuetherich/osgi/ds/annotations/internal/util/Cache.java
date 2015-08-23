/*******************************************************************************
 * Copyright (c) 2011-2013 Gerd W&uuml;therich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd W&uuml;therich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * Helper class.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 * 
 * @param <K>
 * @param <V>
 */
public abstract class Cache<K, V> extends HashMap<K, V> {

  /** - */
  private static final long serialVersionUID = 1L;

  /**
   * <p>
   * Creates a new instance of type {@link Cache}.
   * </p>
   */
  public Cache() {
    super();
  }

  /**
   * <p>
   * Creates a new instance of type {@link Cache}.
   * </p>
   * 
   * @param initialCapacity
   * @param loadFactor
   */
  public Cache(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  /**
   * <p>
   * Creates a new instance of type {@link Cache}.
   * </p>
   * 
   * @param initialCapacity
   */
  public Cache(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * <p>
   * Creates a new instance of type {@link Cache}.
   * </p>
   * 
   * @param m
   */
  public Cache(Map<? extends K, ? extends V> m) {
    super(m);
  }

  /**
   * <p>
   * </p>
   * 
   * @param key
   * @return
   */
  public V getOrCreate(K key) {

    //
    Assert.isNotNull(key);

    //
    if (!containsKey(key)) {

      V value = create(key);

      put(key, value);
    }

    //
    return get(key);

  }

  /**
   * <p>
   * </p>
   * 
   * @param key
   * @return
   */
  protected abstract V create(K key);
}
