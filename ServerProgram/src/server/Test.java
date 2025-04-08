package server;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Test class to run CRUD operations.
 */
public class Test {
    public static void main(String[] args) {
        
        DBOperations dbo = new DBOperations();
        
        // Register User
        System.out.println(dbo.registerUser("celestine", "mySecretPassword"));
        
        // Login User
        System.out.println(dbo.loginUser("celestine", "mySecretPassword"));

        // Insert test
        System.out.println(dbo.insert("1001", "John Doe", "Computer Science"));

        // Select test
        System.out.println(dbo.select("1001"));

        // Update test
        System.out.println(dbo.update("1001", "Johnathan Doe", "Software Engineering"));

        // Delete test
        System.out.println(dbo.delete("1001"));
    }
}
