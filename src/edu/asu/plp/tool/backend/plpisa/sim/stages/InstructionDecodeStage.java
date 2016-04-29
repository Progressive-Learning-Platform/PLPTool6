package edu.asu.plp.tool.backend.plpisa.sim.stages;

import com.google.common.eventbus.EventBus;

import edu.asu.plp.tool.backend.plpisa.InstructionExtractor;
import edu.asu.plp.tool.backend.plpisa.sim.stages.events.InstructionDecodeCompletion;

public class InstructionDecodeStage implements Stage
{
	private EventBus bus;
	private InstructionDecodeEventHandler eventHandler;
	
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
	
	public InstructionDecodeStage(EventBus simulatorBus)
	{
		this.bus = simulatorBus;
		this.eventHandler = new InstructionDecodeEventHandler();
		reset();
	}
	
	@Override
	public void evaluate()
	{
		InstructionDecodeCompletion executePackage = new InstructionDecodeCompletion();
		
		//bus.post(new ExecuteStageStateRequest());
		//bus.post(new MemoryStageStateRequest());
		
		byte opCode = (byte) InstructionExtractor.opcode(currentInstruction);
		byte funct = (byte) InstructionExtractor.funct(currentInstruction);
		
		long addressRt = InstructionExtractor.rt(currentInstruction);
		long addressRs = InstructionExtractor.rs(currentInstruction);
		
		//Stuff to pass to execute stage 
		//bubble, currentInstruction, currInstructionAddress
		
		//Stuff to get from execute stage
		long executeStageInstruction = -1;
		
		if(hot)
		{
			hot = false;
			//set execute hot to true
		}
		
		if(!bubble)
			idCount++;
		
		
		
		//Load-use hazard detection logic
		
		//The register being written to by load word
		long executeRt = InstructionExtractor.rt(executeStageInstruction);
		
		//Get if mem stage is hot. Get mem if mem-ex-lw fowarding flag is true
		
		bus.post(executePackage);
	}
	
	@Override
	public void clock()
	{
		bubble = nextBubble;
		currentCt1Pcplus4 = nextCt1Pcplus4;
		currentInstruction = nextInstruction;
		currentInstructionAddress = nextInstructionAddress;
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
		
		currentInstruction = -1;
		currentInstructionAddress = -1;
		
		
		nextInstruction = -1;
		nextInstructionAddress = -1;
		
		currentCt1Pcplus4 = -1;
		nextCt1Pcplus4 = -1;
	}
	
	public class InstructionDecodeEventHandler
	{
		private InstructionDecodeEventHandler()
		{
	
		}
	}
	
}
