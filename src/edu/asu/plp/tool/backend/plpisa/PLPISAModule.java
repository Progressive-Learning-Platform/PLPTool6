package edu.asu.plp.tool.backend.plpisa;

import java.util.function.Function;

import edu.asu.plp.tool.backend.plpisa.assembler2.PLPAssembler;
import edu.asu.plp.tool.backend.plpisa.sim.PLPSimulator;
import edu.asu.plp.tool.core.ISAModule;

public class PLPISAModule extends ISAModule
{

	public PLPISAModule(PLPAssembler assembler, PLPSimulator simulator, Function<String, Boolean> supportsProjectType) {
		super(assembler, simulator, supportsProjectType);
		// TODO Auto-generated constructor stub
	}

}
