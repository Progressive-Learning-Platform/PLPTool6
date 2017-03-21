package edu.asu.plp.tool.backend.mipsisa.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Simulator;
import edu.asu.plp.tool.backend.isa.exceptions.SimulatorException;
import edu.asu.plp.tool.backend.mipsisa.InstructionExtractor;
import edu.asu.plp.tool.backend.mipsisa.MIPSASMImage;
import edu.asu.plp.tool.backend.mipsisa.sim.stages.ExecuteStage;
import edu.asu.plp.tool.backend.mipsisa.sim.stages.InstructionDecodeStage;
import edu.asu.plp.tool.backend.mipsisa.sim.stages.MemoryStage;
import edu.asu.plp.tool.backend.mipsisa.sim.stages.Stage;
import edu.asu.plp.tool.backend.mipsisa.sim.stages.WriteBackStage;
import javafx.beans.property.LongProperty;
import javafx.util.Pair;

/**
 * Port of old PLP-Tool simulator with minor improvements
 *
 * @author Morgan Nesbitt
 */
public class MIPSSimulator implements Simulator
{
	/**
	 * Used to prevent unknown messages to simulator only events. Used for internal stage
	 * communication.
	 */
	private EventBus simulatorBus;

	private MIPSASMImage assembledImage;

	//private MemoryModule32Bit regFile;
	private MIPSRegFile regFile;
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

	public MIPSAddressBus addressBus;



	/**
	 * Used to evaluate breakpoints.
	 * <p>
	 * Represents visible instruction in editor, since pseudo instruction break down into
	 * base instructions.
	 */
	private long asmInstructionAddress;
	private BreakpointModule breakpoints;

	private boolean isBranched;
	private long branchDestination;

	private ALU alu;


	// A sim bus?
	// breakpoint array?

	public MIPSSimulator()
	{
		super();
		initialize();
		breakpoints = new BreakpointModule();
	}

	@Override
	public boolean run()
	{
		while(instructionsIssued < assembledImage.getDisassemblyInfo().size()){
		if(breakpoints.hasBreakpoint()){
			if(breakpoints.isBreakpoint(asmInstructionAddress)){ // asmInstructionAddress ?
				statusManager.isSimulationRunning = false;

			}else{
				step();
			}
		}
		}
		return false;
	}


	public MIPSRegFile getRegisterFile()
	{
		return regFile;
	}

	public MIPSAddressBus getAddressBus()
	{
		return addressBus;
	}

	@Override
	public boolean step()
	{
		statusManager.advanceFlags();
		instructionsIssued++;
		((WriteBackStage) writeBackStage).retireInstruction();
		asmInstructionAddress = -1;
		long oldPc = programCounter.evaluate();

		statusManager.isFunctional = true;

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
		addressBus.eval();
		// Evaluate interrupt controller again to see if anything raised an IRQ
		// (PLPSimBus evaluates modules from index 0 upwards)
		// bus.eval(0);
		addressBus.eval(0);

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
		else if (interruptRequestStateMachine == 2)
		{
			System.out.println("IRQ service, int_inject 2->1");
			instructionDecodeStage.getState().nextInstruction = 0x0380f009L; // jalr $ir,
																				// $iv
			instructionDecodeStage.getState().nextInstructionAddress = 0;
			instructionDecodeStage.getState().nextCt1Pcplus4 = interrutReturnAddress - 4;
			instructionDecodeStage.getState().hot = true;

			interruptRequestStateMachine--;
			return true;
		}
		else if (interruptRequestStateMachine == 1)
		{
			System.out.println("IRQ service, int_inject 1->0");
			instructionDecodeStage.getState().nextInstruction = 0;
			instructionDecodeStage.getState().nextInstructionAddress = -1;
			instructionDecodeStage.getState().hot = true;

			interruptRequestStateMachine--;
			interruptAcknowledge = 0;
			return true;
		}
		else if (interruptRequestStateMachine == 3)
		{
			System.out.println("IRQ Triggered.");
			long diff = programCounter.input()
					- executeStage.getState().nextInstructionAddress;
			System.out.println("InstructionAddress diff: " + diff);
			statusManager.currentFlags |= SimulatorFlag.PLP_SIM_IRQ.getFlag();

			if (diff == 8)
			{
				statusManager.currentFlags |= SimulatorFlag.PLP_SIM_IRQ_SERVICED
						.getFlag();
				interrutReturnAddress = memoryStage.getState().nextInstructionAddress;
				interruptRequestStateMachine--;
				interruptAcknowledge = 1;
				System.out.println(String.format("%s 0x%02x",
						"IRQ service started, int_inject = 2, irq_ret = ",
						interrutReturnAddress));

				instructionDecodeStage.getState().nextInstruction = 0;
				instructionDecodeStage.getState().nextInstructionAddress = -1;

				executeStage.getState().nextInstruction = 0;
				executeStage.getState().nextForwardCt1Regwrite = 0;
				executeStage.getState().nextForwardCt1Memwrite = 0;
				executeStage.getState().nextCt1Branch = 0;
				executeStage.getState().nextCt1Jump = 0;
				executeStage.getState().nextInstructionAddress = -1;

				memoryStage.getState().nextInstruction = 0;
				memoryStage.getState().nextForwardCt1Regwrite = 0;
				memoryStage.getState().nextCt1Memwrite = 0;
				memoryStage.getState().nextInstructionAddress = -1;

				instructionDecodeStage.getState().hot = true;
				executeStage.getState().hot = true;
				memoryStage.getState().hot = true;

				return true;
			}
			else
				return fetch();
		}
		else
			return fetch();
	}

