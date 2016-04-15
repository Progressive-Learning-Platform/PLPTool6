package edu.asu.plp.tool.backend.plpisa.sim.stages.state;

public class CpuState implements Cloneable
{
	public int count;
	public int ifCount;
	public int idCount;
	
	public long currentInstruction;
	public long currentInstructionAddress;
	
	public long forwardCt1Memtoreg;
	public long forwardCt1Regwrite;
	
	public long forwardCt1Memwrite;
	public long forwardCt1Memread;
	public long forwardCt1Jal;
	public long forwardCt1Linkaddress;
	
	public long forwardCt1DestRegAddress;

	public long forwardDataAluResult;
	
	public long ct1Pcplus4;
	
	public long ct1Alusrc;
	public long ct1Aluop;
	public long ct1Regdest;
	
	public long ct1Jump;
	public long ct1Pcsrc;
	public long ct1Branch;
	public long ct1Branchtarget;
	public long ct1JumpTarget;
	
	public long ct1Forwardx;
	public long ct1Forwardy;
	
	public long ct1Memwrite;
	public long ct1Memread;
	public long ct1ForwardMemMem;
	
	public long ct1Memtoreg;
	public long ct1Regwrite;
	public long ct1DestRegAddress;
	public long ct1Linkaddress;
	public long ct1Jal;
	
	public long dataRs;
	public long dataRt;
	
	public long dataX;
	public long dataEffY;
	public long dataY;
	
	public long dataMemwritedata;
	public Long dataMemLoad;
	public long dataMemStore;
	
	public long dataMemreaddata;
	public long dataAluResult;
	public long dataRegwrite;
	
	public long dataImmediateSignextended;
	public long ct1RtAddress;
	public long ct1RdAddress;
	
	public long nextInstruction;
	public long nextInstructionAddress;
	
	public long nextForwardCt1Jal;
	public long nextForwardCt1LinkAddress;
	public long nextForwardCt1Regwrite;
	public long nextForwardCt1Memtoreg;
	public long nextForwardCt1Memread;
	public long nextForwardCt1Memwrite;
	
	public long nextForwardCt1DestRegAddress;
	
	public long nextForwardDataAluResult;
	
	public long nextCt1Pcplus4;
	
	public long nextCt1RdAddress;
	public long nextCt1RtAddress;
	public long nextCt1AluOp;
	public long nextCt1AluSrc;
	public long nextCt1BranchTarget;
	public long nextCt1Branch;
	public long nextCt1Jump;
	public long nextCt1Regdest;
	
	public long nextCt1Memwrite;
	public long nextCt1Memread;
	
	public long nextCt1Memtoreg;
	public long nextCt1Regwrite;
	public long nextCt1DestRegAddress;
	public long nextCt1Linkaddress;
	public long nextCt1Jal;
	
	public long nextDataMemwritedata;

	public long nextDataRs;
	public long nextDataRt;
	
	public long nextDataMemreaddata;
	public long nextDataAluResult;
	
	public long nextDataImmediateSignExtended;
	
	public long internalAluOut;
	
	public boolean hot;
	public boolean bubble;
	public boolean nextBubble;
	
	public boolean instructionRetired;
	
	public CpuState()
	{
		count = 0;
		ifCount = 0;
		idCount = 0;
		
		hot = false;
		bubble = false;
		nextBubble = false;
		instructionRetired = false;
	}
	
	public CpuState clone()
	{
		CpuState copy = new CpuState();
		
		
		
		return copy;
	}
}
