package edu.asu.plp.tool.backend.plpisa;

import java.util.HashMap;
import java.util.List;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.UnitSize;
import edu.asu.plp.tool.backend.isa.UnitSize.DefaultSize;

public class PLPAssembler extends Assembler
{
	private List<String> assembly; 
	
	public PLPAssembler()
	{
		initialize();
	}
	
	private void initialize()
	{
		allowedOpCodeLengths = new int[] {1};
		opCodeSize = UnitSize.getSize(DefaultSize.BYTE);
		instructionOpCodeMap = new HashMap<>();
		pseudoInstructionMap = new HashMap<>();
		registerMap = new HashMap<>();
		setInstructionMapValues();
		setPseudoInstructionMapValues();
		setRegisterMapValues();
	}

	private void setInstructionMapValues()
	{
		// TODO Auto-generated method stub
	}

	private void setPseudoInstructionMapValues()
	{
		// TODO Auto-generated method stub
	}

	private void setRegisterMapValues()
	{
		String[] registers = {
				"$zero", "$at", "$v0", "$v1" ,"$a0",
				"$a1", "$a2", "$a3", "$t0", "$t1",
				"$t2", "$t3", "$t4", "$t5", "$t6",
				"$t7", "$t8", "$t9", "$s0", "$s1",
				"$s2", "$s3", "$s4", "$s5", "$s6",
				"$s7", "$i0", "$i1", "$iv", "$sp",
				"$ir", "$ra"
		};
		for(int index = 0; index < registers.length; index++)
		{
			registerMap.put(registers[index], (byte)index);
		}
	}

	@Override
	public ASMImage assemble(String source)
	{
		BiDirectionalOneToManyMap<String, String> assemblyToDisassemblyMap = null;
		
		assembly = (List<String>) assemblyToDisassemblyMap.keySet();
		
		preprocess();
		
		
		
		return new ASMImage(assemblyToDisassemblyMap);
	}

	private void preprocess()
	{
		// TODO Auto-generated method stub
		
	}
}
