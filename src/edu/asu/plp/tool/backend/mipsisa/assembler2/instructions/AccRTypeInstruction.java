package edu.asu.plp.tool.backend.mipsisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.mipsisa.assembler2.Argument;
import edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.ArgumentType;
import edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.RegisterArgument;

public class AccRTypeInstruction extends AbstractInstruction
{
	//multu $rs, $rt
	
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int RS_POSITION = 21;
	public static final int RT_POSITION = 16;
	public static final int RD_POSITION = 11;
	public static final int SHAMT_POSITION = 6;
	public static final int FUNCT_CODE_POSITION = 0;
	
	private int functCode;
	
	public AccRTypeInstruction(int functCode)
	{
		super(new ArgumentType[] { REGISTER, REGISTER });
		this.functCode = functCode;
	}
	
	@Override
	protected int safeAssemble(Argument[] arguments)
	{
		Argument rsRegisterArgument = arguments[0];
		Argument rtRegisterArgument = arguments[1];
		
		return assembleEncodings(rsRegisterArgument.encode(), rtRegisterArgument.encode());
	}
	
	private int assembleEncodings(int encodedRSArgument, int encodedRTArgument)
	{
		
		//Argument loRegisterArgument = new RegisterArgument("$LO");
		
		int encodedBitString = 0;
		encodedBitString |= (encodedRSArgument & MASK_5BIT) << RS_POSITION;
		encodedBitString |= (encodedRTArgument & MASK_5BIT) << RT_POSITION;
		encodedBitString |= (functCode & MASK_6BIT) << FUNCT_CODE_POSITION;
		
		return encodedBitString;
	}
}
