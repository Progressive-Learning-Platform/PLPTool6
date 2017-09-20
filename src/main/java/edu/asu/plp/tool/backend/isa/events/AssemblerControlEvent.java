package edu.asu.plp.tool.backend.isa.events;

import java.util.List;

import edu.asu.plp.tool.backend.isa.ASMFile;

/**
 ** This class is controls the assembler and manages the events
 **/
public class AssemblerControlEvent {
	
	private String command;
	private String projectName;
	private String projectType;
	private List<ASMFile> assemblerFiles;

     /**
     * This is a constructor for which accepts Command, ProjectName, ProjectType and AssemblerFiles parameters
     * @param {String} Command        - It take the command for the assembler
     * @param {string} ProjectName    - The name of the project
     * @param {string} ProjectType    - It was the project type
     * @param {string} AssemblerFiles - List of assembler files for the processing
     * **/

	public AssemblerControlEvent(String command, String projectName, String projectType, List<ASMFile> assemblerFiles) {
		this.command = command;
		this.projectName = projectName;
		this.projectType = projectType;
		this.assemblerFiles = assemblerFiles;
	}

    /**
    ** This is a function which return a command
    **/
	public String getCommand() {
		return command;
	}

    /**
     ** This method returns the name of the project
    **/
	public String getProjectName() {
		return projectName;
	}

	/**
	 ** This function returns the project type
    **/
	public String getProjectType() {
		return projectType;
	}

    /**
     ** This function returns the list of assembler files
    **/
	public List<ASMFile> getAssemblerFiles() {
		return assemblerFiles;
	}

}
