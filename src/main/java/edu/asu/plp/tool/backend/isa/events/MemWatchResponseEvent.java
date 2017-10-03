package edu.asu.plp.tool.backend.isa.events;

import javafx.beans.property.LongProperty;

/**
 * This class defines the response object for Memory watch event when the user requests for an address to be watched
 */
public class MemWatchResponseEvent {
	
	private boolean success;
	private long watchedAddress;
	private LongProperty memObject;

	/**
	 * Constructor for Memory watch response object
	 * @param success Memory watch request result, true is a success, otherwise a failure.
	 * @param watchedAddress Address of the memory that is requested to be watched
	 * @param memObject reference of the memory object at particular address
	 */
	public MemWatchResponseEvent(boolean success, long watchedAddress, LongProperty memObject) {
		this.success = success;
		this.watchedAddress = watchedAddress;
		this.memObject = memObject;
	}

	/**
	 * getter for successor flag which only when true the reference of the memory object is
	 * fetched and the address is added to rows of memory addresses
	 * @return
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * getter for address that is watched
	 * @return a valid address when the watch request succeed, otherwise the address is invalid
	 */
	public long getWatchedAddress() {
		return watchedAddress;
	}

	/**
	 * Getter for the reference of the memory object at particular address.
	 * @return the reference of the memory object at particular address.
	 */
	public LongProperty getMemObject() {
		return memObject;
	}

}
