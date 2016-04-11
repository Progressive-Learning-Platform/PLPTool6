package edu.asu.plp.tool.backend.plpisa.sim.stages;

import com.google.common.eventbus.EventBus;

public class WriteBackStage implements Stage
{
	private EventBus bus;

	public WriteBackStage(EventBus simulatorBus)
	{
		this.bus = simulatorBus;
	}
	
	@Override
	public void evaluate()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clock()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void printVariables()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void printNextVariables()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String printInstruction()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}
	
}
