package com.edu.mvp.repository;

import com.edu.mvp.model.Lesson;
import com.edu.mvp.model.LessonProgress;
import com.edu.mvp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    Optional<LessonProgress> findByStudentAndLesson(Student student, Lesson lesson);
    List<LessonProgress> findByStudentAndLessonIn(Student student, List<Lesson> lessons);
}
