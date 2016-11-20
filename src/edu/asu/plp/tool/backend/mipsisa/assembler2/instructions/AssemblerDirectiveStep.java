package edu.asu.plp.tool.backend.mipsisa.assembler2.instructions;

import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;

@FunctionalInterface 
public interface AssemblerDirectiveStep {

		public String perform() throws AssemblerException;

}
