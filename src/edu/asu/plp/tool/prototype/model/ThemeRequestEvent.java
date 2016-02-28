package edu.asu.plp.tool.prototype.model;

/**
 * @author Nesbitt, Morgan on 2/24/2016.
 */
public class ThemeRequestEvent
{
	private String themeName;

	public ThemeRequestEvent( String themeName )
	{
		this.themeName = themeName;
	}

	public String getThemeName()
	{
		return themeName;
	}
}
