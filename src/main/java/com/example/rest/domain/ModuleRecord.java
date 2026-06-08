package com.example.rest.domain;

import java.util.List;

public record ModuleRecord(Long id, String title, List<LessonRecord> lessons) {

    public double calculateProgress(List<ProgressRecord> progressRecords) {
        if (lessons == null || lessons.isEmpty()) return 0.0;
        return lessons.stream()
                .mapToDouble(lesson -> {
                    ProgressRecord pr = findProgress(progressRecords, lesson);
                    return pr != null && pr.completed() ? 100.0 : 0.0;
                })
                .average()
                .orElse(0.0);
    }

    private ProgressRecord findProgress(List<ProgressRecord> records, LessonRecord lesson) {
        if (records == null) return null;
        return records.stream()
                .filter(p -> p.lesson().id().equals(lesson.id()))
                .findFirst()
                .orElse(null);
    }
}
