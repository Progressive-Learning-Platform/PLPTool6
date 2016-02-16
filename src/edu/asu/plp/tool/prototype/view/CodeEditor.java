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
	private static String REGEX_KEY = "regex";
	private static String COLOR_KEY = "color";
	
	private StringProperty textProperty;
	
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
		
		setCenter(webView);
		this.accessibleRoleProperty().set(AccessibleRole.TEXT_AREA);
	}
	

	public void setSyntaxHighlighting(HashMap<String, Color> regexSyntaxHighlighting)
	{
//		textPane.setKeywordColor(regexSyntaxHighlighting);
	}
	
	public void setSyntaxHighlighting(JSONObject syntaxSpecification)
	{
		HashMap<String, Color> regexSyntaxMap = new HashMap<>();
		
		for (String syntaxName : syntaxSpecification.keySet())
		{
			JSONObject syntax = syntaxSpecification.getJSONObject(syntaxName);
			// TODO: account for invalid syn file (e.g. missing regex or color)
			String regex = syntax.getString(REGEX_KEY);
			String colorHexString = syntax.getString(COLOR_KEY);
			
			int red = Integer.valueOf(colorHexString.substring(1, 3), 16);
			int green = Integer.valueOf(colorHexString.substring(3, 5), 16);
			int blue = Integer.valueOf(colorHexString.substring(5, 7), 16);
			
			Color color = new Color(red, green, blue);
			regexSyntaxMap.put(regex, color);
		}
		
		setSyntaxHighlighting(regexSyntaxMap);
	}
	
	public void setSyntaxHighlighting(File syntaxSpecificationFile) throws IOException
	{
		String jsonString = FileUtils.readFileToString(syntaxSpecificationFile, "UTF-8");
		JSONObject syntaxSpecification = new JSONObject(jsonString);
		setSyntaxHighlighting(syntaxSpecification);
	}
	
	public void setText(String text)
	{
//		textPane.setText(text);
		updateText();
	}
	
	private void updateText()
	{
//		String text = textPane.getText();
//		this.textProperty.set(text);
		adjustLineNumbers();
	}
	
	private void adjustLineNumbers()
	{
		// Workaround for a bug in CodeEditorPane.getNumberOfLines
//		int lineCount = textPane.getText().split("\n").length;
//		String lineNumberString = Integer.toString(lineCount);
		
//		Font font = textPane.getFont();
//		FontMetrics metrics = textPane.getFontMetrics(font);
//		int width = SwingUtilities.computeStringWidth(metrics, lineNumberString);
		
		// Workaround for a bug in LineNumbersTextPane
//		JSplitPane paneWithLines = (JSplitPane) textPane.getContainerWithLines();
//		paneWithLines.setDividerLocation(width);
	}
	
	public String getText()
	{
		return get();
	}
	
	@Override
	public String get()
	{
		return textProperty.get();
	}
	
	@Override
	public String getValue()
	{
		return textProperty.getValue();
	}
	
	@Override
	public void addListener(ChangeListener<? super String> listener)
	{
		textProperty.addListener(listener);
	}
	
	@Override
	public void removeListener(ChangeListener<? super String> listener)
	{
		textProperty.removeListener(listener);
	}
	
	@Override
	public void addListener(InvalidationListener listener)
	{
		textProperty.addListener(listener);
	}
	
	@Override
	public void removeListener(InvalidationListener listener)
	{
		textProperty.removeListener(listener);
	}
	
	private class UpdateOnKeyPressListener implements KeyListener
	{
		@Override
		public void keyTyped(KeyEvent arg0)
		{
			updateText();
		}
		
		@Override
		public void keyReleased(KeyEvent arg0)
		{
			updateText();
		}
		
		@Override
		public void keyPressed(KeyEvent arg0)
		{
			updateText();
		}
	}
}
