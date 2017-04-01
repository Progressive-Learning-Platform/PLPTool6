package edu.asu.plp.tool.backend.isa;

public interface RegisterFile {
	void reset();
	boolean hasRegister(String registerName);
	long read(int address);
	public void write(int address, long value);
	boolean isInstruction(int address);
	void write(int address, long value, boolean isInstruction);
	void validateAddress(int address);
	String getRegisterID(String registerName);

}
