package edu.asu.plp.tool.prototype.model;

public class QuickViewEntry
{
	private String content;
	private String description;
	
	public QuickViewEntry(String content, String description)
	{
		super();
		this.content = content;
		this.description = description;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public String getDescription()
	{
		return description;
	}
}
