package edu.asu.plp.tool.prototype.model;

import java.util.List;

/**
 * Created by Morgan on 2/28/2016.
 */
public interface OptionSection extends List<OptionSection>
{
	String getName();

	String getFullPath();

	void setParent(String parentPath);
}
