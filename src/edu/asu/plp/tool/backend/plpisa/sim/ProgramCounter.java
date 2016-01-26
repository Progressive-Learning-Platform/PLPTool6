package edu.asu.plp.tool.backend.plpisa.sim;

public class ProgramCounter
{
	private long data;
	
	public ProgramCounter(long data)
	{
		this.data = data;
	}
	
	public void write(long data)
	{
		this.data = data;
	}
	
	public long input()
	{
		return data;
	}
	
	public long evaluate()
	{
		throw new UnsupportedOperationException("Evaluate not implemented");
	}
	
	public void clock()
	{
		;
	}
	
	public void reset(long data)
	{
		;
		this.data = data;
	}
}
