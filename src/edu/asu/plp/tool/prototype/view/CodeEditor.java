package edu.asu.plp.tool.prototype.view;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import edu.asu.plp.tool.prototype.model.AceEditor;

/**
 * Accessible CodeEditor panel supporting syntax highlighting and data binding.
 * <p>
 * This panel accepts text input while it is focused, and can be navigated and manipulated
 * using the keyboard. Changes in the text of this editor can be observed using
 * {@link #addListener(ChangeListener)}.
 * <p>
 * Note that the accessibility property of this {@link Node} is bound to this editor's
 * text value. Any changes made to this node's accessible text property using
 * {@link #accessibleTextProperty()} and {@link ObjectProperty#set(Object)} will be
 * overwritten if the text of this editor is changed. As such, it is advised that the
 * accessible text property of this node not be altered outside of this class.
 * 
 * @author Moore, Zachary
 * @author Hawks, Elliott
 * @author Nesbitt, Morgan
 *
 */
public class CodeEditor extends BorderPane implements ObservableStringValue
{	
	private WebView webView;
	private AceEditor aceEditor;
	private StringProperty codeBodyProperty;
	private StringProperty acePageContentsProperty;
	
	public CodeEditor()
	{
		webView = new WebView();
		aceEditor = new AceEditor();
		
		codeBodyProperty = aceEditor.getBodyProperty();
		acePageContentsProperty = aceEditor.getPage();
		
		addDefaultRoutines();
		initializeEngineEvents();
		
		webView.getEngine().loadContent(aceEditor.getPage().get());
		
		// Add interface to access Java model from Javascript
		JSObject jsObject = (JSObject) webView.getEngine().executeScript("window");
		jsObject.setMember("javaContentModel", this);
		
		acePageContentsProperty.addListener((observable, old, newValue) -> 
		{
			webView.getEngine().loadContent(newValue);
		});
		
		codeBodyProperty.addListener(
				(observable, old, newValue) -> System.out.println("Code changed"));
		
		webView.setContextMenuEnabled(false);
		//TODO create custom context menu (right click menu)
		
		// TODO: move this to a js file
		aceEditor.addCustomJavascriptRoutine(() -> "window.onload = function() {"
				+ "editor.on(\"change\", function() {"
				+ "javaContentModel.setText(editor.getValue());"
				+ "});"
				+ "};");
		
		setCenter(webView);
		this.accessibleRoleProperty().set(AccessibleRole.TEXT_AREA);
	}
	
	public StringProperty codeBodyProperty()
	{
		return codeBodyProperty;
	}
	
	public void setText(String text)
	{
		codeBodyProperty.set(text);
	}
	
	@Override
	public String get()
	{
		return codeBodyProperty.get();
	}
	
	@Override
	public String getValue()
	{
		return codeBodyProperty.getValue();
	}
	
	@Override
	public void addListener(ChangeListener<? super String> listener)
	{
		codeBodyProperty.addListener(listener);
	}
	
	@Override
	public void removeListener(ChangeListener<? super String> listener)
	{
		codeBodyProperty.removeListener(listener);
	}
	
	@Override
	public void addListener(InvalidationListener listener)
	{
		codeBodyProperty.addListener(listener);
	}
	
	@Override
	public void removeListener(InvalidationListener listener)
	{
		codeBodyProperty.removeListener(listener);
	}
	
	private void initializeEngineEvents()
	{
		webView.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
			if( e.isShortcutDown() && e.getCode() == KeyCode.V) 
			{
				String content = (String) Clipboard.getSystemClipboard().getContent(DataFormat.PLAIN_TEXT);
	            if (content != null) 
	            {
	            	webView.getEngine().executeScript("editor.onPaste('" + sanitizeForAce(content) + "');");
	            }
            }
		});
	}
	
	private String sanitizeForAce(String content)
	{
		String intermediary = content;
		
		intermediary = intermediary.replace(System.getProperty("line.separator"), "\\n");
		intermediary = intermediary.replace("\n", "\\n");
		intermediary = intermediary.replace("\r", "\\n");
		intermediary = intermediary.replace("'", "\\'");
		
		return intermediary;
	}

	// Custom Routines
	
	private void addDefaultRoutines()
	{
	}
}
