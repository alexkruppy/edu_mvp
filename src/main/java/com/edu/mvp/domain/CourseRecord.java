package com.edu.mvp.domain;

import java.util.List;

public record CourseRecord(Long id, String title, String description, List<ModuleRecord> modules) {

    public double calculateProgress(List<ProgressRecord> progressRecords) {
        if (modules == null || modules.isEmpty()) return 0.0;
        return modules.stream()
                .mapToDouble(m -> m.calculateProgress(progressRecords))
                .average()
                .orElse(0.0);
    }

    public double calculateLessonCompletionPercentage(int totalLessons, List<ProgressRecord> progressRecords) {
        if (totalLessons == 0) return 0.0;
        long completed = progressRecords.stream()
                .filter(ProgressRecord::completed)
                .count();
        return (double) completed / totalLessons * 100.0;
    }
}
