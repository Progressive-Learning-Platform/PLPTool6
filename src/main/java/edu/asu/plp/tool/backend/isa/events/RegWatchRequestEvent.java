package edu.asu.plp.tool.backend.isa.events;

/**
 * Class to denote Register Request Event
 */
public class RegWatchRequestEvent {

	/**
	 * String Variable to store the registerName
	 */
	private String registerName;

	/**
	 * This is a constructor for which accepts registerName parameter
	 * @param {String} registerName       - It takes the registerName parameter
	 */
	public RegWatchRequestEvent(String registerName) {
		this.registerName = registerName;
	}

	/**
	 * Function returns the name of the register
	 */
	public String getRegisterName() {
		return registerName;
	}

}
