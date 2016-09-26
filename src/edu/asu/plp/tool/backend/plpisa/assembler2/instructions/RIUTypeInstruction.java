package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.NUMBER_LITERAL;
import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;

public class RIUTypeInstruction extends AbstractInstruction
{
	//lui $rt, imm
	//Type 5
	
	public static final int OP_CODE_POSITION = 26;
	public static final int MASK_6BIT = 0b111111;
	public static final int MASK_5BIT = 0b011111;
	public static final int RT_LOCATION = 16;
	
	int opCode = 0;

	public RIUTypeInstruction(int opCode) 
	{
		super(new ArgumentType[] { REGISTER, NUMBER_LITERAL });
		this.opCode = opCode;
		
	}

	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException {
		
		Argument regArgument = arguments[0];
		Argument valArgument = arguments[1];
		
		return assembleEncodings(regArgument.encode(), valArgument.encode());
	}
	
	private int assembleEncodings(int encodedRTArgument, int encodedImmediateArgument)
	{
		int encodedBitString = 0;
		
		encodedBitString |= encodedImmediateArgument;
		encodedBitString |= (encodedRTArgument & MASK_5BIT) << RT_LOCATION;
		encodedBitString |= (opCode & MASK_6BIT) << OP_CODE_POSITION;
		
		return encodedBitString;
	}
	

}
