package edu.asu.plp.tool.prototype.view;

import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.asu.plp.tool.prototype.util.OnLoadListener;

public class ConsolePane extends BorderPane
{
	private static class Message
	{
		private String tagType;
		private String cssClasses;
		private String inlineStyles;
		private String message;
		
		public Message(String tagType, String classes, String styles, String message)
		{
			super();
			this.tagType = tagType;
			this.cssClasses = classes;
			this.inlineStyles = styles;
			this.message = message;
		}
	}
	
	private static final String TEXT_PANE_ID = "textPane";
	private static final String TEXT_PANE_CLASS = "scrollPane";
	private static final String CSS_MESSAGE_CLASS = "message";
	
	private Element textPaneElement;
	private WebEngine webEngine;
	private Queue<Message> messageQueue;
	
	public ConsolePane()
	{
		WebView view = new WebView();
		view.setContextMenuEnabled(false);
		webEngine = view.getEngine();
		messageQueue = new LinkedList<>();
		
		ObservableValue<State> property = webEngine.getLoadWorker().stateProperty();
		OnLoadListener.register(this::onLoad, property);
		
		String content = "<html><head></head><body></body></html>";
		webEngine.loadContent(content);
		
		this.setCenter(view);
	}
	
	private void onLoad()
	{
		Document dom = webEngine.getDocument();
		textPaneElement = dom.createElement("div");
		textPaneElement.setAttribute("id", TEXT_PANE_ID);
		textPaneElement.setAttribute("class", TEXT_PANE_CLASS);
		
		Node body = dom.getElementsByTagName("body").item(0);
		body.appendChild(textPaneElement);
		
		URL cssURL = getClass().getResource("defaultConsoleStyle.css");
		String cssPath = cssURL.toExternalForm();
		addStylesheet(cssPath);
		
		for (Message message : messageQueue)
		{
			output(message.tagType, message.cssClasses, message.inlineStyles,
					message.message);
		}
	}
	
	private void output(String tagType, String cssClasses, String inlineStyles,
			String message)
	{
		Document dom = webEngine.getDocument();
		
		if (dom == null)
		{
			Message target = new Message(tagType, cssClasses, inlineStyles, message);
			messageQueue.add(target);
		}
		else
		{
			Element tag = dom.createElement(tagType);
			tag.setAttribute("class", cssClasses);
			tag.setAttribute("style", inlineStyles);
			
			Element content = dom.createElement("code");
			content.setTextContent(message);
			
			tag.appendChild(content);
			textPaneElement.appendChild(tag);
		}
	}
	
	private void output(String tagType, String cssClasses, String message)
	{
		output(tagType, cssClasses, null, message);
	}
	
	public void println(String message)
	{
		output("div", CSS_MESSAGE_CLASS, message);
	}
	
	public void print(String message)
	{
		output("span", CSS_MESSAGE_CLASS, message);
	}
	
	public void clear()
	{
		textPaneElement.setTextContent("");
	}
	
	public void addStylesheet(String path)
	{
		Document dom = webEngine.getDocument();
		Node head = dom.getElementsByTagName("head").item(0);
		
		Element styleReference = dom.createElement("link");
		styleReference.setAttribute("rel", "stylesheet");
		styleReference.setAttribute("type", "text/css");
		styleReference.setAttribute("href", path);
		head.appendChild(styleReference);
	}
}
