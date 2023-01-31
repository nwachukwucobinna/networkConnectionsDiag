package com.docler.assignments.pinger;

import com.docler.assignments.dao.IcmpPingsDao;
import com.docler.assignments.reporter.Reporter;
import com.docler.assignments.util.AppUtil;
import com.docler.assignments.util.SystemUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IcmpPinger implements Runnable {


    private static final String DELAY = System.getenv("SIMPLE_PING_APP_IMCP_DELAY");
    private final IcmpPingsDao icmpPingsDao = new IcmpPingsDao();
    private final String host;
    private final String pingCommand;
    private long timeOfPingOccurrence = 0;

    public IcmpPinger(String host, String pingCommand) {
        this.host = host;
        this.pingCommand = pingCommand;
    }

    private void pingHost() {

        final List<String> pingCommandAsList = new ArrayList<>(
                Arrays.asList(pingCommand.split(" ")));

        pingCommandAsList.add(host);

        try {
            final Process process = SystemUtil.executeCommand(pingCommandAsList);

            final BufferedReader inputStreamBufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            final BufferedReader errorStreamBufferedReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()));

            String readStream;

            while ((readStream = inputStreamBufferedReader.readLine()) != null) {
                if (AppUtil.isDelayExpired(DELAY, timeOfPingOccurrence)) {
                    icmpPingsDao.insert(host, readStream);
                    timeOfPingOccurrence = System.currentTimeMillis();
                }
            }

            while ((readStream = errorStreamBufferedReader.readLine()) != null) {
                if (AppUtil.isDelayExpired(DELAY, timeOfPingOccurrence)) {
                    icmpPingsDao.insert(host, readStream);
                    Reporter.report(host);
                    timeOfPingOccurrence = System.currentTimeMillis();
                    pingHost();
                }
            }
        } catch (IOException | SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run() {
        pingHost();
    }
}
