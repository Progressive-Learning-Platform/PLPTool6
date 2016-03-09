package edu.asu.plp.tool.prototype.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * A pane whose content can be swapped out dynamically.
 * <p>
 * E.g. Use in a split pane where one side is a collection of panes that need to be dynamically swapped.
 *
 * @author by Morgan on 3/6/2016.
 */
public class SwapPane extends BorderPane
{
	private Pane activePane;

	public SwapPane()
	{
		this(new BorderPane());
	}

	public SwapPane( Pane activePane )
	{
		setActivePane(activePane);
	}

	/**
	 * Swap current pane with the activePane parameter. If and only if the passed pane is not null.
	 *
	 * @param activePane
	 */
	public void setActivePane( Pane activePane )
	{
		if ( activePane != null )
		{
			this.activePane = activePane;
			this.setCenter(activePane);
		}
	}
}
