package edu.asu.plp.tool.prototype.view;

import edu.asu.plp.tool.prototype.view.WatcherWindow.RegisterRow;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class CpuWindow extends BorderPane
{
	public class ValueRow
	{
		private IntegerProperty value;
		
		public ValueRow(int value)
		{
			this.value = new SimpleIntegerProperty(value);
		}
		
		public ValueRow(IntegerProperty value)
		{
			this.value = value;
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
		private StringProperty registerContents;
		
		public RegisterRow(String name, String id, int value)
		{
			super(value);
			registerName = new SimpleStringProperty(name);
			registerContents = new SimpleStringProperty(id);
		}
		
		public RegisterRow(String name, String id, IntegerProperty value)
		{
			super(value);
			registerName = new SimpleStringProperty(name);
			registerContents = new SimpleStringProperty(id);
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
			return registerContents.get();
		}
		
		public void setRegisterID(String id)
		{
			registerContents.set(id);
		}
	}
	
	public class MemoryRow extends ValueRow
	{
		private IntegerProperty address;
		
		public MemoryRow(int address, IntegerProperty value)
		{
			super(value);
			this.address = new SimpleIntegerProperty(address);
		}
		
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
	
	private ObservableList<RegisterRow> registers;
	
	public CpuWindow()
	{
		TabPane cpuWindowTabs = new TabPane();
		registers = FXCollections.observableArrayList();
		
		Tab registerFileTab = new Tab();
		registerFileTab.setText("Register");
		registerFileTab.setClosable(false);
		
		Tab disassemblyTab = new Tab();
		disassemblyTab.setText("Disassembly");
		disassemblyTab.setClosable(false);
		
		Tab memoryMapTab = new Tab();
		memoryMapTab.setText("About");
		memoryMapTab.setClosable(false);
		
		Tab simOptionsTab = new Tab();
		simOptionsTab.setText("Sim Options");
		simOptionsTab.setClosable(false);
		
		Tab consoleTab = new Tab();
		consoleTab.setText("Console");
		consoleTab.setClosable(false);
		

		cpuWindowTabs.getTabs().addAll(registerFileTab, disassemblyTab, memoryMapTab, simOptionsTab, consoleTab);
		
		TableView<RegisterRow> registerTabContent = createRegisterTabContent();
		
		registerFileTab.setContent(registerTabContent);
		registers.add(new RegisterRow("0: $zero", "0x00000000", 0));
		registers.add(new RegisterRow("1: $at", "0x00000000", 0));
		registers.add(new RegisterRow("2: $v0", "0x00000000", 0));
		registers.add(new RegisterRow("3: $a0", "0x00000000", 0));
		
		HBox topHBox = createTopBar();
		this.setTop(topHBox);
		this.setCenter(cpuWindowTabs);

	}
	
	private HBox createTopBar()
	{
		HBox counterHBox = new HBox();
		counterHBox.setPadding(new Insets(15, 15, 15, 15));
		counterHBox.setSpacing(10);
		
		Label programCounterLabel = new Label("Program Count");
		programCounterLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField programCounterText = new TextField();
		programCounterText.setPrefWidth(50);
		
		Label nextInstructionLabel = new Label("Next Instruction");
		nextInstructionLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField nextInstructionText = new TextField();
		nextInstructionText.setPrefWidth(200);
		counterHBox.getChildren().addAll(programCounterLabel, programCounterText, nextInstructionLabel, nextInstructionText);
		
		return counterHBox;
	}
	
	private TableView<RegisterRow> createRegisterTabContent()
	{
		TableView<RegisterRow> table = new TableView<>();
		table.setEditable(true);
		
		TableColumn<RegisterRow, String> nameColumn = new TableColumn<>("Register");
		nameColumn.setCellValueFactory(registerFactory("registerName"));
		setPercentSize(table, nameColumn, 1.0 / 3.0);
		table.getColumns().add(nameColumn);
		
		TableColumn<RegisterRow, String> idColumn = new TableColumn<>("Contents");
		idColumn.setCellValueFactory(registerFactory("registerContents"));
		setPercentSize(table, idColumn, 1.0 / 3.0);
		table.getColumns().add(idColumn);
		
		TableColumn<RegisterRow, String> valueColumn = new TableColumn<>("Edit Contents");
		valueColumn.setCellValueFactory(registerFactory("editContents"));
		setPercentSize(table, valueColumn, 1.0 / 3.0);
		table.getColumns().add(valueColumn);
		
		table.setItems(registers);
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
	
}
