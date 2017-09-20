package edu.asu.plp.tool.backend.isa.events;

/**
 * This class defines the request object for Memory watch event
 */
public class MemWatchRequestEvent {

	private long memoryAddress;

	/**
	 * Constructor for Memory watch request object
	 * @param memoryAddress
	 */
	public MemWatchRequestEvent(long memoryAddress) {
		this.memoryAddress = memoryAddress;
	}

	/**
	 * getter for address that is requested to be watched
	 * @return
	 */
	public long getMemoryAddress() {
		return memoryAddress;
	}
}
