package jsimple.io;

import org.jetbrains.annotations.Nullable;

/**
 * A Path is an abstraction over file system-like data structures.  A Directory path can contain other Directories and
 * Files.  The caller can get an input stream / output stream to read/write a File.
 *
 * @author Bret Johnson
 * @since 11/22/12 12:14 AM
 */
public abstract class Path {
    /**
     * Get the name of this file/directory--the last component of the path.
     *
     * @return name of this file/directory
     */
    public abstract String getName();

    /**
     * Get the parent directory of this path--all the components except for the last one.  The parent is null if this
     * path is off the root.
     *
     * @return parent directory or null
     */
    public abstract @Nullable Directory getParent();

    /**
     * Get the extension (the text after the period) from the specified file/directory name.  The period itself isn't
     * returned, just the text after.  If there's no extension, the empty string is returned. s
     *
     * @param name file/directory name
     * @return extension part of the name (minus the period) or the empty string if there's no extension
     */
    public static String getNameExtension(String name) {
        int periodIndex = name.lastIndexOf('.');

        if (periodIndex == -1)
            return "";
        else return name.substring(periodIndex + 1);
    }
}