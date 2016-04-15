package edu.asu.plp.tool.backend.plpisa.sim.stages;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.plpisa.sim.stages.events.MemoryStageStateRequest;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.MemoryStageStateResponse;
import edu.asu.plp.tool.backend.plpisa.sim.stages.state.CpuState;

public class MemoryStage implements Stage
{
	private EventBus bus;
	private MemoryEventHandler eventHandler;
	
	private CpuState state;
	
	public MemoryStage(EventBus simulatorBus)
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
	
	public class MemoryEventHandler
	{
		private MemoryEventHandler()
		{
			
		}

		public void stateRequested(MemoryStageStateRequest event)
		{
			bus.post(new MemoryStageStateResponse(state.clone()));
		}
	}
	
}
