package com.edu.mvp.repository;

import com.edu.mvp.model.Course;
import com.edu.mvp.model.Enrollment;
import com.edu.mvp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndCourse(Student student, Course course);
    Optional<Enrollment> findByStudentAndCourse(Student student, Course course);
}
