package edu.asu.plp.tool.prototype;

import edu.asu.plp.tool.backend.isa.ASMImage;

public class ProjectAssemblyDetails
{
	private boolean isDirty;
	private ASMImage assembledImage;
	
	public ProjectAssemblyDetails()
	{
		this.isDirty = true;
		this.assembledImage = null;
		// TODO: add listeners to set isDirty when files are changed or added
	}
	
	public boolean isDirty()
	{
		return isDirty;
	}
	
	public void setDirty()
	{
		this.isDirty = true;
	}
	
	public ASMImage getAssembledImage()
	{
		return assembledImage;
	}
	
	public void setAssembledImage(ASMImage assembledImage)
	{
		this.assembledImage = assembledImage;
		this.isDirty = false;
	}
	
}
