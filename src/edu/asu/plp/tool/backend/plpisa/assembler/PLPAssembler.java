package edu.asu.plp.tool.backend.plpisa.assembler;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.faeysoft.preceptor.lexer.LexException;
import com.faeysoft.preceptor.lexer.Lexer;
import com.faeysoft.preceptor.lexer.Token;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.UnitSize;
import edu.asu.plp.tool.backend.isa.UnitSize.DefaultSize;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;

public class PLPAssembler extends Assembler
{
	private Lexer lexer;
	
	private BiDirectionalOneToManyMap<String, String> assemblyToDisassemblyMap;
	private HashMap<String, Integer> functionMap;
	private HashMap<String, Integer> opcodeMap;
	
	private List<String> source;
	private List<Token> tokens;
	private ListIterator<Token> tokenIterator;
	private Token currentToken;
	
	public PLPAssembler(List<String> source)
	{
		this.source = source;
		initialize();
	}
	
	@Override
	public ASMImage assemble() throws AssemblerException
	{
		assemblyToDisassemblyMap = null;
		tokens = null;
		
		try
		{
			tokens = lexer.lex(source);
			tokenIterator = tokens.listIterator();
		}
		catch (LexException e)
		{
			e.printStackTrace();
		}
		
		if (tokens == null)
			throw new AssemblerException("File was not lexed correctly.");
		
		preprocess();
		
		return new ASMImage(assemblyToDisassemblyMap);
	}
	
	/*
	 * 1st past on Assembly map. Resolves assembler directives, pseudo-ops, and
	 * populate the symbol table.
	 */
	private void preprocess() throws AssemblerException
	{
		// TODO loop through each file
		nextToken();
		
		while (currentToken != null)
		{
			System.out.println(currentToken);
			// Loop directives
			if (directiveMap.containsKey(currentToken.getValue()))
			{
				
			}
			// Loop PseudoOps
			else if (pseudoOperationMap.containsKey(currentToken.getValue()))
			{
				
			}
			// Instructions
			else if (functionMap.containsKey(currentToken.getValue())
					|| opcodeMap.containsKey(currentToken.getValue()))
			{
				
			}
			// Comments
			else if (currentToken.getTypeName().equals(
					PLPTokenType.COMMENT.name()))
			{
			}
			// Labels
			else if (currentToken.getTypeName().equals(
					PLPTokenType.LABEL_COLON.name()))
			{
				
			}
			
			if (!nextToken())
				break;
		}
		
	}
	
	private boolean nextToken()
	{
		if (!tokenIterator.hasNext())
			return false;
		
		currentToken = tokenIterator.next();
		return true;
	}
	
	private void initialize()
	{
		allowedOpCodeLengths = new int[] { 1 };
		opCodeSize = UnitSize.getSize(DefaultSize.BYTE);
		instructionOpcodeMap = new HashMap<>();
		registerMap = new HashMap<>();
		functionMap = new HashMap<>();
		opcodeMap = new HashMap<>();
		pseudoOperationMap = new HashMap<>();
		directiveMap = new HashMap<>();
		
		setInstructionMapValues();
		setRegisterMapValues();
		setPseudoMapValues();
		
		lexer = new Lexer(PLPTokenType.createSet());
	}
	
