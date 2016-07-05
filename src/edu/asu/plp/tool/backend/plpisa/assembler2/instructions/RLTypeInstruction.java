package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.MEMORY_LOCATION;
import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;

public class RLTypeInstruction extends AbstractInstruction
{
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int MASK_16BIT = 0xFFFF;
	public static final int OP_CODE_POSITION = 26;
	public static final int RS_POSITION = 21;
	public static final int RT_POSITION = 16;
	
	private int opCode;

	public RLTypeInstruction(int nOpcode) {
		super(new ArgumentType[] { REGISTER, MEMORY_LOCATION });
		
		this.opCode = nOpcode;
	}

	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException {
		
		Argument registerArgument = arguments[0];
		Argument memoryArgument = arguments[1];
		
		return assembleEncodings(registerArgument.encode(),
				memoryArgument.encode());
		
	}
	
	private int assembleEncodings(int encodedRegisterArgument,
			int encodedMemoryArgument)
	{
		int encodedBitString = 0;
		
		//TODO: encodin need to be done
		
		return encodedBitString;
	}

}
