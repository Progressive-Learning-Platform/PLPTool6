package edu.asu.plp.tool.prototype;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

public interface BusinessLogic
{
	void onCreateNewProject(ActionEvent event);
	
	void onCreateNewProject(MouseEvent event);
	
	void onOpenProject(ActionEvent event);
	
	void onOpenProject(MouseEvent event);
	
	void onSaveProject(ActionEvent event);
	
	void onSaveProject(MouseEvent event);
	
	void onSaveProjectAs(ActionEvent event);
	
	void onSaveProjectAs(MouseEvent event);
	
	void onPrint(ActionEvent event);
	
	void onExit(ActionEvent event);
	
	void onToggleToolbar(ActionEvent event);
	
	void onToggleProjectPane(ActionEvent event);
	
	void onToggleOutputPane(ActionEvent event);
	
	void onClearOutputPane(ActionEvent event);
	
	void onAssemble(ActionEvent event);
	
	void onAssemble(MouseEvent event);
	
	void onSimulate(ActionEvent event);
	
	void onSimulate(MouseEvent event);
	
	void onDownloadToBoard(ActionEvent event);
	
	void onNewASMFile(ActionEvent event);
	
	void onNewASMFile(MouseEvent event);
	
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
	
	void onSimulationStep(MouseEvent event);
	
	void onSimulationInterrupt(MouseEvent event);
	
	void onResetSimulation(ActionEvent event);
	
	void onResetSimulation(MouseEvent event);
	
	void onRunSimulation(ActionEvent event);
	
	void onRunSimulation(MouseEvent event);
	
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
	
	void onOpenCPUView(MouseEvent event);
	
	void onOpenWatcherWindow(MouseEvent event);
	
	void onDisplayLEDEmulator(MouseEvent event);
	
	void onDisplaySwitchesEmulator(MouseEvent event);
	
	void onDisplaySevenSegmentEmulator(MouseEvent event);
	
	void onDisplayUARTEmulator(MouseEvent event);
	
	void onDisplayVGAEmulator(MouseEvent event);
	
	void onDisplayPLPIDEmulator(MouseEvent event);
	
	void onDisplayGPIOEmulator(MouseEvent event);
	
	void onStopSimulation(ActionEvent event);
	
	void onOpenOptionsMenu(ActionEvent event);
	
	void onOpenModuleManager(ActionEvent event);
	
	void onLoadModule(ActionEvent event);
	
	void onClearModuleCache(ActionEvent event);
	
	void onOpenSerialTerminal(ActionEvent event);
	
	void onOpenNumberConverter(ActionEvent event);
}
