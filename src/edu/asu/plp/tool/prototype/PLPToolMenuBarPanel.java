package edu.asu.plp.tool.prototype;

import java.io.File;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.prototype.model.PLPSourceFile;
import edu.asu.plp.tool.prototype.model.Project;
import edu.asu.plp.tool.prototype.util.Dialogues;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;

public class PLPToolMenuBarPanel extends BorderPane
{
	public PLPToolMenuBarPanel()
	{
		MenuBar menuBar = new MenuBar();
		
		Menu file = createFileMenu();
		Menu edit = createEditMenu();
		Menu view = createViewMenu();
		Menu project = createProjectMenu();
		Menu tools = createToolsMenu();
		Menu simulation = createSimulationMenu();
		Menu help = createHelpMenu();
		
		menuBar.getMenus().addAll(file, edit, view, project, tools, simulation, help);
		this.setCenter(menuBar);
	}
	
	private Menu createToolsMenu()
	{
		Menu toolsMenu = new Menu("Tools");
		MenuItem itemOptions = new MenuItem("Options");
		itemOptions.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		Menu modules = new Menu("Modules");
		MenuItem itemModuleManager = new MenuItem("Module Manager...");
		itemModuleManager.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemLoadJar = new MenuItem("Load Module JAR File...");
		itemLoadJar.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemClearCache = new MenuItem("Clear Module Auto-Load Cache");
		itemClearCache.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemSerialTerminal = new MenuItem("Serial Terminal");
		itemSerialTerminal.setAccelerator(new KeyCodeCombination(KeyCode.T,
				KeyCombination.CONTROL_DOWN));
		itemSerialTerminal.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemNumConverter = new MenuItem("Number Converter");
		itemNumConverter.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		itemNumConverter.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		modules.getItems().addAll(itemModuleManager, itemLoadJar, itemClearCache);
		toolsMenu.getItems().addAll(itemOptions, modules, new SeparatorMenuItem(),
				itemSerialTerminal, itemNumConverter);
		
		return toolsMenu;
	}
	
