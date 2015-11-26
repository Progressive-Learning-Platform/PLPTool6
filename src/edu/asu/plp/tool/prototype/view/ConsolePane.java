package edu.asu.plp.tool.prototype.view;

import java.net.URL;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import moore.util.Subroutine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ConsolePane extends BorderPane
{
	private static class OnLoadListener implements ChangeListener<State>
	{
		private ObservableValue<State> observable;
		private Subroutine onLoadFunction;

		public static void register(Subroutine function, ObservableValue<State> target)
		{
			OnLoadListener listener = new OnLoadListener(function);
			listener.observable = target;
			target.addListener(listener);
		}
		
		public OnLoadListener(Subroutine onLoadFunction)
		{
			this.onLoadFunction = onLoadFunction;
		}
		
		public void changed(ObservableValue<? extends State> value, State oldState,
				State newState)
		{
			if (newState == State.SUCCEEDED)
			{
				onLoadFunction.perform();
				
				if (observable != null)
					observable.removeListener(this);
			}
		}
	}
	
	private static final String TEXT_PANE_ID = "textPane";
	private static final String TEXT_PANE_CLASS = "scrollPane";
	private static final String CSS_MESSAGE_CLASS = "message";
	
	private Element textPaneElement;
	private WebEngine webEngine;
	
	public ConsolePane()
	{
		WebView view = new WebView();
		webEngine = view.getEngine();
		
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
		
		for (int i = 0; i < 25; i++)
		{
			println("Test<br/>" + i);
		}
	}
	
	public void println(String message)
	{
		Document dom = webEngine.getDocument();
		Element div = dom.createElement("div");
		div.setAttribute("class", CSS_MESSAGE_CLASS);
		
		Element content = dom.createElement("code");
		content.setTextContent(message);
		
		div.appendChild(content);
		textPaneElement.appendChild(div);
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
