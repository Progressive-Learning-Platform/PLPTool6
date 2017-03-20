package edu.asu.plp.tool.backend.mipsisa.assembler2;
//START HERE, DEFINE MIPS TOKEN TYPES

import com.faeysoft.preceptor.lexer.TokenType;
import com.faeysoft.preceptor.lexer.TokenTypeSet;

public enum MIPSTokenType
{
	//Foundation
	//TODO make label colon have nothing after it
	LABEL_COLON("\\b([a-zA-Z]([a-zA-Z]|_|[0-9])*([a-zA-Z]|[0-9])+):\\B"),
	//INSTRUCTION("\\b(addu|subu|mullo|mulhi|and|andi|or|ori|slt|slti|sltu|sltiu|sll|sllv|srl|srlv|bne|jal|jr|jalr|lw|sw)\\b"),
	INSTRUCTION("\\b(add|addu|subu|addiu|and|andi|or|ori|nor|slt|slti|sltu|sltiu|sll|sllv|srl|srlv|beq|bne|j|jal|jr|jalr|lw|sw|multu|divu|mflo|mfhi|mtlo|mthi|xor|xori|clz|clo)\\b"),
	//INSTRUCTION("\\b(addiu|beq|j|lui|nor)"),
	LABEL_PLAIN("\\b([a-zA-Z]([a-zA-Z]|_|[0-9])*([a-zA-Z]|[0-9])+)\\b"),
	ADDRESS("\\$([a-zA-Z]|[0-9])+"),
	COMMENT("#.*$"),
	COMMA(","),
	PARENTHESIS_ADDRESS("([0-9]+|0[xh][0-9a-fA-F]+|0b[01]+)\\(\\$([a-zA-Z]|[0-9])+\\)"),
	DIRECTIVE("\\B\\.(.+?)\\b"),
	STRING("\"(.*?)\""),
	NUMERIC("\\b([-\\+]?(\\d)+|0[xh]([0-9a-fA-F]+)|0b[01]+)\\b"),
	NEW_LINE("^\\s*$");
	//TODO include .include, .text, .data
	
	
	private String regex;
	
	private MIPSTokenType(String regex)
	{
		this.regex = regex;
	}
	
	public String regex()
	{
		return regex;
	}
	
	public static TokenTypeSet createSet()
	{
		TokenTypeSet set = new TokenTypeSet();
		
		for (MIPSTokenType type : MIPSTokenType.values())
		{
			TokenType tokenType = new TokenType(type.regex, type.name());
			set.add(tokenType);
		}
		
		return set;
	}
	
	@Override
	public String toString()
	{
		return super.toString().toLowerCase();
	}
}
