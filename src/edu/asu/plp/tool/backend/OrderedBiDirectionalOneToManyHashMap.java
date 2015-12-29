package edu.asu.plp.tool.backend;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderedBiDirectionalOneToManyHashMap<K, V> implements
		BiDirectionalOneToManyMap<K, V>
{
	private Map<K, List<V>> keys;
	private Map<V, K> values;
	
	public OrderedBiDirectionalOneToManyHashMap()
	{
		this.keys = new LinkedHashMap<>();
		this.values = new LinkedHashMap<>();
	}
	
	@Override
	public K put(K key, V value)
	{
		if (key.equals(getKey(value)))
			return key;
		
		K oldKey = removeValue(value);
		List<V> associates = keys.get(key);
		if (associates == null)
		{
			associates = new ArrayList<>();
			keys.put(key, associates);
		}
		
		associates.add(value);
		values.put(value, key);
		return oldKey;
	}

	@Override
	public boolean remove(K key, V value)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public List<V> removeKey(K key)
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public K removeValue(V value)
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public boolean containsKey(K key)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public boolean containsValue(V value)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public boolean contains(K key, V value)
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public K getKey(V value)
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public List<V> get(K key)
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public Set<K> keySet()
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public Set<V> valueSet()
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public int size()
	{
		// TODO Auto-generated method stub return 0;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public int keySize()
	{
		// TODO Auto-generated method stub return 0;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public int valueSize()
	{
		// TODO Auto-generated method stub return 0;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public boolean clear()
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
}
