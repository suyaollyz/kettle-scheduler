package cn.kettle.scheduler.commons.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LoggerFactory {

    private static final ConcurrentMap<String, Logger> LOGGERS = new ConcurrentHashMap<String, Logger>();

    public static Logger getLogger() {
        return new Logger(LoggerFactory.class);
    }

    public static Logger getLogger(String className) {
        Logger logger = LOGGERS.get(className);
        if (logger == null) {
            LOGGERS.putIfAbsent(className, new Logger(className));
            logger = LOGGERS.get(className);
        }
        return logger;
    }

    public static Logger getLogger(Class clazz) {
        return getLogger(clazz.getName());
    }

}
