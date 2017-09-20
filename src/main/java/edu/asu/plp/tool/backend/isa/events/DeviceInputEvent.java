package edu.asu.plp.tool.backend.isa.events;
/**
 * This class contains the attributes and behaviors of the DeviceInputEvent. It basically gets
 * the Device Name and the data that is sent as input to the device, which is initialized using
 * the constructor, and returned using the getters.
 */
public class DeviceInputEvent {
	
	private String deviceName;
	private Object deviceData;

	public DeviceInputEvent(String deviceName, Object deviceData) {
		this.deviceName = deviceName;
		this.deviceData = deviceData;
	}
	/**
 	*
 	* @return current device name (in String)
 	*/
	public String getDeviceName() {
		return deviceName;
	}
	
	/**
	 *
 	* @return data associated with the corresponding device name
 	*/
	public Object getDeviceData() {
		return deviceData;
	}
}
