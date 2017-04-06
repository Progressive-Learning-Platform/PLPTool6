package edu.asu.plp.tool.backend.mipsisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.ArgumentType.LABEL_LITERAL;
import static edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.mipsisa.assembler2.Argument;
import edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.ArgumentType;
import edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.LabelLiteral;
import edu.asu.plp.tool.backend.mipsisa.assembler2.instructions.AbstractInstruction;

public class BRTypeInstruction extends AbstractInstruction
{
	//bgez $t1, label
	//bgezal $t1, label
	
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int MASK_16BIT = 0xFFFF;
	public static final int RS_POSITION = 21;
	public static final int BRANCH_CODE_POSITION = 16;
	public static final int OP_CODE_POSITION = 26;
	public static final int LABEL_LOCATION = 0;
	
	private int opCode;
	private int branchCode;

	public BRTypeInstruction(int opCode, int branchCode) 
	{
		super(new ArgumentType[] { REGISTER, LABEL_LITERAL});
		this.opCode = opCode;
		this.branchCode = branchCode;
	}

	@Override
	protected int safeAssemble(Argument[] arguments) {
		Argument r1RegisterArgument = arguments[0];
		LabelLiteral labelArgument = (LabelLiteral)arguments[1];
		
		return assembleEncodings(r1RegisterArgument.encode(), labelArgument.encode(), labelArgument.getCurrentInstructionLocation());
	}
	
	private int assembleEncodings(int encodedR1Argument, int labelLocation, int currentLocation)
	{
		int encodedValue = 0;
		
		int branchTarget = labelLocation - (currentLocation + 4);
		branchTarget /= 4;
		
		encodedValue |= (branchTarget & MASK_16BIT) << LABEL_LOCATION;
		encodedValue |= (encodedR1Argument & MASK_5BIT) << RS_POSITION;
		encodedValue |= (branchCode & MASK_5BIT )<< BRANCH_CODE_POSITION;
		encodedValue |= (opCode & MASK_6BIT) << OP_CODE_POSITION;
		
		
		return encodedValue;
	}
	

}
