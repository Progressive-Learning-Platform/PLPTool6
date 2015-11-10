package edu.asu.plp.tool.prototype.view;

import edu.asu.plp.tool.prototype.model.Project;
import edu.asu.plp.tool.prototype.model.ProjectFile;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

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
	
	private void projectListChanged(ListChangeListener.Change<? extends Project> change)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
	
	private void addProjectToTree(Project project)
	{
		TreeItem<String> projectItem = new TreeItem<>(project.getName());
		
		for (ProjectFile file : project)
		{
			TreeItem<String> item = new TreeItem<>(file.getName());
			projectItem.getChildren().add(item);
		}
		
		projectTreeDisplay.getRoot().getChildren().add(projectItem);
	}
	
	private void addFileToProject(Project project, ProjectFile file)
	{
		TreeItem<String> item = new TreeItem<>(file.getName());
		
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("The method is not implemented yet.");
	}
}
