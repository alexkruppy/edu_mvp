package com.example.rest.domain;

import java.time.LocalDateTime;

public record ProgressRecord(
        StudentRecord student,
        LessonRecord lesson,
        boolean completed,
        LocalDateTime completedAt
) {
    public double getPercentage() {
        return completed ? 100.0 : 0.0;
    }
}
