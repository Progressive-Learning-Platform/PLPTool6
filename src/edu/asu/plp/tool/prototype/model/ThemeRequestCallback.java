package edu.asu.plp.tool.prototype.model;

import java.util.Optional;

/**
 * @author Nesbitt, Morgan on 2/24/2016.
 */
public class ThemeRequestCallback
{
	private Optional<Theme> theme;

	public ThemeRequestCallback(Optional<Theme> theme )
	{
		this.theme = theme;
	}

	public Optional<Theme> requestedTheme()
	{
		return theme;
	}
}
