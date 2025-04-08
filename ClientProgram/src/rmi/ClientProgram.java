package rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientProgram {
    public static void main(String[] args) {
        try {
            // Locate the registry running on localhost, port 1099
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Lookup the remote object
            StudentDBInterface db = (StudentDBInterface) registry.lookup("StudentDBService");

            // Perform CRUD operations
            System.out.println(db.insert("1001", "John Doe", "Computer Science"));
            System.out.println(db.select("1001"));
            System.out.println(db.update("1001", "Johnathan Doe", "Software Engineering"));
            System.out.println(db.delete("1001"));
            
            // Let's also test the register and login functionality
            System.out.println(db.registerUser("Mr Mido", "password123BBIT"));
            System.out.println(db.loginUser("Mr Mido", "password123BBIT"));
            System.out.println(db.loginUser("Mr Mido", "wrongpassword"));
            
        } catch (NotBoundException | RemoteException ex) {
            System.err.println("Client error: " + ex.getMessage());
        }
    }
}
