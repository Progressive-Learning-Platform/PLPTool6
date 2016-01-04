package edu.asu.plp.tool.backend.plpisa.assembler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.Timer;

import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.UnitSize;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.backend.util.FileUtil;

public class AssembleConsole
{
	static Scanner scanner;
	static Assembler assembler;
	static StringJoiner fileJoiner;
	static boolean isBenchMarking;
	
	public static void main(String[] args)
	{
		boolean running = true;
		isBenchMarking = true;
		scanner = new Scanner(System.in);
		UnitSize.initializeDefaultValues();
		long startTime = System.nanoTime();
		while (running)
		{
			String directive = "D:/Users/Morgan/Documents/Github/plpTool-prototype/examples/ASM Only/gpio_test/main.asm";
			String length = "D:/Users/Morgan/Documents/Github/plpTool-prototype/examples/ASM Only/universe/length.asm";
			//System.out.println("Enter a file to assemble: ");
			//String input = scanner.nextLine();
			
			File file = new File(directive);
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
		long endTime = System.nanoTime();
		System.out.println(String.format("It took: %.2f seconds", (endTime - startTime) * 1e-9));
		
		scanner.close();
		System.out.println("Assemble Console Exited");
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
