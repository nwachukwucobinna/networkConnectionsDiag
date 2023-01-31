package com.docler.assignments.tracer;

import com.docler.assignments.dao.TraceroutesDao;
import com.docler.assignments.util.AppUtil;
import com.docler.assignments.util.SystemUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TracerouteTracer implements Runnable {

    private static final String DELAY = System.getenv("SIMPLE_PING_APP_TRACERT_DELAY");
    private final TraceroutesDao traceroutesDao = new TraceroutesDao();
    private final String host;
    private final String traceRouteCommand;
    private long timeOfPingOccurrence = 0;

    public TracerouteTracer(String host, String traceRouteCommand) {
        this.host = host;
        this.traceRouteCommand = traceRouteCommand;
    }

    private void traceHost() {

        final List<String> traceRouteCommandAsList = new ArrayList<>(
                Arrays.asList(traceRouteCommand.split(" ")));
        traceRouteCommandAsList.add(host);

        try {
            while (true) {
                if (AppUtil.isDelayExpired(DELAY, timeOfPingOccurrence)) {

                    final Process process = SystemUtil.executeCommand(traceRouteCommandAsList);

                    final BufferedReader inputStreamBufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    final StringBuilder inputStreamStringBuilder = new StringBuilder();

                    String readInputStreamBuffer;

                    while ((readInputStreamBuffer = inputStreamBufferedReader.readLine()) != null) {
                        inputStreamStringBuilder.append(readInputStreamBuffer);
                    }
                    inputStreamBufferedReader.close();
                    traceroutesDao.insert(host, inputStreamStringBuilder.toString());
                    timeOfPingOccurrence = System.currentTimeMillis();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            traceHost();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void run() {
        traceHost();
    }
}
