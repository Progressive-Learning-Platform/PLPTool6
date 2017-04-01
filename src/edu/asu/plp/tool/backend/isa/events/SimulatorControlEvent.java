package edu.asu.plp.tool.backend.isa.events;

public class SimulatorControlEvent {
	
	private String command;
	private Object simulatorData;

	public SimulatorControlEvent(String command, Object simulatorData) {
		this.command = command;
		this.simulatorData = simulatorData;
	}

	public String getCommand() {
		return command;
	}

	public Object getSimulatorData() {
		return simulatorData;
	}

}
