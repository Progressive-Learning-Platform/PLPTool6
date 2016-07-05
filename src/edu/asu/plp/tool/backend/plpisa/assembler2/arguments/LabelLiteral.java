package edu.asu.plp.tool.backend.plpisa.assembler2.arguments;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;

public class LabelLiteral implements Argument{
	
	String rawValue;
	
	public LabelLiteral(String rawValue)
	{
		this.rawValue = rawValue;
	}

	@Override
	public int encode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String raw() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArgumentType getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
