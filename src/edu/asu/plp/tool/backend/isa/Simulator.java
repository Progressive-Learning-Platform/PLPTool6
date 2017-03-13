package edu.asu.plp.tool.backend.isa;

public interface Simulator
{
	boolean isRunning();
	
	void run();
	
	boolean step();
	
	void reset();
	
	boolean isPaused();
	
	void pause();
	
	boolean isSimModeEnabled();
	
	boolean toggleSimMode();
	
	boolean isProgramLoaded();
	
	boolean loadProgram(ASMImage assembledImage);
	
	RegisterFile getRegisterFile();
	
	AddressBus getAddressBus();
	
	void setIRQ(long value);
	void maskIRQ(long value);
}
