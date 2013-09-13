package com.wuetherich.osgi.ds.annotations.internal.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 * 
 * @param <K>
 * @param <V>
 */
public abstract class GenericCache<K, V> extends HashMap<K, V> {

  /** - */
  private static final long serialVersionUID = 1L;

  /**
   * <p>
   * Creates a new instance of type {@link GenericCache}.
   * </p>
   */
  public GenericCache() {
    super();
  }

  /**
   * <p>
   * Creates a new instance of type {@link GenericCache}.
   * </p>
   * 
   * @param initialCapacity
   * @param loadFactor
   */
  public GenericCache(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }

  /**
   * <p>
   * Creates a new instance of type {@link GenericCache}.
   * </p>
   * 
   * @param initialCapacity
   */
  public GenericCache(int initialCapacity) {
    super(initialCapacity);
  }

  /**
   * <p>
   * Creates a new instance of type {@link GenericCache}.
   * </p>
   * 
   * @param m
   */
  public GenericCache(Map<? extends K, ? extends V> m) {
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
