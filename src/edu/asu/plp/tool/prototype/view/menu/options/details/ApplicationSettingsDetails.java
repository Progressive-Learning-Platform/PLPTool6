package edu.asu.plp.tool.prototype.view.menu.options.details;

/**
 * @author Nesbitt, Morgan on 2/27/2016.
 */
public class ApplicationSettingsDetails
{
	public static final ApplicationSettingsDetails DEFAULT = ApplicationSettingsDetails.defaultDetails();

	private String fontName;
	private String fontSize;
	private String applicationTheme;

	public ApplicationSettingsDetails()
	{

	}

	private static ApplicationSettingsDetails defaultDetails()
	{
		return null;
	}
}
