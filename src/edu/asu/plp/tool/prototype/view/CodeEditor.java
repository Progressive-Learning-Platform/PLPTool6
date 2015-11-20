package edu.asu.plp.tool.prototype.view;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

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

import javax.swing.CodeEditorPane;
import javax.swing.SwingUtilities;

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
 *
 */
public class CodeEditor extends BorderPane implements ObservableStringValue
{
	private CodeEditorPane textPane;
	private StringProperty textProperty;
	
	public CodeEditor()
	{
		textProperty = new SimpleStringProperty();
		textPane = new CodeEditorPane();
		textPane.addKeyListener(new UpdateOnKeyPressListener());
		
		SwingNode swingNode = new SwingNode();
		createSwingContent(swingNode);
		setCenter(swingNode);
		
		this.accessibleRoleProperty().set(AccessibleRole.TEXT_AREA);
		this.textProperty.bindBidirectional(accessibleTextProperty());
	}
	
	public void setText(String text)
	{
		textPane.setText(text);
		textProperty.set(text);
	}
	
	private void updateText()
	{
		String text = textPane.getText();
		this.textProperty.set(text);
	}
	
	private void createSwingContent(SwingNode swingNode)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				textPane.setForeground(Color.red);
				textPane.setText("This is a Code Editor");
				swingNode.setContent(textPane);
			}
		});
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
