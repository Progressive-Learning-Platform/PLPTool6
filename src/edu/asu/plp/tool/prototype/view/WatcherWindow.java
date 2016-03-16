package edu.asu.plp.tool.prototype.view;

import static java.nio.ByteOrder.*;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

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
	private Map<String, Function<Integer, String>> valueDisplayOptions;
	
	public WatcherWindow()
	{
		valueDisplayOptions = new LinkedHashMap<>();
		populateDisplayOptions();
		memoryAddresses = FXCollections.observableArrayList();
		registers = FXCollections.observableArrayList();
		// TODO: remove placeholder
		memoryAddresses.add(new MemoryRow(10025, 100000));
		registers.add(new RegisterRow("$t0", "$8", 100000));
		
		TableView<RegisterRow> watchedRegisters = createRegisterTable();
		TableView<MemoryRow> watchedAddresses = createMemoryTable();
		Node registerControlPanel = createRegisterControlPanel();
		Node memoryControlPanel = createMemoryControlPanel();
		
		GridPane center = new GridPane();
		center.add(watchedRegisters, 0, 0);
		center.add(registerControlPanel, 0, 1);
		center.add(watchedAddresses, 1, 0);
		center.add(memoryControlPanel, 1, 1);
		
		ColumnConstraints constraint = new ColumnConstraints();
		constraint.setPercentWidth(50);
		center.getColumnConstraints().add(constraint);
		constraint = new ColumnConstraints();
		constraint.setPercentWidth(50);
		center.getColumnConstraints().add(constraint);
		
		RowConstraints rowConstraint = new RowConstraints();
		rowConstraint.setPercentHeight(80);
		center.getRowConstraints().add(rowConstraint);
		rowConstraint = new RowConstraints();
		rowConstraint.setPercentHeight(20);
		center.getRowConstraints().add(rowConstraint);
		
		this.setCenter(center);
	}
	
	private void populateDisplayOptions()
	{
		valueDisplayOptions.put("Decimal", (value) -> Integer.toString(value));
		valueDisplayOptions.put("Hex", (value) -> Integer.toString(value, 16));
		valueDisplayOptions.put("Binary", (value) -> Integer.toString(value, 2));
		// TODO: move to utility class
		valueDisplayOptions.put("Packed ASCII",
				(value) -> {
					byte[] bytes = ByteBuffer.allocate(4).order(BIG_ENDIAN).putInt(value)
							.array();
					StringBuilder builder = new StringBuilder();
					for (byte element : bytes)
						builder.append((char) element);
					
					return builder.toString();
				});
	}
	
	private Node createRegisterControlPanel()
	{
		BorderPane registerPanel = new BorderPane();
		
		Label watchRegisterLabel = new Label("Watch Register: ");
		registerPanel.setLeft(watchRegisterLabel);
		setAlignment(watchRegisterLabel, Pos.CENTER);
		
		TextField registerNameField = new TextField();
		registerPanel.setCenter(registerNameField);
		setAlignment(registerNameField, Pos.CENTER);
		
		Button watchRegisterButton = new Button("Add");
		registerPanel.setRight(watchRegisterButton);
		setAlignment(watchRegisterButton, Pos.CENTER);
		
		Node displayOptions = createDisplayOptionsRow();
		
		VBox controlPanel = new VBox();
		controlPanel.getChildren().add(registerPanel);
		controlPanel.getChildren().add(displayOptions);
		controlPanel.setAlignment(Pos.CENTER);
		setAlignment(controlPanel, Pos.CENTER);
		
		return controlPanel;
	}
	
	private Node createMemoryControlPanel()
	{
		BorderPane addressPanel = new BorderPane();
		
		Label watchAddressLabel = new Label("Watch Address: ");
		addressPanel.setLeft(watchAddressLabel);
		setAlignment(watchAddressLabel, Pos.CENTER);
		
		TextField addressField = new TextField();
		addressPanel.setCenter(addressField);
		setAlignment(addressField, Pos.CENTER);
		
		Button watchAddressButton = new Button("Add");
		addressPanel.setRight(watchAddressButton);
		setAlignment(watchAddressButton, Pos.CENTER);
		
		BorderPane rangePanel = new BorderPane();
		
		Label watchRangeFromLabel = new Label("Watch Range From ");
		rangePanel.setLeft(watchRangeFromLabel);
		setAlignment(watchRangeFromLabel, Pos.CENTER);
		
		HBox inputBox = new HBox();
		
		TextField fromField = new TextField();
		inputBox.getChildren().add(fromField);
		fromField.setPrefWidth(Integer.MAX_VALUE);
		
		Label toLabel = new Label(" To ");
		toLabel.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
		inputBox.getChildren().add(toLabel);
		inputBox.setAlignment(Pos.CENTER);
		
		TextField toField = new TextField();
		toField.setPrefWidth(Integer.MAX_VALUE);
		inputBox.getChildren().add(toField);
		
		rangePanel.setCenter(inputBox);
		setAlignment(inputBox, Pos.CENTER);
		
		Button watchRangeButton = new Button("Add");
		rangePanel.setRight(watchRangeButton);
		setAlignment(watchRangeButton, Pos.CENTER);
		
		Node displayOptions = createDisplayOptionsRow();
		
		VBox controlPanel = new VBox();
		controlPanel.getChildren().add(addressPanel);
		controlPanel.getChildren().add(rangePanel);
		controlPanel.getChildren().add(displayOptions);
		controlPanel.setAlignment(Pos.CENTER);
		setAlignment(controlPanel, Pos.CENTER);
		
		return controlPanel;
	}
	
	private Node createDisplayOptionsRow()
	{
		BorderPane displayOptions = new BorderPane();
		
		Label label = new Label("Display values as: ");
		displayOptions.setLeft(label);
		
		ComboBox<String> dropdown = createDisplayOptionsDropdown();
		dropdown.setPrefWidth(Integer.MAX_VALUE);
		displayOptions.setCenter(dropdown);
		
		return displayOptions;
	}
	
	private ComboBox<String> createDisplayOptionsDropdown()
	{
		ObservableList<String> options = FXCollections.observableArrayList();
		options.addAll(valueDisplayOptions.keySet());
		
		ComboBox<String> dropdown = new ComboBox<>(options);
		dropdown.getSelectionModel().select(0);
		return dropdown;
	}
	
	private void watchRegister(String string)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	private void watchMemoryAddress(int address)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
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
