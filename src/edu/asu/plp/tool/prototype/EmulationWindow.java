package edu.asu.plp.tool.prototype;

import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EmulationWindow extends BorderPane
{
	
	public EmulationWindow()
	{
		GridPane grid = new GridPane();
		HBox topBar = createTopBar();
		
		Node ledPic = new ImageView("leds_example.png");
		Node switchesPic = new ImageView("switches_example.png");
		Node uartPic = new ImageView("uart_example.png");
		Node sevenSegPic = new ImageView("seven_seg_example.png");
		
		this.setTop(topBar);
		this.setCenter(uartPic);
		this.setLeft(ledPic);
		this.setRight(switchesPic);
		this.setBottom(sevenSegPic);
		
	}
	
	public HBox createTopBar()
	{
		HBox hbox = new HBox();
	    hbox.setPadding(new Insets(15, 15, 15, 15));
	    hbox.setSpacing(10);
	    //hbox.setStyle("-fx-background-color: Blue;");
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
