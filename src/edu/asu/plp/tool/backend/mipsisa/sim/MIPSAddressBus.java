package edu.asu.plp.tool.backend.mipsisa.sim;

import java.util.ArrayList;

import edu.asu.plp.tool.backend.isa.AddressBus;
import edu.asu.plp.tool.backend.isa.IOMemoryModule;
import edu.asu.plp.tool.prototype.EmulationWindow;
import javafx.beans.property.LongProperty;
import plptool.Constants;
/**
 * This class is the main bus connecting the PLP processor to different I/O modules including Main Memory.
 * So all the IO devices will be attached to this.
 * Code is directly taken from the old PLPTool 5.2
 * As the confidence increase, go on changing the contents.
 * @author Harsha
 *
 */
public class MIPSAddressBus implements AddressBus{
	
	/**
	 * This list contains all I/O module attached with this bus
	 */
	private ArrayList<IOMemoryModule> modules;
	
	private EmulationWindow window;
	
	public MIPSAddressBus()
	{
		modules = new ArrayList<IOMemoryModule>();
	}
	
	/**
	 * This method is used to add a new PLPIOMemoryModule to the bus
	 * @param mod PLPIOMemoryModule to be added
	 * @return returns the index of the module after adding
	 */
	public int add(IOMemoryModule mod)
	{
		modules.add(mod);
		return modules.indexOf(mod);
	}
	
	@Override
	public int remove(IOMemoryModule rmmod) {
		modules.remove(rmmod);
		
		return 0;
	}

	
	/**
	 * This method removes the module form the bus. 
	 * @param index position where module has been added
	 * @return ok if no error else error code
	 */
	public int remove(int index)
	{
		//Index out of range possibility
		modules.remove(index);
		return Constants.PLP_OK;
	}
	
	@Override
	public boolean validateAddress(long address)
	{
		boolean isValid = false;
		
		for(IOMemoryModule mod : modules)
		{
			if(mod.isAddressWithModule(address))
			{
				isValid = true;
				break;
			}
		}
		
		return isValid;
	}
	
	/**
	 * This method will read the data from the module
	 * @param addr address whose data needs to be read
	 * @return read object else error code
	 */
	public synchronized Long read(long addr)
	{
		Long value = null;
		
		for(IOMemoryModule mod : modules)
		{
			if(mod.isAddressWithModule(addr))
			{
				if(!mod.isPhantom())
					value = mod.read(addr);
				else
				{
					//verify: why are we even calling read of the module if not interested in its value
					mod.read(addr);
					System.out.println("Within module, but phantom");
				}
				
				break;
			}
			
			
		}
		
		if(value == null && isMapped(addr))
		{
			System.out.println("No value, but mapped");
			 //Constants.PLP_SIM_MODULE_NO_DATA_ON_READ;
			return null;
		}
		else if(!isMapped(addr))
		{
			System.out.println("Not mapped");
			//return Constants.PLP_SIM_UNMAPPED_MEMORY_ACCESS;
			return null;
		}
		
		
		return value;
	}
	
	@Override
	public LongProperty getMemoryValueProperty(long address)
	{
		LongProperty valueProperty = null;
		
		for(IOMemoryModule mod : modules)
		{
			if(mod.isAddressWithModule(address))
			{
				if(!mod.isPhantom())
					valueProperty = mod.getMemoryValueProperty(address);
				else
				{
					//verify: why are we even calling read of the module if not interested in its value
					//mod.read(addr);
				}
				
				break;
			}
			
			
		}
		
		return valueProperty;
	}
	
	/**
	 * This method is used to write to a particular location of a module.
	 * @param addr address where data needs to be written
	 * @param data data to be written to the address
	 * @param isInstr whether data to be written is an instruction or not
	 * @return
	 */
	public synchronized int write(long addr, long data, boolean isInstr)
	{
		int ret = Constants.PLP_SIM_UNMAPPED_MEMORY_ACCESS;
		
		
		for(IOMemoryModule mod : modules)
		{
			if(mod.isAddressWithModule(addr))
			{
				ret = mod.write(addr, data, isInstr);
				break;
			}
		}
		
		return ret;
	}
	
	public IOMemoryModule getModule(int index)
	{
		return modules.get(index);
	}
	
	/**
	 * This method checks if the module to which the given address belongs is initialized or not
	 * @param addr address belonging to a particular module
	 * @return true if initialized else false
	 */
	public synchronized boolean isInitialized(long addr)
	{
		boolean bInitialized = false;
		
		for(IOMemoryModule mod : modules)
			if(mod.isAddressWithModule(addr))
			{
				if(mod.isInitialized(addr))
				{
					bInitialized = true;
				}
				break;
					
			}
		
		return bInitialized;
	}
	
	/**
	 * This method will check if the given address belongs to a module or not
	 * @param addr address whose mapping needs to be verified
	 * @return true if mapped else false
	 */
	public synchronized boolean isMapped(long addr)
	{
		boolean bMapped = false;
		
		for(IOMemoryModule mod : modules)
		{
			if(mod.isAddressWithModule(addr))
			{
				if(mod.checkAlignment(addr))
				{
					bMapped = true;
				}
				break;
			}
			
		}
		
		return bMapped;
	}
	
