package edu.asu.plp.tool.backend.plpisa.assembler2;

public interface Argument
{
	int encode();

	boolean isRegister();

	boolean isNumberLiteral();

	boolean isStringLiteral();

	String raw();
}
