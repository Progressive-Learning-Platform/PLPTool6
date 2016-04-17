package edu.asu.plp.tool.backend.plpisa.sim.stages;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.plpisa.InstructionExtractor;
import edu.asu.plp.tool.backend.plpisa.sim.SimulatorFlag;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.ExecuteCompletion;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.MemoryStageStateRequest;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.MemoryStageStateResponse;
import edu.asu.plp.tool.backend.plpisa.sim.stages.state.CpuState;

public class MemoryStage implements Stage
{
	private EventBus bus;
	private MemoryEventHandler eventHandler;
	
	private CpuState state;
	
	public MemoryStage(EventBus simulatorBus)
	{
		this.bus = simulatorBus;
		this.eventHandler = new MemoryEventHandler();
		
		this.bus.register(eventHandler);
		
		this.state = new CpuState();
		
		reset();
	}
	
	@Override
	public void evaluate()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clock()
	{
		state.bubble = state.nextBubble;
		state.currentInstruction = state.nextInstruction;
		state.currentInstructionAddress = state.nextInstructionAddress;
		
		state.forwardCt1Memtoreg = state.nextForwardCt1Memtoreg;
		state.forwardCt1Regwrite = state.nextForwardCt1Regwrite;
		state.forwardCt1DestRegAddress = state.nextForwardCt1DestRegAddress;
		state.dataAluResult = state.nextDataAluResult;
		state.forwardCt1Linkaddress = state.nextForwardCt1LinkAddress;
		state.forwardCt1Jal = state.nextForwardCt1Jal;
		
		state.ct1Memwrite = state.nextCt1Memwrite;
		state.ct1Memread = state.nextCt1Memread;
		state.dataMemwritedata = state.nextDataMemwritedata;
	}
	
	@Override
	public void printVariables()
	{
		 String writeDataForwarded = ""; //(simFlags & SimulatorFlag.PLP_SIM_FWD_MEM_MEM) == 0 ? "" : " (forwarded)";
		int spaceSize = -35;
		
		System.out.println("MEM vars");
		System.out.println(String.format("%" + spaceSize + "s %08x %s",
				"\tInstruction", state.nextInstruction,
				InstructionExtractor.format(state.currentInstruction)));
				
		String formattedInstructionAddress = ((state.currentInstructionAddress == -1)
				? "--------" : String.format("%08x", state.currentInstructionAddress));
		System.out.println(String.format("%" + spaceSize + "s %s",
				"\tInstructionAddress", formattedInstructionAddress));
				
		System.out.println(String.format("%" + spaceSize + "s %x",
				"\tForwardCt1MemToReg", state.forwardCt1Memtoreg));
		System.out.println(String.format("%" + spaceSize + "s %x",
				"\tForwardCt1Regwrite", state.forwardCt1Regwrite));
		System.out.println(String.format("%" + spaceSize + "s %x",
				"\tForwardCt1DestRegAddress", state.forwardCt1DestRegAddress));
		System.out.println(String.format("%" + spaceSize + "s %08x",
				"\tForwardCt1LinkAddress", state.forwardCt1Linkaddress));
		System.out.println(String.format("%" + spaceSize + "s %x", "\tForwardCt1Jal",
				state.forwardCt1Jal));
		System.out.println(String.format("%" + spaceSize + "s %08x",
				"\tnForwardDataAluResult", state.forwardDataAluResult));
				
		System.out.println(String.format("%" + spaceSize + "s %x", "\tCt1Memwrite",
				state.ct1Memwrite));
		System.out.println(String.format("%" + spaceSize + "s %x", "\tCt1Memread",
				state.ct1Memread));
		System.out.println(String.format("%" + spaceSize + "s %x", "\tct1ForwardMemMem",
				state.ct1ForwardMemMem));
				
		System.out.println(String.format("%" + spaceSize + "s %08x",
				"\tDataMemwritedata", state.dataMemwritedata));
		System.out.println(String.format("%" + spaceSize + "s %08x",
				"\tdataMemLoad*", state.dataMemLoad));
		System.out.println(String.format("%" + spaceSize + "s %08x %s",
				"\tdataMemStore*", state.dataMemStore, writeDataForwarded));
		System.out.println();
	}
	
	@Override
	public void printNextVariables()
	{
		int spaceSize = -35;
		
		System.out.println("MEM next vars");
		System.out.println(String.format("%" + spaceSize + "s %08x %s",
				"\tNextInstruction", state.nextInstruction,
				InstructionExtractor.format(state.nextInstruction)));
				
		String formattedInstructionAddress = ((state.currentInstructionAddress == -1)
				? "--------" : String.format("%08x", state.nextInstructionAddress));
		System.out.println(String.format("%" + spaceSize + "s %s",
				"\tNextInstructionAddress", formattedInstructionAddress));
				
		System.out.println(String.format("%" + spaceSize + "s %x",
				"\tNextForwardCt1MemToReg", state.nextForwardCt1Memtoreg));
		System.out.println(String.format("%" + spaceSize + "s %x",
				"\tNextForwardCt1Regwrite", state.nextForwardCt1Regwrite));
		System.out.println(String.format("%" + spaceSize + "s %x",
				"\tNextForwardCt1DestRegAddress", state.nextForwardCt1DestRegAddress));
		System.out.println(String.format("%" + spaceSize + "s %08x",
				"\tnextForwardCt1LinkAddress", state.nextForwardCt1LinkAddress));
		System.out.println(String.format("%" + spaceSize + "s %x", "\tnextForwardCt1Jal",
				state.nextForwardCt1Jal));
		System.out.println(String.format("%" + spaceSize + "s %08x",
				"\tnextForwardDataAluResult", state.nextForwardDataAluResult));
				
		System.out.println(String.format("%" + spaceSize + "s %x", "\tnextCt1Memwrite",
				state.nextCt1Memwrite));
		System.out.println(String.format("%" + spaceSize + "s %x", "\tnextCt1Memread",
				state.nextCt1Memread));
				
		System.out.println(String.format("%" + spaceSize + "s %08x",
				"\tnextDataMemwritedata", state.nextDataMemwritedata));
		System.out.println();
	}
	
	@Override
	public String printInstruction()
	{
		String formattedInstructionAddress = (state.currentInstructionAddress == -1
				|| state.bubble) ? "--------"
						: String.format("08x", state.currentInstructionAddress);
						
		// TODO add MIPSInstr format like ability
		String instruction = String.format("%s %s %s %08x %s", "Execute:",
				formattedInstructionAddress, "Instruction:", state.currentInstruction,
				" : " + InstructionExtractor.format(state.currentInstruction));
				
		return instruction;
	}
	
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}
	
	public class MemoryEventHandler
	{
		private MemoryEventHandler()
		{
		
		}
		
		public void executeCompletionEvent(ExecuteCompletion event)
		{
			CpuState postState = event.getPostMemoryState();
		}
		
		public void stateRequested(MemoryStageStateRequest event)
		{
			bus.post(new MemoryStageStateResponse(state.clone()));
		}
	}
	
}
