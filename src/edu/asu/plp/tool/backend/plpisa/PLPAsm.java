package edu.asu.plp.tool.backend.plpisa;

import java.io.IOException;

import edu.asu.plp.tool.backend.isa.ASMFile;

public class PLPAsm extends ASMFile
{
	public PLPAsm(String asmFilePath) throws IOException
	{
		super(asmFilePath);
	}
	
	public PLPAsm(String asmContents, String asmFilePath) throws IOException
	{
		super(asmContents, asmFilePath);
	}
	
	@Override
	public String toString()
	{
		return "PLPAsm: " + asmFilePath;
	}
	
}
