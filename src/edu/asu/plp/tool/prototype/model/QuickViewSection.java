package edu.asu.plp.tool.prototype.model;

import java.util.List;

public class QuickViewSection
{
	private String title;
	private List<QuickViewEntry> entries;
	private String contentHeader;
	private String descriptionHeader;
	
	public QuickViewSection(String title, List<QuickViewEntry> entries)
	{
		super();
		this.title = title;
		this.entries = entries;
		setHeaders("Content", "Description");
	}
	
	public void setHeaders(String contentHeader, String descriptionHeader)
	{
		this.contentHeader = contentHeader;
		this.descriptionHeader = descriptionHeader;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public List<QuickViewEntry> getEntries()
	{
		return entries;
	}
	
	public String getContentHeader()
	{
		return contentHeader;
	}
	
	public void setContentHeader(String contentHeader)
	{
		this.contentHeader = contentHeader;
	}
	
	public String getDescriptionHeader()
	{
		return descriptionHeader;
	}
	
	public void setDescriptionHeader(String descriptionHeader)
	{
		this.descriptionHeader = descriptionHeader;
	}
}
