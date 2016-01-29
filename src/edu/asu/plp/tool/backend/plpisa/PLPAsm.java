package edu.asu.plp.tool.backend.plpisa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.util.FileUtil;

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
