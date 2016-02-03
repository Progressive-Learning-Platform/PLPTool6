package edu.asu.plp.tool.prototype.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import edu.asu.plp.tool.backend.isa.ASMFile;
import edu.asu.plp.tool.backend.util.PLP5ProjectParser;
import edu.asu.plp.tool.core.ISAModule;
import edu.asu.plp.tool.core.ISARegistry;
import edu.asu.plp.tool.exceptions.UnexpectedFileTypeException;

/**
 * A {@link PLPProject} represents an ordered, observable collection of
 * {@link PLPSourceFile}s that can be assembled collectively as a single unit.
 * 
 * @author Moore, Zachary
 *
 */
public class PLPProject extends ArrayListProperty<ASMFile> implements Project
{
	public static final String FILE_EXTENSION = ".project";
	private static final String PROJECT_FILE_NAME = "" + FILE_EXTENSION;
	
	/**
	 * Path to this project on in the file system. If the this project exists in memory
	 * only (it has not yet been written to disk), then the value contained by
	 * {@link #pathProperty} should be null.
	 * <p>
	 * Note that {@link #pathProperty} itself should always be non-null.
	 * <p>
	 * Also note that the path should point to a directory, unless the project was loaded
	 * from a legacy source. The actual project FILE will be located in the given
	 * directory, with the name "{@value #PROJECT_FILE_NAME}" The files contained by this
	 * project will be located in a subdirectory named "src"
	 * <p>
	 * In the case of a legacy file, the path will point to the project file directly, and
	 * no src directory will exist.
	 */
	private StringProperty pathProperty;
	private StringProperty nameProperty;
	private StringProperty typeProperty;
	