	private boolean stepFunctional()
	{
		programCounter.clock();
		fetch(); // get the instruction

		long instruction = instructionDecodeStage.getState().nextInstruction;

		//fill a nop for our interrupts jalr branch delay slot
		if(interruptRequestStateMachine == 1)
		{
			instruction = 0;
			interruptRequestStateMachine = 0;
			interruptAcknowledge = 0; // we are ready to handle interrupt requests again
		}

		long pcplus4 = programCounter.evaluate() + 4;

		//pcplus4, default execution
		if(!isBranched && interruptRequestStateMachine != 3)
			programCounter.write(pcplus4);
		else if(!isBranched && interruptRequestStateMachine == 3)
		{
			//System.out.println("INT REQ - ret_addr(pcplus4 - 4) = " + );
			statusManager.currentFlags |= SimulatorFlag.PLP_SIM_IRQ.getFlag();

            // rewrite instruction to jalr $iv, $ir
			instruction = 0x0380f009L;
			instructionDecodeStage.getState().nextInstructionAddress = 0;
			pcplus4 -= 8; // replay the discarded instruction after return from IRQ (jalr adds 4)
			interruptRequestStateMachine = 1;
		}
		else
		{
			//System.out.println("Delay slot - branching to = ");
			programCounter.write(branchDestination);
			isBranched = false;
		}

		//decode instruction
		int opcode = InstructionExtractor.opcode(instruction);
		byte rs = InstructionExtractor.rs(instruction);
		byte rd = InstructionExtractor.rd(instruction);
		byte rt = InstructionExtractor.rt(instruction);
		byte funct = InstructionExtractor.funct(instruction);
		long imm = InstructionExtractor.imm(instruction);
		long jaddr = InstructionExtractor.jaddr(instruction);

		//TODO memory read
		long s = regFile.read(rs);	//0; //regFile.read(rs)
		long t = regFile.read(rt);	//0; //regFile.read(rt)
		long s_imm = (short) imm & 0xffffffffL;
		long alu_result;

		//execute
		try 
		{
			if(opcode == 0)
			{
				if(funct == 0x08 || funct == 0x09) //JR
				{
					isBranched = true;
					branchDestination = s;
	
					if(funct  == 0x09) //jalr
					{
						//TODO memory write
						regFile.write(rd, (int)pcplus4 + 4, false);
						//regFile.write(rd, pcplus4 + 4, false);
					}
				}
				else if (funct == 0x12 || funct == 0x10 || funct == 0x11 || funct == 0x13) //mflo, mfhi, mtlo, mthi
				{
					boolean lo = true;
					boolean hi = false;
					boolean from  = true;
					boolean to = false;
					if (funct == 0x12) {
						regFile.write(rd, lo, from);
					} else if (funct == 0x10) {
						regFile.write(rd, hi, from);
					} else if (funct == 0x13) {
						regFile.write(rd, lo, to);
					} else {
						regFile.write(rd, hi, to);					
					}
				}
				else if (funct == 0x19 || funct == 0x1B) //multu, divu
				{
					alu_result = alu.evaluate(s, t, instruction);
					long loResult = alu_result & 0xffffffffL;
					long hiResult = (alu_result & 0xffffffff00000000L) >> 32;
					regFile.write(loResult, hiResult);
				}
				else
				{
					alu_result = alu.evaluate(s, t, instruction);
					alu_result &= 0xffffffffL;
	
					//TODO memory write
					regFile.write(rd, (int)alu_result, false);
					//regFile.write(rd, alu_result, false);
				}
			}
			else if(opcode == 0x04) //beq
			{
				if(s == t)
				{
					isBranched = true;
					branchDestination = (pcplus4 + (s_imm << 2)) & 0xffffffffL;
				}
			}
			else if(opcode == 0x05) //bne
			{
				if(s != t)
				{
					isBranched = true;
					branchDestination = (pcplus4 + (s_imm << 2)) & 0xffffffffL;
				}
			}
			else if(opcode == 0x23) //lw
			{
				//TODO bus read
				//Long data = (Long) 0L; //bus.read((s + s_imm) & 0xffffffffL)
				//Object odata = addressBus.read((s + s_imm) & 0xffffffffL);
				Long data = (Long)addressBus.read((s + s_imm) & 0xffffffffL);
				//Integer data = 0;
				if(data == null)
				{
					System.out.println("Bus read error");
					return false;
				}
	
				//TODO memory write
				//regFile.write(rt, data, false);
				regFile.write(rt, data.longValue(), false);
			}
			else if(opcode == 0x2B) //sw
			{
				//TODO bus write
				//int ret = bus.write((s + s_imm) & 0xffffffffL, regFile.read(rt), false);
				int ret = addressBus.write((s + s_imm) & 0xffffffffL, regFile.read(rt), false);
	
				if(ret > 0)
				{
					System.out.println("Bus write error");
					return false;
				}
			}
			else if(opcode == 0x02 || opcode == 0x03) // j
			{
				isBranched = true;
				branchDestination = jaddr << 2 | (pcplus4 & 0xf0000000L);
	
				if(opcode == 0x03) //jal
				{
					//TODO memory write
					//regFile.write(31, pcplus4 + 4, false);
					regFile.write(31,  (int)pcplus4 + 4, false);
				}
			}
			else if(opcode == 0x0C || opcode == 0x0D || opcode == 0x0E) //ori, andi, xori
			{
				alu_result = alu.evaluate(s, imm, instruction) & 0xffffffffL;
				//TODO memory write
				//regFile.write(rt, alu_result, false);
				regFile.write(rt, alu_result, false);
			}
			else if(opcode == 0x1c) //clz, clo
			{
				alu_result = alu.evaluate(s, 0, instruction);
				regFile.write(rd, alu_result);
			}
			else
			{
				alu_result = alu.evaluate(s, s_imm, instruction);
	
				if(alu_result == -1)
				{
					System.out.println("Unhandled instruction: invalid op-code");
					return false;
				}
	
				alu_result &= 0xffffffffL;
				//TODO memory write
				//regFile.write(rt, alu_result, false);
				regFile.write(rt, (int)alu_result, false);
			}
		} 
		catch (SimulatorException e) 
		{
			// log exceptions
			// TODO: log to PLPTool console?
			System.out.println(e.getLocalizedMessage());
		}

		//TODO Bus actions
		//bus.eval();
		addressBus.eval();

		//Evaluate interrupt controller again to see if anything raised an irq
		//(PLP sim bus evaluates modules from index 0 upwards)
		//bus.eval(0);
		addressBus.eval(0);

		//We have an irq waiting, set ack so the controller wont set another
		//request while we process this one
		if(interruptRequestStateMachine == 3)
		{
			interruptAcknowledge = 1;
		}

		return true;
	}

