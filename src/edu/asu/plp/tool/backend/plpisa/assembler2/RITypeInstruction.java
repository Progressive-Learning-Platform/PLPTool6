package edu.asu.plp.tool.backend.plpisa.assembler2;

import static edu.asu.plp.tool.backend.plpisa.assembler2.RTypeInstruction.*;

import java.text.ParseException;

public class RITypeInstruction implements PLPInstruction
{
	private int functCode;
	
	public RITypeInstruction(int functCode)
	{
		this.functCode = functCode;
	}
	
	@Override
	public int assemble(Argument[] arguments) throws ParseException
	{
		validateArguments(arguments);
		Argument rdRegisterArgument = arguments[0];
		Argument rtRegisterArgument = arguments[1];
		Argument shamtArgument = arguments[2];
		
		return assembleEncodings(rdRegisterArgument.encode(),
				rtRegisterArgument.encode(), shamtArgument.encode());
	}
	
	private int assembleEncodings(int encodedRDArgument, int encodedRTArgument,
			int encodedShamtArgument)
	{
		int encodedBitString = 0;
		encodedBitString |= (encodedShamtArgument & MASK_5BIT) << SHAMT_POSITION;
		encodedBitString |= (encodedRTArgument & MASK_5BIT) << RT_POSITION;
		encodedBitString |= (encodedRDArgument & MASK_5BIT) << RD_POSITION;
		encodedBitString |= (functCode & MASK_6BIT) << FUNCT_CODE_POSITION;
		
		return encodedBitString;
	}
	
	private void validateArguments(Argument[] arguments) throws ParseException
	{
		if (arguments.length != 3)
		{
			String message = "R-Type Instructions require 3 arguments";
			message += ", but " + arguments.length + " were found";
			throw new ParseException(message, 0);
		}
		
		boolean valid = arguments[0].isRegister();
		valid = valid && arguments[1].isRegister() && arguments[2].isNumberLiteral();
		if (!valid)
		{
			String message = "R-Type Immediate Instructions require 2 register arguments and one immediate argument,";
			message += ", but instead found: [";
			for (Argument argument : arguments)
				message += argument.raw();
			throw new ParseException(message, 0);
		}
	}
}
