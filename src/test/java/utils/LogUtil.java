package utils;

import org.apache.log4j.Logger;
import org.testng.Reporter;

public final class LogUtil {
    private static final Logger log = Logger.getLogger("company");

    private static final String DEBUG = "debug";
    private static final String INFO = "info";
    private static final String WARN = "warn";
    private static final String ERROR = "error";


    /**
     * Logs INFO level message to root logger
     *
     * @param message
     */
    public static void info(String message) {
        log.info(message);
        if (log.isInfoEnabled()) {
            Reporter.log("&nbsp;&nbsp;&nbsp;&nbsp;" + message + "<br/>");
        }
    }

    /**
     * Logs INFO level message to root logger
     *
     * @param message
     */


    /**
     * Logs DEBUG level message to root logger
     *
     * @param message
     */
    public static void debug(String message) {
        log.debug(message);

        if (log.isDebugEnabled()) {
            Reporter.log("&nbsp;&nbsp;&nbsp;&nbsp;" + message + "<br/>");
        }
    }

    /**
     * Logs WARN level message to root logger
     *
     * @param message
     */
    public static void warn(String message) {
        log.warn(message);
        Reporter.log("&nbsp;&nbsp;&nbsp;&nbsp;" + message + "<br/>");
    }

    /**
     * Logs ERROR level message to root logger
     *
     * @param message
     */
    public static void error(String message) {
        log.error(message);
        Reporter.log("&nbsp;&nbsp;&nbsp;&nbsp;" + message + "<br/>");
    }

    public static void error(String message, Throwable e) {
        error(message);
        for (StackTraceElement element : e.getStackTrace()) {
            error(element.toString());
        }
    }
}

