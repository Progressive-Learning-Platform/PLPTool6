package edu.asu.plp.tool.backend.mipsisa.sim;

//import edu.asu.plp.tool.backend.isa.AddressBus;
import edu.asu.plp.tool.backend.plpisa.devices.PLPMainMemory;

public class SetupDevicesandMemory {
	
	private MIPSAddressBus addressBus;
	
	public SetupDevicesandMemory(MIPSAddressBus bus)
	{
		addressBus = bus;
	}
	
	
	public void initialize()
	{
		
	}
	
	public void reset()
	{
		
	}
	
	public void setup()
	{
		//Interrupt
		//RAM
		PLPMainMemory mem = new PLPMainMemory(0x10000000, 0x1000000, true);
		addressBus.add(mem);
		//UART
		//ROM
		//Switches
		//LED
		//Seven Segment Display
	}

}