	/**
	 * Loads a {@link PLPProject} from the given project file. This method auto-detects
	 * the project version, and is therefore capable of loading both PLP6 and legacy (PLP5
	 * and prior) projects.
	 * 
	 * @param file
	 *            file or directory of the specified project
	 * @return A {@link PLPProject} representative of the information stored in the given
	 *         file.
	 * @throws UnexpectedFileTypeException
	 *             if the given file is not a PLP project file (PLP6 or legacy formats).
	 * @throws IOException
	 *             if an IO problem occurs while opening the specified file.
	 */
	public static PLPProject load(File file) throws UnexpectedFileTypeException,
			IOException
	{
		// TODO: implement
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	/**
	 * Loads a {@link PLPProject} from the given project file. This method calls
	 * {@link #load(File)}, which auto-detects the project version, and is therefore
	 * capable of loading both PLP6 and legacy (PLP5 and prior) projects.
	 * 
	 * @param filePath
	 *            Path to the specified file; may be relative or absolute
	 * @return A {@link PLPProject} representative of the information stored in the given
	 *         file.
	 * @throws UnexpectedFileTypeException
	 *             if the given file is not a PLP project file (PLP6 or legacy formats).
	 * @throws IOException
	 *             if an IO problem occurs while opening the specified file.
	 * @see #load(File)
	 */
	public static Project load(String filePath) throws UnexpectedFileTypeException,
			IOException
	{
		// TODO: implement
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	/**
	 * Loads a project file from PLP5 or earlier. The format for these older versions is
	 * Tarball, and typically have the .plp extension.
	 * <p>
	 * Legacy PLP projects should have a tarball entry called "plp.metafile" and will
	 * specify the file version on the first line of the entry, in the format "PLP-#.#"
	 * 
	 * @param filePath
	 *            Path to the specified file; may be relative or absolute
	 * @returnA {@link PLPProject} representative of the information stored in the given
	 *          file.
	 */
	private static Project loadLegacy(String filePath)
	{
		try
		{
			File file = new File(filePath);
			PLP5ProjectParser parser = new PLP5ProjectParser();
			return parser.parse(file);
		}
		catch (IOException exception)
		{
			// TODO: rethrow appropriate exception
			return null;
		}
	}
	
	public PLPProject()
	{
		pathProperty = new SimpleStringProperty();
		nameProperty = new SimpleStringProperty();
		typeProperty = new SimpleStringProperty();
	}
	
	public PLPProject(String name, String type)
	{
		this();
		nameProperty.set(name);
		typeProperty.set(type);
	}
	
	public PLPProject(String name)
	{
		// TODO: remove this constructor and force a filetype to be declared
		this(name, "plp");
	}
	
	/**
	 * Outputs this project and all its files to the directory specified by
	 * {@link #getPath()}, as a PLP6 project file with a src directory of .asm files.
	 * <p>
	 * The project information specified by this object will be saved in a file within the
	 * specified directory named "{@value #PROJECT_FILE_NAME}"
	 * <p>
	 * The files contained by this project will be saved to a subdirectory named "src"
	 * <p>
	 * Source files will be exported to a child directory of the project, called "src" and
	 * each .asm file will be named according to {@link PLPSourceFile#getName()}.
	 * 
	 * @see
	 * @throws IllegalStateException
	 *             if the specified path is null
	 * @throws IOException
	 *             if there is an issue outputting to the specified path
	 */
	@Override
	public void save()
	{
		// TODO: implement
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	/**
	 * Outputs this project and all its files to the path specified by {@link #getPath()},
	 * as a PLP5 (legacy) project file. This method is intended only for backwards
	 * compatibility purposes, and where possible, {@link #save()} should be used instead.
	 * <p>
	 * As a legacy format, some features may not be supported by the format. Thus, <b>if
	 * this method can be triggered by a user interface, the interface should display a
	 * warning indicating this risk.</b>
	 * 
	 * @throws IllegalStateException
	 *             if the specified path is null
	 * @throws IOException
	 *             if there is an issue outputting to the specified path
	 */
	public void saveLegacy()
	{
		// TODO: implement
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	/**
	 * Outputs this project and all its files, as specified by {@link #save()}, to the
	 * given directory, and updates this project's {@link #pathProperty} if the save was
	 * successful.
	 * <p>
	 * Source files will be exported to a child directory of the project, called "src" and
	 * each .asm file will be named according to {@link PLPSourceFile#getName()}.
	 * <p>
	 * If the specified file already exists, an exception will be raised, and the file
	 * <b>will not</b> be overwritten.
	 * <p>
	 * In order to save over an already existing file, a program must delete the file
	 * <b>before</b> calling this method.
	 * 
	 * @param directoryPath
	 *            The location in the file system to save this project to. This path
	 *            should point to a DIRECTORY.
	 * 
	 * @see #save()
	 * @throws IllegalArgumentException
	 *             if the specified path is null, or points to a file instead of a
	 *             directory.
	 * @throws FileAlreadyExistsException
	 *             if the file already exists. If you still wish to save to this location,
	 *             delete the file before calling this method.
	 * @throws IOException
	 *             if there is an issue outputting to the specified path
	 */
	@Override
	public void saveAs(String filePath)
	{
		// TODO: implement
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	@Override
	public StringProperty getNameProperty()
	{
		return nameProperty;
	}
	
	@Override
	public String getName()
	{
		return nameProperty.get();
	}
	
	@Override
	public void setName(String name)
	{
		nameProperty.set(name);
	}
	
	@Override
	public StringProperty getTypeProperty()
	{
		return typeProperty;
	}
	
	@Override
	public String getType()
	{
		return typeProperty.get();
	}
	
	@Override
	public void setType(String type)
	{
		typeProperty.set(type);
	}
	
	@Override
	public StringProperty getPathProperty()
	{
		return pathProperty;
	}
	
	@Override
	public String getPath()
	{
		return pathProperty.get();
	}
	
	/**
	 * Sets the path pointer of this {@link PLPProject}.
	 * <p>
	 * Note that this method alone does not alter disk contents, i.e. it does not move the
	 * project file, or remove the old project file. For these functionalities, the
	 * application responsible for instantiating {@link PLPProject} should add a change
	 * listener to the {@link #pathProperty} (via {@link #getPathProperty()}), that
	 * achieves the desired effect.
	 * 
	 * @param path
	 */
	@Override
	public void setPath(String path)
	{
		pathProperty.set(path);
	}
	
	@Override
	public int getFileCount()
	{
		return this.size();
	}
	
	/**
	 * Convenience method for accessing the {@link ISARegistry}
	 * 
	 * @return
	 */
	@Override
	public Optional<ISAModule> getISA()
	{
		ISARegistry registry = ISARegistry.getGlobalRegistry();
		String type = getType();
		return registry.lookupByProjectType(type);
	}
}
