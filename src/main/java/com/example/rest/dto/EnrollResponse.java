package com.example.rest.dto;

public class EnrollResponse {
    private Long enrollmentId;
    private Long courseId;
    private String courseTitle;
    private String message;

    public EnrollResponse(Long enrollmentId, Long courseId, String courseTitle) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.message = "Successfully enrolled in course: " + courseTitle;
    }

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
