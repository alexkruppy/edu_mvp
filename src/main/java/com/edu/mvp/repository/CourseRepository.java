package com.edu.mvp.repository;

import com.edu.mvp.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("""
            SELECT c FROM Course c
            LEFT JOIN FETCH c.modules m
            LEFT JOIN FETCH m.lessons
            WHERE c.id = :id
            """)
    Optional<Course> findByIdWithModulesAndLessons(Long id);
}
