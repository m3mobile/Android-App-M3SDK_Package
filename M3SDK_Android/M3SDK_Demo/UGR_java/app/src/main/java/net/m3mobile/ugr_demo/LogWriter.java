package net.m3mobile.ugr_demo;

import android.util.Log;

public class LogWriter {

    private static final String TAG = "UGR_java";
    static int logLevel = 0;

    /**
     * Log Level Error
     **/
    public static void e(String message) {
        if (logLevel <= 4)
            Log.e(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Warning
     **/
    public static void w(String message) {
        if (logLevel <= 3)
            Log.w(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Information
     **/
    public static void i(String message) {
        if (logLevel <= 2)
            Log.i(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Debug
     **/
    public static void d(String message) {
        if (logLevel <= 1)
            Log.d(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Verbose
     **/
    public static void v(String message) {
        if (logLevel <= 0)
            Log.v(TAG, buildLogMsg(message));
    }

    private static String buildLogMsg(String message) {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        return "[" + stackTraceElement.getFileName().replace(".java", "") + "::" + stackTraceElement.getMethodName() + "]" + message;
    }
}

