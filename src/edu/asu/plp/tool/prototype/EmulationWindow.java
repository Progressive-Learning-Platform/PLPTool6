package edu.asu.plp.tool.prototype;

import java.util.HashSet;
import java.util.Set;

import edu.asu.plp.tool.prototype.view.LEDDisplay;
import edu.asu.plp.tool.prototype.view.SevenSegmentPanel;
import edu.asu.plp.tool.prototype.view.SwitchesDisplay;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.ColumnConstraints;
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
		
		this.setTop(topBar);
		this.setCenter(demoGrid);
	}
	
	private GridPane createDemo()
	{
		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(10);
		ColumnConstraints column1 = new ColumnConstraints();
		column1.setMinWidth(150);
		grid.getColumnConstraints().add(column1);
		
		VBox leftSide = new VBox();
		leftSide.setSpacing(10);
		VBox rightSide = new VBox();
		rightSide.setSpacing(10);
		VBox checkOptions = new VBox();
		checkOptions.setPadding(new Insets(10));
		checkOptions.setSpacing(8);
		
		DropShadow backgroundColor = new DropShadow();
		backgroundColor.setColor(Color.BLACK);
		
		Node ledDisplay = new LEDDisplay();
		ledDisplay.setEffect(backgroundColor);
		
		Node switchesDisplay = new SwitchesDisplay();
		switchesDisplay.setEffect(backgroundColor);
		
		Node uartPic = new ImageView("uart_example.png");
		uartPic.setEffect(backgroundColor);
		
		Node sevenSegDisplay = new SevenSegmentPanel();
		sevenSegDisplay.setEffect(backgroundColor);
		
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
		
		leftSide.getChildren().addAll(sevenSegLabel, sevenSegDisplay, ledLabel, ledDisplay,
				switchesLabel, switchesDisplay);
		rightSide.getChildren().addAll(uartLabel, uartPic);
		
		Text title = new Text("Windows");
		title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		checkOptions.getChildren().add(title);
		
		CheckBox sevenSegCheckBox = new CheckBox("7 Segment Display");
		sevenSegCheckBox.setSelected(true);
		sevenSegCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
					Boolean new_val)
			{
				if (!sevenSegCheckBox.isSelected())
				{
					leftSide.getChildren().remove(sevenSegLabel);
					leftSide.getChildren().remove(sevenSegDisplay);
				}
				else
				{
					leftSide.getChildren().add(sevenSegLabel);
					leftSide.getChildren().add(sevenSegDisplay);
				}
			}
		});
		CheckBox ledCheckBox = new CheckBox("LED's");
		ledCheckBox.setSelected(true);
		ledCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
					Boolean new_val)
			{
				if (!ledCheckBox.isSelected())
				{
					leftSide.getChildren().remove(ledLabel);
					leftSide.getChildren().remove(ledDisplay);
				}
				else
				{
					leftSide.getChildren().add(ledLabel);
					leftSide.getChildren().add(ledDisplay);
				}
			}
		});
		
		CheckBox uartCheckBox = new CheckBox("UART");
		uartCheckBox.setSelected(true);
		uartCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
					Boolean new_val)
			{
				if (!uartCheckBox.isSelected())
				{
					rightSide.getChildren().remove(uartLabel);
					rightSide.getChildren().remove(uartPic);
				}
				else
				{
					rightSide.getChildren().add(uartLabel);
					rightSide.getChildren().add(uartPic);
				}
			}
		});
		
		CheckBox switchesCheckBox = new CheckBox("Switches");
		switchesCheckBox.setSelected(true);
		switchesCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val,
					Boolean new_val)
			{
				if (!switchesCheckBox.isSelected())
				{
					leftSide.getChildren().remove(switchesLabel);
					leftSide.getChildren().remove(switchesDisplay);
				}
				else
				{
					leftSide.getChildren().add(switchesLabel);
					leftSide.getChildren().add(switchesDisplay);
				}
			}
		});
		
		checkOptions.getChildren().addAll(sevenSegCheckBox, ledCheckBox, uartCheckBox,
				switchesCheckBox);
		
		
		grid.add(checkOptions, 0, 0);
		grid.add(leftSide, 1, 0);
		grid.add(rightSide, 2, 0);
		return grid;
	}
	
	public HBox createTopBar()
	{
		HBox hbox = new HBox();
		hbox.setPadding(new Insets(15, 15, 15, 15));
		hbox.setSpacing(10);
		hbox.setStyle("-fx-background-color: lightsteelblue;");
		ObservableList<Node> buttons = hbox.getChildren();
		Set<Node> buttonEffectsSet = new HashSet<>();
		
		Node runButton = new ImageView("toolbar_run.png");
		runButton.setOnMouseClicked((event) -> {
			// TODO: Attach to Backend
		});
		buttons.add(runButton);
		buttonEffectsSet.add(runButton);
		
		Node stepButton = new ImageView("toolbar_step.png");
		runButton.setOnMouseClicked((event) -> {
			// TODO: Attach to Backend
		});
		buttons.add(stepButton);
		buttonEffectsSet.add(stepButton);
		
		Node resetButton = new ImageView("toolbar_reset.png");
		runButton.setOnMouseClicked((event) -> {
			// TODO: Attach to Backend
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
		stepLabelCount.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
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
		// TODO: Implement this
	}
	
	private void createSwitches()
	{
		// TODO: Implement this
	}
	
	private void createLED()
	{
		// TODO: Implement this
	}
	
	private void createSevenSegment()
	{
		// TODO: Implement this
	}
	
	private void createUART()
	{
		// TODO: Implement this
	}
	
	private static void toggleDisabled(Node node)
	{
		// Invert the isDisabled property
		boolean isDisabled = !node.isDisabled();
		
		// node.setEffect(isDisabled ? dropShadow : null);
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
		node.addEventHandler(MouseEvent.MOUSE_EXITED, (event) -> node.setEffect(null));
		
		// Darken shadow on click
		node.addEventHandler(MouseEvent.MOUSE_PRESSED,
				(event) -> node.setEffect(clickColor));
				
		// Restore hover style on click end
		node.addEventHandler(MouseEvent.MOUSE_RELEASED,
				(event) -> node.setEffect(rollOverColor));
				
	}
}
