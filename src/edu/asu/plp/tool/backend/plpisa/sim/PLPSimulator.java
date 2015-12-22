package edu.asu.plp.tool.backend.plpisa.sim;

import plptool.mips.SimCore.mem;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Simulator;

public class PLPSimulator implements Simulator
{
	private ASMImage assembledImage;
	private boolean isSimulationRunning;
	private boolean hasSimulationStarted;
	private boolean isSimEnabled;
	
	private Stage instructionDecodeStage;
	private Stage executeStage;
	private Stage memoryStage;
	private Stage writeBackStage;
	
	public PLPSimulator()
	{
		initialize();
	}

	private void initialize()
	{
		assembledImage = null;
		
		isSimulationRunning = false;
		hasSimulationStarted = false;
		isSimEnabled = false;
		
		instructionDecodeStage = new InstructionDecodeStage();
		executeStage = new ExecuteStage();
		memoryStage = new MemoryStage();
		writeBackStage = new WriteBackStage();
	}

	@Override
	public boolean isRunning()
	{
		return (hasSimulationStarted && isSimulationRunning);
	}

	@Override
	public boolean run()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean step()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isPaused()
	{
		return (hasSimulationStarted && !isSimulationRunning);
	}

	@Override
	public boolean pause()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSimModeEnabled()
	{
		return isSimEnabled;
	}
	
	@Override
	public boolean toggleSimMode()
	{
		isSimEnabled = !isSimEnabled;
		return isSimEnabled;
	}

	@Override
	public boolean isProgramLoaded()
	{
		if(assembledImage == null)
			return false;
		
		return true;
	}

	@Override
	public boolean loadProgram(ASMImage assembledImage)
	{
		if(assembledImage == null)
			return false;
		
		this.assembledImage = assembledImage;
		return true;
	}
	
}
