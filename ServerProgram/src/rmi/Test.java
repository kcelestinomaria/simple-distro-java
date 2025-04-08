/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package rmi;
//import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author User
 */
public class Test {
    public static void main(String[] args) {
        try {
            
            // Obtain the reference to the RMI registry on the same port
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            // We lookup the remote object in the registry
            StudentDBInterface stub = (StudentDBInterface) registry.lookup("StudentDBService");
            
            // We then test a method(e.g registerUser) on the remote object
            System.out.println(stub.insert("1", "Mr. Mido", "BBIT"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
