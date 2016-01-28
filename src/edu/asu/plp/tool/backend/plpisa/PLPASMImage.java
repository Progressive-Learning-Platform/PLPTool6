package edu.asu.plp.tool.backend.plpisa;

import java.util.List;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.isa.ASMDisassembly;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.ASMInstruction;

public class PLPASMImage implements ASMImage
{
	private final BiDirectionalOneToManyMap<ASMInstruction, ? extends ASMDisassembly> assemblyToDisassemblyMap;
	private final List<ASMFile> fileList;
	
	public PLPASMImage(
			BiDirectionalOneToManyMap<ASMInstruction, ? extends ASMDisassembly> assemblyToDisassemblyMap,
			List<ASMFile> fileList)
	{
		this.assemblyToDisassemblyMap = assemblyToDisassemblyMap;
		this.fileList = fileList;
	}
	
	@Override
	public BiDirectionalOneToManyMap<ASMInstruction, ? extends ASMDisassembly> getAssemblyDisassemblyMap()
	{
		return assemblyToDisassemblyMap;
	}
	
	@Override
	public List<ASMFile> getAsmList()
	{
		return fileList;
	}
	
}
