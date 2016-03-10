package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.PLPInstruction;

public class RTypeInstruction implements PLPInstruction
{
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int RS_POSITION = 21;
	public static final int RT_POSITION = 16;
	public static final int RD_POSITION = 11;
	public static final int SHAMT_POSITION = 6;
	public static final int FUNCT_CODE_POSITION = 0;
	
	private int functCode;
	
	public RTypeInstruction(int functCode)
	{
		this.functCode = functCode;
	}
	
	@Override
	public int assemble(Argument[] arguments) throws ParseException
	{
		validateArguments(arguments);
		Argument rdRegisterArgument = arguments[0];
		Argument rsRegisterArgument = arguments[1];
		Argument rtRegisterArgument = arguments[2];
		
		return assembleEncodings(rdRegisterArgument.encode(),
				rsRegisterArgument.encode(), rtRegisterArgument.encode());
	}
	
	private int assembleEncodings(int encodedRDArgument, int encodedRSArgument,
			int encodedRTArgument)
	{
		int encodedBitString = 0;
		encodedBitString |= (encodedRSArgument & MASK_5BIT) << RS_POSITION;
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
		
		for (int index = 0; index < arguments.length; index++)
		{
			Argument argument = arguments[index];
			if (!argument.isRegister())
			{
				String message = "R-Type Instructions require 3 register arguments";
				message += ", but a non-register argument was found: " + argument.raw();
				throw new ParseException(message, index);
			}
		}
	}
}
