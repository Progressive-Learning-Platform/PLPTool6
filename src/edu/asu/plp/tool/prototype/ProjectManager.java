package edu.asu.plp.tool.prototype;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import org.apache.commons.io.FilenameUtils;

import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.prototype.model.Project;

public class ProjectManager
{
	public static class UnsupportedFileExtensionException extends Exception
	{
		public UnsupportedFileExtensionException()
		{
			super();
		}
		
		public UnsupportedFileExtensionException(String message)
		{
			super(message);
		}
	}
	
	public static class ProjectAlreadyOpenException extends Exception
	{
		private Project existingProject;
		
		public ProjectAlreadyOpenException(Project existingProject)
		{
			super();
			this.existingProject = existingProject;
		}
		
		public ProjectAlreadyOpenException(Project existingProject, String message)
		{
			super(message);
			this.existingProject = existingProject;
		}
		
		public Project getExistingProject()
		{
			return existingProject;
		}
	}
	
	public static class NameConflictException extends Exception
	{
		private Project conflictingProject;
		
		public NameConflictException(Project conflictingProject)
		{
			super();
			this.conflictingProject = conflictingProject;
		}
		
		public NameConflictException(Project conflictingProject, String message)
		{
			super(message);
			this.conflictingProject = conflictingProject;
		}
		
		public Project getConflictingProject()
		{
			return conflictingProject;
		}
	}
	
	@FunctionalInterface
	public static interface SaveFunction
	{
		void save(Project project, File file);
	}
	
	@FunctionalInterface
	public static interface LoadFunction
	{
		Project load(File file);
	}
	
	public static class ProjectType
	{
		private String name;
		private String extension;
		private SaveFunction saveFunction;
		private LoadFunction loadFunction;
		
		public ProjectType(String name, String extension, SaveFunction saveFunction,
				LoadFunction loadFunction)
		{
			super();
			this.name = name;
			this.extension = extension;
			this.saveFunction = saveFunction;
			this.loadFunction = loadFunction;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getExtension()
		{
			return extension;
		}
		
		public SaveFunction saveFunction()
		{
			return saveFunction;
		}
		
		public LoadFunction loadFunction()
		{
			return loadFunction;
		}
	}
	
	private ObservableList<Project> projects;
	private ObjectProperty<Project> activeProjectProperty;
	private ObservableList<ProjectType> supportedProjectTypes;
	
	public ProjectManager(ObservableList<Project> projects)
	{
		this.projects = projects;
		this.activeProjectProperty = new SimpleObjectProperty<>();
	}
	
	public void addProject(Project project) throws ProjectAlreadyOpenException,
			NameConflictException
	{
		Project existingProject = getProjectByName(project.getName());
		if (existingProject != null)
		{
			if (existingProject.getPath().equals(project.getPath()))
				throw new ProjectAlreadyOpenException(existingProject);
			else
				throw new NameConflictException(existingProject);
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
			NameConflictException, FileNotFoundException,
			UnsupportedFileExtensionException
	{
		if (!file.exists())
			throw new FileNotFoundException();
		
		String extension = FilenameUtils.getExtension(file.getAbsolutePath());
		Predicate<ProjectType> filter = (type) -> extension.equals(type.getExtension());
		ObservableList<ProjectType> validTypes = supportedProjectTypes.filtered(filter);
		
		if (validTypes.isEmpty())
		{
			throw new UnsupportedFileExtensionException(extension);
		}
		else
		{
			ProjectType type = supportedProjectTypes.get(0);
			Project project = type.loadFunction().load(file);
			this.addProject(project);
		}
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
		Project activeProject = getActiveProject();
		Predicate<ASMFile> filter = (asm) -> asm.getName().equals(asmName);
		List<ASMFile> list = activeProject.filtered(filter);
		
		if (list.isEmpty())
		{
			throw new IllegalArgumentException("Active poject {"
					+ activeProject.getName() + "} does not contain the given file {"
					+ asmName + "}");
		}
		else if (list.size() == 1)
		{
			setMainASMFile(list.get(0));
		}
		else
		{
			throw new IllegalStateException("Project {" + activeProject.getName()
					+ "} contains duplicate file names.");
		}
	}
	
	public void setMainASMFile(ASMFile asmFile)
	{
		Project activeProject = getActiveProject();
		int index = activeProject.indexOf(asmFile);
		
		if (index < 0)
		{
			throw new IllegalArgumentException("Active poject {"
					+ activeProject.getName() + "} does not contain the given file {"
					+ asmFile.getName() + "}");
		}
		
		Collections.swap(activeProject, 0, index);
	}
	
	public String[] getSupportedProjectTypeNames()
	{
		int length = supportedProjectTypes.size();
		String[] typeNames = new String[length];
		
		for (int index = 0; index < length; index++)
		{
			ProjectType type = supportedProjectTypes.get(index);
			typeNames[index] = type.getName();
		}
		
		return typeNames;
	}
	
	public ObjectProperty<Project> getActiveProjectProperty()
	{
		return activeProjectProperty;
	}
	
	public Project getActiveProject()
	{
		return activeProjectProperty.get();
	}
	
	public void setActiveProject(String projectName) throws IllegalArgumentException
	{
		Project project = getProjectByName(projectName);
		if (project == null)
			throw new IllegalArgumentException("Project does not exist: " + projectName);
		activeProjectProperty.set(project);
	}
	
	public ObservableList<ProjectType> getSupportedProjectTypesProperty()
	{
		return supportedProjectTypes;
	}
}
