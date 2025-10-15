package org.example.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface StudentService extends Remote {
    void addStudent(Student student) throws RemoteException;
    List<Student> getAllStudents() throws RemoteException;
}

