package edu.asu.plp.tool.prototype.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nesbitt, Morgan
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
			+ ".ace_gutter-cell.ace_breakpoint{ \n"
			+ "    border-radius: 20px 0px 0px 20px; \n"
			+ "    box-shadow: 0px 0px 1px 1px red inset; \n"
			+ "}"
			+ "</style>" 
			+ "</head>";
	//@formatter:on

	private static final String tail = "</body></html>";

	private String currentTheme;
	private String currentSessionMode;

	private List<EditorRoutine> editorRoutines;

	private StringProperty currentBodyProperty;

	private StringProperty fullPage;

	public AceEditor()
	{
		ApplicationSetting setting = ApplicationSetting.EDITOR_THEME;
		String editorTheme =
				SettingUtil.loadRequiredSavedSettingDefaultIfNotPresent(setting);

		currentTheme = editorTheme;
		currentSessionMode = "plp";
		currentBodyProperty = new SimpleStringProperty();
		editorRoutines = new ArrayList<>();

		fullPage = new SimpleStringProperty();
		buildPage();
	}

	public void addCustomJavascriptRoutine( EditorRoutine routine )
	{
		this.editorRoutines.add(routine);
		buildPage();
	}

	public StringProperty getPage()
	{
		return fullPage;
	}

	public StringProperty getBodyProperty()
	{
		return currentBodyProperty;
	}

	public boolean setTheme( String themeName )
	{
		throw new UnsupportedOperationException();
	}

	public boolean setSessionMode( String themeName )
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
		builder.append("editor.on(\"guttermousedown\", function(e){ \n" +
							   "    var target = e.domEvent.target; \n" +
							   "    if (target.className.indexOf(\"ace_gutter-cell\") == -1) \n" +
							   "        return; \n" +
							   "    if (!editor.isFocused()) \n" +
							   "        return; \n" +
							   "    if (e.clientX > 25 + target.getBoundingClientRect().left) \n" +
							   "        return; \n" +
							   "\n" +
							   "    var row = e.getDocumentPosition().row \n" +
							   "	var session = e.editor.session \n" +
							   "	var breakPointArray = session.getBreakpoints(); \n" +
							   "	if(breakPointArray[row] != null) \n" +
							   "	{ \n" +
							   "		session.clearBreakpoint(row); \n" +
							   "		return; \n" +
							   "	}" +
							   "	else \n" +
							   "	{" +
							   "		session.setBreakpoint(row); \n" +
							   "	}" +
							   "    e.stop() \n" +
							   "}) \n");

		builder.append("editor.setTheme(\"ace/theme/" + currentTheme + "\");");
		builder.append("editor.getSession().setMode(\"ace/mode/" + currentSessionMode
							   + "\");");
		builder.append(getJavascriptRoutines());
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
		catch ( MalformedURLException e )
		{
			e.printStackTrace();
		}
		builder.append("<script src=\"" + filePath + "\" ");
		builder.append("text=\"text/javascript\" ");
		builder.append("charset=\"utf-8\">");
		builder.append("</script>");

		return builder.toString();
	}

	private String getJavascriptRoutines()
	{
		if ( editorRoutines.size() > 0 )
		{
			StringBuilder builder = new StringBuilder();

			for ( EditorRoutine routine : editorRoutines )
			{
				builder.append(routine.get());
			}

			return builder.toString();
		}
		else
			return "";
	}
}
