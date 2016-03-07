package edu.asu.plp.tool.prototype.view.menu.options;


import edu.asu.plp.tool.prototype.model.OptionSection;
import edu.asu.plp.tool.prototype.view.SwapPane;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import moore.util.Subroutine;

import java.util.AbstractMap;
import java.util.HashMap;

/**
 * @author Nesbitt, Morgan on 2/27/2016.
 */
public class OptionsPane extends BorderPane
{
	//TODO add proper event handling for adding options
	private OptionsSettingsTree sections;
	private SwapPane sectionView;
	private AbstractMap<OptionSection, Pane> optionScreenMap;

	private Subroutine okAction;
	private Subroutine cancelAction;

	public OptionsPane( HashMap<OptionSection, Pane> optionsMenuModel )
	{
		//TODO use some kind of selection model to default to select first item or last item selected
		optionScreenMap = optionsMenuModel;
		sections = new OptionsSettingsTree(optionsMenuModel.keySet());
		sections.setTreeDoubleClick(this::onTreeDoubleClick);

		sectionView = new SwapPane();

		SplitPane sectionContentSplitPane = new SplitPane();
		sectionContentSplitPane.setOrientation(Orientation.HORIZONTAL);
		sectionContentSplitPane.getItems().addAll(sections, sectionView);

		sectionContentSplitPane.setDividerPositions(0.4, 1);
		setCenter(sectionContentSplitPane);

		okAction = () -> {
		};
		cancelAction = () -> {
		};

		Button okButton = new Button();
		okButton.setText("OK");
		okButton.setOnMouseClicked(this::okButtonClicked);

		Button cancelButton = new Button();
		cancelButton.setText("Cancel");
		cancelButton.setOnMouseClicked(this::cancelButtonClicked);

		HBox buttonBar = new HBox();
		buttonBar.getChildren().addAll(okButton, cancelButton);
		buttonBar.setAlignment(Pos.BASELINE_RIGHT);

		setBottom(buttonBar);
	}

	private void onTreeDoubleClick( OptionSection selection, OptionSection selectionRoot )
	{
		//Note these are separate in the instance that you want to break them up
		//TODO focus to specifc area (if desired) when not equal
		if ( optionScreenMap.containsKey(selection) )
			sectionView.setActivePane(optionScreenMap.get(selection));
		else
			sectionView.setActivePane(optionScreenMap.get(selectionRoot));
	}

	public void setOkAction( Subroutine okAction )
	{
		this.okAction = ( okAction != null ) ? okAction : () -> {
		};
	}

	public void setCancelAction( Subroutine cancelAction )
	{
		this.cancelAction = ( cancelAction != null ) ? cancelAction : () -> {
		};
	}

	private void okButtonClicked( MouseEvent mouseEvent )
	{
		okAction.perform();
	}

	private void cancelButtonClicked( MouseEvent mouseEvent )
	{
		cancelAction.perform();
	}
}
