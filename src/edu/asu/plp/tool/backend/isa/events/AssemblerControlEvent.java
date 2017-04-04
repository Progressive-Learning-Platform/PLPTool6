package edu.asu.plp.tool.backend.isa.events;

import java.util.List;

import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.prototype.model.Project;

public class AssemblerControlEvent {
	
	private String command;
	private String projectName;
	private Project assemblerFiles;

	public AssemblerControlEvent(String command, String projectName, Project assemblerFiles) {
		this.command = command;
		this.projectName = projectName;
		this.assemblerFiles = assemblerFiles;
	}

	public String getCommand() {
		return command;
	}

	public String getProjectName() {
		return projectName;
	}

	public Project getAssemblerFiles() {
		return assemblerFiles;
	}

}
