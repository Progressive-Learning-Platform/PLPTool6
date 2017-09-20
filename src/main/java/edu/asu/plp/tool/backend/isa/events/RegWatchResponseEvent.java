package edu.asu.plp.tool.backend.isa.events;

import javafx.beans.property.LongProperty;

public class RegWatchResponseEvent {

	private boolean success;
	private String registerID;
	private String registerName;
	private LongProperty regObject;

	public RegWatchResponseEvent(boolean success, String registerID, String registerName, LongProperty regObject) {
		this.success = success;
		this.registerID = registerID;
		this.registerName = registerName;
		this.regObject = regObject;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public String getRegisterID() {
		return registerID;
	}

	public String getRegisterName() {
		return registerName;
	}
	
	public LongProperty getRegObject() {
		return regObject;
	}

}
