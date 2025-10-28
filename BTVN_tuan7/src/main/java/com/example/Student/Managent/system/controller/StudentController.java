package com.example.Student.Managent.system.controller;

import com.example.Student.Managent.system.entity.Classroom;
import com.example.Student.Managent.system.entity.Student;
import com.example.Student.Managent.system.service.ClassroomService;
import com.example.Student.Managent.system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private ClassroomService classroomService;

    @PostMapping("/class/{classId}")
    public ResponseEntity<Student> createStudent(@PathVariable Long classId, @RequestBody Student student) {
        Classroom classroom = classroomService.getClassroomById(classId);
        student.setClassroom(classroom);
        Student savedStudent = studentService.saveStudent(student);
        return new ResponseEntity<>(savedStudent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return ResponseEntity.ok(student);
    }
    @PutMapping("/{studentId}")
    public ResponseEntity<Student> updateStudent(@PathVariable Long studentId, @RequestBody Student updatedStudent) {
        Student existing = studentService.getStudentById(studentId);
        updatedStudent.setId(existing.getId());
        updatedStudent.setClassroom(existing.getClassroom()); // giữ nguyên lớp cũ
        Student saved = studentService.saveStudent(updatedStudent);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }


}
