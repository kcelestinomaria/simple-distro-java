package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StudentDBInterface extends Remote {
    
    // New methods for user registration and login
    String registerUser(String username, String password) throws RemoteException;
    String loginUser(String username, String password) throws RemoteException;
    
    String insert(String id, String name, String course) throws RemoteException;
    String select(String id) throws RemoteException;
    String update(String id, String newName, String newCourse) throws RemoteException;
    String delete(String id) throws RemoteException;
    
    // fetch all student records in one method
    String getAllStudents() throws RemoteException;
}