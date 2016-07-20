package edu.asu.plp.tool.backend.plpisa.assembler2.instructions;

import static edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType.REGISTER;

import java.text.ParseException;

import edu.asu.plp.tool.backend.plpisa.assembler2.Argument;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.ArgumentType;
import edu.asu.plp.tool.backend.plpisa.assembler2.arguments.RegisterArgument;

public class RJTypeInstruction extends AbstractInstruction
{
	//jr $rd
	//Type 2
	
	public static final int RD_LOCATION = 21;
	public static final int MASK_5BIT = 0b011111;
	public static final int MASK_6BIT = 0b111111;
	public static final int OP_CODE_LOCATION = 0;
	
	private int opCode = 0;
	
	public RJTypeInstruction(int opCode)
	{
		super(new ArgumentType[] { REGISTER });
		this.opCode = opCode;
	}
	
	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException
	{
		Argument rdArgument = arguments[0];
		
		return assembleEncodings(rdArgument.encode());
	}
	
	private int assembleEncodings(int encodedRegister)
	{
		int encodedBitString = 0;
		
		encodedBitString |= (encodedRegister & MASK_5BIT) << RD_LOCATION;
		encodedBitString |= (opCode & MASK_6BIT) << OP_CODE_LOCATION;
		
		return encodedBitString;
	}
	
	/*private RTypeInstruction backingInstruction;
	
	public RJTypeInstruction(int functCode)
	{
		super(new ArgumentType[] { REGISTER });
		this.backingInstruction = new RTypeInstruction(functCode);
	}
	
	@Override
	protected int safeAssemble(Argument[] arguments) throws ParseException
	{
		Argument rdRegisterArgument = new RegisterArgument("$0");
		Argument rsRegisterArgument = arguments[0];
		Argument rtRegisterArgument = new RegisterArgument("$0");
		
		arguments = new Argument[] { rdRegisterArgument, rsRegisterArgument,
				rtRegisterArgument };
		return backingInstruction.assemble(arguments);
	}
	*/
}
