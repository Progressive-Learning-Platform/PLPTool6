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
import edu.asu.plp.tool.backend.isa.exceptions.AssemblyException;
import edu.asu.plp.tool.backend.plpisa.PLPAsm;
import edu.asu.plp.tool.backend.util.ISAUtil;
import moore.util.Subroutine;

public class PLPAssembler extends Assembler
{
	private BiDirectionalOneToManyMap<ASMLine, ASMDisassembly> assemblyToDisassemblyMap;
	private HashMap<String, Integer> functionMap;
	private HashMap<String, Integer> opcodeMap;
	private HashMap<String, AssemblerStep> directiveMap;
	private List<PLPAsm> asmFiles;
	private List<Integer> regionMap;
	
	private long currentAddress;
	private long currentTextAddress;
	private long currentDataAddress;
	private long bytesSpace;
	private long entryPoint;
	
	private int directiveOffset;
	private int currentRegion;
	private int lineNumber;
	
	private String currentActiveFile;
	private String topLeveLFile;
	
	private static final String ASM__WORD__ = "ASM__WORD__";
	private static final String ASM__ORG__ = "ASM__ORG__";
	private static final String ASM__SKIP__ = "ASM__SKIP__";
	private static final String ASM__LINE__OFFSET__ = "ASM__LINE__OFFSET__";
	private static final String ASM__POINTER__ = "ASM__POINTER__";
	
	private Lexer lexer;
	// Map position in asmFile list to that files tokens
	private HashMap<Integer, List<Token>> tokens;
	private ListIterator<Token> tokenIterator;
	private Token currentToken;
	
