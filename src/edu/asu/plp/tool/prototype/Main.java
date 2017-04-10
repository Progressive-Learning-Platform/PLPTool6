package edu.asu.plp.tool.prototype;

import static edu.asu.plp.tool.prototype.util.Dialogues.showAlertDialogue;
import static edu.asu.plp.tool.prototype.util.Dialogues.showInfoDialogue;

import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import moore.fx.components.Components;
import moore.util.ExceptionalSubroutine;
import moore.util.Subroutine;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.io.FileUtils;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

import edu.asu.plp.tool.backend.EventRegistry;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.isa.events.AssemblerControlEvent;
import edu.asu.plp.tool.backend.isa.events.AssemblerResultEvent;
import edu.asu.plp.tool.backend.isa.events.SimulatorControlEvent;
import edu.asu.plp.tool.exceptions.DiskOperationFailedException;
import edu.asu.plp.tool.exceptions.ProjectAlreadyOpenException;
import edu.asu.plp.tool.exceptions.ProjectNameConflictException;
import edu.asu.plp.tool.exceptions.UnexpectedFileTypeException;
import edu.asu.plp.tool.prototype.model.ApplicationSetting;
import edu.asu.plp.tool.prototype.model.ApplicationThemeManager;
import edu.asu.plp.tool.prototype.model.OptionSection;
import edu.asu.plp.tool.prototype.model.PLPOptions;
import edu.asu.plp.tool.prototype.model.PLPProject;
import edu.asu.plp.tool.prototype.model.Project;
import edu.asu.plp.tool.prototype.model.QuickViewSection;
import edu.asu.plp.tool.prototype.model.SimpleASMFile;
import edu.asu.plp.tool.prototype.model.Submittable;
import edu.asu.plp.tool.prototype.model.Theme;
import edu.asu.plp.tool.prototype.model.ThemeRequestCallback;
import edu.asu.plp.tool.prototype.model.ThemeRequestEvent;
import edu.asu.plp.tool.prototype.util.Dialogues;
import edu.asu.plp.tool.prototype.view.CodeEditor;
import edu.asu.plp.tool.prototype.view.ConsolePane;
import edu.asu.plp.tool.prototype.view.CpuWindow;
import edu.asu.plp.tool.prototype.view.OutlineView;
import edu.asu.plp.tool.prototype.view.ProjectExplorerTree;
import edu.asu.plp.tool.prototype.view.QuickViewPanel;
import edu.asu.plp.tool.prototype.view.WatcherWindow;
import edu.asu.plp.tool.prototype.view.menu.options.OptionsPane;
import edu.asu.plp.tool.prototype.view.menu.options.sections.ApplicationSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.EditorSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.ProgrammerSettingsPanel;
import edu.asu.plp.tool.prototype.view.menu.options.sections.SimulatorSettingsPanel;

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
public class Main extends Application implements Controller
{
	public static final String APPLICATION_NAME = "PLPTool";
	public static final long VERSION = 0;
	public static final long REVISION = 1;
	public static final int DEFAULT_WINDOW_WIDTH = 1280;
	public static final int DEFAULT_WINDOW_HEIGHT = 720;
	
	private Thread simRunThread;
	private Stage stage;
	private TabPane openProjectsPanel;
	private BidiMap<ASMFile, Tab> openFileTabs;
	private ObservableList<PLPLabel> activeNavigationItems;
	private ProjectManager projectManager;

	private Map<String, ProjectAssemblyDetails> assemblyDetails;
	private ProjectExplorerTree projectExplorer;
	private ConsolePane console;
	private WatcherWindow watcher = null;
	private EmulationWindow emulationWindow = null;
	
