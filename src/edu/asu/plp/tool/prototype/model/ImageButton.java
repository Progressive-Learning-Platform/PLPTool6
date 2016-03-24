package edu.asu.plp.tool.prototype.model;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class ImageButton extends ImageView
{
	String enabledImage, disabledImage;
	
	public ImageButton()
	{
		
	}
	
	public ImageButton(String image)
	{
		enabledImage = image;
		disabledImage = image;
	}
	
	public ImageButton(String enabled, String disabled)
	{
		enabledImage = new Image(enabled);
		disabledImage = new Image(disabled);
		this.setImage(enabledImage);
		update(this);
	}
	
	public void toggleDisabled()
	{
		boolean isDisabled = !this.isDisabled();
		this.setDisable(isDisabled);
		if(isDisabled)
		{
			this.setImage(disabledImage);
		}
		else
		{
			this.setImage(enabledImage);
		}
		update(this);
	}
	
	private void update(ImageButton imageButton)
	{
		
		DropShadow rollOverColor = new DropShadow();
		rollOverColor.setColor(Color.ORANGERED);
		DropShadow clickColor = new DropShadow();
		clickColor.setColor(Color.DARKBLUE);
		
		imageButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				(event) -> imageButton.setEffect(rollOverColor));
				
		// Removing the shadow when the mouse cursor is off
		imageButton.addEventHandler(MouseEvent.MOUSE_EXITED, (event) -> imageButton.setEffect(null));
		
		// Darken shadow on click
		imageButton.addEventHandler(MouseEvent.MOUSE_PRESSED,
				(event) -> imageButton.setEffect(clickColor));
				
		// Restore hover style on click end
		imageButton.addEventHandler(MouseEvent.MOUSE_RELEASED,
				(event) -> imageButton.setEffect(rollOverColor));
	}
	
	
}
