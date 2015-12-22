package edu.asu.plp.tool.backend.isa;

import java.util.HashMap;

public class UnitSize
{
	public static enum DefaultSize
	{
		// 1 bit, 0-1
		BIT(1),
		// 4 bits, 16
		NIBBLE(4),
		// 8 bits, 256
		BYTE(8),
		// 10 bits, 1024, 4 bytes
		KiB(10),
		// 16 bits, 65536, 256 bytes
		HALF_WORD(16),
		// 32 bits, 65536 Half words
		WORD(32),
		// 64 bits
		DOUBLE_WORD(64);
		
		private int sizeInBits;
		
		DefaultSize(int value)
		{
			this.sizeInBits = value;
		}
		
		public int bitSize()
		{
			return sizeInBits;
		}
	}
	
	public static HashMap<String, Integer> unitSizes = new HashMap<>();
	
	protected UnitSize()
	{
	}
	
	public static void initializeDefaultValues()
	{
		clearExistingUnits();
		for (DefaultSize unitSize : DefaultSize.values())
		{
			addUnitSize(unitSize.name(), unitSize.bitSize());
		}
	}
	
	public boolean containsName(String name)
	{
		return unitSizes.containsKey(name.toLowerCase());
	}
	
	public int getSize(String name)
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
		if(unitSizes.containsKey(name))
			unitSizes.replace(name, sizeInBits);
		else
			unitSizes.put(name, sizeInBits);
	}
	
	public static void clearExistingUnits()
	{
		unitSizes.clear();
	}
}
