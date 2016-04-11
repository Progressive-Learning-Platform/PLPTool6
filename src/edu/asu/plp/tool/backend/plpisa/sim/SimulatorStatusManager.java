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
	private List<SimulatorFlag> currentFlags;
	private List<SimulatorFlag> previousFlags;
	
	SimulatorStatusManager()
	{
		isInstructionDecodeStalled = false;
		isExecuteStalled = false;
		
		isExecuteContinuing = false;
		
		isSimulationRunning = false;
		hasSimulationStarted = false;
		isSimEnabled = false;
		
		currentFlags = new ArrayList<>();
		previousFlags = new ArrayList<>();
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
		currentFlags.clear();
		previousFlags.clear();
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
		currentFlags = new ArrayList<>();
	}
}
