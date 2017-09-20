package edu.asu.plp.tool.prototype.devices;

import java.util.TreeMap;

import edu.asu.plp.tool.backend.isa.IOMemoryModule;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import plptool.Text;

public abstract class PLPToolIOMemoryModule extends Thread implements IOMemoryModule{
	
	//If this module needs to be run separately as thread.
		public boolean threaded = false;
		public boolean stop;
		
		// Registers or Memory specific to this module
		protected TreeMap<Long, LongProperty> values;
		
		//Denotes each entry in the register or memory whether it is an instruction.
		// Used in main memory implementation and caches
		protected TreeMap<Long, Boolean> isInstruction;
		
		//Whether registers are addressed in memory-aligned fashion
		protected boolean wordAligned;
		
		//Starting address of the current modules address space
		protected long startAddress;
		
		//Final address of the current modules address space
		protected long endAddress;
		
		// Write-enabled and whether evaluations are enabled, but subclasses can choose to ignore this
		protected boolean enabled;
		
		//This variable tells the bus whether it actually stores data in the mapped region. Useful for 
		//modules that need to see bus access but do not require registers (cache simulators, snoopers, etc)
		public boolean phantom;
		
		/**
		 * This gives the current version of PLPTool.
		 * User Modules can override this. May be useful for dynamic modules
		 * @return
		 */
		public String getVersion()
		{
			return Text.versionString;
		}
		
		/**
		 * The constructor for the superclass requires the address space and 
		 * whether the registers of the module are word-aligned
		 * 
		 * @param startAddr Starting address of the module's address space
		 * @param endAddr Final address of the module's address space
		 * @param wordAligned Whether the module's address space is word aligned
		 */
	PLPToolIOMemoryModule(long startAddr, long endAddr, boolean wordAligned)
	{
		values = new TreeMap<Long, LongProperty>();
		isInstruction = new TreeMap<Long, Boolean>();
		this.startAddress = startAddr;
		this.endAddress = endAddr;
		this.wordAligned = wordAligned;
		enabled = false;
		phantom = false;
	}
	
	PLPToolIOMemoryModule()
	{
		
	}

	@Override
	public synchronized int write(long addr, long data, boolean isInstr) {
		return writeRegister(addr, data, isInstr);
	}
	
	@Override
	public synchronized int writeRegister(long addr, long data, boolean isInstr)
	{
		if(addr > endAddress || addr < startAddress)
		{
			//Error
			return -1;//Constants.PLP_SIM_OUT_ADDRESS_OUT_OF_RANGE;
		}
		else if(wordAligned && addr % 4 != 0)
		{
			//Error
			return -2;//Constants.PLP_SIM_OUT_UNALIGNED_MEMORY;
		}
		else if(!enabled)
		{
			//Error
			return -3;//Constants.PLP_SIM_MODULE_DISABLED;
		}
		else
		{
			if(values.containsKey(addr))
			{
				//values.remove(addr);
				//LongProperty pt = (LongProperty)values.get(addr);
				values.get(addr).set(data);
				//pt.set((long)data);
				isInstruction.remove(addr);
				isInstruction.put(new Long(addr), isInstr);
			}
			else
			{
				SimpleLongProperty ldata = new SimpleLongProperty(data);
				
				values.put(new Long(addr), ldata);
				isInstruction.put(new Long(addr), isInstr);
			}
			
			
		}
		return 0;
		
	}

	@Override
	public synchronized Long read(long addr) {
		return readRegister(addr);
		
	}
	
	@Override
	public synchronized Long readRegister(long addr)
	{
		if(addr < startAddress || addr > endAddress)
		{
			//Error
			return null;//Constants.PLP_SIM_OUT_ADDRESS_OUT_OF_RANGE;
		}
		else if(wordAligned && addr % 4 != 0)
		{
			return null;//Constants.PLP_SIM_OUT_UNALIGNED_MEMORY;
		}
		else if(!values.containsKey(addr))
		{
			return null;//Constants.PLP_SIM_UNINITIALIZED_MEMORY;
		}
		else
			return values.get(addr).get();
		
	}
	
	@Override
	public LongProperty getMemoryValueProperty(long addr)
	{
		if(addr < startAddress || addr > endAddress)
		{
			//Error
			return null;//Constants.PLP_SIM_OUT_ADDRESS_OUT_OF_RANGE;
		}
		else if(wordAligned && addr % 4 != 0)
		{
			return null;//Constants.PLP_SIM_OUT_UNALIGNED_MEMORY;
		}
		else if(!values.containsKey(addr))
		{
			return null;//Constants.PLP_SIM_UNINITIALIZED_MEMORY;
		}
		else
			return values.get(addr);
		
	}

	@Override
	public synchronized void clear() {
		values = new TreeMap<Long, LongProperty>();
		isInstruction = new TreeMap<Long, Boolean>();
		
	}

	@Override
	public synchronized Object[][] getValueSet() {
		Object[][] valueSet = new Object[values.size()][3];
		
		//TODO:Implementation changes as every data is percieved as long now
		/*int index = 0;
		
		Iterator keyIterator = values.keySet().iterator();
		
		while(keyIterator.hasNext())
		{
			valueSet[index][0] = keyIterator.next();
			valueSet[index][1] = values.get((Long)valueSet[index][0]);
			valueSet[index][2] = isInstruction.get((Long)valueSet[index][0]);
			index++;
		}*/
		
		return valueSet;
	}

	@Override
	public synchronized long size() {
		if(!wordAligned)
		{
			return endAddress - startAddress + 1;
		}
		else
		{
			return (endAddress - startAddress)/4 + 1;
		}
		
	}

	@Override
	public long endAddress() {
		return endAddress;
	}

	@Override
	public long startAddress() {
		return startAddress;
	}

	@Override
	public synchronized void enable() {
		enabled = true;
		
	}

	@Override
	public synchronized void disable() {
		enabled = true;
		
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isInstruction(long addr) {
		if(isInstruction.get(addr) != null)
			return isInstruction.get(addr);
		else
			return false;
	}

	@Override
	public boolean isWordAligned() {
		
		return wordAligned;
	}

	@Override
	public boolean checkAlignment(long addr) {
		if(wordAligned)
			return (addr % 4 == 0);
		else
			return true;
	}

	@Override
	public boolean isInitialized(long addr) {
		return values.containsKey(addr);
	}

	@Override
	public synchronized void setNewParameters(long startAddr, long endAddr, boolean isWordAligned) {
		values = new TreeMap<Long, LongProperty>();
		isInstruction = new TreeMap<Long, Boolean>();
		startAddress = startAddr;
		endAddress = endAddr;
		wordAligned = isWordAligned;
		
	}

	@Override
	public boolean isAddressWithModule(long addr) {
		if(addr >= startAddress && addr <= endAddress)
			return true;
		return false;
	}
	
	@Override
	public boolean isPhantom()
	{
		return phantom;
	}
	
	@Override
	public boolean isThreaded()
	{
		return threaded;
	}

	@Override
	abstract public String introduce();

	@Override
	abstract public void reset();

	@Override
	abstract public int eval();

}
