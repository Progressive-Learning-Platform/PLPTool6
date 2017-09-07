package edu.asu.SimulatorFiles;


public class InstructionExtractor
{
	private enum Constants
	{
		R_MASK(0x1F),
		V_MASK(0x3F),
		C_MASK(0xFFFF),
		J_MASK(0x3FFFFFF);
		
		private int value;
		
		Constants(int maskValue)
		{
			this.value = maskValue;
		}
		
		public int getValue()
		{
			return value;
		}
	}
	
	public static int imm(long instr)
	{
		return (int) (instr & Constants.C_MASK.getValue());
	}
	
	public static byte funct(long instr)
	{
		return (byte) (instr & Constants.V_MASK.getValue());
	}
	
	public static byte sa(long instr)
	{
		return (byte) ((instr >> 6) & Constants.R_MASK.getValue());
	}
	
	public static byte rd(long instr)
	{
		return (byte) ((instr >> 11) & Constants.R_MASK.getValue());
	}
	
	public static byte rt(long instr)
	{
		return (byte) ((instr >> 16) & Constants.R_MASK.getValue());
	}
	
	public static byte rs(long instr)
	{
		return (byte) ((instr >> 21) & Constants.R_MASK.getValue());
	}
	
	public static byte opcode(long instr)
	{
		return (byte) ((instr >> 26) & Constants.V_MASK.getValue());
	}
	
	public static int jaddr(long instr)
	{
		return (int) (instr & Constants.J_MASK.getValue());
	}
	
	public static long resolve_jaddr(long pc, long instr)
	{ 
		// pc=address of jump instruction
		return ((pc + 8) & 0xff000000L) | (jaddr(instr) << 2);
	}
	
	public static long resolve_baddr(long pc, long instr)
	{ 
		// pc=address of branch instruction
		return (pc + 4 + (imm(instr) << 2)) & ((long) 0xfffffff << 4 | 0xf);
	}
	
	public static byte extractByteCode(long instruction)
	{
		return (opcode(instruction) != 0) ? opcode(instruction) : funct(instruction);
	}
	
	
}
