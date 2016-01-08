package edu.asu.plp.tool.backend.isa;

public interface ASMInstruction
{
	int getLineNumber();
	String getInstructionContents();
	String filePath();
}
