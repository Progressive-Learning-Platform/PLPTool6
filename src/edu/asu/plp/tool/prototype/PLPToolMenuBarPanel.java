package edu.asu.plp.tool.prototype;

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
	private BusinessLogic businessLogic;
	
	public PLPToolMenuBarPanel(BusinessLogic businessLogic)
	{
		this.businessLogic = businessLogic;
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
		itemOptions.setOnAction(businessLogic::onOpenOptionsMenu);
		
		Menu modules = new Menu("Modules");
		MenuItem itemModuleManager = new MenuItem("Module Manager...");
		itemModuleManager.setOnAction(businessLogic::onOpenModuleManager);
		
		MenuItem itemLoadJar = new MenuItem("Load Module JAR File...");
		itemLoadJar.setOnAction(businessLogic::onLoadModule);
		
		MenuItem itemClearCache = new MenuItem("Clear Module Auto-Load Cache");
		itemClearCache.setOnAction(businessLogic::onClearModuleCache);
		
		MenuItem itemSerialTerminal = new MenuItem("Serial Terminal");
		itemSerialTerminal.setAccelerator(new KeyCodeCombination(KeyCode.T,
				KeyCombination.CONTROL_DOWN));
		itemSerialTerminal.setOnAction(businessLogic::onOpenSerialTerminal);
		
		MenuItem itemNumConverter = new MenuItem("Number Converter");
		itemNumConverter.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		itemNumConverter.setOnAction(businessLogic::onOpenNumberConverter);
		
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
		itemStep.setOnAction(businessLogic::onSimulationStep);
		
		MenuItem itemReset = new MenuItem("Reset");
		itemReset.setGraphic(new ImageView(new Image("toolbar_reset.png")));
		itemReset.setAccelerator(new KeyCodeCombination(KeyCode.F9));
		itemReset.setOnAction(businessLogic::onResetSimulation);
		
		MenuItem itemRun = new MenuItem("Run");
		itemRun.setAccelerator(new KeyCodeCombination(KeyCode.F7));
		itemRun.setOnAction(businessLogic::onRunSimulation);
		
		Menu cyclesSteps = new Menu("Cycles/Steps");
		MenuItem itemOne = new MenuItem("1");
		itemOne.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD1,
				KeyCombination.ALT_DOWN));
		itemOne.setOnAction((event) -> businessLogic.onChangeSimulationSpeed(event, 1));
		
		MenuItem itemFive = new MenuItem("5");
		itemFive.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD2,
				KeyCombination.ALT_DOWN));
		itemFive.setOnAction((event) -> businessLogic.onChangeSimulationSpeed(event, 5));
		
		MenuItem itemTwenty = new MenuItem("20");
		itemTwenty.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD3,
				KeyCombination.ALT_DOWN));
		itemTwenty.setOnAction((event) -> businessLogic.onChangeSimulationSpeed(event, 20));
		
		MenuItem itemHundred = new MenuItem("100");
		itemHundred.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD4,
				KeyCombination.ALT_DOWN));
		itemHundred.setOnAction((event) -> businessLogic.onChangeSimulationSpeed(event, 100));
		
		MenuItem itemFiveThousand = new MenuItem("5000");
		itemFiveThousand.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD5,
				KeyCombination.ALT_DOWN));
		itemFiveThousand.setOnAction((event) -> businessLogic.onChangeSimulationSpeed(event, 5000));
		
		MenuItem itemClearBreakpoints = new MenuItem("Clear Breakpoints");
		itemClearBreakpoints.setAccelerator(new KeyCodeCombination(KeyCode.B,
				KeyCombination.CONTROL_DOWN));
		itemClearBreakpoints.setOnAction(businessLogic::onClearBreakpoints);
		
		Menu views = new Menu("Views");
		MenuItem itemCpuView = new MenuItem("CPU View");
		itemCpuView.setAccelerator(new KeyCodeCombination(KeyCode.C,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuView.setOnAction(businessLogic::onOpenCPUView);
		
		MenuItem itemCpuWindow = new MenuItem("Watcher Window");
		itemCpuWindow.setAccelerator(new KeyCodeCombination(KeyCode.W,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuWindow.setOnAction(businessLogic::onOpenWatcherWindow);
		
		MenuItem itemSimControlWindow = new MenuItem("Simulation Control Window");
		itemSimControlWindow.setAccelerator(new KeyCodeCombination(KeyCode.R,
				KeyCombination.CONTROL_DOWN));
		// TODO: itemSimControlWindow.setOnAction(); {{what does this button do?}}
		
		Menu toolsSubMenu = new Menu("Tools");
		MenuItem itemioRegistry = new MenuItem("I/O Registry");
		itemioRegistry.setAccelerator(new KeyCodeCombination(KeyCode.R,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		// TODO: itemioRegistry.setOnAction(); {{what does this button do?}}
		
		MenuItem itemASMView = new MenuItem("ASM View");
		// TODO: itemASMView.setOnAction(); {{what does this button do?}}
		
		MenuItem itemCreateMemVis = new MenuItem("Create a PLP CPU Memory Visualizer");
		// TODO: itemCreateMemVis.setOnAction(); {{what does this button do?}}
		
		MenuItem itemRemoveMemVis = new MenuItem("Remove Memory Visualizers from Project");
		// TODO: itemRemoveMemVis.setOnAction(); {{what does this button do?}}
		
		MenuItem itemDisplayBus = new MenuItem("Display Bus Monitor Timing Diagram");
		// TODO: itemDisplayBus.setOnAction(); {{what does this button do?}}
		
		// FIXME: These emulators depend on the context of the project. 
		// FIXME: Not all ISAs support the same device emulators...
		Menu ioDevices = new Menu("I/O Devices");
		MenuItem itemLedArray = new MenuItem("LED Array");
		itemLedArray.setGraphic(new ImageView(new Image("toolbar_sim_leds.png")));
		itemLedArray.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD1,
				KeyCombination.CONTROL_DOWN));
		itemLedArray.setOnAction(businessLogic::onDisplayLEDEmulator);
		
		MenuItem itemSwitches = new MenuItem("Switches");
		itemSwitches.setGraphic(new ImageView(new Image("toolbar_sim_switches.png")));
		itemSwitches.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD2,
				KeyCombination.CONTROL_DOWN));
		itemSwitches.setOnAction(businessLogic::onDisplaySwitchesEmulator);
		
		MenuItem itemSevenSeg = new MenuItem("Seven Segments");
		itemSevenSeg.setGraphic(new ImageView(new Image("toolbar_sim_7segments.png")));
		itemSevenSeg.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD3,
				KeyCombination.CONTROL_DOWN));
		itemSevenSeg.setOnAction(businessLogic::onDisplaySevenSegmentEmulator);
		
		MenuItem itemUART = new MenuItem("UART");
		itemUART.setGraphic(new ImageView(new Image("toolbar_sim_uart.png")));
		itemUART.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD4,
				KeyCombination.CONTROL_DOWN));
		itemUART.setOnAction(businessLogic::onDisplayUARTEmulator);
		
		MenuItem itemVGA = new MenuItem("VGA");
		itemVGA.setGraphic(new ImageView(new Image("toolbar_sim_vga.png")));
		itemVGA.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD5,
				KeyCombination.CONTROL_DOWN));
		itemVGA.setOnAction(businessLogic::onDisplayVGAEmulator);
		
		MenuItem itemPLPID = new MenuItem("PLPID");
		itemPLPID.setGraphic(new ImageView(new Image("toolbar_sim_plpid.png")));
		itemPLPID.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD6,
				KeyCombination.CONTROL_DOWN));
		itemPLPID.setOnAction(businessLogic::onDisplayPLPIDEmulator);
		
		MenuItem itemGPIO = new MenuItem("GPIO");
		itemGPIO.setGraphic(new ImageView(new Image("toolbar_sim_gpio.png")));
		itemGPIO.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD7,
				KeyCombination.CONTROL_DOWN));
		itemGPIO.setOnAction(businessLogic::onDisplayGPIOEmulator);
		
		MenuItem itemExitSim = new MenuItem("ExitSimulation");
		itemExitSim.setAccelerator(new KeyCodeCombination(KeyCode.F11));
		itemExitSim.setOnAction(businessLogic::onStopSimulation);
		
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
		itemQuickRef.setOnAction(businessLogic::onOpenQuickReference);
		
		MenuItem itemOnlineManual = new MenuItem("Online Manual");
		itemOnlineManual.setOnAction(businessLogic::onOpenOnlineManual);
		
		MenuItem itemReportIssue = new MenuItem("Report Issue (Requires Google Account");
		itemReportIssue.setOnAction(businessLogic::onOpenIssueReport);
		
		// FIXME: should be "Open Issues Page"
		// FIXME: should open a GitHub page (not Google Code)
		// FIXME: the host provider of the issues page may change in the future
		MenuItem itemGoogleIssues = new MenuItem("Open Google Code Issues Page");
		itemGoogleIssues.setOnAction(businessLogic::onOpenIssuesPage);
		
		MenuItem itemAboutPLP = new MenuItem("About PLP Tool...");
		itemAboutPLP.setOnAction(businessLogic::onAboutPLPToolPanel);
		
		MenuItem itemSWLicense = new MenuItem("Third Party Software License");
		itemSWLicense.setOnAction(businessLogic::onOpenThirdPartyLicenses);
		
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
		itemAssemble.setOnAction(businessLogic::onAssemble);
		
		MenuItem itemSimulate = new MenuItem("Simulate");
		itemSimulate.setGraphic(new ImageView(new Image("toolbar_simulate.png")));
		itemSimulate.setAccelerator(new KeyCodeCombination(KeyCode.F3));
		itemSimulate.setOnAction(businessLogic::onSimulate);
		
		MenuItem itemPLPBoard = new MenuItem("Program PLP Board...");
		itemPLPBoard.setGraphic(new ImageView(new Image("toolbar_program.png")));
		itemPLPBoard.setAccelerator(new KeyCodeCombination(KeyCode.F4,
				KeyCombination.SHIFT_DOWN));
		itemPLPBoard.setOnAction(businessLogic::onDownloadToBoard);
		
		MenuItem itemQuickProgram = new MenuItem("Quick Program");
		itemQuickProgram.setAccelerator(new KeyCodeCombination(KeyCode.F4));
		// TODO: itemQuickProgram.setOnAction(); {{what does this button do?}}
		
		MenuItem itemNewASM = new MenuItem("New ASM File...");
		itemNewASM.setOnAction(businessLogic::onNewASMFile);
		
		MenuItem itemImportASM = new MenuItem("Import ASM File...");
		itemImportASM.setOnAction(businessLogic::onImportASMFile);
		
		MenuItem itemExportASM = new MenuItem("Export Selected ASM File...");
		itemExportASM.setOnAction(businessLogic::onExportASMFile);
		
		MenuItem itemRemoveASM = new MenuItem("Remove Selected ASM File from Project");
		itemRemoveASM.setAccelerator(new KeyCodeCombination(KeyCode.E,
				KeyCombination.CONTROL_DOWN));
		itemRemoveASM.setOnAction(businessLogic::onRemoveASMFile);
		
		MenuItem itemCurrentAsMain = new MenuItem("Set Current Open File as Main Program");
		itemCurrentAsMain.setOnAction(businessLogic::onSetMainASMFile);
		
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
		cItemToolbar.setOnAction(businessLogic::onToggleToolbar);
		
		CheckMenuItem cItemProjectPane = new CheckMenuItem("Project Pane");
		cItemProjectPane.setAccelerator(new KeyCodeCombination(KeyCode.P,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemProjectPane.setOnAction(businessLogic::onToggleProjectPane);
		
		CheckMenuItem cItemOutputPane = new CheckMenuItem("Output Pane");
		cItemOutputPane.setAccelerator(new KeyCodeCombination(KeyCode.O,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemOutputPane.setOnAction(businessLogic::onToggleOutputPane);
		
		MenuItem itemClearOutput = new MenuItem("Clear Output Pane");
		itemClearOutput.setAccelerator(new KeyCodeCombination(KeyCode.D,
				KeyCombination.CONTROL_DOWN));
		itemClearOutput.setOnAction(businessLogic::onClearOutputPane);
		
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
		
		MenuItem itemCut = new MenuItem("Cut");
		itemCut.setAccelerator(new KeyCodeCombination(KeyCode.X,
				KeyCombination.CONTROL_DOWN));
		
		MenuItem itemPaste = new MenuItem("Paste");
		itemPaste.setAccelerator(new KeyCodeCombination(KeyCode.V,
				KeyCombination.CONTROL_DOWN));
		
		MenuItem itemFandR = new MenuItem("Find and Replace");
		itemFandR.setAccelerator(new KeyCodeCombination(KeyCode.F,
				KeyCombination.CONTROL_DOWN));
		
		MenuItem itemUndo = new MenuItem("Undo");
		itemUndo.setAccelerator(new KeyCodeCombination(KeyCode.Z,
				KeyCombination.CONTROL_DOWN));
		
		MenuItem itemRedo = new MenuItem("Redo");
		itemRedo.setAccelerator(new KeyCodeCombination(KeyCode.Y,
				KeyCombination.CONTROL_DOWN));
		
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
		itemNew.setOnAction(businessLogic::onCreateNewProject);
		
		// FIXME: Open PLP Project may be a misnomer; can the project be any ISA?
		MenuItem itemOpen = new MenuItem("Open PLP Project");
		itemOpen.setGraphic(new ImageView(new Image("toolbar_open.png")));
		itemOpen.setAccelerator(new KeyCodeCombination(KeyCode.O,
				KeyCombination.CONTROL_DOWN));
		itemOpen.setOnAction(businessLogic::onOpenProject);
		
		MenuItem itemSave = new MenuItem("Save");
		itemSave.setGraphic(new ImageView(new Image("toolbar_save.png")));
		itemSave.setAccelerator(new KeyCodeCombination(KeyCode.S,
				KeyCombination.CONTROL_DOWN));
		itemSave.setOnAction(businessLogic::onSaveProject);
		
		MenuItem itemSaveAs = new MenuItem("Save As");
		itemSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.A,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemSaveAs.setOnAction(businessLogic::onSaveProjectAs);
		
		MenuItem itemPrint = new MenuItem("Print");
		itemPrint.setAccelerator(new KeyCodeCombination(KeyCode.P,
				KeyCombination.CONTROL_DOWN));
		itemPrint.setOnAction(businessLogic::onPrint);
		
		MenuItem itemExit = new MenuItem("Exit");
		itemExit.setAccelerator(new KeyCodeCombination(KeyCode.Q,
				KeyCombination.CONTROL_DOWN));
		itemExit.setOnAction(businessLogic::onExit);
		
		fileMenu.getItems().addAll(itemNew, new SeparatorMenuItem(), itemOpen, itemSave,
				itemSaveAs, new SeparatorMenuItem(), itemPrint, new SeparatorMenuItem(),
				itemExit);
		
		return fileMenu;
	}
}
