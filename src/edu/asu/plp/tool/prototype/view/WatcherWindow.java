package edu.asu.plp.tool.prototype.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class WatcherWindow extends BorderPane
{
	private static class RegisterRow
	{
		public static PropertyValueFactory<RegisterRow, String> factory(String attribute)
		{
			return new PropertyValueFactory<RegisterRow, String>(attribute);
		}
	}
	
	private static class MemoryRow
	{
		public static PropertyValueFactory<MemoryRow, String> factory(String attribute)
		{
			return new PropertyValueFactory<MemoryRow, String>(attribute);
		}
	}
	
	private ObservableList<MemoryRow> memoryAddresses;
	private ObservableList<RegisterRow> registers;
	
	public WatcherWindow()
	{
		memoryAddresses = FXCollections.observableArrayList();
		registers = FXCollections.observableArrayList();
		
		TableView<RegisterRow> watchedRegisters = createRegisterTable();
		TableView<MemoryRow> watchedAddresses = createMemoryTable();
		
		GridPane center = new GridPane();
		center.add(watchedRegisters, 0, 0);
		center.add(watchedAddresses, 1, 0);
		
		ColumnConstraints constraint = new ColumnConstraints();
		constraint.setPercentWidth(50);
		center.getColumnConstraints().add(constraint);
		constraint = new ColumnConstraints();
		constraint.setPercentWidth(50);
		center.getColumnConstraints().add(constraint);
		
		RowConstraints rowConstraint = new RowConstraints();
		rowConstraint.setPercentHeight(100);
		center.getRowConstraints().add(rowConstraint);
		
		this.setCenter(center);
	}
	
	private TableView<RegisterRow> createRegisterTable()
	{
		TableView<RegisterRow> table = new TableView<>();
		table.setEditable(true);
		
		TableColumn<RegisterRow, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(RegisterRow.factory("registerName"));
		setPercentSize(table, nameColumn, 1.0 / 3.0);
		table.getColumns().add(nameColumn);
		
		TableColumn<RegisterRow, String> idColumn = new TableColumn<>("Register");
		idColumn.setCellValueFactory(RegisterRow.factory("registerID"));
		setPercentSize(table, idColumn, 1.0 / 3.0);
		table.getColumns().add(idColumn);
		
		TableColumn<RegisterRow, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(RegisterRow.factory("value"));
		setPercentSize(table, valueColumn, 1.0 / 3.0);
		table.getColumns().add(valueColumn);
		
		table.setItems(registers);
		return table;
	}
	
	private TableView<MemoryRow> createMemoryTable()
	{
		TableView<MemoryRow> table = new TableView<>();
		table.setEditable(true);
		
		TableColumn<MemoryRow, String> idColumn = new TableColumn<>("Address");
		idColumn.setCellValueFactory(MemoryRow.factory("address"));
		setPercentSize(table, idColumn, 0.5);
		table.getColumns().add(idColumn);
		
		TableColumn<MemoryRow, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(MemoryRow.factory("value"));
		setPercentSize(table, valueColumn, 0.5);
		table.getColumns().add(valueColumn);
		
		table.setItems(memoryAddresses);
		return table;
	}
	
	private void setPercentSize(TableView<?> parent, TableColumn<?, ?> column,
			double percent)
	{
		parent.widthProperty().addListener(
				(item, old, current) -> column.setPrefWidth((double) current * percent));
	}
}
