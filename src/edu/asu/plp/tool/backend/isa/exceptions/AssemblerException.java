package edu.asu.plp.tool.backend.isa.exceptions;

public class AssemblerException extends ISAException
{
	public static final int ASSEMBLER_DIRECTIVE_ERROR = 1;
	public static final int ASSEMBLER_DIRECTIVE_MISSING_TOKEN_ERROR = 1;
	public static final int ASSEMBLER_DIRECTIVE_INVALID_TOKEN_ERROR = 2;
	public static final int DUPLICATE_LABEL_ERROR = 2;
	public static final int DUPLICATE_LABEL_SUB_ERROR = 1;
	public static final int INVALID_TOKEN_ERROR = 3;
	public static final int INVALID_TOKEN_INVALID_INSTRUCTION_ERROR = 1;
	public static final int INVALID_TOKEN_INVALID_LABEL_ERROR = 2;
	public static final int INVALID_LABEL_TARGET_ERROR = 4;
	public static final int INVALID_LABEL_TARGET_JUMP_ERROR = 1;
	public static final int INVALID_LABEL_TARGET_BRANCH_ERROR = 2;
	public static final int INVALID_NUMBER_OF_TOKENS_ERROR = 5;
	public static final int INVALID_NUMBER_OF_TOKENS_MISSING_ERROR = 1;
	public static final int INVALID_NUMBER_OF_TOKENS_EXTRA_ERROR = 2;
	public static final int INVALID_NUMBER_OF_TOKENS_NOT_MATCHING_ERROR = 3;
	public static final int INVALID_REGISTER_ERROR = 6;
	public static final int INVALID_REGISTEr_NOT_REGISTER_ERROR = 1;
	public static final int INVALID_NUMBER_ERROR = 7;
	public static final int INVALID_NUMBER_SUB_ERROR = 1;
	public static final int LEXXER_ERROR = 8;
	/*public static final int LEXXER_ERROR = 2;
	public static final int UNKOWN_TOKEN_ERROR = 3;
	public static final int EXTRA_TOKEN_ERROR = 4;
	public static final int MISSING_TOKEN_ERROR = 5;
	public static final int NEXT_TOKEN_MISSING_ERROR = 6;
	public static final int TOKEN_NOT_MATCHING_ERROR = 7;
	public static final int SANITIZE_ERROR = 8;
	public static final int DUPLICATE_LABEL_ERROR = 9;*/
	
	public static final String ErrorMessagingSystemKey = "ERRORMESSAGESYSTEM";
	public static final String DescriptionKey = "DESCRIPTION";
	public static final String LinksKey = "LINKS";
	public static final String ExamplesKey = "EXAMPLES";
	
	public static final String ERROR_ASSEMBLER_DIRECTIVE = "ASSEMBLER_DIRECTIVE_ERROR";
	public static final String ERROR_MISSING_TOKEN_ASSEMBLER_DIRECTIVE = "MISSING_TOKEN_ERROR";
	public static final String ERROR_INVALID_TOKEN_ASSEMBLER_DIRECTIVE = "INVALID_TOKEN_ERROR";
	public static final String ERROR_DUPLICATE_LABEL = "DUPLICATE_LABEL_ERROR";
	public static final String ERROR_SUB_DUPLICATE_LABEL = "DUPLICATE_LABEL_ERROR";
	public static final String ERROR_INVALID_TOKEN = "INVALID_TOKEN_ERROR";
	public static final String ERROR_INVLAID_INSTRUCTION_INVALID_TOKEN = "INVALID_INSTRUCTION_ERROR";
	public static final String ERROR_INVALID_LABEL_INVALID_TOKEN = "INVALID_LABEL_ERROR";
	public static final String ERROR_INVALID_LABEL_TARGET = "INVALID_LABEL_TARGET_ERROR";
	public static final String ERROR_BRANCH_INVALID_LABEL_TARGET = "INVALID_JUMP_TARGET_ERROR";
	public static final String ERROR_JUMP_INVALID_LABEL_TARGET = "INVALID_BRANCH_TARGET_ERROR";
	public static final String ERROR_INVALID_NUMBER_OF_TOKENS = "INVALID_NUMBER_OF_TOKENS_ERROR";
	public static final String ERROR_MISSING_TOKENS_INVALID_NUMBER_OF_TOKENS = "MISSING_TOKENS_ERROR";
	public static final String ERROR_EXTRA_TOKENS_INVALID_NUMBER_OF_TOKENS = "EXTRA_TOKENS_ERROR";
	public static final String ERROR_NOT_MATCHING_INVALID_NUMBER_OF_TOKENS = "NOT_MATCHING_ERROR";
	public static final String ERROR_INVALID_REGISTER = "INVALID_REGISTER_ERROR";
	public static final String ERROR_SUB_INVALID_REGISTER = "NOT_REGISTER_ERROR";
	public static final String ERROR_INVALID_NUMBER = "INVALID_NUMBER_ERROR";
	public static final String ERROR_NOT_A_NUMBER_INVALID_NUMBER = "NOT_A_NUMBER_ERROR";
	
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
