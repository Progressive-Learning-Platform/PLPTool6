package edu.asu.plp.tool.backend.mipsisa.sim;

import java.util.HashMap;
import java.util.Map;

import edu.asu.plp.tool.backend.isa.RegisterFile;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

/***
 * This class represents the registry file of PLP processor
 * TODO:: Change name to PLPRegisterFile
 * @author Harsha
 *
 */
public class MIPSRegFile implements RegisterFile {
	/***
	 * Total number of registers present in MIPS processor
	 */
	public static final int NUMBER_OF_REGISTERS = 34;
	
	/***
	 * The default value stored in register
	 */
	private static final int DEFAULT_REGISTER_VALUE = 0;
	
	/***
	 * This stores the actual register values
	 */
	private LongProperty[] registers;
	
	/***
	 * This indicates whether each register is holding an instruction or data
	 */
	private BooleanProperty[] regInstructions;
	
	/***
	 * This holds the mapping for register names to their index
	 */
	private Map<String, Integer> namedRegisters;
	
	/***
	 * PLPRegFile constructor. It creates all the registers and initializes to default value
	 */
	public MIPSRegFile()
	{
		this.registers = new LongProperty[NUMBER_OF_REGISTERS];
		this.regInstructions = new BooleanProperty[NUMBER_OF_REGISTERS];
		
		for(int i = 0; i < NUMBER_OF_REGISTERS; i++)
			this.registers[i] = new SimpleLongProperty(DEFAULT_REGISTER_VALUE);
		
		for(int i = 0; i < NUMBER_OF_REGISTERS; i++)
			this.regInstructions[i] = new SimpleBooleanProperty(false);
			
		
		this.namedRegisters = buildNamedRegistersMap();
	}
	
	/**
	 * This function is used to clear all the values in register and set to 0
	 */
	public void reset()
	{
		for(int i = 0; i < NUMBER_OF_REGISTERS; i++)
		{
			registers[i].set(DEFAULT_REGISTER_VALUE);
			regInstructions[i].set(false);
		}
		
		
	}
	
	/***
	 * This will map all the named registers to their respective index values
	 * @return dictionary/map having register to index mapping
	 */
	private Map<String, Integer> buildNamedRegistersMap()
	{
		// TODO: This is sloppy. Consider reading from JSON?
		Map<String, Integer> map = new HashMap<>();
		map.put("zero", 0);
		
		map.put("at", 1);
		
		map.put("v0", 2);
		map.put("v1", 3);
		
		map.put("a0", 4);
		map.put("a1", 5);
		map.put("a2", 6);
		map.put("a3", 7);
		
		map.put("t0", 8);
		map.put("t1", 9);
		map.put("t2", 10);
		map.put("t3", 11);
		map.put("t4", 12);
		map.put("t5", 13);
		map.put("t6", 14);
		map.put("t7", 15);
		
		map.put("s0", 16);
		map.put("s1", 17);
		map.put("s2", 18);
		map.put("s3", 19);
		map.put("s4", 20);
		map.put("s5", 21);
		map.put("s6", 22);
		map.put("s7", 23);
		
		map.put("t8", 24);
		map.put("t9", 25);
		
		map.put("k0", 26);
		map.put("k1", 27);
		map.put("gp", 28);
		map.put("sp", 29);
		map.put("s8", 30);
		
		map.put("ra", 31);
		
		//pseudo-registers
		map.put("lo", 32);
		map.put("hi", 33);
		return map;
	}
	
	/***
	 * A register name can be given as $s1 or s1. In either case make a standard form and return that name.
	 * @param registerName register whose name has to be standardized
	 * @return if $s1 is given then s1 will be returned else return as it is
	 */
	private String normalizeRegisterName(String registerName)
	{
		if (registerName == null)
		{
			return null;
		}
		else if (registerName.startsWith("$"))
		{
			return registerName.substring(1);
		}
		else
		{
			return registerName;
		}
	}
	
