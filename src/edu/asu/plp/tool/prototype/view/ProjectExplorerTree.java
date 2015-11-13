package edu.asu.plp.tool.prototype.view;

import java.util.function.Consumer;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import edu.asu.plp.tool.prototype.model.Project;
import edu.asu.plp.tool.prototype.model.ProjectFile;

/**
 * An FX view of a list of known projects and their files displayed as a tree. This class
 * only displays projects specified by a given project model (see
 * {@link #setProjectsModel(ObservableList)}) and is not responsible for adding or
 * removing projects from the model.
 * <p>
 * However, {@link ProjectExplorerTree} uses data binding such that any changes made to
 * the project model will be reflected in this view automatically.
 * 
 * @author Moore, Zachary
 *
 */
public class ProjectExplorerTree extends BorderPane
{
	/**
	 * The project model displayed by this tree. All projects in this list should be
	 * displayed by the tree in the same order, with the same file order within projects.
	 * <p>
	 * This list is backed externally, and will thus be modified externally.
	 */
	private ObservableList<Project> projects;
	
	/** The view of this explorer */
	private TreeView<String> projectTreeDisplay;
	
	/**
	 * If a file item (not a project item) is double clicked in
	 * {@link #projectTreeDisplay}, this explorer should fire {@link #onFileDoubleClicked}
	 * <p>
	 * This is intended to be specified externally (not inside this class) using
	 * {@link #setOnFileDoubleClicked(Consumer)}
	 */
	private Consumer<ProjectFile> onFileDoubleClicked;
	
	public ProjectExplorerTree(ObservableList<Project> projectsModel)
	{
		assert projectsModel != null;
		projectTreeDisplay = createEmptyRootedProjectTree();
		projectTreeDisplay.setOnMouseClicked(this::onTreeClick);
		setCenter(projectTreeDisplay);
		
		setProjectsModel(projectsModel);
	}
	
	/**
	 * Set the listener for when a file is double clicked in this view. If an item
	 * representing a file is double clicked, the specified {@link Consumer} will be
	 * invoked, passing the selected {@link ProjectFile} as a parameter.
	 * <p>
	 * The specified listener will not be invoked if a file is single clicked, or if a
	 * <em>project</em> is double clicked.
	 * 
	 * @param onFileDoubleClicked
	 *            Listener to be called when a file item is double clicked
	 */
	public void setOnFileDoubleClicked(Consumer<ProjectFile> onFileDoubleClicked)
	{
		this.onFileDoubleClicked = onFileDoubleClicked;
	}
	
	/**
	 * Set the project model and update this view to display the specified projects and
	 * their files.
	 * <p>
	 * This view will no longer monitor or update in response to the old model.
	 * 
	 * @param projectsModel
	 */
	public void setProjectsModel(ObservableList<Project> projectsModel)
	{
		if (projects != null)
			this.projects.removeListener(this::projectListChanged);
		
		assert projectsModel != null;
		this.projects = projectsModel;
		this.projects.addListener(this::projectListChanged);
		
		this.projectTreeDisplay.getRoot().getChildren().clear();
		for (Project project : projectsModel)
			addProjectToTree(project);
	}
	
	/**
	 * Specifies an active file to be focused (and highlighted) by this
	 * {@link ProjectExplorerTree}. This could be used, for instance, to emphasize a
	 * specific file during a tutorial, or if the file is selected in a different view -
	 * to synchronize views.
	 * <p>
	 * This method is typically used by a controller or driver.
	 * 
	 * @param file
	 *            The target to focus
	 */
	public void setActiveFile(ProjectFile file)
	{
		TreeItem<String> fileNode = getFileNode(file);
		int focusIndex = getGlobalIndexOf(fileNode);
		projectTreeDisplay.getFocusModel().focus(focusIndex);
	}
	
