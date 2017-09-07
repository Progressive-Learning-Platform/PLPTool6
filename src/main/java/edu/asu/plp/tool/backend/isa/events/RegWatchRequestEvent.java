package edu.asu.plp.tool.backend.isa.events;

public class RegWatchRequestEvent {

	private String registerName;

	public RegWatchRequestEvent(String registerName) {
		this.registerName = registerName;
	}

	public String getRegisterName() {
		return registerName;
	}

}
