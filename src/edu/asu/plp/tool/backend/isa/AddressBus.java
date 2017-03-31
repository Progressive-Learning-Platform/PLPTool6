package edu.asu.plp.tool.backend.isa;

import javafx.beans.property.LongProperty;

public interface AddressBus {
	int add(IOMemoryModule mod);
	int remove(IOMemoryModule mod);
	Long read(long addr);
	int write(long addr, long data, boolean isInstr);
	boolean isMapped(long addr);
	boolean isInitialized(long addr);
	boolean isInstruction(long addr);
	int eval();
	int eval(int index);
	int enable_allmodules();
	int disable_allmodules();
	int enableModule(int index);
	int disableModule(int index);
	boolean isEnabled(int index);
	void reset();
	boolean validateAddress(long address);
	long getModuleStartAddress(int index);
	long getModuleEndAddress(int index);
	IOMemoryModule getModule(int index);
	LongProperty getMemoryValueProperty(long addr);
}
