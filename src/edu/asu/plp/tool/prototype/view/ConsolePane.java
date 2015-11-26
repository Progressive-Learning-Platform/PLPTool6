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

import edu.asu.plp.tool.prototype.model.CSSStyle;
import edu.asu.plp.tool.prototype.util.OnLoadListener;

public class ConsolePane extends BorderPane
{
	private static class Message
	{
		private String tagType;
		private String message;
		private CSSStyle style;
		
		public Message(String tagType, String message, CSSStyle style)
		{
			this.tagType = tagType;
			this.message = message;
			this.style = style;
		}
	}
	
	private static final String TEXT_PANE_ID = "textPane";
	private static final String TEXT_PANE_CLASS = "scrollPane";
	private static final String CSS_MESSAGE_CLASS = "message";
	private static final String CSS_WARNING_CLASS = "warning";
	private static final String CSS_ERROR_CLASS = "error";
	
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
			output(message.tagType, message.message, message.style);
		}
		
		messageQueue = null;
	}
	
	private void output(String tagType, String message, CSSStyle style)
	{
		Document dom = webEngine.getDocument();
		
		if (dom == null)
		{
			Message target = new Message(tagType, message, style);
			messageQueue.add(target);
		}
		else
		{
			Element tag = dom.createElement(tagType);
			tag.setAttribute("class", style.compileClassesString());
			tag.setAttribute("style", style.compileStyleString());
			
			Element content = dom.createElement("code");
			content.setTextContent(message);
			
			tag.appendChild(content);
			textPaneElement.appendChild(tag);
		}
	}
	
	private CSSStyle messageStyle()
	{
		CSSStyle style = new CSSStyle();
		style.addStyleClass(CSS_MESSAGE_CLASS);
		
		return style;
	}
	
	private CSSStyle warningStyle()
	{
		CSSStyle style = new CSSStyle();
		style.addStyleClass(CSS_MESSAGE_CLASS);
		style.addStyleClass(CSS_WARNING_CLASS);
		
		return style;
	}
	
	private CSSStyle errorStyle()
	{
		CSSStyle style = new CSSStyle();
		style.addStyleClass(CSS_MESSAGE_CLASS);
		style.addStyleClass(CSS_ERROR_CLASS);
		
		return style;
	}
	
	public void println(String message)
	{
		println(message, messageStyle());
	}
	
	public void print(String message)
	{
		print(message, messageStyle());
	}
	
	public void println(String message, CSSStyle style)
	{
		output("div", message, style);
	}
	
	public void print(String message, CSSStyle style)
	{
		output("span", message, style);
	}
	
	public void warning(String message)
	{
		println(message, warningStyle());
	}
	
	public void error(String message)
	{
		println(message, errorStyle());
	}
	
	public void message(String message)
	{
		println(message, messageStyle());
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
