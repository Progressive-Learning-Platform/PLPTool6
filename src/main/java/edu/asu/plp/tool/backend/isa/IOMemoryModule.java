package edu.asu.plp.tool.backend.isa;

import javafx.beans.property.LongProperty;

public interface IOMemoryModule {
	int write(long addr, long data, boolean isInstr);
	int writeRegister(long addr, long data, boolean isInst);
	Long read(long addr);
	Long readRegister(long addr);
	void clear();
	Object[][] getValueSet();
	long size();
	long endAddress();
	long startAddress();
	void enable();
	void disable();
	boolean isEnabled();
	boolean isInstruction(long addr);
	boolean isWordAligned();
	boolean checkAlignment(long addr);
	boolean isInitialized(long addr);
	void setNewParameters(long startAddr, long endAddr, boolean isWordAligned);
	boolean isAddressWithModule(long addr);
	String introduce();
	void reset();
	int eval();
	boolean isPhantom();
	boolean isThreaded();
	LongProperty getMemoryValueProperty(long address);
}
