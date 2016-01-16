package edu.asu.plp.tool.prototype.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PLPSourceFile
{
	private static final String ENCODING_NAME = "UTF-8";
	private static final String FILE_EXTENSION = ".asm";
	
	private PLPProject project;
	private StringProperty nameProperty;
	private StringProperty contentProperty;
	
	public PLPSourceFile(PLPProject project, String name)
	{
		this.project = project;
		this.nameProperty = new SimpleStringProperty(name);
		this.contentProperty = new SimpleStringProperty();
	}
	
	public boolean writeToFile(File file) throws IOException
	{
		return writeToFile(file, true);
	}
	
	public boolean writeToFile(File file, boolean overwrite) throws IOException
	{
		String content = getContent();
		content = (content != null) ? content : "";
		
		if (file.isDirectory())
		{
			String filePath = file.getAbsolutePath() + "/" + constructFileName();
			file = new File(filePath);
		}
		
		if (!file.exists())
			file.createNewFile();
		else if (!overwrite)
			return false;
		
		List<String> lines = Collections.singletonList(content);
		Path path = file.toPath();
		Files.write(path, lines, Charset.forName(ENCODING_NAME));
		
		return true;
	}
	
	/**
	 * Writes the contents of this {@link PLPSourceFile}, specified by {@link #getContent()}
	 * , to the specified path, overwriting the current contents.
	 * <p>
	 * This method is equivalent to {@link #writeToFile(String, boolean)} with the
	 * parameters (path, true).
	 * <p>
	 * See {@link #writeToFile(String, boolean)} for more details.
	 * 
	 * @param path
	 *            Path to a directory or file at which to save this file.
	 * @return True if the file was written, false otherwise.
	 * @throws IOException
	 *             if the file cannot be overwritten, the path is invalid, the path is
	 *             restricted, or the write fails from another IO issue.
	 */
	public boolean writeToFile(String path) throws IOException
	{
		return writeToFile(path, true);
	}
	
	/**
	 * Writes the contents of this {@link PLPSourceFile}, specified by {@link #getContent()}
	 * , to the specified path.
	 * <p>
	 * If the path references a file, the specified name will be used. If the specified
	 * file already exists, this operation will overwrite it only if the "overwrite"
	 * parameter is set to true. If it is set to false, this method will not overwrite the
	 * file, and return false.
	 * <p>
	 * If the path references a directory, this file will be named according to this
	 * file's {@link #nameProperty()}, with the extension "{@value #FILE_EXTENSION}". If
	 * this files name is null, {@link IllegalStateException} will be thrown.
	 * <p>
	 * If the file cannot be overwritten, the path is invalid, the path is restricted, or
	 * the write fails from another IO issue, and {@link IOException} will be thrown.
	 * 
	 * @param path
	 *            Path to a directory or file at which to save this file.
	 * @param overwrite
	 *            True if this operation should overwrite a pre-existing file, false
	 *            otherwise.
	 * @return True if the file was written, false otherwise.
	 * @throws IOException
	 *             if the file cannot be overwritten, the path is invalid, the path is
	 *             restricted, or the write fails from another IO issue.
	 */
	public boolean writeToFile(String path, boolean overwrite) throws IOException
	{
		File file = new File(path);
		return writeToFile(file, overwrite);
	}
	
	private String constructFileName()
	{
		// TODO: check name for illegal characters
		String name = getName();
		if (name == null)
			throw new IllegalStateException("Null file name is not allowed");
		else
			return name + FILE_EXTENSION;
	}
	
	public String getName()
	{
		return nameProperty.get();
	}
	
	public void setName(String name)
	{
		nameProperty.set(name);
	}
	
	public StringProperty nameProperty()
	{
		return nameProperty;
	}
	
	public String getContent()
	{
		return contentProperty.get();
	}
	
	public void setContent(String content)
	{
		contentProperty.set(content);
	}
	
	public StringProperty contentProperty()
	{
		return contentProperty;
	}
	
	public PLPProject getProject()
	{
		return project;
	}
}
