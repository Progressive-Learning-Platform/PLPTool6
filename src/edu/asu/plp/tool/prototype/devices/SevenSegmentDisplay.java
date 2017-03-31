package edu.asu.plp.tool.prototype.devices;

import java.util.ArrayList;
import java.util.List;

import edu.asu.plp.tool.backend.isa.events.IOEvent;
import plptool.Constants;


public class SevenSegmentDisplay extends PLPToolIOMemoryModule {
	
	private List<IOEvent> listeners = new ArrayList<IOEvent>();
	
    public SevenSegmentDisplay(long addr) {
        super(addr, addr, true);
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
        for (IOEvent hl : listeners) {
            hl.recevieUpdateEvent((long)value);
        }
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

	@Override
	public void addListener(IOEvent toAdd) {
		listeners.add(toAdd);
	}
}
