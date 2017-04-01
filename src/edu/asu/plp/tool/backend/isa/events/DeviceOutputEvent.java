package edu.asu.plp.tool.backend.isa.events;

public class DeviceOutputEvent {

	private String deviceName;
	private Object deviceData;

	public DeviceOutputEvent(String deviceName, Object deviceData) {
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
