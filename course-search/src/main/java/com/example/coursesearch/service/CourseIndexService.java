package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseIndexService {

    private static final Logger logger = LoggerFactory.getLogger(CourseIndexService.class);

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;
    private final ElasticsearchOperations elasticsearchOperations;

    public void indexSampleCourses() {
        try {
            elasticsearchOperations.indexOps(CourseDocument.class).delete();
            elasticsearchOperations.indexOps(CourseDocument.class).create();
            elasticsearchOperations.indexOps(CourseDocument.class).putMapping();

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("sample-courses.json");
            List<CourseDocument> courses = objectMapper.readValue(inputStream, new TypeReference<List<CourseDocument>>() {});
            

            courses.forEach(course -> course.setTitleSuggest(course.getTitle()));
            
            courseRepository.saveAll(courses);
            logger.info("Indexed " + courses.size() + " courses into es.");
            logger.info(courses.get(0).getId() + " → " + courses.get(0).getNextSessionDate());

        } catch (Exception e) {
            throw new RuntimeException("Failed to index", e);
        }
    }
}
