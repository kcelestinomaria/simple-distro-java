package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection manages the connection to the PostgreSQL database.
 */
public class DBConnection {

    // Database connection parameters (Move to environment variables for security)
    private static final String URL = "jdbc:postgresql://localhost:5432/university1";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123";

    /**
     * Establishes a connection to the database.
     * @return Connection object if successful, otherwise null.
     */
    public Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver"); // Load the PostgreSQL driver
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        return null;
    }
}