	private ApplicationThemeManager applicationThemeManager;
	private OutlineView outlineView;
	private boolean isSimulationRunning;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	private void onTabActivation(ObservableValue<? extends Tab> value, Tab old,
			Tab current)
	{
		ASMFile previousASM = openFileTabs.getKey(current);
		if (previousASM != null)
			previousASM.contentProperty().removeListener(this::updateOutline);
		
		ASMFile asmFile = openFileTabs.getKey(current);
		if (asmFile != null)
		{
			String content = asmFile.getContent();
			List<PLPLabel> labels = PLPLabel.scrape(content);
			outlineView.setModel(FXCollections.observableArrayList(labels));
			asmFile.contentProperty().addListener(this::updateOutline);
		}
	}
	
	private void updateOutline(ObservableValue<? extends String> value, String old,
			String current)
	{
		List<PLPLabel> labels = PLPLabel.scrape(current);
		outlineView.setModel(FXCollections.observableArrayList(labels));
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
		
		this.projectManager = new ProjectManager();
		this.assemblyDetails = new HashMap<>();
		this.openFileTabs = new DualHashBidiMap<>();
		this.openProjectsPanel = new TabPane();
		this.projectExplorer = createProjectTree();
		outlineView = createOutlineView();
		console = createConsole();
		console.println(">> Console Initialized.");
		
		openProjectsPanel.getSelectionModel().selectedItemProperty()
				.addListener(this::onTabActivation);
		
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
		
		//loadOpenProjects();
		
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
		
		String themeName = ApplicationSettings.getSetting(
				ApplicationSetting.APPLICATION_THEME).get();
		EventRegistry.getGlobalRegistry().post(new ThemeRequestEvent(themeName));
		
		primaryStage.show();
		EventRegistry.getGlobalRegistry().register(this);
	}
	
