package edu.asu.plp.tool.backend.isa;

public interface AddressBus {
	String add(String moduleName, IOMemoryModule mod);
	int remove(String moduleName);
	Long read(long addr);
	int write(long addr, long data, boolean isInstr);
	boolean isMapped(long addr);
	boolean isInitialized(long addr);
	boolean isInstruction(long addr);
	int eval();
	int eval(String moduleName);
	int enable_allmodules();
	int disable_allmodules();
	int enableModule(String moduleName);
	int disableModule(String moduleName);
	boolean isEnabled(String moduleName);
	void reset();
	boolean validateAddress(long address);
	long getModuleStartAddress(String moduleName);
	long getModuleEndAddress(String moduleName);
	IOMemoryModule getModule(String moduleName);
}
