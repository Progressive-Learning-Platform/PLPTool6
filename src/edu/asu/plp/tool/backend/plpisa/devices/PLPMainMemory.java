package edu.asu.plp.tool.backend.plpisa.devices;

import plptool.Constants;
import plptool.Text;

/**
 * This class represents the main memory of the PLP
 * This actually extends its IOMemoryModule
 * @author Harsha
 * @see PLPIOMemoryModule
 */
public class PLPMainMemory extends PLPIOMemoryModule
{
	/**
	 * This is the constructor of the MainMemory.
	 * Actual variable where storage happens is present in the parent class
	 * @param addr Start address from which memory is associated to this module
	 * @param size Amount of memory associated to this module
	 * @param wordAligned Whether memory allocated is word aligned or not
	 */
	public PLPMainMemory(long addr, long size, boolean wordAligned)
	{
		super(addr, addr + (wordAligned? size-4: size-1), wordAligned);
	}

	/**
	 * This function is not used here. Main Memory doesn't have evaluation. It can only read and write to particular
	 * memory addresses.
	 */
	@Override
	public int eval() 
	{
		return Constants.PLP_OK;
	}
	
	/**
	 * This function is not used here. Main memory doesn't have gui evaluation. It can only read and write to particular memory address
	 */
	@Override
	public int gui_eval(Object x) {
		return Constants.PLP_OK;
	}

	/**
	 * This will clear all the contents from the main memory. As actual storage in parent class it will call
	 * parents clear function.
	 */
	@Override
	public void reset() {
		super.clear();
		
	}

	/**
	 * This function will return a string describing this module
	 */
	@Override
	public String introduce() {
		return "Main Memory Module " + Text.versionString;
	}

}