	/**
	 * Given a register name corresponding index value of that register will be returned
	 * @param registerName register whose index value needs to be fetched
	 * @return index value of the register
	 */
	private int convertNameToIndex(String registerName)
	{
		registerName = normalizeRegisterName(registerName);
		Integer mappedIndex = namedRegisters.get(registerName);
		try
		{
			return (mappedIndex != null) ? mappedIndex : Integer.parseInt(registerName);
		}
		catch (Exception e)
		{
			return -1;
		}
	}
	
	/**
	 * It checks if the given registername is valid register or not
	 * @param registerName Register Name which needs to be verified
	 * @return true of register exists else false
	 */
	public boolean hasRegister(String registerName)
	{
		int index = convertNameToIndex(registerName);
		return registerIndexIsValid(index);
	}
	
	/**
	 * Given an index of any register whether that index is valid or not
	 * @param index index which needs to be checked whether it is in the range
	 * @return true if index is present with in the range else false
	 */
	private boolean registerIndexIsValid(int index)
	{
		return index >= 0 && index < registers.length;
	}
	
	/**
	 * This gets the value of the given register. It actually gives the property so we can bind to any other object
	 * @param registerName Register whose value property needs to be fetched
	 * @return returns the registers value propery.
	 */
	public LongProperty getRegisterValueProperty(String registerName)
	{
		int index = convertNameToIndex(registerName);
		if (registerIndexIsValid(index))
			return registers[index];
		else
			return null;
	}
	
	/**
	 * Given a register it will return the index corresponding to that register.
	 * @param registerName register whose index needs to be fetched
	 * @return index of the register
	 */
	public String getRegisterID(String registerName)
	{
		int index = convertNameToIndex(registerName);
		if (registerIndexIsValid(index))
			return "$" + index;
		else
			return null;
	}
	
	/**
	 * Check if given register index is valid. If not valid throw an exception
	 * @param address index of the register
	 */
	public void validateAddress(int address)
	{
		/*if ((address % 4) != 0)
			throw new IllegalArgumentException("Address must be word-aligned");*/
		
		if(address >= this.NUMBER_OF_REGISTERS || address < 0)
			throw new IllegalArgumentException("Register Number can be between 0 to " + Integer.toString(this.NUMBER_OF_REGISTERS));
	}
	
	/**
	 * This function will read the content of the register and return its value.
	 * @param address index of the register which needs to be read
	 * @return value of the register
	 */
	public long read(int address)
	{
		long value = 0;
		
		validateAddress(address);
		
		value = registers[address].get();
		
		return value;
		
	}
	
	/**
	 * This function will write the content of the register
	 * @param address register index where value needs to be written
	 * @param value value to be stored in register
	 * @param isInstruction whether the value if bytecode of an instruciton or not
	 */
	public void write(int address, long value, boolean isInstruction)
	{
		validateAddress(address);
		registers[address].set(value);
		//registers[address].
		regInstructions[address].set(isInstruction);
	}
	
	/**
	 * This function will write the value to the registers of $lo and $hi
	 * @param value to be stored in $hi
	 * @param value to stored in $lo
	 */
	public void write(long valueLO, long valueHI) {
		registers[32].set(valueLO);
		registers[33].set(valueHI);
	}
	
	/**
	 * This function will write the value to a register given the registers index
	 * @param address index of the register where data needs to be written
	 * @param value data to be written to register
	 */
	public void write(int address, long value)
	{
		validateAddress(address);
		registers[address].set(value);
	}
	
	/**
	 * This function will write the value of $lo or $hi to a given register
	 * @param address index of the register where data needs to be written
	 * @param value data to be written to register
	 */
	public void write(int address, boolean lo, boolean from)
	{
		validateAddress(address);
		if (lo) {
			if (from) {
				registers[address].set(registers[32].get());
			} else { //to low
				registers[32].set(registers[address].get());
			}
		} else {
			if (from) {
				registers[address].set(registers[33].get());
			} else { //to high
				registers[33].set(registers[address].get());
			}
		}
		
	}
	
	/**
	 * Checks if the register indicated by the address holds an instruction or not
	 * @param address index of the register whose instruction verification needs to be done
	 * @return true if data stored is an instruciton else false
	 */
	public boolean isInstruction(int address)
	{
		validateAddress(address);
		return regInstructions[address].get();
	}

}
