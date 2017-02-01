package edu.asu.plp.tool.backend.mipsisa;

import edu.asu.plp.tool.backend.isa.ASMInstruction;

public class MIPSAssemblyInstruction implements ASMInstruction
{
	private final int lineNumber;
	private final String instructionContents;
	private final String sourceFile;
	
	public MIPSAssemblyInstruction(int lineNumber, String instructionContents, String sourceFile)
	{
		this.lineNumber = lineNumber;
		this.instructionContents = instructionContents;
		this.sourceFile = sourceFile;
	}

	@Override
	public int getLineNumber()
	{
		return lineNumber;
	}

	@Override
	public String getInstructionContents()
	{
		return instructionContents;
	}
	
	@Override
	public String getSourceFile()
	{
		return this.sourceFile;
	}
	
	
	
}
