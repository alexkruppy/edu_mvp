package com.example.rest.exception;

public class CourseNotFoundException extends RuntimeException {
    public CourseNotFoundException(Long id) {
        super("Course not found with id: " + id);
    }
}
