package edu.asu.plp.tool.prototype;

import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class MainToolbar extends BorderPane
{
	public MainToolbar(Controller controller)
	{
		HBox toolbar = new HBox();
		Set<Node> effectsOfButtons = new HashSet<>();
		Set<Node> runButtons = new HashSet<>();
		Set<Node> simButtons = new HashSet<>();
		toolbar.setPadding(new Insets(1.5, 0, 1, 5));
		toolbar.setSpacing(5);
		ObservableList<Node> buttons = toolbar.getChildren();
		
		Node newProjectButton = new ImageView("toolbar_new.png");
		newProjectButton.setOnMouseClicked((e) -> controller.createNewProject());
		buttons.add(newProjectButton);
		effectsOfButtons.add(newProjectButton);
		
		Node newFileButton = new ImageView("menu_new.png");
		newFileButton.setOnMouseClicked((e) -> controller.createNewASM());
		buttons.add(newFileButton);
		effectsOfButtons.add(newFileButton);
		
		Node openButton = new ImageView("toolbar_open.png");
		openButton.setOnMouseClicked((e) -> controller.openProject());
		buttons.add(openButton);
		effectsOfButtons.add(openButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		Node saveButton = new ImageView("toolbar_save.png");
		saveButton.setOnMouseClicked((e) -> controller.saveActiveProject());
		buttons.add(saveButton);
		effectsOfButtons.add(saveButton);
		
		Node assembleButton = new ImageView("toolbar_assemble.png");
		assembleButton.setOnMouseClicked((event) -> {
			controller.assembleActiveProject();
			simButtons.forEach(MainToolbar::toggleDisabled);	
		});
		buttons.add(assembleButton);
		effectsOfButtons.add(assembleButton);
		Tooltip assembleTooltip = new Tooltip();
		assembleTooltip.setText("Once Assembled, the Simulate Project button will become enabled.");
		Tooltip.install(assembleButton, assembleTooltip);
		
		Node simulateButton = new ImageView("toolbar_simulate_grey.png");
		simulateButton.setOnMouseClicked((event) -> {
			controller.simulateActiveProject();
			runButtons.forEach(MainToolbar::toggleDisabled);
		});
		simulateButton.setDisable(true);
		buttons.add(simulateButton);
		effectsOfButtons.add(simulateButton);
		simButtons.add(simulateButton);
		Tooltip simTooltip = new Tooltip();
		simTooltip.setText("Once the Sim button is clicked, the Run and Emulator buttons will enable.");
		Tooltip.install(simulateButton, simTooltip);
		
		Node programBoardButton = new ImageView("toolbar_program.png");
		programBoardButton.setOnMouseClicked((e) -> controller.downloadActiveProjectToBoard());
		buttons.add(programBoardButton);
		effectsOfButtons.add(programBoardButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		Node stepButton = new ImageView("toolbar_step_grey.png");
		stepButton.setOnMouseClicked((e) -> controller.stepSimulation());
		buttons.add(stepButton);
		runButtons.add(stepButton);
		effectsOfButtons.add(stepButton);
		
		Node runButton = new ImageView("toolbar_run_grey.png");
		runButton.setOnMouseClicked((e) -> controller.runSimulation());
		buttons.add(runButton);
		runButtons.add(runButton);
		effectsOfButtons.add(runButton);
		
		Node resetButton = new ImageView("toolbar_reset_grey.png");
		resetButton.setOnMouseClicked((e) -> controller.resetSimulation());
		buttons.add(resetButton);
		runButtons.add(resetButton);
		effectsOfButtons.add(resetButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		Node emulatorButton = new ImageView("toolbar_watcher.png");
		buttons.add(emulatorButton);

		runButtons.forEach(MainToolbar::toggleDisabled);
		this.setCenter(toolbar);
		
		effectsOfButtons.forEach(MainToolbar::setButtonEffect);
	}
	
	private static void setButtonEffect(Node node)
	{
		DropShadow rollOverColor = new DropShadow();
		rollOverColor.setColor(Color.ORANGERED);
		DropShadow clickColor = new DropShadow();
		clickColor.setColor(Color.DARKBLUE);
		
		node.addEventHandler(MouseEvent.MOUSE_ENTERED,
				(event) -> node.setEffect(rollOverColor));
		
		// Removing the shadow when the mouse cursor is off
		node.addEventHandler(MouseEvent.MOUSE_EXITED,
				(event) -> node.setEffect(null));
		
		// Darken shadow on click
		node.addEventHandler(MouseEvent.MOUSE_PRESSED, 
				(event) -> node.setEffect(clickColor));
		
		// Restore hover style on click end
		node.addEventHandler(MouseEvent.MOUSE_RELEASED,
				(event) -> node.setEffect(rollOverColor));
		
	}
	
	private static void toggleDisabled(Node node)
	{
		boolean isDisabled = !node.isDisabled();
		node.setDisable(isDisabled);
	}
}
