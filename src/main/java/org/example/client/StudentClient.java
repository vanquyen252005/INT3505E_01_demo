package org.example.client;

import org.example.common.Student;
import org.example.common.StudentService;

import java.rmi.Naming;
import java.util.List;

public class StudentClient {
    public static void main(String[] args) {
        try {
            StudentService service = (StudentService) Naming.lookup("rmi://localhost:1099/StudentService");

            // Gọi remote method
            service.addStudent(new Student(1, "Nguyen Van A"));
            service.addStudent(new Student(2, "Tran Thi B"));

            List<Student> list = service.getAllStudents();
            System.out.println("Danh sách sinh viên từ server:");
            list.forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

