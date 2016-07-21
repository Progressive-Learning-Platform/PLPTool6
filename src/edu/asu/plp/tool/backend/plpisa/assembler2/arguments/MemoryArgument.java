package edu.asu.plp.tool.backend.plpisa.assembler2.arguments;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.RegisterArgument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.Value;


public class MemoryArgument implements Argument
{
	private String rawValue;
	private RegisterArgument reg;
	private Value offsetValue;
	
	
	public MemoryArgument(String rawValue)
	{
		this.rawValue = rawValue;
		
		
		
	}
	
	@Override
	public int encode()
	{
		String[] parts = this.rawValue.split("\\(");
		
		offsetValue = new Value(parts[0]);
		
		String reg = parts[1].substring(0, parts[1].length()-1);
		this.reg = new RegisterArgument(reg);
		return offsetValue.encode();
	}

	@Override
	public ArgumentType getType()
	{
		return ArgumentType.MEMORY_LOCATION;
	}
	
	@Override
	public String raw()
	{
		return this.rawValue;
	}
	
	public int getRegisterValue()
	{
		return this.reg.encode();
	}
	
	public int getOffsetValue()
	{
		return this.offsetValue.encode();
	}
	
}
