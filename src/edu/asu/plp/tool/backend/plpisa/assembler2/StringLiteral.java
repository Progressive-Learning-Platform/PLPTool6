package edu.asu.plp.tool.backend.plpisa.assembler2;

public class StringLiteral implements Argument
{
	private String rawValue;
	
	public StringLiteral(String rawValue)
	{
		this.rawValue = rawValue;
	}

	@Override
	public int encode()
	{
		// exclude the quotes (first and last character) in value
		int lastIndex = rawValue.length() - 1;
		String value = rawValue.substring(1, lastIndex);
		
		// TODO Auto-generated method stub return 0;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public boolean isRegister()
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public boolean isNumberLiteral()
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public boolean isStringLiteral()
	{
		// TODO Auto-generated method stub return false;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public String raw()
	{
		// TODO Auto-generated method stub return null;
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
}
