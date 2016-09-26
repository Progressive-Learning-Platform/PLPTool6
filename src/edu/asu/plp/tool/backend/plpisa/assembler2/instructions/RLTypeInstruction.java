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
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.MemoryArgument;

public class RLTypeInstruction extends AbstractInstruction
{
	//lw $rs, offset(register)
	//sw $rd, offset(register)
	
	//Type 6
	
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
		MemoryArgument memoryArgument = (MemoryArgument)arguments[1];
		
		memoryArgument.encode();
		
		return assembleEncodings(registerArgument.encode(),
				memoryArgument.getOffsetValue(), memoryArgument.getRegisterValue());
		
	}
	
	private int assembleEncodings(int encodedRTArgument,
			int offsetofMemoryArgument, int registerOfMemoryArgument)
	{
		int encodedBitString = 0;
		
		encodedBitString |= (offsetofMemoryArgument & MASK_16BIT) << IMMEDIATE_POSITION;
		encodedBitString |= (encodedRTArgument & MASK_5BIT) << RT_POSITION;
		encodedBitString |= (registerOfMemoryArgument & MASK_5BIT) << RS_POSITION;
		encodedBitString |= (opCode & MASK_6BIT) << OP_CODE_POSITION;
		
		return encodedBitString;
		
		
	}

}
