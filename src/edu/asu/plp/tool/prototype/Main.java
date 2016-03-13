package edu.asu.plp.tool.prototype;

import static edu.asu.plp.tool.prototype.util.Dialogues.showAlertDialogue;
import static edu.asu.plp.tool.prototype.util.Dialogues.showInfoDialogue;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;
import java.util.List;

import javafx.application.Application;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Pair;
import moore.fx.components.Components;
import moore.util.ExceptionalSubroutine;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.io.FileUtils;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

import edu.asu.plp.tool.backend.EventRegistry;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.ASMImage;
import edu.asu.plp.tool.backend.isa.Assembler;
import edu.asu.plp.tool.backend.isa.Simulator;
import edu.asu.plp.tool.backend.isa.exceptions.AssemblerException;
import edu.asu.plp.tool.core.ISAModule;
import edu.asu.plp.tool.exceptions.UnexpectedFileTypeException;
import edu.asu.plp.tool.prototype.model.*;
import edu.asu.plp.tool.prototype.view.menu.options.OptionsPane;
import edu.asu.plp.tool.prototype.view.menu.options.sections.ApplicationSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.EditorSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.ProgrammerSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.SimulatorSettingsPanel;
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
 * @author Nesbitt, Morgan
 * 
 */
public class Main extends Application implements BusinessLogic
{
	public static final String APPLICATION_NAME = "PLPTool";
	public static final long VERSION = 0;
	public static final long REVISION = 1;
	public static final int DEFAULT_WINDOW_WIDTH = 1280;
	public static final int DEFAULT_WINDOW_HEIGHT = 720;
	public boolean simMode = false;
	
	private Simulator activeSimulator;
	private Stage stage;
	private TabPane openProjectsPanel;
	private BidiMap<ASMFile, Tab> openFileTabs;
	private ObservableList<Project> projects;
	private Map<Project, ProjectAssemblyDetails> assemblyDetails;
	private ProjectExplorerTree projectExplorer;
	private ConsolePane console;
	