	/**
	 * Perform an instruction fetch and warm up the decode stage. This function represents
	 * the instruction fetch phase of the PLP CPU core. The only memory element in this
	 * stage is the program counter (and the instruction memory, but that is external).
	 *
	 * @return Returns true for success, false otherwise.
	 */
	private boolean fetch()
	{
		long address = programCounter.evaluate();

		// fetch instruction / frontend stage
		if (address < 0 || !addressBus.isMapped(address))
		{
			if (statusManager.willSimDumpTraceOnFailedEvaluation)
			{
				registersDump();
				System.out.println(String.format("%s 0x%08x",
						"fetch(): PC points to unmapped address. Halt. pc=", address));
				return false;
			}
		}

		//Long ret = (Long) 0L; // (Long) bus.read(address);
		Long val = addressBus.read(address);
		//long val.get();

		if (val == null)
		{
			if (statusManager.willSimDumpTraceOnFailedEvaluation)
			{
				registersDump();
				System.out.println(String.format("%s 0x%08x",
						"fetch(): Unable to fetch next instruction from the bus. pc=",
						address));
				return false;
			}
		}

		if (!addressBus.isInstruction(address))		//!statusManager.willSimAllowExecutionOfArbitaryMem) // !bus.isInstr(address) &&
		{
			System.out.println(String.format("%s %08x",
					"fetch(): Attempted to fetch non-executable memory: pc=", address));
			return false;
		}

		instructionDecodeStage.getState().nextInstruction = val;
		instructionDecodeStage.getState().nextInstructionAddress = address;
		instructionDecodeStage.getState().nextCt1Pcplus4 = address + 4;

		instructionDecodeStage.getState().hot = true;
		instructionDecodeStage.getState().nextBubble = false;
		instructionDecodeStage.getState().ifCount++;

		asmInstructionAddress = address;

		System.out.println(String.format("%s 0x%08x %s 0x%08x", "fetch(): PC input side:",
				programCounter.input(), "- PC output side:", programCounter.evaluate()));

		return true;
	}

