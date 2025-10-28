package com.example.Student.Managent.system.controller;

import com.example.Student.Managent.system.entity.Department;
import com.example.Student.Managent.system.entity.Student;
import com.example.Student.Managent.system.exception.DepartmentAlreadyExistException;
import com.example.Student.Managent.system.exception.ErrorRespone;
import com.example.Student.Managent.system.exception.NoSuchDepartmentExistsException;
import com.example.Student.Managent.system.entity.Classroom;
import com.example.Student.Managent.system.service.DepartmentService;
import com.example.Student.Managent.system.service.ClassroomService;
import com.example.Student.Managent.system.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ClassroomService classroomService;
    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseEntity<List<Department>> listDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<Department> getDepartment(@PathVariable Long departmentId) {
        Department department = departmentService.getDepartmentById(departmentId);
        return ResponseEntity.ok(department);
    }

    @GetMapping("/{departmentId}/classes")
    public ResponseEntity<List<Classroom>> viewClassesInDepartment(@PathVariable Long departmentId) {
        List<Classroom> classes = classroomService.getClassesByDepartmentId(departmentId);
        return ResponseEntity.ok(classes);
    }
    // Lay tat ca cac hoc sinh trong cung 1 khoa
    @GetMapping("/{departmentId}/classes/students")
    public ResponseEntity<List<Student>> viewStudentInDepartment(@PathVariable Long departmentId) {
        List<Student> students = new ArrayList<>();
        List<Classroom> classes = classroomService.getClassesByDepartmentId(departmentId);
        for(int i=0;i<classes.size();i++) {
            List<Student> studentInClass = studentService.findByClassroomId(classes.get(i).getId());
            students.addAll(studentInClass);
        }
        return ResponseEntity.ok(students);
    }
    @PostMapping
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        Department department1 = departmentService.saveDepartment(department);
        return new ResponseEntity<>(department1, HttpStatus.CREATED);
    }
    @PutMapping("/{departmentId}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long departmentId, @RequestBody Department updatedDepartment) {
        updatedDepartment.setId(departmentId);
        Department result = departmentService.saveDepartment(updatedDepartment);
        return ResponseEntity.ok(result);
    }
    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long departmentId) {
        departmentService.deleteDepartment(departmentId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchDepartmentExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRespone handleDepartmentNotFound(NoSuchDepartmentExistsException ex) {
        return new ErrorRespone(HttpStatus.NOT_FOUND.value(), "Department not found");
    }

    @ExceptionHandler(DepartmentAlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorRespone handleDepartmentAlreadyExists(DepartmentAlreadyExistException ex) {
        return new ErrorRespone(HttpStatus.CONFLICT.value(), "Department already exists");
    }
}
