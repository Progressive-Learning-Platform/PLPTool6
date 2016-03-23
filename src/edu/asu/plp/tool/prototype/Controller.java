package edu.asu.plp.tool.prototype;

public interface Controller
{
	// Project Management
	void createNewProject();
	
	void openProject();
	
	void saveActiveProject();
	
	void saveActiveProjectAs();
	
	void printActiveFile();
	
	void createNewASM();
	
	void importASM();
	
	void exportASM();
	
	void removeASM();
	
	void setMainASMFile();
	
	// Application Controls
	void exit();
	
	// Application Controls :: View Manipulation
	void toggleToolbar();
	
	void toggleProjectPane();
	
	void toggleOutputPane();
	
	void clearConsole();
	
	void clearAllBreakpoints();
	
	// Utilities
	void showNumberConverter();
	
	// Backend Interaction
	void assembleActiveProject();
	
	void simulateActiveProject();
	
	void downloadActiveProjectToBoard();
	
	void stepSimulation();
	
	void triggerSimulationInterrupt();
	
	void resetSimulation();
	
	void runSimulation();
	
	void changeSimulationSpeed(int requestedSpeed);
	
	void stopSimulation();
	
	// IDE-Specific Emulators
	void showCPUView();
	
	void showWatcherWindow();
	
	void showLEDEmulator();
	
	void showSwitchesEmulator();
	
	void showSevenSegmentEmulator();
	
	void showUARTEmulator();
	
	void showVGAEmulator();
	
	void showPLPIDEmulator();
	
	void showGPIOEmulator();
	
	void showOptionsMenu();
	
	void showModuleManager();
	
	void showSerialTerminal();
	
	// --
	void loadModule();
	
	void clearModuleCache();
	
	// Meta-User Interactions
	void showQuickReference();
	
	void showOnlineManual();
	
	void reportIssue();
	
	void showIssuesPage();
	
	void showAboutPLPTool();
	
	void showThirdPartyLicenses();
}
