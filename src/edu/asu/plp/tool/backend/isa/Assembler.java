package edu.asu.plp.tool.backend.isa;

import java.util.HashMap;

public abstract class Assembler
{
	/**
	 * <p>
	 * Some ISA's have opCodes that span different lengths.
	 * </p>
	 * <p>
	 * e.g. one-byte & two-byte.
	 * </p>
	 */
	protected int[] allowedOpCodeLengths;
	
	/**
	 * Size of opcode, Byte etc.
	 */
	protected int opCodeSize;
	
	/**
	 * Since opCodeSize can be of sizes > 1 byte, they are stored in an integer;
	 */
	protected HashMap<String, Integer> instructionOpCodeMap;
	
	/*
	 * 
	 */
	protected HashMap<String, Integer> pseudoInstructionMap;
	
	/*
	 * Most CPUs dont even pass 40 registers, so a byte is okay.
	 */
	protected HashMap<String, Byte> registerMap;
	
	public abstract ASMImage assemble(String source);
}
