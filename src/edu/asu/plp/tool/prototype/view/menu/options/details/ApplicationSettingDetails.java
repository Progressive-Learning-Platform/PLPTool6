package edu.asu.plp.tool.prototype.view.menu.options.details;

import edu.asu.plp.tool.prototype.model.ApplicationSetting;
import edu.asu.plp.tool.prototype.model.SettingUtil;

/**
 *
 * @author Nesbitt, Morgan on 2/27/2016.
 */
public class ApplicationSettingDetails
{
	public static final ApplicationSettingDetails DEFAULT = ApplicationSettingDetails.defaultDetails();

	private String fontName;
	private String fontSize;
	private String applicationTheme;
	private String editorTheme;

	public ApplicationSettingDetails()
	{
		this(DEFAULT);
	}

	public ApplicationSettingDetails( ApplicationSettingDetails details )
	{
		this.fontName = details.fontName;
		this.fontSize = details.fontSize;
		this.applicationTheme = details.applicationTheme;
		this.editorTheme = details.editorTheme;
	}

	private static ApplicationSettingDetails defaultDetails()
	{
		ApplicationSettingDetails details = new ApplicationSettingDetails();

		ApplicationSetting setting = ApplicationSetting.EDITOR_FONT;
		details.fontName =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.EDITOR_FONT_SIZE;
		details.fontSize =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.APPLICATION_THEME;
		details.applicationTheme =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.EDITOR_THEME;
		details.editorTheme =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		return details;
	}


}
