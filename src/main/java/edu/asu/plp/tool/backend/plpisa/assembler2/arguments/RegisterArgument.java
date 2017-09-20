package edu.asu.plp.tool.backend.plpisa.assembler2.arguments;

import java.util.HashMap;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;

public class RegisterArgument implements Argument
{
	private String rawValue;
	
	
	public RegisterArgument(String rawValue)
	{
		this.rawValue = rawValue;
		
	}
	
	@Override
	public int encode()
	{
		int encodedValue = -1;
		
		String[] registers = { "$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
				"$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8", "$t9",
				"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$i0", "$i1",
				"$iv", "$sp", "$ir", "$ra" };
		
		for (int index = 0; index < registers.length; index++)
		{
			String posReg1 = registers[index];
			String posReg2 = "$" + index; 
			
			if(this.rawValue.equalsIgnoreCase(posReg2) || this.rawValue.equalsIgnoreCase(posReg1))
			{
				encodedValue = index;
				break;
			}
			
		}
		
		return encodedValue;
	}

	@Override
	public String raw()
	{
		return this.rawValue;
		
	}

	@Override
	public ArgumentType getType()
	{
		return ArgumentType.REGISTER;
	}
}
