package org.example.server;

import org.example.common.StudentService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class StudentServer {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099); // mở RMI registry
            StudentService service = new StudentServiceImpl();
            Naming.rebind("rmi://localhost:1099/StudentService", service);

            System.out.println("Student RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

