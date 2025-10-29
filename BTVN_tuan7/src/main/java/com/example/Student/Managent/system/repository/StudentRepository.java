package com.example.Student.Managent.system.repository;

import com.example.Student.Managent.system.entity.Student;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends JpaRepository<Student,Long> {
    @Query(value = """
        SELECT
        s.id,
        s.first_name,
        s.last_name,
        s.email,
        s.student_id,
        s.birthday,
        s.home_town,
        s.classroom_id
        FROM student s
        JOIN classroom c ON c.id = s.classroom_id
        JOIN department d ON d.id = c.department_id
        WHERE d.id = :departmentId
   \s""",
        countQuery = """
        SELECT COUNT(*)
        FROM student s
        JOIN classroom c ON c.id = s.classroom_id
        JOIN department d ON d.id = c.department_id
        WHERE d.id = :departmentId
    """,
    nativeQuery = true)
    Page<Student> findStudentsWithClassAndDepartment(@Param("departmentId") Long departmentId, Pageable pageable);
    List<Student> findByClassroomId(Long classroomId);
}
    