package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.NUMBER_LITERAL;
import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;

public class RITypeInstruction extends AbstractInstruction
{
	//sll $rd, $rt, shamt
	//srl $rd, $rt, shamt
	//Type 1
	
	private int functCode;
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int RS_POSITION = 21;
	public static final int RT_POSITION = 16;
	public static final int RD_POSITION = 11;
	public static final int SHAMT_POSITION = 6;
	public static final int FUNCT_CODE_POSITION = 0;
	
	public RITypeInstruction(int functCode)
	{
		super(new ArgumentType[] { REGISTER, REGISTER, NUMBER_LITERAL });
		this.functCode = functCode;
	}
	
	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException
	{
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

	
}
