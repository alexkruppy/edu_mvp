package com.example.rest.controller;

import com.example.rest.dto.CourseDetailDto;
import com.example.rest.dto.CourseDto;
import com.example.rest.dto.EnrollResponse;
import com.example.rest.model.User;
import com.example.rest.service.AuthService;
import com.example.rest.service.CourseService;
import com.example.rest.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final AuthService authService;

    public CourseController(CourseService courseService, EnrollmentService enrollmentService, AuthService authService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDetailDto> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<EnrollResponse> enroll(@PathVariable Long id, Authentication authentication) {
        User student = authService.getCurrentUser(authentication.getName());
        EnrollResponse response = enrollmentService.enroll(student, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
