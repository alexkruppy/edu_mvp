package com.example.rest.service;

import com.example.rest.dto.CourseDetailDto;
import com.example.rest.dto.CourseDto;
import com.example.rest.exception.CourseNotFoundException;
import com.example.rest.model.Course;
import com.example.rest.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDto::from)
                .collect(Collectors.toList());
    }

    public CourseDetailDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return CourseDetailDto.from(course);
    }
}
