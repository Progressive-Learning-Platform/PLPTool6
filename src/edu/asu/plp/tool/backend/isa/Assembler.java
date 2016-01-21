package edu.asu.plp.tool.backend.isa;

import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;

public interface Assembler
{
	ASMImage assemble() throws AssemblerException;
}
