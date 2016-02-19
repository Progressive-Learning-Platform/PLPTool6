package edu.asu.plp.tool.prototype.view;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import javax.swing.CodeEditorPane;
import javax.swing.JSplitPane;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import edu.asu.plp.tool.prototype.model.AceEditor;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableStringValue;
import javafx.embed.swing.SwingNode;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

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
		
		webView.getEngine().loadContent(aceEditor.getPage().get());
		
		acePageContentsProperty.addListener((observable, old, newValue) -> 
		{
			webView.getEngine().loadContent(newValue);
		});
		
		webView.setContextMenuEnabled(false);
		//TODO create custom context menu (right click menu)
		
		setCenter(webView);
		this.accessibleRoleProperty().set(AccessibleRole.TEXT_AREA);
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
	
}
