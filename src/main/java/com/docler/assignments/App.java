package com.docler.assignments;

import com.docler.assignments.pinger.IcmpPinger;
import com.docler.assignments.pinger.TcpipPinger;
import com.docler.assignments.reporter.Reporter;
import com.docler.assignments.tracer.TracerouteTracer;
import com.docler.assignments.util.DatabaseUtil;

import java.io.IOException;
import java.sql.SQLException;

public class App {

    private static final String HOSTS = System.getenv("SIMPLE_PING_APP_HOSTS");
    private static final String PING_COMMAND = System.getenv("SIMPLE_PING_APP_PING_CMD");
    private static final String TRACERT_COMMAND = System.getenv("SIMPLE_PING_APP_TRACERT_CMD");

    public static void main(String[] args) {
        checkForNullOrBlankEssentialEnvVars();
        try {
            DatabaseUtil.initialize();
            Reporter.initialize();
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
            return;
        }
        for (String host : HOSTS.split(",")) {
            final String trimmedHost = host.trim();
            new Thread(new IcmpPinger(trimmedHost, PING_COMMAND.trim())).start();
            new Thread(new TcpipPinger(trimmedHost)).start();
            new Thread(new TracerouteTracer(trimmedHost, TRACERT_COMMAND.trim())).start();
        }
    }

    private static void checkForNullOrBlankEssentialEnvVars() {
        if (HOSTS == null) {
            throw new NullPointerException("Hosts environment variable is null");
        } else if (HOSTS.trim().isEmpty()) {
            throw new RuntimeException("Hosts environment variable is blank");
        }
        if (PING_COMMAND == null) {
            throw new NullPointerException("Ping command environment variable is null");
        } else if (PING_COMMAND.trim().isEmpty()) {
            throw new RuntimeException("Ping command environment variable is blank");
        }
        if (TRACERT_COMMAND == null) {
            throw new NullPointerException("Trace route command environment variable is null");
        } else if (TRACERT_COMMAND.trim().isEmpty()) {
            throw new RuntimeException("Trace route command environment variable is blank");
        }
    }
}
