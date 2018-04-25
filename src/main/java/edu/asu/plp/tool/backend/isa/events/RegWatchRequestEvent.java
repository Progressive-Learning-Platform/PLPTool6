package edu.asu.plp.tool.backend.isa.events;

/**
 * @brief Event to denote Register Watch Request. It is used to retrieve the status and value of the register
 * 	      It offers communication between the front-end and the back-end
 */
public class RegWatchRequestEvent {

	/**
	 * String Variable to store the registerName
	 */
	private String registerName;

	/**
	 * @brief This is a constructor which accepts registerName parameter
	 * 		  It is used to watch the register values and send it to the front-end
	 * @param registerName       - It takes the registerName parameter
	 * If the registerName is invalid, the response will indicate failed.
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
