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
		Set<ImageButton> runButtons = new HashSet<>();
		Set<ImageButton> simButtons = new HashSet<>();
		toolbar.setPadding(new Insets(1.5, 0, 1, 5));
		toolbar.setSpacing(5);
		ObservableList<Node> buttons = toolbar.getChildren();
		
		boolean inSimMode = false;
		
		ImageButton newProjectButton = new ImageButton("toolbar_new.png");
		newProjectButton.setOnMouseClicked(businessLogic::onCreateNewProject);
		buttons.add(newProjectButton);
		
		ImageButton newFileButton = new ImageButton("menu_new.png");
		newFileButton.setOnMouseClicked(businessLogic::onNewASMFile);
		buttons.add(newFileButton);
		
		ImageButton openButton = new ImageButton("toolbar_open.png");
		openButton.setOnMouseClicked(businessLogic::onOpenProject);
		buttons.add(openButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		ImageButton saveButton = new ImageButton("toolbar_save.png");
		saveButton.setOnMouseClicked(businessLogic::onSaveProject);
		buttons.add(saveButton);
		
		ImageButton assembleButton = new ImageButton("toolbar_assemble.png");
		assembleButton.setOnMouseClicked((event) -> {
			businessLogic.onAssemble(event);
			simButtons.forEach(MainToolbar::toggleDisabled);
		});
		buttons.add(assembleButton);
		Tooltip assembleTooltip = new Tooltip();
		assembleTooltip.setText("Once Assembled, the Simulate Project button will become enabled.");
		Tooltip.install(assembleButton, assembleTooltip);
		
		ImageButton simulateButton = new ImageButton("toolbar_simulate.png", "toolbar_simulate_grey.png");
		simulateButton.setOnMouseClicked((event) -> {
			//businessLogic.onSimulate(event);
			runButtons.forEach(MainToolbar::toggleDisabled);
		});
		buttons.add(simulateButton);
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
		// ImageButton programBoardButton = new ImageButton("toolbar_program.png");
		// TODO: programButton.setOnMouseClicked(); {{what does this button do?}}
		// buttons.add(programBoardButton);
		// effectsOfButtons.add(programBoardButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		ImageButton stepButton = new ImageButton("toolbar_step.png", "toolbar_step_grey.png");
		stepButton.setOnMouseClicked(businessLogic::onSimulationStep);
		buttons.add(stepButton);
		runButtons.add(stepButton);
		
		ImageButton runButton = new ImageButton("toolbar_run.png", "toolbar_run_grey.png");
		runButton.setOnMouseClicked(businessLogic::onRunSimulation);
		buttons.add(runButton);
		runButtons.add(runButton);
		
		ImageButton resetButton = new ImageButton("toolbar_reset.png", "toolbar_reset_grey.png");
		resetButton.setOnMouseClicked(businessLogic::onResetSimulation);
		buttons.add(resetButton);
		runButtons.add(resetButton);
		
		/*This Button opens up a separate window with the "step"
		 * "run", and "reset" buttons. It also includes a slider that
		 * can adjust how fast the program is simulated
		 * 
		 * Probably not necessary for our scope of the project
		 * 
		 */
		ImageButton remoteButton = new ImageButton("toolbar_remote.png", "toolbar_remote_grey.png");
		// TODO: listener = (e) -> console.println("Floating Sim Control Window Clicked");
		// TODO: remoteButton.setOnMouseClicked(); {{what does this do? Is it needed?}}
		buttons.add(remoteButton);
		runButtons.add(remoteButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		ImageButton emulatorButton = new ImageButton("toolbar_watcher.png");
		emulatorButton.setOnMouseClicked((event) -> {
			//TODO: Attach to I/O Sim Window
		});
		buttons.add(emulatorButton);
		
		ImageButton cpuViewButton = new ImageButton("toolbar_cpu.png");
		buttons.add(cpuViewButton);

		simButtons.forEach(MainToolbar::toggleDisabled);
		runButtons.forEach(MainToolbar::toggleDisabled);
		this.setCenter(toolbar);
		
	}
	
	private static void toggleDisabled(ImageButton button)
	{		
		button.toggleImage();
		
		boolean isDisabled = !button.isDisabled();
		button.setDisable(isDisabled);
	}
}
