package edu.asu.plp.tool.prototype.devices;

import edu.asu.plp.tool.backend.EventRegistry;
import edu.asu.plp.tool.backend.isa.events.DeviceOutputEvent;
import plptool.Constants;


public class SevenSegmentDisplay extends PLPToolIOMemoryModule {
	
	private String deviceName;
	
    public SevenSegmentDisplay(String deviceName, long addr) {
        super(addr, addr, true);
        this.deviceName = deviceName;
    }

    public int eval() {
        if(!enabled)
            return Constants.PLP_OK;

        // Get register value
        if(!isInitialized(startAddress)) {
            return Constants.PLP_SIM_BUS_ERROR;
        }

        int value = super.read(super.startAddress).intValue();
        // No need to eval every cycle
        EventRegistry.getGlobalRegistry().post(new DeviceOutputEvent(deviceName, value));
        return Constants.PLP_OK;
    }

    @Override public void reset() {
        super.write(startAddress, new Long(0xffffffffL), false);
    }

    public String introduce() {
        return "Seven Segments Display";
    }

    @Override
    public String toString() {
        return "SevenSegments";
    }
    
    @Override
	public synchronized void enable() {
		super.enable();
		writeRegister(startAddress(), (long)0, false);
		
	}
}
