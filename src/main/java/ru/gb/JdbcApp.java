package ru.gb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class JdbcApp {
    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:javadb.db")) {
            createTable(connection);
            insert(connection, "Bob", 90);
            insert(connection, "Smith", 99);
            insert(connection, "John", 75);
            select(connection);
            // sql injection: SELECT * FROM students WHERE name = 'John' union select 1, sql, 1 from sql_master --'"
            selectByName(connection, "John' union select 1, sql, 1 from sqlite_master --");
            dropById(connection, 1);
            bulkInsert(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void dropById(Connection connection, int id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM students WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }

    }

    private static void selectByName(Connection connection, String name) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM students WHERE name = ?")) {
            statement.setString(1, name);
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nameDB = rs.getString("name");
                int score = rs.getInt("score");
                System.out.printf("%d - %s - %d\n", id, nameDB, score);
            }
        }
    }

    private static void select(Connection connection) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("SELECT * FROM students")) {
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int score = rs.getInt("score");
                System.out.printf("%d - %s - %d\n", id, name, score);
            }
        }
    }

    private static void bulkInsert(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO students (name, score) VALUES (?, ?)")) {
            for (int i = 0; i < 10; i++) {
                statement.setString(1, "name" + i);
                statement.setInt(2, new Random().nextInt(100));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static void insert(Connection connection, String name, int score) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO students (name, score) VALUES (?, ?)")) {
            statement.setString(1, name);
            statement.setInt(2, score);
            statement.executeUpdate();
        }
    }


    private static void createTable(Connection connection) throws SQLException {
        try (final PreparedStatement statement = connection.prepareStatement("" +
                " CREATE TABLE IF NOT EXISTS students (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT, " +
                "    score INTEGER" +
                ")")) {
            statement.executeUpdate();
        }
    }
}