	private ApplicationThemeManager applicationThemeManager;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage)
	{
		this.stage = primaryStage;
		primaryStage.setTitle(APPLICATION_NAME + " V" + VERSION + "." + REVISION);
		
		ApplicationSettings.initialize();
		ApplicationSettings.loadFromFile("settings/plp-tool.settings");
		
		EventRegistry.getGlobalRegistry().register(new ApplicationEventBusEventHandler());
		
		applicationThemeManager = new ApplicationThemeManager();
		
		this.assemblyDetails = new HashMap<>();
		this.openFileTabs = new DualHashBidiMap<>();
		this.openProjectsPanel = new TabPane();
		this.projectExplorer = createProjectTree();
		Parent outlineView = createOutlineView();
		console = createConsole();
		console.println(">> Console Initialized.");
		
		ScrollPane scrollableProjectExplorer = new ScrollPane(projectExplorer);
		scrollableProjectExplorer.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollableProjectExplorer.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollableProjectExplorer.setFitToHeight(true);
		scrollableProjectExplorer.setFitToWidth(true);
		
		// Left side holds the project tree and outline view
		SplitPane leftSplitPane = new SplitPane();
		leftSplitPane.orientationProperty().set(Orientation.VERTICAL);
		leftSplitPane.getItems().addAll(scrollableProjectExplorer, outlineView);
		leftSplitPane.setDividerPositions(0.5, 1.0);
		leftSplitPane.setMinSize(0, 0);
		
		// Right side holds the source editor and the output console
		SplitPane rightSplitPane = new SplitPane();
		rightSplitPane.orientationProperty().set(Orientation.VERTICAL);
		rightSplitPane.getItems().addAll(Components.wrap(openProjectsPanel),
				Components.wrap(console));
		rightSplitPane.setDividerPositions(0.75, 1.0);
		rightSplitPane.setMinSize(0, 0);
		
		// Container for the whole view (everything under the toolbar)
		SplitPane explorerEditorSplitPane = new SplitPane();
		explorerEditorSplitPane.getItems().addAll(Components.wrap(leftSplitPane),
				Components.wrap(rightSplitPane));
		explorerEditorSplitPane.setDividerPositions(0.225, 1.0);
		explorerEditorSplitPane.setMinSize(0, 0);
		
		SplitPane.setResizableWithParent(leftSplitPane, Boolean.FALSE);
		
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
		
		Scene scene = new Scene(Components.wrap(mainPanel), width, height);
		
		primaryStage.setScene(scene);

		String themeName = ApplicationSettings.getSetting(ApplicationSetting.APPLICATION_THEME).get();
		EventRegistry.getGlobalRegistry().post(new ThemeRequestEvent(themeName));

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
				alert.setContentText("A project with the name \""
						+ project.getName()
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
		Tab tab = openFileTabs.get(file);
		
		if (tab == null)
		{
			// Create new tab
			CodeEditor content = createCodeEditor();
			tab = addTab(openProjectsPanel, fileName, content);
			openFileTabs.put(file, tab);
			
			// Set content
			if (file.getContent() != null)
				content.setText(file.getContent());
			else
				content.setText("");
			
			// Bind content
			file.contentProperty().bind(content);
			file.contentProperty().addListener(
					(value, old, current) -> System.out.println(current));
		}
		
		// Activate the specified tab
		openProjectsPanel.getSelectionModel().select(tab);
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
		selectedProject.setText("Save Project: \"" + getActiveProject().getName()
				+ "\" as :");
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
					chosenLocation = file.getAbsolutePath().concat(
							File.separator + projTextField.getText());
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
		// grid.add(selectedProject, 0, 1);
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
	
	private List<PLPLabel> scrapeLabelsInActiveTab()
	{
		Tab selectedTab = openProjectsPanel.getSelectionModel().getSelectedItem();
		if (selectedTab == null)
			return Collections.emptyList();
		else
		{
			ASMFile activeASM = openFileTabs.getKey(selectedTab);
			String content = activeASM.getContent();
			return PLPLabel.scrape(content);
		}
	}
	
	private CodeEditor createCodeEditor()
	{
		return new CodeEditor();
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
				openFileTabs.removeValue(tab);
			}
		});
		tab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				ASMFile activeFile = openFileTabs.getKey(tab);
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
		try
		{
			PLPProject project;
			project = PLPProject.load(new File("examples/PLP Projects/memtest.plp"));
			projects.add(project);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
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
		MainToolbar toolbar = new MainToolbar(this);
		return toolbar;
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
		PLPToolMenuBarPanel menuBar = new PLPToolMenuBarPanel(this);
		return menuBar;
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
	
	private void toggleSimulation()
	{
		// TODO: activate simulator?
		simMode = !simMode;
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
		return openFileTabs.getKey(selectedTab);
	}
	
	private ASMFile getActiveFileInProjectExplorer()
	{
		Pair<Project, ASMFile> selection = projectExplorer.getActiveSelection();
		if (selection == null)
			return null;
		
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
					+ "The asm \""
					+ activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName()
					+ "\" but it is suggested that you verify the deletion from disk manually.";
			Dialogues.showInfoDialogue(message);
			Project activeProject = activeFile.getProject();
			activeProject.remove(activeFile);
			return;
		}
		
		if (removalTarget.isDirectory())
		{
			// XXX: show a confirmation dialogue to confirm removal
			String message = "The path specified is a directory, but should be a file."
					+ "The asm \""
					+ activeFile.getName()
					+ "\" will be removed from the project \""
					+ activeFile.getProject().getName()
					+ "\" but it is suggested that you verify the deletion from disk manually.";
			Exception exception = new IllegalStateException(
					"The path to the specified ASMFile is a directory, but should be a file.");
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
				throw new Exception("The file \"" + removalTarget.getAbsolutePath()
						+ "\" was not deleted.");
		}
		catch (Exception exception)
		{
			Dialogues
					.showAlertDialogue(
							exception,
							"Failed to delete asm from disk. It is suggested that you verify the deletion from disk manually.");
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
			Dialogues
					.showInfoDialogue("There are not projects open, please create a project first.");
		}
		else
		{
			Stage createASMStage = new Stage();
			ASMCreationPanel asmCreationMenu = createASMMenu();
			asmCreationMenu.setFinallyOperation(createASMStage::close);
			
			Scene scene = new Scene(asmCreationMenu, 450, 200);
			createASMStage.setTitle("New ASMFile");
			createASMStage.setScene(scene);
			createASMStage.setResizable(false);
			createASMStage.show();
		}
	}
	
	private ASMCreationPanel createASMMenu()
	{
		ASMCreationPanel createASMMenu = new ASMCreationPanel(this::createASM);
		String projectName = getActiveProject().getName();
		createASMMenu.setProjectName(projectName);
		return createASMMenu;
	}
	
	private void createASM(ASMCreationDetails details)
	{
		String projectName = details.getProjectName();
		String fileName = details.getFileName();
		
		Project project = getProjectByName(projectName);
		if (project != null)
		{
			PLPSourceFile createASM = new PLPSourceFile(project, fileName);
			project.add(createASM);
			openFile(createASM);
		}
		else
		{
			// TODO: display message "The project {name} was not found"
			// TODO: ask to use the active project?
			throw new IllegalStateException("Project \"" + projectName + "\" not found");
		}
	}
	
	private void createNewProject()
	{
		Stage createProjectStage = new Stage();
		ProjectCreationPanel projectCreationPanel = projectCreateMenu();
		projectCreationPanel.setFinallyOperation(createProjectStage::close);
		
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
	
	@Override
	public void onCreateNewProject(ActionEvent event)
	{
		createNewProject();
	}
	
	@Override
	public void onOpenProject(ActionEvent event)
	{
		console.println("Open Project Clicked");
		openProjectFromFile();
	}
	
	@Override
	public void onSaveProject(ActionEvent event)
	{
		Project activeProject = getActiveProject();
		tryAndReport(activeProject::save);
	}
	
	@Override
	public void onSaveProjectAs(ActionEvent event)
	{
		saveProjectAs();
	}
	
	@Override
	public void onPrint(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onExit(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onToggleToolbar(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onToggleProjectPane(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onToggleOutputPane(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onClearOutputPane(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onAssemble(ActionEvent event)
	{
		console.println("Assemble Menu Item Clicked");
		Project activeProject = getActiveProject();
		assemble(activeProject);
	}
	
	@Override
	public void onSimulate(ActionEvent event)
	{
		toggleSimulation();
	}
	
	@Override
	public void onDownloadToBoard(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onNewASMFile(ActionEvent event)
	{
		// TODO: Check this implementation, doesnt look correct
		createASMFile(null);
	}
	
	@Override
	public void onImportASMFile(ActionEvent event)
	{
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
	}
	
	@Override
	public void onExportASMFile(ActionEvent event)
	{
		// XXX: Consider moving this to a component
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
	}
	
	@Override
	public void onRemoveASMFile(ActionEvent event)
	{
		removeActiveFile();
	}
	
	@Override
	public void onSetMainASMFile(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenQuickReference(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenOnlineManual(ActionEvent event)
	{
		// XXX: consider moving to a sub-component
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
	
	@Override
	public void onOpenIssueReport(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenIssuesPage(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onAboutPLPToolPanel(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenThirdPartyLicenses(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onSimulationStep(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onResetSimulation(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onRunSimulation(ActionEvent event)
	{
		console.println("Run Project Clicked (from menu)");
		onRunProjectClicked();
	}
	
	@Override
	public void onChangeSimulationSpeed(ActionEvent event, int requestedSpeed)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onClearBreakpoints(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenCPUView(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenWatcherWindow(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayLEDEmulator(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplaySwitchesEmulator(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplaySevenSegmentEmulator(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayUARTEmulator(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayVGAEmulator(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayPLPIDEmulator(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayGPIOEmulator(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onStopSimulation(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenOptionsMenu(ActionEvent event)
	{
		List<Submittable> submittables = new ArrayList<>();
		Map<OptionSection, Pane> optionsMenuModel = createOptionsMenuModel(submittables);

		OptionsPane optionsPane = new OptionsPane(optionsMenuModel);
		Scene popupScene = new Scene(optionsPane);

		Stage popupWindow = new Stage(StageStyle.DECORATED);
		popupWindow.setTitle("Settings");
		popupWindow.initModality(Modality.WINDOW_MODAL);
		popupWindow.initOwner(stage);
		popupWindow.setScene(popupScene);

		popupWindow.setMinWidth(stage.getScene().getWidth() / 2);
		popupWindow.setMinHeight(stage.getScene().getHeight()  - (stage.getScene().getHeight() / 3));


		popupScene.getStylesheets().addAll(stage.getScene().getStylesheets());

		optionsPane.setOkAction(()-> {
			if(optionsMenuOkSelected(submittables))
			{
				submittables.forEach(submittable -> submittable.submit());
				popupWindow.close();
			}
		});
		optionsPane.setCancelAction(() -> {popupWindow.close();});

		popupWindow.setOnCloseRequest((windowEvent)-> {popupWindow.close();});
		popupWindow.show();
	}

	private boolean optionsMenuOkSelected(List<Submittable> submittables)
	{
		for ( Submittable submittable : submittables )
		{
			if(!submittable.isValid())
				return false;
		}
		return true;
	}

	private HashMap<OptionSection, Pane> createOptionsMenuModel( List<Submittable> submittables )
	{
		HashMap<OptionSection, Pane> model =  new LinkedHashMap<>();

		addApplicationOptionSettings(model, submittables);
		addEditorOptionSettings(model, submittables);
		addASimulatorOptionSettings(model, submittables);
		addProgrammerOptionSettings(model, submittables);

		//TODO Accept new things

		return model;
	}

	private void addApplicationOptionSettings( HashMap<OptionSection, Pane> model, List<Submittable> submittables )
	{
		PLPOptions applicationSection = new PLPOptions("Application");

		ObservableList<String> applicationThemeNames = FXCollections.observableArrayList();
		applicationThemeNames.addAll(applicationThemeManager.getThemeNames());

		//TODO acquire editor theme names
		//TODO add filters, disabling sounds retarded. Just filter and put non adjacent at bottom
		ObservableList<String> editorThemeNames = FXCollections.observableArrayList();
		editorThemeNames.addAll("eclipse", "tomorrow", "xcode", "ambiance", "monokai", "twilight");

		ApplicationSettingsPanel applicationPanel = new ApplicationSettingsPanel(applicationThemeNames, editorThemeNames);
		submittables.add(applicationPanel);

		model.put(applicationSection, applicationPanel);
	}

	private void addEditorOptionSettings( HashMap<OptionSection, Pane> model, List<Submittable> submittables )
	{
		PLPOptions editorSection = new PLPOptions("Editor");

		//TODO acquire all usable fonts
		ObservableList<String> fontNames = FXCollections.observableArrayList();
		fontNames.addAll("courier", "inconsolata");

		//TODO acquire editor modes
		ObservableList<String> editorModes = FXCollections.observableArrayList();
		editorModes.addAll("plp");

		EditorSettingsPanel editorPanel = new EditorSettingsPanel(fontNames, editorModes);
		submittables.add(editorPanel);

		model.put(editorSection, editorPanel);
	}

	private void addASimulatorOptionSettings( HashMap<OptionSection, Pane> model, List<Submittable> submittables )
	{
		PLPOptions simulatorSection = new PLPOptions("Simulator");

		SimulatorSettingsPanel simulatorPanel = new SimulatorSettingsPanel();
		submittables.add(simulatorPanel);

		model.put(simulatorSection, simulatorPanel);
	}

	private void addProgrammerOptionSettings( HashMap<OptionSection, Pane> model, List<Submittable> submittables )
	{
		PLPOptions programmerSection = new PLPOptions("Programmer");

		ProgrammerSettingsPanel programmerPanel = new ProgrammerSettingsPanel();
		submittables.add(programmerPanel);

		model.put(programmerSection, programmerPanel);
	}
	
	@Override
	public void onOpenModuleManager(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onLoadModule(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onClearModuleCache(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenSerialTerminal(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenNumberConverter(ActionEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onCreateNewProject(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenProject(MouseEvent event)
	{
		console.println("Open Project Clicked");
		openProjectFromFile();
	}
	
	@Override
	public void onSaveProject(MouseEvent event)
	{
		Project activeProject = getActiveProject();
		tryAndReport(activeProject::save);
	}
	
	@Override
	public void onSaveProjectAs(MouseEvent event)
	{
		saveProjectAs();
	}
	
	@Override
	public void onAssemble(MouseEvent event)
	{
		console.println("Assemble Button Clicked");
		Project activeProject = getActiveProject();
		assemble(activeProject);
	}
	
	@Override
	public void onSimulate(MouseEvent event)
	{
		toggleSimulation();
	}
	
	@Override
	public void onNewASMFile(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onSimulationStep(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onSimulationInterrupt(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onResetSimulation(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onRunSimulation(MouseEvent event)
	{
		console.println("Run Project Clicked (from button)");
		onRunProjectClicked();
	}
	
	@Override
	public void onOpenCPUView(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onOpenWatcherWindow(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayLEDEmulator(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplaySwitchesEmulator(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplaySevenSegmentEmulator(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayUARTEmulator(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayVGAEmulator(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayPLPIDEmulator(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void onDisplayGPIOEmulator(MouseEvent event)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	public class ApplicationEventBusEventHandler
	{
		private ApplicationEventBusEventHandler()
		{
			
		}
		
		@Subscribe
		public void applicationThemeRequestCallback(ThemeRequestCallback event)
		{
			if (event.requestedTheme().isPresent())
			{
				Theme applicationTheme = event.requestedTheme().get();
				try
				{
					stage.getScene().getStylesheets().clear();
					stage.getScene().getStylesheets().add(applicationTheme.getPath());
					return;
				}
				catch (MalformedURLException e)
				{
					console.warning("Unable to load application theme "
							+ applicationTheme.getName());
					return;
				}
			}
			
			console.warning("Unable to load application theme.");
		}
		
		@Subscribe
		public void deadEvent(DeadEvent event)
		{
			System.out.println("Dead Event");
			System.out.println(event.getEvent());
		}
	}
}
