package edu.asu.plp.tool.prototype.view.menu.options;


import edu.asu.plp.tool.prototype.model.OptionSection;
import edu.asu.plp.tool.prototype.model.PLPOptions;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import moore.util.Subroutine;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * @author Nesbitt, Morgan on 2/27/2016.
 */
public class OptionsPane extends BorderPane
{
	private OptionsSettingsTree sections;
	private BorderPane sectionView;
	private AbstractMap<OptionSection, BorderPane> optionScreenMap;

	private Subroutine okAction;
	private Subroutine cancelAction;

	public OptionsPane()
	{
		retrieveOptionsSections();
		sections = new OptionsSettingsTree(optionScreenMap.keySet());
		sectionView = new BorderPane();

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


	private AbstractMap<OptionSection, BorderPane> retrieveOptionsSections()
	{
		optionScreenMap = new LinkedHashMap<>();

		constructorApplicationSection();
		constructorEditorSection();
		constructorSimulatorSection();
		constructorProgrammerSection();

		return optionScreenMap;
	}

	private OptionSection constructorEditorSection()
	{
		PLPOptions editorSection = new PLPOptions("Editor");

		PLPOptions general = new PLPOptions("General");
		PLPOptions editorTheming = new PLPOptions("Theming");

		editorSection.addAll(Arrays.asList(general, editorTheming));

		optionScreenMap.put(editorSection, new BorderPane());
		optionScreenMap.put(general, new BorderPane());
		optionScreenMap.put(editorTheming, new BorderPane());

		return editorSection;
	}

	private OptionSection constructorSimulatorSection()
	{
		PLPOptions simulatorSection = new PLPOptions("Simulator");

		optionScreenMap.put(simulatorSection, new BorderPane());

		return simulatorSection;
	}

	private OptionSection constructorProgrammerSection()
	{
		PLPOptions programmerSection = new PLPOptions("Programmer");

		optionScreenMap.put(programmerSection, new BorderPane());

		return programmerSection;
	}

	private OptionSection constructorApplicationSection()
	{
		PLPOptions applicationSection = new PLPOptions("Application");

		PLPOptions appearance = new PLPOptions("Appearance");
		PLPOptions toolbars = new PLPOptions("Toolbars");

		applicationSection.addAll(Arrays.asList(appearance, toolbars));

		optionScreenMap.put(appearance, new BorderPane());
		optionScreenMap.put(toolbars, new BorderPane());
		optionScreenMap.put(applicationSection, new BorderPane());

		return applicationSection;
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
