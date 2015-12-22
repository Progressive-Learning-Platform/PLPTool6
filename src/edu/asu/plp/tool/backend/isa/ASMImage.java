package edu.asu.plp.tool.backend.isa;

import java.util.ArrayList;
import java.util.List;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;

/**
 * Image of an assembled program. This class needs to be re-instantiated
 * everytime the assembly changes.
 * 
 * @author Nesbitt, Morgan
 *
 */
public class ASMImage
{
	protected final BiDirectionalOneToManyMap<String, String> assemblyToDisassemblyMap;
	protected List<Integer> breakPoints;
	
	public ASMImage(
			BiDirectionalOneToManyMap<String, String> assemblyDisassemblyMap)
	{
		this.assemblyToDisassemblyMap = assemblyDisassemblyMap;
		this.breakPoints = new ArrayList<>();
	}
	
	
	
	public boolean addBreakPoint(int breakPointLineNumber)
	{
		if(breakPoints.contains(breakPointLineNumber))
			return false;
		
		breakPoints.add(breakPointLineNumber);
		return true;
	}
	
	public boolean removeBreakPoint(int breakPointLineNumber)
	{
		if(!breakPoints.contains(breakPointLineNumber))
			return false;
		
		breakPoints.remove(breakPointLineNumber);
		return true;
	}
	
}
