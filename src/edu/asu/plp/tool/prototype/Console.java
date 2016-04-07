package edu.asu.plp.tool.prototype;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//This whole Console.java will be updated

public class Console
{
	private Controller controller;
	Map<String, Command> plpCommands;
	
	public Console(Controller controller)
	{
		this.controller = controller;
		plpCommands = createCommands();
		
		plpCommands.get("list commands").execute();
		
		boolean running = false;
		while (running)
		{
			String cmd = getInput();
			System.out.println(cmd + " was entered.");
			if (!plpCommands.containsKey(cmd))
			{
				System.out.println(cmd
						+ " was an invalid command. Enter: List Commands for all commands.");
			}
			else
			{
				plpCommands.get(cmd).execute();
			}
		}
	}
	
	private interface Command
	{
		void execute();
		
	}
	
	private Map<String, Command> createCommands()
	{
		Map<String, Command> commands = new HashMap<String, Command>();
		
		commands.put("assemble", new Command() {
			public void execute()
			{
				controller.assembleActiveProject();
			}
		});
		commands.put("change simulation speed", new Command() {
			public void execute()
			{
				System.out.println("What Speed Would you like?");
				int requestedSpeed = Integer.parseInt(getInput());
				controller.changeSimulationSpeed(requestedSpeed);
			}
		});
		commands.put("clear all breakpoints", new Command() {
			public void execute()
			{
				controller.clearAllBreakpoints();
			}
		});
		commands.put("clear console", new Command() {
			public void execute()
			{
				controller.clearConsole();
			}
		});
		commands.put("clear module cache", new Command() {
			public void execute()
			{
				controller.clearModuleCache();;
			}
		});
		commands.put("create new asm", new Command() {
			public void execute()
			{
				controller.createNewASM();
			}
		});
		commands.put("create new project", new Command() {
			public void execute()
			{
				controller.createNewProject();
			}
		});
		commands.put("download active project to board", new Command() {
			public void execute()
			{
				controller.downloadActiveProjectToBoard();
			}
		});
		commands.put("exit", new Command() {
			public void execute()
			{
				controller.exit();
			}
		});
		commands.put("export asm", new Command() {
			public void execute()
			{
				controller.exportASM();
			}
		});
		commands.put("import asm", new Command() {
			public void execute()
			{
				controller.importASM();
			}
		});
		commands.put("load module", new Command() {
			public void execute()
			{
				controller.loadModule();
			}
		});
		commands.put("open cpu view", new Command() {
			public void execute()
			{
				controller.openCpuViewWindow();
			}
		});
		commands.put("open project", new Command() {
			public void execute()
			{
				controller.openProject();
			}
		});
		commands.put("print active file", new Command() {
			public void execute()
			{
				controller.printActiveFile();
			}
		});
		commands.put("remove asm", new Command() {
			public void execute()
			{
				controller.removeASM();
			}
		});
		commands.put("report issue", new Command() {
			public void execute()
			{
				controller.reportIssue();
			}
		});
		commands.put("reset simulation", new Command() {
			public void execute()
			{
				controller.resetSimulation();
			}
		});
		commands.put("run", new Command() {
			public void execute()
			{
				controller.runSimulation();
			}
		});
		commands.put("save active project", new Command() {
			public void execute()
			{
				controller.saveActiveProject();
			}
		});
		commands.put("save active project as", new Command() {
			public void execute()
			{
				controller.saveActiveProjectAs();
			}
		});
		commands.put("save all", new Command() {
			public void execute()
			{
				controller.saveAll();
			}
		});
		commands.put("set main asm file", new Command() {
			public void execute()
			{
				controller.setMainASMFile();
			}
		});
		commands.put("show about plp tool", new Command() {
			public void execute()
			{
				controller.showAboutPLPTool();
			}
		});
		commands.put("show issues page", new Command() {
			public void execute()
			{
				controller.showIssuesPage();
			}
		});
		commands.put("show module manager", new Command() {
			public void execute()
			{
				controller.showModuleManager();
			}
		});
		commands.put("show number converter", new Command() {
			public void execute()
			{
				controller.showNumberConverter();
			}
		});
		commands.put("show number converter", new Command() {
			public void execute()
			{
				controller.showOnlineManual();
			}
		});
		commands.put("show online manual", new Command() {
			public void execute()
			{
				controller.showOnlineManual();
			}
		});
		commands.put("show options menu", new Command() {
			public void execute()
			{
				controller.showOptionsMenu();
			}
		});
		commands.put("show quick reference", new Command() {
			public void execute()
			{
				controller.showQuickReference();
			}
		});
		commands.put("show third party license", new Command() {
			public void execute()
			{
				controller.showThirdPartyLicenses();
			}
		});
		commands.put("show watcher window", new Command() {
			public void execute()
			{
				controller.showWatcherWindow();
			}
		});
		commands.put("sim", new Command() {
			public void execute()
			{
				controller.simulateActiveProject();
			}
		});
		commands.put("step", new Command() {
			public void execute()
			{
				controller.stepSimulation();
			}
		});
		commands.put("stop", new Command() {
			public void execute()
			{
				controller.stopSimulation();
			}
		});
		commands.put("trigger simulation interrupt", new Command() {
			public void execute()
			{
				controller.triggerSimulationInterrupt();
			}
		});
		commands.put("list commands", new Command() {
			public void execute()
			{
				for (String key : commands.keySet())
				{
					System.out.println(key);
				}
			}
		});
		
		return commands;
	}
	
	private String getInput()
	{
		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		input = input.toLowerCase();
		return input;
	}
	
}