	/**
	 * This method will check if given data present in the given address is an instruction or not
	 * @param addr address whose value needs to be verified whether it is an instruction or not
	 * @return true if it is an instruction or else false
	 */
	public synchronized boolean isInstruction(long addr)
	{
		boolean bInstr = false;
		
		for(IOMemoryModule mod : modules)
		{
			if(mod.isAddressWithModule(addr))
			{
				if(mod.isInstruction(addr))
					bInstr = true;
				break;
			}
		}
		
		return bInstr;
	}
	
	/**
	 * This function will execute or evaluate all the modules attached to this bus
	 * @return Ok if success else error code
	 */
	public synchronized int eval()
	{
		int ret = Constants.PLP_OK;
		for(IOMemoryModule mod: modules)
		{
			ret += mod.eval();
		}
		
		window.updateEmulationComponents();
		
		//window.getWatcherWindow().update_values();
		
		return ret;
	}
	
	/**
	 * This method will execute or evalute only a module indicated by index
	 * @param index position where the module is installed in bus
	 * @return Ok for success else error
	 */
	public int eval(int index)
	{
		// There is a possiblity of error here - index not in range
		return modules.get(index).eval();
	}
	
	/**
	 * This method will execute or evaluate the module indicated by the index.
	 * @param index modules index in the Bus
	 * @param x gui frame of that module
	 * @return OK for success else error
	 */
	public int gui_eval(int index, Object x)
	{
		//There is a possiblity of error here - index not in range
		if(modules.get(index).isThreaded())
			return modules.get(index).gui_eval(x);
		else
		{
			modules.get(index).notify();
			return Constants.PLP_OK;
		}
		
		
	}
	
	@Override
	public synchronized int enable_allmodules() {
		for (IOMemoryModule mod: modules)
			mod.enable();
		
		return Constants.PLP_OK;
		
	}

	@Override
	public synchronized int disable_allmodules() {
		for(IOMemoryModule mod: modules)
			mod.disable();
		return Constants.PLP_OK;
	}
	
	
	/**
	 * This method checks if a module indicated by index is enabled or not
	 * @param index location of the module in the bus
	 * @return true if enabled else false
	 */
	public synchronized boolean isEnabled(int index)
	{
		//Index out of range possibility
		return modules.get(index).isEnabled();
	}
	
	/**
	 * This method enables the module indicated by the index
	 * @param index location of the module in the bus
	 * @return OK for success else error
	 */
	public synchronized int enableModule(int index)
	{
		//Index out of range possibility
		modules.get(index).enable();
		return Constants.PLP_OK;
	}
	
	/**
	 * This method disables the module indicated by the index
	 * @param index location of the module in the bus
	 * @return OK for success else error
	 */
	public synchronized int disableModule(int index)
	{
		//Index out of range possibility
		modules.get(index).disable();
		return Constants.PLP_OK;
	}
	
	/**
	 * This will set all the modules content to zero??
	 * @param index location of the module in bus
	 * @return Okay for success else error
	 */
	public int clearModuleRegisters(int index)
	{
		//Index out of range possibility
		modules.get(index).clear();
		return Constants.PLP_OK;
	}
	
	/**
	 * This method returns the string describing that module as indicated by index
	 * @param index location of the module in the bus
	 * @return string describing module
	 */
	public String introduceModule(int index)
	{
		//Index out of range possibility
		return modules.get(index).introduce();
	}
	
	@Override
	/**
	 * This method describes this bus
	 */
	public String toString()
	{
		return "PLPAddressBus";
	}
	
	/**
	 * This method does a read similar to read function but without caring for error
	 * @param addr address whose data needs to be read
	 * @return Object read by the module, if error return null
	 */
	public synchronized Object uncheckedRead(long addr)
	{
		Object value = null;
		for(IOMemoryModule mod: modules)
		{
			if(mod.isAddressWithModule(addr))
			{
				value = mod.read(addr);
				break;
			}
		}
		return value;
	}
	
	/**
	 * This method will resets all the modules installed in this bus
	 */
	public void reset()
	{
		for(IOMemoryModule mod: modules)
			mod.reset();
	}
	
	/**
	 * This method will put zeroes in the registers of that module
	 * @param index module location in the bus
	 */
	public synchronized void issueZeroes(int index)
	{
		IOMemoryModule mod = modules.get(index);
		
		for(int i = 0; i < mod.size(); i++)
		{
			write(mod.startAddress() + i * (mod.isWordAligned() ? 4: 1), new Long(0), false);
		}
	}
	
	/**
	 * Returns the object of the module indicated by the index
	 * @param index location of the module as installed in the bus
	 * @return Object of module
	 */
	public synchronized IOMemoryModule getReferenceModule(int index)
	{
		//Index out of range possibility
		return modules.get(index);
	}
	
	/**
	 * Returns number of modules installed in the bus
	 * @return size of the modules
	 */
	public synchronized int getNumberOfModules()
	{
		return modules.size();
	}
	
	/**
	 * Returns the starting address of the module indicated by the index
	 * @param index location of the module installed in bus
	 * @return start address of the module
	 */
	public synchronized long getModuleStartAddress(int index)
	{
		//Index out of range possibility
		return modules.get(index).startAddress();
	}
	
	/**
	 * Returns the end address of the module indicated by the index
	 * @param index location of the module installed in the bus
	 * @return end address of the module
	 */
	public synchronized long getModuleEndAddress(int index)
	{
		//Index out of range possibility
		return modules.get(index).endAddress();
	}

	@Override
	public void setEmulationWindow(EmulationWindow window) {
		// TODO Auto-generated method stub
		this.window = window;
		
	}


	
	
	

}