	public PLPAssembler(String asmFilePath) throws IOException
	{
		this(Arrays.asList(new PLPAsm[] { new PLPAsm(asmFilePath) }));
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
		
		for (PLPAsm asmFile : asmFiles)
		{
			try
			{
				System.out.println("Start " + asmFile.getAsmFilePath() + " lexing.");
				tokens.put(asmFiles.indexOf(asmFile), lexer.lex(asmFile.getAsmLines()));
			}
			catch (LexException e)
			{
				e.printStackTrace();
			}
		}
		
		if (asmFiles.isEmpty())
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
					directiveMap.get(currentToken.getValue()).perform();
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
		
		currentAddress = 0;
		currentTextAddress = -1;
		currentDataAddress = -1;
		entryPoint = -1;
		directiveOffset = 0;
		bytesSpace = 0;
		topLeveLFile = asmFiles.get(0).getAsmFilePath();
		
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
		
		instructionOpcodeMap.put(ASM__WORD__, 10);
		instructionOpcodeMap.put(ASM__ORG__, 10);
		instructionOpcodeMap.put(ASM__SKIP__, 10);
		instructionOpcodeMap.put(ASM__LINE__OFFSET__, 10);
		instructionOpcodeMap.put(ASM__POINTER__, 10);
		
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
		
		directiveMap.put(".org", this::orgDirective);
		directiveMap.put(".word", this::wordDirective);
		directiveMap.put(".space", this::spaceDirective);
		directiveMap.put(".ascii", this::asciiDirective);
		directiveMap.put(".asciiz", this::asciiDirective);
		directiveMap.put(".asciiw", this::asciiDirective);
		directiveMap.put(".include", this::includeDirective);
		directiveMap.put(".text", this::textDirective);
		directiveMap.put(".data", this::dataDirective);
		directiveMap.put(".equ", this::equDirective);
		
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
	
	private void orgDirective() throws AssemblerException
	{
		expectedNextToken(".org directive");
		
		if (!currentToken.getTypeName().equals(PLPTokenType.NUMERIC.name()))
		{
			throw new AssemblerException(
					"(.org) Expected an address, found: " + currentToken.getValue());
		}
		
		appendPreprocessedInstruction(ASM__ORG__ + currentToken.getValue(), lineNumber,
				true);
		directiveOffset++;
		try
		{
			currentAddress = ISAUtil.sanitize32bits(currentToken.getValue());
		}
		catch (AssemblyException e)
		{
			e.printStackTrace();
		}
		
		entryPoint = (entryPoint < 0) ? currentAddress : entryPoint;
	}
	
	private void wordDirective() throws AssemblerException
	{
		expectedNextToken(".word directive");
		
		if (!currentToken.getTypeName().equals(PLPTokenType.NUMERIC.name()))
		{
			throw new AssemblerException(
					"(.word) Expected number to initialize current memory address to, found: "
							+ currentToken.getValue());
		}
		
		appendPreprocessedInstruction(ASM__WORD__ + currentToken.getValue(), lineNumber,
				true);
		regionMap.add(currentRegion);
		currentAddress += 4;
	}
	
	private void spaceDirective() throws AssemblerException
	{
		expectedNextToken(".space directive");
		
		if (!currentToken.getTypeName().equals(PLPTokenType.NUMERIC.name()))
		{
			throw new AssemblerException(
					"(.space) Expected a number, found: " + currentToken.getValue());
		}
		
		try
		{
			long size = ISAUtil.sanitize32bits(currentToken.getValue());
			currentAddress += 4 * size;
			bytesSpace += 4 * size;
			
			appendPreprocessedInstruction(ASM__ORG__ + currentAddress, lineNumber, true);
			directiveOffset++;
			
			regionMap.add(currentRegion);
		}
		catch (AssemblyException e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void asciiDirective() throws AssemblerException
	{
		Token directiveToken = currentToken;
		boolean wordAligned = directiveToken.getValue().equals(".asciiw");
		
		expectedNextToken(currentToken.getValue() + " directive");
		
		if (!currentToken.getTypeName().equals(PLPTokenType.STRING.name()))
		{
			throw new AssemblerException("(" + directiveToken.getValue() + ") Expected a string to store, found: "
					+ currentToken.getValue());
		}
		
		
		
		throw new UnsupportedOperationException("Ascii Directive is not implemented");
	}
	
	private void includeDirective() throws AssemblerException
	{
		expectedNextToken("include directive");
		
		throw new UnsupportedOperationException("Include Directive is not implemented");
	}
	
	private void textDirective() throws AssemblerException
	{
		expectedNextToken(".text directive");
		
		if (currentRegion != 1)
		{
			if (!currentToken.getTypeName().equals(PLPTokenType.STRING.name()))
			{
				throw new AssemblerException(
						"(.text) Expected a string, found: " + currentToken.getValue());
			}
			
			directiveOffset++;
			
			if (currentRegion == 2)
				currentDataAddress = currentAddress;
				
			currentRegion = 1;
			currentAddress = currentTextAddress;
			
			// TODO ASM .text has a line that should never be reached. Look into it.
			appendPreprocessedInstruction(ASM__ORG__ + currentToken.getValue(), lineNumber, true);
			try
			{
				currentAddress = ISAUtil.sanitize32bits(currentToken.getValue());
			}
			catch (AssemblyException e)
			{
				e.printStackTrace();
			}
			entryPoint = currentAddress;
			currentTextAddress = entryPoint;
			
			if(currentAddress < 0)
				throw new AssemblerException("Starting address for .text is not defined.");
		}
	}
	
	private void dataDirective() throws AssemblerException
	{
		expectedNextToken(".data directive");
		
		if (!currentToken.getTypeName().equals(PLPTokenType.STRING.name()))
		{
			throw new AssemblerException(
					"(.data) Expected a string, found: " + currentToken.getValue());
		}
		
		if(currentRegion != 2)
		{
			directiveOffset++;
			if(currentRegion == 1)
				currentTextAddress = currentAddress;
			
			currentRegion = 2;
			currentAddress = currentDataAddress;
			
			// TODO Asm .data has a line that should never be reached. Look into it.
			appendPreprocessedInstruction(ASM__ORG__ + currentToken.getValue(), lineNumber, true);
			try
			{
				currentAddress = ISAUtil.sanitize32bits(currentToken.getValue());
			}
			catch (AssemblyException e)
			{
				e.printStackTrace();
			}
			currentDataAddress = currentAddress;
			
			if(currentAddress < 0)
				throw new AssemblerException("Starting address for .data is not defined.");
		}
	}
	
	private void equDirective() throws AssemblerException
	{
		expectedNextToken(".equ directive");
		
		throw new UnsupportedOperationException("equ Directive is not implemented");
	}
	
	private void appendPreprocessedInstruction(String instruction, int lineNumber,
			boolean newLine)
	{
	
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
	
	private void expectedNextToken(String location) throws AssemblerException
	{
		if (!nextToken())
			throw new AssemblerException(
					"Unexpected end of token stream. In " + location);
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
