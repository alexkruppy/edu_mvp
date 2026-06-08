package com.edu.mvp.controller;

import com.edu.mvp.dto.CourseDetailDto;
import com.edu.mvp.dto.CourseDto;
import com.edu.mvp.dto.EnrollResponse;
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

    public CourseController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
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
}
