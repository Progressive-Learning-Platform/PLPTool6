package edu.asu.plp.tool.backend.plpisa.sim;

import edu.asu.plp.tool.backend.plpisa.devices.LEDArray;
//import edu.asu.plp.tool.backend.isa.AddressBus;
import edu.asu.plp.tool.backend.plpisa.devices.PLPMainMemory;
import edu.asu.plp.tool.backend.plpisa.devices.SevenSegmentDisplay;
import edu.asu.plp.tool.backend.plpisa.devices.Switches;
import edu.asu.plp.tool.backend.plpisa.devices.UART;
import plptool.Constants;

import java.io.*;
import org.json.JSONObject;


public class SetupDevicesandMemory {
	
	private PLPSimulator sim;
	
	public SetupDevicesandMemory(PLPSimulator sim)
	{
		this.sim = sim;
	}
	
	
	public void initialize()
	{
		
	}
	
	public void reset()
	{
		
	}
	
	public void setup()
	{
		try {
			
	        BufferedReader br = new BufferedReader(new FileReader("C:\\D_Drive\\Coding\\plpTool-prototype\\src\\edu\\asu\\plp\\tool\\backend\\plpisa\\devices\\DeviceConfiguration.json"));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        br.close();
	        
	        JSONObject jobj = new JSONObject(sb.toString());
	        
	        JSONObject obj = (JSONObject)jobj.get("Interrupt Controller");
	        obj = (JSONObject)jobj.get("MainMemory");
	        PLPMainMemory mem = new PLPMainMemory(Long.decode(obj.getString("start address")),Long.decode(obj.getString("size")), obj.getBoolean("aligned"));
			sim.addressBus.add(mem);
	        obj = (JSONObject)jobj.get("ROM");
	        obj = (JSONObject)jobj.get("LED Array");
	        LEDArray led = new LEDArray(Long.decode(obj.getString("start address")));
	        sim.addressBus.add(led);
	        obj = (JSONObject)jobj.get("Switches");
	        Switches swit = new Switches(Long.decode(obj.getString("start address")));
	        sim.addressBus.add(swit);
	        obj = (JSONObject)jobj.get("PLPID");
	        obj = (JSONObject)jobj.get("VGA");
	        obj = (JSONObject)jobj.get("Timer");
	        obj = (JSONObject)jobj.get("UART");
	        UART uart = new UART(Long.decode(obj.getString("start address")), sim);
	        sim.addressBus.add(uart);
	        obj = (JSONObject)jobj.get("Seven Segment LEDs");
	        SevenSegmentDisplay segDisplay = new SevenSegmentDisplay(Long.decode(obj.getString("start address")));
	        sim.addressBus.add(segDisplay);
	        obj = (JSONObject)jobj.get("General Purpose IO");
	        
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
		
		//Interrupt
		//RAM
		
		//UART
		//ROM
		//Switches
		//LED
		//Seven Segment Display
	}

}
