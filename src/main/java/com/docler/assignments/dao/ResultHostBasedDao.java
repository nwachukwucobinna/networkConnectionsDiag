package com.docler.assignments.dao;

import com.docler.assignments.util.DatabaseUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultHostBasedDao {

    private final String table;

    public ResultHostBasedDao(String table) {
        this.table = table;
    }

    public void insert(String host, String result) throws SQLException {
        DatabaseUtil.STATEMENT.execute(new StringBuilder(
                "INSERT INTO ").append(table).append("(host, result) VALUES('")
                .append(host).append("', '").append(result)
                .append("');").toString());
    }

    public String getLatestResult(String host) throws SQLException {
        final ResultSet resultSet = DatabaseUtil.STATEMENT.executeQuery(
                new StringBuilder("SELECT result FROM ")
                        .append(table)
                        .append(" WHERE id IN ")
                        .append("(SELECT id FROM ")
                        .append(table)
                        .append(" WHERE occurred = ")
                        .append("(SELECT MAX(occurred) FROM ")
                        .append(table)
                        .append(" WHERE host = '")
                        .append(host)
                        .append("')) ")
                        .append("ORDER BY id DESC ")
                        .append("LIMIT 1").toString());
        resultSet.next();
        return resultSet.getString(1);
    }
}
