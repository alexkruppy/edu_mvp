package com.edu.mvp.dto;

import com.edu.mvp.model.Course;

import java.util.List;

public class CourseDetailDto {
    private Long id;
    private String title;
    private String description;
    private List<ModuleDto> modules;
    private String createdAt;

    public static CourseDetailDto from(Course course) {
        CourseDetailDto dto = new CourseDetailDto();
        dto.setId(course.getId());
        dto.setTitle(course.getTitle());
        dto.setDescription(course.getDescription());
        dto.setCreatedAt(course.getCreatedAt() != null ? course.getCreatedAt().toString() : null);
        dto.setModules(course.getModules().stream().map(ModuleDto::from).toList());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<ModuleDto> getModules() { return modules; }
    public void setModules(List<ModuleDto> modules) { this.modules = modules; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
