package edu.asu.plp.tool.backend;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An object that maps keys to values. Unlike {@link Map}, this class represents a
 * one-to-many mapping with unique keys <b>and<b> values.
 * 
 * @author Moore, Zachary
 *
 * @param <K>
 *            key type
 * @param <V>
 *            value type
 */
public interface BiDirectionalOneToManyMap<K, V>
{
	public boolean put(K key, V value);
	
	public boolean remove(K key, V value);
	
	public List<V> removeKey(K key);
	
	public K removeValue(V value);
	
	public boolean containsKey(K key);
	
	public boolean containsValue(V value);
	
	public boolean contains(K key, V value);
	
	public K getKey(V value);
	
	public List<V> get(K key);
	
	public Set<K> keySet();
	
	public Set<V> valueSet();
	
	/**
	 * Returns the number of key-value pairs contained in this map.
	 * <p>
	 * Since each key may be mapped to multiple values, each value may have only one key,
	 * and values must be unique, the number of key-value pairs of this map is equal to
	 * the number of values contained in the map.
	 * 
	 * @return the number of key-value pairs contained in this map
	 * @see #valueSize()
	 */
	public int size();
	
	/**
	 * Returns the number of keys contained in this map.
	 * <p>
	 * While this behaviour can be achieved by {@link #keySet()}.size(), this method is
	 * generally preferred.
	 * 
	 * @return the number of keys contained in this map
	 */
	public int keySize();
	
	/**
	 * Returns the number of values contained in this map.
	 * <p>
	 * Note that this method is equivalent to {@link #size()}
	 * 
	 * @return the number of values contained in this map
	 * @see #size()
	 */
	public int valueSize();
}
