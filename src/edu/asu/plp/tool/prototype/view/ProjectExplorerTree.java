package edu.asu.plp.tool.prototype.view;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
	private ObservableList<Project> projects;
	private TreeView<String> projectTreeDisplay;
	
	public ProjectExplorerTree()
	{
		projectTreeDisplay = createEmptyRootedProjectTree();
		
		projects = new SimpleListProperty<>();
		projects.addListener(this::projectListChanged);
	}
	
	public void setProjectsModel(ObservableList<Project> projectsModel)
	{
		assert projectsModel != null;
		this.projects = projectsModel;
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
		for (Project project : change.getAddedSubList())
			addProjectToTree(project);
		
		for (Project project : change.getRemoved())
			removeProjectFromTree(project);
	}
	
	private void projectFilesChanged(Change<? extends ProjectFile> change)
	{
		for (ProjectFile file : change.getAddedSubList())
			addFileToTree(file);
		
		for (ProjectFile file : change.getRemoved())
			removeFileFromTree(file);
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
	
	@SuppressWarnings("unused")
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
