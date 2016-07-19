package edu.asu.plp.tool.backend.plpisa.assembler2.arguments;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;

public class LabelLiteral implements Argument{
	
	String rawValue;
	long addressValue;
	long currentLocation;
	
	public LabelLiteral(String rawValue)
	{
		this.rawValue = rawValue;
		addressValue = -1;
		currentLocation = -1;
	}
	
	public LabelLiteral(String rawValue, long addressValue)
	{
		this.rawValue = rawValue;
		this.addressValue = addressValue;
	}
	
	public LabelLiteral(String rawValue, long symbolLocation, long currentLocation)
	{
		this.rawValue = rawValue;
		this.addressValue = symbolLocation;
		this.currentLocation = currentLocation;
	}

	@Override
	public int encode() {
		return (int)this.addressValue;
	}
	
	public int getCurrentInstructionLocation()
	{
		return (int)this.currentLocation;
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
