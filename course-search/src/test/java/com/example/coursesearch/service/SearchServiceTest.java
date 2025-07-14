package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @Mock
    private SearchHits<CourseDocument> searchHits;

    @Mock
    private SearchHit<CourseDocument> searchHit;

    @InjectMocks
    private SearchService searchService;

    private CourseDocument sampleCourse;

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
    }

    @Test
    void testSearch_WithKeyword() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", null, null, null, null,
                null, null, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        assertEquals("Math for Beginners", result.getCourses().get(0).getTitle());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithAllFilters() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", 6, 10, "Math", "COURSE",
                100.0, 300.0, ZonedDateTime.parse("2025-06-01T00:00:00Z"), "priceAsc", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithNoKeyword() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search(null, null, null, null, null,
                null, null, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithEmptyResults() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(0L);
        when(searchHits.stream()).thenReturn(Stream.empty());

        SearchResult result = searchService.search("nonexistent", null, null, null, null,
                null, null, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getCourses().isEmpty());
    }

    @Test
    void testSearch_WithPriceSort() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", null, null, null, null,
                null, null, null, "priceAsc", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithPriceDescSort() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", null, null, null, null,
                null, null, null, "priceDesc", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithPagination() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", null, null, null, null,
                null, null, null, "nextSessionDate", 2, 5);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithAgeRange() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", 6, 10, null, null,
                null, null, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithPriceRange() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", null, null, null, null,
                100.0, 300.0, null, "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithStartDate() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.getTotalHits()).thenReturn(1L);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SearchResult result = searchService.search("math", null, null, null, null,
                null, null, ZonedDateTime.parse("2025-06-01T00:00:00Z"), "nextSessionDate", 0, 10);

        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getCourses().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSearch_WithInvalidAgeRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.search("math", 10, 5, null, null, null, null, null, "nextSessionDate", 0, 10);
        });
    }

    @Test
    void testSearch_WithInvalidPriceRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.search("math", null, null, null, null, 300.0, 100.0, null, "nextSessionDate", 0, 10);
        });
    }

    @Test
    void testSearch_WithInvalidSort() {
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.search("math", null, null, null, null, null, null, null, "invalidSort", 0, 10);
        });
    }

    @Test
    void testSuggest_WithValidQuery() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SuggestResult result = searchService.suggest("math");

        assertNotNull(result);
        assertEquals(1, result.getSuggestions().size());
        assertEquals("Math for Beginners", result.getSuggestions().get(0));
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSuggest_WithEmptyQuery() {
        SuggestResult result = searchService.suggest("");

        assertNotNull(result);
        assertEquals(0L, result.getTotalHits());
        assertTrue(result.getSuggestions().isEmpty());
        verify(elasticsearchOperations, never()).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSuggest_WithNullQuery() {
        SuggestResult result = searchService.suggest(null);

        assertNotNull(result);
        assertEquals(0L, result.getTotalHits());
        assertTrue(result.getSuggestions().isEmpty());
        verify(elasticsearchOperations, never()).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSuggest_WithWhitespaceQuery() {
        SuggestResult result = searchService.suggest("   ");

        assertNotNull(result);
        assertEquals(0L, result.getTotalHits());
        assertTrue(result.getSuggestions().isEmpty());
        verify(elasticsearchOperations, never()).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSuggest_WithMultipleResults() {
        CourseDocument course2 = new CourseDocument();
        course2.setTitle("Advanced Math");
        SearchHit<CourseDocument> searchHit2 = mock(SearchHit.class);
        when(searchHit2.getContent()).thenReturn(course2);

        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.stream()).thenReturn(Arrays.asList(searchHit, searchHit2).stream());
        when(searchHit.getContent()).thenReturn(sampleCourse);

        SuggestResult result = searchService.suggest("math");

        assertNotNull(result);
        assertEquals(2, result.getSuggestions().size());
        assertTrue(result.getSuggestions().contains("Math for Beginners"));
        assertTrue(result.getSuggestions().contains("Advanced Math"));
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }

    @Test
    void testSuggest_WithNoResults() {
        when(elasticsearchOperations.search(any(Query.class), eq(CourseDocument.class)))
                .thenReturn(searchHits);
        when(searchHits.stream()).thenReturn(Stream.empty());

        SuggestResult result = searchService.suggest("nonexistent");

        assertNotNull(result);
        assertEquals(0, result.getSuggestions().size());
        verify(elasticsearchOperations, times(1)).search(any(Query.class), eq(CourseDocument.class));
    }
} 