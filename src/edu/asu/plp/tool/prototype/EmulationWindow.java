package edu.asu.plp.tool.prototype;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
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
import edu.asu.plp.tool.prototype.view.LEDDisplay;
import edu.asu.plp.tool.prototype.view.SevenSegmentPanel;
import edu.asu.plp.tool.prototype.view.SwitchesDisplay;
import edu.asu.plp.tool.prototype.view.UARTPanel;
import edu.asu.plp.tool.prototype.view.WatcherWindow;

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
		
		VBox leftSide = new VBox(25);
		leftSide.setSpacing(10);
		VBox rightSide = new VBox(25);
		rightSide.setSpacing(10);
		VBox checkOptions = new VBox();
		checkOptions.setPadding(new Insets(10));
		checkOptions.setSpacing(8);
		
		DropShadow backgroundColor = new DropShadow();
		backgroundColor.setColor(Color.BLACK);
		
		Node ledDisplay = new LEDDisplay();
		ledDisplay.setEffect(backgroundColor);
		HBox ledFrame = new HBox();
		ledFrame.setPadding(new Insets(10));
		ledFrame.setStyle("-fx-background-color: grey;");
		// frame.setEffect(backgroundColor);
		ledFrame.getChildren().add(ledDisplay);
		
		Node switchesDisplay = new SwitchesDisplay();
		// switchesDisplay.setEffect(backgroundColor);
		HBox switchesFrame = new HBox();
		switchesFrame.setPadding(new Insets(10));
		switchesFrame.setStyle("-fx-background-color: grey;");
		switchesFrame.getChildren().add(switchesDisplay);
		
		Node uartDisplay = new UARTPanel();
		// uartDisplay.setEffect(backgroundColor);
		HBox uartFrame = new HBox();
		uartFrame.setPadding(new Insets(10));
		uartFrame.setStyle("-fx-background-color: grey;");
		uartFrame.getChildren().add(uartDisplay);
		
		Node watcherWindowDisplay = new WatcherWindow();
		// watcherWindowDisplay.setEffect(backgroundColor);
		HBox watcherFrame = new HBox();
		watcherFrame.setPadding(new Insets(10));
		watcherFrame.setStyle("-fx-background-color: grey;");
		watcherFrame.getChildren().add(watcherWindowDisplay);
		
		Node sevenSegDisplay = new SevenSegmentPanel();
		// sevenSegDisplay.setEffect(backgroundColor);
		HBox sevenSegFrame = new HBox();
		sevenSegFrame.setStyle("-fx-background-color: grey;");
		sevenSegFrame.getChildren().add(sevenSegDisplay);
		
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
		
		Label watcherWindowLabel = new Label();
		watcherWindowLabel.setText("Watcher Window ");
		watcherWindowLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		leftSide.getChildren().addAll(watcherWindowLabel, watcherFrame, ledLabel,
				ledFrame, switchesLabel, switchesFrame);
		rightSide.getChildren()
				.addAll(uartLabel, uartFrame, sevenSegLabel, sevenSegFrame);
		
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
					rightSide.getChildren().remove(sevenSegLabel);
					rightSide.getChildren().remove(sevenSegFrame);
				}
				else
				{
					rightSide.getChildren().add(sevenSegLabel);
					rightSide.getChildren().add(sevenSegFrame);
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
					leftSide.getChildren().remove(ledFrame);
				}
				else
				{
					leftSide.getChildren().add(ledLabel);
					leftSide.getChildren().add(ledFrame);
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
					rightSide.getChildren().remove(uartFrame);
				}
				else
				{
					rightSide.getChildren().add(uartLabel);
					rightSide.getChildren().add(uartFrame);
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
					leftSide.getChildren().remove(switchesFrame);
				}
				else
				{
					leftSide.getChildren().add(switchesLabel);
					leftSide.getChildren().add(switchesFrame);
				}
			}
		});
		
		CheckBox watcherWindowCheckBox = new CheckBox("Watcher Window");
		watcherWindowCheckBox.setSelected(true);
		watcherWindowCheckBox.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> ov,
							Boolean old_val, Boolean new_val)
					{
						if (!watcherWindowCheckBox.isSelected())
						{
							leftSide.getChildren().remove(watcherWindowLabel);
							leftSide.getChildren().remove(watcherFrame);
						}
						else
						{
							leftSide.getChildren().add(watcherWindowLabel);
							leftSide.getChildren().add(watcherFrame);
						}
					}
				});
		
		checkOptions.getChildren().addAll(sevenSegCheckBox, ledCheckBox, uartCheckBox,
				switchesCheckBox, watcherWindowCheckBox);
		
		SplitPane splitPane = new SplitPane();
		splitPane.setStyle("-fx-box-border: transparent;");
		splitPane.setStyle("-fx-padding: 4 10 10 10;");
		Node divider = splitPane.lookup(".split-pane-divider");
		if (divider != null)
		{
			divider.setStyle("-fx-background-color: transparent;");
		}
		splitPane.getItems().addAll(leftSide, rightSide);
		
		grid.add(checkOptions, 0, 0);
		grid.add(splitPane, 1, 0);
		// grid.add(rightSide, 2, 0);
		
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
		cycleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		buttons.add(cycleLabel);
		
		Label cycleLabelCount = new Label();
		cycleLabelCount.setText("0");
		cycleLabelCount.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		buttons.add(cycleLabelCount);
		
		Label stepLabel = new Label();
		stepLabel.setText("Step: ");
		stepLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		buttons.add(stepLabel);
		
		Label stepLabelCount = new Label();
		stepLabelCount.setText("0");
		stepLabelCount.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
		buttons.add(stepLabelCount);
		
		Label simModeState = new Label();
		simModeState.setText("Sim Mode");
		simModeState.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		buttons.add(simModeState);
		
		Node simModeImage = new ImageView("sim_mode_on.png");
		buttons.add(simModeImage);
		
		return hbox;
		
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
