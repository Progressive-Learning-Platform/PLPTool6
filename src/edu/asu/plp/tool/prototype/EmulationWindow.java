package edu.asu.plp.tool.prototype;

import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class EmulationWindow extends BorderPane
{
	
	public EmulationWindow()
	{
		GridPane demoGrid = createDemo();
		HBox topBar = createTopBar();
		VBox optionsBar = createOptions();
				
		this.setTop(topBar);
		this.setCenter(demoGrid);
		this.setLeft(optionsBar);
	}
	
	private VBox createOptions()
	{
	    VBox vbox = new VBox();
	    vbox.setPadding(new Insets(10));
	    vbox.setSpacing(8);

	    Text title = new Text("Windows");
	    title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
	    vbox.getChildren().add(title);

	    CheckBox windows[] = new CheckBox[] {
	        new CheckBox("7 Segement Display"),
	        new CheckBox("LEDs"),
	        new CheckBox("UART"),
	        new CheckBox("Switches")};

	    for (int i=0; i<4; i++) {
	        VBox.setMargin(windows[i], new Insets(0, 0, 0, 4));
	        windows[i].setSelected(true);
	        vbox.getChildren().add(windows[i]);
	    }

	    return vbox;
	}

	private GridPane createDemo()
	{
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		
		DropShadow backgroundColor = new DropShadow();
		backgroundColor.setColor(Color.BLACK);
		
		Node ledPic = new ImageView("leds_example.png");
		ledPic.setEffect(backgroundColor);
		Node switchesPic = new ImageView("switches_example.png");
		switchesPic.setEffect(backgroundColor);
		Node uartPic = new ImageView("uart_example.png");
		uartPic.setEffect(backgroundColor);
		Node sevenSegPic = new ImageView("seven_seg_example.png");
		sevenSegPic.setEffect(backgroundColor);
		
		Label ledLabel = new Label();
		ledLabel.setText("LEDs: ");
		ledLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		Label switchesLabel = new Label();
		switchesLabel.setText("Switches: ");
		switchesLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		Label uartLabel = new Label();
		uartLabel.setText("UART: ");
		uartLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		Label sevenSegLabel = new Label();
		sevenSegLabel.setText("Seven Segment Display: ");
		sevenSegLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		grid.add(sevenSegLabel, 0, 0);
		grid.add(sevenSegPic, 0, 1, 1, 1);
		
		grid.add(ledLabel, 0, 2);
		grid.add(ledPic, 0, 3, 1, 1);
		
		grid.add(switchesLabel, 0, 4);
		grid.add(switchesPic, 0, 5, 1, 1);
		
		grid.add(uartLabel, 1, 0);
		grid.add(uartPic, 1, 1, 1, 4);
		//grid.setGridLinesVisible(true);
		
		return grid;
	}

	public HBox createTopBar()
	{
		HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 15, 15, 15));
	    hbox.setSpacing(10);
	    hbox.setStyle("-fx-background-color: Grey;");
	    ObservableList<Node> buttons = hbox.getChildren();
	    Set<Node> buttonEffectsSet = new HashSet<>();

		Node runButton = new ImageView("toolbar_run.png");
		runButton.setOnMouseClicked((event) -> {
			//TODO: Attach to Backend
		});
		buttons.add(runButton);
		buttonEffectsSet.add(runButton);
		
		Node stepButton = new ImageView("toolbar_step.png");
		runButton.setOnMouseClicked((event) -> {
			//TODO: Attach to Backend
		});
		buttons.add(stepButton);
		buttonEffectsSet.add(stepButton);
		
		Node resetButton = new ImageView("toolbar_reset.png");
		runButton.setOnMouseClicked((event) -> {
			//TODO: Attach to Backend
		});
		buttons.add(resetButton);
		buttonEffectsSet.add(resetButton);
		
		buttonEffectsSet.forEach(EmulationWindow::setButtonEffect);
		
		Label cycleLabel = new Label();
		cycleLabel.setText("Cycle: ");
		cycleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		buttons.add(cycleLabel);
		
		Label cycleLabelCount = new Label();
		cycleLabelCount.setText("0");
		cycleLabelCount.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		buttons.add(cycleLabelCount);
		
		Label stepLabel = new Label();
		stepLabel.setText("Step: ");
		stepLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		buttons.add(stepLabel);
		
		Label stepLabelCount = new Label();
		stepLabelCount.setText("0");
		stepLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		buttons.add(stepLabelCount);
		
		Label simModeState = new Label();
		simModeState.setText("Sim Mode");
		simModeState.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		buttons.add(simModeState);
		
		Node simModeImage = new ImageView("sim_mode_on.png");
		buttons.add(simModeImage);
		
		

	    return hbox;
			
	}
	
	private void createWatcherWindow()
	{
		//TODO: Implement this
	}
	
	private void createSwitches()
	{
		//TODO: Implement this
	}
	
	private void createLED()
	{
		//TODO: Implement this
	}
	
	private void createSevenSegment()
	{
		//TODO: Implement this
	}
	
	private void createUART()
	{
		//TODO: Implement this
	}
	
	private static void toggleDisabled(Node node)
	{		
		// Invert the isDisabled property
		boolean isDisabled = !node.isDisabled();
		
		//node.setEffect(isDisabled ? dropShadow : null);
		node.setDisable(isDisabled);
		
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
}
