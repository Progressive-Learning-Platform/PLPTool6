package edu.asu.plp.tool.backend.isa;

import java.util.HashMap;

import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;

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
	protected HashMap<String, Integer> instructionOpcodeMap;
	
	/*
	 * <p>Pseudo Operation mneumonic label mapped to how many tokens follow.</p>
	 * <p>e.g.</p>
	 * <p>nop -> 0</p>
	 * <p>b -> 1 (b label)</p>
	 * <p>move -> 3 (move $rd, $rs)</p>
	 */
	protected HashMap<String, Integer> pseudoOperationMap;
	
	/*
	 * Most CPUs dont even pass 40 registers, so a byte is okay.
	 */
	protected HashMap<String, Byte> registerMap;
	
	public abstract ASMImage assemble() throws AssemblerException;
}
