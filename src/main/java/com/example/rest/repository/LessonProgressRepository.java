package com.example.rest.repository;

import com.example.rest.model.Lesson;
import com.example.rest.model.LessonProgress;
import com.example.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByStudentAndLesson(User student, Lesson lesson);
    List<LessonProgress> findByStudentAndLessonIn(User student, List<Lesson> lessons);
}
