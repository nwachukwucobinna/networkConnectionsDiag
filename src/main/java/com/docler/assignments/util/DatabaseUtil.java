package com.docler.assignments.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    private static final String ORIGINAL_URL = "jdbc:h2:mem:simple_ping_application";
    private static String URL = ORIGINAL_URL;
    public static Statement STATEMENT;

    public static void initialize() throws SQLException {
            final Connection connection = DriverManager.getConnection(
                    new StringBuilder(URL)
                            .append(";INIT=")
                            .append(AppUtil.generateStatementForRunningDDLScripts(
                                    "icmp_pings_ddl.sql"))
                            .append("\\;")
                            .append(AppUtil.generateStatementForRunningDDLScripts(
                                    "tcpip_pings_ddl.sql"))
                            .append("\\;")
                            .append(AppUtil.generateStatementForRunningDDLScripts(
                                    "traceroutes_ddl.sql"))
                            .append(";DB_CLOSE_ON_EXIT=FALSE").toString());
            STATEMENT = connection.createStatement();
    }

    public static void setUrl(String url) {
        URL = url;
    }

    public static void reset() {
        URL = ORIGINAL_URL;
    }
}
