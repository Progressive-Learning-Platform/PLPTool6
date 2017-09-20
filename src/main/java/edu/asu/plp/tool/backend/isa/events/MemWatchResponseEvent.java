package edu.asu.plp.tool.backend.isa.events;

import javafx.beans.property.LongProperty;

public class MemWatchResponseEvent {
	
	private boolean success;
	private long watchedAddress;
	private LongProperty memObject;

	public MemWatchResponseEvent(boolean success, long watchedAddress, LongProperty memObject) {
		this.success = success;
		this.watchedAddress = watchedAddress;
		this.memObject = memObject;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public long getWatchedAddress() {
		return watchedAddress;
	}

	public LongProperty getMemObject() {
		return memObject;
	}

}
