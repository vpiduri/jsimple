package jsimple.logging.stdimpl;

import jsimple.io.StdIO;
import jsimple.logging.ILoggerFactory;
import jsimple.logging.Level;
import jsimple.logging.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Bret Johnson
 * @since 4/8/13 12:57 AM
 */
public class StdLoggerFactory implements ILoggerFactory {
    private HashMap<String, StdLogger> loggers = new HashMap<String, StdLogger>();
    private Level defaultLevel = Level.DEBUG;
    private List<Appender> defaultAppenders = new ArrayList<Appender>();

    public StdLoggerFactory() {
        defaultAppenders.add(new WriterAppender(StdIO.stdout));
    }

    /**
     * Return an appropriate {@link jsimple.logging.Logger} instance as specified by the <code>name</code> parameter.
     * <p/>
     * If the name parameter is equal to {@link jsimple.logging.Logger#ROOT_LOGGER_NAME}, that is the string value
     * "ROOT" (case insensitive), then the root logger of the underlying logging system is returned.
     * <p/>
     * Null-valued name arguments are considered invalid.
     * <p/>
     * Certain extremely simple logging systems, e.g. NOP, may always return the same logger instance regardless of the
     * requested name.
     *
     * @param name the name of the Logger to return
     * @return a Logger instance
     */
    public Logger getLogger(String name) {
        synchronized (this) {
            StdLogger logger = loggers.get(name);
            if (logger == null) {
                logger = new StdLogger(this, name);
                loggers.put(name, logger);
            }

            return logger;
        }
    }

    public Level getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(Level defaultLevel) {
        synchronized (this) {
            this.defaultLevel = defaultLevel;

            for (StdLogger logger : loggers.values())
                logger.updateEffectiveLevel();
        }
    }

    /**
     * Return the current default list of appenders that are configured.  The returned list should NOT be modified; it
     * must be treated as immutable.
     *
     * @return configured appenders
     */
    public List<Appender> getDefaultAppenders() {
        return defaultAppenders;
    }

    public void setDefaultAppenders(List<Appender> defaultAppenders) {
        synchronized (this) {
            this.defaultAppenders = defaultAppenders;

            for (StdLogger logger : loggers.values())
                logger.updateEffectiveAppenders();
        }
    }
}
