package edu.asu.plp.tool.backend.plpisa.assembler2.arguments;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.util.ISAUtil;

public class Value implements Argument
{
	private String rawValue;
	
	public Value(String rawValue)
	{
		this.rawValue = rawValue;
	}
	
	@Override
	public int encode() 
	{
		long value = 0;
		
		if (this.rawValue.startsWith("0x") || this.rawValue.startsWith("0h"))
		{
			value =  Long.parseLong(this.rawValue.substring(2), 16) & 0xFFFF;
		}
		else if (this.rawValue.startsWith("0b"))
		{
			value = Long.parseLong(this.rawValue.substring(2), 2) & 0xFFFF;
		}
		else
		{
			value = Long.parseLong(this.rawValue) & 0xFFFF;
		}
		
		return (int)value;
	}

	@Override
	public ArgumentType getType()
	{
		return ArgumentType.NUMBER_LITERAL;
	}
	
	@Override
	public String raw()
	{
		return this.rawValue;
	}
}
