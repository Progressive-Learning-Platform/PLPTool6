package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.LABEL_LITERAL;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;
import edu.asu.plp.tool.backend.plpisa.assembler2.instructions.AbstractInstruction;

public class JTypeInstruction extends AbstractInstruction
{
	//j label
	//jal label
	//Type 7
	
	public static final int OP_CODE_POSITION = 26;
	public static final int MASK_6BIT = 0b111111;
	public static final int LABEL_POSITION = 0;
	public static final int LABEL_MASK = 0x3FFFFFF;
	int opCode;
	

	public JTypeInstruction(int nOpcode) 
	{
		super(new ArgumentType[] { LABEL_LITERAL });
		
		this.opCode = nOpcode;
	}

	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException {
		
		Argument lbArgument = arguments[0];
		
		
		return assembleEncodings(lbArgument.encode());
	}
	
	private int assembleEncodings(int labelLocation)
	{
		int encodedBitString = 0;
		
		labelLocation = labelLocation >> 2;
		
		encodedBitString |= (labelLocation & LABEL_MASK) << LABEL_POSITION;
		encodedBitString |= (opCode & MASK_6BIT) << OP_CODE_POSITION;
		
		return encodedBitString;
		
	}
	
}
