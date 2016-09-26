package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;

public class JRRTypeInstruction extends AbstractInstruction
{
	public static final int RS_LOCATION = 21;
	public static final int RD_LOCATION = 11;
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int OP_CODE_LOCATION = 0;
	
	//jalr $rd, $rs
	//Type 9
	
	private int opCode;

	public JRRTypeInstruction(int opCode) {
		super(new ArgumentType[] { REGISTER, REGISTER});
		this.opCode = opCode;
	}

	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException {
		Argument rdArgument = arguments[0];
		Argument rsArgument = arguments[1];
		
		return assembleEncodings(rdArgument.encode(), rsArgument.encode());
	}
	
	private int assembleEncodings(int rdArgument, int rsArgument)
	{
		int encodedBitString = 0;
		
		encodedBitString |= (rsArgument & MASK_5BIT) << RS_LOCATION;
		encodedBitString |= (rdArgument & MASK_5BIT) << RD_LOCATION;
		encodedBitString |= (opCode & MASK_6BIT) << OP_CODE_LOCATION;
		
		return encodedBitString;
	}

}
