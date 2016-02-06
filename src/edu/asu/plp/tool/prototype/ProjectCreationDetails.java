package edu.asu.plp.tool.prototype;

import java.io.File;

public class ProjectCreationDetails
{
	private String projectName;
	private String mainSourceFileName;
	private String projectLocation;
	private File projectDirectory;
	
	public ProjectCreationDetails(String projectName, String mainSourceFileName,
			String projectLocation)
	{
		this.projectName = projectName;
		this.mainSourceFileName = mainSourceFileName;
		this.projectLocation = projectLocation;
		this.projectDirectory = new File(projectLocation);
	}
	
	public String getProjectName()
	{
		return projectName;
	}
	
	public void setProjectName(String projectName)
	{
		this.projectName = projectName;
	}
	
	public String getMainSourceFileName()
	{
		return mainSourceFileName;
	}
	
	public void setMainSourceFileName(String mainSourceFileName)
	{
		this.mainSourceFileName = mainSourceFileName;
	}
	
	public String getProjectLocation()
	{
		return projectLocation;
	}
	
	public void setProjectLocation(String projectLocation)
	{
		this.projectLocation = projectLocation;
		this.projectDirectory = new File(projectLocation);
	}
	
	public File getProjectDirectory()
	{
		return projectDirectory;
	}
	
	public void setProjectDirectory(File projectDirectory)
	{
		this.projectDirectory = projectDirectory;
		this.projectLocation = projectDirectory.getAbsolutePath();
	}
}
