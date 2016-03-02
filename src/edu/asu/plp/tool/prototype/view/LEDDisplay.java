package edu.asu.plp.tool.prototype.view;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class LEDDisplay extends BorderPane
{
	private static final int MINIMUM_SIZE = 100;
	private static final int NUMBER_OF_LEDS = 8;
	private static final String FONT_NAME = "Arial";
	private static final int FONT_SIZE = 30;
	private static final Paint FONT_COLOR = Color.WHITE;
	private static final String LIT_COLOR = "green";
	private static final String UNLIT_COLOR = "black";
	
	public LEDDisplay()
	{
		GridPane grid = new GridPane();
		for (int index = 0; index < NUMBER_OF_LEDS; index++)
		{
			// TODO: replace isLit with state information; this calculation is placeholder
			boolean isLit = index % 2 == 0;
			Node led = createLED(index, isLit);
			grid.add(led, index, 0);
		}
		
		setCenter(grid);
	}
	
	private Node createLED(int number, boolean isLit)
	{
		String labelText = Integer.toString(number);
		Label ledLabel = new Label(labelText);
		ledLabel.setFont(new Font(FONT_NAME, FONT_SIZE));
		ledLabel.setTextAlignment(TextAlignment.CENTER);
		ledLabel.setTextFill(FONT_COLOR);
		
		String style = "-fx-border-color: white; -fx-text-align: center; -fx-background-color:";
		style += (isLit) ? LIT_COLOR : UNLIT_COLOR;
		
		BorderPane led = new BorderPane();
		led.setMinHeight(MINIMUM_SIZE);
		led.setMinWidth(MINIMUM_SIZE);
		led.setStyle(style);
		led.setCenter(ledLabel);
		
		return led;
	}
}
