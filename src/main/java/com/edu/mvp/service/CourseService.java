package com.edu.mvp.service;

import com.edu.mvp.dto.CourseDetailDto;
import com.edu.mvp.dto.CourseDto;
import com.edu.mvp.exception.CourseNotFoundException;
import com.edu.mvp.model.Course;
import com.edu.mvp.repository.CourseRepository;
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
