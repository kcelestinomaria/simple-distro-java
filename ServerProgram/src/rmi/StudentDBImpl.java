package rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import server.DBOperations;

public class StudentDBImpl extends UnicastRemoteObject implements StudentDBInterface {

    private final DBOperations dbo = new DBOperations();

    public StudentDBImpl() throws RemoteException {
        super();
    }

    @Override
    public String insert(String id, String name, String course) throws RemoteException {
        return dbo.insert(id, name, course);
    }

    @Override
    public String select(String id) throws RemoteException {
        return dbo.select(id);
    }

    @Override
    public String update(String id, String newName, String newCourse) throws RemoteException {
        return dbo.update(id, newName, newCourse);
    }

    @Override
    public String delete(String id) throws RemoteException {
        return dbo.delete(id);
    }

    // Implement methods for user registration and login
    @Override
    public String registerUser(String username, String password) throws RemoteException {
        return dbo.registerUser(username, password);
    }

    @Override
    public String loginUser(String username, String password) throws RemoteException {
        return dbo.loginUser(username, password);
    }
    
    @Override
    public String getAllStudents() throws RemoteException {
        return dbo.getAllStudents();
    }
}