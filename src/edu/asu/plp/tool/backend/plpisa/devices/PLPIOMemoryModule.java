package edu.asu.plp.tool.backend.plpisa.devices;

import java.util.Iterator;
import java.util.TreeMap;

import plptool.Constants;
import plptool.Text;

/*
 * This class is the general purpose input output memory module. All the devices will implement
 * this class and build functionality upon this. Example MainMemory, Secondary Memory, LED, Switches
 * UART, etc
 * 
 * TODO: As of now it is same as PLPTool 5.2. Update or modify this as you gain confidence and more understanding about simulation
 */
public abstract class PLPIOMemoryModule extends Thread {
	
	//If this module needs to be run separately as thread.
	public boolean threaded = false;
	public boolean stop;
	
	// Registers or Memory specific to this module
	protected TreeMap<Long, Object> values;
	
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
	public PLPIOMemoryModule(long startAddr, long endAddr, boolean wordAligned)
	{
		values = new TreeMap<Long, Object>();
		isInstruction = new TreeMap<Long, Boolean>();
		this.startAddress = startAddr;
		this.endAddress = endAddr;
		this.wordAligned = wordAligned;
		enabled = false;
		phantom = false;
	}
	
	/**
	 * An empty constructor for dynamic module loading
	 */
	public PLPIOMemoryModule(){}
	
	/**
	 * This method is used to write data to one of the module's registers.
	 * It should be enabled.
	 * 
	 * @param addr The address where data needs to be written
	 * @param data Data to be written to memory address
	 * @param isInstr Data is an instruction or not
	 * @return PLP_OK or error code
	 */
	public synchronized final int writeReg(long addr, Object data, boolean isInstr)
	{
		if(addr > endAddress || addr < startAddress)
		{
			//Error
			return Constants.PLP_SIM_OUT_ADDRESS_OUT_OF_RANGE;
		}
		else if(wordAligned && addr % 4 != 0)
		{
			//Error
			return Constants.PLP_SIM_OUT_UNALIGNED_MEMORY;
		}
		else if(!enabled)
		{
			//Error
			return Constants.PLP_SIM_MODULE_DISABLED;
		}
		else
		{
			if(values.containsKey(addr))
			{
				values.remove(addr);
				isInstruction.remove(addr);
			}
			
			values.put(new Long(addr), data);
			isInstruction.put(new Long(addr), isInstr);
		}
		
		return Constants.PLP_OK;
	}
	
	/**
	 * This function will read the values from the registers of that module.
	 * @param addr Address from where data needs to be read
	 * @return data or error
	 */
	public synchronized final Object readReg(long addr)
	{
		if(addr < startAddress || addr > endAddress)
		{
			//Error
			return Constants.PLP_SIM_OUT_ADDRESS_OUT_OF_RANGE;
		}
		else if(wordAligned && addr % 4 != 0)
		{
			return Constants.PLP_SIM_OUT_UNALIGNED_MEMORY;
		}
		else if(!values.containsKey(addr))
		{
			return Constants.PLP_SIM_UNINITIALIZED_MEMORY;
		}
		else
			return values.get(addr);
	}
	
	/**
	 * Wrapper function for writeReg. Developers may wish to override this function to implement module-specific
	 * write actions without sacrificing PLPIOMemoryModule original writeReg functionality. This is the actual 
	 * function that will be called by PLPAddressBus.
	 * 
	 * @param addr Address where data needs to be written
	 * @param data data that needs to be written at the addr
	 * @param isInstr whether given data is an instruction
	 * @return PLP_OK, or error code
	 */
	public synchronized int write(long addr, Object data, boolean isInstr)
	{
		return writeReg(addr, data, isInstr);
	}
	
	/**
	 * Wrapper function for readReg. Developers may wish to override this function to implement module-specific read 
	 * actions without sacrificing PLPIOMemoryModule original readReg functionality. This is actual function that will
	 * be called by PLPAddressBus
	 * @param addr Address whose data needs to be read
	 * @return Data, or PLP_Error_Return
	 */
	public synchronized Object read(long addr)
	{
		return readReg(addr);
	}
	
	/**
	 * This function resets the module by clearing all the modules registers
	 */
	public synchronized void clear()
	{
		values = new TreeMap<Long, Object>();
		isInstruction = new TreeMap<Long, Boolean>();
	}
	
