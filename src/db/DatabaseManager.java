package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://localhost/password_manager";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "92491043";

    public static void createTable() {
        try (var connection = getConnection()) {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS passwords (" +
                    "id SERIAL PRIMARY KEY, " +
                    "service VARCHAR(255) NOT NULL, " +
                    "username VARCHAR(255) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL)";
            Statement statement = connection.createStatement();
            statement.executeUpdate(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
