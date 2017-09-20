package edu.asu.plp.tool.backend.isa.events;

public class DeviceInputEvent {
	
	private String deviceName;
	private Object deviceData;

	public DeviceInputEvent(String deviceName, Object deviceData) {
		this.deviceName = deviceName;
		this.deviceData = deviceData;
	}

	public String getDeviceName() {
		return deviceName;
	}
	
	public Object getDeviceData() {
		return deviceData;
	}
}
