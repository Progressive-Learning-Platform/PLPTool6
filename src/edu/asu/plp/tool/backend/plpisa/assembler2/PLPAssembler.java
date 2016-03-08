package edu.asu.plp.tool.backend.plpisa.assembler2;

import java.util.List;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.OrderedBiDirectionalOneToManyHashMap;
import edu.asu.plp.tool.backend.isa.ASMDisassembly;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.ASMInstruction;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.backend.plpisa.PLPASMImage;

public class PLPAssembler implements Assembler
{
	private BiDirectionalOneToManyMap<ASMInstruction, ? extends ASMDisassembly> assemblyToDisassemblyMap;
	
	@Override
	public ASMImage assemble(List<ASMFile> asmFiles) throws AssemblerException
	{
		assemblyToDisassemblyMap = new OrderedBiDirectionalOneToManyHashMap<>();
		
		for (ASMFile asmFile : asmFiles)
		{
			assembleFile(asmFile.getContent());
		}
		
		return new PLPASMImage(assemblyToDisassemblyMap);
	}
	
	private void assembleFile(String content)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
}
