package edu.asu.plp.tool.backend.plpisa.sim.stages.state;

public class ExecuteStageState implements Cloneable
{
	public int count;
	
	public long currentInstruction;
	public long currentAddress;
	
	public long forwardCt1Memtoreg;
	public long forwardCt1Regwrite;
	
	public long forwardCt1Memwrite;
	public long forwardCt1Memread;
	public long forwardCt1Jal;
	public long forwardCt1Linkaddress;
	
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
	
	public long dataRs;
	public long dataRt;
	
	public long dataX;
	public long dataEffY;
	public long dataY;
	
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
	
	public long nextCt1RdAddress;
	public long nextCt1RtAddress;
	public long nextCt1AluOp;
	public long nextCt1AluSrc;
	public long nextCt1BranchTarget;
	public long nextCt1Branch;
	public long nextCt1Jump;
	public long nextCt1Regdest;

	public long nextDataRs;
	public long nextDataRt;
	
	public long nextDataImmediateSignExtended;
	
	public long internalAluOut;
	
	public boolean hot;
	public boolean bubble;
	public boolean nextBubble;
	
	public ExecuteStageState()
	{
		count = 0;
		
		hot = false;
		bubble = false;
		nextBubble = false;
	}
	
	
	public ExecuteStageState clone()
	{
		ExecuteStageState copy = new ExecuteStageState();

		copy.count = count;
		copy.currentInstruction = currentInstruction;
		copy.nextInstruction = nextInstruction;
		copy.nextInstructionAddress = nextInstructionAddress;
		copy.nextCt1RdAddress = nextCt1RdAddress;
		copy.nextCt1RtAddress = nextCt1RtAddress;
		copy.nextCt1AluOp = nextCt1AluOp;
		copy.nextCt1AluSrc = nextCt1AluSrc;
		copy.nextCt1BranchTarget = nextCt1BranchTarget;
		copy.nextCt1Branch = nextCt1Branch;
		copy.nextCt1Jump = nextCt1Jump;
		copy.nextCt1Regdest = nextCt1Regdest;
		copy.nextForwardCt1Jal = nextForwardCt1Jal;
		copy.nextForwardCt1LinkAddress = nextForwardCt1LinkAddress;
		copy.nextForwardCt1Regwrite = nextForwardCt1Regwrite;
		copy.nextForwardCt1Memtoreg = nextForwardCt1Memtoreg;
		copy.nextForwardCt1Memread = nextForwardCt1Memread;
		copy.nextForwardCt1Memwrite = nextForwardCt1Memwrite;
		copy.nextDataImmediateSignExtended = nextDataImmediateSignExtended;
		copy.forwardCt1Memread = forwardCt1Memread;
		copy.hot = hot;
		copy.bubble = bubble;
		copy.nextBubble = nextBubble;
		
		return copy;
	}
}
