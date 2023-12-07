package com.cifre.sap.su.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Static class to use log4j2 logger.
 */
public class LoggerWriter {
    private static final Logger logger = LogManager.getLogger("Name");

    public static void error(String msg){
        logger.error(msg);
    }

    public static void fatal(String msg){
        logger.fatal(msg);
    }

    public static void warn(String msg){
        logger.warn(msg);
    }

    public static void info(String msg){
        logger.info(msg);
    }
}