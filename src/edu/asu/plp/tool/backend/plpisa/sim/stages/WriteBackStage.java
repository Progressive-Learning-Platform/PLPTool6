package edu.asu.plp.tool.backend.plpisa.sim.stages;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.plpisa.sim.stages.events.WriteBackStageStateRequest;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.WriteBackStageStateResponse;
import edu.asu.plp.tool.backend.plpisa.sim.stages.state.CpuState;

public class WriteBackStage implements Stage
{
	private EventBus bus;
	private WriteBackEventHandler eventHandler;
	
	private CpuState state;

	public WriteBackStage(EventBus simulatorBus)
	{
		this.bus = simulatorBus;
		this.eventHandler = new WriteBackEventHandler();
		
		this.bus.register(eventHandler);
		
		this.state = new CpuState();
		
		reset();
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
	
	public class WriteBackEventHandler
	{
		private WriteBackEventHandler()
		{
		
		}
		
		public void stateRequested(WriteBackStageStateRequest event)
		{
			bus.post(new WriteBackStageStateResponse(state.clone()));
		}
	}
	
}
