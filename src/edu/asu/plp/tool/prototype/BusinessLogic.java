package edu.asu.plp.tool.prototype;

import javafx.event.ActionEvent;

public interface BusinessLogic
{
	void onCreateNewProject(ActionEvent event);
	
	void onOpenProject(ActionEvent event);
	
	void onSaveProject(ActionEvent event);
	
	void onSaveProjectAs(ActionEvent event);
	
	void onPrint(ActionEvent event);
	
	void onExit(ActionEvent event);
	
	void onCopy(ActionEvent event);
	
	void onCut(ActionEvent event);
	
	void onPaste(ActionEvent event);
	
	void onFindAndReplace(ActionEvent event);
	
	void onUndo(ActionEvent event);
	
	void onRedo(ActionEvent event);
	
	void onToggleToolbar(ActionEvent event);
	
	void onToggleProjectPane(ActionEvent event);
	
	void onToggleOutputPane(ActionEvent event);
	
	void onClearOutputPane(ActionEvent event);
	
	void onAssemble(ActionEvent event);
	
	void onSimulate(ActionEvent event);
	
	void onDownloadToBoard(ActionEvent event);
	
	void onNewASMFile(ActionEvent event);
	
	void onImportASMFile(ActionEvent event);
	
	void onExportASMFile(ActionEvent event);
	
	void onRemoveASMFile(ActionEvent event);
	
	void onSetMainASMFile(ActionEvent event);
	
	void onOpenQuickReference(ActionEvent event);
	
	void onOpenOnlineManual(ActionEvent event);
	
	void onOpenIssueReport(ActionEvent event);
	
	void onOpenIssuesPage(ActionEvent event);
	
	void onAboutPLPToolPanel(ActionEvent event);
	
	void onOpenThirdPartyLicenses(ActionEvent event);
	
	void onSimulationStep(ActionEvent event);
	
	void onResetSimulation(ActionEvent event);
	
	void onRunSimulation(ActionEvent event);
	
	void onChangeSimulationSpeed(ActionEvent event, int requestedSpeed);
	
	void onClearBreakpoints(ActionEvent event);
	
	void onOpenCPUView(ActionEvent event);
	
	void onOpenWatcherWindow(ActionEvent event);
	
	void onDisplayLEDEmulator(ActionEvent event);
	
	void onDisplaySwitchesEmulator(ActionEvent event);
	
	void onDisplaySevenSegmentEmulator(ActionEvent event);
	
	void onDisplayUARTEmulator(ActionEvent event);
	
	void onDisplayVGAEmulator(ActionEvent event);
	
	void onDisplayPLPIDEmulator(ActionEvent event);
	
	void onDisplayGPIOEmulator(ActionEvent event);
	
	void onStopSimulation(ActionEvent event);
	
	void onOpenOptionsMenu(ActionEvent event);
	
	void onOpenModuleManager(ActionEvent event);
	
	void onLoadModule(ActionEvent event);
	
	void onClearModuleCache(ActionEvent event);
	
	void onOpenSerialTerminal(ActionEvent event);
	
	void onOpenNumberConverter(ActionEvent event);
}
