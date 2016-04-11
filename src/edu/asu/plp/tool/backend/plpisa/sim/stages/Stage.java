package edu.asu.plp.tool.backend.plpisa.sim;

public interface Stage
{
	void evaluate();
	
	void clock();
	
	void printVariables();
	
	void printNextVariables();
	
	String printInstruction();
}
