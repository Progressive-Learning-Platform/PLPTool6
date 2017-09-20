package edu.asu.plp.tool.backend.isa.events;

/**
 * This class contains the attributes and behaviors of the DeviceOutputEvent. It basically gets
 * the Device Name and the device data which is sent to the OuptutEvent, initialized using the
 * constructor, and returned using the getters.
 */
public class DeviceOutputEvent {

	private String deviceName;
	private Object deviceData;

	public DeviceOutputEvent(String deviceName, Object deviceData) {
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
