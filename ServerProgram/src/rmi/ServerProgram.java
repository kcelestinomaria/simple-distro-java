package rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ServerProgram {
    public static void main(String[] args) {
        try {
            // Create an instance of the remote object
            StudentDBImpl dbImpl = new StudentDBImpl();

            // Start the RMI registry on port 1099
            Registry registry = LocateRegistry.createRegistry(1099);

            // Bind the remote object to the registry
            registry.rebind("StudentDBService", dbImpl);

            System.out.println("RMI Server is running...");
        } catch (RemoteException ex) {
            System.err.println("Server error: " + ex.getMessage());
        }
    }
}