package edu.asu.plp.tool.backend.isa.events;

import java.util.List;

import edu.asu.plp.tool.backend.isa.ASMFile;

/**
 * This class is controls the assembler and manages the events
 */
public class AssemblerControlEvent {
	
	private String command;
	private String projectName;
	private String projectType;
	private List<ASMFile> assemblerFiles;

	/**
	 *
	 * @param command		It take the command for the assembler to process the request
	 * @param projectName		The name of the project for which the control event should be generated
	 * @param projectType		It was the project type indicating whether the project is PLP6 or legacy project
	 * @param assemblerFiles	List of assembler files for the processing
	 */
	public AssemblerControlEvent(String command, String projectName, String projectType, List<ASMFile> assemblerFiles) {
		this.command = command;
		this.projectName = projectName;
		this.projectType = projectType;
		this.assemblerFiles = assemblerFiles;
	}

	/**
	 *
	 * @return the command to the assembler
	 */
	public String getCommand() {
		return command;
	}

	/**
	 *
	 * @return the name of the project
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 *
	 * @return the type of the project
	 */
	public String getProjectType() {
		return projectType;
	}

	/**
	 *
	 * @return the list of assembler files
	 */
	public List<ASMFile> getAssemblerFiles() {
		return assemblerFiles;
	}

}
