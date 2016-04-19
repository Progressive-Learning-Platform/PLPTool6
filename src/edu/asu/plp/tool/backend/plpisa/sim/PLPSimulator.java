package edu.asu.plp.tool.backend.plpisa.sim;

import java.util.ArrayList;
import java.util.Arrays;
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
	 * Used to prevent unknown messages to simulator only events. Used for internal stage
	 * communication.
	 */
	private EventBus simulatorBus;
	
	private PLPASMImage assembledImage;
	
	private MemoryModule32Bit regFile;
	private ProgramCounter programCounter;
	
	private Stage instructionDecodeStage;
	private Stage executeStage;
	private Stage memoryStage;
	private Stage writeBackStage;
	
	private List<Stage> stages;
	
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
		((WriteBackStage) writeBackStage).retireInstruction();
		asmInstructionAddress = -1;
		long oldPc = programCounter.evaluate();
		
		if (statusManager.isFunctional())
			return stepFunctional();
			
		/****************** RISING EDGE OF THE CLOCK **********************/
		
		// Propagate values
		// move next* values to the output side of the pipeline registers
		if (writeBackStage.isHot())
			writeBackStage.clock();
		if (memoryStage.isHot())
			memoryStage.clock();
		if (executeStage.isHot())
			executeStage.clock();
		if (instructionDecodeStage.isHot())
			instructionDecodeStage.clock();
			
		// clock pc for next instruction
		if (!statusManager.isInstructionDecodeStalled)
			programCounter.clock();
			
		/****************** FALLING EDGE OF THE CLOCK *********************/
		
		// Evaluate stages
		// produce next* values for the input side of the pipeline registers
		// that will be used in the next cycle
		writeBackStage.evaluate();
		memoryStage.evaluate();
		executeStage.evaluate();
		instructionDecodeStage.evaluate();
		
		// Program counter update logic (input side IF)
		boolean nonNegativeInstructionAddress = executeStage
				.getState().currentInstructionAddress != -1;
		boolean ct1Pcsrc = executeStage.getState().ct1Pcsrc == 1;
		
		if (executeStage.isHot() && nonNegativeInstructionAddress && ct1Pcsrc)
		{
			programCounter.write(executeStage.getState().ct1Branchtarget);
		}
		else if (executeStage.isHot() && nonNegativeInstructionAddress
				&& executeStage.getState().ct1Jump == 1)
		{
			programCounter.write(executeStage.getState().ct1JumpTarget);
		}
		else if (!statusManager.isInstructionDecodeStalled)
		{
			programCounter.write(programCounter.evaluate() + 4);
		}
		
		// TODO bus
		// Evaluate modules attached to the bus
		// bus.eval();
		// Evalulate interrupt controller again to see if anything raised an IRQ
		// (PLPSimBus evaluates modules from index 0 upwards)
		// bus.eval(0);
		
		/*
		 * STALL ROUTINES
		 *
		 * By default, the CPU here will just get the next instruction as determined by
		 * the current value of PC. This is done by updating the input side of the IF/ID
		 * pipeline register (decode stage next* values) by calling the fetch() function.
		 *
		 * There are three conditions where this is not true:
		 *
		 * 1. IF stall due to jumps (decode stage next* values will take a bubble and no
		 * new instruction will be fetched, i.e. fetch() function will not be called in
		 * this case).
		 *
		 * 2. Stall on EX stage due to load-use hazard. Insert bubble for EX in the next
		 * cycle by making sure execute stage next* values will not change the CPU state
		 * (no write to memory and register, no branch and jump). Then, the IF/ID pipeline
		 * is turned off by making instruction decode stage.isHot() = false. This will
		 * keep the instruction being decoded to stay in that stage. fetch() will be
		 * called to fill the IF stage or the instruction decode.next* values. Finally we
		 * rewrite PC so we don't skip an instruction since PC is already clocked due to
		 * the procedural nature of our simulation engine. In a real machine, the PC would
		 * have held its value.
		 *
		 * 3. An interrupt service is requested. The interrupt service is a 3-step
		 * process. First, when a request is detected in the end of a clock cycle, the CPU
		 * checks whether a jump or a branch has been executed in the EX stage. If yes,
		 * the CPU will wait another cycle before servicing (the next instruction is
		 * fetched in the IF stage as usual). If a jump or branch is not in the EX stage,
		 * the CPU will go ahead and flush the IF, ID, and EX stages for the next
		 * cycle.and record the address of the instruction that was in the EX stage. In
		 * the next cycle, a jalr $ir, $iv instruction is injected in the IF stage
		 * (instruction decode stage.next*) with the return value being the recorded
		 * address minus 4 to offset the plus 4 of the PC logic. During the third cycle,
		 * the CPU injects a bubble for the jump and resumes normal operation in the ISR
		 * space.
		 */
		
		// We're stalled in the NEXT cycle, do not fetch new instruction
		if (statusManager.isInstructionDecodeStalled
				&& !statusManager.isExecuteContinuing)
		{
			statusManager.isInstructionDecodeStalled = false;
			instructionDecodeStage.getState().nextInstruction = 0;
			instructionDecodeStage.getState().nextInstructionAddress = programCounter
					.input();
			instructionDecodeStage.getState().hot = true;
			instructionDecodeStage.getState().nextBubble = true;
			
			return true;
		}
		// ex_stall, clear id/ex register
		else if (statusManager.isExecuteStalled)
		{
			statusManager.isExecuteStalled = false;
			statusManager.isExecuteContinuing = true;
			
			// Insert bubble for EX stage in the next cycle
			executeStage.getState().nextInstruction = 0;
			executeStage.getState().nextInstructionAddress = -1;
			executeStage.getState().nextForwardCt1Memwrite = 0;
			executeStage.getState().nextForwardCt1Regwrite = 0;
			executeStage.getState().nextCt1Branch = 0;
			executeStage.getState().nextCt1Jump = 0;
			
			executeStage.getState().hot = true;
			executeStage.getState().nextBubble = true;
			
			fetch();
			
			instructionDecodeStage.getState().hot = false;
			
			programCounter.write(oldPc + 4);
			
			return true;
		}
		// resume from ex_stall, turn on id/ex register
		else if (statusManager.isExecuteContinuing)
		{
			executeStage.getState().hot = true;
			statusManager.isExecuteContinuing = false;
			
			return fetch();
		}
		else if(interruptRequestStateMachine == 2)
		{
			
		}
		else if(interruptRequestStateMachine == 1)
		{
			
		}
		else if(interruptRequestStateMachine == 3)
		{
			
		}
		else
			return fetch();
		
		return true;
	}
	
	private boolean stepFunctional()
	{
		return false;
	}
	
	private boolean fetch()
	{
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
		
		stages = Arrays.asList(instructionDecodeStage, executeStage, memoryStage,
				writeBackStage);
				
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
