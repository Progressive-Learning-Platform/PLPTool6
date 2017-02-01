package edu.asu.plp.tool.backend.mipsisa;

import edu.asu.plp.tool.backend.MIPS;
import edu.asu.plp.tool.backend.isa.LanguageSpecification;

public class MIPSLanguageSpecification extends LanguageSpecification
{

	public MIPSLanguageSpecification()
	{
		super(MIPS.NAME, MIPS.MAJOR_VERSION, MIPS.MINOR_VERSION);
	}
}
