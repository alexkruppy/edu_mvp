package com.edu.mvp.dto;

import com.edu.mvp.model.Module;

import java.util.List;

public class ModuleDto {
    private Long id;
    private String title;
    private int orderIndex;
    private List<LessonDto> lessons;

    public static ModuleDto from(Module module) {
        ModuleDto dto = new ModuleDto();
        dto.setId(module.getId());
        dto.setTitle(module.getTitle());
        dto.setOrderIndex(module.getOrderIndex());
        dto.setLessons(module.getLessons().stream().map(LessonDto::from).toList());
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
