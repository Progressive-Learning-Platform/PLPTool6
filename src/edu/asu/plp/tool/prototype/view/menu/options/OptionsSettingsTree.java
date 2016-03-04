package edu.asu.plp.tool.prototype.view.menu.options;

import edu.asu.plp.tool.prototype.model.OptionSection;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created by Morgan on 2/28/2016.
 */
public class OptionsSettingsTree extends BorderPane
{
	private TreeView<String> sections;

	private Consumer<String> onSectionDoubleClicked;

	public OptionsSettingsTree( Set<OptionSection> sectionsList )
	{
		sections = createEmptyRootedProjectTree();
		sections.setOnMouseClicked(this::onTreeClick);
		setCenter(sections);

		populateSectionsTree(sectionsList);
	}

	private void populateSectionsTree( Set<OptionSection> sectionsList )
	{
		for ( OptionSection section : sectionsList )
		{
			//TODO this feels disgusting. Fix this
			if ( !section.getFullPath().contains(".") )
			{
				TreeItem<String> sectionItem = new TreeItem<>(section.getName());
				if ( section.size() > 0 )
					sectionItem.getChildren().addAll(getSectionChildren(section));

				sections.getRoot().getChildren().add(sectionItem);
			}
		}
	}

	private List<TreeItem<String>> getSectionChildren( OptionSection section )
	{
		List<TreeItem<String>> subSectionList = new ArrayList<>();

		for ( OptionSection subSection : section )
		{
			TreeItem<String> subSectionItem = new TreeItem<>(subSection.getName());
			if ( subSection.size() > 0 )
				subSectionItem.getChildren().addAll(getSectionChildren(subSection));

			subSectionList.add(subSectionItem);
		}
		return subSectionList;
	}

	private void onTreeClick( MouseEvent event )
	{
		if ( event.getClickCount() == 2 )
		{
			if ( onSectionDoubleClicked != null )
			{

			}
		}
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
}
