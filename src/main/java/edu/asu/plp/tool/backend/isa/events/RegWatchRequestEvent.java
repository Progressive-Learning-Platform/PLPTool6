package edu.asu.plp.tool.backend.isa.events;

// Class to watch register values
public class RegWatchRequestEvent {

	private String registerName;

	// Constructor with registerName as a parameter
	public RegWatchRequestEvent(String registerName) {
		this.registerName = registerName;
	}

	// Function to get the register Name
	public String getRegisterName() {
		return registerName;
	}

}
