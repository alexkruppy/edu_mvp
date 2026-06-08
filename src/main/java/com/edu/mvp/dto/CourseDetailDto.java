package com.edu.mvp.dto;

import com.edu.mvp.model.*;

import java.util.List;
import java.util.stream.Collectors;

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
        dto.setModules(course.getModules().stream().map(ModuleDto::from).collect(Collectors.toList()));
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

    public static class ModuleDto {
        private Long id;
        private String title;
        private int orderIndex;
        private List<LessonDto> lessons;

        public static ModuleDto from(Module module) {
            ModuleDto dto = new ModuleDto();
            dto.setId(module.getId());
            dto.setTitle(module.getTitle());
            dto.setOrderIndex(module.getOrderIndex());
            dto.setLessons(module.getLessons().stream().map(LessonDto::from).collect(Collectors.toList()));
            return dto;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public int getOrderIndex() { return orderIndex; }
        public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
        public List<LessonDto> getLessons() { return lessons; }
        public void setLessons(List<LessonDto> lessons) { this.lessons = lessons; }
    }

    public static class LessonDto {
        private Long id;
        private String title;
        private int durationMinutes;
        private int orderIndex;

        public static LessonDto from(Lesson lesson) {
            LessonDto dto = new LessonDto();
            dto.setId(lesson.getId());
            dto.setTitle(lesson.getTitle());
            dto.setDurationMinutes(lesson.getDurationMinutes());
            dto.setOrderIndex(lesson.getOrderIndex());
            return dto;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public int getOrderIndex() { return orderIndex; }
        public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
    }
}
