package edu.asu.plp.tool.backend.plpisa.assembler2;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import com.faeysoft.preceptor.lexer.LexException;
import com.faeysoft.preceptor.lexer.Lexer;
import com.faeysoft.preceptor.lexer.Token;

import edu.asu.plp.tool.backend.BiDirectionalOneToManyMap;
import edu.asu.plp.tool.backend.OrderedBiDirectionalOneToManyHashMap;
import edu.asu.plp.tool.backend.isa.ASMDisassembly;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.ASMInstruction;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblyException;
import edu.asu.plp.tool.backend.plpisa.PLPASMImage;
import edu.asu.plp.tool.backend.plpisa.PLPAssemblyInstruction;
import edu.asu.plp.tool.backend.plpisa.assembler.AssemblerStep;
import edu.asu.plp.tool.backend.plpisa.assembler.PLPDisassembly;
import edu.asu.plp.tool.backend.plpisa.assembler2.PLPTokenType;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.RegisterArgument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.StringLiteral;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.Value;
import edu.asu.plp.tool.backend.plpisa.assembler2.instructions.AssemblerDirectiveStep;
import edu.asu.plp.tool.backend.util.ISAUtil;

public class PLPAssembler implements Assembler
{
	private InstructionMap plpInstructions;
	private HashMap<String, AssemblerDirectiveStep> directiveMap;
	private HashMap<String, Byte> registerMap;
	private HashMap<String, AssemblerStep> pseudoOperationMap;
	
	private HashMap<String, Long> symbolTable;
	private HashMap<String, HashMap<Integer, String>> lineNumAndAsmFileMap;
	
	private BiDirectionalOneToManyMap<ASMInstruction, ASMDisassembly> assemblyToDisassemblyMap;
	
	private ListIterator<Token> tokenIterator;
	private Lexer lexer;
	private Token currentToken;
	
	private long programLocation;
	private int lineNumber;
	private long currentAddress;
	
	private long currentDataAddress;
	private long currentTextAddress;
	private int currentRegion;
	private long entryPoint;
	//private ArrayList<Integer> regionMap;
	
	
	private static final String ASM__WORD__ = "ASM__WORD__";
	private static final String ASM__ORG__ = "ASM__ORG__";
	private static final String ASM__SKIP__ = "ASM__SKIP__";
	
	private static final String ASM__HIGH__ = "$_hi:";
	private static final String ASM__LOW__ = "$_lo:";
	
	@Override
	public void initialize() throws AssemblerException
	{
		symbolTable = new HashMap<>();
		lexer = new Lexer(PLPTokenType.createSet());
		lineNumAndAsmFileMap = new HashMap<>();
		
		loadPLPInstructionsMap();
		loadPLPAssemblerDirectivesMap();
		loadPLPPseudoOperationsMap();
		loadRegisterMap();
		
	}
	
