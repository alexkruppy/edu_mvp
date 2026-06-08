package com.example.rest.repository;

import com.example.rest.model.Course;
import com.example.rest.model.Enrollment;
import com.example.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentAndCourse(User student, Course course);
    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
}
