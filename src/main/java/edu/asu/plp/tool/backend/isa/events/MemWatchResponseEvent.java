package edu.asu.plp.tool.backend.isa.events;

import javafx.beans.property.LongProperty;

/**
 * This class defines the response object for Memory watch event
 */
public class MemWatchResponseEvent {
	
	private boolean success;
	private long watchedAddress;
	private LongProperty memObject;

	/**
	 * Constructor for Memory watch response object
	 * @param success
	 * @param watchedAddress
	 * @param memObject
	 */
	public MemWatchResponseEvent(boolean success, long watchedAddress, LongProperty memObject) {
		this.success = success;
		this.watchedAddress = watchedAddress;
		this.memObject = memObject;
	}

	/**
	 * getter for success flag
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * getter for address that is watched
	 * @return
	 */
	public long getWatchedAddress() {
		return watchedAddress;
	}

	/**
	 * getter for property of the memory at particular address
	 * @return
	 */
	public LongProperty getMemObject() {
		return memObject;
	}

}
