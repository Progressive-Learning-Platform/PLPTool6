package edu.asu.plp.tool.backend.plpisa;

import java.util.ArrayList;
import java.util.List;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.isa.ASMDisassembly;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.ASMInstruction;
import javafx.util.Pair;
//import edu.asu.plp.tool.backend.util.Pair;


public class PLPASMImage implements ASMImage
{
	//private int nStartAddress;
	private List<Pair<ASMInstruction, ASMDisassembly>> disAssem;
	
	public PLPASMImage(List<Pair<ASMInstruction,ASMDisassembly>> disAssem)
	{
		this.disAssem = disAssem;
	}
	
	@Override
	public List<Pair<ASMInstruction, ASMDisassembly>> getDisassemblyInfo()
	{
		return disAssem;
	}
	
	//private final BiDirectionalOneToManyMap<ASMInstruction, ? extends ASMDisassembly> assemblyToDisassemblyMap;
	
	/*public PLPASMImage(
			BiDirectionalOneToManyMap<ASMInstruction, ? extends ASMDisassembly> assemblyToDisassemblyMap, int startAddress)
	{
		this.assemblyToDisassemblyMap = assemblyToDisassemblyMap;
		nStartAddress = startAddress;
	}
	
	@Override
	public BiDirectionalOneToManyMap<ASMInstruction, ? extends ASMDisassembly> getAssemblyDisassemblyMap()
	{
		return assemblyToDisassemblyMap;
	}
	
	/*public List<ASMDisassembly> getDisassemblyOfFile(String sourceFile)
	{
		ArrayList<? extends ASMDisassembly> listDisassm = new ArrayList<? extends ASMDisassembly>();
		
		for(ASMInstruction asm: assemblyToDisassemblyMap.keySet())
			if(asm.getSourceFile() == sourceFile)
				listDisassm.add(assemblyToDisassemblyMap.get(asm));
		
		
		return listDisassm;
	}*/
	
	
	
}
