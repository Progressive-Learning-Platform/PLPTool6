package edu.asu.plp.tool.backend.plpisa;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Assembler;

public class PLPAssembler implements Assembler
{
	private PLPLanguageSpecification languageSpecification;
	
	public PLPAssembler(PLPLanguageSpecification languageSpecification)
	{
		this.languageSpecification = languageSpecification;
	}
	
	@Override
	public ASMImage assemble(String source)
	{
		BiDirectionalOneToManyMap<String, String> assemblyToDisassemblyMap;
		
		
		
		return null;
	}
}
