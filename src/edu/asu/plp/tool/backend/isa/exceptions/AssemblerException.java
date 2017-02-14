package edu.asu.plp.tool.backend.isa.exceptions;

public class AssemblerException extends ISAException
{
	public static final int LEXXER_ERROR = 1;
	public static final int UNKOWN_TOKEN_ERROR = 2;
	public static final int EXTRA_TOKEN_ERROR = 3;
	public static final int MISSING_TOKEN_ERROR = 4;
	public static final int NEXT_TOKEN_MISSING_ERROR = 5;
	public static final int TOKEN_NOT_MATCHING_ERROR = 6;
	public static final int SANITIZE_ERROR = 7;
	public static final int DUPLICATE_LABEL_ERROR = 8;
	
	public AssemblerException(String exception)
	{
		super(exception);
	}
	
	public AssemblerException()
	{
		super();
	}
	
	public AssemblerException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}
	
	public AssemblerException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public AssemblerException(Throwable cause)
	{
		super(cause);
	}
}