	private void loadPLPAssemblerDirectivesMap()
	{
		directiveMap = new HashMap<>();
		
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
	
	private void loadPLPPseudoOperationsMap()
	{
		pseudoOperationMap = new HashMap<>();
	}
	
	private void loadRegisterMap()
	{
		registerMap = new HashMap<>();
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
	
	
	private void loadPLPInstructionsMap()
	{
		plpInstructions = new InstructionMap();
		
		plpInstructions.addRTypeInstruction("addu", 0x21);
		plpInstructions.addRTypeInstruction("subu", 0x23);
		plpInstructions.addRTypeInstruction("and", 0x24);
		plpInstructions.addRTypeInstruction("or", 0x25);
		plpInstructions.addRTypeInstruction("nor", 0x27);
		plpInstructions.addRTypeInstruction("slt", 0x2a);
		plpInstructions.addRTypeInstruction("sltu", 0x2b);
		plpInstructions.addRTypeInstruction("mullo", 0x10);
		plpInstructions.addRTypeInstruction("mulhi", 0x11);
		plpInstructions.addRTypeInstruction("sllv", 0x01);
		plpInstructions.addRTypeInstruction("slrv", 0x03);
		
		plpInstructions.addRITypeInstruction("sll", 0x00);
		plpInstructions.addRITypeInstruction("srl", 0x02);
		
		plpInstructions.addRJTypeInstruction("jr", 0x08);
		plpInstructions.addRJTypeInstruction("jalr", 0x09);
		
		plpInstructions.addITypeInstruction("addiu", 0x09);
		plpInstructions.addITypeInstruction("andi", 0x0c);
		plpInstructions.addITypeInstruction("ori", 0x0d);
		plpInstructions.addITypeInstruction("slti", 0x0a);
		plpInstructions.addITypeInstruction("sltiu", 0x0b);
		plpInstructions.addITypeInstruction("lui", 0x0f);
		
		plpInstructions.addRLTypeInstruction("lw", 0x23);
		plpInstructions.addRLTypeInstruction("sw", 0x2B);
		
		
	}
	
	@Override
	public ASMImage assemble(List<ASMFile> asmFiles) throws AssemblerException
	{
		assemblyToDisassemblyMap = new OrderedBiDirectionalOneToManyHashMap<>();
		
		initialize();
		
		//2nd Step Preprocess - Take care of syntax errors, symbol table, assembler directives, pseudoOperations, comments and empty lines
		for (ASMFile asmFile : asmFiles)
		{
			preprocessFile(asmFile.getContent(), asmFile);
		}
		
		//2nd Step Object Code generation
		for (ASMFile asmFile : asmFiles)
		{
			
			assembleFile(asmFile.getContent(), asmFile.getName());
		}
		
		return new PLPASMImage(assemblyToDisassemblyMap);
	}
	
	
	
	private void assembleFile(String content, String asmFileName) throws AssemblerException
	{
		String[] lines = content.split("\\n\\r?");
		lineNumber = 1;
		try
		{
			for (String line : lines)
			{
				String source = line.trim();
				String instruction = source.split("\\s+")[0];
				String remainder = source.substring(instruction.length());
				remainder = remainder.trim();
				String[] argumentStrings = remainder.split(",\\s*");
				
				Argument[] arguments = parseArguments(argumentStrings);
				
				
				PLPDisassembly disassembly = process(instruction, arguments);
				ASMInstruction key = new PLPAssemblyInstruction(lineNumber, source);
				assemblyToDisassemblyMap.put(key, disassembly);
				
				lineNumber++;
			}
		}
		catch (ParseException exception)
		{
			throw new AssemblerException(exception);
		}
		catch (Exception exception)
		{
			throw new AssemblerException(exception);
		}
	}
	
	private void preprocessFile(String content, ASMFile asmFile) throws AssemblerException
	{
		String[] lines = content.split("\\n\\r?");
		lineNumber = 1;
		
		List<Token> fileTokens = new ArrayList<Token>();
		
		HashMap<Integer, String> lineNumberToPreprocessed = new HashMap<>();
		
		try
		{
			for (String line: lines)
			{
				List<Token> linetokens = lexer.lex(line);
				
				tokenIterator = linetokens.listIterator();
				currentToken = null;
				
				if(!nextToken(1))
				{
					continue;
				}
				
				String preprocessedInstruction = "";
				
				if(currentToken == null)
				{
					//Error
					throw new AssemblerException( "Line number: " + Integer.toString(lineNumber) + " Token is null");
				}
				
				if(isAssemblerDirective(currentToken))
				{
					preprocessedInstruction = directiveMap.get(currentToken.getValue()).perform();
				}
				else if(pseudoOperationMap.containsKey(currentToken.getValue()))
				{
						
				}
				else if(isInstruction(currentToken))
				{
					preprocessedInstruction = preprocessNormalInstruction();
				}
				else if(currentToken.getTypeName() == PLPTokenType.COMMENT.name())
				{
					preprocessedInstruction = ASM__SKIP__;						
				}
				else if(currentToken.getTypeName() == PLPTokenType.LABEL_COLON.name())
				{
					preprocessedInstruction = labeldeclarationProcessing();
				}
				else if(currentToken.getTypeName() == PLPTokenType.NEW_LINE.name())
				{
					preprocessedInstruction = ASM__SKIP__;
				}
				else
				{
					throw new AssemblerException(
								"Line number: " + Integer.toString(lineNumber) + ":Unknown token in preprocessing, found: "
										+ currentToken.getValue());
				}
				
				if(nextToken(1))
				{
					throw new AssemblerException( "Line number: " + Integer.toString(lineNumber) + ":Extra token is present a line, found: " + currentToken.getValue());
				}
				
					
				
				lineNumberToPreprocessed.put(lineNumber, preprocessedInstruction);
				fileTokens.addAll(linetokens);
				lineNumber++;
				
			}
			
			lineNumAndAsmFileMap.put(asmFile.getName(), lineNumberToPreprocessed);
		}
		catch(Exception exp)
		{
			
		}
		
	}
	
	private PLPDisassembly process(String instructionName, Argument[] arguments)
			throws ParseException
	{
		PLPInstruction instruction = plpInstructions.get(instructionName);
		int codedInstruction = instruction.assemble(arguments);
		long address = programLocation++;
		PLPDisassembly disassembly = new PLPDisassembly(address, codedInstruction);
		
		return disassembly;
	}
	
	private Argument[] parseArguments(String[] argumentStrings) throws ParseException
	{
		int size = argumentStrings.length;
		Argument[] arguments = new Argument[size];
		
		for (int index = 0; index < size; index++)
		{
			String argumentString = argumentStrings[index];
			arguments[index] = parseArgument(argumentString);
		}
		
		return arguments;
	}
	
	private Argument parseArgument(String argumentString) throws ParseException
	{
		argumentString = argumentString.trim();
		if (argumentString.startsWith("'") || argumentString.startsWith("\""))
		{
			boolean valid = argumentString.endsWith("" + argumentString.charAt(0));
			if (!valid)
			{
				throw new ParseException(
						"String literals must be enclosed in single or double quotes.",
						lineNumber);
			}
			
			return new StringLiteral(argumentString);
		}
		else if (argumentString.startsWith("\\$"))
		{
			return new RegisterArgument(argumentString);
		}
		else if (argumentString.startsWith("0x"))
		{
			boolean valid = argumentString.matches("0x[a-fA-F0-9]+");
			if (!valid)
			{
				throw new ParseException(
						"Expected an integer value to follow '0x' but found '"
								+ argumentString + "'", lineNumber);
			}
			
			String number = argumentString.substring(2);
			int value = Integer.parseInt(number, 16);
			return new Value(value);
		}
		else if (argumentString.startsWith("0h"))
		{
			boolean valid = argumentString.matches("0h[a-fA-F0-9]+");
			if (!valid)
			{
				throw new ParseException(
						"Expected an integer value to follow '0x' but found '"
								+ argumentString + "'", lineNumber);
			}
			
			String number = argumentString.substring(2);
			int value = Integer.parseInt(number, 16);
			return new Value(value);
		}
		else if (argumentString.startsWith("0b"))
		{
			boolean valid = argumentString.matches("0b[01]+");
			if (!valid)
			{
				throw new ParseException(
						"Expected an integer value to follow '0b' but found '"
								+ argumentString + "'", lineNumber);
			}
			
			String number = argumentString.substring(2);
			int value = Integer.parseInt(number, 2);
			return new Value(value);
		}
		else if (argumentString.matches("[0-9]+"))
		{
			int value = Integer.parseInt(argumentString);
			return new Value(value);
		}
		else
		{
			throw new ParseException("Expected argument but found '" + argumentString
					+ "'", lineNumber);
		}
	}
	
	
	private String orgDirective() throws AssemblerException
	{
		expectedNextToken(".org directive");
		
		ensureTokenEquality("(.org) Expected an address, found: ", PLPTokenType.NUMERIC);
		
		try
		{
			currentAddress = ISAUtil.sanitize32bits(currentToken.getValue());
		}
		catch (AssemblyException e)
		{
			e.printStackTrace();
		}
		
		return ASM__ORG__ + " " + currentToken.getValue();
		
		
	}
	
	private String wordDirective() throws AssemblerException
	{
		expectedNextToken(".word directive");
		
		ensureTokenEquality(
				"(.word) Expected number to initialize current memory address to, found: ",
				PLPTokenType.NUMERIC);
		
		addRegionAndIncrementAddress(1, 4);
		
		return ASM__WORD__ + " " + currentToken.getValue();
		
	}
	
	private String spaceDirective() throws AssemblerException
	{
		expectedNextToken(".space directive");
		
		ensureTokenEquality("(.space) Expected a number, found: ", PLPTokenType.NUMERIC);
		
		try
		{
			long size = ISAUtil.sanitize32bits(currentToken.getValue());
			currentAddress += 4 * size;
			//byteSpace += 4 * size;
			
		}
		catch (AssemblyException e)
		{
			e.printStackTrace();
		}
		return ASM__ORG__ + " " + Long.toString(currentAddress);
	}
	
	private String asciiDirective() throws AssemblerException
	{
		
		Token directiveToken = currentToken;
		boolean wordAligned = directiveToken.getValue().equals(".asciiw");
		StringBuilder preInstruction = new StringBuilder();
		preInstruction.append("");
		
		expectedNextToken(currentToken.getValue() + " directive");
		
		ensureTokenEquality("(" + directiveToken.getValue()
				+ ") Expected a string to store, found: ", PLPTokenType.STRING);
		
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
					stringBuffer = stringBuffer.replace(
							index,
							index + 2,
							"\\"
									+ specialEscapedCharacters.indexOf(currentValue
											.charAt(index + 1)));
				}
				else if (currentValue.charAt(index + 1) == '\\')
				{
					stringBuffer = stringBuffer.replace(index, index + 2, "\\");
				}
				else
				{
					System.out
							.println("("
									+ directiveToken.getValue()
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
				preInstruction.append(ASM__WORD__ + " 0x");
			
			if (!wordAligned)
			{
				preInstruction.append(String.format("%02x", (int) currentValue.charAt(index)));
				
			}
			else
			{
				preInstruction.append(String.format("%08x", (int) currentValue.charAt(index)));
				
				addRegionAndIncrementAddress(1, 4);
			}
			
			if (!wordAligned && (index + 1) % 4 == 0 && index > 0)
			{
				addRegionAndIncrementAddress(1, 4);
				preInstruction.append("\r\n");
			}
		}
		return preInstruction.toString();
	}
	
	/**
	 * This function will handle include assembler directive. 
	 * By using this directive we can include an asm file without actually "importing/creating" new asm files in the project.
	 * On encountering this directive, the asm file given in .include directive will be first verified whether it is already present in
	 * the current asm list, if not the file will be added to the list and preprocessing will be done for that file.
	 * @return
	 * @throws AssemblerException
	 */
	private String includeDirective() throws AssemblerException
	{
		expectedNextToken("include directive");
		
		throw new UnsupportedOperationException("Include Directive is not implemented");
		
		//return ASM__SKIP__;
	}
	
	private String textDirective() throws AssemblerException
	{
		expectedNextToken(".text directive");
		
		if (currentRegion != 1)
		{
			ensureTokenEquality("(.text) Expected a number, found: ", PLPTokenType.NUMERIC);
			
			//directiveOffset++;
			
			if (currentRegion == 2)
				currentDataAddress = currentAddress;
			
			currentRegion = 1;
			currentAddress = currentTextAddress;
		}
		
		try
		{
			currentAddress = ISAUtil.sanitize32bits(currentToken.getValue());
			
			if (currentAddress < 0)
				throw new AssemblerException("Line Number: "+ Integer.toString(lineNumber)+ " Starting address for .text is not defined.");
		}
		catch (AssemblyException e)
		{
			e.printStackTrace();
		}
		
		entryPoint = currentAddress;
		currentTextAddress = entryPoint;
		
		return ASM__ORG__ + " " + currentToken.getValue();
	}
	
	private String dataDirective() throws AssemblerException
	{
		expectedNextToken(".data directive");
		
		ensureTokenEquality("(.data) Expected a number, found: ", PLPTokenType.NUMERIC);
	
		if (currentRegion != 2)
		{
			//directiveOffset++;
			if (currentRegion == 1)
				currentTextAddress = currentAddress;
			
			currentRegion = 2;
			currentAddress = currentDataAddress;
		}
		
		try
		{
			currentAddress = ISAUtil.sanitize32bits(currentToken.getValue());
			
			if (currentAddress < 0)
				throw new AssemblerException("Line Number: "+ Integer.toString(lineNumber)+ " Starting address for .data is not defined.");
		}
		catch (AssemblyException e)
		{
			e.printStackTrace();
		}
		currentDataAddress = currentAddress;	
		
		return ASM__ORG__ + " " + currentToken.getValue();
	}
	
	private String equDirective() throws AssemblerException
	{
		expectedNextToken(".equ directive");
		
		ensureTokenEquality("(.equ) Expected a string, found: ", PLPTokenType.STRING);
		
		String symbol = currentToken.getValue();
		if (symbolTable.containsKey(symbol))
		{
			throw new AssemblerException("Line Number: " + Integer.toString(lineNumber) + " (.equ) Symbol table already contains: "
					+ currentToken.getValue());
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
					"Line Number: " + Integer.toString(lineNumber) + " (.equ) Could not process address after symbol, found: "
							+ currentToken.getValue());
		}
		
		symbolTable.put(symbol, value);
		
		return "";
	}
	
	private String preprocessNormalInstruction() throws AssemblerException
	{
		String preprocessedInstruction = "";
		String strInstruction = currentToken.getValue();
		String strFirstArgument = "", strSecondArgument = "";
		
		
		PLPInstruction instruction = plpInstructions.get(strInstruction);
		
		preprocessedInstruction += strInstruction;
		
		
		if(instruction != null)
		{
			ArgumentType[] lstArguments = instruction.getArgumentsofInstruction();
			if(lstArguments.length > 0)
			{
				
				expectedNextToken(currentToken.getValue() + " instruction needs more tokens");
				ensureArgumentEquality(strInstruction, lstArguments[0] );
				
				strFirstArgument = currentToken.getValue();
				
				preprocessedInstruction += (" " + strFirstArgument);
				
				if(lstArguments.length > 1)
				{
					expectedNextToken(strInstruction + " operation");
					ensureTokenEquality("(" + strInstruction + ") Expected a comma after "
							+ strFirstArgument + " found: ", PLPTokenType.COMMA);

					expectedNextToken(strInstruction + " operation");
					ensureArgumentEquality(strInstruction, lstArguments[1]);
						
					strSecondArgument = currentToken.getValue();
					preprocessedInstruction += (" " + strSecondArgument);
						
					if(lstArguments.length > 2)
					{
						expectedNextToken(strInstruction + " operation");
						ensureTokenEquality("(" + strInstruction + ") Expected a comma after "
									+ strSecondArgument + " found: ", PLPTokenType.COMMA);
							
						expectedNextToken(strInstruction + " operation");
						ensureArgumentEquality(strInstruction, lstArguments[2]);
							
						preprocessedInstruction += (" " + currentToken.getValue());
					}
						
					
					
					
				}
				
				
			}
			
		}
			
		
		return preprocessedInstruction;
	}
	
	private String labeldeclarationProcessing() throws AssemblerException
	{
		String processInstruction = "";
		
		Token directiveToken = currentToken;
		String labelValue = directiveToken.getValue();
		// Remove colon from label
		labelValue = labelValue.substring(0, labelValue.length() - 1);
		
		if (symbolTable.containsKey(labelValue))
		{
			throw new AssemblerException("Line Number: "+ Integer.toString(lineNumber)+" (" + directiveToken.getTypeName()
					+ ") preprocessing label failure. Symbol already defined, found: "
					+ directiveToken.getValue());
		}
		else
		{
			symbolTable.put(labelValue, currentAddress);
			processInstruction = ASM__SKIP__;
			//directiveOffset++;
		}
		
		return processInstruction;
		
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
		//String strLineNumber = String.valueOf(lineNumber);
		if (!nextToken(1))
			throw new AssemblerException("Line Number: "+String.valueOf(lineNumber)+" Previous token->(" + previousToken
					+ ") Unexpected end of token stream at " + location);
		
	}
	
	private void ensureArgumentEquality(String message, ArgumentType argument) throws AssemblerException
	{
		if(argument.equals(ArgumentType.REGISTER) && !isRegister(currentToken))
		{
			throw new AssemblerException("Line Number: "+Integer.toString(lineNumber)+ " expected a register but got "+ currentToken.getValue()+" at "+message);
		}
		else if(argument.equals(ArgumentType.NUMBER_LITERAL) && !isNumericValue(currentToken))
		{
			throw new AssemblerException("Line Number: "+Integer.toString(lineNumber)+ " expected a numeric value but got "+ currentToken.getValue()+" at "+message);
		}
		else if(argument.equals(ArgumentType.MEMORY_LOCATION) && !isMemoryLocation(currentToken))
		{
			throw new AssemblerException("Line Number: "+Integer.toString(lineNumber)+ " expected a memory location (represented -> <<number>>(<<register>>) example - 4($t3) ) but got "+ currentToken.getValue()+" at "+message);
		}
		
	}
	
	private void ensureTokenEquality(String message, PLPTokenType compareTo) throws AssemblerException
	{
		String sMessage = "Line Number: " +Integer.toString(lineNumber) + " " + message + currentToken.getValue();
		
		if (compareTo.equals(PLPTokenType.INSTRUCTION))
		{
			if(!isInstruction(currentToken))
			{
				throw new AssemblerException(sMessage);
			}
			return;
		}
		else if (compareTo.equals(PLPTokenType.LABEL_PLAIN))
		{
			if(!isLabel(currentToken))
			{
				throw new AssemblerException(sMessage);
			}
			
			return;
		}
		else if (compareTo.equals(PLPTokenType.ADDRESS))
		{
			if(!isRegister(currentToken))
			{
				throw new AssemblerException(sMessage);
			}
			
			return;
		}
		else if (compareTo.equals(PLPTokenType.PARENTHESIS_ADDRESS))
		{
			if(!isMemoryLocation(currentToken))
			{
				throw new AssemblerException(sMessage);
			}
			
			return;
		}
		
		if (!currentToken.getTypeName().equals(compareTo.name()))
			throw new AssemblerException(sMessage);
		
	}
	
	private boolean isMemoryLocation(Token token)
	{
		
		boolean valid = false;
		
		String value = token.getValue();
		
		String[] parts = value.split("\\(");
		
		if(isNumericValue(parts[0]))
		{
			String reg = parts[1].substring(0, parts[1].length()-1);
			valid = registerMap.containsKey(reg);
		}
		
		return valid;
	}
	
	private boolean isNumericValue(Token token)
	{
		boolean valid = false;
		
		String argumentString = token.getValue();
		
		
		
		return isNumericValue(argumentString);
		
	}
	
	private boolean isNumericValue(String argumentString)
	{
		boolean valid = false;
		
		if (argumentString.startsWith("0x"))
		{
			valid = argumentString.matches("0x[a-fA-F0-9]+");
			
		}
		else if(argumentString.startsWith("0h"))
		{
			valid = argumentString.matches("0h[a-fA-F0-9]+");
		}
		else if (argumentString.startsWith("0b"))
		{
			valid = argumentString.matches("0b[01]+");
		}
		else 
		{
			valid = argumentString.matches("[0-9]+");
		}
		
		return valid;
	}
	
	private boolean isInstruction(Token token)
	{
		if(token.getTypeName() == PLPTokenType.INSTRUCTION.name() && plpInstructions.containsKey(token.getValue()))
			return true;
		else
			return false;
		
	}
	
	private boolean isLabel(Token token)
	{
		if(false)
			return true;
		else
			return false;
		
	}
	
	private boolean isRegister(Token token)
	{
		if (token.getTypeName().equals(PLPTokenType.ADDRESS.name()))
		{
			return registerMap.containsKey(token.getValue());
		}
		else if (token.getTypeName().equals(PLPTokenType.PARENTHESIS_ADDRESS.name()))
		{
			return registerMap.containsKey(token.getValue().replaceAll("\\(|\\)", ""));
		}
		
		return false;
	}
	
	private boolean isAssemblerDirective(Token token)
	{
		if(token.getTypeName() == PLPTokenType.DIRECTIVE.name() && directiveMap.containsKey(token.getValue()))
			return true;
		else
			return false;
	}
	
	private void addRegionAndIncrementAddress(int timesToAddCurrentRegion,
			int currentAddressIncrementSize)
	{
		currentAddress += currentAddressIncrementSize;
	}
	
	
}
