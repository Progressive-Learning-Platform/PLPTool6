package edu.asu.plp.tool.backend.isa.events;

/**
 * Class to denote Register Request Event. It is used to retrieve the status and value of the register
 * It offers communication between the front-end and the back-end
 */
public class RegWatchRequestEvent {

	/**
	 * String Variable to store the registerName
	 */
	private String registerName;

	/**
	 * @brief This is a constructor which accepts registerName parameter
	 * @brief It is used to watch the register values and send it to the front-end
	 * @param {String} registerName       - It takes the registerName parameter
	 */
	public RegWatchRequestEvent(String registerName) {
		this.registerName = registerName;
	}

	/**
	 * @brief This is a getter method used to get the Register Name
	 * @return current register name (in String)
	 */
	public String getRegisterName() {
		return registerName;
	}

}
