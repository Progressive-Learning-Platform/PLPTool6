package edu.asu.plp.tool.prototype;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//This whole Console.java will be updated

public class CommandLine
{
	public static void main(String[] args)
	{
		CommandList commandList = new CommandList();
		String[] commandInput;
		
		System.out.println("PLP Command Line:");
		System.out.println("Enter Command or 'Help' for list of commands");
		
		while (true)
		{
			commandInput = getInput();
			System.out.println("User Input was: ");
			for (int i = 0; i < commandInput.length; i++)
			{
				System.out.println(commandInput[i] + " ");
			}
			if (commandList.commands.get(commandInput[0]) == null)
			{
				System.out.println("Invalid command");
			}
			else
			{
				if (commandInput.length > 1)
					commandList.commands.get(commandInput[0]).execute();
				else
					commandList.commands.get(commandInput[0]).execute(commandInput[1]);
					
			}
		}
		
	}
	
	public interface Command
	{
		void execute();
		
		void execute(String args);
		
	}
	
	public static class CommandList
	{
		Map<String, Command> commands = new HashMap<String, Command>();
		
		public CommandList()
		{
			commands.put("test", new Command() {
				public void execute()
				{
					System.out.println("No arg");
				}
				
				@Override
				public void execute(String args)
				{
					System.out.println("This arg: " + args);
				}
			});
			
			commands.put("OpenTextEdit", new Command() {
				public void execute()
				{
					System.out.println("Opening default text editor");
					try
					{
						java.awt.Desktop.getDesktop().edit(new File("Temp.txt"));
					}
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void execute(String args)
				{
					// java.awt.Desktop.getDesktop().edit(args);
				}
			});
			
		}
		
	}
	
	private static String[] getInput()
	{
		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		String[] arguments = input.split("\\s+");
		return arguments;
	}
	
}
