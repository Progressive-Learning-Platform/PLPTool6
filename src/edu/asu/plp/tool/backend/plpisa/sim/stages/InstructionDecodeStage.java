package edu.asu.plp.tool.backend.plpisa.sim.stages;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.plpisa.InstructionExtractor;
import edu.asu.plp.tool.backend.plpisa.PLPInstruction;
import edu.asu.plp.tool.backend.plpisa.sim.SimulatorFlag;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.ExecuteStageStateRequest;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.ExecuteStageStateResponse;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.InstructionDecodeCompletion;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.MemoryStageStateRequest;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.MemoryStageStateResponse;
import edu.asu.plp.tool.backend.plpisa.sim.stages.state.ExecuteStageState;
import edu.asu.plp.tool.backend.plpisa.sim.stages.state.MemoryStageState;

public class InstructionDecodeStage implements Stage
{
	private EventBus bus;
	private InstructionDecodeEventHandler eventHandler;
	
	//TODO move to a state class?
	
	private int ifCount;
	private int idCount;
	
	private boolean hot;
	
	private boolean bubble;
	private boolean nextBubble;
	
	private long currentInstruction;
	private long currentInstructionAddress;
	
	// Pipeline Registers
	private long nextInstruction;
	private long nextInstructionAddress;
	
	private long currentCt1Pcplus4;
	private long nextCt1Pcplus4;
	
	// Get state from other stages thats required (Hard Porting)
	private ExecuteStageState currentExecuteStageState;
	private MemoryStageState currentMemoryStageState;
	
	public InstructionDecodeStage(EventBus simulatorBus)
	{
		this.bus = simulatorBus;
		this.eventHandler = new InstructionDecodeEventHandler();
		
		this.bus.register(eventHandler);
		
		reset();
	}
	
	@Override
	public void evaluate()
	{
		InstructionDecodeCompletion executePackage = new InstructionDecodeCompletion();
		ExecuteStageState postExecuteStageState = new ExecuteStageState();
		
		executePackage.setPostExecuteStageState(postExecuteStageState);
		
		bus.post(new ExecuteStageStateRequest());
		bus.post(new MemoryStageStateRequest());
		
		if (currentExecuteStageState == null)
			throw new IllegalStateException("Could not retrieve execute stage state.");
			
		if (currentMemoryStageState == null)
			throw new IllegalStateException("Could not retrieve memory stage state.");
			
		// TODO get from wherever the flag is
		boolean mem_ex_lw = false;
		
		byte opCode = (byte) InstructionExtractor.opcode(currentInstruction);
		byte funct = (byte) InstructionExtractor.funct(currentInstruction);
		
		long addressRt = InstructionExtractor.rt(currentInstruction);
		long addressRs = InstructionExtractor.rs(currentInstruction);
		
		long executeStageCurrentInstruction = currentExecuteStageState.currentInstruction;

		// The register being written to by load word
		long executeRt = InstructionExtractor.rt(executeStageCurrentInstruction);
		
		if (hot)
		{
			hot = false;
			postExecuteStageState.hot = true;
		}
		
		if (!bubble)
			idCount++;
			
		postExecuteStageState.nextBubble = bubble;
		postExecuteStageState.nextInstruction = currentInstruction;
		postExecuteStageState.nextInstructionAddress = currentInstructionAddress;
		
		if (currentMemoryStageState.isHot() && mem_ex_lw)
		{
			boolean executeEqualsAddressRt = executeRt == addressRt;
			boolean executeForwardCt1Memread = currentExecuteStageState.forwardCt1Memread == 1;
			boolean isCurrentInstructionNotStoreWord = InstructionExtractor.opcode(
					currentInstruction) != PLPInstruction.STORE_WORD.getByteCode();
					
			if (executeEqualsAddressRt && (addressRt != 0) && executeForwardCt1Memread)
			{
				if (isCurrentInstructionNotStoreWord)
				{
					// TODO set execute stall to true
					// TODO add sim flag SimulatorFlag.PLP_SIM_FWD_MEM_EX_LW_RT
				}
				
				// TODO set execute stall to true
				// TODO add sim flag SimulatorFlag.PLP_SIM_FWD_MEM_EX_LW_RS
			}
			
		}
		
		// long rt = (addressRt == 0) ? 0 : (Long) memoryModule.read(addressRt);
		// executePackage.setNextDataRt(rt);
		
		// long rs = (addressRs == 0) ? 0 : (Long) memoryModule.read(addressRs);
		// executePackage.setNextDataRs(rs);
		
		long immediateField = InstructionExtractor.imm(currentInstruction);
		
		boolean isNotAndImmediate = opCode != PLPInstruction.AND_IMMEDIATE.getByteCode();
		boolean isNotOrImmediate = opCode != PLPInstruction.OR_IMMEDIATE.getByteCode();
		
		if (isNotAndImmediate && isNotOrImmediate)
		{
			long value = (short) immediateField & ((long) 0xfffffff << 4 | 0xf);
			postExecuteStageState.nextDataImmediateSignExtended = value;
		}
		else
		{
			postExecuteStageState.nextDataImmediateSignExtended = immediateField;
		}
		
		postExecuteStageState.nextCt1RdAddress = InstructionExtractor
				.rd(currentInstruction); // rd
		postExecuteStageState.nextCt1RtAddress = addressRt;
		
		postExecuteStageState.nextCt1AluOp = currentInstruction;
		
		postExecuteStageState.nextForwardCt1LinkAddress = currentCt1Pcplus4 + 4;
		
		executePackage.clearLogic();
		
		if (opCode != PLPInstruction.SHIFT_LEFT_LOGICAL.getByteCode())
		{
			switch (InstructionExtractor.instructionType(currentInstruction))
			{
				case 3: // beq and bne
					postExecuteStageState.nextCt1Branch = 1;
					break;
				case 4: // i-types
				case 5: // lui
					postExecuteStageState.nextCt1AluSrc = 1;
					postExecuteStageState.nextForwardCt1Regwrite = 1;
					break;
				case 6: // lw and sw
					if (opCode == PLPInstruction.LOAD_WORD.getByteCode())
					{
						postExecuteStageState.nextForwardCt1Memtoreg = 1;
						postExecuteStageState.nextForwardCt1Regwrite = 1;
						postExecuteStageState.nextForwardCt1Memread = 1;
					}
					else if (opCode == PLPInstruction.STORE_WORD.getByteCode())
					{
						postExecuteStageState.nextForwardCt1Memwrite = 1;
					}
					postExecuteStageState.nextCt1AluSrc = 1;
					break;
				case 7: // j and jal
					postExecuteStageState.nextCt1Jump = 1;
					if (InstructionExtractor.mnemonic(currentInstruction)
							.equals(PLPInstruction.JUMP_AND_LINK.getMnemonic()))
					{
						postExecuteStageState.nextCt1Regdest = 1;
						postExecuteStageState.nextCt1RdAddress = 31;
						postExecuteStageState.nextForwardCt1Regwrite = 1;
						postExecuteStageState.nextForwardCt1Jal = 1;
					}
					break;
				default:
					throw new IllegalStateException("Unhandled instruction type.");
			}
		}
		else
		{
			switch (InstructionExtractor.instructionType(currentInstruction))
			{
				case 0: // r-types
				case 1: // shifts
				case 8: // multiply
					postExecuteStageState.nextCt1Regdest = 1;
					postExecuteStageState.nextForwardCt1Regwrite = 1;
					break;
				case 2: // jr
					postExecuteStageState.nextCt1Jump = 1;
					break;
				case 9: // jalr
					postExecuteStageState.nextCt1Jump = 1;
					postExecuteStageState.nextCt1Regdest = 1;
					postExecuteStageState.nextForwardCt1Regwrite = 1;
					postExecuteStageState.nextForwardCt1Jal = 1;
					break;
				default:
					throw new IllegalStateException("Unhandled instruction type.");
			}
		}
		
		long nextCt1BranchTarget = currentCt1Pcplus4
				+ ((short) postExecuteStageState.nextDataImmediateSignExtended << 2);
		postExecuteStageState.nextCt1BranchTarget = nextCt1BranchTarget;
		
		bus.post(executePackage);
	}
	
