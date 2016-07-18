package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.MEMORY_LOCATION;
import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;
import static edu.asu.plp.tool.backend.plpisa.assembler2.instructions.RTypeInstruction.FUNCT_CODE_POSITION;
import static edu.asu.plp.tool.backend.plpisa.assembler2.instructions.RTypeInstruction.MASK_5BIT;
import static edu.asu.plp.tool.backend.plpisa.assembler2.instructions.RTypeInstruction.MASK_6BIT;
import static edu.asu.plp.tool.backend.plpisa.assembler2.instructions.RTypeInstruction.RD_POSITION;
import static edu.asu.plp.tool.backend.plpisa.assembler2.instructions.RTypeInstruction.RT_POSITION;
import static edu.asu.plp.tool.backend.plpisa.assembler2.instructions.RTypeInstruction.SHAMT_POSITION;

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
	public static final int IMMEDIATE_POSITION = 0;
	
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
	
	private int assembleEncodings(int encodedRTArgument,
			int encodedMemoryArgument)
	{
		int encodedBitString = 0;
		int offset = Integer.parseInt(Integer.toString(encodedMemoryArgument).split("")[0]);
		int encodedRSArgument = Integer.parseInt(Integer.toString(encodedMemoryArgument).split("")[1]);
		encodedBitString |= (encodedRSArgument & MASK_5BIT) << RS_POSITION;
		encodedBitString |= (encodedRTArgument & MASK_5BIT) << RT_POSITION;
		encodedBitString |= (offset & MASK_16BIT) << IMMEDIATE_POSITION;
		encodedBitString |= (opCode & MASK_6BIT) << OP_CODE_POSITION;
		
		return encodedBitString;
		//TODO: encoding need to be done
		
	}

}
