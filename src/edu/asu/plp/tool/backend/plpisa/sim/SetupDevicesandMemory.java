package edu.asu.plp.tool.backend.plpisa.sim;

//import edu.asu.plp.tool.backend.isa.AddressBus;
import edu.asu.plp.tool.backend.plpisa.devices.PLPMainMemory;

public class SetupDevicesandMemory {
	
	private PLPAddressBus addressBus;
	
	public SetupDevicesandMemory(PLPAddressBus bus)
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
