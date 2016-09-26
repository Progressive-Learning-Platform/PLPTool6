package edu.asu.plp.tool.backend.plpisa.assembler2;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;

public interface PLPInstruction
{
	int assemble(Argument[] arguments) throws ParseException;
	ArgumentType[] getArgumentsofInstruction();
}
