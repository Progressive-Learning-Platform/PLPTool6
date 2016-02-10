package edu.asu.plp.tool.prototype;

import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class MainToolbar extends BorderPane
{
	public MainToolbar(BusinessLogic businessLogic)
	{
		HBox toolbar = new HBox();
		Set<Node> toggleButtons = new HashSet<>();
		toolbar.setPadding(new Insets(1.5, 0, 1, 5));
		toolbar.setSpacing(5);
		ObservableList<Node> buttons = toolbar.getChildren();
		
		DropShadow lightBlueShadow = new DropShadow();
		lightBlueShadow.setColor(Color.LIGHTBLUE);
		DropShadow darkBlueShadow = new DropShadow();
		darkBlueShadow.setColor(Color.DARKBLUE);
		
		Node newProjectButton = new ImageView("toolbar_new.png");
		// Hover style
		newProjectButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				(event) -> newProjectButton.setEffect(lightBlueShadow));
		
		// Removing the shadow when the mouse cursor is off
		newProjectButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				(event) -> newProjectButton.setEffect(null));
		
		// Darken shadow on click
		newProjectButton.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> {
			businessLogic.onCreateNewProject(event);
			newProjectButton.setEffect(darkBlueShadow);
		});
		
		// Restore hover style on click end
		newProjectButton.addEventHandler(MouseEvent.MOUSE_RELEASED,
				(event) -> newProjectButton.setEffect(lightBlueShadow));
		buttons.add(newProjectButton);
		
		Node newFileButton = new ImageView("menu_new.png");
		newFileButton.setOnMouseClicked(businessLogic::onNewASMFile);
		buttons.add(newFileButton);
		
		Node openButton = new ImageView("toolbar_open.png");
		openButton.setOnMouseClicked(businessLogic::onOpenProject);
		buttons.add(openButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		Node saveButton = new ImageView("toolbar_save.png");
		saveButton.setOnMouseClicked(businessLogic::onSaveProject);
		buttons.add(saveButton);
		
		Node assembleButton = new ImageView("toolbar_assemble.png");
		assembleButton.setOnMouseClicked(businessLogic::onAssemble);
		buttons.add(assembleButton);
		
		Node simulateButton = new ImageView("toolbar_simulate.png");
		simulateButton.setOnMouseClicked((event) -> {
			businessLogic.onSimulate(event);
			toggleButtons.forEach(MainToolbar::toggleDisabled);
		});
		buttons.add(simulateButton);
		
		Node programButton = new ImageView("toolbar_program.png");
		// TODO: programButton.setOnMouseClicked(); {{what does this button do?}}
		buttons.add(programButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		Node stepButton = new ImageView("toolbar_step.png");
		stepButton.setOnMouseClicked(businessLogic::onSimulationStep);
		buttons.add(stepButton);
		toggleButtons.add(stepButton);
		
		Node runButton = new ImageView("toolbar_run.png");
		runButton.setOnMouseClicked(businessLogic::onRunSimulation);
		buttons.add(runButton);
		toggleButtons.add(runButton);
		
		Node resetButton = new ImageView("toolbar_reset.png");
		resetButton.setOnMouseClicked(businessLogic::onResetSimulation);
		buttons.add(resetButton);
		toggleButtons.add(resetButton);
		
		Node remoteButton = new ImageView("toolbar_remote.png");
		// TODO: listener = (e) -> console.println("Floating Sim Control Window Clicked");
		// TODO: remoteButton.setOnMouseClicked(); {{what does this do? Is it needed?}}
		buttons.add(remoteButton);
		toggleButtons.add(remoteButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		// TODO: move these buttons to their own panel/frame
		Node cpuButton = new ImageView("toolbar_cpu.png");
		cpuButton.setOnMouseClicked(businessLogic::onOpenCPUView);
		buttons.add(cpuButton);
		toggleButtons.add(cpuButton);
		
		Node watcherButton = new ImageView("toolbar_watcher.png");
		watcherButton.setOnMouseClicked(businessLogic::onOpenWatcherWindow);
		buttons.add(watcherButton);
		toggleButtons.add(watcherButton);
		
		Node ledsButton = new ImageView("toolbar_sim_leds.png");
		ledsButton.setOnMouseClicked(businessLogic::onDisplayLEDEmulator);
		buttons.add(ledsButton);
		toggleButtons.add(ledsButton);
		
		Node switchesButton = new ImageView("toolbar_sim_switches.png");
		switchesButton.setOnMouseClicked(businessLogic::onDisplaySwitchesEmulator);
		buttons.add(switchesButton);
		toggleButtons.add(switchesButton);
		
		Node sevenSegmentButton = new ImageView("toolbar_sim_7segments.png");
		sevenSegmentButton
				.setOnMouseClicked(businessLogic::onDisplaySevenSegmentEmulator);
		buttons.add(sevenSegmentButton);
		toggleButtons.add(sevenSegmentButton);
		
		Node uartButton = new ImageView("toolbar_sim_uart.png");
		uartButton.setOnMouseClicked(businessLogic::onDisplayUARTEmulator);
		buttons.add(uartButton);
		toggleButtons.add(uartButton);
		
		Node vgaButton = new ImageView("toolbar_sim_vga.png");
		vgaButton.setOnMouseClicked(businessLogic::onDisplayVGAEmulator);
		buttons.add(vgaButton);
		toggleButtons.add(vgaButton);
		
		Node plpidButton = new ImageView("toolbar_sim_plpid.png");
		plpidButton.setOnMouseClicked(businessLogic::onDisplayPLPIDEmulator);
		buttons.add(plpidButton);
		toggleButtons.add(plpidButton);
		
		Node gpioButton = new ImageView("toolbar_sim_gpio.png");
		gpioButton.setOnMouseClicked(businessLogic::onDisplayGPIOEmulator);
		buttons.add(gpioButton);
		toggleButtons.add(gpioButton);
		
		Node interuptButton = new ImageView("toolbar_exclamation.png");
		interuptButton.setOnMouseClicked(businessLogic::onSimulationInterrupt);
		buttons.add(interuptButton);
		toggleButtons.add(interuptButton);

		toggleButtons.forEach(MainToolbar::disable);
		this.setCenter(toolbar);
	}
	
	private static void disable(Node node)
	{
		DropShadow dropShadow = new DropShadow();
		node.setEffect(dropShadow);
		node.setDisable(true);
	}
	
	private static void toggleDisabled(Node node)
	{
		DropShadow dropShadow = new DropShadow();
		
		// Invert the isDisabled property
		boolean isDisabled = !node.isDisabled();
		
		node.setEffect(isDisabled ? dropShadow : null);
		node.setDisable(isDisabled);
	}
}
