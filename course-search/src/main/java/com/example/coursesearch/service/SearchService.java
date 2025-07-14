package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    private final ElasticsearchOperations elasticsearchOperations;

    public SearchResult search(
            String keyword,
            Integer minAge,
            Integer maxAge,
            String category,
            String type,
            Double minPrice,
            Double maxPrice,
            ZonedDateTime startDate,
            String sort,
            int page,
            int size
    ) {
        logger.info("SearchService called");

        validateSearchParameters(minAge, maxAge, minPrice, maxPrice, sort);

        Criteria criteria = new Criteria();

        if (keyword != null && !keyword.isBlank()) {
            Criteria textCriteria = new Criteria()
                    .or(new Criteria("title").contains(keyword))
                    .or(new Criteria("description").contains(keyword));
            criteria = criteria.and(textCriteria);
        }

        if (category != null && !category.isBlank()) {
            criteria = criteria.and(new Criteria("category").is(category));
        }

        if (type != null && !type.isBlank()) {
            criteria = criteria.and(new Criteria("type").is(type));
        }

        if (minAge != null || maxAge != null) {
            Criteria ageCriteria = new Criteria("minAge");
            if (minAge != null) ageCriteria = ageCriteria.greaterThanEqual(minAge);
            if (maxAge != null) ageCriteria = ageCriteria.lessThanEqual(maxAge);
            criteria = criteria.and(ageCriteria);
        }

        if (minPrice != null || maxPrice != null) {
            Criteria priceCriteria = new Criteria("price");
            if (minPrice != null) priceCriteria = priceCriteria.greaterThanEqual(minPrice);
            if (maxPrice != null) priceCriteria = priceCriteria.lessThanEqual(maxPrice);
            criteria = criteria.and(priceCriteria);
        }

        if (startDate != null) {
            criteria = criteria.and(new Criteria("nextSessionDate").greaterThanEqual(startDate));
        }

        Pageable pageable = PageRequest.of(page, size);
        Query query = new CriteriaQuery(criteria, pageable);

        if ("priceAsc".equals(sort)) {
            query.addSort(Sort.by(Sort.Direction.ASC, "price"));
        } else if ("priceDesc".equals(sort)) {
            query.addSort(Sort.by(Sort.Direction.DESC, "price"));
        } else {
            query.addSort(Sort.by(Sort.Direction.ASC, "nextSessionDate"));
        }

        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        List<CourseDocument> courses = hits.stream()
                .map(SearchHit::getContent)
                .toList();

        return new SearchResult(hits.getTotalHits(), courses);
    }

    private void validateSearchParameters(Integer minAge, Integer maxAge, Double minPrice, Double maxPrice, String sort) {
        if (minAge != null && maxAge != null && minAge > maxAge) {
            throw new IllegalArgumentException("minAge cannot be greater than maxAge");
        }
        if (minAge != null && minAge < 0) {
            throw new IllegalArgumentException("minAge cannot be negative");
        }
        if (maxAge != null && maxAge < 0) {
            throw new IllegalArgumentException("maxAge cannot be negative");
        }

        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            throw new IllegalArgumentException("minPrice cannot be greater than maxPrice");
        }
        if (minPrice != null && minPrice < 0) {
            throw new IllegalArgumentException("minPrice cannot be negative");
        }
        if (maxPrice != null && maxPrice < 0) {
            throw new IllegalArgumentException("maxPrice cannot be negative");
        }

        if (sort != null && !sort.matches("^(priceAsc|priceDesc|nextSessionDate)$")) {
            throw new IllegalArgumentException("Invalid sort parameter. Must be one of: priceAsc, priceDesc, nextSessionDate");
        }
    }
}
