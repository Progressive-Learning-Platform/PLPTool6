package edu.asu.plp.tool.backend.mipsisa;

public enum MIPSInstruction
{
	SHIFT_LEFT_LOGICAL("sll", "function", 0x00, 1),
	SHIFT_LEFT_LOGICAL_VARIABLE("sllv", "function", 0x01, 0),
	SHIFT_RIGHT_LOGICAL("srl", "function", 0x02, 1),
	SHIFT_RIGHT_LOGICAL_VARIABLE("srlv", "function", 0x03, 0),
	JUMP_RETURN("jr", "function", 0x08, 2),
	JUMP_AND_LINK_RETURN("jalr", "function", 0x09, 9),
	//MULTIPLY_LOW("mullo", "function", 0x10, 8),
	//MULTIPLY_HIGH("mulhi", "function", 0x11, 8),
	//ADD("add", "function", 0x20),
	ADD_UNSIGNED("addu", "function", 0x21, 0),
	SUBTRACT_UNSIGNED("subu", "function", 0x23, 0),
	//SUBTRACT("sub", "function", 0x22),
	AND("and", "function", 0x24, 0),
	OR("or", "function", 0x25, 0),
	NOR("nor", "function", 0x27, 0),
	SET_ON_LESS("slt", "function", 0x2A, 0),
	SET_ON_LESS_UNSIGNED("sltu", "function", 0x2B, 0),
	
	JUMP("j", "opcode", 0x02, 7),
	JUMP_AND_LINK("jal", "opcode", 0x03, 7),
	BRANCH_EQUALS("beq", "opcode", 0x04, 3),
	BRANCH_NOT_EQUALS("bne", "", 0x05, 3),
	ADD_IMMEDIATE_UNSIGNED("addiu", "opcode", 0x09, 4),
	SET_ON_LESS_IMMEDIATE("slti", "opcode", 0x0A, 4),
	SET_ON_LESS_IMMEDIATE_UNSIGNED("sltiu", "opcode", 0x0B, 4),
	AND_IMMEDIATE("andi", "opcode", 0x0C, 4),
	OR_IMMEDIATE("ori", "opcode", 0x0D, 4),
	//LOAD_UPPER_IMMEDIATE("lui", "opcode", 0x0F, 5),
	LOAD_WORD("lw", "opcode", 0x23, 6),
	STORE_WORD("sw", "opcode", 0x2B, 6),
	
	//MIPS ONLY
	ADD("add", "function", 0x20, 0),
	ADD_IMMEDIATE("addi", "opcode", 0x08, 4),
	SUBTRACT("sub", "function", 0x22, 0),
	MULTIPLY("mult", "function", 0x18, 0),
	MULTIPLY_UNSIGNED("multu", "function", 0x19, 0),
	DIVIDE("div", "function", 0x1A, 0),
	DIVIDE_UNSIGNED("divu", "function", 0x1B, 0),
	MOVE_FROM_LOW("mflo", "function", 0x12, 0),
	MOVE_FROM_HIGH("mfhi", "function", 0x10, 0),
	MOVE_TO_LOW("mtlo", "function", 0x13, 0),
	MOVE_TO_HIGH("mthi", "function", 0x11, 0),
	EXCLUSIVE_OR("xor", "function", 0x26, 0),
	EXCLUSIVE_OR_IMMEDIATE("xori", "opcode", 0x0E, 4),
	SIGN_EXTEND_HALFWORD("seh", "opcode", 0x1F, 9),
	SIGN_EXTEND_BYTE("seb", "opcode", 0x1F, 9),
	WORD_SWAP_BYTE_HALFWORD("wsbh", "opcode", 0x1F, 9),
	INSERT_BYTE_FIELD("ins", "opcode", 0x1F, 8),
	EXTRACT_BYTE_FIELD("ext", "opcode", 0x1F, 8),
	MOVE_IF_ZERO("movz", "function", 0x0a, 4),
	MOVE_IF_NOT_ZERO("movn", "function", 0x0b, 4),
	ROTATE_RIGHT("rotr", "function", 0x02, 1),
	ROTATE_RIGHT_VARIABLE("rotrv", "function", 0x06, 0),
	SHIFT_RIGHT_ARITHMATIC("sra", "function", 0x03, 1),
	SHIFT_RIGHT_ARITHMATIC_VARIABLE("srav", "function", 0x07, 0),
	COUNT_LEADING_ZEROES("clz", "opcode", 0x1C, 9),
	COUNT_LEADING_ONES("clo", "opcode", 0x1C, 9),
	MULTIPLY_32BIT("mul", "opcode", 0x1C, 4),
	MULTIPLY_ADD("madd", "opcode", 0x1C, 0),
	MULTIPLY_ADD_UNSIGNED("maddu", "opcode", 0x1C, 0),
	MULTIPLY_SUB("msub", "opcode", 0x1C, 0),
	MULTIPLY_SUB_UNSIGNED("msubu", "opcode", 0x1C, 0),
	BRANCH_GREATER_THAN_EQUAL_TO_ZERO("bgez", "opcode", 0x01, 10),
	BRANCH_GREATER_THAN_EQUAL_TO_ZERO_AND_LINK("bgezal", "opcode", 0x01, 10),
	BRANCH_GREATER_THAN_ZERO("bgtz", "opcode", 0x07, 10),
	BRANCH_LESS_THAN_EQUAL_TO_ZERO("blez", "opcode", 0x06, 10),
	BRANCH_LESS_THAN_ZERO("bltz", "opcode", 0x01, 10),
	BRANCH_LESS_THAN_ZERO_AND_LINK("bltzal", "opcode", 0x01, 10),
	LOAD_BYTE("lb", "opcode", 0x20, 6),
	LOAD_BYTE_UNSIGNED("lbu", "opcode", 0x24, 6),
	LOAD_HALFWORD("lh", "opcode", 0x21, 6),
	LOAD_HALFWORD_UNSIGNED("lhu", "opcode", 0x25, 6),
	LOAD_WORD_LEFT("lwl", "opcode", 0x22, 6),
	LOAD_WORD_RIGHT("lwr", "opcode", 0x26, 6),
	STORE_BYTE("sb", "opcode", 0x28, 6),
	STORE_HALFWORD("sh", "opcode", 0x29, 6),
	STORE_WORD_LEFT("swl", "opcode", 0x2A, 6),
	STORE_WORD_RIGHT("swr", "opcode", 0x2E, 6);
	
	private String identifier;
	private String type;
	private int byteCode;
	private int instructionType;
	
	// TODO: consider removing "type"
	private MIPSInstruction(String identifier, String type, int byteCode, int instructionType)
	{
		this.identifier = identifier;
		this.type = type;
		this.byteCode = byteCode;
		this.instructionType = instructionType;
	}

	public String getMnemonic()
	{
		return identifier;
	}

	public String getType()
	{
		return type;
	}

	public int getByteCode()
	{
		return byteCode;
	}

	public int getInstructionType()
	{
		return instructionType;
	}
	
}
