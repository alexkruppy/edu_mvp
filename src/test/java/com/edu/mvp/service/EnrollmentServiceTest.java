package com.edu.mvp.service;

import com.edu.mvp.dto.EnrollResponse;
import com.edu.mvp.exception.AlreadyEnrolledException;
import com.edu.mvp.exception.CourseNotFoundException;
import com.edu.mvp.model.Course;
import com.edu.mvp.model.Enrollment;
import com.edu.mvp.model.Student;
import com.edu.mvp.repository.CourseRepository;
import com.edu.mvp.repository.EnrollmentRepository;
import com.edu.mvp.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    private EnrollmentService enrollmentService;

    private Student student;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(enrollmentRepository, courseRepository, studentRepository);

        student = new Student("Test Student", "student@test.com");
        student.setId(1L);

        course = new Course("Java Basics", "Introduction to Java");
        course.setId(10L);
    }

    @Test
    void enroll_shouldSucceed_whenStudentNotAlreadyEnrolled() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);

        Enrollment saved = new Enrollment(student, course);
        saved.setId(100L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(saved);

        EnrollResponse response = enrollmentService.enroll(1L, 10L);

        assertNotNull(response);
        assertEquals(100L, response.getEnrollmentId());
        assertEquals(10L, response.getCourseId());
        assertEquals("Java Basics", response.getCourseTitle());
        assertEquals(1L, response.getStudentId());
        assertTrue(response.getMessage().contains("Successfully enrolled"));

        verify(studentRepository).findById(1L);
        verify(courseRepository).findById(10L);
        verify(enrollmentRepository).existsByStudentAndCourse(student, course);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enroll_shouldThrowCourseNotFoundException_whenCourseDoesNotExist() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> enrollmentService.enroll(1L, 99L));

        verify(courseRepository).findById(99L);
        verify(enrollmentRepository, never()).existsByStudentAndCourse(any(), any());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enroll_shouldThrowAlreadyEnrolledException_whenStudentAlreadyEnrolled() {
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        AlreadyEnrolledException ex = assertThrows(AlreadyEnrolledException.class,
                () -> enrollmentService.enroll(1L, 10L));

        assertTrue(ex.getMessage().contains("already enrolled"));

        verify(enrollmentRepository).existsByStudentAndCourse(student, course);
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enroll_shouldThrowException_whenStudentDoesNotExist() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> enrollmentService.enroll(99L, 10L));

        verify(studentRepository).findById(99L);
        verify(courseRepository, never()).findById(any());
        verify(enrollmentRepository, never()).save(any());
    }
}
