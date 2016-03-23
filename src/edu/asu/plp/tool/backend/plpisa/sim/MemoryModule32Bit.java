package edu.asu.plp.tool.backend.plpisa.sim;

import javafx.beans.property.IntegerProperty;

public interface MemoryModule32Bit
{
	boolean hasRegister(String registerName);

	IntegerProperty getRegisterValueProperty(String registerName);

	String getRegisterID(String registerName);

	IntegerProperty getMemoryValueProperty(int address);

	void validateAddress(int address);
}
