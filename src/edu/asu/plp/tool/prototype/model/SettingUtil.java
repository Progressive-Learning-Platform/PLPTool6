package edu.asu.plp.tool.prototype.model;

import edu.asu.plp.tool.prototype.ApplicationSettings;

import java.util.Optional;

/**
 * Methods for accessing the {@link ApplicationSettings} class easier.
 *
 * @author by Morgan on 3/5/2016.
 */
public class SettingUtil
{
	/**
	 * For use in loading only saved settings that are have a default counterpart. Will load the saved setting
	 * parameter
	 * if present. If not present it will default to the {@link ApplicationSetting}. If the default setting is not
	 * present in the settings it will throw an illegal state exception.
	 * <p>
	 * NOTE: This is reliant on the fact that any setting in {@link ApplicationSetting} must me present in {@link
	 * ApplicationSettings}.
	 * <p>
	 * If the setting you are trying to retrieve is not present in {@link ApplicationSetting}, this is not the method
	 * you are looking for.
	 *
	 * @param saved
	 * @param setting
	 *
	 * @return
	 */
	public static String loadSavedSettingDefaultIfNotPresent( String saved, ApplicationSetting setting )
	{
		Optional<String> savedSetting = ApplicationSettings.getSetting(saved);
		if ( savedSetting.isPresent() )
			return savedSetting.get();
		else
		{
			Optional<String> defaultSetting = ApplicationSettings.getSetting(setting);
			if ( defaultSetting.isPresent() )
				return defaultSetting.get();
			else
				throw new IllegalStateException(
						"Default ApplicationSetting was not present. ApplicationSettings must resolve all " +
								"default application settings if not present in loading.");
		}
	}

}
