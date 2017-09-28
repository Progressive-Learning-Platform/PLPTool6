package edu.asu.plp.tool.backend.isa.events;

/**
 * This class offers the communication object(event) from front-end to back-end. The front-end sets
 * the Device Name and the data that is sent as input to the back-end device module using
 * the constructor, and the back-end module gets the data using the getters.
 */
public class DeviceInputEvent {
	
	private String deviceName;
	private Object deviceData;

	/**
	 * @brief A constructor to initialize the Device Name and the data sent as input to the back-end module
	 * @param deviceName a device name for matching the backend device module name
	 * @param deviceData data of the device sent as input to the back-end module
	 * @note if the deviceName doesn't exist on the backend, this input event will be discarded.
	 */
	public DeviceInputEvent(String deviceName, Object deviceData) {
		this.deviceName = deviceName;
		this.deviceData = deviceData;
	}

	/**
	 * @brief This is a getter method used by the back-end module to get the Device Name sent by the front-end module
	 * @return current device name (in String)
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @brief This is a getter method used by the back-end module to get the Device Data sent by the front-end module
	 * @return data associated with the corresponding device name
	 */
	public Object getDeviceData() {
		return deviceData;
	}
}
