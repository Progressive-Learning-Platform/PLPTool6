package edu.asu.plp.tool.backend.isa;

import java.util.HashMap;

public class UnitSize
{
	public static enum DefaultSize
	{
		// 1 bit, 0-1
		BIT,
		// 4 bits, 16
		NIBBLE,
		// 8 bits, 256
		BYTE,
		// 10 bits, 1024, 4 bytes
		KiB,
		// 16 bits, 65536, 256 bytes
		HALF_WORD,
		// 32 bits, 65536 Half words
		WORD,
		// 64 bits
		DOUBLE_WORD;
	}
	
	public static HashMap<String, Integer> unitSizes = new HashMap<>();
	
	protected UnitSize()
	{
	}
	
	public static void initializeDefaultValues()
	{
		clearExistingUnits();
		addUnitSize(DefaultSize.BIT.toString(), 1);
		addUnitSize(DefaultSize.NIBBLE.toString(), 4);
		addUnitSize(DefaultSize.BYTE.toString(), 8);
		addUnitSize(DefaultSize.KiB.toString(), 10);
		addUnitSize(DefaultSize.HALF_WORD.toString(), 16);
		addUnitSize(DefaultSize.WORD.toString(), 32);
		addUnitSize(DefaultSize.DOUBLE_WORD.toString(), 64);
	}
	
	public boolean containsName(String name)
	{
		return unitSizes.containsKey(name.toLowerCase());
	}
	
	public static int getSize(DefaultSize size)
	{
		return getSize(size.toString());
	}
	
	public static int getSize(String name)
	{
		return unitSizes.get(name.toLowerCase());
	}
	
	public static boolean addUnitSize(String name, int sizeInBits)
	{
		if (unitSizes.containsKey(name.toLowerCase()))
			return false;
		unitSizes.put(name.toLowerCase(), sizeInBits);
		return true;
	}
	
	/**
	 * If a value exists with the same key it will replace the value attached to
	 * that key. Otherwise it will insert the pair into the unitSize map
	 * 
	 * @param name
	 * @param sizeInBits
	 */
	public static void replaceUnitSize(String name, int sizeInBits)
	{
		if(unitSizes.containsKey(name.toLowerCase()))
			unitSizes.replace(name.toLowerCase(), sizeInBits);
		else
			unitSizes.put(name.toLowerCase(), sizeInBits);
	}
	
	public static void clearExistingUnits()
	{
		unitSizes.clear();
	}
}
