package edu.asu.plp.tool.backend.isa;

/**
 * Static class to provide constants for unit size definitions, in bits.
 * <p>
 * For instance, {@link #BYTE} has a value of 8, as it is representative of 8 bits.
 * 
 * @author Nesbitt, Morgan
 * @author Moore, Zachary
 *
 */
public class UnitSize
{
	public static final int BIT = 1;
	public static final int HALF_BYTE = 4;
	public static final int NIBBLE = HALF_BYTE;
	public static final int BYTE = 8;
	public static final int TWO_BYTES = 16;
	public static final int FOUR_BYTES = 32;
	public static final int EIGHT_BYTES = 64;
	public static final int KiB = 1024 * 8;
}
