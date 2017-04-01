package edu.asu.plp.tool.backend.isa.events;

public class MemWatchRequestEvent {
	
	private long memoryAddress;

	public MemWatchRequestEvent(long memoryAddress) {
		this.memoryAddress = memoryAddress;
	}

	public long getMemoryAddress() {
		return memoryAddress;
	}
}
