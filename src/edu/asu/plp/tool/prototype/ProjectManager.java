package edu.asu.plp.tool.prototype;

import java.io.File;

import javafx.collections.ObservableList;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.prototype.model.Project;

public class ProjectManager
{
	public static class ProjectAlreadyOpenException extends Exception{}
	public static class NameConflictException extends Exception{}
	
	private ObservableList<Project> projects;
	
	public ProjectManager(ObservableList<Project> projects)
	{
		this.projects = projects;
	}
	
	public void addProject(Project project) throws ProjectAlreadyOpenException, NameConflictException{
		Project existingProject = getProjectByName(project.getName());
		if (existingProject != null)
		{
			if (existingProject.getPath().equals(project.getPath()))
				throw new ProjectAlreadyOpenException();
			else
				throw new NameConflictException();
		}
		else
		{
			projects.add(project);
		}
	}
	
	public Project getProjectByName(String name)
	{
		for (Project project : projects)
		{
			String projectName = project.getName();
			boolean namesAreNull = (projectName == null && name == null);
			if (namesAreNull || name.equals(projectName))
				return project;
		}
		
		return null;
	}
	
	public void openProjectFromFile(File file) throws ProjectAlreadyOpenException, NameConflictException{}
	
	public void saveActiveProject(){}
	
	public void saveActiveProjectAs(String filePath){}
	
	public void saveActiveProjectAs(String filePath, String projectType){}
	
	public void createNewASM(String name, String type){}
	
	public void importASM(String filePath){}
	
	public void exportASM(String asmName){}
	
	public void removeASM(String asmName){}
	
	public void setMainASMFile(String asmName){}
	
	public void setMainASMFile(ASMFile asmFile){}
	
	public void setMainASMFile(Project project, ASMFile asmFile){}
	
	public void setMainASMFile(Project project, String asmName){}
	
	public String[] getSupportedASMTypes() {return null;}
	
	public String[] getSupportedProjectTypes() {return null;}
}