	private Parent plpQuickRef()
	{
		// TODO: load this from a JSON file
		List<QuickViewSection> plp = new ArrayList<>();
		
		QuickViewSection instructionsRType = new QuickViewSection("R-Type Instructions");
		instructionsRType.addEntry("addu $rd, $rs, $rt", "rd = rs + rt");
		instructionsRType.addEntry("subu $rd, $rs, $rt", "rd = rs - rt");
		instructionsRType.addEntry("and $rd, $rs, $rt", "rd = rs & rt");
		instructionsRType.addEntry("or $rd, $rs, $rt", "rd = rs | rt");
		// TODO: add remaining R-Type instructions
		
		QuickViewSection instructionsIType = new QuickViewSection("I-Type Instructions");
		instructionsIType.addEntry("addiu $rt, $rs, imm", "rt = rs + SignExtend(imm)");
		instructionsIType.addEntry("andi $rt, $rs, imm", "rt = rs & ZeroExtend(imm)");
		instructionsIType.addEntry("ori $rt, $rs, imm", "rt = rs | ZeroExtend(imm)");
		// TODO: add remaining I-Type instructions
		
		QuickViewSection instructionsJType = new QuickViewSection("J-Type Instructions");
		instructionsJType.addEntry("j label", "PC = label");
		instructionsJType.addEntry("jal label", "ra = PC + 4; PC = label");
		
		QuickViewSection instructionsPsuedo = new QuickViewSection("Pseudo-Operations");
		instructionsPsuedo.addEntry("nop", "sll $0, $0, 0");
		instructionsPsuedo.addEntry("b label", "beq $0, $0, label");
		instructionsPsuedo.addEntry("move $rd, $rs", "or $rd, $0, $rs");
		// TODO: add remaining pseudo instructions
		
		QuickViewSection directives = new QuickViewSection("Assembler Directives");
		directives.addEntry(".org address",
				"Place subsequent statements starting from address");
		directives.addEntry("label:", "Label current memory location as label");
		directives.addEntry(".word value", "Write 32-bit value to the current address");
		directives.addEntry(".ascii \"string\"",
				"Place string starting from the current address");
		directives.addEntry(".asciiz \"string\"",
				"Place null-terminated string starting from the current address");
		directives.addEntry(".asciiw \"string\"",
				"Place word-aligned string starting from the current address");
		directives.addEntry(".space value",
				"Reserve value words starting from the current address");
		directives.addEntry(".equ symbol value",
				"Add a symbol and its associated value to the symbol table (a constant)");
		
		QuickViewSection registers = new QuickViewSection("Registers Usage Guide");
		registers.addEntry("$0, $zero", "The zero register");
		registers.addEntry("$1, $at", "Assembler temporary");
		registers.addEntry("$2-$3, $v0-$v1", "Return values");
		registers.addEntry("$4-$7, $a0-$a3", "Function arguments");
		registers.addEntry("$8-$17, $t0-$t9", "Temporaries");
		registers.addEntry("$18-$25, $s0-$s7", "Saved temporaries");
		registers.addEntry("$26-$27, $i0-$i1", "Interrupt temporaries");
		registers.addEntry("$28, $iv", "Interrupt vector");
		registers.addEntry("$29, $sp", "Stack pointer");
		registers.addEntry("$30, $ir", "Interrupt return address");
		registers.addEntry("$31, $ra", "Return address");
		
		QuickViewSection ioMap = new QuickViewSection("I/O Memory Map");
		ioMap.addEntry("0x00000000", "Boot ROM");
		ioMap.addEntry("0x10000000", "RAM");
		ioMap.addEntry("0xf0000000", "UART");
		ioMap.addEntry("0xf0100000", "Switches");
		ioMap.addEntry("0xf0200000", "LEDs");
		ioMap.addEntry("0xf0300000", "GPIO");
		ioMap.addEntry("0xf0400000", "VGA");
		ioMap.addEntry("0xf0500000", "PLPID");
		ioMap.addEntry("0xf0600000", "Timer");
		ioMap.addEntry("0xf0a00000", "Seven Segment Display");
		ioMap.addEntry("0xf0700000", "Interrupt Controller");
		
		plp.add(instructionsRType);
		plp.add(instructionsIType);
		plp.add(instructionsJType);
		plp.add(instructionsPsuedo);
		plp.add(directives);
		plp.add(registers);
		plp.add(ioMap);
		return new QuickViewPanel("PLP 5.2", plp);
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
	
	private File showExportDialogue(String defaultName)
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Export");
		fileChooser.setInitialFileName(defaultName + ".asm");
		
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
	
	
	@Override
	public void openProject()
	{
		File selectedFile = showOpenDialogue();
		if (selectedFile != null)
		{
			//If we are selecting the new format, that is a file with name .project.
			//In that case change the file to parent folder rather than file. - Harsha
			if(selectedFile.getName().endsWith(PLPProject.FILE_EXTENSION))
				selectedFile = selectedFile.getParentFile();
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
			Project project = new PLPProject(file);
			addProject(project);
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
		Project existingProject = projectManager.getProjectByName(project.getName());
		if (existingProject != null)
		{
			if (existingProject.getPath().equals(project.getPath()))
			{
				// Projects are the same
				showInfoDialogue("This project is already open!");
				projectExplorer.expandProject(existingProject);
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
			try {
				projectManager.addProject(project);
				projectManager.setActiveProject(project.getName());
			} catch (ProjectAlreadyOpenException | ProjectNameConflictException e) {
				e.printStackTrace();
			}
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
	
	private void navigateToLabel(PLPLabel label)
	{
		CodeEditor editor = getActiveCodeEditor();
		if (editor == null)
			throw new IllegalStateException("Cannot access active code editor");
		
		try
		{
			int lineNumber = label.getLineNumber();
			editor.jumpToLine(lineNumber);
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
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
		file.contentProperty().addListener(this::updateOutline);
		String fileName = file.getName();
		
		System.out.println("Opening " + fileName);
		Tab tab = openFileTabs.get(file);
		
		String str = file.getContent();
		
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
		}
		
		// Activate the specified tab
		openProjectsPanel.getSelectionModel().select(tab);
	}
	
	@Override
	public void editCopy()
	{
		Tab tab = openProjectsPanel.getSelectionModel().getSelectedItem();
		
		CodeEditor ed = (CodeEditor)tab.getContent();
		
		ed.copySelectionToClipboard();	
		
	}
	
	@Override
	public void editUndo()
	{
		Tab tab = openProjectsPanel.getSelectionModel().getSelectedItem();
		
		CodeEditor ed = (CodeEditor)tab.getContent();
		
		ed.undoText();	
		
	}
	
	@Override
	public void saveActiveProjectAs()
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
		selectedProject.setText("Save Project: \"" + projectManager.getActiveProject().getName()
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
				String projectName = projTextField.getText();
				String projectLocation = projLocationField.getText();
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
					Project activeProject = projectManager.getActiveProject();
					try
					{
						activeProject.saveAs(projectLocation);
					}
					catch (IOException ioException)
					{
						Dialogues.showAlertDialogue(ioException);
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
		tab.setContent(contentPanel);
		tab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				ASMFile asmFile = openFileTabs.removeValue(tab);
				if (asmFile != null)
				{
					asmFile.contentProperty().removeListener(Main.this::updateOutline);
				}
			}
		});
		tab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event event)
			{
				ASMFile activeFile = openFileTabs.getKey(tab);
				if (activeFile != null) {
					projectExplorer.setActiveFile(activeFile);
					projectManager.setActiveProject(activeFile.getProject().getName());
				}
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
	
	private OutlineView createOutlineView()
	{
		List<PLPLabel> activeLabels = scrapeLabelsInActiveTab();
		activeNavigationItems = FXCollections.observableArrayList(activeLabels);
		
		OutlineView outlineView = new OutlineView(activeNavigationItems);
		outlineView.setOnAction(this::navigateToLabel);
		
		return outlineView;
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
			project = new PLPProject(new File("examples/PLP Projects/memtest.plp"));
			addProject(project);
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
		ProjectExplorerTree projectExplorer = new ProjectExplorerTree(projectManager.getProjectLists());
		
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
	
	private Parent createMenuBar()
	{
		PLPToolMenuBarPanel menuBar = new PLPToolMenuBarPanel(this);
		return menuBar;
	}
	
	@Subscribe
	public void receivedAssembleResult(AssemblerResultEvent e) {
		if (!e.getAssembleSuccess()) {
			console.error(e.getErrorMessage());
			return;
		}
		ProjectAssemblyDetails details = getAssemblyDetailsFor(e.getProjectName());
		details.setAssembledImage(e.getAsmImage());
	}
	
	private void assemble(Project project)
	{
		if (project.getISA().isPresent())
		{
			EventRegistry.getGlobalRegistry().post(
									new AssemblerControlEvent("assemble", 
															project.getName(), 
															project.getType(),
															project));
		}
		else
		{
			// TODO: handle "no compatible ISA" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	private ProjectAssemblyDetails getAssemblyDetailsFor(String projectName)
	{
		ProjectAssemblyDetails details = assemblyDetails.get(projectName);
		Project activeProject = projectManager.getActiveProject();
		if (details == null)
		{
			details = new ProjectAssemblyDetails(activeProject);
			System.out.println("GXY debug: " + activeProject.getName());
			assemblyDetails.put(activeProject.getName(), details);
		}
		
		return details;
	}
	
	private CodeEditor getActiveCodeEditor()
	{
		Tab activeTab = openProjectsPanel.getSelectionModel().getSelectedItem();
		if (activeTab == null)
			return null;
		
		Node tabContents = activeTab.getContent();
		if (tabContents != null)
			return (CodeEditor) tabContents;
		else
			return null;
	}
	
	private String getActiveFileInProjectExplorer()
	{
		Pair<Project, ASMFile> selection = projectExplorer.getActiveSelection();
		if (selection == null)
			return null;
		
		ASMFile selectedFile = selection.getValue();
		return selectedFile.getName();
	}
	
	private ASMCreationPanel createASMMenu()
	{
		ASMCreationPanel createASMMenu = new ASMCreationPanel(this::createASM);
		List<Project> projects = projectManager.getProjectLists();
		for (Project project : projects)
		{
			String projectName = project.getName();
			createASMMenu.addProjectName(projectName);
		}
		return createASMMenu;
	}
	
	private void createASM(ASMCreationDetails details)
	{
		String projectName = details.getProjectName();
		String fileName = details.getFileName();
		
		Project project = projectManager.getProjectByName(projectName);
		if (project != null)
		{
			SimpleASMFile createASM = new SimpleASMFile(project, fileName);
			project.add(createASM);
			openFile(createASM);
		}
		else
		{
			String message = "The project \"" + projectName + "\" could not be found";
			throw new IllegalArgumentException(message);
		}
	}
	
	@Override
	public void createNewProject()
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
		SimpleASMFile sourceFile = new SimpleASMFile(project, sourceName);
		project.add(sourceFile);
		tryAndReport(project::saveLegacy);
		addProject(project);
		openFile(sourceFile);
	}
	
	private void createProject(ProjectCreationDetails details)
	{
		PLPProject project = new PLPProject(details.getProjectName());
		project.setPath(details.getProjectLocation());
		
		String sourceName = details.getMainSourceFileName();
		SimpleASMFile sourceFile = new SimpleASMFile(project, sourceName);
		project.add(sourceFile);
		tryAndReport(project::save);
		addProject(project);
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
	
	public void showEmulationWindow()
	{
		Stage createEmulationStage = new Stage();
		
		
		Scene scene = new Scene(emulationWindow, 1275, 700);
		createEmulationStage.setTitle("Emulation Window");
		createEmulationStage.setScene(scene);
		// createEmulationStage.setResizable(false);
		createEmulationStage.show();
		

	}
	
	public void openCpuViewWindow()
	{
		Stage createCpuStage = new Stage();
		CpuWindow cpuWindowView = new CpuWindow();
		
		Scene scene = new Scene(cpuWindowView, 1200, 700);
		createCpuStage.setTitle("PLP CPU Core Simulation");
		createCpuStage.setScene(scene);
		createCpuStage.show();
	}
	
	private boolean optionsMenuOkSelected(List<Submittable> submittables)
	{
		for (Submittable submittable : submittables)
		{
			if (!submittable.isValid())
				return false;
		}
		return true;
	}
	
	private HashMap<OptionSection, Pane> createOptionsMenuModel(
			List<Submittable> submittables)
	{
		HashMap<OptionSection, Pane> model = new LinkedHashMap<>();
		
		addApplicationOptionSettings(model, submittables);
		addEditorOptionSettings(model, submittables);
		addASimulatorOptionSettings(model, submittables);
		addProgrammerOptionSettings(model, submittables);
		
		// TODO Accept new things
		
		return model;
	}
	
	private void addApplicationOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions applicationSection = new PLPOptions("Application");
		
		ObservableList<String> applicationThemeNames = FXCollections
				.observableArrayList();
		applicationThemeNames.addAll(applicationThemeManager.getThemeNames());
		
		// TODO acquire editor theme names
		// TODO add filters, disabling sounds retarded. Just filter and put non adjacent
		// at bottom
		ObservableList<String> editorThemeNames = FXCollections.observableArrayList();
		editorThemeNames.addAll("eclipse", "tomorrow", "xcode", "ambiance", "monokai",
				"twilight");
		
		ApplicationSettingsPanel applicationPanel = new ApplicationSettingsPanel(
				applicationThemeNames, editorThemeNames);
		submittables.add(applicationPanel);
		
		model.put(applicationSection, applicationPanel);
	}
	
	private void addEditorOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions editorSection = new PLPOptions("Editor");
		ObservableList<String> fontNames = getAvailableFontNames();
		
		// TODO acquire editor modes
		ObservableList<String> editorModes = FXCollections.observableArrayList();
		editorModes.addAll("plp");
		
		EditorSettingsPanel editorPanel = new EditorSettingsPanel(fontNames, editorModes);
		submittables.add(editorPanel);
		
		model.put(editorSection, editorPanel);
	}
	
	private ObservableList<String> getAvailableFontNames()
	{
		ObservableList<String> fontNames = FXCollections.observableArrayList();
		GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		java.awt.Font[] fonts = graphicsEnvironment.getAllFonts();
		for (java.awt.Font font : fonts)
		{
			String fontName = font.getFontName();
			fontNames.add(fontName);
		}
		
		return fontNames;
	}
	
	private void addASimulatorOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions simulatorSection = new PLPOptions("Simulator");
		
		SimulatorSettingsPanel simulatorPanel = new SimulatorSettingsPanel();
		submittables.add(simulatorPanel);
		
		model.put(simulatorSection, simulatorPanel);
	}
	
	private void addProgrammerOptionSettings(HashMap<OptionSection, Pane> model,
			List<Submittable> submittables)
	{
		PLPOptions programmerSection = new PLPOptions("Programmer");
		
		ProgrammerSettingsPanel programmerPanel = new ProgrammerSettingsPanel();
		submittables.add(programmerPanel);
		
		model.put(programmerSection, programmerPanel);
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
	
	@Override
	public void saveActiveProject()
	{
		Project activeProject = projectManager.getActiveProject();
		tryAndReport(activeProject::save);
	}
	
	@Override
	public void printActiveFile()
	{
		CodeEditor activeEditor = getActiveCodeEditor();
		if (activeEditor == null)
		{
			Dialogues.showActionFailedDialogue("No file is open!");
			return;
		}
		
		PrinterJob printAction = PrinterJob.createPrinterJob();
		if (printAction == null)
		{
			Dialogues.showActionFailedDialogue("Unable to access system print utilities");
			return;
		}
		
		boolean notCancelled = printAction.showPrintDialog(stage);
		if (notCancelled)
		{
			boolean success = printAction.printPage(activeEditor);
			if (success)
				printAction.endJob();
			else
				Dialogues.showActionFailedDialogue("Print may have failed");
		}
	}
	
	@Override
	public void createNewASM()
	{
		if (projectManager.isEmpty())
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
	
	@Override
	public void importASM()
	{
		File importTarget = showImportDialogue();
		try
		{
			projectManager.importASM(importTarget.getAbsolutePath());
		}
		catch (Exception exception)
		{
			Dialogues.showAlertDialogue(exception, "Failed to import asm");
		}
	}
	
	@Override
	public void exportASM()
	{
		// XXX: Consider moving this to a component
		String activeFileName = getActiveFileInProjectExplorer();
		if (activeFileName == null)
		{
			// XXX: possible feature: select file from a list or dropdown
			String message = "No file is selected! Open the file you wish to export, or select it in the ProjectExplorer.";
			Dialogues.showInfoDialogue(message);
			return;
		}
		
		File exportTarget = showExportDialogue(activeFileName);
		if (exportTarget == null)
			return;
		
		if (exportTarget.isDirectory())
		{
			String exportPath = exportTarget.getAbsolutePath()
					+ activeFileName;
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
		
		try {
			projectManager.exportASM(activeFileName, exportTarget.getAbsolutePath(), true);
		} catch (FileAlreadyExistsException | DiskOperationFailedException e) {
			e.printStackTrace();
		}
//		String fileContents = activeFile.getContent();
//		try
//		{
//			FileUtils.write(exportTarget, fileContents);
//		}
//		catch (Exception exception)
//		{
//			Dialogues.showAlertDialogue(exception, "Failed to export asm");
//		}
	}
	
	@Override
	public void removeASM()
	{
		String activeFileName = getActiveFileInProjectExplorer();
		if (activeFileName == null)
		{
			// XXX: possible feature: select file from a list or dropdown
			String message = "No file is selected! Select the file you wish to remove in the ProjectExplorer, then click remove.";
			Dialogues.showInfoDialogue(message);
			return;
		}
		
		File removalTarget = projectManager.findDiskObjectForASM(activeFileName);
		if (removalTarget == null)
		{
			// XXX: show a confirmation dialogue to confirm removal
			String message = "Unable to locate file on disk. "
					+ "The asm \""
					+ activeFileName
					+ "\" will be removed from the project \""
					+ projectManager.getActiveProject().getName()
					+ "\" but it is suggested that you verify the deletion from disk manually.";
			Dialogues.showInfoDialogue(message);
			try {
				projectManager.removeASM(activeFileName);
			} catch (FileNotFoundException | UnexpectedFileTypeException | DiskOperationFailedException e) {
				e.printStackTrace();
			}
			return;
		}
		
		if (removalTarget.isDirectory())
		{
			// XXX: show a confirmation dialogue to confirm removal
			String message = "The path specified is a directory, but should be a file. "
					+ "The asm \""
					+ activeFileName
					+ "\" will be removed from the project \""
					+ projectManager.getActiveProject().getName()
					+ "\" but it is suggested that you verify the deletion from disk manually.";
			Exception exception = new IllegalStateException(
					"The path to the specified ASMFile is a directory, but should be a file.");
			Dialogues.showAlertDialogue(exception, message);
			return;
		}
		else
		{
			String message = "The asm \"" + activeFileName
					+ "\" will be removed from the project \""
					+ projectManager.getActiveProject().getName() + "\" and the file at \""
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
	
	@Override
	public void setMainASMFile()
	{
		String activeFileName = getActiveFileInProjectExplorer();
		if (activeFileName == null)
		{
			Dialogues.showActionFailedDialogue("No file is selected!");
			return;
		}
		

		String message = "The file \"" + activeFileName
				+ "\" will be used as the main file for the project \""
				+ projectManager.getActiveProject().getName() + "\"";
		Optional<ButtonType> result = Dialogues.showConfirmationDialogue(message);
		
		if (result.get() == ButtonType.OK)
		{
			projectManager.setMainASMFile(activeFileName);
		}
	}
	
	@Override
	public void exit()
	{
		stage.close();
		Platform.exit();
	}
	
	@Override
	public void clearConsole()
	{
		console.clear();
	}
	
	@Override
	public void clearAllBreakpoints()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showNumberConverter()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void assembleActiveProject()
	{
		Project activeProject = projectManager.getActiveProject();
		assemble(activeProject);
	}
	
	@Override
	public void simulateActiveProject()
	{
		Project activeProject = projectManager.getActiveProject();
		
		if (activeProject.getISA().isPresent())
		{
			emulationWindow = new EmulationWindow();
			
			EventRegistry.getGlobalRegistry().post(
								new SimulatorControlEvent("load", activeProject.getType(),
														getAssemblyDetailsFor(activeProject.getName()).getAssembledImage()));
			
		}
		else
		{
			String message = "No simulator is available for the project type: ";
			message += activeProject.getType();
			Dialogues.showAlertDialogue(new IllegalStateException(message));
		}
		
		// TODO: open associated views? emulation window?
	}
	
	@Override
	public void downloadActiveProjectToBoard()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void stepSimulation()
	{
		performIfActive(() -> {
			EventRegistry.getGlobalRegistry().post(
								new SimulatorControlEvent("step", "", null));
		});
	}
	
	private void performIfActive(Subroutine subroutine)
	{
		try
		{
			subroutine.perform();
		}
		catch (Exception exception)
		{
			throw new IllegalStateException("No simulator is active!", exception);
		}
	}
	
	@Override
	public void triggerSimulationInterrupt()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void resetSimulation()
	{
		EventRegistry.getGlobalRegistry().post(new SimulatorControlEvent("reset", "", null));
	}
	
	@Override
	public void runSimulation()
	{
		Project activeProject = projectManager.getActiveProject();
		
		ProjectAssemblyDetails details = getAssemblyDetailsFor(activeProject.getName());
		if (!details.isDirty())
		{
			//activeSimulator.loadProgram(details.getAssembledImage());
			EventRegistry.getGlobalRegistry().post(
					new SimulatorControlEvent("load", "", details.getAssembledImage()));
			//performIfActive(activeSimulator::run);
			isSimulationRunning = true;
			simRunThread = new Thread(new Runnable(){
				public void run()
				{
					while (isSimulationRunning) {
						EventRegistry.getGlobalRegistry().post(new SimulatorControlEvent("step", "", null));
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
				}
			});
			simRunThread.start();
			
		}
		else
		{
			// TODO: handle "Project Not Assembled" case
			throw new UnsupportedOperationException("Not yet implemented");
		}
	}
	
	@Override
	public void changeSimulationSpeed(int requestedSpeed)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void stopSimulation()
	{
		EventRegistry.getGlobalRegistry().post(new SimulatorControlEvent("pause", "", null));
		EventRegistry.getGlobalRegistry().post(new SimulatorControlEvent("reset", "", null));
		isSimulationRunning = false;
		if(simRunThread != null)
		{
			simRunThread.interrupt();
			simRunThread = null;
		}
		// TODO: deactivate simulation views (e.g. Emulation Window)
	}
	
	@Override
	public void loadModule()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void clearModuleCache()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showQuickReference()
	{
		Stage stage = new Stage();
		
		// TODO: remove hard-coded numbers. Where did these even come from?
		// TODO account for different quick references, such as MIPs, x86, etc
		Scene scene = new Scene(plpQuickRef(), 888, 500);
		stage.setTitle("Quick Reference");
		stage.setScene(scene);
		stage.show();
	}
	
	@Override
	public void showOnlineManual()
	{
		String webAddress = "https://code.google.com/p/progressive-learning-platform/wiki/UserManual";
		openWebPage(webAddress);
	}
	
	private void openWebPage(String webAddress)
	{
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
	public void reportIssue()
	{
		String webAddress = "https://github.com/zcmoore/plpTool-prototype/issues";
		openWebPage(webAddress);
	}
	
	@Override
	public void showIssuesPage()
	{
		String webAddress = "https://github.com/zcmoore/plpTool-prototype/issues/new";
		openWebPage(webAddress);
	}
	
	@Override
	public void showAboutPLPTool()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	@Override
	public void showThirdPartyLicenses()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	
	
	@Override
	public void showOptionsMenu()
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
		popupWindow.setMinHeight(stage.getScene().getHeight()
				- (stage.getScene().getHeight() / 3));
		
		popupScene.getStylesheets().addAll(stage.getScene().getStylesheets());
		
		optionsPane.setOkAction(() -> {
			if (optionsMenuOkSelected(submittables))
			{
				submittables.forEach(submittable -> submittable.submit());
				popupWindow.close();
			}
		});
		optionsPane.setCancelAction(() -> {
			popupWindow.close();
		});
		
		popupWindow.setOnCloseRequest((windowEvent) -> {
			popupWindow.close();
		});
		popupWindow.show();
	}
	
	@Override
	public void showWatcherWindow()
	{
		Stage stage = new Stage();
		// TODO: pass active memory module and register File to WatcherWindow
		//WatcherWindow watcherWindow = new WatcherWindow(new , new PLPRegFile());
		watcher = emulationWindow.getWatcherWindow();
		
		Scene scene = new Scene(watcher, 888, 500);
		stage.setTitle("Watcher Window");
		stage.setScene(scene);
		stage.show();
	}
	
	@Override
	public void showModuleManager()
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}

	@Override
	public void saveAll() {
		tryAndReport(projectManager::saveAll);
	}
}