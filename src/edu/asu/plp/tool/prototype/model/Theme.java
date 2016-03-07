package edu.asu.plp.tool.prototype.model;

import java.net.MalformedURLException;

/**
 * Interface to extend if needing to convert some theming scheme to CSS.
 * <p>
 * Standard {@link CSSTheme} is used if all themeing is done in base CSS.
 * <p>
 * However, say you wanted to make all the theming with LESS and compile that to CSS at runtime. Create a LESSTheme that
 * implements this class and then you would accept the theme directory. Load in all the less files and compile them into
 * an app.css file.
 * <p>
 * {@link Theme#getPath()} should return the theme file path.
 * <p>
 * {@link Theme#getName()} should return the name of the parent directory containing the theme file and should
 * represent the themes name.
 *
 * @author Nesbitt, Morgan Created on 2/23/2016.
 */
public interface Theme
{
	String getPath() throws MalformedURLException;

	String getName ();
}
