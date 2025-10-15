package org.example.server;

import org.example.common.Student;
import org.example.common.StudentService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class StudentServiceImpl extends UnicastRemoteObject implements StudentService {

    private final List<Student> students = new ArrayList<>();

    protected StudentServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public void addStudent(Student student) throws RemoteException {
        students.add(student);
        System.out.println("Đã thêm sinh viên: " + student);
    }

    @Override
    public List<Student> getAllStudents() throws RemoteException {
        return new ArrayList<>(students);
    }
}
