package edu.asu.plp.tool.backend.plpisa.sim.stages.state;

public class ExecuteStageState
{
	public long currentInstruction;
	
	public long nextInstruction;
	public long nextInstructionAddress;
	
	public long nextCt1RdAddress;
	public long nextCt1RtAddress;
	public long nextCt1AluOp;
	public long nextCt1AluSrc;
	public long nextCt1BranchTarget;
	public long nextCt1Branch;
	public long nextCt1Jump;
	public long nextCt1Regdest;

	public long nextForwardCt1Jal;
	public long nextForwardCt1LinkAddress;
	public long nextForwardCt1Regwrite;
	public long nextForwardCt1Memtoreg;
	public long nextForwardCt1Memread;
	public long nextForwardCt1Memwrite;
	
	public long nextDataImmediateSignExtended;
	
	public long forwardCt1Memread;
	
	public boolean nextBubble;
}
