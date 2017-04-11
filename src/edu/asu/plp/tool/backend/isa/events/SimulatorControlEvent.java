package edu.asu.plp.tool.backend.isa.events;

public class SimulatorControlEvent {
	
	private String command;
	private String projectType;
	private Object simulatorData;

	public SimulatorControlEvent(String command, String projectType, Object simulatorData) {
		this.command = command;
		this.projectType = projectType;
		this.simulatorData = simulatorData;
	}

	public String getCommand() {
		return command;
	}

	public String getProjectType() {
		return projectType;
	}

	public Object getSimulatorData() {
		return simulatorData;
	}

}
