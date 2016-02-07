package edu.asu.plp.tool.prototype;

import static edu.asu.plp.tool.prototype.util.Dialogues.showAlertDialogue;
import static edu.asu.plp.tool.prototype.util.Dialogues.showInfoDialogue;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;
import moore.fx.components.Components;
import moore.util.ExceptionalSubroutine;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.io.FileUtils;

import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.Simulator;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.core.ISAModule;
import edu.asu.plp.tool.exceptions.UnexpectedFileTypeException;
import edu.asu.plp.tool.prototype.model.PLPProject;
import edu.asu.plp.tool.prototype.model.PLPSourceFile;
import edu.asu.plp.tool.prototype.model.Project;
import edu.asu.plp.tool.prototype.util.Dialogues;
import edu.asu.plp.tool.prototype.view.CodeEditor;
import edu.asu.plp.tool.prototype.view.ConsolePane;
import edu.asu.plp.tool.prototype.view.ProjectExplorerTree;

/**
 * Driver for the PLPTool prototype.
 * 
 * The driver's only responsibility is to launch the PLPTool Prototype window. This class
 * also defines the window and its contents.
 * 
 * @author Moore, Zachary
 * @author Hawks, Elliott
 * 		
 */
public class Main extends Application
{
	public static final String APPLICATION_NAME = "PLPTool";
	public static final long VERSION = 0;
	public static final long REVISION = 1;
	public static final int DEFAULT_WINDOW_WIDTH = 1280;
	public static final int DEFAULT_WINDOW_HEIGHT = 720;
	public boolean simMode = false;
	
	private Stage stage;
	private TabPane openProjectsPanel;
	// XXX: openProjects is a misnomer - should be openFiles
	private BidiMap<ASMFile, Tab> openProjects;
	private ObservableList<Project> projects;
	private Map<Project, ProjectAssemblyDetails> assemblyDetails;
	private ProjectExplorerTree projectExplorer;
	private ConsolePane console;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		this.stage = primaryStage;
		primaryStage.setTitle(APPLICATION_NAME + " V" + VERSION + "." + REVISION);
		
		this.assemblyDetails = new HashMap<>();
		this.openProjects = new DualHashBidiMap<>();
		this.openProjectsPanel = new TabPane();
		this.projectExplorer = createProjectTree();
		Parent outlineView = createOutlineView();
		console = createConsole();
		console.println(">> Console Initialized.");
		
		// Left side holds the project tree and outline view
		SplitPane leftSplitPane = new SplitPane();
		leftSplitPane.orientationProperty().set(Orientation.VERTICAL);
		leftSplitPane.getItems().addAll(Components.passiveScroll(projectExplorer),
				Components.wrap(outlineView));
		leftSplitPane.setDividerPositions(0.5, 1.0);
		
		// Right side holds the source editor and the output console
		SplitPane rightSplitPane = new SplitPane();
		rightSplitPane.orientationProperty().set(Orientation.VERTICAL);
		rightSplitPane.getItems().addAll(Components.wrap(openProjectsPanel),
				Components.wrap(console));
		rightSplitPane.setDividerPositions(0.75, 1.0);
		
		// Container for the whole view (everything under the toolbar)
		SplitPane explorerEditorSplitPane = new SplitPane();
		explorerEditorSplitPane.getItems().addAll(Components.wrap(leftSplitPane),
				Components.wrap(rightSplitPane));
		explorerEditorSplitPane.setDividerPositions(0.2, 1.0);
		
		loadOpenProjects();
		
		Parent menuBar = createMenuBar();
		Parent toolbar = createToolbar();
		BorderPane mainPanel = new BorderPane();
		VBox topContainer = new VBox();
		topContainer.getChildren().add(menuBar);
		topContainer.getChildren().add(toolbar);
		mainPanel.setTop(topContainer);
		mainPanel.setCenter(explorerEditorSplitPane);
		
