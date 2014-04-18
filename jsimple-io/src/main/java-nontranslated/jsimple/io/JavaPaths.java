package jsimple.io;

import org.jetbrains.annotations.Nullable;

/**
 * @author Bret Johnson
 * @since 3/15/13 10:52 PM
 */
public class JavaPaths extends Paths {
    /**
     * This method is defined to return the directory where the application should store its private data.  In Java,
     * there's no standard way to do exactly that so we return user.dir, which is the working directory (the current
     * directory) when the process was started.
     *
     * @return the directory where the application should store its private data
     */
    @Override public Directory getApplicationDataDirectory() {
        @Nullable String workingDirectory = System.getProperty("user.dir");
        if (workingDirectory == null)
            throw new IOException("user.dir is unset for some reason; working directory not specified by the JVM");

        return new FileSystemDirectory(workingDirectory);
    }

    @Override public Directory getFileSystemDirectory(String directoryPathString) {
        return new FileSystemDirectory(directoryPathString);
    }
}
