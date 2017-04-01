package edu.asu.plp.tool.prototype.devices;


import java.util.ArrayList;
import java.util.List;

import edu.asu.plp.tool.backend.isa.events.IOEvent;
import plptool.Constants;


public class LEDArray extends PLPToolIOMemoryModule {

    private List<IOEvent> listeners = new ArrayList<IOEvent>();

    public LEDArray(long addr) {
        super(addr, addr, true);
    }

    public int eval() {
        if(!enabled)
            return Constants.PLP_OK;

        // Get register value
        if(!isInitialized(startAddress)) {
            return Constants.PLP_SIM_BUS_ERROR;
        }
        
        long value = super.read(super.startAddress);
        for (IOEvent hl : listeners) {
            hl.recevieUpdateEvent((long)value);
        }
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

	@Override
	public void addListener(IOEvent toAdd) {
		listeners.add(toAdd);
	}
}