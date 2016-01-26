package edu.asu.plp.tool.backend.plpisa.sim;

import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Simulator;
import edu.asu.plp.tool.backend.isa.exceptions.SimulatorException;
import edu.asu.plp.tool.backend.plpisa.PLPASMImage;

public class PLPSimulator implements Simulator
{
	private PLPASMImage assembledImage;

	private ProgramCounter programCounter;

	private Stage instructionDecodeStage;
	private Stage executeStage;
	private Stage memoryStage;
	private Stage writeBackStage;

	private boolean isSimulationRunning;
	private boolean hasSimulationStarted;
	private boolean isSimEnabled;

	public PLPSimulator()
	{
		super();
		initialize();
	}

	@Override
	public boolean run()
	{
		return false;
	}

	@Override
	public boolean step()
	{
		return false;
	}

	@Override
	public void reset()
	{

	}

	@Override
	public boolean isRunning()
	{
		return (hasSimulationStarted && isSimulationRunning);
	}

	@Override
	public boolean isPaused()
	{
		return (hasSimulationStarted && !isSimulationRunning);
	}

	@Override
	public boolean pause()
	{
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
		if (assembledImage == null)
			return false;

		return true;
	}

	@Override
	public boolean loadProgram(ASMImage assembledImage)
	{
		if (assembledImage == null)
			return false;

		this.assembledImage = (PLPASMImage) assembledImage;
		return true;
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

		programCounter = new ProgramCounter(0);
	}

}
