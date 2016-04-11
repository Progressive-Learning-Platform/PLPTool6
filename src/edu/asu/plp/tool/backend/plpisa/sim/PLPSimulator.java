package edu.asu.plp.tool.backend.plpisa.sim;

import java.util.List;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Simulator;
import edu.asu.plp.tool.backend.plpisa.PLPASMImage;
import edu.asu.plp.tool.backend.plpisa.sim.stages.ExecuteStage;
import edu.asu.plp.tool.backend.plpisa.sim.stages.InstructionDecodeStage;
import edu.asu.plp.tool.backend.plpisa.sim.stages.MemoryStage;
import edu.asu.plp.tool.backend.plpisa.sim.stages.Stage;
import edu.asu.plp.tool.backend.plpisa.sim.stages.WriteBackStage;

/**
 * Port of old PLP-Tool simulator with minor improvements
 *
 * @author Morgan Nesbitt
 */
public class PLPSimulator implements Simulator
{
	/**
	 * Used to prevent unknown messages to simulator only events.
	 * Used for internal stage communication.
	 */
	private EventBus simulatorBus;
	
	private PLPASMImage assembledImage;
	
	private MemoryModule32Bit regFile;
	private ProgramCounter programCounter;
	
	private Stage instructionDecodeStage;
	private Stage executeStage;
	private Stage memoryStage;
	private Stage writeBackStage;
	
	private SimulatorStatusManager statusManager;
	
	private int interruptRequestStateMachine;
	private long interrutReturnAddress;
	private long interruptAcknowledge;
	
	private long externalInterrupt;
	
	private int instructionsIssued;
	
	private long startAddress;
	
	/**
	 * Used to evaluate breakpoints.
	 * <p>
	 * Represents visible instruction in editor, since pseudo instruction break down into
	 * base instructions.
	 */
	private long asmInstructionAddress;
	
	private boolean isBranched;
	private long branchDestination;
	
	// A sim bus?
	// breakpoint array?
	
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
		statusManager.advanceFlags();
		
		instructionsIssued++;
		
		return false;
	}
	
	@Override
	public void reset()
	{
		loadProgram(assembledImage);
	}
	
	public void softReset()
	{
		programCounter.reset(startAddress);
		flushPipeline();
		
		statusManager.isExecuteContinuing = false;
		statusManager.isExecuteStalled = false;
		statusManager.isInstructionDecodeStalled = false;
		
		// TODO Potentially print from console
	}
	
	@Override
	public boolean loadProgram(ASMImage assembledImage)
	{
		if (assembledImage == null)
			return false;
			
		this.assembledImage = (PLPASMImage) assembledImage;
		
		setupFromImage();
		
		return true;
	}
	
	private void setupFromImage()
	{
		// Clears Ram, Zeroes out register file, reloads program to memory
		// Resets program counter, flushes pipeline, clears flags, resets statistics
		
		// TODO get from assembled Image
		this.startAddress = 0;
		
		// Zero register file
		
		externalInterrupt = 0;
		interruptAcknowledge = 0;
		interruptRequestStateMachine = 0;
		
		programCounter.reset(startAddress);
		
		asmInstructionAddress = startAddress;
		
		instructionsIssued = 0;
		
		isBranched = false;
		
		// TODO clear stages
		
		flushPipeline();
		
		// TODO Maybe print simulator reset to console
		
		// TODO Load program to bus?
		
		statusManager.reset();
	}
	
	private void flushPipeline()
	{
		// TODO flushPipeline
	}
	
	private void initialize()
	{
		simulatorBus = new EventBus();
		
		assembledImage = null;
		
		statusManager = new SimulatorStatusManager();
		
		instructionDecodeStage = new InstructionDecodeStage(simulatorBus);
		executeStage = new ExecuteStage(simulatorBus);
		memoryStage = new MemoryStage(simulatorBus);
		writeBackStage = new WriteBackStage(simulatorBus);

		// FIXME new MemModule(0,32,false);
		regFile = null;
		programCounter = new ProgramCounter(0);
	}
	
	@Override
	public boolean isRunning()
	{
		return statusManager.isRunning();
	}
	
	@Override
	public boolean isPaused()
	{
		return statusManager.isPaused();
	}
	
	@Override
	public boolean pause()
	{
		return false;
	}
	
	@Override
	public boolean isSimModeEnabled()
	{
		return statusManager.isSimEnabled();
	}
	
	@Override
	public boolean toggleSimMode()
	{
		return statusManager.toggleSimMode();
	}
	
	@Override
	public boolean isProgramLoaded()
	{
		if (assembledImage == null)
			return false;
			
		return true;
	}
	
}