	private Menu createSimulationMenu()
	{
		Menu simulationMenu = new Menu("Simulation");
		MenuItem itemStep = new MenuItem("Step");
		itemStep.setGraphic(new ImageView(new Image("toolbar_step.png")));
		itemStep.setAccelerator(new KeyCodeCombination(KeyCode.F5));
		itemStep.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemReset = new MenuItem("Reset");
		itemReset.setGraphic(new ImageView(new Image("toolbar_reset.png")));
		itemReset.setAccelerator(new KeyCodeCombination(KeyCode.F9));
		itemReset.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemRun = new MenuItem("Run");
		itemRun.setAccelerator(new KeyCodeCombination(KeyCode.F7));
		itemRun.setOnAction(this::onRunProjectClicked);
		
		Menu cyclesSteps = new Menu("Cycles/Steps");
		MenuItem itemOne = new MenuItem("1");
		itemOne.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD1,
				KeyCombination.ALT_DOWN));
		itemOne.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemFive = new MenuItem("5");
		itemFive.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD2,
				KeyCombination.ALT_DOWN));
		itemFive.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemTwenty = new MenuItem("20");
		itemTwenty.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD3,
				KeyCombination.ALT_DOWN));
		itemTwenty.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemHundred = new MenuItem("100");
		itemHundred.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD4,
				KeyCombination.ALT_DOWN));
		itemHundred.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemFiveThousand = new MenuItem("5000");
		itemFiveThousand.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD5,
				KeyCombination.ALT_DOWN));
		itemFiveThousand.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemClearBreakpoints = new MenuItem("Clear Breakpoints");
		itemClearBreakpoints.setAccelerator(new KeyCodeCombination(KeyCode.B,
				KeyCombination.CONTROL_DOWN));
		itemClearBreakpoints.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		Menu views = new Menu("Views");
		MenuItem itemCpuView = new MenuItem("CPU View");
		itemCpuView.setAccelerator(new KeyCodeCombination(KeyCode.C,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuView.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemCpuWindow = new MenuItem("Watcher Window");
		itemCpuWindow.setAccelerator(new KeyCodeCombination(KeyCode.W,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuWindow.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemSimControlWindow = new MenuItem("Simulation Control Window");
		itemSimControlWindow.setAccelerator(new KeyCodeCombination(KeyCode.R,
				KeyCombination.CONTROL_DOWN));
		itemSimControlWindow.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		Menu toolsSubMenu = new Menu("Tools");
		MenuItem itemioRegistry = new MenuItem("I/O Registry");
		itemioRegistry.setAccelerator(new KeyCodeCombination(KeyCode.R,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemioRegistry.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemASMView = new MenuItem("ASM View");
		itemASMView.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemCreateMemVis = new MenuItem("Create a PLP CPU Memory Visualizer");
		itemCreateMemVis.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemRemoveMemVis = new MenuItem("Remove Memory Visualizers from Project");
		itemRemoveMemVis.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemDisplayBus = new MenuItem("Display Bus Monitor Timing Diagram");
		itemDisplayBus.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		Menu ioDevices = new Menu("I/O Devices");
		MenuItem itemLedArray = new MenuItem("LED Array");
		itemLedArray.setGraphic(new ImageView(new Image("toolbar_sim_leds.png")));
		itemLedArray.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD1,
				KeyCombination.CONTROL_DOWN));
		itemLedArray.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemSwitches = new MenuItem("Switches");
		itemSwitches.setGraphic(new ImageView(new Image("toolbar_sim_switches.png")));
		itemSwitches.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD2,
				KeyCombination.CONTROL_DOWN));
		itemSwitches.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemSevenSeg = new MenuItem("Seven Segments");
		itemSevenSeg.setGraphic(new ImageView(new Image("toolbar_sim_7segments.png")));
		itemSevenSeg.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD3,
				KeyCombination.CONTROL_DOWN));
		itemSevenSeg.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemUART = new MenuItem("UART");
		itemUART.setGraphic(new ImageView(new Image("toolbar_sim_uart.png")));
		itemUART.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD4,
				KeyCombination.CONTROL_DOWN));
		itemUART.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemVGA = new MenuItem("VGA");
		itemVGA.setGraphic(new ImageView(new Image("toolbar_sim_vga.png")));
		itemVGA.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD5,
				KeyCombination.CONTROL_DOWN));
		itemVGA.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPLPID = new MenuItem("PLPID");
		itemPLPID.setGraphic(new ImageView(new Image("toolbar_sim_plpid.png")));
		itemPLPID.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD6,
				KeyCombination.CONTROL_DOWN));
		itemPLPID.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemGPIO = new MenuItem("GPIO");
		itemGPIO.setGraphic(new ImageView(new Image("toolbar_sim_gpio.png")));
		itemGPIO.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD7,
				KeyCombination.CONTROL_DOWN));
		itemGPIO.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemExitSim = new MenuItem("ExitSimulation");
		itemExitSim.setAccelerator(new KeyCodeCombination(KeyCode.F11));
		itemExitSim.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		cyclesSteps.getItems().addAll(itemOne, itemFive, itemTwenty, itemHundred,
				itemFiveThousand);
		views.getItems().addAll(itemCpuView, itemCpuWindow, itemSimControlWindow);
		toolsSubMenu.getItems().addAll(itemioRegistry, itemASMView,
				new SeparatorMenuItem(), itemCreateMemVis, itemRemoveMemVis,
				itemDisplayBus);
		ioDevices.getItems().addAll(itemLedArray, itemSwitches, itemSevenSeg, itemUART,
				itemVGA, itemPLPID, itemGPIO);
		simulationMenu.getItems().addAll(itemStep, itemReset, new SeparatorMenuItem(),
				itemRun, cyclesSteps, itemClearBreakpoints, new SeparatorMenuItem(),
				views, toolsSubMenu, ioDevices, new SeparatorMenuItem(), itemExitSim);
		
		return simulationMenu;
	}
	
	private Menu createHelpMenu()
	{
		Menu helpMenu = new Menu("Help");
		MenuItem itemQuickRef = new MenuItem("Quick Reference");
		itemQuickRef.setAccelerator(new KeyCodeCombination(KeyCode.F1));
		itemQuickRef.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemOnlineManual = new MenuItem("Online Manual");
		itemOnlineManual.setOnAction((event) -> {
			onlineManualWeb();
		});
		
		MenuItem itemReportIssue = new MenuItem("Report Issue (Requires Google Account");
		itemReportIssue.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemGoogleIssues = new MenuItem("Open Google Code Issues Page");
		itemGoogleIssues.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemAboutPLP = new MenuItem("About PLP Tool...");
		itemAboutPLP.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemSWLicense = new MenuItem("Third Party Software License");
		itemSWLicense.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		helpMenu.getItems().addAll(itemQuickRef, itemOnlineManual,
				new SeparatorMenuItem(), itemReportIssue, itemGoogleIssues,
				new SeparatorMenuItem(), itemAboutPLP, itemSWLicense);
		
		return helpMenu;
	}
	
	private Menu createProjectMenu()
	{
		Menu projectMenu = new Menu("Project");
		MenuItem itemAssemble = new MenuItem("Assemble");
		itemAssemble.setGraphic(new ImageView(new Image("toolbar_assemble.png")));
		itemAssemble.setAccelerator(new KeyCodeCombination(KeyCode.F2));
		itemAssemble.setOnAction((event) -> {
			console.println("Assemble Menu Item Clicked");
			Project activeProject = getActiveProject();
			assemble(activeProject);
		});
		
		MenuItem itemSimulate = new MenuItem("Simulate");
		itemSimulate.setGraphic(new ImageView(new Image("toolbar_simulate.png")));
		itemSimulate.setAccelerator(new KeyCodeCombination(KeyCode.F3));
		itemSimulate.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemPLPBoard = new MenuItem("Program PLP Board...");
		itemPLPBoard.setGraphic(new ImageView(new Image("toolbar_program.png")));
		itemPLPBoard.setAccelerator(new KeyCodeCombination(KeyCode.F4,
				KeyCombination.SHIFT_DOWN));
		itemPLPBoard.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemQuickProgram = new MenuItem("Quick Program");
		itemQuickProgram.setAccelerator(new KeyCodeCombination(KeyCode.F4));
		itemQuickProgram.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemNewASM = new MenuItem("New ASM File...");
		itemNewASM.setOnAction((event) -> {
			createASMFile(null);
			// TODO: Check this implementation, doesnt look correct
			});
		
		MenuItem itemImportASM = new MenuItem("Import ASM File...");
		itemImportASM.setOnAction((event) -> {
			File importTarget = showImportDialogue();
			try
			{
				String content = FileUtils.readFileToString(importTarget);
				Project activeProject = getActiveProject();
				String name = importTarget.getName();
				
				// TODO: account for non-PLP source files
				ASMFile asmFile = new PLPSourceFile(activeProject, name);
				asmFile.setContent(content);
				activeProject.add(asmFile);
				activeProject.save();
			}
			catch (Exception exception)
			{
				Dialogues.showAlertDialogue(exception, "Failed to import asm");
			}
		});
		
		MenuItem itemExportASM = new MenuItem("Export Selected ASM File...");
		itemExportASM.setOnAction((event) -> {
			ASMFile activeFile = getActiveFile();
			if (activeFile == null)
			{
				// XXX: possible feature: select file from a list or dropdown
				String message = "No file is selected! Open the file you wish to export, or select it in the ProjectExplorer.";
				Dialogues.showInfoDialogue(message);
			}
			
			File exportTarget = showExportDialogue(activeFile);
			if (exportTarget == null)
				return;
			
			if (exportTarget.isDirectory())
			{
				String exportPath = exportTarget.getAbsolutePath()
						+ activeFile.constructFileName();
				exportTarget = new File(exportPath);
				
				String message = "File will be exported to " + exportPath;
				Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
				
				if (result.get() != ButtonType.OK)
				{
					// Export was canceled
					return;
				}
			}
			
			if (exportTarget.exists())
			{
				String message = "The specified file already exists. Press OK to overwrite this file, or cancel to cancel the export.";
				Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
				
				if (result.get() != ButtonType.OK)
				{
					// Export was canceled
					return;
				}
			}
			
			String fileContents = activeFile.getContent();
			try
			{
				FileUtils.write(exportTarget, fileContents);
			}
			catch (Exception exception)
			{
				Dialogues.showAlertDialogue(exception, "Failed to export asm");
			}
		});
		
		MenuItem itemRemoveASM = new MenuItem("Remove Selected ASM File from Project");
		itemRemoveASM.setAccelerator(new KeyCodeCombination(KeyCode.E,
				KeyCombination.CONTROL_DOWN));
		itemRemoveASM.setOnAction((event) -> {
			removeActiveFile();
		});
		
		MenuItem itemCurrentAsMain = new MenuItem("Set Current Open File as Main Program");
		itemCurrentAsMain.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		projectMenu.getItems().addAll(itemAssemble, itemSimulate, itemPLPBoard,
				itemQuickProgram, new SeparatorMenuItem(), itemNewASM, itemImportASM,
				itemExportASM, itemRemoveASM, new SeparatorMenuItem(), itemCurrentAsMain);
		
		return projectMenu;
	}
	
	private Menu createViewMenu()
	{
		Menu viewMenu = new Menu("View");
		CheckMenuItem cItemToolbar = new CheckMenuItem("Toolbar");
		cItemToolbar.setAccelerator(new KeyCodeCombination(KeyCode.T,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemToolbar.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		CheckMenuItem cItemProjectPane = new CheckMenuItem("Project Pane");
		cItemProjectPane.setAccelerator(new KeyCodeCombination(KeyCode.P,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemProjectPane.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		CheckMenuItem cItemOutputPane = new CheckMenuItem("Output Pane");
		cItemOutputPane.setAccelerator(new KeyCodeCombination(KeyCode.O,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemOutputPane.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemClearOutput = new MenuItem("Clear Output Pane");
		itemClearOutput.setAccelerator(new KeyCodeCombination(KeyCode.D,
				KeyCombination.CONTROL_DOWN));
		itemClearOutput.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		viewMenu.getItems().addAll(cItemToolbar, cItemProjectPane, cItemOutputPane,
				itemClearOutput);
		cItemToolbar.setSelected(true);
		cItemProjectPane.setSelected(true);
		cItemOutputPane.setSelected(true);
		
		return viewMenu;
	}
	
	private Menu createEditMenu()
	{
		Menu editMenu = new Menu("Edit");
		MenuItem itemCopy = new MenuItem("Copy");
		itemCopy.setAccelerator(new KeyCodeCombination(KeyCode.C,
				KeyCombination.CONTROL_DOWN));
		itemCopy.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemCut = new MenuItem("Cut");
		itemCut.setAccelerator(new KeyCodeCombination(KeyCode.X,
				KeyCombination.CONTROL_DOWN));
		itemCut.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPaste = new MenuItem("Paste");
		itemPaste.setAccelerator(new KeyCodeCombination(KeyCode.V,
				KeyCombination.CONTROL_DOWN));
		itemPaste.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemFandR = new MenuItem("Find and Replace");
		itemFandR.setAccelerator(new KeyCodeCombination(KeyCode.F,
				KeyCombination.CONTROL_DOWN));
		itemFandR.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemUndo = new MenuItem("Undo");
		itemUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z,
				KeyCombination.CONTROL_DOWN));
		itemUndo.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemRedo = new MenuItem("Redo");
		itemRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y,
				KeyCombination.CONTROL_DOWN));
		itemRedo.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		editMenu.getItems().addAll(itemCopy, itemCut, itemPaste, new SeparatorMenuItem(),
				itemFandR, new SeparatorMenuItem(), itemUndo, itemRedo);
		
		return editMenu;
	}
	
	private Menu createFileMenu()
	{
		Menu fileMenu = new Menu("File");
		MenuItem itemNew = new MenuItem("New PLP Project");
		itemNew.setGraphic(new ImageView(new Image("menu_new.png")));
		itemNew.setAccelerator(new KeyCodeCombination(KeyCode.N,
				KeyCombination.CONTROL_DOWN));
		itemNew.setOnAction((event) -> {
			createNewProject();
		});
		
		MenuItem itemOpen = new MenuItem("Open PLP Project");
		itemOpen.setGraphic(new ImageView(new Image("toolbar_open.png")));
		itemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O,
				KeyCombination.CONTROL_DOWN));
		itemOpen.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSave = new MenuItem("Save");
		itemSave.setGraphic(new ImageView(new Image("toolbar_save.png")));
		itemSave.setAccelerator(new KeyCodeCombination(KeyCode.S,
				KeyCombination.CONTROL_DOWN));
		itemSave.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSaveAs = new MenuItem("Save As");
		itemSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.A,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemSaveAs.setOnAction((event) -> {
			// TODO: Add Event for menu item
				saveProjectAs();
			});
		
		MenuItem itemPrint = new MenuItem("Print");
		itemPrint.setAccelerator(new KeyCodeCombination(KeyCode.P,
				KeyCombination.CONTROL_DOWN));
		itemPrint.setOnAction((event) -> {
			// TODO: Add Event for menu item
			});
		
		MenuItem itemExit = new MenuItem("Exit");
		itemExit.setAccelerator(new KeyCodeCombination(KeyCode.Q,
				KeyCombination.CONTROL_DOWN));
		itemExit.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		fileMenu.getItems().addAll(itemNew, new SeparatorMenuItem(), itemOpen, itemSave,
				itemSaveAs, new SeparatorMenuItem(), itemPrint, new SeparatorMenuItem(),
				itemExit);
		
		return fileMenu;
	}
}
