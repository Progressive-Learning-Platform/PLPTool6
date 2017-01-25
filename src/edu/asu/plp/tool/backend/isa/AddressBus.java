package edu.asu.plp.tool.backend.isa;

import edu.asu.plp.tool.prototype.EmulationWindow;

public interface AddressBus {
	int add(IOMemoryModule mod);
	int remove(IOMemoryModule mod);
	Object read(long addr);
	int write(long addr, Object data, boolean isInstr);
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
	void setEmulationWindow(EmulationWindow window);

}
