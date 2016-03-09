package edu.asu.plp.tool.backend.plpisa.assembler2;

import java.text.ParseException;

public class RJTypeInstruction implements PLPInstruction
{
	private RTypeInstruction backingInstruction;
	
	public RJTypeInstruction(int functCode)
	{
		this.backingInstruction = new RTypeInstruction(functCode);
	}
	
	@Override
	public int assemble(Argument[] arguments) throws ParseException
	{
		validateArguments(arguments);
		Argument rdRegisterArgument = new RegisterArgument("$0");
		Argument rsRegisterArgument = arguments[0];
		Argument rtRegisterArgument = new RegisterArgument("$0");
		
		arguments = new Argument[] { rdRegisterArgument, rsRegisterArgument,
				rtRegisterArgument };
		return backingInstruction.assemble(arguments);
	}
	
	private void validateArguments(Argument[] arguments) throws ParseException
	{
		if (arguments.length != 1)
		{
			String message = "R-Type Jump Instructions must have exactly 1 argument";
			message += ", but " + arguments.length + " were found";
			throw new ParseException(message, 0);
		}
		
		Argument argument = arguments[0];
		if (!argument.isRegister())
		{
			String message = "R-Type Jump Instructions require a single register argument";
			message += ", but the non-register argument {" + argument.raw()
					+ "} was found.";
			throw new ParseException(message, 0);
		}
	}
}
