package edu.asu.plp.tool.backend.isa.events;

import java.util.List;

import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.prototype.model.Project;

public class AssemblerControlEvent {
	
	private String command;
	private String projectName;
	private String projectType;
	private Project assemblerFiles;

	public AssemblerControlEvent(String command, String projectName, String projectType, Project assemblerFiles) {
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

	public Project getAssemblerFiles() {
		return assemblerFiles;
	}

}
