package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResult {
    private long total;
    private List<CourseDocument> courses;
}