	private int getGlobalIndexOf(TreeItem<String> fileNode)
	{
		TreeItem<String> root = projectTreeDisplay.getRoot();
		ObservableList<TreeItem<String>> projectNodes = root.getChildren();
		
		int index = 0;
		for (TreeItem<String> node : projectNodes)
		{
			if (node.equals(fileNode))
				return index;
			
			for (TreeItem<String> child : node.getChildren())
			{
				if (child.equals(fileNode))
					return index;
				index++;
			}
			index++;
		}
		
		throw new IllegalArgumentException(fileNode.getValue() + " not found in tree");
	}
	
	private void onTreeClick(MouseEvent event)
	{
		if (event.getClickCount() == 2)
		{
			TreeItem<String> selection = projectTreeDisplay.getSelectionModel()
					.getSelectedItem();
			TreeItem<String> parent = selection.getParent();
			if (onFileDoubleClicked == null)
			{
				return;
			}
			else if (parent != null && parent.getValue().length() > 0)
			{
				// Selection is a file
				Project project = lookupProjectByName(parent.getValue());
				for (ProjectFile file : project)
				{
					if (file.getName().equals(selection.getValue()))
					{
						onFileDoubleClicked.accept(file);
						break;
					}
				}
			}
		}
	}
	
	private Project lookupProjectByName(String value)
	{
		for (Project project : projects)
			if (project.getName().equals(value))
				return project;
		
		throw new IllegalStateException("Selected project not found: " + value);
	}
	
	private TreeView<String> createEmptyRootedProjectTree()
	{
		TreeItem<String> root = new TreeItem<String>("");
		root.setExpanded(true);
		
		TreeView<String> treeView = new TreeView<String>(root);
		treeView.showRootProperty().set(false);
		treeView.setBackground(Background.EMPTY);
		
		return treeView;
	}
	
	private void projectListChanged(Change<? extends Project> change)
	{
		while (change.next())
		{
			for (Project project : change.getAddedSubList())
				addProjectToTree(project);
			
			for (Project project : change.getRemoved())
				removeProjectFromTree(project);
		}
	}
	
	private void projectFilesChanged(Change<? extends ProjectFile> change)
	{
		while (change.next())
		{
			for (ProjectFile file : change.getAddedSubList())
				addFileToTree(file);
			
			for (ProjectFile file : change.getRemoved())
				removeFileFromTree(file);
		}
	}
	
	private void addProjectToTree(Project project)
	{
		project.addListener(this::projectFilesChanged);
		TreeItem<String> projectItem = new TreeItem<>(project.getName());
		
		for (ProjectFile file : project)
		{
			TreeItem<String> item = new TreeItem<>(file.getName());
			projectItem.getChildren().add(item);
		}
		
		projectTreeDisplay.getRoot().getChildren().add(projectItem);
	}
	
	private void addFileToTree(ProjectFile file)
	{
		TreeItem<String> fileNode = new TreeItem<>(file.getName());
		TreeItem<String> projectNode = getProjectNode(file.getProject());
		
		projectNode.getChildren().add(fileNode);
	}
	
	private void removeProjectFromTree(Project project)
	{
		TreeItem<String> projectNode = getProjectNode(project);
		projectTreeDisplay.getRoot().getChildren().remove(projectNode);
	}
	
	private void removeFileFromTree(ProjectFile file)
	{
		Project project = file.getProject();
		TreeItem<String> projectNode = getProjectNode(project);
		TreeItem<String> fileNode = getFileNode(file, projectNode);
		projectNode.getChildren().remove(fileNode);
	}
	
	private TreeItem<String> getProjectNode(Project project)
	{
		String name = project.getName();
		
		for (TreeItem<String> node : projectTreeDisplay.getRoot().getChildren())
		{
			if (node.getValue().equals(name))
				return node;
		}
		
		return null;
	}
	
	private TreeItem<String> getFileNode(ProjectFile file)
	{
		Project project = file.getProject();
		TreeItem<String> projectNode = getProjectNode(project);
		
		return getFileNode(file, projectNode);
	}
	
	private TreeItem<String> getFileNode(ProjectFile file, TreeItem<String> projectNode)
	{
		String name = file.getName();
		
		for (TreeItem<String> node : projectNode.getChildren())
		{
			if (node.getValue().equals(name))
				return node;
		}
		
		return null;
	}
}
