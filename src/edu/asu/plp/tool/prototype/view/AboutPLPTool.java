package edu.asu.plp.tool.prototype.view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import moore.fx.components.Components;

public class AboutPLPTool extends BorderPane
{
	public static final String aboutText = "About Text";
	TabPane tabPane = new TabPane();
	
	public AboutPLPTool()
	{
		this.setPadding(new Insets(20));
		tabPane = new TabPane();
		
		TextArea content = new TextArea();
		
		Tab aboutTab = new Tab();
		aboutTab.setText("About");
		aboutTab.setClosable(false);
		content.setText(aboutText);
		aboutTab.setContent(content);
		
		Tab contactTab = new Tab();
		contactTab.setText("Contact");
		contactTab.setClosable(false);
		content.setText(aboutText);
		contactTab.setContent(content);
		
		Tab thirdPartyTab = new Tab();
		thirdPartyTab.setText("Third Party Software");
		thirdPartyTab.setClosable(false);
		content.setText(aboutText);
		thirdPartyTab.setContent(content);
		
		Tab gplTab = new Tab();
		gplTab.setText("GPL");
		gplTab.setClosable(false);
		content.setText(aboutText);
		gplTab.setContent(content);
		
		tabPane.getTabs().addAll(aboutTab, contactTab, thirdPartyTab, gplTab);
		this.setCenter(tabPane);
	}

}