		int width = DEFAULT_WINDOW_WIDTH;
		int height = DEFAULT_WINDOW_HEIGHT;
		primaryStage.setScene(new Scene(Components.wrap(mainPanel), width, height));
		primaryStage.show();
	}
	
	private File showOpenDialogue()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		
		String plp6Extension = "*" + PLPProject.FILE_EXTENSION;
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("PLP6 Project Files", plp6Extension),
				new ExtensionFilter("Legacy Project Files", "*.plp"),
				new ExtensionFilter("All PLP Project Files", "*.plp", plp6Extension),
				new ExtensionFilter("All Files", "*.*"));
				
		return fileChooser.showOpenDialog(stage);
	}
	
	private File showExportDialogue(ASMFile exportItem)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export");
		fileChooser.setInitialFileName(exportItem.getName() + ".asm");
		
		String plp6Extension = "*" + PLPProject.FILE_EXTENSION;
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("PLP6 Project Files", plp6Extension),
				new ExtensionFilter("Legacy Project Files", "*.plp"),
				new ExtensionFilter("All PLP Project Files", "*.plp", plp6Extension),
				new ExtensionFilter("All Files", "*.*"));
				
		return fileChooser.showOpenDialog(stage);
	}
	
	private File showImportDialogue()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import ASM");
		
		fileChooser.getExtensionFilters().addAll(
				new ExtensionFilter("ASM Files", "*.asm"),
				new ExtensionFilter("All Files", "*.*"));
				
		return fileChooser.showOpenDialog(stage);
	}
	
	private void openProjectFromFile()
	{
		File selectedFile = showOpenDialogue();
		if (selectedFile != null)
		{
			openProjectFromFile(selectedFile);
		}
	}
	
	/**
	 * Loads the given file from disk using {@link Project#load(File)}, and adds the
	 * project to the project explorer.
	 * <p>
	 * If the project is already in the project explorer, a message will be displayed
	 * indicating the project is already open, and the project will be expanded in the
	 * project tree.
	 * <p>
	 * If the project is not in the tree, but a project with the same name is in the tree,
	 * then a message will be displayed indicating that a project with the same name
	 * already exists, and will ask if the user would like to rename one of the projects.
	 * If not, the dialogue will be closed and the project will not be opened.
	 * 
	 * @param file
	 *            The file or directory (PLP6 only) containing the project to be opened
	 */
	private void openProjectFromFile(File file)
	{
		try
		{
			Project project = PLPProject.load(file);
			addProject(project);
		}
		catch (UnexpectedFileTypeException e)
		{
			showAlertDialogue(e, "The selected file could not be loaded");
		}
		catch (IOException e)
		{
			showAlertDialogue(e, "There was a problem loading the selected file");
		}
		catch (Exception e)
		{
			showAlertDialogue(e);
		}
	}
	
	private void addProject(Project project)
	{
		Project existingProject = getProjectByName(project.getName());
		if (existingProject != null)
		{
			if (existingProject.getPath().equals(project.getPath()))
			{
				// Projects are the same
				showInfoDialogue("This project is already open!");
				// TODO: expand project in the projectExplorer
			}
			else
			{
				// Project with the same name already exists
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation Dialog");
				alert.setGraphic(null);
				alert.setHeaderText(null);
				alert.setContentText("A project with the name \"" + project.getName()
						+ "\" already exists. In order to open this project, you must choose a different name."
						+ "\n\n"
						+ "Press OK to choose a new name, or Cancel to close this dialog.");
						
				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK)
				{
					boolean renamed = renameProject(project);
					if (renamed)
						addProject(project);
				}
			}
		}
		else
		{
			projects.add(project);
		}
	}
	
	private boolean renameProject(Project project)
	{
		TextInputDialog dialog = new TextInputDialog(project.getName());
		dialog.setTitle("Rename Project");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.setContentText("Enter a new name for the project:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent())
		{
			String newName = result.get();
			if (newName.equals(project.getName()))
			{
				showInfoDialogue("The new name must be different from the old name");
				return renameProject(project);
			}
			else
			{
				project.setName(newName);
			}
		}
		
		return false;
	}
	
	private Project getProjectByName(String name)
	{
		for (Project project : projects)
		{
			String projectName = project.getName();
			boolean namesAreNull = (projectName == null && name == null);
			if (namesAreNull || name.equals(projectName))
				return project;
		}
		
		return null;
	}
	
	/**
	 * Creates a tab for the specified project, or selects the project, if the tab already
	 * exists.
	 * 
	 * @param project
	 *            The project to open
	 */
	private void openFile(ASMFile file)
	{
		String fileName = file.getName();
		
		System.out.println("Opening " + fileName);
		Tab tab = openProjects.get(file);
		
		if (tab == null)
		{
			// Create new tab
			CodeEditor content = createCodeEditor();
			tab = addTab(openProjectsPanel, fileName, content);
			openProjects.put(file, tab);
			
			// Set content
			content.setText(file.getContent());
			
			// Bind content
			ChangeListener<? super String> onChanged;
			onChanged = (value, old, current) -> content.setText(file.getContent());
			file.contentProperty().addListener(onChanged);
		}
		
		// Activate the specified tab
		openProjectsPanel.getSelectionModel().select(tab);
	}
	
	private void saveProject(MouseEvent event)
	{
		Project activeProject = getActiveProject();
		tryAndReport(activeProject::save);
	}
	
	private void saveProjectAs()
	{
		Stage createProjectStage = new Stage();
		Parent myPane = saveAsMenu();
		Scene scene = new Scene(myPane, 600, 350);
		createProjectStage.setTitle("Save Project As");
		createProjectStage.setScene(scene);
		createProjectStage.setResizable(false);
		createProjectStage.show();
		
	}
	
	private Parent saveAsMenu()
	{
		BorderPane border = new BorderPane();
		border.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Label projectName = new Label();
		projectName.setText("New Project Name: ");
		projectName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projTextField = new TextField();
		projTextField.setText("Project Name");
		projTextField.requestFocus();
		projTextField.setPrefWidth(200);
		
		Label selectedProject = new Label();
		selectedProject.setText("Save Project: \"" + getActiveProject().getName() + "\" as :");
		selectedProject.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		Label projectLocation = new Label();
		projectLocation.setText("Location: ");
		projectLocation.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projLocationField = new TextField();
		projTextField.setPrefWidth(200);
		
		Button browseLocation = new Button();
		browseLocation.setText("Browse");
		browseLocation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				String chosenLocation = "";
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("Choose Project Location");
				File file = directoryChooser.showDialog(null);
				if (file != null)
				{
					chosenLocation = file.getAbsolutePath()
							.concat(File.separator + projTextField.getText());
					projLocationField.setText(chosenLocation);
				}
				
			}
		});
		
		Button saveAsButton = new Button("Save");
		saveAsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				String projectName;
				String projectLocation;
				projectName = projTextField.getText();
				projectLocation = projLocationField.getText();
				if (projectName == null || projectName.trim().isEmpty())
				{
					Dialogues.showInfoDialogue("You entered an invalid Project Name");
				}
				else if (projectLocation == null || projectLocation.trim().isEmpty())
				{
					Dialogues.showInfoDialogue("You entered an invalid Project Location");
					
				}
				else
				{
					// TODO: this is either a misnomer (should be path) or an issue
					projectName = projLocationField.getText();
					Project activeProject = getActiveProject();
					try
					{
						activeProject.saveAs(projectName);
					}
					catch (IOException ioException)
					{
						// TODO report exception to user
						ioException.printStackTrace();
					}
					Stage stage = (Stage) saveAsButton.getScene().getWindow();
					stage.close();
				}
			}
		});
		saveAsButton.setDefaultButton(true);
		Button cancelCreate = new Button("Cancel");
		cancelCreate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				Stage stage = (Stage) cancelCreate.getScene().getWindow();
				stage.close();
			}
		});
		
		grid.add(projectName, 0, 0);
		grid.add(projTextField, 1, 0);
		//grid.add(selectedProject, 0, 1);
		grid.add(projectLocation, 0, 2);
		grid.add(projLocationField, 1, 2);
		grid.add(browseLocation, 2, 2);
		
		border.setTop(selectedProject);
		border.setCenter(grid);
		
		buttons.getChildren().addAll(saveAsButton, cancelCreate);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		border.setBottom(buttons);
		
		return Components.wrap(border);
	}
	
	private CodeEditor createCodeEditor()
	{
		return new CodeEditor();
		/*
		 * try { CodeEditor editor = new CodeEditor(); File syntaxFile = new
		 * File("resources/languages/plp.syn"); editor.setSyntaxHighlighting(syntaxFile);
		 * return editor; } catch (IOException e) { e.printStackTrace(); return new
		 * CodeEditor(); }
		 */
	}
	
	private Tab addTab(TabPane panel, String projectName, Node contentPanel)
	{
		Tab tab = new Tab();
		tab.setText(projectName);
		tab.setContent(Components.wrap(contentPanel));
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				openProjects.removeValue(tab);
			}
		});
		tab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				ASMFile activeFile = openProjects.getKey(tab);
				if (activeFile != null)
					projectExplorer.setActiveFile(activeFile);
			}
		});
		panel.getTabs().add(tab);
		
		return tab;
	}
	
	private ConsolePane createConsole()
	{
		ConsolePane console = new ConsolePane();
		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem clearConsoleItem = new MenuItem("Clear");
		clearConsoleItem.setOnAction(e -> console.clear());
		contextMenu.getItems().add(clearConsoleItem);
		
		console.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
			contextMenu.show(console, event.getScreenX(), event.getScreenY());
			event.consume();
		});
		console.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY)
				contextMenu.hide();
		});
		
		return console;
	}
	
	private Parent createOutlineView()
	{
		// TODO: replace with relevant outline window
		return Components.wrap(new TextArea());
	}
	
	/**
	 * Restore all projects from a persistent data store, and call
	 * {@link #openProject(String, String)} for each
	 */
	private void loadOpenProjects()
	{
		// TODO: replace with actual content
	}
	
	/**
	 * Creates a project tree to display all known projects, and their contents. The tree
	 * orders projects as first level elements, with their folders and files being
	 * children elements.
	 * <p>
	 * This method is responsible for adding all appropriate listeners to allow navigation
	 * of the tree (and display it appropriately), including setting the background and
	 * any other applicable css attributes.
	 * <p>
	 * The returned tree will open a file in the {@link #openProjectsPanel} when a
	 * fileName is double-clicked.
	 * 
	 * @return A tree-view of the project explorer
	 */
	private ProjectExplorerTree createProjectTree()
	{
		projects = FXCollections.observableArrayList();
		ProjectExplorerTree projectExplorer = new ProjectExplorerTree(projects);
		
		PLPProject project = new PLPProject("Assignment1");
		project.add(new PLPSourceFile(project, "main.asm"));
		project.add(new PLPSourceFile(project, "sorting.asm"));
		project.add(new PLPSourceFile(project, "division.asm"));
		projects.add(project);
		
		project = new PLPProject("Assignment2");
		project.add(new PLPSourceFile(project, "main.asm"));
		project.add(new PLPSourceFile(project, "uart_utilities.asm"));
		projects.add(project);
		
		projectExplorer.setOnFileDoubleClicked(this::openFile);
		
		return projectExplorer;
	}
	
	/**
	 * Creates a horizontal toolbar containing controls to:
	 * <ul>
	 * <li>Create a new PLPProject
	 * <li>Add a new file
	 * <li>Save the current project
	 * <li>Open a new PLPProject
	 * <li>Assemble the current project
	 * </ul>
	 * 
	 * @return a Parent {@link Node} representing the PLP toolbar
	 */
	private Parent createToolbar()
	{
		HBox toolbar = new HBox();
		toolbar.setPadding(new Insets(1.5, 0, 1, 5));
		toolbar.setSpacing(5);
		ObservableList<Node> buttons = toolbar.getChildren();
		
		EventHandler<MouseEvent> listener;
		Node button;
		
		DropShadow lightBlueShadow = new DropShadow();
		lightBlueShadow.setColor(Color.LIGHTBLUE);
		DropShadow darkBlueShadow = new DropShadow();
		darkBlueShadow.setColor(Color.DARKBLUE);
		
		// TODO: replace event handlers with actual content
		Node projectButton = new ImageView("toolbar_new.png");
		projectButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e)
					{
						projectButton.setEffect(lightBlueShadow);
					}
				});
		// Removing the shadow when the mouse cursor is off
		projectButton.addEventHandler(MouseEvent.MOUSE_EXITED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e)
					{
						projectButton.setEffect(null);
					}
				});
		projectButton.addEventHandler(MouseEvent.MOUSE_PRESSED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e)
					{
						console.println("New Project Clicked");
						
						createNewProject();
						
						projectButton.setEffect(darkBlueShadow);
					}
				});
		projectButton.addEventHandler(MouseEvent.MOUSE_RELEASED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent e)
					{
						projectButton.setEffect(lightBlueShadow);
					}
				});
		buttons.add(projectButton);
		
		Node newFileButton = new ImageView("menu_new.png");
		listener = this::createASMFile;
		newFileButton.setOnMouseClicked(listener);
		buttons.add(newFileButton);
		
		button = new ImageView("toolbar_open.png");
		listener = this::onOpenProjectClicked;
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		button = new ImageView("toolbar_save.png");
		listener = this::saveProject;
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_assemble.png");
		listener = this::onAssembleProjectClicked;
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_simulate.png");
		listener = (event) -> (onSimProjectClicked(event, toolbar));
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_program.png");
		listener = (event) -> console.println("Program Project Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		button = new ImageView("toolbar_step.png");
		listener = (event) -> console.println("Step Through Project Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_run.png");
		listener = this::onRunProjectClicked;
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_reset.png");
		listener = (event) -> console.println("Reset Sim Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_remote.png");
		listener = (event) -> console.println("Floating Sim Control Window Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		buttons.add(new Separator(Orientation.VERTICAL));
		
		// I Think we are putting all these buttons onto one page
		// but until that happens I laid them all out, just in case
		
		button = new ImageView("toolbar_cpu.png");
		listener = (event) -> console.println("CPU View");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_watcher.png");
		listener = (event) -> console.println("Watcher Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_leds.png");
		listener = (event) -> console.println("LED's Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_switches.png");
		listener = (event) -> console.println("Switches Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_7segments.png");
		listener = (event) -> console.println("7 Seg Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_uart.png");
		listener = (event) -> console.println("UART Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_vga.png");
		listener = (event) -> console.println("VGA Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_plpid.png");
		listener = (event) -> console.println("PLPID Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_sim_gpio.png");
		listener = (event) -> console.println("GPIO Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		button = new ImageView("toolbar_exclamation.png");
		listener = (event) -> console.println("Interupt Clicked");
		button.setOnMouseClicked(listener);
		buttons.add(button);
		
		for (int x = 9; x <= 23; x++)
		{
			DropShadow dropShadow = new DropShadow();
			toolbar.getChildren().get(x).setEffect(dropShadow);
			toolbar.getChildren().get(x).setDisable(true);
		}
		
		return Components.wrap(toolbar);
	}
	
	private void onRunProjectClicked(ActionEvent event)
	{
		console.println("Run Project Clicked (from menu)");
		onRunProjectClicked();
	}
	
	private void onRunProjectClicked(MouseEvent event)
	{
		console.println("Run Project Clicked (from button)");
		onRunProjectClicked();
	}
	
	private void onRunProjectClicked()
	{
		Project activeProject = getActiveProject();
		
		ProjectAssemblyDetails details = assemblyDetails.get(activeProject);
		if (details != null && !details.isDirty())
		{
			run(activeProject);
		}
		else
		{
			// TODO: handle "Project Not Assembled" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	private void run(Project project)
	{
		Optional<ISAModule> optionalISA = project.getISA();
		if (optionalISA.isPresent())
		{
			ISAModule isa = optionalISA.get();
			Simulator simulator = isa.getSimulator();
			simulator.run();
		}
		else
		{
			// TODO: handle "no compatible ISA" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}

	private Parent createMenuBar()
	{
		MenuBar menuBar = new MenuBar();
		
		// Menu Items under "File"
		Menu file = new Menu("File");
		MenuItem itemNew = new MenuItem("New PLP Project");
		itemNew.setGraphic(new ImageView(new Image("menu_new.png")));
		itemNew.setAccelerator(
				new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		itemNew.setOnAction((event) -> {
			createNewProject();
		});
		
		MenuItem itemOpen = new MenuItem("Open PLP Project");
		itemOpen.setGraphic(new ImageView(new Image("toolbar_open.png")));
		itemOpen.setAccelerator(
				new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
		itemOpen.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSave = new MenuItem("Save");
		itemSave.setGraphic(new ImageView(new Image("toolbar_save.png")));
		itemSave.setAccelerator(
				new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		itemSave.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSaveAs = new MenuItem("Save As");
		itemSaveAs.setAccelerator(new KeyCodeCombination(KeyCode.A,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemSaveAs.setOnAction((event) -> {
			// TODO: Add Event for menu item
			saveProjectAs();
		});
		
		MenuItem itemPrint = new MenuItem("Print");
		itemPrint.setAccelerator(
				new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
		itemPrint.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemExit = new MenuItem("Exit");
		itemExit.setAccelerator(
				new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
		itemExit.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		file.getItems().addAll(itemNew, new SeparatorMenuItem(), itemOpen, itemSave,
				itemSaveAs, new SeparatorMenuItem(), itemPrint, new SeparatorMenuItem(),
				itemExit);
				
		// Menu Items under "Edit"
		Menu edit = new Menu("Edit");
		MenuItem itemCopy = new MenuItem("Copy");
		itemCopy.setAccelerator(
				new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
		itemCopy.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemCut = new MenuItem("Cut");
		itemCut.setAccelerator(
				new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
		itemCut.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPaste = new MenuItem("Paste");
		itemPaste.setAccelerator(
				new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
		itemPaste.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemFandR = new MenuItem("Find and Replace");
		itemFandR.setAccelerator(
				new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
		itemFandR.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemUndo = new MenuItem("Undo");
		itemUndo.setAccelerator(
				new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
		itemUndo.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemRedo = new MenuItem("Redo");
		itemRedo.setAccelerator(
				new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
		itemRedo.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		edit.getItems().addAll(itemCopy, itemCut, itemPaste, new SeparatorMenuItem(),
				itemFandR, new SeparatorMenuItem(), itemUndo, itemRedo);
				
		// Menu Items under "View"
		Menu view = new Menu("View");
		CheckMenuItem cItemToolbar = new CheckMenuItem("Toolbar");
		cItemToolbar.setAccelerator(new KeyCodeCombination(KeyCode.T,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemToolbar.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		CheckMenuItem cItemProjectPane = new CheckMenuItem("Project Pane");
		cItemProjectPane.setAccelerator(new KeyCodeCombination(KeyCode.P,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemProjectPane.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		CheckMenuItem cItemOutputPane = new CheckMenuItem("Output Pane");
		cItemOutputPane.setAccelerator(new KeyCodeCombination(KeyCode.O,
				KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
		cItemOutputPane.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemClearOutput = new MenuItem("Clear Output Pane");
		itemClearOutput.setAccelerator(
				new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
		itemClearOutput.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		view.getItems().addAll(cItemToolbar, cItemProjectPane, cItemOutputPane,
				itemClearOutput);
		cItemToolbar.setSelected(true);
		cItemProjectPane.setSelected(true);
		cItemOutputPane.setSelected(true);
		
		// Menu Items Under "Project"
		Menu project = new Menu("Project");
		MenuItem itemAssemble = new MenuItem("Assemble");
		itemAssemble.setGraphic(new ImageView(new Image("toolbar_assemble.png")));
		itemAssemble.setAccelerator(new KeyCodeCombination(KeyCode.F2));
		itemAssemble.setOnAction((event) -> {
			console.println("Assemble Menu Item Clicked");
			Project activeProject = getActiveProject();
			assemble(activeProject);
		});
		
		MenuItem itemSimulate = new MenuItem("Simulate");
		itemSimulate.setGraphic(new ImageView(new Image("toolbar_simulate.png")));
		itemSimulate.setAccelerator(new KeyCodeCombination(KeyCode.F3));
		itemSimulate.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPLPBoard = new MenuItem("Program PLP Board...");
		itemPLPBoard.setGraphic(new ImageView(new Image("toolbar_program.png")));
		itemPLPBoard.setAccelerator(
				new KeyCodeCombination(KeyCode.F4, KeyCombination.SHIFT_DOWN));
		itemPLPBoard.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemQuickProgram = new MenuItem("Quick Program");
		itemQuickProgram.setAccelerator(new KeyCodeCombination(KeyCode.F4));
		itemQuickProgram.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemNewASM = new MenuItem("New ASM File...");
		itemNewASM.setOnAction((event) -> {
			createASMFile(null);
			// TODO: Check this implementation, doesnt look correct
		});
		
		MenuItem itemImportASM = new MenuItem("Import ASM File...");
		itemImportASM.setOnAction((event) -> {
			File importTarget = showImportDialogue();
			try
			{
				String content = FileUtils.readFileToString(importTarget);
				Project activeProject = getActiveProject();
				String name = importTarget.getName();
				
				// TODO: account for non-PLP source files
				ASMFile asmFile = new PLPSourceFile(activeProject, name);
				asmFile.setContent(content);
				activeProject.add(asmFile);
				activeProject.save();
			}
			catch (Exception exception)
			{
				Dialogues.showAlertDialogue(exception, "Failed to import asm");
			}
		});
		
		MenuItem itemExportASM = new MenuItem("Export Selected ASM File...");
		itemExportASM.setOnAction((event) -> {
			ASMFile activeFile = getActiveFile();
			if (activeFile == null)
			{
				// XXX: possible feature: select file from a list or dropdown
				String message = "No file is selected! Open the file you wish to export, or select it in the ProjectExplorer.";
				Dialogues.showInfoDialogue(message);
			}
			
			File exportTarget = showExportDialogue(activeFile);
			if (exportTarget == null)
				return;
			
			if (exportTarget.isDirectory())
			{
				String exportPath = exportTarget.getAbsolutePath() 
						+ activeFile.constructFileName();
				exportTarget = new File(exportPath);
				
				String message = "File will be exported to " + exportPath;
				Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
				
				if (result.get() != ButtonType.OK)
				{
					// Export was canceled
					return;
				}
			}
			
			if (exportTarget.exists())
			{
				String message = "The specified file already exists. Press OK to overwrite this file, or cancel to cancel the export.";
				Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
				
				if (result.get() != ButtonType.OK)
				{
					// Export was canceled
					return;
				}
			}
			
			String fileContents = activeFile.getContent();
			try
			{
				FileUtils.write(exportTarget, fileContents);
			}
			catch (Exception exception)
			{
				Dialogues.showAlertDialogue(exception, "Failed to export asm");
			}
		});
		
		MenuItem itemRemoveASM = new MenuItem("Remove Selected ASM File from Project");
		itemRemoveASM.setAccelerator(
				new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
		itemRemoveASM.setOnAction((event) -> {
			removeActiveFile();
		});
		
		MenuItem itemCurrentAsMain = new MenuItem(
				"Set Current Open File as Main Program");
		itemCurrentAsMain.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		project.getItems().addAll(itemAssemble, itemSimulate, itemPLPBoard,
				itemQuickProgram, new SeparatorMenuItem(), itemNewASM, itemImportASM,
				itemExportASM, itemRemoveASM, new SeparatorMenuItem(), itemCurrentAsMain);
				
		// Menu Items Under "Tools"
		Menu tools = new Menu("Tools");
		MenuItem itemOptions = new MenuItem("Options");
		itemOptions.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		Menu modules = new Menu("Modules");
		MenuItem itemModuleManager = new MenuItem("Module Manager...");
		itemModuleManager.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemLoadJar = new MenuItem("Load Module JAR File...");
		itemLoadJar.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemClearCache = new MenuItem("Clear Module Auto-Load Cache");
		itemClearCache.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSerialTerminal = new MenuItem("Serial Terminal");
		itemSerialTerminal.setAccelerator(
				new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));
		itemSerialTerminal.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemNumConverter = new MenuItem("Number Converter");
		itemNumConverter.setAccelerator(new KeyCodeCombination(KeyCode.F12));
		itemNumConverter.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		modules.getItems().addAll(itemModuleManager, itemLoadJar, itemClearCache);
		tools.getItems().addAll(itemOptions, modules, new SeparatorMenuItem(),
				itemSerialTerminal, itemNumConverter);
				
		// Menu Items Under "Simulation"
		Menu simulation = new Menu("Simulation");
		MenuItem itemStep = new MenuItem("Step");
		itemStep.setGraphic(new ImageView(new Image("toolbar_step.png")));
		itemStep.setAccelerator(new KeyCodeCombination(KeyCode.F5));
		itemStep.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemReset = new MenuItem("Reset");
		itemReset.setGraphic(new ImageView(new Image("toolbar_reset.png")));
		itemReset.setAccelerator(new KeyCodeCombination(KeyCode.F9));
		itemReset.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemRun = new MenuItem("Run");
		itemRun.setAccelerator(new KeyCodeCombination(KeyCode.F7));
		itemRun.setOnAction(this::onRunProjectClicked);
		Menu cyclesSteps = new Menu("Cycles/Steps");
		MenuItem itemOne = new MenuItem("1");
		itemOne.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD1, KeyCombination.ALT_DOWN));
		itemOne.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemFive = new MenuItem("5");
		itemFive.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD2, KeyCombination.ALT_DOWN));
		itemFive.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemTwenty = new MenuItem("20");
		itemTwenty.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD3, KeyCombination.ALT_DOWN));
		itemTwenty.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemHundred = new MenuItem("100");
		itemHundred.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD4, KeyCombination.ALT_DOWN));
		itemHundred.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemFiveThousand = new MenuItem("5000");
		itemFiveThousand.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD5, KeyCombination.ALT_DOWN));
		itemFiveThousand.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemClearBreakpoints = new MenuItem("Clear Breakpoints");
		itemClearBreakpoints.setAccelerator(
				new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
		itemClearBreakpoints.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		Menu views = new Menu("Views");
		MenuItem itemCpuView = new MenuItem("CPU View");
		itemCpuView.setAccelerator(new KeyCodeCombination(KeyCode.C,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuView.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemCpuWindow = new MenuItem("Watcher Window");
		itemCpuWindow.setAccelerator(new KeyCodeCombination(KeyCode.W,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemCpuWindow.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSimControlWindow = new MenuItem("Simulation Control Window");
		itemSimControlWindow.setAccelerator(
				new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
		itemSimControlWindow.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		Menu toolsSubMenu = new Menu("Tools");
		MenuItem itemioRegistry = new MenuItem("I/O Registry");
		itemioRegistry.setAccelerator(new KeyCodeCombination(KeyCode.R,
				KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
		itemioRegistry.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemASMView = new MenuItem("ASM View");
		itemASMView.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemCreateMemVis = new MenuItem("Create a PLP CPU Memory Visualizer");
		itemCreateMemVis.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemRemoveMemVis = new MenuItem(
				"Remove Memory Visualizers from Project");
		itemRemoveMemVis.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemDisplayBus = new MenuItem("Display Bus Monitor Timing Diagram");
		itemDisplayBus.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		Menu ioDevices = new Menu("I/O Devices");
		MenuItem itemLedArray = new MenuItem("LED Array");
		itemLedArray.setGraphic(new ImageView(new Image("toolbar_sim_leds.png")));
		itemLedArray.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD1, KeyCombination.CONTROL_DOWN));
		itemLedArray.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSwitches = new MenuItem("Switches");
		itemSwitches.setGraphic(new ImageView(new Image("toolbar_sim_switches.png")));
		itemSwitches.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD2, KeyCombination.CONTROL_DOWN));
		itemSwitches.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSevenSeg = new MenuItem("Seven Segments");
		itemSevenSeg.setGraphic(new ImageView(new Image("toolbar_sim_7segments.png")));
		itemSevenSeg.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD3, KeyCombination.CONTROL_DOWN));
		itemSevenSeg.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemUART = new MenuItem("UART");
		itemUART.setGraphic(new ImageView(new Image("toolbar_sim_uart.png")));
		itemUART.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD4, KeyCombination.CONTROL_DOWN));
		itemUART.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemVGA = new MenuItem("VGA");
		itemVGA.setGraphic(new ImageView(new Image("toolbar_sim_vga.png")));
		itemVGA.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD5, KeyCombination.CONTROL_DOWN));
		itemVGA.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemPLPID = new MenuItem("PLPID");
		itemPLPID.setGraphic(new ImageView(new Image("toolbar_sim_plpid.png")));
		itemPLPID.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD6, KeyCombination.CONTROL_DOWN));
		itemPLPID.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemGPIO = new MenuItem("GPIO");
		itemGPIO.setGraphic(new ImageView(new Image("toolbar_sim_gpio.png")));
		itemGPIO.setAccelerator(
				new KeyCodeCombination(KeyCode.NUMPAD7, KeyCombination.CONTROL_DOWN));
		itemGPIO.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemExitSim = new MenuItem("ExitSimulation");
		itemExitSim.setAccelerator(new KeyCodeCombination(KeyCode.F11));
		itemExitSim.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		cyclesSteps.getItems().addAll(itemOne, itemFive, itemTwenty, itemHundred,
				itemFiveThousand);
		views.getItems().addAll(itemCpuView, itemCpuWindow, itemSimControlWindow);
		toolsSubMenu.getItems().addAll(itemioRegistry, itemASMView,
				new SeparatorMenuItem(), itemCreateMemVis, itemRemoveMemVis,
				itemDisplayBus);
		ioDevices.getItems().addAll(itemLedArray, itemSwitches, itemSevenSeg, itemUART,
				itemVGA, itemPLPID, itemGPIO);
		simulation.getItems().addAll(itemStep, itemReset, new SeparatorMenuItem(),
				itemRun, cyclesSteps, itemClearBreakpoints, new SeparatorMenuItem(),
				views, toolsSubMenu, ioDevices, new SeparatorMenuItem(), itemExitSim);
				
		// Menu Items Under "Help"
		Menu help = new Menu("Help");
		MenuItem itemQuickRef = new MenuItem("Quick Reference");
		itemQuickRef.setAccelerator(new KeyCodeCombination(KeyCode.F1));
		itemQuickRef.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemOnlineManual = new MenuItem("Online Manual");
		itemOnlineManual.setOnAction((event) -> {
			onlineManualWeb();
		});
		
		MenuItem itemReportIssue = new MenuItem("Report Issue (Requires Google Account");
		itemReportIssue.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemGoogleIssues = new MenuItem("Open Google Code Issues Page");
		itemGoogleIssues.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemAboutPLP = new MenuItem("About PLP Tool...");
		itemAboutPLP.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		MenuItem itemSWLicense = new MenuItem("Third Party Software License");
		itemSWLicense.setOnAction((event) -> {
			// TODO: Add Event for menu item
		});
		
		help.getItems().addAll(itemQuickRef, itemOnlineManual, new SeparatorMenuItem(),
				itemReportIssue, itemGoogleIssues, new SeparatorMenuItem(), itemAboutPLP,
				itemSWLicense);
				
		menuBar.getMenus().addAll(file, edit, view, project, tools, simulation, help);
		
		return Components.wrap(menuBar);
	}
	
	private void onOpenProjectClicked(MouseEvent event)
	{
		console.println("Open Project Clicked");
		openProjectFromFile();
	}
	
	private void onAssembleProjectClicked(MouseEvent event)
	{
		console.println("Assemble Button Clicked");
		Project activeProject = getActiveProject();
		assemble(activeProject);
	}
	
	private void assemble(Project project)
	{
		Optional<ISAModule> optionalISA = project.getISA();
		if (optionalISA.isPresent())
		{
			ISAModule isa = optionalISA.get();
			Assembler assembler = isa.getAssembler();
			assemble(assembler, project);
		}
		else
		{
			// TODO: handle "no compatible ISA" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	private void assemble(Assembler assembler, Project project)
	{
		try
		{
			ASMImage assembledImage = assembler.assemble(project);
			ProjectAssemblyDetails details = getAssemblyDetailsFor(project);
			details.setAssembledImage(assembledImage);
		}
		catch (AssemblerException exception)
		{
			console.error(exception.getLocalizedMessage());
		}
	}

	private ProjectAssemblyDetails getAssemblyDetailsFor(Project activeProject)
	{
		ProjectAssemblyDetails details = assemblyDetails.get(activeProject);
		
		if (details == null)
		{
			details = new ProjectAssemblyDetails();
			assemblyDetails.put(activeProject, details);
		}
		
		return details;
	}

	private Project getActiveProject()
	{
		ASMFile activeFile = getActiveFile();
		// TODO: check activeFile for null-value
		return activeFile.getProject();
	}
	
	private ASMFile getActiveFileInTabPane()
	{
		Tab selectedTab = openProjectsPanel.getSelectionModel().getSelectedItem();
		return openProjects.getKey(selectedTab);
	}
	
	private ASMFile getActiveFileInProjectExplorer()
	{
		Pair<Project, ASMFile> selection = projectExplorer.getActiveSelection();
		ASMFile selectedFile = selection.getValue();
		return selectedFile;
	}
	
	private ASMFile getActiveFile()
	{
		ASMFile selectedFile = getActiveFileInTabPane();
		if (selectedFile == null)
			return getActiveFileInProjectExplorer();
		else 
			return selectedFile;
	}

	private void onSimProjectClicked(MouseEvent event, HBox toolbar)
	{
		// TODO: Take out the hard values and replace with a better solution
		DropShadow ds = new DropShadow();
		if (!simMode)
		{
			for (int x = 9; x <= 23; x++)
			{
				toolbar.getChildren().get(x).setEffect(null);
				toolbar.getChildren().get(x).setDisable(false);
			}
			simMode = true;
		}
		else
		{
			for (int x = 9; x <= 23; x++)
			{
				toolbar.getChildren().get(x).setEffect(ds);
				toolbar.getChildren().get(x).setDisable(true);
			}
			simMode = false;
		}
	}
	
	private void onlineManualWeb()
	{
		String webAddress = "https://code.google.com/p/progressive-learning-platform/wiki/UserManual";
		try
		{
			if (Desktop.isDesktopSupported())
			{
				URI location = new URI(webAddress);
				Desktop.getDesktop().browse(location);
			}
			else
			{
				String cause = "This JVM does not support Desktop. Try updating Java to the latest version.";
				throw new Exception(cause);
			}
		}
		catch (Exception exception)
		{
			String recoveryMessage = "There was a problem opening the following webpage:"
					+ "\n" + webAddress;
			Dialogues.showAlertDialogue(exception, recoveryMessage);
		}
	}
	
	private void removeActiveFile()
	{
		ASMFile activeFile = getActiveFile();
		if (activeFile == null)
		{
			// XXX: possible feature: select file from a list or dropdown
			String message = "No file is selected! Select the file you wish to remove in the ProjectExplorer, then click remove.";
			Dialogues.showInfoDialogue(message);
			return;
		}
		
		File removalTarget = findDiskObjectForASM(activeFile);
		if (removalTarget == null)
		{
			// XXX: show a confirmation dialogue to confirm removal
			String message = "Unable to locate file on disk. "
					+ "The asm \"" + activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName() + 
					"\" but it is suggested that you verify the deletion from disk manually.";
			Dialogues.showInfoDialogue(message);
			Project activeProject = activeFile.getProject();
			activeProject.remove(activeFile);
			return;
		}
		
		if (removalTarget.isDirectory())
		{
			// XXX: show a confirmation dialogue to confirm removal
			String message = "The path specified is a directory, but should be a file."
					+ "The asm \"" + activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName() + 
					"\" but it is suggested that you verify the deletion from disk manually.";
			Exception exception = new IllegalStateException("The path to the specified ASMFile is a directory, but should be a file.");
			Dialogues.showAlertDialogue(exception, message);
			return;
		}
		else
		{
			String message = "The asm \"" + activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName() + "\" and the file at \""
					+ removalTarget.getAbsolutePath() + "\" will be deleted.";
			Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
			
			if (result.get() != ButtonType.OK)
			{
				// Removal was canceled
				return;
			}
		}
		
		if (!removalTarget.exists())
		{
			String message = "Unable to locate file on disk. The file will be removed from the project, but it is suggested that you verify the deletion from disk manually.";
			Dialogues.showInfoDialogue(message);
		}
		
		try
		{
			boolean wasRemoved = removalTarget.delete();
			if (!wasRemoved)
				throw new Exception("The file \"" 
						+ removalTarget.getAbsolutePath() + "\" was not deleted.");
		}
		catch (Exception exception)
		{
			Dialogues.showAlertDialogue(exception, "Failed to delete asm from disk. It is suggested that you verify the deletion from disk manually.");
		}
	}
	
	private File findDiskObjectForASM(ASMFile activeFile)
	{
		Project project = activeFile.getProject();
		String path = project.getPathFor(activeFile);
		if (path == null)
			return null;
		
		return new File(path);
	}
	
	private void createASMFile(MouseEvent event)
	{
		if (projects.isEmpty())
		{
			Dialogues.showInfoDialogue(
					"There are not projects open, please create a project first.");
		}
		else
		{
			Stage createASMStage = new Stage();
			Parent myPane = createASMMenu();
			Scene scene = new Scene(myPane, 450, 200);
			createASMStage.setTitle("New ASMFile");
			createASMStage.setScene(scene);
			createASMStage.setResizable(false);
			createASMStage.show();
		}
	}
	
	private Parent createASMMenu()
	{
		BorderPane border = new BorderPane();
		border.setPadding(new Insets(20));
		GridPane grid = new GridPane();
		HBox buttons = new HBox(10);
		grid.setHgap(10);
		grid.setVgap(30);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		Label ASMFileName = new Label();
		ASMFileName.setText("File Name: ");
		ASMFileName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField nameText = new TextField();
		nameText.setText("");
		nameText.requestFocus();
		nameText.setPrefWidth(200);
		
		Label projectName = new Label();
		projectName.setText("Add to Project: ");
		projectName.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
		
		TextField projectText = new TextField();
		projectText.setText(getActiveProject().getName());
		projectText.setPrefWidth(200);
		
		Button create = new Button();
		create.setText("Create");
		create.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e)
			{
				String projectName = projectText.getText();
				String fileName = nameText.getText();
				
				if (projectName.equals("") || getProjectByName(projectName).equals(null))
				{
					Dialogues.showInfoDialogue("You entered an invalid Project Name");
					
				}
				
				if (fileName == null || fileName.trim().isEmpty())
				{
					Dialogues.showInfoDialogue("You entered an invalid File Name");
				}
				
				if (!fileName.contains(".asm"))
				{
					fileName = fileName.concat(".asm");
				}
				
				PLPSourceFile createASM = new PLPSourceFile(getProjectByName(projectName),
						fileName);
				getProjectByName(projectName).add(createASM);
				openFile(createASM);
				
				Stage stage = (Stage) create.getScene().getWindow();
				stage.close();
			}
			
		});
		
		grid.add(ASMFileName, 0, 0);
		grid.add(nameText, 1, 0);
		grid.add(projectName, 0, 1);
		grid.add(projectText, 1, 1);
		
		border.setCenter(grid);
		buttons.getChildren().add(create);
		buttons.setAlignment(Pos.BASELINE_RIGHT);
		border.setBottom(buttons);
		
		return Components.wrap(border);
	}
	
	private void createNewProject()
	{
		Stage createProjectStage = new Stage();
		ProjectCreationPanel projectCreationPanel = projectCreateMenu();
		projectCreationPanel.setFinallyOperation(() -> createProjectStage.close());
		
		Scene scene = new Scene(projectCreationPanel, 450, 350);
		createProjectStage.setTitle("Create New PLP Project");
		createProjectStage.setScene(scene);
		createProjectStage.setResizable(false);
		createProjectStage.show();
	}
	
	private ProjectCreationPanel projectCreateMenu()
	{
		ProjectCreationPanel projectCreationPanel = new ProjectCreationPanel();
		projectCreationPanel.addProjectType("PLP6", this::createProject);
		projectCreationPanel.addProjectType("PLP5 (Legacy)", this::createLegacyProject);
		projectCreationPanel.setSelectedType("PLP6");
		return projectCreationPanel;
	}
	
	private void createLegacyProject(ProjectCreationDetails details)
	{
		PLPProject project = new PLPProject(details.getProjectName());
		project.setPath(details.getProjectLocation());
		
		String sourceName = details.getMainSourceFileName();
		PLPSourceFile sourceFile = new PLPSourceFile(project, sourceName);
		project.add(sourceFile);
		tryAndReport(project::saveLegacy);
		projects.add(project);
		openFile(sourceFile);
	}
	
	private void createProject(ProjectCreationDetails details)
	{
		PLPProject project = new PLPProject(details.getProjectName());
		project.setPath(details.getProjectLocation());
		
		String sourceName = details.getMainSourceFileName();
		PLPSourceFile sourceFile = new PLPSourceFile(project, sourceName);
		project.add(sourceFile);
		tryAndReport(project::save);
		projects.add(project);
		openFile(sourceFile);
	}
	
	private void tryAndReport(ExceptionalSubroutine subroutine)
	{
		try
		{
			subroutine.perform();
		}
		catch (Exception exception)
		{
			Dialogues.showAlertDialogue(exception);
		}
	}
}
