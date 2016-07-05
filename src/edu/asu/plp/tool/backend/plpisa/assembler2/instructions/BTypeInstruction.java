package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.LABEL_LITERAL;
import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;
import edu.asu.plp.tool.backend.plpisa.assembler2.instructions.AbstractInstruction;

public class BTypeInstruction extends AbstractInstruction
{
	
	private int opCode;

	public BTypeInstruction(int opCode) 
	{
		super(new ArgumentType[] { REGISTER, REGISTER, LABEL_LITERAL});
		this.opCode = opCode;
	}

	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException {
		// TODO Auto-generated method stub
		return 0;
	}
	

}
