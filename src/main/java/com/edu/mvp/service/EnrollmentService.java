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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             StudentRepository studentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public EnrollResponse enroll(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));

        if (enrollmentRepository.existsByStudentAndCourse(student, course)) {
            throw new AlreadyEnrolledException(
                    "Student " + student.getEmail() + " is already enrolled in course: " + course.getTitle());
        }

        Enrollment enrollment = new Enrollment(student, course);
        enrollment = enrollmentRepository.save(enrollment);

        return new EnrollResponse(enrollment.getId(), course.getId(), course.getTitle(), student.getId());
    }
}
