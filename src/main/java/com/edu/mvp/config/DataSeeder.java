package com.edu.mvp.config;

import com.edu.mvp.model.*;
import com.edu.mvp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;

    public DataSeeder(CourseRepository courseRepository, StudentRepository studentRepository,
                      EnrollmentRepository enrollmentRepository, LessonProgressRepository lessonProgressRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonProgressRepository = lessonProgressRepository;
    }

    @Override
    public void run(String... args) {
        if (courseRepository.count() > 0) return;

        Course java = seedJavaCourse();
        Course spring = seedSpringCourse();
        Course sql = seedDatabaseCourse();

        Student alice = studentRepository.save(new Student("Alice Johnson", "alice@test.com"));
        Student bob = studentRepository.save(new Student("Bob Smith", "bob@test.com"));
        Student charlie = studentRepository.save(new Student("Charlie Brown", "charlie@test.com"));

        enrollmentRepository.save(new Enrollment(alice, java));
        enrollmentRepository.save(new Enrollment(alice, spring));
        enrollmentRepository.save(new Enrollment(bob, java));
        enrollmentRepository.save(new Enrollment(charlie, sql));

        List<Lesson> javaLessons = java.getModules().stream()
                .flatMap(m -> m.getLessons().stream()).toList();
        progress(alice, javaLessons.subList(0, 3));
        progress(bob, javaLessons.subList(0, 1));

        List<Lesson> springLessons = spring.getModules().stream()
                .flatMap(m -> m.getLessons().stream()).toList();
        progress(alice, springLessons.subList(0, 2));
    }

    private Course seedJavaCourse() {
        Course c = new Course("Java 17 Fundamentals",
                "Complete introduction to Java 17 covering OOP, collections, streams, concurrency, and best practices.");

        Module m1 = new Module("Java Basics & OOP", c, 1);
        m1.getLessons().addAll(List.of(
                new Lesson("JVM Architecture & Memory Model", "Understanding stack, heap, and class loading.", 25, m1, 1),
                new Lesson("Classes, Inheritance & Polymorphism", "Abstract classes, interfaces, sealed classes.", 35, m1, 2),
                new Lesson("Records & Pattern Matching", "Modern data carriers and switch expressions.", 30, m1, 3)
        ));

        Module m2 = new Module("Collections & Streams", c, 2);
        m2.getLessons().addAll(List.of(
                new Lesson("Collection Framework Deep Dive", "ArrayList, HashMap, TreeSet, concurrent collections.", 40, m2, 1),
                new Lesson("Stream API & Optional", "Map, filter, reduce, flatMap, collectors.", 45, m2, 2),
                new Lesson("Date/Time API", "LocalDate, Duration, Period, and formatting.", 20, m2, 3)
        ));

        Module m3 = new Module("Concurrency", c, 3);
        m3.getLessons().addAll(List.of(
                new Lesson("Threads & Executors", "Thread pools, Callable, Future, and best practices.", 35, m3, 1),
                new Lesson("CompletableFuture", "Async pipelines, thenCombine, exceptionally, allOf.", 40, m3, 2),
                new Lesson("Virtual Threads (Project Loom)", "Lightweight threads for high-concurrency apps.", 30, m3, 3)
        ));

        c.getModules().addAll(List.of(m1, m2, m3));
        return courseRepository.save(c);
    }

    private Course seedSpringCourse() {
        Course c = new Course("Spring Boot 3 & Microservices",
                "Build production-ready microservices with Spring Boot 3, Spring Data JPA, and REST APIs.");

        Module m1 = new Module("Spring Core & DI", c, 1);
        m1.getLessons().addAll(List.of(
                new Lesson("IoC Container & Beans", "ApplicationContext, @Component, @Bean, scopes.", 30, m1, 1),
                new Lesson("Dependency Injection Patterns", "Constructor vs setter injection, qualifiers.", 25, m1, 2),
                new Lesson("Configuration & Properties", "application.yml, @ConfigurationProperties, profiles.", 20, m1, 3)
        ));

        Module m2 = new Module("Spring Data JPA", c, 2);
        m2.getLessons().addAll(List.of(
                new Lesson("Entities & Relationships", "@Entity, @OneToMany, @ManyToOne, cascade types.", 35, m2, 1),
                new Lesson("Spring Data Repositories", "JpaRepository, custom queries, @Query, Specifications.", 30, m2, 2),
                new Lesson("Transactions & Locking", "@Transactional, isolation levels, optimistic locking.", 25, m2, 3)
        ));

        Module m3 = new Module("REST APIs & Testing", c, 3);
        m3.getLessons().addAll(List.of(
                new Lesson("Building REST Controllers", "@RestController, @RequestMapping, DTOs, @ControllerAdvice.", 30, m3, 1),
                new Lesson("Validation & Error Handling", "@Valid, custom exceptions, standardized error responses.", 25, m3, 2),
                new Lesson("Testing with JUnit 5 & Mockito", "Unit tests, MockMvc, @WebMvcTest, @DataJpaTest.", 40, m3, 3)
        ));

        c.getModules().addAll(List.of(m1, m2, m3));
        return courseRepository.save(c);
    }

    private Course seedDatabaseCourse() {
        Course c = new Course("SQL & Database Design",
                "Master SQL queries, database modeling, indexing, and PostgreSQL specific features.");

        Module m1 = new Module("SQL Fundamentals", c, 1);
        m1.getLessons().addAll(List.of(
                new Lesson("SELECT, JOINs & Subqueries", "Inner, left, right joins; correlated subqueries, CTE.", 40, m1, 1),
                new Lesson("Aggregation & Window Functions", "GROUP BY, HAVING, ROW_NUMBER, RANK, LAG/LEAD.", 35, m1, 2),
                new Lesson("DML & Transactions", "INSERT, UPDATE, DELETE, MERGE, ACID properties.", 25, m1, 3)
        ));

        Module m2 = new Module("Database Design", c, 2);
        m2.getLessons().addAll(List.of(
                new Lesson("Normalization & ER Diagrams", "1NF–3NF, foreign keys, composite keys.", 30, m2, 1),
                new Lesson("Indexing Strategies", "B-tree, hash, composite indexes, EXPLAIN ANALYZE.", 25, m2, 2),
                new Lesson("PostgreSQL Specific Features", "JSONB, full-text search, array types, extensions.", 30, m2, 3)
        ));

        c.getModules().addAll(List.of(m1, m2));
        return courseRepository.save(c);
    }

    private void progress(Student student, List<Lesson> lessons) {
        for (Lesson lesson : lessons) {
            LessonProgress lp = new LessonProgress(student, lesson);
            lp.setCompleted(true);
            lessonProgressRepository.save(lp);
        }
    }
}
