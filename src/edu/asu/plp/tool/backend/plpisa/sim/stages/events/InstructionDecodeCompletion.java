package edu.asu.plp.tool.backend.plpisa.sim.stages.events;

import edu.asu.plp.tool.backend.plpisa.sim.stages.state.ExecuteStageState;

public class InstructionDecodeCompletion
{
	private boolean willClearLogic;
	private ExecuteStageState postExecuteStageState;
	
	public InstructionDecodeCompletion()
	{
		this.willClearLogic = false;
		this.postExecuteStageState = null;
	}

	public void clearLogic()
	{
		willClearLogic = true;
	}

	public void setPostExecuteStageState(ExecuteStageState postExecuteStageState)
	{
		this.postExecuteStageState = postExecuteStageState;
	}

	public boolean willClearLogic()
	{
		return willClearLogic;
	}
	
	public ExecuteStageState getPostState()
	{
		return postExecuteStageState;
	}

}
