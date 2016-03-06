package edu.asu.plp.tool.prototype.view.menu.options.details;

import edu.asu.plp.tool.prototype.model.ApplicationSetting;
import edu.asu.plp.tool.prototype.model.SettingUtil;

/**
 * @author Nesbitt, Morgan on 2/27/2016.
 */
public class EditorSettingDetails
{
	public static final EditorSettingDetails DEFAULT = EditorSettingDetails.defaultDetails();

	private String editorMode;
	private String fontName;
	private String fontSize;
	//TODO Consider new below EditorSettingDetails
	//Soft wrapping, scroll behavior, show line numbers, print margin length, use soft tabs

	public EditorSettingDetails()
	{
		this(DEFAULT);
	}

	public EditorSettingDetails( EditorSettingDetails details )
	{
		this.editorMode = details.editorMode;
		this.fontName = details.fontName;
		this.fontSize = details.fontSize;
	}

	private static EditorSettingDetails defaultDetails()
	{
		EditorSettingDetails details = new EditorSettingDetails();

		ApplicationSetting setting = ApplicationSetting.EDITOR_MODE;
		details.editorMode =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.EDITOR_FONT;
		details.fontName =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.EDITOR_FONT_SIZE;
		details.fontSize =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		return details;
	}
}