	@Override
	public void clock()
	{
		bubble = nextBubble;
		currentCt1Pcplus4 = nextCt1Pcplus4;
		currentInstruction = nextInstruction;
		currentInstructionAddress = nextInstructionAddress;
		
		currentExecuteStageState = null;
		currentMemoryStageState = null;
	}
	
	@Override
	public void printVariables()
	{
		prettyPrintVariables("Instruction Decode Variables", currentInstruction,
				currentInstructionAddress, currentCt1Pcplus4);
	}
	
	@Override
	public void printNextVariables()
	{
		prettyPrintVariables("Instruction Decode Next Variables", nextInstruction,
				nextInstructionAddress, nextCt1Pcplus4);
	}
	
	@Override
	public String printInstruction()
	{
		String formattedInstructionAddress = (currentInstructionAddress == -1 || bubble)
				? "--------" : String.format("08x", currentInstructionAddress);
				
		// TODO add MIPSInstr format like ability
		String instruction = String.format("%s %s %s %08x", "Instruction Decode:",
				formattedInstructionAddress, "Instruction:", currentInstruction);
				
		return instruction;
	}
	
	private void prettyPrintVariables(String title, long instruction,
			long instructionAddress, long ct1Pcplus4)
	{
		System.out.println(title);
		
		// TODO add MIPSInstr format like ability
		String instructionFormatted = String.format("%-25s %08x %s", "\tinstruction",
				instruction);
				
		String instructionAddressTemp = (currentInstructionAddress == -1) ? "--------"
				: String.format("%08x", instructionAddress);
		String instructionAddressFormatted = String.format("%-25s %s",
				"\tinstruction address", instructionAddressTemp);
				
		String tct1Pcplus4Formatted = String.format("%-25s %08x", "\ttct1 pcplus4",
				ct1Pcplus4);
				
		System.out.println(instructionFormatted);
		System.out.println(instructionAddressFormatted);
		System.out.println(tct1Pcplus4Formatted);
		System.out.println();
	}
	
	@Override
	public void reset()
	{
		ifCount = 0;
		idCount = 0;
		
		hot = false;
		
		bubble = false;
		nextBubble = false;
		
		currentExecuteStageState = null;
		currentMemoryStageState = null;
	}
	
	public class InstructionDecodeEventHandler
	{
		private InstructionDecodeEventHandler()
		{
		
		}
		
		public void executeStageStateResponse(ExecuteStageStateResponse event)
		{
			currentExecuteStageState = event.getExecuteStageState();
		}
		
		public void memoryStageStateResponse(MemoryStageStateResponse event)
		{
			currentMemoryStageState = event.getMemoryStageState();
		}
	}
	
}
