package com.example.Student.Managent.system.controller;

import com.example.Student.Managent.system.entity.Classroom;
import com.example.Student.Managent.system.entity.Department;
import com.example.Student.Managent.system.entity.Student;
import com.example.Student.Managent.system.service.ClassroomService;
import com.example.Student.Managent.system.service.DepartmentService;
import com.example.Student.Managent.system.service.StudentService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/classes")
public class ClassroomController {

    @Autowired
    private ClassroomService classroomService;

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        return ResponseEntity.ok(classroomService.getAllClassrooms());
    }

    @GetMapping("/{classId}/students")
    public ResponseEntity<List<Student>> getStudentsInClass(@PathVariable Long classId) {
        List<Student> students = studentService.findByClassroomId(classId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{classId}")
    public ResponseEntity<Classroom> getClassroomById(@PathVariable Long classId) {
        Classroom classroom = classroomService.getClassroomById(classId);
        return ResponseEntity.ok(classroom);
    }

    @PostMapping("/department/{departmentId}")
    public ResponseEntity<Classroom> createClassroom(@PathVariable Long departmentId, @RequestBody Classroom classroom) {
        Department department = departmentService.getDepartmentById(departmentId);
        classroom.setDepartment(department);
        Classroom savedClassroom = classroomService.saveClassroom(classroom);
        return new ResponseEntity<>(savedClassroom, HttpStatus.CREATED);
    }

    @PutMapping("/{classId}")
    public ResponseEntity<Classroom> updateClassroom(@PathVariable Long classId, @RequestBody Classroom updatedClassroom) {
        Classroom existing = classroomService.getClassroomById(classId);
        updatedClassroom.setId(existing.getId());
        updatedClassroom.setDepartment(existing.getDepartment()); // giữ nguyên department
        Classroom saved = classroomService.saveClassroom(updatedClassroom);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClassroom(@PathVariable Long classId) {
        classroomService.deleteClassroomByClassId(classId);
        return ResponseEntity.noContent().build();
    }

}
