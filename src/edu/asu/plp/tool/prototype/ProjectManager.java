package edu.asu.plp.tool.prototype;

import java.io.File;

import javafx.collections.ObservableList;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.prototype.model.Project;

public class ProjectManager
{
	public static class ProjectAlreadyOpenException extends Exception
	{
		// TODO
	}
	
	public static class NameConflictException extends Exception
	{
		// TODO
	}
	
	private ObservableList<Project> projects;
	
	public ProjectManager(ObservableList<Project> projects)
	{
		this.projects = projects;
	}
	
	public void addProject(Project project) throws ProjectAlreadyOpenException,
			NameConflictException
	{
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
	
	public void openProjectFromFile(File file) throws ProjectAlreadyOpenException,
			NameConflictException
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void saveActiveProject()
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void saveActiveProjectAs(String filePath)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void saveActiveProjectAs(String filePath, String projectType)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void createNewASM(String name, String type)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void importASM(String filePath)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void exportASM(String asmName)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void removeASM(String asmName)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void setMainASMFile(String asmName)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void setMainASMFile(ASMFile asmFile)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void setMainASMFile(Project project, ASMFile asmFile)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public void setMainASMFile(Project project, String asmName)
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public String[] getSupportedASMTypes()
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	public String[] getSupportedProjectTypes()
	{
		// TODO
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
