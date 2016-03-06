package edu.asu.plp.tool.prototype.view.menu.options.details;

import edu.asu.plp.tool.prototype.model.ApplicationSetting;
import edu.asu.plp.tool.prototype.model.SettingUtil;

/**
 * @author Nesbitt, Morgan on 2/27/2016.
 */
public class ProgrammerSettingDetails
{
	public static final ProgrammerSettingDetails DEFAULT = ProgrammerSettingDetails.defaultDetails();

	private String programInChunks;
	private String maximumChunkSize;
	private String receiveTimeoutMilliseconds;
	private String autoDetectSerialPorts;

	public ProgrammerSettingDetails()
	{
		this(DEFAULT);
	}

	public ProgrammerSettingDetails(ProgrammerSettingDetails details)
	{
		this.programInChunks = details.programInChunks;
		this.maximumChunkSize = details.maximumChunkSize;
		this.receiveTimeoutMilliseconds = details.receiveTimeoutMilliseconds;
		this.autoDetectSerialPorts = details.autoDetectSerialPorts;
	}

	private static ProgrammerSettingDetails defaultDetails()
	{
		ProgrammerSettingDetails details = new ProgrammerSettingDetails();

		ApplicationSetting setting = ApplicationSetting.PROGRAMMER_PROGRAM_IN_CHUNKS;
		details.programInChunks =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.PROGRAMMER_MAXIMUM_CHUNK_SIZE;
		details.maximumChunkSize =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.PROGRAMMER_TIMEOUT_MILLISECONDS;
		details.receiveTimeoutMilliseconds =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		setting = ApplicationSetting.PROGRAMMER_AUTO_DETECT_SERIAL_PORTS;
		details.autoDetectSerialPorts =
				SettingUtil.loadSavedSettingDefaultIfNotPresent(SettingUtil.prependSaveLabel(setting), setting);

		return details;
	}
}
