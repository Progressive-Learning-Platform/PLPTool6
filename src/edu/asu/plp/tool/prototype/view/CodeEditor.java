package edu.asu.plp.tool.prototype.view;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javafx.embed.swing.SwingNode;
import javafx.scene.AccessibleRole;
import javafx.scene.layout.BorderPane;

import javax.swing.CodeEditorPane;
import javax.swing.SwingUtilities;

public class CodeEditor extends BorderPane
{
	private CodeEditorPane textPane;
	
	public CodeEditor()
	{
		textPane = new CodeEditorPane();
		textPane.addKeyListener(new UpdateOnKeyPressListener());
		
		SwingNode swingNode = new SwingNode();
		createSwingContent(swingNode);
		setCenter(swingNode);
		
		this.accessibleRoleProperty().set(AccessibleRole.TEXT_AREA);
		updateAccessibleText();
	}
	
	private void updateAccessibleText()
	{
		this.accessibleTextProperty().set(textPane.getText());
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
	
	private class UpdateOnKeyPressListener implements KeyListener
	{
		@Override
		public void keyTyped(KeyEvent arg0)
		{
			updateAccessibleText();
		}
		
		@Override
		public void keyReleased(KeyEvent arg0)
		{
			updateAccessibleText();
		}
		
		@Override
		public void keyPressed(KeyEvent arg0)
		{
			updateAccessibleText();
		}
	}
}
