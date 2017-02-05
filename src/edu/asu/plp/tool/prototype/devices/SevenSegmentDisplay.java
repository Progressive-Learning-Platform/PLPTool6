package edu.asu.plp.tool.prototype.devices;

import java.util.ArrayList;

import edu.asu.plp.tool.prototype.view.SevenSegmentPanel;
import edu.asu.plp.tool.prototype.view.SevenSegmentPanel.Segment;
import plptool.Constants;
import javafx.scene.layout.HBox;


public class SevenSegmentDisplay extends PLPToolIOMemoryModule {
    public SevenSegmentDisplay(long addr) {
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
        if(!isInitialized(startAddress)) {
            return Constants.PLP_SIM_BUS_ERROR;
        }

        int value = super.read(super.startAddress).intValue();
        //int value = (Integer) super.read(super.startAddress);
        HBox hbox = (HBox) ((SevenSegmentPanel)x).getCenter();
        Object lis = hbox.getChildren();
        Object ar[] = hbox.getChildren().toArray();
        ArrayList<SevenSegmentPanel.Segment> segments = new ArrayList<SevenSegmentPanel.Segment>();
        for(Object ob: ar)
        {
        	segments.add((SevenSegmentPanel.Segment)ob);
        }
        
        int maskValue = 0x000000FF;
        int nCount = 0;
        for(SevenSegmentPanel.Segment seg: segments)
        {
        	int afterMaskValue = maskValue & value;
        	int temp = nCount;
        	while(temp > 0)
        	{
        		afterMaskValue = afterMaskValue >> 8;
        		temp--;
        	}
        	//String str = Integer.toBinaryString(afterMaskValue);
        	seg.setState(afterMaskValue);
        	
        	maskValue = maskValue<<8;
        	nCount++;
        	
        	
        }
        
       
        /*
        /* Converting from BCD to 7 segment in reverse order according to the interpretation 
        of state in setState(int state) function of SevenSegmentPanel.java 
        For eg: 0(BCD) -> {1,1,0,0,0,0,0,0} in the form {h,g,f,e,d,c,b,a}(7 segment)-> 192 (11000000)
        where (a to g) are the 7 segments and h is for decimal point which is always masked (off state)
        
        int convert[] = {192, 249, 164, 176, 153, 146, 130, 248, 128, 152};
		int temp = 0;
        int prev = (int)value;
        
        // Only considering lower 4 digits (4 seven segment displays)
        if(value/10000 != 0){
        	temp = (int)value/10000;
        	prev = prev - temp * 10000;
        }
        temp = 0;
        for(int j = 3; j >= 0; j--){
        	temp = (int) (prev / (Math.pow(10, j)));
        	segments.get(j).setState(convert[temp]);
        	prev = (int) (prev - temp * (Math.pow(10, j)));
        }*/
        
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
