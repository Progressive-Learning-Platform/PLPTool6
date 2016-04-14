package edu.asu.plp.tool.backend.plpisa.sim.stages;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.plpisa.sim.stages.events.InstructionDecodeCompletion;

public class ExecuteStage implements Stage
{
	private EventBus bus;
	private ExecuteEventHandler eventHandler;
	
	public ExecuteStage(EventBus simulatorBus)
	{
		this.bus = simulatorBus;
		this.eventHandler =  new ExecuteEventHandler(); 
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
	
	public class ExecuteEventHandler
	{
		private ExecuteEventHandler()
		{
			
		}
		
		public void instructionDecodeCompletionEvent(InstructionDecodeCompletion event)
		{
			ExecuteStageState postState = event.getPostState();
			
			if(event.willClearLogic())
			{
				postState.nextForwardCt1Memtoreg = 0;
				postState.nextForwardCt1Regwrite = 0;
				postState.nextForwardCt1Memwrite = 0;
				postState.nextForwardCt1Memread = 0;
				postState.nextForwardCt1Jal = 0;
				postState.nextCt1AluSrc = 0;
				postState.nextCt1Regdest = 0;
				postState.nextCt1Jump = 0;
				postState.nextCt1Branch = 0;
			}
		}
		
		public void stateRequested(ExecuteStageStateRequest event)
		{
			bus.post(new ExecuteStageStateResponse(state.clone()));
		}
	}
	
}
