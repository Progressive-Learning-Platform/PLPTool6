package edu.asu.plp.tool.backend.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Joiner;

import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.UnitSize;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.backend.plpisa.assembler.PLPAssembler;

public class AssembleConsole
{
	protected static String assemblerName;
	protected static Assembler assembler;
	
	protected static CommandLine commandLine;
	protected static Options options;
	
	protected static File assembleFile;
	protected static boolean isProject;
	
	protected static boolean isBenchMarking;
	protected static StringJoiner fileJoiner;
	
	// Sample Projects
	protected static HashMap<String, String> exampleProjects;
	
	public static void main(String[] args)
	{
		configureStaticSettings();
		
		initializeCommandLineOptions();
		
		parseCLIArguments(args);
		
		configureEnteredSettings();
		
		long startTime = System.nanoTime();
		
		try
		{
			assembler = new PLPAssembler(assembleFile.getPath());
			assembler.assemble();
		}
		catch (IOException | AssemblerException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		long endTime = System.nanoTime();
		
		if (isBenchMarking)
			System.out.println(
					String.format("It took: %.2f seconds", (endTime - startTime) * 1e-9));
	}
	
	private static void configureStaticSettings()
	{
		UnitSize.initializeDefaultValues();
		exampleProjects = new HashMap<>();
		
		exampleProjects.put("directives",
				"examples/Stripped PLP Projects (ASM Only)/gpio_test/main.asm");
		exampleProjects.put("length",
				"examples/Stripped PLP Projects (ASM Only)/universe/length.asm");
	}
	
	private static void initializeCommandLineOptions()
	{
		options = new Options();
		options.addOption("h", "help", false, "show help");
		options.addOption("b", "benchmark", false, "enable benchmark timing ouput");
		options.addOption("a", "assembler", true,
				"set assembler from choices: plp, mips");
		options.addOption("p", "project", true, "set project path to assemble");
		options.addOption("f", "file", true, "set path of a single asm file to assemble");
		options.addOption("e", "example", true, "set example from choices: "
				+ Joiner.on(", ").join(exampleProjects.keySet()));
	}
	
	private static void parseCLIArguments(String[] args)
	{
		CommandLineParser parser = new DefaultParser();
		
		try
		{
			commandLine = parser.parse(options, args);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private static void configureEnteredSettings()
	{
		if (commandLine.hasOption("h"))
			printHelp();
			
		if (commandLine.hasOption("b"))
			isBenchMarking = true;
			
		if (commandLine.hasOption("a"))
		{
			assemblerName = commandLine.getOptionValue("a").toLowerCase();
			if (!assemblerName.equalsIgnoreCase("plp"))
			{
				System.out.println("Only assembler currently supported is plp.");
				System.exit(-1);
			}
		}
		
		if (commandLine.hasOption("p"))
		{
			// TODO enforce correct project ending relative to project type (.plp for plp)
			// TODO parse project into list of asm files
			assembleFile = new File(commandLine.getOptionValue("p"));
			System.out.println("Projects are not currently supported");
			System.exit(-1);
		}
		else if (commandLine.hasOption("f"))
		{
			// TODO enforce correct file type (.asm for plp)
			assembleFile = new File(commandLine.getOptionValue("f"));
		}
		else if (commandLine.hasOption("e"))
		{
			String exampleName = commandLine.getOptionValue("e").toLowerCase();
			if (exampleProjects.containsKey(exampleName))
			{
				assembleFile = new File(exampleProjects.get(exampleName));
			}
			else
			{
				System.out.println(
						"Unknown example was entered, found: " + exampleName + ".");
				System.out.println(
						"Please see the help (via -h or -help) for possible examples.");
				System.exit(-1);
			}
		}
		else
		{
			System.out.println("No project, file, or example specified to assemble.");
			System.exit(0);
		}
	}
	
	private static void printHelp()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Assembler Console",
				"Your one stop shop, when you are lost and don't know what to do!",
				options, "Thank you for using Assembler Console!");
		System.exit(0);
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
		
		if (fileLines != null)
		{
			fileJoiner = new StringJoiner("\n");
			for (int index = 0; index < fileLines.size(); index++)
			{
				fileJoiner.add(fileLines.get(index));
			}
			return fileJoiner.toString();
		}
		
		return null;
	}
}
