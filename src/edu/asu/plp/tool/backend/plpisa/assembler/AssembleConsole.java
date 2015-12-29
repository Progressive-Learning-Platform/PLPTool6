package edu.asu.plp.tool.backend.plpisa.assembler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

import edu.asu.plp.tool.backend.isa.UnitSize;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;

public class AssembleConsole
{
	static Scanner scanner;
	static PLPAssembler assembler;
	static StringJoiner fileJoiner;
	
	public static void main(String[] args)
	{
		boolean running = true;
		scanner = new Scanner(System.in);
		UnitSize.initializeDefaultValues();
		
		while (running)
		{
			//System.out.println("Enter a file to assemble: ");
			//String input = scanner.nextLine();
			File file = new File("D:/Users/Morgan/Documents/Github/plpTool-prototype/examples/ASM Only/memtest/main.asm");
			if (!file.isFile())
			{
				System.out.println("Path entered is not a file!");
				break;
				//continue;
			}
			
			List<String> fileContents;
			try
			{
				assembler = new PLPAssembler(file.getAbsolutePath());
				assembler.assemble();
			}
			catch (IOException | AssemblerException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			running = false;
		}
		
		scanner.close();
		System.out.println("Console Exited");
	}
	
	private static String getFileContents(Path path)
	{
		List<String> fileLines = null; 
		try
		{
			fileLines = Files.readAllLines(path);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		if(fileLines != null)
		{
			fileJoiner = new StringJoiner("\n");
			for(int index = 0; index < fileLines.size(); index++)
			{
				 fileJoiner.add(fileLines.get(index));
			}
			return fileJoiner.toString();
		}
		
		return null;
	}
}
