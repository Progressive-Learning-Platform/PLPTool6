package edu.asu.plp.tool.backend.isa.events;

/**
 * This class offers the communication object(event) from back-end to front-end. The back-end sets
 * the Device Name and the data that is sent as input to the front-end device module using
 * the constructor, and the front-end module gets the data using the getters.
 */
public class DeviceOutputEvent {

	private String deviceName;
	private Object deviceData;

	/**
	 * @brief A constructor to initialize the Device Name and the data by back-end, sent as input to the front-end module
	 * @param deviceName a device name for matching the front-end device module name
	 * @param deviceData data of the device sent as input to the front-end module
	 * @note if the deviceName doesn't exist on the frontend, this input event will be discarded.
	 */
	public DeviceOutputEvent(String deviceName, Object deviceData) {
		this.deviceName = deviceName;
		this.deviceData = deviceData;
	}

	/**
	 * @brief This is a getter method used by the front-end module to get the Device Name, sent by the back-end module
	 * @return current device name (in String)
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @brief This is a getter method used by the front-end module to get the Device Data, sent by the back-end module
	 * @return data associated with the corresponding device name
	 */
	public Object getDeviceData() {
		return deviceData;
	}
}

