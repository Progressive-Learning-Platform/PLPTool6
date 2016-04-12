package edu.asu.plp.tool.backend.plpisa.sim.stages.events;

import edu.asu.plp.tool.backend.plpisa.sim.stages.state.ExecuteStageState;

public class InstructionDecodeCompletion
{
	private boolean isHot;
	private boolean willClearLogic;
	private ExecuteStageState postExecuteStageState;
	
	public InstructionDecodeCompletion()
	{
		this.willClearLogic = false;
		this.postExecuteStageState = null;
		this.isHot = false;
	}
	
	/**
	 * If is true, set execute hot to true.
	 * Otherwise do nothing.
	 * @param isHot
	 */
	public void setHot(boolean isHot)
	{
		this.isHot = isHot;
	}

	public void clearLogic()
	{
		willClearLogic = true;
	}

	public void setPostExecuteStageState(ExecuteStageState postExecuteStageState)
	{
		this.postExecuteStageState = postExecuteStageState;
	}

}
