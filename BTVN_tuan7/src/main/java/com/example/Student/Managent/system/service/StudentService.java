package com.example.Student.Managent.system.service;

import com.example.Student.Managent.system.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
     public List<Student> getAllStudents();
     public Student saveStudent(Student student);
     public Student getStudentById(Long studentId);
     public void deleteStudent(Long studentId);
     public List<Student> findByClassroomId(Long classroomId);
     public Page<Student> findStudentsWithClassAndDepartment(Long departmentId, Pageable pageable);
}
