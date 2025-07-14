package com.example.coursesearch.controller;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.service.SearchResult;
import com.example.coursesearch.service.SearchService;
import com.example.coursesearch.service.SuggestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private CourseDocument sampleCourse;
    private SearchResult sampleSearchResult;
    private SuggestResult sampleSuggestResult;

    @BeforeEach
    void setUp() {
        sampleCourse = new CourseDocument();
        sampleCourse.setId("1");
        sampleCourse.setTitle("Math for Beginners");
        sampleCourse.setDescription("Intro to basic math concepts.");
        sampleCourse.setCategory("Math");
        sampleCourse.setType("COURSE");
        sampleCourse.setMinAge(6);
        sampleCourse.setMaxAge(8);
        sampleCourse.setPrice(199.99);
        sampleCourse.setNextSessionDate(ZonedDateTime.parse("2025-06-10T15:00:00Z"));

        sampleSearchResult = new SearchResult(1L, Arrays.asList(sampleCourse));
        sampleSuggestResult = new SuggestResult(Arrays.asList("Math for Beginners", "Advanced Math"), 2L);
    }

    @Test
    void testSearchCourses_WithAllParameters() {
        String keyword = "math";
        Integer minAge = 6;
        Integer maxAge = 10;
        String category = "Math";
        String type = "COURSE";
        Double minPrice = 100.0;
        Double maxPrice = 300.0;
        ZonedDateTime startDate = ZonedDateTime.parse("2025-06-01T00:00:00Z");
        String sort = "priceAsc";
        int page = 0;
        int size = 10;

        when(searchService.search(eq(keyword), eq(minAge), eq(maxAge), eq(category), eq(type),
                eq(minPrice), eq(maxPrice), eq(startDate), eq(sort), eq(page), eq(size)))
                .thenReturn(sampleSearchResult);


        SearchResult result = searchController.searchCourses(keyword, minAge, maxAge, category, type,
                minPrice, maxPrice, startDate, sort, page, size);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        assertEquals("Math for Beginners", result.getCourses().get(0).getTitle());

        verify(searchService, times(1)).search(eq(keyword), eq(minAge), eq(maxAge), eq(category), eq(type),
                eq(minPrice), eq(maxPrice), eq(startDate), eq(sort), eq(page), eq(size));
    }

    @Test
    void testSearchCourses_WithOnlyKeyword() {
        String keyword = "math";
        when(searchService.search(eq(keyword), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq("nextSessionDate"), eq(0), eq(10)))
                .thenReturn(sampleSearchResult);

        SearchResult result = searchController.searchCourses(keyword, null, null, null, null,
                null, null, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());

        verify(searchService, times(1)).search(eq(keyword), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq("nextSessionDate"), eq(0), eq(10));
    }

    @Test
    void testSearchCourses_WithNoParameters() {
        when(searchService.search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq("nextSessionDate"), eq(0), eq(10)))
                .thenReturn(sampleSearchResult);

        SearchResult result = searchController.searchCourses(null, null, null, null, null,
                null, null, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());

        verify(searchService, times(1)).search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq("nextSessionDate"), eq(0), eq(10));
    }

    @Test
    void testSearchCourses_WhenServiceReturnsNull() {
        when(searchService.search(eq("math"), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq("nextSessionDate"), eq(0), eq(10)))
                .thenReturn(null);

        SearchResult result = searchController.searchCourses("math", null, null, null, null,
                null, null, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getCourses().isEmpty());
    }

    @Test
    void testSearchCourses_WithCustomPagination() {
        int page = 2;
        int size = 5;
        when(searchService.search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq("nextSessionDate"), eq(page), eq(size)))
                .thenReturn(sampleSearchResult);

        SearchResult result = searchController.searchCourses(null, null, null, null, null,
                null, null, null, "nextSessionDate", page, size);

        assertNotNull(result);
        verify(searchService, times(1)).search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq("nextSessionDate"), eq(page), eq(size));
    }

    @Test
    void testSearchCourses_WithDifferentSortOptions() {
        String sortPriceAsc = "priceAsc";
        String sortPriceDesc = "priceDesc";
        
        when(searchService.search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq(sortPriceAsc), eq(0), eq(10)))
                .thenReturn(sampleSearchResult);
        
        when(searchService.search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq(sortPriceDesc), eq(0), eq(10)))
                .thenReturn(sampleSearchResult);

        SearchResult result1 = searchController.searchCourses(null, null, null, null, null,
                null, null, null, sortPriceAsc, 0, 10);
        
        SearchResult result2 = searchController.searchCourses(null, null, null, null, null,
                null, null, null, sortPriceDesc, 0, 10);

        assertNotNull(result1);
        assertNotNull(result2);
        
        verify(searchService, times(1)).search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq(sortPriceAsc), eq(0), eq(10));
        verify(searchService, times(1)).search(isNull(), isNull(), isNull(), isNull(), isNull(),
                isNull(), isNull(), isNull(), eq(sortPriceDesc), eq(0), eq(10));
    }

    @Test
    void testSuggestCourses_WithValidQuery() {
        String query = "math";
        when(searchService.suggest(query)).thenReturn(sampleSuggestResult);

        SuggestResult result = searchController.suggestCourses(query);

        assertNotNull(result);
        assertEquals(2L, result.getTotalHits());
        assertEquals(2, result.getSuggestions().size());
        assertTrue(result.getSuggestions().contains("Math for Beginners"));
        assertTrue(result.getSuggestions().contains("Advanced Math"));

        verify(searchService, times(1)).suggest(query);
    }

    @Test
    void testSuggestCourses_WithEmptyQuery() {
        String query = "";
        SuggestResult emptyResult = new SuggestResult(Collections.emptyList(), 0L);
        when(searchService.suggest(query)).thenReturn(emptyResult);

        SuggestResult result = searchController.suggestCourses(query);

        assertNotNull(result);
        assertEquals(0L, result.getTotalHits());
        assertTrue(result.getSuggestions().isEmpty());

        verify(searchService, times(1)).suggest(query);
    }

    @Test
    void testSuggestCourses_WithSingleCharacterQuery() {
        String query = "m";
        when(searchService.suggest(query)).thenReturn(sampleSuggestResult);

        SuggestResult result = searchController.suggestCourses(query);

        assertNotNull(result);
        assertEquals(2L, result.getTotalHits());
        assertEquals(2, result.getSuggestions().size());

        verify(searchService, times(1)).suggest(query);
    }

    @Test
    void testSuggestCourses_WithLongQuery() {
        String query = "mathematics for advanced students";
        when(searchService.suggest(query)).thenReturn(sampleSuggestResult);

        SuggestResult result = searchController.suggestCourses(query);

        assertNotNull(result);
        assertEquals(2L, result.getTotalHits());
        assertEquals(2, result.getSuggestions().size());

        verify(searchService, times(1)).suggest(query);
    }

    @Test
    void testSuggestCourses_WithSpecialCharacters() {
        String query = "math@#$%";
        when(searchService.suggest(query)).thenReturn(sampleSuggestResult);

        SuggestResult result = searchController.suggestCourses(query);

        assertNotNull(result);
        assertEquals(2L, result.getTotalHits());
        assertEquals(2, result.getSuggestions().size());

        verify(searchService, times(1)).suggest(query);
    }
}