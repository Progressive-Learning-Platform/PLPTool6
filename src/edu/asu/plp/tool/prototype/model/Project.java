package edu.asu.plp.tool.prototype.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A {@link Project} represents an ordered, observable collection of {@link ProjectFile}s
 * that can be assembled collectively as a single unit.
 * 
 * @author Moore, Zachary
 *
 */
public class Project extends ArrayListProperty<ProjectFile>
{
	private StringProperty nameProperty;
	
	public Project()
	{
		nameProperty = new SimpleStringProperty();
	}
	
	public Project(String name)
	{
		nameProperty = new SimpleStringProperty(name);
	}
	
	public StringProperty getNameProperty()
	{
		return nameProperty;
	}
	
	public String getName()
	{
		return nameProperty.get();
	}
	
	public void setName(String name)
	{
		nameProperty.set(name);
	}
}
