package edu.asu.plp.tool.backend.isa;

public abstract class Simulator
{
	protected long startAddress;
	
	public Simulator()
	{
		startAddress = 0;
	}

	public abstract boolean isRunning();
	
	public abstract boolean run();
	
	public abstract boolean step();
	
	public abstract void reset();

	public abstract boolean isPaused();
	
	public abstract boolean pause();
	
	public abstract boolean isSimModeEnabled();
	
	public abstract boolean toggleSimMode();
	
	public abstract boolean isProgramLoaded();
	
	public abstract boolean loadProgram(ASMImage assembledImage);
}
