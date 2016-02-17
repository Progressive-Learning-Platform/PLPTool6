package edu.asu.plp.tool.prototype;

import java.util.ArrayList;
import java.util.List;

import com.faeysoft.preceptor.lexer.LexException;
import com.faeysoft.preceptor.lexer.Lexer;
import com.faeysoft.preceptor.lexer.Token;
import com.faeysoft.preceptor.lexer.TokenType;
import com.faeysoft.preceptor.lexer.TokenTypeSet;

public class PLPLabel
{
	private String name;
	private int lineNumber;
	
	public PLPLabel(String name, int lineNumber)
	{
		super();
		this.name = name;
		this.lineNumber = lineNumber;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
}
