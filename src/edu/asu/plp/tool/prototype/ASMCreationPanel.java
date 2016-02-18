package edu.asu.plp.tool.prototype;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import moore.util.Subroutine;
import edu.asu.plp.tool.prototype.util.Dialogues;

public class ASMCreationPanel extends BorderPane
{
	private TextField nameText;
	private TextField projectText;
	private ComboBox<String> projectListDropdown;
	
	/** Routine to be performed after {@link #onCreateASM} (usually to close the panel) */
	private Subroutine finallyOperation;
	private Consumer<ASMCreationDetails> onCreateASM;
	
	public ASMCreationPanel(Consumer<ASMCreationDetails> onCreateASM)
	{
		this.onCreateASM = onCreateASM;
		this.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Label ASMFileName = new Label();
		ASMFileName.setText("File Name: ");
		ASMFileName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		nameText = new TextField();
		nameText.setText("");
		nameText.requestFocus();
		nameText.setPrefWidth(200);
		
		Label projectName = new Label();
		projectName.setText("Add to Project: ");
		projectName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		projectListDropdown = new ComboBox<>();
		
		Button create = new Button();
		create.setText("Create");
		create.setOnAction(this::onCreateASMClicked);
		
		grid.add(ASMFileName, 0, 0);
		grid.add(nameText, 1, 0);
		grid.add(projectName, 0, 1);
		grid.add(projectListDropdown, 1, 1);
		
		this.setCenter(grid);
		buttons.getChildren().add(create);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		this.setBottom(buttons);
	}
	
	public void setProjectName(String projectName)
	{
		projectListDropdown.setValue(projectName);
	}
	
	public void addProjectName(String name)
	{
		projectListDropdown.getItems().add(name);
	}
	
	public void setFinallyOperation(Subroutine finallyOperation)
	{
		this.finallyOperation = finallyOperation;
	}
	
	private void onCreateASMClicked(ActionEvent event)
	{
		ASMCreationDetails details = extractDetailsFromGUI();
		boolean isValid = validateDefaultFileDetails(details);
		if (isValid)
		{
			if (onCreateASM == null)
				throw new IllegalStateException("onCreateASM handler not defined");
			else
				onCreateASM.accept(details);
			
			if (finallyOperation != null)
				finallyOperation.perform();
		}
	}
	
	private ASMCreationDetails extractDetailsFromGUI()
	{
		String projectName = projectListDropdown.getValue();
		String fileName = nameText.getText();
		
		return new ASMCreationDetails(projectName, fileName);
	}
	
	private boolean validateDefaultFileDetails(ASMCreationDetails details)
	{
		String projectName = details.getProjectName();
		String fileName = details.getFileName();
		
		if (projectName == null || projectName.trim().isEmpty())
		{
			Dialogues.showInfoDialogue("You entered an invalid Project Name");
		}
		else if (fileName == null || fileName.trim().isEmpty())
		{
			Dialogues.showInfoDialogue("You entered an invalid File Name");
		}
		else
		{
			return true;
		}
		
		return false;
	}
}
