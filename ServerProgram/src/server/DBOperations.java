package server;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DBOperations handles CRUD operations for the STUDENTS table.
 */
public class DBOperations {
    private final DBConnection dbc = new DBConnection();

    public DBOperations(){
        createUsersTableIfNotExists();
        addLoginTrackingColumns();
    }
    
    /**
     * Create USERS table if it doesn't already exist.
     */
    private void createUsersTableIfNotExists() {
        String query = "CREATE TABLE IF NOT EXISTS USERS (" +
                "user_id SERIAL PRIMARY KEY, " +
                "username VARCHAR(50) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL" +
                ")";
        try (Connection conn = dbc.getConnection();
             Statement stmt = conn.createStatement()) {

            if (conn == null) {
                System.out.println("Connection not established for USERS table check.");
                return;
            }

            stmt.executeUpdate(query);
            System.out.println("USERS table is ready.");
        } catch (SQLException e) {
            System.err.println("Failed to create USERS table: " + e.getMessage());
        }
    }
    
    
    private void addLoginTrackingColumns() {
    String alterSQL = """
        ALTER TABLE USERS 
        ADD COLUMN IF NOT EXISTS login_attempts INT DEFAULT 0,
        ADD COLUMN IF NOT EXISTS last_login TIMESTAMP;
    """;
    try (Connection conn = dbc.getConnection();
         Statement stmt = conn.createStatement()) {
        stmt.executeUpdate(alterSQL);
    } catch (SQLException e) {
        System.err.println("Could not update USERS table: " + e.getMessage());
    }
}

    
    /**
     * Register a new user with hashed password.
     */
    public String registerUser(String username, String plainPassword) {
        String query = "INSERT INTO USERS (username, password) VALUES (?, ?)";

        // Hash the password using BCrypt
        String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        try (Connection conn = dbc.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (conn == null) return "Connection not established.";

            pst.setString(1, username);
            pst.setString(2, hashedPassword);

            int rowsAffected = pst.executeUpdate();
            return (rowsAffected > 0) ? "User registered successfully." : "User registration failed.";

        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate key")) {
                return "Username already taken.";
            }
            return "Registration failed: " + e.getMessage();
        }
    }
    
    /**
     * Validate user login with hashed password check.
     */
    public String loginUser(String username, String plainPassword) {
        String fetchQuery = "SELECT password, login_attempts FROM USERS WHERE username = ?";
        String updateSuccessQuery = "UPDATE USERS SET login_attempts = 0, last_login = NOW() WHERE username = ?";
        String updateFailQuery = "UPDATE USERS SET login_attempts = login_attempts + 1 WHERE username = ?";

        
        
        try (Connection conn = dbc.getConnection();
             PreparedStatement fetchPst = conn.prepareStatement(fetchQuery)) {

            if (conn == null) return "Connection not established.";

            fetchPst.setString(1, username);
            ResultSet rs = fetchPst.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                int attempts = rs.getInt("login_attempts"); // login attempts
                
                if (BCrypt.checkpw(plainPassword, storedHash)) {
                    try (PreparedStatement updateSuccessPst = conn.prepareStatement(updateSuccessQuery)) {
                    updateSuccessPst.setString(1, username);
                    updateSuccessPst.executeUpdate();
                    }
                    return "Login successful.";
                } else {
                    try (PreparedStatement updateFailPst = conn.prepareStatement(updateFailQuery)) {
                    updateFailPst.setString(1, username);
                    updateFailPst.executeUpdate();
                    }
                    return "Incorrect password. Attempt " + (attempts + 1);
                }
            } else {
                return "User not found.";
            }

        } catch (SQLException e) {
            return "Login failed: " + e.getMessage();
        }
    }
    
    /**
     * Inserts a new student record into the database.
     */
    public String insert(String studentId, String studentName, String studentCourse) {
        String query = "INSERT INTO STUDENTS (student_id, student_name, student_course) VALUES (?, ?, ?)";
        try (Connection conn = dbc.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (conn == null) return "Connection not established.";

            pst.setString(1, studentId);
            pst.setString(2, studentName);
            pst.setString(3, studentCourse);

            int rowsAffected = pst.executeUpdate();
            return (rowsAffected > 0) ? "Record inserted successfully." : "Insert failed.";

        } catch (SQLException e) {
            return "Operation failed: " + e.getMessage();
        }
    }
    
    /**
     * Register a new user
     */

    /**
     * Retrieves a student record by ID.
     */
    public String select(String studentId) {
        String query = "SELECT * FROM STUDENTS WHERE student_id = ?";
        StringBuilder result = new StringBuilder();

        try (Connection conn = dbc.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (conn == null) return "Connection not established.";

            pst.setString(1, studentId);
            ResultSet rs = pst.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                result.append("ID: ").append(rs.getString("student_id"))
                      .append("\nName: ").append(rs.getString("student_name"))
                      .append("\nCourse: ").append(rs.getString("student_course"))
                      .append("\n");
            }

            return found ? result.toString() : "No student found with ID " + studentId;

        } catch (SQLException e) {
            return "Operation failed: " + e.getMessage();
        }
    }

    /**
     * Updates a student's name and course.
     */
    public String update(String studentId, String newStudentName, String newStudentCourse) {
        String query = "UPDATE STUDENTS SET student_name = ?, student_course = ? WHERE student_id = ?";
        try (Connection conn = dbc.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (conn == null) return "Connection not established.";

            pst.setString(1, newStudentName);
            pst.setString(2, newStudentCourse);
            pst.setString(3, studentId);

            int rowsAffected = pst.executeUpdate();
            return (rowsAffected > 0) ? "Record updated successfully." : "Update failed.";

        } catch (SQLException e) {
            return "Operation failed: " + e.getMessage();
        }
    }

    /**
     * Deletes a student record.
     */
    public String delete(String studentId) {
        String query = "DELETE FROM STUDENTS WHERE student_id = ?";
        try (Connection conn = dbc.getConnection();
             PreparedStatement pst = conn.prepareStatement(query)) {

            if (conn == null) return "Connection not established.";

            pst.setString(1, studentId);
            int rowsAffected = pst.executeUpdate();

            return (rowsAffected > 0) ? "Record deleted successfully." : "Delete failed.";

        } catch (SQLException e) {
            return "Operation failed: " + e.getMessage();
        }
    }
    
    /*
    * This method retrieves all stored data on students
    * Useful for PDF report generation
    */
    public String getAllStudents() {
        String query = "SELECT * FROM STUDENTS";
        StringBuilder result = new StringBuilder();
        try (Connection conn = dbc.getConnection(); 
             PreparedStatement pst = conn.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
               result.append("ID: ").append(rs.getString("student_id")).append(", ");
               result.append("Name: ").append(rs.getString("student_name")).append(", ");
               result.append("Course: ").append(rs.getString("student_course")).append("\n");
            }
            return result.toString();
        } catch (SQLException e) {
            return "Error retrieving students: " + e.getMessage();
        }
    }
}
