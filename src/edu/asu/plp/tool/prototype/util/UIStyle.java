package edu.asu.plp.tool.prototype.util;

import javafx.scene.Node;

/**
 * Utility methods for styling/unstyling the UI.
 *
 * @author by Morgan on 3/8/2016.
 */
public class UIStyle
{
	private static final String errorIdentifier = "error";

	public static void applyError( boolean isValid, Node node )
	{
		if ( isValid )
		{
			if ( node.getStyleClass().contains(errorIdentifier) )
				node.getStyleClass().remove(errorIdentifier);
		} else
		{
			if ( !node.getStyleClass().contains(errorIdentifier) )
				node.getStyleClass().add(errorIdentifier);
		}
	}
}
