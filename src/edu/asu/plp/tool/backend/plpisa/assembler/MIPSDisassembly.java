package edu.asu.plp.tool.backend.plpisa.assembler;

import edu.asu.plp.tool.backend.isa.ASMDisassembly;

public class MIPSDisassembly implements ASMDisassembly
{
	public static final long MASK_32BIT = 0xFFFFFFFF;
	
	private long address;
	private long instruction;
	
	public MIPSDisassembly(long address, long instruction)
	{
		this.address = address;
		this.instruction = instruction & MASK_32BIT;
	}
	
	@Override
	public long getAddresss()
	{
		return address;
	}
	
	@Override
	public long getInstruction()
	{
		return instruction;
	}
}
