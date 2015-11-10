package edu.asu.plp.tool.prototype.model;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A {@link Project} represents an ordered, observable collection of {@link ProjectFile}s
 * that can be assembled collectively as a single unit.
 * 
 * @author Moore, Zachary
 *
 */
public class Project extends SimpleListProperty<ProjectFile>
{
	private StringProperty nameProperty;
	
	public Project()
	{
		nameProperty = new SimpleStringProperty();
	}
	
	public StringProperty getNameProperty()
	{
		return nameProperty;
	}
	
	@Override
	public String getName()
	{
		return nameProperty.get();
	}
}
