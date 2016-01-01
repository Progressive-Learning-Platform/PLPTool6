package edu.asu.plp.tool.backend.plpisa.assembler;

import java.io.IOException;
import java.util.ArrayList;
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

public class PLPAssembler extends Assembler
{
	private List<PLPAsm> asmFiles;
	private List<Integer> regionMap;
	
	private BiDirectionalOneToManyMap<ASMLine, ASMDisassembly> assemblyToDisassemblyMap;
	
	private HashMap<String, Integer> functionMap;
	private HashMap<String, Integer> opcodeMap;
	private HashMap<String, AssemblerStep> directiveMap;
	protected HashMap<String, AssemblerStep> pseudoOperationMap;
	
	private HashMap<String, Long> symbolTable;
	
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
		if (!nextToken())
			return;
			
		while (currentToken != null)
		{
			// Loop directives
			if (currentToken.getTypeName() == PLPTokenType.DIRECTIVE.name())
			{
				if (directiveMap.containsKey(currentToken.getValue()))
					directiveMap.get(currentToken.getValue()).perform();
				else
					throw new AssemblerException(
							"Unknown directive. Found: " + currentToken.getValue());
			}
			// Loop PseudoOps
			else if (pseudoOperationMap.containsKey(currentToken.getValue()))
			{
				pseudoOperationMap.get(currentToken.getValue()).perform();
			}
			// Instructions
			else if (functionMap.containsKey(currentToken.getValue())
					|| opcodeMap.containsKey(currentToken.getValue()))
			{
				preprocessNormalInstruction();
			}
			// Comments
			else if (currentToken.getTypeName().equals(PLPTokenType.COMMENT.name()))
			{
				appendPreprocessedInstruction(ASM__SKIP__, lineNumber, true);
				directiveOffset++;
			}
			// Labels
			else if (currentToken.getTypeName().equals(PLPTokenType.LABEL_COLON.name()))
			{
				preprocessLabels();
			}
			
			if (!nextToken())
				break;
		}
		
	}
	
	/*
	 * 
	 * ======================= Pseudo Operations =========================
	 * 
	 */
	
	/**
	 * No-operation. Can be used for branch delay slots
	 * 
	 * nop
	 * 
	 * equivalent to: sll $0, $0, 0
	 * 
	 * @throws AssemblerException
	 */
	private void nopOperation() throws AssemblerException
	{
		appendPreprocessedInstruction("sll $0, $0, 0", lineNumber, true);
		addRegionAndIncrementAddress();
	}
	
	/**
	 * Branch always to label
	 * 
	 * b label
	 * 
	 * equivalent to: beq $0, $0, label
	 * 
	 * @throws AssemblerException
	 */
	private void branchOperation() throws AssemblerException
	{
		expectedNextToken("pseudo move operation");
		
		ensureTokenEquality(PLPTokenType.LABEL_PLAIN,
				"(b) Expected a label to branch to, found: ");
				
		appendPreprocessedInstruction("beq $0, $0, " + currentToken.getValue(),
				lineNumber, true);
				
		addRegionAndIncrementAddress();
	}
	
	/**
	 * Copy Register. Copy $rs to $rd
	 * 
	 * move $rd, $rs
	 * 
	 * equivalent to: add $rd, $0, $rs
	 * 
	 * @throws AssemblerException
	 */
	private void moveOperation() throws AssemblerException
	{
		expectedNextToken("pseudo move operation");
		
		String destinationRegister = currentToken.getValue();
		ensureTokenEquality(PLPTokenType.ADDRESS, "(move) Expected a register, found: ");
		
		expectedNextToken("pseudo move operation");
		
		String startingRegister = currentToken.getValue();
		ensureTokenEquality(PLPTokenType.ADDRESS, "(move) Expected a register, found: ");
		
		// TODO (Look into) Google Code PLP says it's equivalent instruction is Add, src
		// code uses or
		appendPreprocessedInstruction(
				"or " + destinationRegister + ", $0," + startingRegister, lineNumber,
				true);
		addRegionAndIncrementAddress();
		
	}
	
	/**
	 * Push register onto stack-- we modify the stack pointer first so if the CPU is
	 * interrupted between the two instructions, the data written wont get clobbered
	 * 
	 * Push $rt into the stack
	 * 
	 * push $rt
	 * 
	 * equivalent to: addiu $sp, $sp, -4; sw $rt, 0($sp)
	 * 
	 * @throws AssemblerException
	 */
	private void pushOperation() throws AssemblerException
	{
		expectedNextToken("push pseudo operation");
		
		ensureTokenEquality(PLPTokenType.ADDRESS, "(push) Expected a register, found: ");
		
		appendPreprocessedInstruction("addiu $sp, $sp, -4", lineNumber, true);
		appendPreprocessedInstruction("sw " + currentToken.getValue() + ", 4($sp)",
				lineNumber, true);
				
		addRegionAndIncrementAddress(2, 8);
	}
	
	/**
	 * Pop data from stack onto a register-- in the pop case, we want to load first so if
	 * the CPU is interrupted we have the data copied already
	 * 
	 * Pop data from the top of the stack to $rt
	 * 
	 * pop $rt
	 * 
	 * equivalent to: lw $rt, 0($sp); addiu $sp, $sp, 4
	 * 
	 * @throws AssemblerException
	 */
	private void popOperation() throws AssemblerException
	{
		expectedNextToken("pop pseudo operation");
		
		ensureTokenEquality(PLPTokenType.ADDRESS, "(push) Expected a register, found: ");
		
		appendPreprocessedInstruction("lw " + currentToken.getValue() + ", 4($sp)",
				lineNumber, true);
		appendPreprocessedInstruction("addiu $sp, $sp, 4", lineNumber, true);
		
		addRegionAndIncrementAddress(2, 8);
	}
	
	/**
	 * Load Immediate
	 * 
	 * Load a 32-bit number to $rd Load the address of a label to a register to be used as
	 * a pointer.
	 * 
	 * li $rd, imm li $rd, label
	 * 
	 * equivalent to: lui $rd, (imm & 0xff00) >> 16; ori $rd, imm & 0x00ff equivalent to:
	 * lui $rd, (imm & 0xff00) >> 16; ori $rd, imm & 0x00ff
	 * 
	 * @throws AssemblerException
	 */
	private void liOperation() throws AssemblerException
	{
		expectedNextToken("load immediate pseudo operation");
		String firstValue = currentToken.getValue();
		ensureTokenEquality(PLPTokenType.ADDRESS, "(li) Expected a register, found: ");
		
		expectedNextToken("load immediate pseudo operation");
		String secondValue = currentToken.getValue();
		// TODO ensure second value type
		
		appendPreprocessedInstruction("lui " + firstValue + ", $_hi: " + secondValue,
				lineNumber, true);
		appendPreprocessedInstruction(
				"ori " + firstValue + ", " + firstValue + ", $_lo: " + secondValue,
				lineNumber, true);
				
		addRegionAndIncrementAddress(2, 8);
	}
	
	/**
	 * Store the value in $rt to a memory location
	 * 
	 * lwm $rt, imm32/label
	 * 
	 * @throws AssemblerException
	 */
	private void lvmOperation() throws AssemblerException
	{
		expectedNextToken("lvm psuedo operation");
		String register = currentToken.getValue();
		ensureTokenEquality(PLPTokenType.ADDRESS, "(lvm) Expected a register, found: ");
		
		expectedNextToken("lvm psuedo operation");
		String immediateOrLabel = currentToken.getValue();
		//TODO ensure token validity
		
		appendPreprocessedInstruction("lui $at, $_hi: " + immediateOrLabel, lineNumber, true);
		appendPreprocessedInstruction("ori $at, $at, $_lo: " + immediateOrLabel, lineNumber, true);
		appendPreprocessedInstruction("lw " + register + ", 0($at)", lineNumber, true);
		
		addRegionAndIncrementAddress(3, 12);
	}
	
	/**
	 * Store to memory
	 * 
	 * swm $rt, imm32/label
	 * 
	 * @throws AssemblerException
	 */
	private void svmOperation() throws AssemblerException
	{
		expectedNextToken("svm psuedo operation");
		String register = currentToken.getValue();
		ensureTokenEquality(PLPTokenType.ADDRESS, "(svm) Expected a register, found: ");
		
		expectedNextToken("svm psuedo operation");
		String immediateOrLabel = currentToken.getValue();
		//TODO ensure token validity
		
		appendPreprocessedInstruction("lui $at, $_hi: " + immediateOrLabel, lineNumber, true);
		appendPreprocessedInstruction("ori $at, $at, $_lo: " + immediateOrLabel, lineNumber, true);
		appendPreprocessedInstruction("sw " + register + ", 0($at)", lineNumber, true);
		
		addRegionAndIncrementAddress(3, 12);
	}
	
	/**
	 * Save registers and call a function
	 * 
	 * Save $aX, $tX, $sX, and $ra to stack and call function
	 * 
	 * call label
	 * 
	 * @throws AssemblerException
	 */
	private void callOperation() throws AssemblerException
	{
	
	}
	
	/**
	 * Restore registers and return from callee. NOT INTERRUPT SAFE
	 * 
	 * Restore $aX, $tX, $sX, and $ra from stack and return
	 * 
	 * return
	 * 
	 * @throws AssemblerException
	 */
	private void returnOperation() throws AssemblerException
	{
	
	}
	
	/**
	 * Save all registers except for $zero to stack
	 * 
	 * save
	 * 
	 * @throws AssemblerException
	 */
	private void saveOperation() throws AssemblerException
	{
	
	}
	
	/**
	 * Restore all none-zero registers from the stack
	 * 
	 * Restore all registers saved by 'save' in reverse order
	 * 
	 * restore
	 * 
	 * @throws AssemblerException
	 */
	private void restoreOperation() throws AssemblerException
	{
	
	}
	
	/*
	 * 
	 * ======================= Preprocess Operations =========================
	 * 
	 */
	
	private void preprocessNormalInstruction() throws AssemblerException
	{
		// TODO Auto-generated method stub
		
	}
	
	private void preprocessLabels() throws AssemblerException
	{
		Token directiveToken = currentToken;
		String labelValue = directiveToken.getValue();
		// Remove colon from label
		labelValue = labelValue.substring(0, labelValue.length() - 1);
		
		if (symbolTable.containsKey(labelValue))
		{
			throw new AssemblerException("(" + directiveToken.getTypeName()
					+ ") preprocessing label failure. Symbol already defined, found: "
					+ directiveToken.getValue());
		}
		else
		{
			symbolTable.put(labelValue, new Long((int) currentAddress));
			appendPreprocessedInstruction(ASM__SKIP__, lineNumber, true);
			directiveOffset++;
		}
		
	}
	
	/*
	 * 
	 * ======================= Preprocess Directives =========================
	 * 
	 */
	
	private void orgDirective() throws AssemblerException
	{
		expectedNextToken(".org directive");
		
		ensureTokenEquality(PLPTokenType.NUMERIC, "(.org) Expected an address, found: ");
		
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
		
		ensureTokenEquality(PLPTokenType.NUMERIC,
				"(.word) Expected number to initialize current memory address to, found: ");
				
		appendPreprocessedInstruction(ASM__WORD__ + currentToken.getValue(), lineNumber,
				true);
		addRegionAndIncrementAddress();
	}
	
	private void spaceDirective() throws AssemblerException
	{
		expectedNextToken(".space directive");
		
		ensureTokenEquality(PLPTokenType.NUMERIC, "(.space) Expected a number, found: ");
		
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
		
		ensureTokenEquality(PLPTokenType.STRING, "(" + directiveToken.getValue()
				+ ") Expected a string to store, found: ");
				
		// Strip quotes
		String currentValue = null;
		if (currentToken.getValue().charAt(0) == '\"')
			currentValue = currentToken.getValue().substring(1,
					currentToken.getValue().length() - 1);
					
		// Check for escaped characters
		// Only loop through indices that contain \\
		StringBuffer stringBuffer = new StringBuffer(currentValue);
		List<Character> specialEscapedCharacters = Arrays.asList('n', 'r', 't', '0');
		
		for (int index = -1; (index = currentValue.indexOf("\\", index + 1)) != -1;)
		{
			if (index != currentValue.length() - 1)
			{
				if (specialEscapedCharacters.contains(currentValue.charAt(index + 1)))
				{
					stringBuffer = stringBuffer.replace(index, index + 2,
							"\\" + specialEscapedCharacters
									.indexOf(currentValue.charAt(index + 1)));
				}
				else if (currentValue.charAt(index + 1) == '\\')
				{
					stringBuffer = stringBuffer.replace(index, index + 2, "\\");
				}
				else
				{
					System.out.println("(" + directiveToken.getValue()
							+ ") Preprocessing could not identify escaped character, found: \\"
							+ currentValue.charAt(index + 1) + ".\n\tIn "
							+ currentToken.getValue() + "\n");
				}
			}
		}
		
		currentValue = stringBuffer.toString();
		
		// if directive is asciiz, we need to append a null character
		if (directiveToken.getValue().equals(".asciiz"))
			currentValue += '\0';
			
		// if string is not word-aligned, pad with zeroes
		if (currentValue.length() % 4 != 0 && !wordAligned)
		{
			int neededPadding = 4 - (currentValue.length() % 4);
			for (int index = 0; index < neededPadding; index++)
			{
				currentValue += '\0';
			}
		}
		
		// add ASM__WORD__ 2nd pass directives and were done
		for (int index = 0; index < currentValue.length(); index++)
		{
			if (index % (wordAligned ? 1 : 4) == 0)
				appendPreprocessedInstruction(ASM__WORD__ + " 0x", lineNumber, false);
				
			if (!wordAligned)
				appendPreprocessedInstruction(
						String.format("%02x", (int) currentValue.charAt(index)),
						lineNumber, false);
			else
			{
				appendPreprocessedInstruction(
						String.format("%08x", (int) currentValue.charAt(index)),
						lineNumber, true);
				addRegionAndIncrementAddress();
			}
			
			if (!wordAligned && (index + 1) % 4 == 0 && index > 0)
			{
				addRegionAndIncrementAddress();
				appendPreprocessedInstruction("", lineNumber, true);
			}
		}
	}
	
	private void textDirective() throws AssemblerException
	{
		expectedNextToken(".text directive");
		
		if (currentRegion != 1)
		{
			ensureTokenEquality(PLPTokenType.STRING,
					"(.text) Expected a string, found: ");
					
			directiveOffset++;
			
			if (currentRegion == 2)
				currentDataAddress = currentAddress;
				
			currentRegion = 1;
			currentAddress = currentTextAddress;
			
			// TODO ASM .text has a line that should never be reached. Look into it.
			appendPreprocessedInstruction(ASM__ORG__ + currentToken.getValue(),
					lineNumber, true);
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
			
			if (currentAddress < 0)
				throw new AssemblerException(
						"Starting address for .text is not defined.");
		}
	}
	
	private void dataDirective() throws AssemblerException
	{
		expectedNextToken(".data directive");
		
		ensureTokenEquality(PLPTokenType.STRING, "(.data) Expected a string, found: ");
		
		if (currentRegion != 2)
		{
			directiveOffset++;
			if (currentRegion == 1)
				currentTextAddress = currentAddress;
				
			currentRegion = 2;
			currentAddress = currentDataAddress;
			
			// TODO Asm .data has a line that should never be reached. Look into it.
			appendPreprocessedInstruction(ASM__ORG__ + currentToken.getValue(),
					lineNumber, true);
			try
			{
				currentAddress = ISAUtil.sanitize32bits(currentToken.getValue());
			}
			catch (AssemblyException e)
			{
				e.printStackTrace();
			}
			currentDataAddress = currentAddress;
			
			if (currentAddress < 0)
				throw new AssemblerException(
						"Starting address for .data is not defined.");
		}
	}
	
	private void equDirective() throws AssemblerException
	{
		expectedNextToken(".equ directive");
		
		ensureTokenEquality(PLPTokenType.STRING, "(.equ) Expected a string, found: ");
		
		String symbol = currentToken.getValue();
		if (symbolTable.containsKey(symbol))
		{
			throw new AssemblerException(
					"(.equ) Symbol table already contains: " + currentToken.getValue());
		}
		
		expectedNextToken(".equ directive");
		
		ensureTokenEquality(PLPTokenType.NUMERIC,
				"(.equ) Expected an address after symbol, found: ");
				
		long value = Long.MIN_VALUE;
		try
		{
			value = ISAUtil.sanitize32bits(currentToken.getValue());
		}
		catch (AssemblyException e)
		{
			e.printStackTrace();
		}
		
		if (value < 0)
		{
			throw new AssemblerException(
					"(.equ) Could not process address after symbol, found: "
							+ currentToken.getValue());
		}
		
		symbolTable.put(symbol, value);
		
	}
	
	private void includeDirective() throws AssemblerException
	{
		expectedNextToken("include directive");
		
		appendPreprocessedInstruction(ASM__SKIP__, lineNumber, true);
		boolean found = false;
		boolean conflict = false;
		
		throw new UnsupportedOperationException("Include Directive is not implemented");
	}
	
	/*
	 * 
	 * ======================= Initialization =========================
	 * 
	 */
	
	private void initialize()
	{
		allowedOpCodeLengths = new int[] { 1 };
		opCodeSize = UnitSize.getSize(DefaultSize.BYTE);
		regionMap = new ArrayList<>();
		symbolTable = new HashMap<>();
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
		pseudoOperationMap.put("nop", this::nopOperation);
		pseudoOperationMap.put("b", this::branchOperation);
		pseudoOperationMap.put("move", this::moveOperation);
		pseudoOperationMap.put("push", this::pushOperation);
		pseudoOperationMap.put("pop", this::popOperation);
		pseudoOperationMap.put("li", this::liOperation);
		pseudoOperationMap.put("call", this::callOperation);
		pseudoOperationMap.put("return", this::returnOperation);
		pseudoOperationMap.put("save", this::saveOperation);
		pseudoOperationMap.put("restore", this::restoreOperation);
		pseudoOperationMap.put("lwm", this::lvmOperation);
		pseudoOperationMap.put("swm", this::svmOperation);
		
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
	
	/*
	 * 
	 * ======================= Helper Functions =========================
	 * 
	 */
	
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
	
	private void addRegionAndIncrementAddress()
	{
		addRegionAndIncrementAddress(1, 4);
	}
	
	private void addRegionAndIncrementAddress(int timesToAddCurrentRegion,
			int currentAddressIncrementSize)
	{
		for(int index = 0; index < timesToAddCurrentRegion; index++)
		{
			regionMap.add(currentRegion);
		}
		currentAddress += currentAddressIncrementSize;
	}
	
	private void ensureTokenEquality(PLPTokenType compareTo,
			String assemblerExceptionMessage) throws AssemblerException
	{
		if (!currentToken.getTypeName().equals(compareTo.name()))
		{
			throw new AssemblerException(
					assemblerExceptionMessage + currentToken.getValue());
		}
	}
}
