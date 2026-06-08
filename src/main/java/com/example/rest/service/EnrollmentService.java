package com.example.rest.service;

import com.example.rest.dto.EnrollResponse;
import com.example.rest.exception.AlreadyEnrolledException;
import com.example.rest.exception.CourseNotFoundException;
import com.example.rest.model.Course;
import com.example.rest.model.Enrollment;
import com.example.rest.model.User;
import com.example.rest.repository.CourseRepository;
import com.example.rest.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    public EnrollResponse enroll(User student, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new AlreadyEnrolledException(
                    "User " + student.getEmail() + " is already enrolled in course: " + course.getTitle());
        }

        Enrollment enrollment = new Enrollment(student, course);
        enrollment = enrollmentRepository.save(enrollment);

        return new EnrollResponse(enrollment.getId(), course.getId(), course.getTitle());
    }
}
