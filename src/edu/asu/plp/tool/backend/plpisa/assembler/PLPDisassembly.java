package edu.asu.plp.tool.backend.plpisa.assembler;

import edu.asu.plp.tool.backend.isa.ASMDisassembly;

public class PLPDisassembly implements ASMDisassembly
{
	private long address;
	private long[] instructions;
	
	public PLPDisassembly(long address, long... instructions)
	{
		this.address = address;
		this.instructions = instructions;
	}
	
	@Override
	public long getAddresss()
	{
		return address;
	}
	
	@Override
	public long[] getInstructions()
	{
		return instructions;
	}
}
