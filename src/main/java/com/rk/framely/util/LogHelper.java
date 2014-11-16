package com.rk.framely.util;

import com.rk.framely.Framely;
import com.rk.framely.reference.Reference;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import org.apache.logging.log4j.Level;


public class LogHelper {

    public static void log(Level level, String msg) {
        FMLLog.log(Reference.MOD_NAME, level, msg);
    }

    public static void all(String msg) {
        log(Level.ALL, msg);
    }

    public static void info(String msg) {
        log(Level.INFO, msg);
    }

    public static void debug(String msg) {
        log(Level.DEBUG, msg);
    }

    public static void error(String msg) {
        log(Level.ERROR, msg);
    }

    public static void fatal(String msg) {
        log(Level.FATAL, msg);
    }

    public static void off(String msg) {
        log(Level.OFF, msg);
    }

    public static void trace(String msg) {
        log(Level.TRACE, msg);
    }

    public static void warn(String msg) {
        log(Level.WARN, msg);
    }

    public static void error(Exception e) {
        log(Level.ERROR, e.getClass() + " " + e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            log(Level.ERROR, "\t" + stackTraceElement.toString());
        }
    }
}