	@Override
	public void reset()
	{
		loadProgram(assembledImage);
	}

	private boolean registersDump()
	{
		return false;
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

		this.assembledImage = (MIPSASMImage) assembledImage;

		setupFromImage();

		return true;
	}

	private void setupFromImage()
	{
		// Clears Ram, Zeroes out register file, reloads program to memory
		// Resets program counter, flushes pipeline, clears flags, resets statistics

		// TODO get from assembled Image
		//this.assembledImage.getAssemblyDisassemblyMap().
		this.startAddress = this.assembledImage.getDisassemblyInfo().get(0).getValue().getAddresss();

		// Zero register file
		this.regFile.reset();

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
		//addressBus.issueZeroes(0);
		for(int i = 0; i < assembledImage.getDisassemblyInfo().size(); i++)
		{
			addressBus.write(assembledImage.getDisassemblyInfo().get(i).getValue().getAddresss(), (long)assembledImage.getDisassemblyInfo().get(i).getValue().getInstruction(), true);

		}
	}

	private void flushPipeline()
	{
		//zero out everything

		instructionDecodeStage.getState().nextInstruction = 0;
		instructionDecodeStage.getState().nextInstructionAddress = -1;

		instructionDecodeStage.clock();
		instructionDecodeStage.evaluate();

		executeStage.clock();
		executeStage.evaluate();

		memoryStage.clock();
		memoryStage.evaluate();

		writeBackStage.clock();
		writeBackStage.evaluate();

		writeBackStage.getState().instructionRetired = false;
	}

	private void initialize()
	{
		simulatorBus = new EventBus();
		addressBus = new MIPSAddressBus();

		assembledImage = null;

		statusManager = new SimulatorStatusManager();
		regFile = new MIPSRegFile();

		instructionDecodeStage = new InstructionDecodeStage(addressBus, statusManager, simulatorBus, regFile);
		executeStage = new ExecuteStage(statusManager, simulatorBus);
		memoryStage = new MemoryStage(addressBus, statusManager, simulatorBus);
		writeBackStage = new WriteBackStage(statusManager, simulatorBus, regFile);

		stages = Arrays.asList(instructionDecodeStage, executeStage, memoryStage,
				writeBackStage);

		// FIXME new MemModule(0,32,false);

		programCounter = new ProgramCounter(0);

		alu = new ALU();

		/*SetupDevicesandMemory setup = new SetupDevicesandMemory(this);
		setup.setup();
		addressBus.enable_allmodules();*/
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

	/**
	 * Set interrupt bit(mask)
	 * @param IRQ particular interrupt raised by the device
	 */
	public void setIRQ(long IRQ)
	{
		this.externalInterrupt |= IRQ;
	}

	/**
	 * Mask interrupt bit
	 * @param IRQ particular interrupt to be masked by the device
	 */
	public void maskIRQ(long IRQ)
	{
		this.externalInterrupt &= IRQ;
	}

	public long getIRQ()
	{
		return this.externalInterrupt;
	}



}
