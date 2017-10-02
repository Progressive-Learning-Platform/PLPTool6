package edu.asu.plp.tool.backend.isa.events;

/**
 */
public class RegWatchRequestEvent {

	/**
	 * String Variable to store the registerName
	 */
	private String registerName;

	/**
	 * @brief This is a constructor which accepts registerName parameter
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
