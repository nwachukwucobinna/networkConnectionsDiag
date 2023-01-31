package com.docler.assignments.reporter;

import com.docler.assignments.dao.IcmpPingsDao;
import com.docler.assignments.dao.TcpipPingsDao;
import com.docler.assignments.dao.TraceroutesDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Reporter {

    private static final String REPORT_URL = System.getenv("SIMPLE_PING_APP_REPORT_URL");
    private static final IcmpPingsDao ICMP_PINGS_DAO = new IcmpPingsDao();
    private static final TcpipPingsDao TCPIP_PINGS_DAO = new TcpipPingsDao();
    private static final TraceroutesDao TRACEROUTES_DAO = new TraceroutesDao();
    private static final Logger LOGGER = Logger.getLogger(Reporter.class.getName());
    private static final String LOG_FILE = System.getenv("SIMPLE_PING_APP_REPORT_LOG_FILE");

    public static void initialize() throws IOException {
        final Path logPath = Paths.get(LOG_FILE);
        Files.createDirectories(logPath.getParent());
        final FileHandler fileHandler = new FileHandler(logPath.toString());
        LOGGER.addHandler(fileHandler);
    }

    public static void report(String host) {
        try {
            final String latestIcmpPing = ICMP_PINGS_DAO.getLatestResult(host);
            final String latestTcpipPing = TCPIP_PINGS_DAO.getLatestResult(host);
            final String latestTraceroute = TRACEROUTES_DAO.getLatestResult(host);
            final URL url = new URL(REPORT_URL);
            final HttpURLConnection httpURLConnection =
                    (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);
            final String jsonInputString = new StringBuilder("{\"host\": \"")
                    .append(host)
                    .append("\", \"icmp_ping\": \"")
                    .append(latestIcmpPing)
                    .append("\", \"tcp_ping\": \"")
                    .append(latestTcpipPing)
                    .append("\", \"trace\": \"")
                    .append(latestTraceroute).append("\"}").toString();
            try (OutputStream outputStream = httpURLConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                outputStream.write(input, 0, input.length);
            }
            try (BufferedReader readInputStream = new BufferedReader(
                    new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8))) {
                final StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = readInputStream.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            LOGGER.warning(jsonInputString);
        } catch (SQLException | IOException exception) {
            exception.printStackTrace();
        }
    }
}
