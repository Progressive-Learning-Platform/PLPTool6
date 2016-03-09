package edu.asu.plp.tool.backend.plpisa.assembler2;

import java.util.HashMap;

public class InstructionMap extends HashMap<String, PLPInstruction>
{
	public void addRTypeInstruction(String name, int functCode)
	{
		RTypeInstruction instruction = new RTypeInstruction(functCode);
		this.put(name, instruction);
	}
	
	public void addRITypeInstruction(String name, int functCode)
	{
		RITypeInstruction instruction = new RITypeInstruction(functCode);
		this.put(name, instruction);
	}
	
	public void addRJTypeInstruction(String name, int functCode)
	{
		RJTypeInstruction instruction = new RJTypeInstruction(functCode);
		this.put(name, instruction);
	}
	
	public void addITypeInstruction(String name, int opCode)
	{
		ITypeInstruction instruction = new ITypeInstruction(opCode);
		this.put(name, instruction);
	}
	
	@Override
	public PLPInstruction put(String name, PLPInstruction instruction)
	{
		validateKey(name);
		return super.put(name, instruction);
	}
	
	private void validateKey(String name)
	{
		if (this.containsKey(name))
		{
			String message = "PLPInstructionMap cannot contain multiple instructions "
					+ "with the same name (" + name + ")";
			throw new IllegalArgumentException(message);
		}
	}
}
