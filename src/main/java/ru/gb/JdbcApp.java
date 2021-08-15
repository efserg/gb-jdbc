package ru.gb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcApp {
    private Connection connection;

    public static void main(String[] args) {
        final JdbcApp jdbcApp = new JdbcApp();
        try {
            jdbcApp.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jdbcApp.disconnect();
        }
    }

    private void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
    }
}
