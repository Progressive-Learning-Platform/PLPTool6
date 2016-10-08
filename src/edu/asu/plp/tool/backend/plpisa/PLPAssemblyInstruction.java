package edu.asu.plp.tool.backend.plpisa;

import edu.asu.plp.tool.backend.isa.ASMInstruction;

public class PLPAssemblyInstruction implements ASMInstruction
{
	private final int lineNumber;
	private final String instructionContents;
	private final String sourceFile;
	
	public PLPAssemblyInstruction(int lineNumber, String instructionContents, String sourceFile)
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
