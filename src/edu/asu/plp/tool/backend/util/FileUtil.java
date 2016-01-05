package edu.asu.plp.tool.backend.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

public class FileUtil
{
	public static String readAllLines(String filePath) throws IOException
	{
		return readAllLines(new File(filePath));
	}
	
	public static String readAllLines(File file) throws IOException
	{
		return FileUtils.readFileToString(file);
	}
	
	public static boolean isAsmFile(String filePath)
	{
		return isAsmFile(new File(filePath));
	}
	
	public static boolean isAsmFile(File file)
	{
		if(!file.isFile())
			return false;
		
		if(file.getAbsolutePath().endsWith(".asm"))
			return true;
		
		return false;
	}
}
