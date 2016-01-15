package edu.asu.plp.tool.prototype.model;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A {@link Project} represents an ordered, observable collection of {@link ProjectFile}s
 * that can be assembled collectively as a single unit.
 * 
 * @author Moore, Zachary
 *
 */
public class Project extends ArrayListProperty<ProjectFile>
{
	/**
	 * Path to this project on in the file system. If the this project exists in memory
	 * only (it has not yet been written to disk), then the value contained by
	 * {@link #pathProperty} should be null.
	 * <p>
	 * Note that {@link #pathProperty} itself should always be non-null.
	 * <p>
	 * Also note that the path should point to a directory, unless the project was loaded
	 * from a legacy source. The actual project FILE will be located in the given
	 * directory, with the name ".project" The files contained by this project will be
	 * located in a subdirectory named "src"
	 * <p>
	 * In the case of a legacy file, the path will point to the project file directly, and
	 * no src directory will exist.
	 */
	private StringProperty pathProperty;
	private StringProperty nameProperty;
	
	/**
	 * Loads a {@link Project} from the given project file. This method auto-detects the
	 * project version, and is therefore capable of loading both PLP6 and legacy (PLP5 and
	 * prior) projects.
	 * 
	 * @param filePath
	 *            Path to the specified file; may be relative or absolute
	 * @return A {@link Project} representative of the information stored in the given
	 *         file.
	 */
	public static Project load(String filePath)
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
	 * @returnA {@link Project} representative of the information stored in the given
	 *          file.
	 */
	private static Project loadLegacy(String filePath)
	{
		// TODO: implement
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	public Project()
	{
		pathProperty = new SimpleStringProperty();
		nameProperty = new SimpleStringProperty();
	}
	
	public Project(String name)
	{
		this();
		nameProperty.set(name);
	}
	
	/**
	 * Outputs this project and all its files to the directory specified by
	 * {@link #getPath()}, as a PLP6 project file with a src directory of .asm files.
	 * <p>
	 * The project information specified by this object will be saved in a file within the
	 * specified directory named ".project"
	 * <p>
	 * The files contained by this project will be saved to a subdirectory named "src"
	 * <p>
	 * Source files will be exported to a child directory of the project, called "src" and
	 * each .asm file will be named according to {@link ProjectFile#getName()}.
	 * 
	 * @see
	 * @throws IllegalStateException
	 *             if the specified path is null
	 * @throws IOException
	 *             if there is an issue outputting to the specified path
	 */
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
	 * each .asm file will be named according to {@link ProjectFile#getName()}.
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
	public void saveAs(String filePath)
	{
		// TODO: implement
		throw new UnsupportedOperationException("Not Yet Implemented");
	}
	
	public StringProperty getNameProperty()
	{
		return nameProperty;
	}
	
	public String getName()
	{
		return nameProperty.get();
	}
	
	public void setName(String name)
	{
		nameProperty.set(name);
	}
	
	public StringProperty getPathProperty()
	{
		return pathProperty;
	}
	
	public String getPath()
	{
		return pathProperty.get();
	}
	
	/**
	 * Sets the path pointer of this {@link Project}.
	 * <p>
	 * Note that this method alone does not alter disk contents, i.e. it does not move the
	 * project file, or remove the old project file. For these functionalities, the
	 * application responsible for instantiating {@link Project} should add a change
	 * listener to the {@link #pathProperty} (via {@link #getPathProperty()}), that
	 * achieves the desired effect.
	 * 
	 * @param path
	 */
	public void setPath(String path)
	{
		pathProperty.set(path);
	}
	
	public int getFileCount()
	{
		return this.size();
	}
}
