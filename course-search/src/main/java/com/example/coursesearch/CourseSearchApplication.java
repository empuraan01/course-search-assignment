package com.example.coursesearch;

import com.example.coursesearch.service.CourseIndexService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CourseSearchApplication implements CommandLineRunner {

    private final CourseIndexService courseIndexService;

    public CourseSearchApplication(CourseIndexService courseIndexService) {
        this.courseIndexService = courseIndexService;
    }

    public static void main(String[] args) {
        SpringApplication.run(CourseSearchApplication.class, args);
    }

    @Override
    public void run(String... args) {
        courseIndexService.indexSampleCourses();
    }
}
