package edu.asu.plp.tool.backend.isa.events;

import javafx.beans.property.LongProperty;

/**
 * Class to denote Register Response Event
 */
public class RegWatchResponseEvent {

	/**
	 * Boolean Variable to store the status (True or False)
	 */
	private boolean success;

	/**
	 * String variable to hold the registerID
	 */
	private String registerID;

	/**
	 * String variable to hold the registerName
	 */
	private String registerName;

	/**
	 * LongProperty class defines a Property wrapping a long value.
	 */
	private LongProperty regObject;

	/**
	 * This is a constructor for which accepts Success, ErrorMessage, ProjectName and ASMImage parameters
	 * @param {boolean} Success       - It takes the register watch success parameter
	 * @param {String} registerID     - It takes the registerID parameter
	 * @param {String} registerName   - It takes the name of the register as a parameter
	 * @param {String} regObject      - It takes the LongProperty object as a parameter
	 */
	public RegWatchResponseEvent(boolean success, String registerID, String registerName, LongProperty regObject) {
		this.success = success;
		this.registerID = registerID;
		this.registerName = registerName;
		this.regObject = regObject;
	}

	/**
	 * Function returns success if the RegWatchResponseEvent is a success
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Getter Function to return registerID
	 */
	public String getRegisterID() {
		return registerID;
	}

	/**
	 * Getter Function to return RegisterName
	 */
	public String getRegisterName() {
		return registerName;
	}

	/**
	 * Getter Function to return RegisterObject
	 */
	public LongProperty getRegObject() {
		return regObject;
	}

}
