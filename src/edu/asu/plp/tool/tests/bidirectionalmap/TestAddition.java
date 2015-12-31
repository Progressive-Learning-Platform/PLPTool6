package edu.asu.plp.tool.tests.bidirectionalmap;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.OrderedBiDirectionalOneToManyHashMap;

public class TestAddition
{
	private BiDirectionalOneToManyMap<String, String> map;
	
	@Before
	public void setUp()
	{
		map = new OrderedBiDirectionalOneToManyHashMap<>();
	}
	
	@After
	public void tearDown()
	{
		map = null;
	}
	
	/*
	 * Put Tests
	 */
	@Test
	public void testNonNullKeyToNullValue()
	{
		String key = "key";
		map.put(key, null);
		assertTrue(map.contains(key, null));
		assertTrue(map.containsKey(key));
		assertTrue(map.containsValue(null));
		
		List<String> mappedValues = map.get(key);
		assertTrue(mappedValues.contains(null));
	}
	
	@Test
	public void testNullKeyToNonNullValue()
	{
		String key = null;
		String value = "value";
		
		map.put(key, value);
		assertTrue(map.contains(key, value));
		assertTrue(map.containsKey(key));
		assertTrue(map.containsValue(value));
		
		List<String> mappedValues = map.get(key);
		assertTrue(mappedValues.contains(value));
	}
	
	@Test
	public void testNullKeyToNullValue()
	{
		String key = null;
		String value = null;
		
		map.put(key, value);
		assertTrue(map.contains(key, value));
		assertTrue(map.containsKey(key));
		assertTrue(map.containsValue(value));
		
		List<String> mappedValues = map.get(key);
		assertTrue(mappedValues.contains(value));
	}
	
	@Test
	public void testNonNullKeyToNonNullValue()
	{
		String key = "key";
		String value = "value";
		
		map.put(key, value);
		assertTrue(map.contains(key, value));
		assertTrue(map.containsKey(key));
		assertTrue(map.containsValue(value));
		
		List<String> mappedValues = map.get(key);
		assertTrue(mappedValues.contains(value));
	}
	
	@Test
	public void testInsertSingleKeyMultiValues()
	{
		String key = "firstKey";
		String[] values = new String[] { "first value", "secondValue", "value3" };
		
		for (String value : values)
			map.put(key, value);
		
		for (String value : values)
			assertTrue(map.contains(key, value));
		
		for (String value : values)
			assertTrue(map.containsValue(value));
		
		List<String> mappedValues = map.get(key);
		assertEquals(values.length, mappedValues.size());
		for (String value : values)
			assertTrue(mappedValues.contains(value));
	}
	
	/**
	 * Values must be unique. As such, the expected behaviour is that the first key to be
	 * associated with the value will be overwritten by the second.
	 */
	@Test
	public void testUniqueKeysSameValue()
	{
		
	}
	
	/*
	 * Size Tests
	 */
	@Test
	public void testSingleKeyMultiValuesKeySize()
	{
		
	}
	
	@Test
	public void testSingleKeyMultiValuesValuesSize()
	{
		
	}
	
	@Test
	public void testSingleKeySingleValuesValuesSize()
	{
		
	}
	
	@Test
	public void testSingleKeyMultiValuesMapSize()
	{
		
	}
	
	@Test
	public void testSingleKeySingleValuesKeySize()
	{
		
	}
	
	/*
	 * Contains Tests
	 */
	
	/*
	 * Get Tests
	 */
	
	/*
	 * Key/Value Set Tests
	 */
	
}
