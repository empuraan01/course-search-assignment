package com.example.coursesearch.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestResult {
    private List<String> suggestions;
    private long totalHits;
} 