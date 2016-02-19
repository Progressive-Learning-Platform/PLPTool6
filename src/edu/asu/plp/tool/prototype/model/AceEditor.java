package edu.asu.plp.tool.prototype.model;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import edu.asu.plp.tool.backend.util.FileUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.input.Clipboard;

/**
 * 
 * @author Nesbitt, Morgan
 * 		
 */
public class AceEditor
{
	//@formatter:off
	private static final String head = "<!DOCTYPE html>" 
			+ "<html lang=\"en\">" 
			+ "<head>"
			+ "<title></title>" 
			+ "<style type=\"text/css\" media=\"screen\">"
			+ "#editor {" 
			+ "position: absolute;" 
			+ "top: 0;" 
			+ "right: 0;" 
			+ "bottom: 0;"
			+ "left: 0;" 
			+ "}" 
			+ "</style>" 
			+ "</head>";

	//@formatter:on
	
	private static final String tail = "</body></html>";
	
	private String currentTheme;
	private String currentSessionMode;
	
	private List<String> editorSettings;
	private List<EditorRoutine> editorRoutines;
	
	private StringProperty currentBodyProperty;
	
	private StringProperty fullPage;
	
	public AceEditor()
	{
		currentTheme = "ambiance";
		currentSessionMode = "plp";
		currentBodyProperty = new SimpleStringProperty();
		currentBodyProperty.set("");
		
		fullPage = new SimpleStringProperty();
		buildPage();
		
		currentBodyProperty.addListener((observable, oldValue, newValue) -> {
			buildPage();
		});
		
		editorSettings = new ArrayList<>();
		editorRoutines = new ArrayList<>();
		
		addDefaultEditorSettings();
		addCustomRoutines();
	}
	
	private void addDefaultEditorSettings()
	{
		
	}
	
	private void addCustomRoutines()
	{
		editorRoutines.add(this::paste);
	}
	
	private void paste()
	{
		
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
