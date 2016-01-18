package edu.asu.plp.tool.backend.util;

import edu.asu.plp.tool.backend.isa.exceptions.AssemblyException;

public class ISAUtil
{
	public static long sanitize32bits(String number) throws AssemblyException
	{
		if (number.startsWith("0x") || number.startsWith("0h"))
		{
			return Long.parseLong(number.substring(2), 16) & 0xFFFFFFFF;
		}
		else if (number.startsWith("0b"))
		{
			return Long.parseLong(number.substring(2), 2) & 0xFFFFFFFF;
		}
		else if (number.startsWith("'") && number.endsWith("'"))
		{
			return parseEscapeCharacter(number);
		}
		
		return Long.parseLong(number) & 0xFFFFFFFF;
	}
	
	public static long sanitize16bits(String number) throws AssemblyException
	{
		if (number.startsWith("0x") || number.startsWith("0h"))
		{
			return Long.parseLong(number.substring(2), 16) & 0xFFFF;
		}
		else if (number.startsWith("0b"))
		{
			return Long.parseLong(number.substring(2), 2) & 0xFFFF;
		}
		else if (number.startsWith("'") && number.endsWith("'"))
		{
			return parseEscapeCharacter(number);
		}
		
		return Long.parseLong(number) & 0xFFFF;
	}
	
	/**
	 * Assumes you have already checked that the string starts and ends with '
	 * 
	 * @param string
	 * @return
	 */
	public static long parseEscapeCharacter(String string) throws AssemblyException
	{
		if (string.length() == 3)
			return string.charAt(1);
		else if (string.length() == 4)
		{
			if (string.charAt(1) == '\\')
			{
				switch (string.charAt(2))
				{
					case 'n':
						return '\n';
					case 'r':
						return '\r';
					case 't':
						return '\t';
					case '\\':
						return '\\';
					case '\"':
						return '\"';
					case '\'':
						return '\'';
					case '0':
						return '\0';
					default:
						throw new AssemblyException("Invalid escape character");
				}
			}
			else
				throw new AssemblyException("Invalid character format");
		}
		else
			throw new AssemblyException("Invalid character format");
	}
}
