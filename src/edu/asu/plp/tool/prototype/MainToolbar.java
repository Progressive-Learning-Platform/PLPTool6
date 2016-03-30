package edu.asu.plp.tool.prototype;

import java.util.HashSet;
import java.util.Set;

import edu.asu.plp.tool.prototype.model.ImageButton;
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
	public MainToolbar(BusinessLogic businessLogic)
	{
		HBox toolbar = new HBox();
		Set<Node> effectsOfButtons = new HashSet<>();
		Set<Node> runButtons = new HashSet<>();
		Set<Node> simButtons = new HashSet<>();
		toolbar.setPadding(new Insets(1.5, 0, 1, 5));
		toolbar.setSpacing(5);
		ObservableList<Node> buttons = toolbar.getChildren();
		
		Node newProjectButton = new ImageView("toolbar_new.png");
		newProjectButton.setOnMouseClicked(businessLogic::onCreateNewProject);
		
		buttons.add(newProjectButton);
		effectsOfButtons.add(newProjectButton);
		
		Node newFileButton = new ImageView("menu_new.png");
		newFileButton.setOnMouseClicked(businessLogic::onNewASMFile);
		buttons.add(newFileButton);
		effectsOfButtons.add(newFileButton);
		
		Node openButton = new ImageView("toolbar_open.png");
		openButton.setOnMouseClicked(businessLogic::onOpenProject);
		buttons.add(openButton);
		effectsOfButtons.add(openButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		Node saveButton = new ImageView("toolbar_save.png");
		saveButton.setOnMouseClicked(businessLogic::onSaveProject);
		buttons.add(saveButton);
		effectsOfButtons.add(saveButton);
		
		Node assembleButton = new ImageView("toolbar_assemble.png");
		assembleButton.setOnMouseClicked((event) -> {
			//businessLogic.onAssemble(event);
			simButtons.forEach(MainToolbar::toggleDisabled);	
		});
		buttons.add(assembleButton);
		effectsOfButtons.add(assembleButton);
		Tooltip assembleTooltip = new Tooltip();
		assembleTooltip.setText("Once Assembled, the Simulate Project button will become enabled.");
		Tooltip.install(assembleButton, assembleTooltip);
		
		Node simulateButton = new ImageView("toolbar_simulate_grey.png");
		simulateButton.setOnMouseClicked((event) -> {
			businessLogic.onSimulate(event);
			runButtons.forEach(MainToolbar::toggleDisabled);
		});
		simulateButton.setDisable(true);
		buttons.add(simulateButton);
		effectsOfButtons.add(simulateButton);
		simButtons.add(simulateButton);
		Tooltip simTooltip = new Tooltip();
		simTooltip.setText("Once the Sim button is clicked, the Run and Emulator buttons will enable.");
		Tooltip.install(simulateButton, simTooltip);
		
		/*This button is supposed Program the PLP Board
		 *Not 100% to its use, may need to check with Dr.  Sohoni
		 *because I don't ever remember using it.
		 *
		 *Probably not included in our scope.
		 */
		Node programBoardButton = new ImageView("toolbar_program.png");
		// TODO: programButton.setOnMouseClicked(); {{what does this button do?}}
		buttons.add(programBoardButton);
		effectsOfButtons.add(programBoardButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		Node stepButton = new ImageView("toolbar_step_grey.png");
		stepButton.setOnMouseClicked(businessLogic::onSimulationStep);
		buttons.add(stepButton);
		runButtons.add(stepButton);
		effectsOfButtons.add(stepButton);
		
		Node runButton = new ImageView("toolbar_run_grey.png");
		runButton.setOnMouseClicked(businessLogic::onRunSimulation);
		buttons.add(runButton);
		runButtons.add(runButton);
		effectsOfButtons.add(runButton);
		
		Node resetButton = new ImageView("toolbar_reset_grey.png");
		resetButton.setOnMouseClicked(businessLogic::onResetSimulation);
		buttons.add(resetButton);
		runButtons.add(resetButton);
		effectsOfButtons.add(resetButton);
		
		/*This Button opens up a separate window with the "step"
		 * "run", and "reset" buttons. It also includes a slider that
		 * can adjust how fast the program is simulated
		 * 
		 * Probably not necessary for our scope of the project
		 * 
		 */
		Node remoteButton = new ImageView("toolbar_remote_grey.png");
		// TODO: listener = (e) -> console.println("Floating Sim Control Window Clicked");
		// TODO: remoteButton.setOnMouseClicked(); {{what does this do? Is it needed?}}
		buttons.add(remoteButton);
		runButtons.add(remoteButton);
		effectsOfButtons.add(remoteButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		/*
		Node emulatorButton = new ImageButton("toolbar_watcher.png", "toolbar_save.png");
		emulatorButton.setOnMouseClicked((event) -> {
			System.out.println("Test");
		});
		buttons.add(emulatorButton);
		*/

		runButtons.forEach(MainToolbar::toggleDisabled);
		this.setCenter(toolbar);
		
		effectsOfButtons.forEach(MainToolbar::setButtonEffect);
	}
	
	private static void changeButtonFace(Set<Node> buttons)
	{
		
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
	
	private static void disable(Node node)
	{
		DropShadow dropShadow = new DropShadow();
		node.setEffect(dropShadow);
		node.setDisable(true);
	}
	
	private static void toggleDisabled(Node node)
	{		
		// Invert the isDisabled property
		boolean isDisabled = !node.isDisabled();
		
		//node.setEffect(isDisabled ? dropShadow : null);
		node.setDisable(isDisabled);
		
	}
}
