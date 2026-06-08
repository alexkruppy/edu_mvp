package com.example.rest.service;

import com.example.rest.dto.EnrollResponse;
import com.example.rest.exception.AlreadyEnrolledException;
import com.example.rest.exception.CourseNotFoundException;
import com.example.rest.model.Course;
import com.example.rest.model.Enrollment;
import com.example.rest.model.User;
import com.example.rest.repository.CourseRepository;
import com.example.rest.repository.EnrollmentRepository;
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

    private EnrollmentService enrollmentService;

    private User student;
    private Course course;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(enrollmentRepository, courseRepository);

        student = new User("student@test.com", "pass", "Test Student");
        student.setId(1L);

        course = new Course("Java Basics", "Introduction to Java");
        course.setId(10L);
    }

    @Test
    void enroll_shouldSucceed_whenStudentNotAlreadyEnrolled() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(false);

        Enrollment saved = new Enrollment(student, course);
        saved.setId(100L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(saved);

        EnrollResponse response = enrollmentService.enroll(student, 10L);

        assertNotNull(response);
        assertEquals(100L, response.getEnrollmentId());
        assertEquals(10L, response.getCourseId());
        assertEquals("Java Basics", response.getCourseTitle());
        assertTrue(response.getMessage().contains("Successfully enrolled"));

        verify(courseRepository).findById(10L);
        verify(enrollmentRepository).existsByStudentAndCourse(student, course);
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    @Test
    void enroll_shouldThrowCourseNotFoundException_whenCourseDoesNotExist() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CourseNotFoundException.class, () -> enrollmentService.enroll(student, 99L));

        verify(courseRepository).findById(99L);
        verify(enrollmentRepository, never()).existsByStudentAndCourse(any(), any());
        verify(enrollmentRepository, never()).save(any());
    }

    @Test
    void enroll_shouldThrowAlreadyEnrolledException_whenStudentAlreadyEnrolled() {
        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentAndCourse(student, course)).thenReturn(true);

        AlreadyEnrolledException ex = assertThrows(AlreadyEnrolledException.class,
                () -> enrollmentService.enroll(student, 10L));

        assertTrue(ex.getMessage().contains("already enrolled"));

        verify(courseRepository).findById(10L);
        verify(enrollmentRepository).existsByStudentAndCourse(student, course);
        verify(enrollmentRepository, never()).save(any());
    }
}
