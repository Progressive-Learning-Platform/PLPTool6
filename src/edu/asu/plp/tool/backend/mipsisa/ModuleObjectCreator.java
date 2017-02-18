package edu.asu.plp.tool.backend.mipsisa;

import java.util.function.Function;
import edu.asu.plp.tool.backend.mipsisa.assembler2.MIPSAssembler;
import edu.asu.plp.tool.backend.mipsisa.sim.MIPSSimulator;

public class ModuleObjectCreator 
{
	public static final String ISA_NAME = "mips";
	public static MIPSISAModule getModule()
	{		
		Function<String, Boolean> supportsProjectType;
		supportsProjectType = (type) -> type.toLowerCase().startsWith(ModuleObjectCreator.ISA_NAME);
		return new MIPSISAModule(new MIPSAssembler(), new MIPSSimulator(), supportsProjectType);
	}

}