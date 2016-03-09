package edu.asu.plp.tool.prototype.view;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class LEDDisplay extends BorderPane
{
	private static final int DEFAULT_SIZE = 100;
	private static final int NUMBER_OF_LEDS = 8;
	private static final int FONT_SIZE = 30;
	private static final String FONT_NAME = "Arial";
	private static final Paint FONT_COLOR = Color.WHITE;
	private static final String LIT_COLOR = "green";
	private static final String UNLIT_COLOR = "black";
	
	/**
	 * The state of each LED in this panel (on or off) where a bit set to 1 is "on" and a
	 * bit set to 0 is "off."
	 * <p>
	 * Each led corresponds to the bit at {@link #ledStates} >> index, where "index" is
	 * the index of the desired LED.
	 * <p>
	 * Note that LEDs are displayed in reverse order (i.e. LED0 is on the far right)
	 */
	private int ledStates;
	private BorderPane[] ledNodes;
	
	public LEDDisplay()
	{
		GridPane grid = new GridPane();
		ledNodes = new BorderPane[NUMBER_OF_LEDS];
		for (int index = 0; index < NUMBER_OF_LEDS; index++)
		{
			boolean isLit = isLEDLit(index);
			BorderPane led = createLED(index, isLit);
			ledNodes[index] = led;
			int position = NUMBER_OF_LEDS - index - 1;
			grid.add(led, position, 0);
		}
		
		setCenter(grid);
		
		this.widthProperty().addListener(this::onSizeChange);
	}
	
	private void onSizeChange(ObservableValue<? extends Number> value, Number old,
			Number current)
	{
		int size = current.intValue() / NUMBER_OF_LEDS;
		resizeLEDs(size);
	}
	
	private void resizeLEDs(int size)
	{
		for (BorderPane led : ledNodes)
		{
			led.setPrefHeight(size);
			led.setPrefWidth(size);
		}
	}
	
	private BorderPane createLED(int number, boolean isLit)
	{
		String labelText = Integer.toString(number);
		Label ledLabel = new Label(labelText);
		ledLabel.setFont(new Font(FONT_NAME, FONT_SIZE));
		ledLabel.setTextAlignment(TextAlignment.CENTER);
		ledLabel.setTextFill(FONT_COLOR);
		
		BorderPane led = new BorderPane();
		led.setPrefHeight(DEFAULT_SIZE);
		led.setPrefWidth(DEFAULT_SIZE);
		led.setCenter(ledLabel);
		setLEDStyle(led, isLit);
		
		return led;
	}
	
	public void setLEDState(int ledIndex, boolean isLit)
	{
		int ledBit = 1 << ledIndex;
		if (isLit)
		{
			// Set bit to TRUE
			ledStates |= ledBit;
		}
		else
		{
			// Set bit to FALSE
			ledBit = ~ledBit;
			ledStates &= ledBit;
		}
		
		setLEDStyle(ledNodes[ledIndex], isLit);
	}
	
	private void setLEDStyle(BorderPane led, boolean isLit)
	{
		String style = "-fx-border-color: white; -fx-text-align: center; -fx-background-color:";
		style += (isLit) ? LIT_COLOR : UNLIT_COLOR;
		led.setStyle(style);
	}
	
	/**
	 * Alias for {@link #isLEDLit(int)}
	 * 
	 * @param ledIndex
	 *            Index of the LED to retrieve the state of
	 * @return True if the LED is lit, false otherwise
	 */
	public boolean getLEDState(int ledIndex)
	{
		return isLEDLit(ledIndex);
	}
	
	public boolean isLEDLit(int index)
	{
		int ledState = (ledStates >> index) & 1;
		boolean isLit = (ledState != 0);
		return isLit;
	}
	
	public void toggleLEDState(int ledIndex)
	{
		boolean newState = !getLEDState(ledIndex);
		setLEDState(ledIndex, newState);
	}
}
