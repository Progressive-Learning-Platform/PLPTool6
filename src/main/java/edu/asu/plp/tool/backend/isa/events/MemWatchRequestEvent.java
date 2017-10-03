package edu.asu.plp.tool.backend.isa.events;

/**
 * This class defines the request object for Memory watch event
 */
public class MemWatchRequestEvent {

	private long memoryAddress;

	/**
	 * Constructor for Memory watch request object
	 * @param memoryAddress Address of the memory that is requested to be watched
	 */
	public MemWatchRequestEvent(long memoryAddress) {
		this.memoryAddress = memoryAddress;
	}

	/**
	 * getter for address that is requested to be watched
	 * @return address that is requested to be watched
	 */
	public long getMemoryAddress() {
		return memoryAddress;
	}
}
