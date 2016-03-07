package edu.asu.plp.tool.prototype;

import edu.asu.plp.tool.prototype.model.Setting;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

/**
 * Global map that holds settings configurations. Derives from a .settings file (JSON Format). If the file is not loaded
 * or cannot be loaded, then the default settings will be used.
 * <p>
 * See {@link edu.asu.plp.tool.prototype.model.ApplicationSetting} for the base required values.
 * <p>
 * To use this settings class, you must call {@link ApplicationSettings#initialize()} before attempting any usage.
 * <p>
 * You must also provide it with a settings file by {@link ApplicationSettings#loadFromFile(String)} or {@link
 * ApplicationSettings#loadFromFile(File)}. Or load from the {@link ApplicationSettings#DEFAULT_SETTINGS_FILE} by
 * calling {@link ApplicationSettings#loadFromFile()}.
 * <p>
 * This will populate the map with all the given values. To retrieve a value from the map call {@link
 * ApplicationSettings#getSetting(String)} with the respective key and it will return the value wrapped in an Optional
 * if it exists or return an empty Optional otherwise.
 * <p>
 * You may also pass a {@link Setting} to {@link ApplicationSettings#getSetting(Setting)}. Please see {@link Setting}
 * for information on how to use/implement {@link Setting}.
 *
 * @author Nesbitt, Morgan Created on 2/23/2016.
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

	public static Optional<String> getSetting( String key )
	{
		if ( settings.containsKey(key) )
			return Optional.of(settings.get(key));

		return Optional.empty();
	}

	public static Optional<String> getSetting( Setting setting )
	{
		return getSetting(setting.toString());
	}

	public static ApplicationSettings initialize()
	{
		if ( instance == null )
			instance = new ApplicationSettings();

		return instance;
	}

	public static final boolean loadFromFile()
	{
		return loadFromFile(DEFAULT_SETTINGS_FILE);
	}

	public static final boolean loadFromFile( String filePath )
	{
		return loadFromFile(new File(filePath));
	}

	public static final boolean loadFromFile( File file )
	{
		if ( !file.isFile() )
		{
			loadFromDefaultSettings();
			return false;
		}

		try
		{
			String fileContents = FileUtils.readFileToString(file);
			JSONObject jsonFile = new JSONObject(fileContents);

			parseJSONSettings(jsonFile, "");

			if ( settings.isEmpty() )
			{
				loadFromDefaultSettings();
				return false;
			} else
				return true;
		}
		catch ( IOException e )
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

	private static void parseJSONSettings( JSONObject jsonSettings, String basePath )
	{
		for ( String key : JSONObject.getNames(jsonSettings) )
		{
			Object value = jsonSettings.get(key);
			if ( value instanceof String )
			{
				settings.put(bindPath(basePath, key), (String) value);
			} else
			{
				parseJSONSettings(jsonSettings.getJSONObject(key), bindPath(basePath, key));
			}
		}
	}

	private static String bindPath( String basePath, String currentPath )
	{
		if ( basePath.isEmpty() )
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
		//Application Theme
		builder.append("\t\"Application Theme\" : {" + newLine);
		builder.append("\t\t\"base path\" : \"resources/application/styling/\"," + newLine);
		builder.append("\t\t\"default path\" : \"seti/\"," + newLine);
		builder.append("\t}," + newLine);
		//Languages
		builder.append("\t\"Languages\" : {" + newLine);
		builder.append("\t\t\"base path\" : \"resources/languages/\"," + newLine);
		builder.append("\t\t\"modes path\" : \"modes/\"," + newLine);
		builder.append("\t\t\"default path\" : \"plp/\"" + newLine);
		builder.append("\t}," + newLine);
		//Editor
		builder.append("\t\"Editor\" : {" + newLine);
		builder.append("\t\t\"font\" : \"inconsolata\"," + newLine);
		builder.append("\t\t\"font size\" : \"14\"," + newLine);
		builder.append("\t\t\"mode\" : \"plp\"," + newLine);
		builder.append("\t\t\"theme\" : \"monokai\"" + newLine);
		builder.append("\t}," + newLine);
		//Application
		builder.append("\t\"Application\" : {" + newLine);
		builder.append("\t\t\"theme\" : \"seti\"," + newLine);
		builder.append("\t}," + newLine);
		//Programmer
		builder.append("\t\"Programmer\" : {" + newLine);
		builder.append("\t\t\"program in chunks\" : \"true\"," + newLine);
		builder.append("\t\t\"maximum chunk size\" : \"2048\"," + newLine);
		builder.append("\t\t\"timeout in milliseconds\" : \"500\"," + newLine);
		builder.append("\t\t\"auto detect serial ports\" : \"true\"" + newLine);
		builder.append("\t}," + newLine);
		//Simulator
		builder.append("\t\"Simulator\" : {" + newLine);
		builder.append("\t\t\"speed\" : \"100\"," + newLine);
		builder.append("\t\t\"allow execution of non instruction memory\" : \"true\"," + newLine);
		builder.append("\t\t\"assume zero on reads from uninitialized memory\" : \"true\"" + newLine);
		builder.append("\t}" + newLine);

		builder.append("}" + newLine);

//		System.out.println(builder.toString());

		return builder.toString();
	}
}
