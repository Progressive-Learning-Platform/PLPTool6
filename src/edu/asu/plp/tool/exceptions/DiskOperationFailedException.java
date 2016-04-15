package edu.asu.plp.tool.exceptions;

public class DiskOperationFailedException extends Exception
{
	public DiskOperationFailedException()
	{
		super();
	}

	public DiskOperationFailedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DiskOperationFailedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public DiskOperationFailedException(String message)
	{
		super(message);
	}

	public DiskOperationFailedException(Throwable cause)
	{
		super(cause);
	}
}
