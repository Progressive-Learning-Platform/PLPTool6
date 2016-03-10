package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.PLPInstruction;

public class ITypeInstruction implements PLPInstruction
{
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int MASK_16BIT = 0xFFFF;
	public static final int OP_CODE_POSITION = 26;
	public static final int RS_POSITION = 21;
	public static final int RT_POSITION = 16;
	public static final int IMMEDIATE_POSITION = 0;
	
	private int opCode;
	
	public ITypeInstruction(int opCode)
	{
		this.opCode = opCode;
	}
	
	@Override
	public int assemble(Argument[] arguments) throws ParseException
	{
		validateArguments(arguments);
		Argument rtRegisterArgument = arguments[0];
		Argument rsRegisterArgument = arguments[1];
		Argument immediateArgument = arguments[2];
		
		return assembleEncodings(rtRegisterArgument.encode(),
				rsRegisterArgument.encode(), immediateArgument.encode());
	}
	
	private int assembleEncodings(int encodedRTArgument, int encodedRSArgument,
			int encodedImmediateArgument)
	{
		int encodedBitString = 0;
		encodedBitString |= (encodedRSArgument & MASK_5BIT) << RS_POSITION;
		encodedBitString |= (encodedRTArgument & MASK_5BIT) << RT_POSITION;
		encodedBitString |= (encodedImmediateArgument & MASK_16BIT) << IMMEDIATE_POSITION;
		encodedBitString |= (opCode & MASK_6BIT) << OP_CODE_POSITION;
		
		return encodedBitString;
	}
	
	private void validateArguments(Argument[] arguments) throws ParseException
	{
		if (arguments.length != 3)
		{
			String message = "I-Type Instructions require 3 arguments";
			message += ", but " + arguments.length + " were found";
			throw new ParseException(message, 0);
		}
		
		boolean valid = arguments[0].isRegister();
		valid = valid && arguments[1].isRegister() && arguments[2].isNumberLiteral();
		if (!valid)
		{
			String message = "I-Type Instructions require 2 register arguments and one immediate argument,";
			message += ", but instead found: [";
			for (Argument argument : arguments)
				message += argument.raw() + ", ";
			throw new ParseException(message, 0);
		}
	}
}
