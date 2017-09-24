package edu.asu.plp.tool.backend.isa.events;

import javafx.beans.property.LongProperty;

public class RegWatchResponseEvent {

	// Boolean Variable to store the status (True or False)
	private boolean success;

	// String variable to hold the registerID
	private String registerID;

	// String variable to hold the registerName
	private String registerName;

	//LongProperty class defines a Property wrapping a long value.
	private LongProperty regObject;

	// Constructor
	public RegWatchResponseEvent(boolean success, String registerID, String registerName, LongProperty regObject) {
		this.success = success;
		this.registerID = registerID;
		this.registerName = registerName;
		this.regObject = regObject;
	}

	// Function returns success if the RegWatchResponseEvent is a success
	public boolean isSuccess() {
		return success;
	}

	// Getter Function to return registerID
	public String getRegisterID() {
		return registerID;
	}

	// Getter Function to return RegisterName
	public String getRegisterName() {
		return registerName;
	}

	// Getter Function to return RegisterObject
	public LongProperty getRegObject() {
		return regObject;
	}

}
