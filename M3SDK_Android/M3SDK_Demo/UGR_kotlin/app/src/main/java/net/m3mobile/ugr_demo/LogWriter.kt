package net.m3mobile.ugr_demo

import android.util.Log

object LogWriter {
    private const val TAG = "UGR_kotlin"
    var logLevel = 0

    /**
     * Log Level Error
     */
    fun e(message: String) {
        if (logLevel <= 4) Log.e(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Warning
     */
    fun w(message: String) {
        if (logLevel <= 3) Log.w(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Information
     */
    fun i(message: String) {
        if (logLevel <= 2) Log.i(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Debug
     */
    fun d(message: String) {
        if (logLevel <= 1) Log.d(TAG, buildLogMsg(message))
    }

    /**
     * Log Level Verbose
     */
    fun v(message: String) {
        if (logLevel <= 0) Log.v(TAG, buildLogMsg(message))
    }

    private fun buildLogMsg(message: String): String {
        val stackTraceElement = Thread.currentThread().stackTrace[4]
        return "[" + stackTraceElement.fileName.replace(
            ".java",
            ""
        ) + "::" + stackTraceElement.methodName + "]" + message
    }
}