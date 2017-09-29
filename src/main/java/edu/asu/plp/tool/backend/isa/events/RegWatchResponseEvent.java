package edu.asu.plp.tool.backend.isa.events;

import javafx.beans.property.LongProperty;

/**
 * @brief Event to denote Register Watch Result. It returns a reference of the register object.
 * 	      It offers communication between the front-end and the back-end
 */
public class RegWatchResponseEvent {

	/**
	 * @brief Boolean Variable to store the status of the request result(True or False)
	 */
	private boolean success;

	/**
	 * @brief String variable to hold the registerID
	 */
	private String registerID;

	/**
	 * @brief String variable to hold the registerName
	 */
	private String registerName;

	/**
	 * @brief LongProperty class defines a Property wrapping a long value.
	 */
	private LongProperty regObject;

	/**
	 * @brief This is a constructor for which accepts Success, ErrorMessage, ProjectName and ASMImage parameters
	 * @param success       - It takes the register watch success parameter
	 * @param registerID     - It takes the registerID parameter
	 * @param registerName   - It takes the name of the register as a parameter
	 * @param regObject      - It takes the LongProperty object as a parameter
	 */
	public RegWatchResponseEvent(boolean success, String registerID, String registerName, LongProperty regObject) {
		this.success = success;
		this.registerID = registerID;
		this.registerName = registerName;
		this.regObject = regObject;
	}

	/**
	 * @brief Function returns success if the RegWatchResponseEvent is a success
	 * @return boolean (True or False)
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * @brief This is a getter method used to get the RegisterID
	 * @return registerID (in String)
	 */
	public String getRegisterID() {
		return registerID;
	}

	/**
	 * @brief This is a getter method used to get the RegisterName
	 * @return register Name (in String)
	 */
	public String getRegisterName() {
		return registerName;
	}

	/**
	 * @brief This is a getter method used to get the Register object
	 * @return register object (in LongProperty)
	 */
	public LongProperty getRegObject() {
		return regObject;
	}

}
