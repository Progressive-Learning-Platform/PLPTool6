package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.MEMORY_LOCATION;
import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;

public class RLTypeInstruction extends AbstractInstruction
{
	
	private int opCode;

	public RLTypeInstruction(int nOpcode) {
		super(new ArgumentType[] { REGISTER, MEMORY_LOCATION });
		
		this.opCode = nOpcode;
	}

	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException {
		// TODO Auto-generated method stub
		return 0;
	}

}
