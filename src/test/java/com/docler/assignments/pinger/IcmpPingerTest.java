package com.docler.assignments.pinger;

import com.docler.assignments.dao.IcmpPingsDao;
import com.docler.assignments.util.DatabaseUtil;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.*;

public class IcmpPingerTest {

    @Test
    public void run() throws SQLException {
        final String host = System.getenv("SIMPLE_PING_APP_HOSTS");
        final StringBuilder pingCommandBuilder = new StringBuilder(
                System.getenv("SIMPLE_PING_APP_PING_CMD"));
        if(System.getProperty("os.name").toLowerCase().contains("linux")) {
            pingCommandBuilder.append(" -c 1");
        }
        final int indexOfTOption = pingCommandBuilder.indexOf("-t");
        if(indexOfTOption > -1) {
            int indexOfCharToRemove = indexOfTOption+2;
            while(pingCommandBuilder.charAt(indexOfCharToRemove) == ' ') {
                indexOfCharToRemove++;
            }
            if(Character.isDigit(pingCommandBuilder.charAt(indexOfCharToRemove))) {
                pingCommandBuilder.replace(indexOfTOption, indexOfCharToRemove+1, "");
            }
        }
        final IcmpPinger icmpPinger = new IcmpPinger(host, pingCommandBuilder.toString());
        DatabaseUtil.setUrl("jdbc:h2:mem:simple_ping_application_test");
        DatabaseUtil.initialize();
        icmpPinger.run();
        assertFalse(new IcmpPingsDao().getLatestResult(host).isEmpty());
    }
}