	private void setInstructionMapValues()
	{
		instructionOpcodeMap.put("addu", 0);
		instructionOpcodeMap.put("subu", 0);
		instructionOpcodeMap.put("and", 0);
		instructionOpcodeMap.put("or", 0);
		instructionOpcodeMap.put("nor", 0);
		instructionOpcodeMap.put("slt", 0);
		instructionOpcodeMap.put("sltu", 0);
		instructionOpcodeMap.put("sllv", 0);
		instructionOpcodeMap.put("srlv", 0);
		
		instructionOpcodeMap.put("sll", 1);
		instructionOpcodeMap.put("srl", 1);
		
		instructionOpcodeMap.put("jr", 2);
		
		instructionOpcodeMap.put("beq", 3);
		instructionOpcodeMap.put("bne", 3);
		
		instructionOpcodeMap.put("addiu", 4);
		instructionOpcodeMap.put("andi", 4);
		instructionOpcodeMap.put("ori", 4);
		instructionOpcodeMap.put("slti", 4);
		instructionOpcodeMap.put("sltiu", 4);
		
		instructionOpcodeMap.put("lui", 5);
		
		instructionOpcodeMap.put("lw", 6);
		instructionOpcodeMap.put("sw", 6);
		
		instructionOpcodeMap.put("j", 7);
		instructionOpcodeMap.put("jal", 7);
		
		instructionOpcodeMap.put("mulhi", 8);
		instructionOpcodeMap.put("mullo", 8);
		
		instructionOpcodeMap.put("jalr", 9);
		
		instructionOpcodeMap.put("ASM__WORD__", 10);
		instructionOpcodeMap.put("ASM__ORG__", 10);
		instructionOpcodeMap.put("ASM__SKIP__", 10);
		instructionOpcodeMap.put("ASM__LINE__OFFSET__", 10);
		instructionOpcodeMap.put("ASM__POINTER__", 10);
		
		// R-Type Arithmetic
		functionMap.put("sll", 0x00);
		functionMap.put("sllv", 0x01);
		functionMap.put("srl", 0x02);
		functionMap.put("srlv", 0x03);
		functionMap.put("jr", 0x08);
		functionMap.put("jalr", 0x09);
		functionMap.put("mullo", 0x10);
		functionMap.put("mulhi", 0x11);
		functionMap.put("add", 0x20);
		functionMap.put("addu", 0x21);
		// functionMap.put("sub", 0x22);
		functionMap.put("subu", 0x23);
		functionMap.put("and", 0x24);
		functionMap.put("or", 0x25);
		functionMap.put("nor", 0x27);
		functionMap.put("slt", 0x2A);
		functionMap.put("sltu", 0x2B);
		
		opcodeMap.put("j", 0x02);
		opcodeMap.put("jal", 0x03);
		opcodeMap.put("beq", 0x04);
		opcodeMap.put("bne", 0x05);
		opcodeMap.put("addiu", 0x09);
		opcodeMap.put("slti", 0x0A);
		opcodeMap.put("sltiu", 0x0B);
		opcodeMap.put("andi", 0x0C);
		opcodeMap.put("ori", 0x0D);
		opcodeMap.put("lui", 0x0F);
		opcodeMap.put("lw", 0x23);
		opcodeMap.put("sw", 0x2B);
	}
	
	private void setPseudoMapValues()
	{
		pseudoOperationMap.put("nop", 0);
		pseudoOperationMap.put("b", 1);
		pseudoOperationMap.put("move", 3);
		pseudoOperationMap.put("push", 1);
		pseudoOperationMap.put("pop", 1);
		pseudoOperationMap.put("li", 3);
		pseudoOperationMap.put("call", 1);
		pseudoOperationMap.put("return", 0);
		pseudoOperationMap.put("save", 0);
		pseudoOperationMap.put("restore", 0);
		pseudoOperationMap.put("lwm", 3);
		pseudoOperationMap.put("swm", 3);
		
		// TODO set directivees
		directiveMap.put(".org", 0);
		directiveMap.put(".word", 0);
		directiveMap.put(".space", 0);
		directiveMap.put(".ascii", 0);
		directiveMap.put(".asciiz", 0);
		directiveMap.put(".include", 0);
		directiveMap.put(".text", 0);
		directiveMap.put(".data", 0);
	}
	
	private void setRegisterMapValues()
	{
		String[] registers = { "$zero", "$at", "$v0", "$v1", "$a0", "$a1",
				"$a2", "$a3", "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6",
				"$t7", "$t8", "$t9", "$s0", "$s1", "$s2", "$s3", "$s4", "$s5",
				"$s6", "$s7", "$i0", "$i1", "$iv", "$sp", "$ir", "$ra" };
		
		for (int index = 0; index < registers.length; index++)
		{
			registerMap.put("$" + index, (byte) index);
			registerMap.put(registers[index], (byte) index);
		}
	}
}
