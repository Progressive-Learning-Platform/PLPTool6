package edu.asu.plp.tool.prototype.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;

import edu.asu.plp.tool.backend.util.FileUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 * @author Nesbitt, Morgan
 * 		
 */
public class AceEditor
{
	//@formatter:off
	private static final String head = "<!DOCTYPE html>\n" 
			+ "<html lang=\"en\">\n" 
			+ "<head>\n"
			+ "<title></title>\n" 
			+ "<style type=\"text/css\" media=\"screen\">\n"
			+ "#editor {\n" 
			+ "position: absolute;\n" 
			+ "top: 0;\n" 
			+ "right: 0;\n" 
			+ "bottom: 0;\n"
			+ "left: 0;\n" 
			+ "}\n" 
			+ "</style>\n" 
			+ "</head>\n";

	//@formatter:on
	
	private static final String tail = "</body></html>";
	
	private String currentTheme;
	private String currentSessionMode;
	
	private StringProperty currentBodyProperty;
	
	private StringProperty fullPage;
	
	public AceEditor()
	{
		currentTheme = "ambiance";
		currentSessionMode = "javascript";
		currentBodyProperty = new SimpleStringProperty();
		currentBodyProperty.set(getSampleBody());
		
		fullPage = new SimpleStringProperty();
		buildPage();
		
		currentBodyProperty.addListener((observable, oldValue, newValue) -> {
			buildPage();
		});
	}
	
	public StringProperty getPage()
	{
		return fullPage;
	}
	
	public StringProperty getBodyProperty()
	{
		return currentBodyProperty;
	}
	
	public boolean setTheme(String themeName)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean setSessionMode(String themeName)
	{
		throw new UnsupportedOperationException();
	}
	
	private void buildPage()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(head);
		builder.append(getBody());
		builder.append(getAceImportScript());
		builder.append(getEditorSettings());
		builder.append(tail);
		
		fullPage.set(builder.toString());
	}
	
	private String getBody()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"editor\">");
		builder.append(currentBodyProperty.get());
		builder.append("</div>");
		return builder.toString();
	}
	
	private String getEditorSettings()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<script>");
		builder.append("var editor = ace.edit(\"editor\");");
		builder.append("editor.setTheme(\"ace/theme/" + currentTheme + "\");");
		builder.append(
				"editor.getSession().setMode(\"ace/mode/" + currentSessionMode + "\");");
		builder.append("</script>");
		
		return builder.toString();
	}
	
	private String getAceImportScript()
	{
		StringBuilder builder = new StringBuilder();
		
		File expectedAceFilePath = new File("lib/ace/ace.js");
		String filePath = expectedAceFilePath.getAbsolutePath().toString();
		try
		{
			filePath = expectedAceFilePath.toURI().toURL().toString();
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		builder.append("<script src=\"" + filePath + "\" ");
		builder.append("text=\"text/javascript\" ");
		builder.append("charset=\"utf-8\">");
		builder.append("</script>");
		
		return builder.toString();
	}
	
	private String getSampleBody()
	{
		String sampleBody = ".org 0x10000000";
		
		try
		{
			sampleBody = FileUtil.readAllLines(new File(
					"examples/Stripped PLP Projects (ASM Only)/memtest/main.asm"));
					
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return sampleBody;
	}
	
}
