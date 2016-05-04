/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ronasof.unforgiving.stalltu.core;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class ByValueComparator.
 *
 * @param <K> the
 * @param <V> the
 */
public class ByValueComparator<K,V extends Comparable<? super V>> 
    implements Comparator<K> {

  /** The map. */
  private Map<K,V> map = new HashMap<>();
  
  /**
   * Instantiates a new by value comparator.
   *
   * @param map the map
   */
  public ByValueComparator(Map<K,V> map) {
    this.map = map;
  }

  /* (non-Javadoc)
   * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
   */
  public int compare(K k1, K k2) {
    return map.get(k1).compareTo(map.get(k2));
  }
  
}