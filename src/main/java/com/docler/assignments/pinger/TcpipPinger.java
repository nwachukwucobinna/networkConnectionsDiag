package com.docler.assignments.pinger;

import com.docler.assignments.dao.TcpipPingsDao;
import com.docler.assignments.reporter.Reporter;
import com.docler.assignments.util.AppUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.SQLException;

public class TcpipPinger implements Runnable {

    private static final String DELAY = System.getenv("SIMPLE_PING_APP_TCPIP_DELAY");
    private static final String TIMEOUT = System.getenv("SIMPLE_PING_APP_IMCP_TIMEOUT");
    private static final String RESPONSE_TIME_LIMIT = System.getenv("SIMPLE_PING_APP_TCPIP_RESPONSE_TIME_LIMIT");
    private final TcpipPingsDao tcpipPingsDao = new TcpipPingsDao();
    private final String host;
    private long timeOfPingOccurrence = 0;

    public TcpipPinger(String host) {
        this.host = host;
    }

    private void pingHost() {
        try {
            final SocketAddress socketAddress = new InetSocketAddress(host, 80);
            while (true) {
                final Socket socket = new Socket();
                if (TIMEOUT != null) {
                    socket.connect(socketAddress, Integer.valueOf(TIMEOUT));
                } else {
                    socket.connect(socketAddress);
                }
                if (AppUtil.isDelayExpired(DELAY, timeOfPingOccurrence)) {
                    final PrintStream printOutputStream = new PrintStream(socket.getOutputStream());
                    final BufferedReader inputStreamBufferedReader =
                            new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    final StringBuilder inputStreamStringBuilder = new StringBuilder();
                    final long startTimeOfWritingRequest = System.currentTimeMillis();
                    printOutputStream.println(
                            new StringBuilder("GET ").append("/").append(" HTTP/1.1"));
                    printOutputStream.println(new StringBuilder("Host: ").append(host));
                    printOutputStream.println();
                    String readInputStreamBuffer = inputStreamBufferedReader.readLine();
                    while (readInputStreamBuffer != null && !readInputStreamBuffer.isEmpty()) {
                        inputStreamStringBuilder.append(readInputStreamBuffer).append(" ");
                        readInputStreamBuffer = inputStreamBufferedReader.readLine();
                    }
                    final long responseTime = System.currentTimeMillis() - startTimeOfWritingRequest;
                    inputStreamBufferedReader.close();
                    printOutputStream.close();
                    socket.close();
                    tcpipPingsDao.insert(host,
                            inputStreamStringBuilder.append(" Elapsed-Time: ")
                                    .append(responseTime)
                                    .append("ms").toString());
                    if (responseTime > Long.valueOf(RESPONSE_TIME_LIMIT)) {
                        Reporter.report(host);
                    }
                    timeOfPingOccurrence = System.currentTimeMillis();
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
            Reporter.report(host);
            pingHost();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void run() {
        pingHost();
    }
}
