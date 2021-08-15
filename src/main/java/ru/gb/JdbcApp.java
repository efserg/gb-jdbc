package ru.gb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcApp {
    private Connection connection;
    private Statement statement;

    public static void main(String[] args) {
        final JdbcApp jdbcApp = new JdbcApp();
        try {
            jdbcApp.connect();
            jdbcApp.createTable();
            jdbcApp.insert("Bob", 95);
            jdbcApp.insert("John", 80);
            jdbcApp.update(1, 100);
            jdbcApp.select(1);
            jdbcApp.select(2);
            jdbcApp.selectByName("bob' union SELECT 1, sql, 1 FROM sqlite_master --");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            jdbcApp.disconnect();
        }
    }

    private void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:javadb.db");
        statement = connection.createStatement();
    }

    public void createTable() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS students (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "score INTEGER" +
                ");");
    }

    public void dropTable() throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXISTS students");
    }

    public void insert(final String name, final Integer score) throws SQLException {
//        statement.executeUpdate("INSERT INTO students(name, score) VALUES (" + name + ", " + score + ")"); // sql injection!!!
        try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO students(name, score) VALUES (?, ?)")) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, score);
            preparedStatement.executeUpdate();
        }
    }

    public void update(final Integer id, final Integer score) throws SQLException {
//        statement.executeUpdate("UPDATE students SET score = " + score + " where id = " + id); // sql injection!!!
        try (final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE students SET score = ? where id = ?")) {
            preparedStatement.setInt(1, score);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    public void select(Integer id) throws SQLException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM students WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            final ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.printf("%d - %s - %d\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
            }
        }
    }

    public void selectByName(String name) throws SQLException {
        try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM students WHERE name = ?")) {
            preparedStatement.setString(1, name);
            final ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.printf("%d - %s - %d\n", resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3));
            }
        }
    }
}
