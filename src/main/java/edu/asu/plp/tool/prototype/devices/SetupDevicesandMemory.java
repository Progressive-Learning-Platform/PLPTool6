package edu.asu.plp.tool.prototype.devices;

import edu.asu.plp.tool.backend.isa.Simulator;
import edu.asu.plp.tool.prototype.ApplicationSettings;

import java.util.ArrayList;

import org.json.JSONObject;


public class SetupDevicesandMemory {
	
	public static final String DevicesAndMemoryKey = "DEVICESANDMEMORY";
	public static final String startAddressKey = "START_ADDRESS";
	public static final String sizeKey = "SIZE";
	public static final String alignedKey = "ALIGNED";
	public static final String interruptControllerKey = "INTERRUPT_CONTROLLER";
	public static final String mainMemoryKey = "MAINMEMORY";
	public static final String switchKey = "SWITCHES";
	public static final String ledKey = "LED_ARRAY";
	public static final String gpioKey = "GENERAL_PURPOSE_IO";
	public static final String uartKey = "UART";
	public static final String timerKey = "TIMER";
	public static final String romKey = "ROM";
	public static final String vgaKey = "VGA";
	public static final String sevenSegmentKey = "SEVEN_SEGMENT_LEDS";
	
	public static String SWITCH_INDEX = "";
	public static String LED_INDEX = "";
	public static String MAIN_MEMORY_INDEX = "";
	public static String SEVEN_SEGMENT_INDEX = "";
	public static String UART_INDEX = "";
	public static String IC_INDEX = "";
	
	private Simulator sim;
	
	public SetupDevicesandMemory(Simulator sim)
	{
		this.sim = sim;
	}
	
	
	public void initialize()
	{
		
	}
	
	public void reset()
	{
		
	}
	
	/*private String getKeyName(ArrayList lst)
	{
		String key = "";
		for(Object str:lst)
		{
			String k = (String)str;
			key += ("_"+k);
		}
		
		key = key.replaceFirst("_", "");
		
		return key;
	}*/
	
	public void setup()
	{
		try {
			
	        /*BufferedReader br = new BufferedReader(new FileReader("C:\\D_Drive\\Coding\\plpTool-prototype\\src\\edu\\asu\\plp\\tool\\backend\\plpisa\\devices\\DeviceConfiguration.json"));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();
	        while (line != null) {
	            sb.append(line);
	            line = br.readLine();
	        }
	        br.close();*/
	        
			//JSONObject jobj = ApplicationSettings.getSetting(SetupDevicesandMemory.DevicesAndMemoryKey);
	        //JSONObject jobj = new JSONObject(ApplicationSettings.getSetting(SetupDevicesandMemory.DevicesAndMemoryKey));
	        
	        //Object nobj = ApplicationSettings.getSetting(SetupDevicesandMemory.DevicesAndMemoryKey+"_"+SetupDevicesandMemory.mainMemoryKey+"_"+SetupDevicesandMemory.startAddressKey);
	        
	        //JSONObject obj = (JSONObject)jobj.get("Interrupt Controller");
	        //obj = (JSONObject)jobj.get("MainMemory");
			ArrayList<String> keys = new ArrayList<String>();
			keys.add(SetupDevicesandMemory.DevicesAndMemoryKey);
			keys.add(SetupDevicesandMemory.mainMemoryKey);
			keys.add(SetupDevicesandMemory.startAddressKey);
			String startAddress = ApplicationSettings.getKeyName(keys);
			keys.clear();
			keys.add(SetupDevicesandMemory.DevicesAndMemoryKey);
			keys.add(SetupDevicesandMemory.mainMemoryKey);
			keys.add(SetupDevicesandMemory.sizeKey);
			String size = ApplicationSettings.getKeyName(keys);
			keys.clear();
			keys.add(SetupDevicesandMemory.DevicesAndMemoryKey);
			keys.add(SetupDevicesandMemory.mainMemoryKey);
			keys.add(SetupDevicesandMemory.alignedKey);
			String aligned = ApplicationSettings.getKeyName(keys);
			keys.clear();
	        PLPToolMainMemory mem = new PLPToolMainMemory(Long.decode(ApplicationSettings.getSetting(startAddress).get()),Long.decode(ApplicationSettings.getSetting(size).get()), Boolean.getBoolean(ApplicationSettings.getSetting(aligned).get()));
	        SetupDevicesandMemory.MAIN_MEMORY_INDEX =  sim.getAddressBus().add(SetupDevicesandMemory.mainMemoryKey, mem);
	        //obj = (JSONObject)jobj.get("ROM");
	        //obj = (JSONObject)jobj.get("LED Array");
	        keys.add(SetupDevicesandMemory.DevicesAndMemoryKey);
			keys.add(SetupDevicesandMemory.ledKey);
			keys.add(SetupDevicesandMemory.startAddressKey);
			startAddress = ApplicationSettings.getKeyName(keys);
			keys.clear();
			
	        LEDArray led = new LEDArray(SetupDevicesandMemory.ledKey,
	        							Long.decode(ApplicationSettings.getSetting(startAddress).get()));
	        SetupDevicesandMemory.LED_INDEX =  sim.getAddressBus().add(SetupDevicesandMemory.ledKey, led);
	        //obj = (JSONObject)jobj.get("Switches");
	        keys.add(SetupDevicesandMemory.DevicesAndMemoryKey);
			keys.add(SetupDevicesandMemory.switchKey);
			keys.add(SetupDevicesandMemory.startAddressKey);
			startAddress = ApplicationSettings.getKeyName(keys);
			keys.clear();
	        Switches swit = new Switches(SetupDevicesandMemory.switchKey, 
	        							Long.decode(ApplicationSettings.getSetting(startAddress).get()));
	        SetupDevicesandMemory.SWITCH_INDEX = sim.getAddressBus().add(SetupDevicesandMemory.switchKey, swit);
	        //obj = (JSONObject)jobj.get("PLPID");
	        //obj = (JSONObject)jobj.get("VGA");
	        //obj = (JSONObject)jobj.get("Timer");
	        //obj = (JSONObject)jobj.get("UART");
	        keys.add(SetupDevicesandMemory.DevicesAndMemoryKey);
			keys.add(SetupDevicesandMemory.uartKey);
			keys.add(SetupDevicesandMemory.startAddressKey);
			startAddress = ApplicationSettings.getKeyName(keys);
			keys.clear();
	        UART uart = new UART(Long.decode(ApplicationSettings.getSetting(startAddress).get()), sim);
	        SetupDevicesandMemory.UART_INDEX = sim.getAddressBus().add(SetupDevicesandMemory.uartKey, uart);
	        //obj = (JSONObject)jobj.get("Seven Segment LEDs");
	        keys.add(SetupDevicesandMemory.DevicesAndMemoryKey);
			keys.add(SetupDevicesandMemory.sevenSegmentKey);
			keys.add(SetupDevicesandMemory.startAddressKey);
			startAddress = ApplicationSettings.getKeyName(keys);
			keys.clear();
	        SevenSegmentDisplay segDisplay = new SevenSegmentDisplay(SetupDevicesandMemory.sevenSegmentKey,
	        													Long.decode(ApplicationSettings.getSetting(startAddress).get()));
	        SetupDevicesandMemory.SEVEN_SEGMENT_INDEX = sim.getAddressBus().add(SetupDevicesandMemory.sevenSegmentKey, segDisplay);
	        //obj = (JSONObject)jobj.get("General Purpose IO");
	        
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
