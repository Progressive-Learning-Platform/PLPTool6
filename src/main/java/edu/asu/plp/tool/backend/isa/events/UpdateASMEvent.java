package edu.asu.plp.tool.backend.isa.events;

/**
 * The UpdateASMEvent Class is invoked in the classes of SimpleASMFile.java and CodeEditor.java, offering event or
 * communication event from these classes to back end, along with the parameters of projectName,
 * asmFileName and fileContent, which are passed as an arguments and recieved in the constructor
 * of this class. These are defined as three properties with their getter and setter methods in UpdateASMEvent.
 */

public class UpdateASMEvent {

	private String projectName;
	private String asmFileName;
	private String fileContent;

	/**
	 * @brief A constructor to initialize the projectName, asmFileName and the fileContent passed as an argument.
	 * @param projectName is a front end argument given at plp, used to associate project name with Project Assembly details,in the
	 * Main.java, AssemblerControlEvent.java, ASMCreationPanel.java, SimpleASMFile.java.
	 * @param asmFileName used to set the text of the file name in the classes like ASMCreationPanel.java and SimpleASMFile.java
	 * @param fileContent used to access and parse and perform operations on the content of the file being written.
	 */
	public UpdateASMEvent(String projectName, String asmFileName, String fileContent) {
		this.projectName = projectName;
		this.asmFileName = asmFileName;
		this.fileContent = fileContent;
	}
	/**
	 * @brief The name of the project for which the control event should be generated
	 * @return returns String value of project name
	 */
	public String getProjectName() {
		return projectName;
	}
	/**
	 * @brief This method used by the back-end classes like PLPSimulator.java, to fetch the command values of load, step, reset and pause.
	 * @return returns asm file name value (in String)
	 */
	public String getAsmFileName() {
		return asmFileName;
	}
	/**
	 * @brief This method used used to access and parse and perform operations on the content of the file being written.
	 * @return returns file content value (in String)
	 */
	public String getFileContent() {
		return fileContent;
	}

}