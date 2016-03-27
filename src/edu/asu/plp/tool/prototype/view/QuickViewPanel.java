package edu.asu.plp.tool.prototype.view;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import edu.asu.plp.tool.prototype.model.QuickViewEntry;
import edu.asu.plp.tool.prototype.model.QuickViewSection;

public class QuickViewPanel extends BorderPane
{
	public QuickViewPanel(List<QuickViewSection> sections)
	{
		VBox vbox = new VBox();
		
		for (QuickViewSection section : sections)
		{
			Node sectionView = createSectionView(section);
			vbox.getChildren().add(sectionView);
		}
		
		Node center = vbox;
		this.setCenter(center);
	}
	
	private Node createSectionView(QuickViewSection section)
	{
		BorderPane view = new BorderPane();
		Label sectionTitle = new Label(section.getTitle());
		Node sectionBody = createSectionEntryTable(section);
		
		view.setTop(sectionTitle);
		view.setCenter(sectionBody);
		
		return view;
	}

	private TableView<QuickViewEntry> createSectionEntryTable(QuickViewSection section)
	{
		String contentHeader = section.getContentHeader();
		String descriptionHeader = section.getDescriptionHeader();
		
		TableView<QuickViewEntry> table = new TableView<>();
		table.setEditable(false);
		
		TableColumn<QuickViewEntry, String> contentColumn = new TableColumn<>(
				contentHeader);
		contentColumn.setCellValueFactory(cellFactory("content"));
		setPercentSize(table, contentColumn, 0.3);
		table.getColumns().add(contentColumn);
		
		TableColumn<QuickViewEntry, String> descriptionColumn = new TableColumn<>(
				descriptionHeader);
		descriptionColumn.setCellValueFactory(cellFactory("description"));
		setPercentSize(table, descriptionColumn, 0.2);
		table.getColumns().add(descriptionColumn);
		
		ObservableList<QuickViewEntry> entries = FXCollections.observableArrayList();
		entries.addAll(section.getEntries());
		table.setItems(entries);
		return table;
	}
	
	private void setPercentSize(TableView<?> parent, TableColumn<?, ?> column,
			double percent)
	{
		parent.widthProperty().addListener(
				(item, old, current) -> column.setPrefWidth((double) current * percent));
	}
	
	private static PropertyValueFactory<QuickViewEntry, String> cellFactory(
			String attribute)
	{
		return new PropertyValueFactory<QuickViewEntry, String>(attribute);
	}
}
