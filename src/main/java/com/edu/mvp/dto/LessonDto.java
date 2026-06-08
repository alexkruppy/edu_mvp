package com.edu.mvp.dto;

import com.edu.mvp.model.Lesson;

public class LessonDto {
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
