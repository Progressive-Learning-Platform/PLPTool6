package edu.asu.plp.tool.backend.plpisa.assembler2;

import java.text.ParseException;

public class RTypeInstruction implements PLPInstruction
{
	private int functCode;
	
	public RTypeInstruction(int functCode)
	{
		this.functCode = functCode;
	}
	
	@Override
	public int assemble(Argument[] arguments) throws ParseException
	{
		// TODO Auto-generated method stub return 0;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
}
