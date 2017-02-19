package edu.asu.plp.tool.backend.mipsisa.assembler2;

import java.text.ParseException;

import edu.asu.plp.tool.backend.mipsisa.assembler2.arguments.ArgumentType;

public interface MIPSInstruction
{
	int assemble(Argument[] arguments);
	ArgumentType[] getArgumentsofInstruction();
}
