package edu.asu.plp.tool.backend.plpisa.assembler;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.faeysoft.preceptor.lexer.LexException;
import com.faeysoft.preceptor.lexer.Lexer;
import com.faeysoft.preceptor.lexer.Token;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.isa.ASMDisassembly;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.ASMLine;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.UnitSize;
import edu.asu.plp.tool.backend.isa.UnitSize.DefaultSize;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.backend.plpisa.PLPAsm;

public class PLPAssembler extends Assembler
{
	private BiDirectionalOneToManyMap<ASMLine, ASMDisassembly> assemblyToDisassemblyMap;
	private HashMap<String, Integer> functionMap;
	private HashMap<String, Integer> opcodeMap;
	private List<PLPAsm> asmFiles;
	
	private long currentAddress;
	private long currentTextAddress;
	private long currentDataAddress;
	private long bytesSpace;
	
	private int directiveOffset;
	private int currentRegion;
	
	private String currentActiveFile;
	private String topLeveLFile;
	
	
	private Lexer lexer;
	//Map position in asmFile list to that files tokens
	private HashMap<Integer, List<Token>> tokens;
	private ListIterator<Token> tokenIterator;
	private Token currentToken;
	
	public PLPAssembler(String asmFilePath) throws IOException
	{
		this(Arrays.asList(new PLPAsm[] {new PLPAsm(asmFilePath)}));		
	}
	
	public PLPAssembler(List<PLPAsm> asmFiles)
	{
		this.asmFiles = asmFiles;
		initialize();
	}
	
	@Override
	public ASMImage assemble() throws AssemblerException
	{
		assemblyToDisassemblyMap = null;
		tokens = new HashMap<>();
		
		for(PLPAsm asmFile : asmFiles)
		{
			try
			{
				tokens.put(asmFiles.indexOf(asmFile), lexer.lex(asmFile.getAsmLines()));
			}
			catch (LexException e)
			{
				e.printStackTrace();
			}
		}
		
		if(asmFiles.isEmpty())
			throw new AssemblerException("Can not assemble an image with no files.");
			
		tokenIterator = tokens.get(0).listIterator();
		
		if (tokens == null)
			throw new AssemblerException("File was not lexed correctly.");
			
		preprocess();
		
		return new ASMImage(assemblyToDisassemblyMap);
	}
	
	/*
	 * 1st past on Assembly map. Resolves assembler directives, pseudo-ops, and populate
	 * the symbol table.
	 */
	private void preprocess() throws AssemblerException
	{
		int lineNumber = 0;
		// TODO loop through each file
		nextToken();
		
		while (currentToken != null)
		{
			// Loop directives
			if (currentToken.getTypeName() == PLPTokenType.DIRECTIVE.name())
			{
				if (directiveMap.containsKey(currentToken.getValue()))
				{
					preprocessDirective();
				}
				else
				{
					throw new AssemblerException(
							"Unknown directive. Found: " + currentToken.getValue());
				}
			}
			// Loop PseudoOps
			else if (pseudoOperationMap.containsKey(currentToken.getValue()))
			{
				preprocessPseudoOperation();
			}
			// Instructions
			else if (functionMap.containsKey(currentToken.getValue())
					|| opcodeMap.containsKey(currentToken.getValue()))
			{
			
			}
			// Comments
			else if (currentToken.getTypeName().equals(PLPTokenType.COMMENT.name()))
			{
			}
			// Labels
			else if (currentToken.getTypeName().equals(PLPTokenType.LABEL_COLON.name()))
			{
			}
			
			if (!nextToken())
				break;
		}
		
	}
	
	private void preprocessPseudoOperation()
	{
		// TODO Auto-generated method stub
		
	}
	
