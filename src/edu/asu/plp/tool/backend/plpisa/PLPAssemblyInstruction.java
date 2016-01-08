package edu.asu.plp.tool.backend.plpisa;

import edu.asu.plp.tool.backend.isa.ASMInstruction;

public class PLPAssemblyInstruction implements ASMInstruction
{
	private final int lineNumber;
	private final String instructionContents;
	private final String filePath;
	
	public PLPAssemblyInstruction(int lineNumber, String instructionContents,
			String filePath)
	{
		this.lineNumber = lineNumber;
		this.instructionContents = instructionContents;
		this.filePath = filePath;
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
	public String filePath()
	{
		return filePath;
	}
	
}
