package edu.asu.plp.tool.backend.plpisa.devices;

import edu.asu.plp.tool.prototype.view.LEDDisplay;
import plptool.PLPSimBusModule;
import plptool.Constants;


public class LEDArray extends PLPSimBusModule {

    public LEDArray(long addr) {
        super(addr, addr, true);
    }

    public int eval() {
        // No need to eval every cycle
        return Constants.PLP_OK;
    }

    public int gui_eval(Object x) {
        if(!enabled)
            return Constants.PLP_OK;

        // Get register value
        if(!isInitialized(startAddr)) {
            return Constants.PLP_SIM_BUS_ERROR;
        }
        
        long value = (Long) super.read(super.startAddr);

        // Combinational logic
        for(int i = 7; i >= 0; i--) {
            if((value & (long) Math.pow(2, i)) == (long) Math.pow(2, i))
                ((LEDDisplay)x).setLEDState(i, true);
            else
                ((LEDDisplay)x).setLEDState(i, false);
        }

        return Constants.PLP_OK;
    }

    @Override public void reset() {
        super.writeReg(super.startAddr, new Long(0L), false);
    }

    public String introduce() {
        return "LED array";
    }

    @Override
    public String toString() {
        return "LEDArray";
    }
}