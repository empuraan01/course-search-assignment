package com.example.coursesearch.controller;

import com.example.coursesearch.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import com.example.coursesearch.service.SearchResult;

@RestController
@RequestMapping("/api/search")
@Validated
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public SearchResult searchCourses(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) Integer minAge,
        @RequestParam(required = false) Integer maxAge,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String type,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
        @RequestParam(defaultValue = "nextSessionDate") String sort,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size
    ) {
        SearchResult result = searchService.search(
                q, minAge, maxAge, category, type,
                minPrice, maxPrice, startDate,
                sort, page, size
        );

        if (result == null) {
            return new SearchResult(0, java.util.Collections.emptyList());
        }

        return result;
    }
}
