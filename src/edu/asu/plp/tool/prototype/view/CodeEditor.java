package edu.asu.plp.tool.prototype.view;

import javafx.embed.swing.SwingNode;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class CodeEditor extends BorderPane
{
	public CodeEditor()
	{
		// This is throwing an Error, not sure why
		// textPane = new CodeEditorPane();
		
		TextArea testText = new TextArea();
		
		SwingNode swingNode = new SwingNode();
		
		createSwingContent(swingNode);
		
		setCenter(swingNode);
		
	}
	
	private void createSwingContent(SwingNode swingNode)
	{
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				JTextArea myText = new JTextArea();
				myText.append("This is a swing Text Area");
				swingNode.setContent(myText);
			}
		});
		
	}
	
}
