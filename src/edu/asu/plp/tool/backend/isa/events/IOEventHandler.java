package edu.asu.plp.tool.backend.isa.events;

public abstract class IOEventHandler implements IOEvent{
	public abstract void recevieUpdateEvent(long value);
}
