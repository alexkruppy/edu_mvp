package com.edu.mvp.controller;

import com.edu.mvp.dto.CourseDetailDto;
import com.edu.mvp.dto.CourseDto;
import com.edu.mvp.dto.EnrollResponse;
import com.edu.mvp.model.Course;
import com.edu.mvp.repository.CourseRepository;
import com.edu.mvp.service.CourseService;
import com.edu.mvp.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final CourseRepository courseRepository;

    public CourseController(CourseService courseService, EnrollmentService enrollmentService, CourseRepository courseRepository) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<EnrollResponse> enroll(
            @PathVariable Long courseId,
            @RequestParam Long studentId) {
        EnrollResponse response = enrollmentService.enroll(studentId, courseId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody Course course) {
        Course saved = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
