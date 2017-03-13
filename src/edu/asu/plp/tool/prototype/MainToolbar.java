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
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;


public class MainToolbar extends BorderPane
{
	ImageButton newProjectButton = null;
	ImageButton newFileButton = null;
	ImageButton openButton = null;
	ImageButton saveButton = null;
	ImageButton assembleButton = null;
	ImageButton simulateButton = null;
	ImageButton programBoardButton = null;
	ImageButton stepButton = null;
	ImageButton runButton = null;
	ImageButton emulationButton = null;
	ImageButton resetButton = null;
	ImageButton cpuViewButton = null;
	
	ObservableList<Node> buttons = null;
	boolean enabledSimulation = false;
	
	public MainToolbar(Controller controller)
	{
		HBox toolbar = new HBox();
		//Set<ImageButton> runButtons = new HashSet<>();
		//Set<ImageButton> simButtons = new HashSet<>();
		toolbar.setPadding(new Insets(1.5, 0, 1, 5));
		toolbar.setSpacing(5);
		buttons = toolbar.getChildren();
		
		newProjectButton = new ImageButton("toolbar_new.png");
		newProjectButton.setOnMouseClicked((e) -> controller.createNewProject());
		buttons.add(newProjectButton);
		
		newFileButton = new ImageButton("menu_new.png");
		newFileButton.setOnMouseClicked((e) -> controller.createNewASM());
		buttons.add(newFileButton);

		openButton = new ImageButton("toolbar_open.png");
		openButton.setOnMouseClicked((e) -> controller.openProject());
		buttons.add(openButton);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		saveButton = new ImageButton("toolbar_save.png");
		saveButton.setOnMouseClicked((e) -> controller.saveActiveProject());
		buttons.add(saveButton);
		
		ImageButton assembleButton = new ImageButton("toolbar_assemble.png");
		ImageButton simulateButton = new ImageButton("toolbar_simulate.png", "toolbar_simulate - On.png");
		assembleButton.setOnMouseClicked((event) -> {
			//simButtons.forEach(MainToolbar::toggleDisabled);
			if(enabledSimulation == true)
			{
				
				buttons.remove(stepButton);
				buttons.remove(runButton);
				buttons.remove(resetButton);
				buttons.remove(cpuViewButton);
				buttons.remove(emulationButton);
				buttons.remove(programBoardButton);
				//simulateButton.toggleImage();
				simulateButton.setImage(new Image("toolbar_simulate.png"));
				enabledSimulation = false;
				controller.stopSimulation();
				
			}
			controller.assembleActiveProject();
		});
		buttons.add(assembleButton);
		Tooltip assembleTooltip = new Tooltip();
		assembleTooltip.setText("Once Assembled, the Simulate Project button will become enabled.");
		Tooltip.install(assembleButton, assembleTooltip);
		
		//ImageButton simulateButton = new ImageButton("toolbar_simulate.png", "toolbar_simulate - On.png");
		simulateButton.setOnMouseClicked((event) -> {
			
			if(enabledSimulation == true)
			{
				controller.stopSimulation();
				buttons.remove(stepButton);
				buttons.remove(runButton);
				buttons.remove(resetButton);
				buttons.remove(cpuViewButton);
				buttons.remove(emulationButton);
				buttons.remove(programBoardButton);
				//simulateButton.toggleImage();
				simulateButton.setImage(new Image("toolbar_simulate.png"));
				
				
				enabledSimulation = false;
				
			}
			else
			{
				buttons.add(stepButton);
				buttons.add(runButton);
				buttons.add(resetButton);
				buttons.add(cpuViewButton);
				buttons.add(emulationButton);
				buttons.add(programBoardButton);
				//simulateButton.toggleImage();
				simulateButton.setImage(new Image("toolbar_simulate - On.png"));
				enabledSimulation = true;
				controller.assembleActiveProject();
				controller.simulateActiveProject();
			}
			
			
			//runButtons.forEach(MainToolbar::toggleDisabled);
		});
		buttons.add(simulateButton);
		//simButtons.add(simulateButton);
		Tooltip simTooltip = new Tooltip();
		simTooltip.setText("Once the Sim button is clicked, the Run and Emulator buttons will enable.");
		Tooltip.install(simulateButton, simTooltip);
		
		/*This button is supposed Program the PLP Board
		 *Not 100% to its use, may need to check with Dr.  Sohoni
		 *because I don't ever remember using it.
		 *
		 *Probably not included in our scope.
		 */
		programBoardButton = new ImageButton("toolbar_program.png");
		programBoardButton.setOnMouseClicked((e) -> controller.downloadActiveProjectToBoard());
		//buttons.add(programBoardButton);
		
		//buttons.add(new Separator(Orientation.VERTICAL));
		
		stepButton = new ImageButton("toolbar_step.png", "toolbar_step_grey.png");		
		stepButton.setOnMouseClicked((e) -> controller.stepSimulation());
		//buttons.add(stepButton);
		//runButtons.add(stepButton);
		
		runButton = new ImageButton("toolbar_run.png", "toolbar_run_grey.png");
		runButton.setOnMouseClicked((e) -> controller.runSimulation());
		//buttons.add(runButton);
		//runButtons.add(runButton);
		
		resetButton = new ImageButton("toolbar_reset.png", "toolbar_reset_grey.png");
		resetButton.setOnMouseClicked((e) -> controller.resetSimulation());
		//buttons.add(resetButton);
		//runButtons.add(resetButton);
		
		//buttons.add(new Separator(Orientation.VERTICAL));
		
		emulationButton = new ImageButton("toolbar_watcher.png");
		emulationButton.setOnMouseClicked((e) -> controller.showEmulationWindow());
		//buttons.add(emulationButton);
		//runButtons.add(emulationButton);
		
		cpuViewButton = new ImageButton("toolbar_cpu.png");
		cpuViewButton.setOnMouseClicked((e) -> controller.openCpuViewWindow());
		//buttons.add(cpuViewButton);

		//simButtons.forEach(MainToolbar::toggleDisabled);
		//runButtons.forEach(MainToolbar::toggleDisabled);
		this.setCenter(toolbar);
		
	}
	
	private static void toggleDisabled(ImageButton button)
	{		
		button.toggleImage();
		
		boolean isDisabled = !button.isDisabled();
		button.setDisable(isDisabled);
	}
}
