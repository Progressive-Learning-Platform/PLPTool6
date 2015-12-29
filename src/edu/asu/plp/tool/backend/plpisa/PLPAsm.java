package edu.asu.plp.tool.backend.plpisa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import edu.asu.plp.tool.backend.util.FileUtil;

public class PLPAsm
{
	private String asmFilePath;
	private String asmContents;
	private List<String> asmLines;
	
	public PLPAsm(String asmFilePath) throws IOException
	{
		this(FileUtil.readAllLines(asmFilePath), asmFilePath);
	}
	
	public PLPAsm(String asmContents, String asmFilePath) throws IOException
	{
		if (asmContents == null && asmFilePath != null)
		{
			setAsmFilePath(asmFilePath, true);
		}
		else
		{
			this.asmContents = asmContents;
			this.asmFilePath = asmFilePath;
			this.asmLines = Arrays.asList(asmContents.split("\n"));
		}
		
		
		
	}
	
	public boolean loadFromFile()
	{
		return loadFromFile(asmFilePath);
	}
	
	private boolean loadFromFile(String asmFilePath)
	{
		if (FileUtil.isAsmFile(asmFilePath))
		{
			File asmFile = new File(asmFilePath);
			
			try
			{
				this.asmLines = Files.readAllLines(asmFile.toPath());
				this.asmContents = String.join("\n", this.asmLines);
				return true;
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		return false;
	}
	
	public String getAsmFilePath()
	{
		return asmFilePath;
	}
	
	/**
	 * Will set the filePath to the one specified and overwrite the current file contents
	 * stored in this class with the ones from the newly specified file.
	 * 
	 * @param asmFilePath
	 */
	public void setAsmFilePath(String asmFilePath)
	{
		setAsmFilePath(asmFilePath, true);
	}
	
	/**
	 * Sets the reference path to the one specified.
	 * 
	 * @param asmFilePath
	 * @param overwriteCurrent
	 *            If overwrite current is true, it will overwrite the current contents of
	 *            this class.
	 */
	public void setAsmFilePath(String asmFilePath, boolean overwriteCurrent)
	{
		this.asmFilePath = asmFilePath;
		if (overwriteCurrent)
		{
			loadFromFile(asmFilePath);
		}
	}
	
	public String getAsmContents()
	{
		return asmContents;
	}
	
	public void setAsmContents(String asmContents)
	{
		this.asmContents = asmContents;
		this.asmLines = Arrays.asList(asmContents.split("\n"));
	}
	
	/**
	 * Retrieve a Line from the ASM file.
	 * 
	 * @param lineNumber
	 *            Follows the file format, 1-n.
	 * @return If the file contains that line number, returns and that string wrapped in
	 *         an optional. Otherwise it returns an empty optional
	 */
	public Optional<String> getAsmLine(int lineNumber)
	{
		if (lineNumber > asmLines.size() || lineNumber <= 0)
			return Optional.empty();
		return Optional.of(asmLines.get(lineNumber - 1));
	}
	
	public List<String> getAsmLines()
	{
		return asmLines;
	}
	
	@Override
	public String toString()
	{
		return "PLPAsm: " + asmFilePath;
	}
	
}