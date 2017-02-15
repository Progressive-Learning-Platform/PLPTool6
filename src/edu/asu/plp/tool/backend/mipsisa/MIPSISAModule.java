package edu.asu.plp.tool.backend.mipsisa;

import java.util.function.Function;
import edu.asu.plp.tool.backend.mipsisa.assembler2.MIPSAssembler;
import edu.asu.plp.tool.backend.mipsisa.sim.MIPSSimulator;
import edu.asu.plp.tool.core.ISAModule;

public class MIPSISAModule extends ISAModule
{

	public MIPSISAModule(MIPSAssembler assembler, MIPSSimulator simulator, Function<String, Boolean> supportsProjectType) {
		super(assembler, simulator, supportsProjectType);
		// TODO Auto-generated constructor stub
	}

}
