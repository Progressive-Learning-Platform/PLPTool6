package edu.asu.plp.tool.backend.plpisa.sim;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Morgan Nesbitt
 */
public class SimulatorStatusManager
{
	// Stallers
	boolean isInstructionDecodeStalled;
	boolean isExecuteStalled;
	
	// Continuers
	boolean isExecuteContinuing;
	
	// Fowarding Flags
	// TODO Fix Names
	boolean mem_mem;
	boolean mem_ex;
	boolean mem_ex_lw;
	boolean ex_ex;
	
	// Simulator Running Statuses
	boolean isSimulationRunning;
	boolean hasSimulationStarted;
	boolean isSimEnabled;
	
	// Flag holders
	long currentFlags;
	long previousFlags;
	
	boolean isFunctional;
	boolean willSimDumpTraceOnFailedEvaluation;
	boolean willSimAllowExecutionOfArbitaryMem;
	
	SimulatorStatusManager()
	{
		isInstructionDecodeStalled = false;
		isExecuteStalled = false;
		
		isExecuteContinuing = false;
		
		isSimulationRunning = false;
		hasSimulationStarted = false;
		isSimEnabled = false;
		
		clearFlags();
		
		mem_mem = true;
		mem_ex = true;
		mem_ex_lw = true;
		ex_ex = true;
		
		isFunctional = false;
		willSimDumpTraceOnFailedEvaluation = false;
		willSimAllowExecutionOfArbitaryMem = false;
	}
	
	public boolean toggleSimMode()
	{
		isSimEnabled = !isSimEnabled;
		return isSimEnabled;
	}
	
	public boolean isPaused()
	{
		return (hasSimulationStarted && !isSimulationRunning);
	}
	
	public boolean isRunning()
	{
		return (hasSimulationStarted && isSimulationRunning);
	}
	
	public void clearFlags()
	{
		currentFlags = (long) 0;
		previousFlags = (long) 0;
	}
	
	public boolean isSimEnabled()
	{
		return isSimEnabled;
	}
	
	public void reset()
	{
		isExecuteContinuing = false;
		isExecuteStalled = false;
		isInstructionDecodeStalled = false;
		
		clearFlags();
	}
	
	public void advanceFlags()
	{
		previousFlags = currentFlags;
		currentFlags = (long) 0;
	}
	
	public boolean isFunctional()
	{
		return isFunctional;
	}
}
