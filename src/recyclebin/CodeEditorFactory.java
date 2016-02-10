package recyclebin;

import java.io.File;
import java.io.IOException;

import edu.asu.plp.tool.prototype.view.CodeEditor;

public class CodeEditorFactory
{
	public CodeEditor createEditorWithSyntaxHighlighting()
	{
		try
		{
			CodeEditor editor = new CodeEditor();
			File syntaxFile = new File("resources/languages/plp.syn");
			editor.setSyntaxHighlighting(syntaxFile);
			return editor;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return new CodeEditor();
		}
	}
}
