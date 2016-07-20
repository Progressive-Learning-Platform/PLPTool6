package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.LABEL_LITERAL;
import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.LabelLiteral;
import edu.asu.plp.tool.backend.plpisa.assembler2.instructions.AbstractInstruction;

public class BTypeInstruction extends AbstractInstruction
{
	//bne $t1, $t2, label
	//beq $t1, $t2, label
	//Type 3
	
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int MASK_16BIT = 0xFFFF;
	public static final int RS_POSITION = 21;
	public static final int RT_POSITION = 16;
	public static final int OP_CODE_POSITION = 26;
	public static final int LABEL_LOCATION = 0;
	
	private int opCode;

	public BTypeInstruction(int opCode) 
	{
		super(new ArgumentType[] { REGISTER, REGISTER, LABEL_LITERAL});
		this.opCode = opCode;
	}

	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException {
		Argument r1RegisterArgument = arguments[0];
		Argument r2RegisterArgument = arguments[1];
		LabelLiteral labelArgument = (LabelLiteral)arguments[2];
		
		return assembleEncodings(r1RegisterArgument.encode(),
				r2RegisterArgument.encode(), labelArgument.encode(), labelArgument.getCurrentInstructionLocation());
	}
	
	private int assembleEncodings(int encodedR1Argument, int encodedR2Argument,
			int labelLocation, int currentLocation)
	{
		int encodedValue = 0;
		
		int branchTarget = labelLocation - (currentLocation + 4);
		branchTarget /= 4;
		
		encodedValue |= (branchTarget & MASK_16BIT) << LABEL_LOCATION;
		encodedValue |= (encodedR1Argument & MASK_5BIT) << RS_POSITION;
		encodedValue |= (encodedR2Argument & MASK_5BIT )<< RT_POSITION;
		encodedValue |= (opCode & MASK_6BIT) << OP_CODE_POSITION;
		
		
		return encodedValue;
	}
	

}
