package edu.asu.plp.tool.backend.plpisa.assembler2.arguments;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;

public class LabelLiteral implements Argument{
	
	String rawValue;
	long addressValue;
	
	public LabelLiteral(String rawValue)
	{
		this.rawValue = rawValue;
		addressValue = -1;
	}
	
	public LabelLiteral(String rawValue, long addressValue)
	{
		this.rawValue = rawValue;
		this.addressValue = addressValue;
	}

	@Override
	public int encode() {
		return (int)this.addressValue;
	}

	@Override
	public String raw() {
		return this.rawValue;
	}

	@Override
	public ArgumentType getType() {
		return ArgumentType.LABEL_LITERAL;
	}

}
