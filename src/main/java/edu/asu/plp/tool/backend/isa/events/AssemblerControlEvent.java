package edu.asu.plp.tool.backend.isa.events;

import java.util.List;

import edu.asu.plp.tool.backend.isa.ASMFile;

public class AssemblerControlEvent {
	
	private String command;
	private String projectName;
	private String projectType;
	private List<ASMFile> assemblerFiles;

	public AssemblerControlEvent(String command, String projectName, String projectType, List<ASMFile> assemblerFiles) {
		this.command = command;
		this.projectName = projectName;
		this.projectType = projectType;
		this.assemblerFiles = assemblerFiles;
	}

	public String getCommand() {
		return command;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getProjectType() {
		return projectType;
	}

	public List<ASMFile> getAssemblerFiles() {
		return assemblerFiles;
	}

}
