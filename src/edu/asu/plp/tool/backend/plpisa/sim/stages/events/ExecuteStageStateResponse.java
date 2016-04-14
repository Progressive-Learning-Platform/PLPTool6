package edu.asu.plp.tool.backend.plpisa.sim.stages.events;

import edu.asu.plp.tool.backend.plpisa.sim.stages.state.ExecuteStageState;

public class ExecuteStageStateResponse
{
	private ExecuteStageState state;
	
	public ExecuteStageStateResponse(ExecuteStageState state)
	{
		this.state = state;
	}

	public ExecuteStageState getExecuteStageState()
	{
		return state;
	}

}
