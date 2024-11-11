package org.automation.apiTest.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerUtil {

    private static final ThreadLocal<Logger> threadLocalLogger = ThreadLocal.withInitial(() -> LogManager.getLogger(LoggerUtil.class));

    public static Logger getLog() {
        return threadLocalLogger.get();
    }

    public static void removeLog() {
        threadLocalLogger.remove();
    }

    public static void logInfo(String message) {
        threadLocalLogger.get().info(message);
    }

    public static void logWarn(String message) {
        threadLocalLogger.get().warn(message);
    }

    public static void logError(String message) {
        threadLocalLogger.get().error(message);
    }

    public static void logException(Exception message) {
        threadLocalLogger.get().error(message);
    }
}