	private void preprocessDirective() throws AssemblerException
	{
		int expectedDirectiveSize = directiveMap.get(currentToken.getValue());
		Token directiveToken = currentToken;
		Token[] nextTokens = new Token[expectedDirectiveSize];
		
		for (int index = 0; index < expectedDirectiveSize; index++)
		{
			if (!nextToken())
				throw new AssemblerException("Insufficient parameter count");
				
			nextTokens[index] = currentToken;
		}
		
		if (directiveToken.getValue().equals(".include"))
		{
			throw new UnsupportedOperationException("Not implemented yet: .include");
		}
		else if (directiveToken.getValue().equals(".org"))
		{
			if (!nextTokens[0].getTypeName().equals(PLPTokenType.NUMERIC.name()))
			{
				throw new AssemblerException(
						"Expected an address, found: " + nextTokens[0].getValue());
			}
			throw new UnsupportedOperationException("Not implemented yet: .org");
		}
		else if (directiveToken.getValue().equals(".text"))
		{
			throw new UnsupportedOperationException("Not implemented yet: .text");
		}
		else if (directiveToken.getValue().equals(".data"))
		{
			throw new UnsupportedOperationException("Not implemented yet: .data");
		}
		else if (directiveToken.getValue().equals(".word"))
		{
			if (!nextTokens[0].getTypeName().equals(PLPTokenType.NUMERIC.name()))
			{
				throw new AssemblerException(
						"Expected number of words to allocate, found: "
								+ nextTokens[0].getValue());
			}
			throw new UnsupportedOperationException("Not implemented yet: .word");
		}
		else if (directiveToken.getValue().equals(".space"))
		{
			if (!nextTokens[0].getTypeName().equals(PLPTokenType.NUMERIC.name()))
			{
				throw new AssemblerException(
						"Expected a number, found: " + nextTokens[0].getValue());
			}
			throw new UnsupportedOperationException("Not implemented yet: .space");
		}
		else if (directiveToken.getValue().equals(".ascii"))
		{
			if (!nextTokens[0].getTypeName().equals(PLPTokenType.STRING.name()))
			{
				throw new AssemblerException(
						"Expected a string to store, found: " + nextTokens[0].getValue());
			}
			throw new UnsupportedOperationException("Not implemented yet: .ascii");
		}
		else if (directiveToken.getValue().equals(".asciiz"))
		{
			if (!nextTokens[0].getTypeName().equals(PLPTokenType.STRING.name()))
			{
				throw new AssemblerException(
						"Expected a string to store, found: " + nextTokens[0].getValue());
			}
			throw new UnsupportedOperationException("Not implemented yet: .asciiz");
		}
		else if (directiveToken.getValue().equals(".asciiw"))
		{
			if (!nextTokens[0].getTypeName().equals(PLPTokenType.STRING.name()))
			{
				throw new AssemblerException(
						"Expected a string to store, found: " + nextTokens[0].getValue());
			}
			throw new UnsupportedOperationException("Not implemented yet: .asciiw");
		}
		else if (directiveToken.getValue().equals(".equ"))
		{
			throw new UnsupportedOperationException("Not implemented yet: .equ");
		}
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
		directiveMap.put(".org", 1);
		directiveMap.put(".word", 1);
		directiveMap.put(".space", 1);
		directiveMap.put(".ascii", 1);
		directiveMap.put(".asciiz", 1);
		directiveMap.put(".asciiw", 1);
		directiveMap.put(".include", 1);
		directiveMap.put(".text", 1);
		directiveMap.put(".data", 1);
		directiveMap.put(".equ", 2);
	}
	
	private void setRegisterMapValues()
	{
		String[] registers = { "$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
				"$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7", "$t8", "$t9",
				"$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7", "$i0", "$i1",
				"$iv", "$sp", "$ir", "$ra" };
				
		for (int index = 0; index < registers.length; index++)
		{
			registerMap.put("$" + index, (byte) index);
			registerMap.put(registers[index], (byte) index);
		}
	}
	
	private boolean nextToken()
	{
		return nextToken(1);
	}
	
	private boolean nextToken(int count)
	{
		for (int index = 0; index < count; index++)
		{
			if (!tokenIterator.hasNext())
				return false;
				
			currentToken = tokenIterator.next();
		}
		
		return true;
	}
	
	private boolean previousToken()
	{
		return previousToken(1);
	}
	
	private boolean previousToken(int count)
	{
		for (int index = 0; index < count; index++)
		{
			if (!tokenIterator.hasPrevious())
				return false;
				
			currentToken = tokenIterator.previous();
		}
		
		return true;
	}
}
