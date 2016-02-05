package edu.asu.plp.tool.prototype;

import java.io.File;
import java.io.IOException;

import moore.fx.components.Components;
import edu.asu.plp.tool.prototype.model.PLPProject;
import edu.asu.plp.tool.prototype.model.PLPSourceFile;
import edu.asu.plp.tool.prototype.util.Dialogues;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ProjectCreationPanel extends BorderPane
{
	public ProjectCreationPanel()
	{
		this.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Label projectNameLabel = new Label();
		projectNameLabel.setText("Project Name: ");
		projectNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projectNameField = new TextField();
		projectNameField.setText("Project Name");
		projectNameField.requestFocus();
		projectNameField.setPrefWidth(200);
		
		Label mainSourceFileNameLabel = new Label();
		mainSourceFileNameLabel.setText("File Name: ");
		mainSourceFileNameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField mainSourceFileNameField = new TextField();
		mainSourceFileNameField.setText("Main.asm");
		mainSourceFileNameField.setPrefWidth(200);
		
		Label projectLocationLabel = new Label();
		projectLocationLabel.setText("Location: ");
		projectLocationLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projectLocationField = new TextField();
		projectLocationField.setPrefWidth(200);
		
		Button browseLocationButton = new Button();
		browseLocationButton.setText("Browse");
		browseLocationButton.setOnAction(this::onBrowseLocation);
		
		Label target = new Label();
		target.setText("Targetted ISA: ");
		target.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		String PLP6 = "PLP6";
		String legacy = "PLP5(Legacy)";
		String mips = "MIPS";
		
		ComboBox<String> projectType = new ComboBox<String>();
		projectType.getItems().addAll(PLP6, legacy, mips);
		projectType.setValue(PLP6);
		
		Label version = new Label();
		version.setText("Version: ");
		version.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		Button createProject = new Button("Create Project");
		createProject.setOnAction(this::onCreateProject);
		
		createProject.setDefaultButton(true);
		Button cancelCreate = new Button("Cancel");
		cancelCreate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				Stage stage = (Stage) cancelCreate.getScene().getWindow();
				stage.close();
			}
		});
		
		grid.add(projectNameLabel, 0, 0);
		grid.add(projectNameField, 1, 0);
		grid.add(mainSourceFileNameLabel, 0, 1);
		grid.add(mainSourceFileNameField, 1, 1);
		grid.add(projectLocationLabel, 0, 2);
		grid.add(projectLocationField, 1, 2);
		grid.add(browseLocationButton, 2, 2);
		grid.add(target, 0, 3);
		grid.add(projectType, 1, 3);
		grid.add(version, 0, 4);
		
		this.setCenter(grid);
		
		buttons.getChildren().addAll(createProject, cancelCreate);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		this.setBottom(buttons);
	}
	
	private void onBrowseLocation(ActionEvent event)
	{
		String chosenLocation = "";
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose Project Location");
		File file = directoryChooser.showDialog(null);
		if (file != null)
		{
			chosenLocation = file.getAbsolutePath().concat(
					File.separator + projTextField.getText());
			projLocationField.setText(chosenLocation);
		}
	}
	
	private void onCreateProject(ActionEvent event)
	{
		String projectName;
		String fileName;
		String projectLocation;
		projectName = projTextField.getText();
		fileName = sourceFileField.getText();
		projectLocation = projLocationField.getText();
		File projectDirectory = new File(projectLocation);
		if (projectName == null || projectName.trim().isEmpty())
		{
			Dialogues.showInfoDialogue("You entered an invalid Project Name");
		}
		else if (fileName == null || fileName.trim().isEmpty())
		{
			Dialogues.showInfoDialogue("You entered an invalid File Name");
		}
		else if (projectLocation == null || projectLocation.trim().isEmpty())
		{
			Dialogues.showInfoDialogue("You entered an invalid Project Location");
			
		}
		else if (projectDirectory.exists())
		{
			Dialogues.showInfoDialogue("This Project Already Exists");
		}
		else
		{
			projectName = projTextField.getText();
			fileName = sourceFileField.getText();
			
			File srcFile = new File(projectLocation + File.separator + "src");
			srcFile.mkdirs();
			
			if (projectType.getValue().equals(PLP6) && !fileName.contains(".asm"))
			{
				fileName = fileName.concat(".asm");
			}
			
			if (projectType.getValue().equals(legacy) && !fileName.contains(".plp"))
			{
				fileName = fileName.concat(".plp");
			}
			
			if (projectType.getValue().equals(legacy))
			{
				PLPProject legacyProject = new PLPProject(projectName);
				legacyProject.setPath(projLocationField.getText());
				PLPSourceFile legacySourceFile = new PLPSourceFile(legacyProject,
						fileName);
				try
				{
					legacyProject.saveLegacy();
				}
				catch (IOException ioException)
				{
					// TODO report exception to user
					ioException.printStackTrace();
				}
				projects.add(legacyProject);
				openFile(legacySourceFile);
			}
			
			if (projectType.getValue().equals(PLP6))
			{
				PLPProject project = new PLPProject(projectName);
				project.setPath(projLocationField.getText());
				PLPSourceFile sourceFile = new PLPSourceFile(project, fileName);
				project.add(sourceFile);
				try
				{
					project.save();
				}
				catch (IOException ioException)
				{
					// TODO report exception to user
					ioException.printStackTrace();
				}
				projects.add(project);
				openFile(sourceFile);
			}
			
			Stage stage = (Stage) createProject.getScene().getWindow();
			stage.close();
		}
	}
}