	/**
	 * This method can be useful to modules that have to walk through the whole register
	 * file on evaluation.
	 * 
	 * @return nx3 Object array containing address, values of the memory module and boolean
	 * denoting whether the value is instruction or not.
	 */
	public synchronized Object[][] getValueSet()
	{
		Object[][] valueSet = new Object[values.size()][3];
		
		int index = 0;
		
		Iterator keyIterator = values.keySet().iterator();
		
		while(keyIterator.hasNext())
		{
			valueSet[index][0] = keyIterator.next();
			valueSet[index][1] = values.get((Long)valueSet[index][0]);
			valueSet[index][2] = isInstruction.get((Long)valueSet[index][0]);
			index++;
		}
		
		return valueSet;
	}
	
	/**
	 * This gives the size of the address space of this module.
	 * @return width of the address space
	 */
	public synchronized final long size()
	{
		if(!wordAligned)
		{
			return endAddress - startAddress + 1;
		}
		else
		{
			return (endAddress - startAddress)/4 + 1;
		}
	}
	
	/**
	 * Final address of the modules address space
	 * @return Final address
	 */
	public synchronized final long endAddress()
	{
		return endAddress;
	}
	
	/**
	 * Starting address of the module
	 * @return Start address of the module
	 */
	public synchronized final long startAddress()
	{
		return startAddress;
	}
	
	/**
	 * This will enable the module
	 */
	public synchronized void enable()
	{
		enabled = true;
	}
	
	/**
	 * This will disable the module
	 */
	public synchronized void disable()
	{
		enabled = true;
	}
	
	/**
	 * This function will remove the module addressBus
	 */
	public synchronized void remove()
	{
		
	}
	
	/**
	 * This function will add the module to addressBus
	 */
	public synchronized void add()
	{
		
	}
	
	/**
	 * This function will tell us if the current module is enabled or not
	 * @return true for enabled else false
	 */
	public synchronized boolean isEnabled()
	{
		return enabled;
	}
	
	/**
	 * Whether specified register contains an instruction or not
	 * @param addr address to read from
	 * @return true if register contains instruction else false
	 */
	public synchronized final boolean isInstruction(long addr)
	{
		if(isInstruction.get(addr) != null)
			return isInstruction.get(addr);
		else
			return false;
	}
	
	/**
	 * If the modules addressing is word aligned
	 * @return true if aligned else false
	 */
	public synchronized final boolean isWordAligned() 
	{
		return wordAligned;
	}
	
	/**
	 * Check whether the address is properly aligned.
	 * 
	 * @param addr Address to check
	 * @return if module is word aligned then check alignment condition, else true
	 */
	public synchronized boolean checkAlignment(long addr)
	{
		if(wordAligned)
			return (addr % 4 == 0);
		else
			return true;
	}
	
	/**
	 * True if the specified address is initialized. Flase otherwise
	 * 
	 * @param addr The address to lookup
	 * @return Returns boolean
	 */
	public synchronized final boolean isInitialized(long addr)
	{
		return values.containsKey(addr);
	}
	
	/**
	 * Reset attributes for dynamically loaded modules
	 * 
	 * @param startAddr Starting address of the modules address space
	 * @param endAddr End address of the modules address space
	 * @param isWordAligned Whether the modules address space is word aligned
	 */
	public void setNewParameters(long startAddr, long endAddr, boolean isWordAligned)
	{
		values = new TreeMap<Long, Object>();
		isInstruction = new TreeMap<Long, Boolean>();
		startAddress = startAddr;
		endAddress = endAddr;
		wordAligned = isWordAligned;
	}
	
	/**
	 * It will check whether given address belongs to this module or not
	 * @param addr Address to be verified
	 * @return true if it is within address space else false
	 */
	public boolean isAddressWithModule(long addr)
	{
		if(addr > startAddress && addr < endAddress)
			return true;
		return false;
	}
	
	/**
	 * The eval() function represets the behavior of the module itself.
	 * For example, an eva function for an array of LED will read its register and light up
	 * proper LEDs. Simulation cores will call this function every cycles, so this may slow down
	 * the simulation considerably
	 * 
	 * @return Status Code.
	 */
	abstract public int eval();
	
	/**
	 * This is designed for simulator developers/users to allow the module to interact 
	 * with the simulation environment directly. This function is only called every time
	 * the GUI components are refereshed. Simulation cores do not/should not call this 
	 * function every cycle.
	 * 
	 * @param x Reference to a frame object that this module will interact with.
	 * @return Status Code.
	 */
	abstract public int gui_eval(Object x);
	
	/**
	 * This method is called by PLPAddressBus.reset(). It is up to the module on 
	 * how this should be implemented.
	 */
	abstract public void reset();
	
	/**
	 * Introduction string when the module is loaded.
	 * @return
	 */
	abstract public String introduce();
	

}
