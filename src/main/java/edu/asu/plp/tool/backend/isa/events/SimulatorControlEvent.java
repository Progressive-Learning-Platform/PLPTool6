package edu.asu.plp.tool.backend.isa.events;


/**
 * The SimulatorControlEvent Class is invoked in the classes of Main.java and PLPWebController.java, offering event or
 * communication event from these classes to back end, along with the parameters of the command being executed in PLP,
 * the project type and an object of simulator data, which are passed as an arguments and recieved in the constructor
 * of this class. These are defined as three properties with their getter and setter methods in SimulatorControlEvent.
 */

public class SimulatorControlEvent {
	
	private String command;
	private String projectType;
	private Object simulatorData;
	/**
	 * @brief A constructor to initialize the command, project type and an object passed as an argument.
	 * @param command is a front end command given at plp, which takes the values of "load", "step", "reset" and "pause"
	 * @param projectType indicating whether the project is PLP6 or legacy project.
	 * @param simulatorData used by backend classes like PLPSimulator.java, when load command is given
	 * @note if the projectType and simulatorData doesn't exist, they are passed as an empty string and a null value resp.
	 */

	public SimulatorControlEvent(String command, String projectType, Object simulatorData) {
		this.command = command;
		this.projectType = projectType;
		this.simulatorData = simulatorData;
	}
	/**
	 * @brief This method used by the back-end classes like PLPSimulator.java, to fetch the command values of load, step, reset and pause.
	 * @return returns command value (in String)
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @brief It was the project type indicating whether the project is PLP6 or legacy project.
	 * @return the type of the project.
	 */

	public String getProjectType() {
		return projectType;
	}

	/**
	 * @brief This is a getter method used by backend classes like PLPSimulator.java, when load command is given
	 * @return returns the object of simulator Data.
	 */

	public Object getSimulatorData() {
		return simulatorData;
	}

}
