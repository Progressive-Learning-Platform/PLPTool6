package edu.asu.plp.tool.prototype;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import edu.asu.plp.tool.prototype.model.Setting;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

/**
 * Global map that holds settings configurations. Derives from a .settings file (JSON Format). If the file is not loaded
 * or cannot be loaded, then the default settings will be used.
 *
 * See {@link edu.asu.plp.tool.prototype.model.ApplicationSetting} for the base required values.
 *
 * To use this settings class, you must call {@link ApplicationSettings#initialize()} attempting any usage.
 *
 * You must also provide it with a settings file by {@link ApplicationSettings#loadFromFile(String)} or
 * {@link ApplicationSettings#loadFromFile(File)}. Or load from the {@link ApplicationSettings#DEFAULT_SETTINGS_FILE}
 * by calling {@link ApplicationSettings#loadFromFile()}.
 *
 * This will populate the map with all the given values. To retrieve a value from the map call
 * {@link ApplicationSettings#getSetting(String)} with the respective key and it will return the value wrapped in an
 * Optional if it exists or return an empty Optional otherwise.
 *
 * You may also pass a {@link Setting} to {@link ApplicationSettings#getSetting(Setting)}. Please see {@link Setting}
 * for information on how to use/implement {@link Setting}.
 *
 * @author Nesbitt, Morgan
 * Created on 2/23/2016.
 */
public class ApplicationSettings
{
	public static final String DEFAULT_SETTINGS_FILE = "settings/plp-tool.settings";
	
	private static ApplicationSettings instance;
	
	private static HashMap<String, String> settings;
	
	private ApplicationSettings()
	{
		settings = new HashMap<>();
	}
	
	public static Optional<String> getSetting(String key)
	{
		if(settings.containsKey(key))
			return Optional.of(settings.get(key));
		
		return Optional.empty();
	}
	
	public static Optional<String> getSetting(Setting setting)
	{
		return getSetting(setting.toString());
	}
	
	public static ApplicationSettings initialize()
	{
		if(instance == null)
			instance = new ApplicationSettings();
		
		return instance;
	}

	public static final boolean loadFromFile()
	{
		return loadFromFile(DEFAULT_SETTINGS_FILE);
	}

	public static final boolean loadFromFile(String filePath)
	{
		return loadFromFile(new File(filePath));
	}
	
	public static final boolean loadFromFile(File file)
	{
		if(!file.isFile())
		{
			loadFromDefaultSettings();
			return false;
		}

		try
		{
			String fileContents = FileUtils.readFileToString(file);
			JSONObject jsonFile = new JSONObject(fileContents);

			parseJSONSettings(jsonFile, "");

			if(settings.isEmpty())
			{
				loadFromDefaultSettings();
				return false;
			}
			else
				return true;
		}
		catch (IOException e)
		{
			loadFromDefaultSettings();
			return false;
		}
	}
	
	public static final void loadFromDefaultSettings()
	{
		JSONObject jsonSettings = new JSONObject(generateDefaultSettings());
		parseJSONSettings(jsonSettings, "");
	}
	
	private static void parseJSONSettings(JSONObject jsonSettings, String basePath)
	{
		for(String key : JSONObject.getNames(jsonSettings))
		{
			Object value = jsonSettings.get(key);
			if(value instanceof String)
			{
				settings.put(bindPath(basePath, key), (String)value);
			}
			else
			{
				parseJSONSettings(jsonSettings.getJSONObject(key), bindPath(basePath, key));
			}
		}
	}

	private static String bindPath(String basePath, String currentPath)
	{
		if(basePath.isEmpty())
			return currentPath.replace(" ", "_").toUpperCase();
		
		String combinedPath = basePath + "_" + currentPath.replace(" ", "_");
		return combinedPath.toUpperCase();
	}
	
	private static final String generateDefaultSettings()
	{
		StringBuilder builder = new StringBuilder();
		
		final String newLine = System.lineSeparator();
		
		builder.append("{" + newLine);
		builder.append("\t\"Resources Path\" : \"resources/\"," + newLine);
		builder.append("\t\"Ace Path\" : \"lib/ace/\"," + newLine);
		builder.append("\t\"Application Theme Path\" : \"resources/application/styling\"," + newLine);
		builder.append("\t\"Languages\" : {" + newLine);
		builder.append("\t\t\"base path\" : \"resources/languages/\"," + newLine);
		builder.append("\t\t\"modes path\" : \"modes/\"," + newLine);
		builder.append("\t\t\"default path\" : \"plp/\"" + newLine);
		builder.append("\t}" + newLine);
		builder.append("}" + newLine);
		
		return builder.toString();
	}
}
