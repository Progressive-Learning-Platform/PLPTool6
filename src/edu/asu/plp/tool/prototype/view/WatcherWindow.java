package edu.asu.plp.tool.prototype.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
	public class ValueRow
	{
		private IntegerProperty value;
		
		public ValueRow(int value)
		{
			this.value = new SimpleIntegerProperty(value);
		}
		
		public String getValue()
		{
			return "0x" + Integer.toString(value.get(), 16).toUpperCase();
		}
		
		public void setValue(int value)
		{
			this.value.set(value);
		}
		
		public void setValue(String value)
		{
			int oldValue = this.value.get();
			try
			{
				setValue(Integer.parseInt(value));
			}
			catch (Exception e)
			{
				setValue(oldValue);
			}
		}
	}
	
	public class RegisterRow extends ValueRow
	{
		private StringProperty registerName;
		private StringProperty registerID;
		
		public RegisterRow(String name, String id, int value)
		{
			super(value);
			registerName = new SimpleStringProperty(name);
			registerID = new SimpleStringProperty(id);
		}
		
		public String getRegisterName()
		{
			return registerName.get();
		}
		
		public void setRegisterName(String name)
		{
			registerName.set(name);
		}
		
		public String getRegisterID()
		{
			return registerID.get();
		}
		
		public void setRegisterID(String id)
		{
			registerID.set(id);
		}
	}
	
	public class MemoryRow extends ValueRow
	{
		private IntegerProperty address;
		
		public MemoryRow(int address, int value)
		{
			super(value);
			this.address = new SimpleIntegerProperty(address);
		}
		
		public String getAddress()
		{
			return Integer.toString(address.get());
		}
		
		public void setAddress(int value)
		{
			this.address.set(value);
		}
		
		public void setAddress(String address)
		{
			int oldAddress = this.address.get();
			try
			{
				setAddress(Integer.parseInt(address));
			}
			catch (Exception e)
			{
				setAddress(oldAddress);
			}
		}
	}
	
	private ObservableList<MemoryRow> memoryAddresses;
	private ObservableList<RegisterRow> registers;
	
	public WatcherWindow()
	{
		memoryAddresses = FXCollections.observableArrayList();
		registers = FXCollections.observableArrayList();
		// TODO: remove placeholder
		memoryAddresses.add(new MemoryRow(10025, 100000));
		registers.add(new RegisterRow("$t0", "$8", 100000));
		
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
		nameColumn.setCellValueFactory(registerFactory("registerName"));
		setPercentSize(table, nameColumn, 1.0 / 3.0);
		table.getColumns().add(nameColumn);
		
		TableColumn<RegisterRow, String> idColumn = new TableColumn<>("Register");
		idColumn.setCellValueFactory(registerFactory("registerID"));
		setPercentSize(table, idColumn, 1.0 / 3.0);
		table.getColumns().add(idColumn);
		
		TableColumn<RegisterRow, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(registerFactory("value"));
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
		idColumn.setCellValueFactory(memoryFactory("address"));
		setPercentSize(table, idColumn, 0.5);
		table.getColumns().add(idColumn);
		
		TableColumn<MemoryRow, String> valueColumn = new TableColumn<>("Value");
		valueColumn.setCellValueFactory(memoryFactory("value"));
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
	
	private static PropertyValueFactory<RegisterRow, String> registerFactory(
			String attribute)
	{
		return new PropertyValueFactory<RegisterRow, String>(attribute);
	}
	
	private static PropertyValueFactory<MemoryRow, String> memoryFactory(String attribute)
	{
		return new PropertyValueFactory<MemoryRow, String>(attribute);
	}
}
