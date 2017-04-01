package edu.asu.plp.tool.prototype.devices;

import edu.asu.plp.tool.backend.EventRegistry;
import edu.asu.plp.tool.backend.isa.events.DeviceOutputEvent;
import plptool.Constants;


public class LEDArray extends PLPToolIOMemoryModule {
	
	private String deviceName = null;

    public LEDArray(String deviceName, long addr) {
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
        
        long value = super.read(super.startAddress);

        EventRegistry.getGlobalRegistry().post(new DeviceOutputEvent(deviceName, value));
        // No need to eval every cycle
        return Constants.PLP_OK;
    }

    @Override public void reset() {
        super.write(super.startAddress, new Long(0L), false);
    }

    public String introduce() {
        return "LED array";
    }

    @Override
    public String toString() {
        return "LEDArray";
    }
    
    @Override
	public synchronized void enable() {
		super.enable();
		writeRegister(startAddress(), (long)0, false);
		
	}
}