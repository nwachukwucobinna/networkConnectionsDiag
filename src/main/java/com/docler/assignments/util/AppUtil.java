package com.docler.assignments.util;

public class AppUtil {

    private static String prefixWithCurrentWorkingDir(String string) {
        return new StringBuilder(System.getProperty("user.dir"))
                .append(string).toString();
    }

    public static String generateStatementForRunningDDLScripts(
            String fileName) {
        return new StringBuilder("RUNSCRIPT FROM ").append("'")
                .append(prefixWithCurrentWorkingDir(
                        "/db/script/"))
                .append(fileName)
                .append("'").toString();
    }

    public static boolean isDelayExpired(String delay, long lastOccurrence) {
        return delay == null || lastOccurrence == 0 ||
                System.currentTimeMillis() - (1000 * Long.valueOf(delay)) >= lastOccurrence;
    }
}
