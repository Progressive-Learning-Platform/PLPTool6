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
import javafx.util.Pair;

public class PLPAssembler extends Assembler
{
	private List<PLPAsm> asmFiles;
	private List<Integer> regionMap;
	
	private BiDirectionalOneToManyMap<ASMLine, ASMDisassembly> assemblyToDisassemblyMap;
	
	private HashMap<String, Pair<AssemblerStep, Integer>> instructionMap;
	private HashMap<String, AssemblerStep> directiveMap;
	private HashMap<String, AssemblerStep> pseudoOperationMap;
	
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
		
		if (asmFiles.isEmpty())
			throw new AssemblerException("Can not assemble an image with no files.");
			
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
		
		if (tokens == null)
			throw new AssemblerException("File was not lexed correctly.");
			
		preprocess();
		
		return assembleImage();
	}
	
	private ASMImage assembleImage()
	{
		long assemblerPCAddress = 0;
		int assemblerDirectiveSkips = 0;
		currentRegion = 0;
		
		return new ASMImage(assemblyToDisassemblyMap);
	}
	
	/*
	 * 1st past on Assembly map. Resolves assembler directives, pseudo-ops, and populate
	 * the symbol table.
	 */
	private void preprocess() throws AssemblerException
	{
		int lineNumber = 0;
		tokenIterator = tokens.get(0).listIterator();
		
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
			else if (instructionMap.containsKey(currentToken.getValue()))
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
			else
			{
				throw new AssemblerException("Unknown token in preprocessing, found: "
						+ currentToken.getValue());
			}
			
			if (!nextToken())
				break;
		}
		
		// TODO append the rest of the files in source files
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
		
		ensureTokenEquality("(b) Expected a label to branch to, found: ",
				PLPTokenType.LABEL_PLAIN);
				
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
		ensureTokenEquality("(move) Expected a register, found: ", PLPTokenType.ADDRESS);
		
		expectedNextToken("pseudo move operation");
		
		String startingRegister = currentToken.getValue();
		ensureTokenEquality("(move) Expected a register, found: ", PLPTokenType.ADDRESS);
		
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
		
		ensureTokenEquality("(push) Expected a register, found: ", PLPTokenType.ADDRESS);
		
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
		
		ensureTokenEquality("(push) Expected a register, found: ", PLPTokenType.ADDRESS);
		
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
		String targetRegister = currentToken.getValue();
		ensureTokenEquality("(li) Expected a register, found: ", PLPTokenType.ADDRESS);
		
		expectedNextToken("load immediate pseudo operation");
		String immediateOrLabel = currentToken.getValue();
		ensureTokenEquality("Expected a immediate value or label, found: ",
				PLPTokenType.NUMERIC, PLPTokenType.LABEL_PLAIN);
				
		appendPreprocessedInstruction(
				"lui " + targetRegister + ", $_hi: " + immediateOrLabel, lineNumber,
				true);
		appendPreprocessedInstruction("ori " + targetRegister + ", " + targetRegister
				+ ", $_lo: " + immediateOrLabel, lineNumber, true);
				
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
		ensureTokenEquality("(lvm) Expected a register, found: ", PLPTokenType.ADDRESS);
		
		expectedNextToken("lvm psuedo operation");
		String immediateOrLabel = currentToken.getValue();
		ensureTokenEquality("Expected a immediate value or label, found: ",
				PLPTokenType.NUMERIC, PLPTokenType.LABEL_PLAIN);
				
		appendPreprocessedInstruction("lui $at, $_hi: " + immediateOrLabel, lineNumber,
				true);
		appendPreprocessedInstruction("ori $at, $at, $_lo: " + immediateOrLabel,
				lineNumber, true);
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
		ensureTokenEquality("(svm) Expected a register, found: ", PLPTokenType.ADDRESS);
		
		expectedNextToken("svm psuedo operation");
		String immediateOrLabel = currentToken.getValue();
		ensureTokenEquality("Expected a immediate value or label, found:",
				PLPTokenType.NUMERIC, PLPTokenType.LABEL_PLAIN);
				
		appendPreprocessedInstruction("lui $at, $_hi: " + immediateOrLabel, lineNumber,
				true);
		appendPreprocessedInstruction("ori $at, $at, $_lo: " + immediateOrLabel,
				lineNumber, true);
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
		expectedNextToken("call psuedo operation");
		String label = currentToken.getValue();
		ensureTokenEquality("(call) Expected a label, found: ", PLPTokenType.LABEL_PLAIN);
		
		String[] registers = { "$a0", "$a1", "$a2", "$a3", "$t0", "$t1", "$t2t", "$t3",
				"$t4", "$t5", "$t6", "$t7", "$t8", "$t9", "$s0", "$s1", "$s2", "$s3",
				"$s4", "$s5", "$s6", "$s7", "$ra" };
				
		appendPreprocessedInstruction("addiu $sp, $sp, " + (registers.length * 4),
				lineNumber, true);
				
		for (int registerIndex = 0; registerIndex < registers.length; registerIndex++)
		{
			appendPreprocessedInstruction("sw " + registers[registerIndex] + ", "
					+ (registerIndex + 1) * 4 + "($sp)", lineNumber, true);
		}
		
		appendPreprocessedInstruction("jal " + label, lineNumber, true);
		appendPreprocessedInstruction("sll $0, $0, $0", lineNumber, true);
		
		addRegionAndIncrementAddress(26, 104);
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
		String[] registers = { "$a0", "$a1", "$a2", "$a3", "$t0", "$t1", "$t2t", "$t3",
				"$t4", "$t5", "$t6", "$t7", "$t8", "$t9", "$s0", "$s1", "$s2", "$s3",
				"$s4", "$s5", "$s6", "$s7" };
				
		for (int registerIndex = 0; registerIndex < registers.length; registerIndex++)
		{
			appendPreprocessedInstruction("lw " + registers[registerIndex] + ", "
					+ (registerIndex + 1) * 4 + "($sp)", lineNumber, true);
		}
		
		appendPreprocessedInstruction("addu $at, $zero, $ra", lineNumber, true);
		appendPreprocessedInstruction("lw $ra, " + ((registers.length + 1) * 4) + "($sp)",
				lineNumber, true);
		appendPreprocessedInstruction("addiu $sp, $sp, " + ((registers.length + 1) * 4),
				lineNumber, true);
		appendPreprocessedInstruction("sll $0, $0, $0", lineNumber, true);
		
		addRegionAndIncrementAddress(27, 108);
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
		// Start at four instead of zero and exclude $zero register, and normal register
		// names ((registerMap.size() / 2) - 2) * 4;
		appendPreprocessedInstruction(
				"addiu $sp, $sp, " + ((registerMap.size() / 2) - 2) * 4, lineNumber,
				true);
				
		int registerCount = (registerMap.size() / 2) - 1;
		for (int registerIndex = 1; registerIndex <= registerCount; registerIndex++)
		{
			appendPreprocessedInstruction(
					"sw $" + registerIndex + ", " + registerIndex * 4 + "($sp)",
					lineNumber, true);
		}
		
		addRegionAndIncrementAddress(registerCount, registerCount * 4);
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
		int registerCount = (registerMap.size() / 2) - 1;
		for (int registerIndex = 1; registerIndex <= registerCount; registerIndex++)
		{
			appendPreprocessedInstruction(
					"lw $" + registerIndex + ", " + registerIndex * 4 + "($sp)",
					lineNumber, true);
		}
		appendPreprocessedInstruction(
				"addiu $sp, $sp, " + ((registerMap.size() / 2) - 2) * 4, lineNumber,
				true);
				
		addRegionAndIncrementAddress(registerCount, registerCount * 4);
	}
	
	/*
	 * 
	 * ======================= Preprocess Operations =========================
	 * 
	 */
	
	private void preprocessNormalInstruction() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		instructionMap.get(instruction).getKey().perform();
		
		addRegionAndIncrementAddress();
	}
	
	public void registerImmediateOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("register immediate normal instruction");
		String register = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected a target register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("register immediate normal instruction");
		String immediate = currentToken.getValue();
		ensureTokenEquality(
				"(" + instruction + ") Expected an immediate value (16-bit), found: ",
				PLPTokenType.NUMERIC);
				
		appendPreprocessedInstruction(instruction + " " + register + ", " + immediate,
				lineNumber, true);
	}
	
	public void singleLabelOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("single label normal instruction");
		String label = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected a label, found: ",
				PLPTokenType.LABEL_PLAIN);
				
		appendPreprocessedInstruction(instruction + " " + label, lineNumber, true);
	}
	
	public void singleRegisterOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("single register normal instruction");
		String register = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected a register, found: ",
				PLPTokenType.ADDRESS);
				
		appendPreprocessedInstruction(instruction + " " + register, lineNumber, true);
	}
	
	public void registerOffsetRegisterOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("register offset register normal instruction");
		String targetRegister = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected a target register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("register offset register normal instruction");
		String offset = currentToken.getValue();
		ensureTokenEquality(
				"(" + instruction
						+ ") Expected an offset value (immediate) in bytes, found: ",
				PLPTokenType.NUMERIC);
				
		expectedNextToken("register offset register normal instruction");
		String sourceRegister = currentToken.getValue();
		ensureTokenEquality(
				"(" + instruction + ") Expected an (source register), found: ",
				PLPTokenType.PARENTHESIS_ADDRESS);
				
		appendPreprocessedInstruction(
				instruction + " " + targetRegister + ", " + offset + sourceRegister,
				lineNumber, true);
	}
	
	public void twoRegisterImmediateOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("two register immediate normal instruction");
		String destinationRegister = currentToken.getValue();
		ensureTokenEquality(
				"(" + instruction + ") Expected a destination register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("two register immediate normal instruction");
		String sourceRegister = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected an source register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("two register immediate normal instruction");
		String immediate = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected an immediate value, found: ",
				PLPTokenType.NUMERIC);
				
		appendPreprocessedInstruction(instruction + " " + destinationRegister + ", "
				+ sourceRegister + ", " + immediate, lineNumber, true);
	}
	
	public void twoRegisterLabelOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("two register label normal instruction");
		String targetRegister = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected a target register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("two register label normal instruction");
		String sourceRegister = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected an source register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("two register label normal instruction");
		String label = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected a label, found: ",
				PLPTokenType.LABEL_PLAIN);
				
		appendPreprocessedInstruction(
				instruction + " " + targetRegister + ", " + sourceRegister + ", " + label,
				lineNumber, true);
	}
	
	public void twoRegisterOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("two register normal instruction");
		String destinationRegister = currentToken.getValue();
		ensureTokenEquality(
				"(" + instruction + ") Expected a destination register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("two register normal instruction");
		String sourceRegister = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected an source register, found: ",
				PLPTokenType.ADDRESS);
				
		appendPreprocessedInstruction(
				instruction + " " + destinationRegister + ", " + sourceRegister,
				lineNumber, true);
	}
	
	public void threeRegisterOperation() throws AssemblerException
	{
		String instruction = currentToken.getValue();
		
		expectedNextToken("three register normal instruction");
		String destinationRegister = currentToken.getValue();
		ensureTokenEquality(
				"(" + instruction + ") Expected a destination register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("three register normal instruction");
		String sourceRegister = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected an source register, found: ",
				PLPTokenType.ADDRESS);
				
		expectedNextToken("three register normal instruction");
		String targetRegister = currentToken.getValue();
		ensureTokenEquality("(" + instruction + ") Expected an target register, found: ",
				PLPTokenType.ADDRESS);
				
		appendPreprocessedInstruction(instruction + " " + destinationRegister + ", "
				+ sourceRegister + ", " + targetRegister, lineNumber, true);
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
		
		ensureTokenEquality("(.org) Expected an address, found: ", PLPTokenType.NUMERIC);
		
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
		
		ensureTokenEquality(
				"(.word) Expected number to initialize current memory address to, found: ",
				PLPTokenType.NUMERIC);
				
		appendPreprocessedInstruction(ASM__WORD__ + currentToken.getValue(), lineNumber,
				true);
		addRegionAndIncrementAddress();
	}
	
	private void spaceDirective() throws AssemblerException
	{
		expectedNextToken(".space directive");
		
		ensureTokenEquality("(.space) Expected a number, found: ", PLPTokenType.NUMERIC);
		
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
		
		ensureTokenEquality(
				"(" + directiveToken.getValue() + ") Expected a string to store, found: ",
				PLPTokenType.STRING);
				
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
			ensureTokenEquality("(.text) Expected a string, found: ",
					PLPTokenType.STRING);
					
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
		
		ensureTokenEquality("(.data) Expected a string, found: ", PLPTokenType.STRING);
		
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
		
		ensureTokenEquality("(.equ) Expected a string, found: ", PLPTokenType.STRING);
		
		String symbol = currentToken.getValue();
		if (symbolTable.containsKey(symbol))
		{
			throw new AssemblerException(
					"(.equ) Symbol table already contains: " + currentToken.getValue());
		}
		
		expectedNextToken(".equ directive");
		
		ensureTokenEquality("(.equ) Expected an address after symbol, found: ",
				PLPTokenType.NUMERIC);
				
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
		instructionMap = new HashMap<>();
		registerMap = new HashMap<>();
		instructionOpcodeMap = new HashMap<>();
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
		instructionMap.put("addu", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("subu", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("and", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("or", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("nor", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("slt", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("sltu", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("sllv", new Pair<>(this::threeRegisterOperation, 0));
		instructionMap.put("srlv", new Pair<>(this::threeRegisterOperation, 0));
		
		instructionMap.put("sll", new Pair<>(this::twoRegisterImmediateOperation, 1));
		instructionMap.put("srl", new Pair<>(this::twoRegisterImmediateOperation, 1));
		
		instructionMap.put("jr", new Pair<>(this::singleRegisterOperation, 2));
		
		instructionMap.put("beq", new Pair<>(this::twoRegisterLabelOperation, 3));
		instructionMap.put("bne", new Pair<>(this::twoRegisterLabelOperation, 3));
		
		instructionMap.put("addiu", new Pair<>(this::twoRegisterImmediateOperation, 4));
		instructionMap.put("andi", new Pair<>(this::twoRegisterImmediateOperation, 4));
		instructionMap.put("ori", new Pair<>(this::twoRegisterImmediateOperation, 4));
		instructionMap.put("slti", new Pair<>(this::twoRegisterImmediateOperation, 4));
		instructionMap.put("sltiu", new Pair<>(this::twoRegisterImmediateOperation, 4));
		
		instructionMap.put("lui", new Pair<>(this::registerImmediateOperation, 5));
		
		instructionMap.put("lw", new Pair<>(this::registerOffsetRegisterOperation, 6));
		instructionMap.put("sw", new Pair<>(this::registerOffsetRegisterOperation, 6));
		
		instructionMap.put("j", new Pair<>(this::singleLabelOperation, 7));
		instructionMap.put("jal", new Pair<>(this::singleLabelOperation, 7));
		
		instructionMap.put("mulhi", new Pair<>(this::threeRegisterOperation, 8));
		instructionMap.put("mullo", new Pair<>(this::threeRegisterOperation, 8));
		
		instructionMap.put("jalr", new Pair<>(this::twoRegisterOperation, 9));
		
		instructionMap.put("ASM__WORD__", new Pair<>(() -> {
		} , 10));
		instructionMap.put("ASM__ORG__", new Pair<>(() -> {
		} , 10));
		instructionMap.put("ASM__SKIP__", new Pair<>(() -> {
		} , 10));
		instructionMap.put("ASM__LINE_OFFSET__", new Pair<>(() -> {
		} , 10));
		instructionMap.put("ASM__POINTER__", new Pair<>(() -> {
		} , 10));
		
		// R-Type Arithmetic
		instructionOpcodeMap.put("sll", 0x00);
		instructionOpcodeMap.put("sllv", 0x01);
		instructionOpcodeMap.put("srl", 0x02);
		instructionOpcodeMap.put("srlv", 0x03);
		instructionOpcodeMap.put("jr", 0x08);
		instructionOpcodeMap.put("jalr", 0x09);
		instructionOpcodeMap.put("mullo", 0x10);
		instructionOpcodeMap.put("mulhi", 0x11);
		instructionOpcodeMap.put("add", 0x20);
		instructionOpcodeMap.put("addu", 0x21);
		// functionMap.put("sub", 0x22);
		instructionOpcodeMap.put("subu", 0x23);
		instructionOpcodeMap.put("and", 0x24);
		instructionOpcodeMap.put("or", 0x25);
		instructionOpcodeMap.put("nor", 0x27);
		instructionOpcodeMap.put("slt", 0x2A);
		instructionOpcodeMap.put("sltu", 0x2B);
		
		instructionOpcodeMap.put("j", 0x02);
		instructionOpcodeMap.put("jal", 0x03);
		instructionOpcodeMap.put("beq", 0x04);
		instructionOpcodeMap.put("bne", 0x05);
		instructionOpcodeMap.put("addiu", 0x09);
		instructionOpcodeMap.put("slti", 0x0A);
		instructionOpcodeMap.put("sltiu", 0x0B);
		instructionOpcodeMap.put("andi", 0x0C);
		instructionOpcodeMap.put("ori", 0x0D);
		instructionOpcodeMap.put("lui", 0x0F);
		instructionOpcodeMap.put("lw", 0x23);
		instructionOpcodeMap.put("sw", 0x2B);
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
		String previousToken = currentToken.getValue();
		if (!nextToken())
			throw new AssemblerException("Previous token->(" + previousToken
					+ ") Unexpected end of token stream. In " + location);
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
		for (int index = 0; index < timesToAddCurrentRegion; index++)
		{
			regionMap.add(currentRegion);
		}
		currentAddress += currentAddressIncrementSize;
	}
	
	private void ensureTokenEquality(String assemblerExceptionMessage,
			PLPTokenType compareTo) throws AssemblerException
	{
		if (!currentToken.getTypeName().equals(compareTo.name()))
		{
			throw new AssemblerException(
					assemblerExceptionMessage + currentToken.getValue());
		}
	}
	
	private void ensureTokenEquality(String assemblerExceptionMessage,
			PLPTokenType... compareTo) throws AssemblerException
	{
		for (PLPTokenType comparison : compareTo)
		{
			if (currentToken.getTypeName().equals(comparison.name()))
				return;
		}
		
		throw new AssemblerException(assemblerExceptionMessage + currentToken.getValue());
	}
}
