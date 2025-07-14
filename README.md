# Course Search API with Elasticsearch

A Spring Boot application that provides course search functionality using Elasticsearch with autocomplete and fuzzy search capabilities.

## Features

### Part A (Completed)
- ✅ Index sample course data on startup
- ✅ Search courses with multiple filters (keyword, category, type, age range, price range, start date)
- ✅ Sorting and pagination
- ✅ Swagger UI documentation
- ✅ Input validation
- ✅ Logging with SLF4J
- ✅ Global error handling

### Part B (New Features)
- ✅ **Autocomplete (Completion Suggester)**: `/api/search/suggest?q={partialTitle}`
- ✅ **Fuzzy Search Enhancement**: Enhanced `/api/search` with fuzzy matching on titles

### Testing & Quality Assurance
- ✅ **Comprehensive Unit Tests**: Full test coverage for both controller and service layers
- ✅ **Mockito Integration**: Proper mocking and stubbing for isolated testing
- ✅ **Edge Case Testing**: Tests for null values, empty results, and error scenarios
- ✅ **Parameter Validation Testing**: Tests for all search parameters and edge cases
- ✅ **Autocomplete Testing**: Tests for various query scenarios including empty and special characters

## API Endpoints

### 1. Search Courses
```
GET /api/search
```

**Parameters:**
- `q` (optional): Search keyword (supports fuzzy matching)
- `category` (optional): Course category
- `type` (optional): Course type
- `minAge` (optional): Minimum age
- `maxAge` (optional): Maximum age
- `minPrice` (optional): Minimum price
- `maxPrice` (optional): Maximum price
- `startDate` (optional): Start date (ISO format)
- `sort` (optional): Sort order (priceAsc, priceDesc, nextSessionDate)
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10, max: 100)

**Example:**
```bash
curl -X GET "http://localhost:8080/api/search?q=math&category=Science&minPrice=50&maxPrice=200&sort=priceAsc" \
  -H "accept: application/json"
```

### 2. Autocomplete Suggestions
```
GET /api/search/suggest
```

**Parameters:**
- `q` (required): Partial title to search for

**Example:**
```bash
curl -X GET "http://localhost:8080/api/search/suggest?q=math" \
  -H "accept: application/json"
```

**Response:**
```json
{
  "suggestions": ["Math for Beginners", "Advanced Mathematics", "Math Fundamentals"],
  "totalHits": 3
}
```

## Testing Examples

### Autocomplete Testing

```bash
# Test with "math" - should return courses starting with "math"
curl -X GET "http://localhost:8080/api/search/suggest?q=math" \
  -H "accept: application/json"

# Test with "science" - should return courses starting with "science"
curl -X GET "http://localhost:8080/api/search/suggest?q=science" \
  -H "accept: application/json"

# Test with "art" - should return courses starting with "art"
curl -X GET "http://localhost:8080/api/search/suggest?q=art" \
  -H "accept: application/json"

# Test with empty query - should return empty results
curl -X GET "http://localhost:8080/api/search/suggest?q=" \
  -H "accept: application/json"
```

### Fuzzy Search Testing

```bash
# Test fuzzy search with typo "dinors" - should match "Dinosaurs 101"
curl -X GET "http://localhost:8080/api/search?q=dinors" \
  -H "accept: application/json"

# Test fuzzy search with "math" - should match "Mathematics" courses
curl -X GET "http://localhost:8080/api/search?q=math" \
  -H "accept: application/json"

# Test fuzzy search with "scienc" - should match "Science" courses
curl -X GET "http://localhost:8080/api/search?q=scienc" \
  -H "accept: application/json"

# Test fuzzy search with "art" - should match "Art" courses
curl -X GET "http://localhost:8080/api/search?q=art" \
  -H "accept: application/json"
```

### Combined Search Testing

```bash
# Search with fuzzy matching and filters
curl -X GET "http://localhost:8080/api/search?q=math&category=Science&minPrice=50&sort=priceAsc" \
  -H "accept: application/json"

# Search with autocomplete and then use results in main search
curl -X GET "http://localhost:8080/api/search/suggest?q=math" \
  -H "accept: application/json"

# Use one of the suggestions in the main search
curl -X GET "http://localhost:8080/api/search?q=Math%20for%20Beginners&category=Science" \
  -H "accept: application/json"
```

## Technical Implementation

### Autocomplete (Completion Suggester)
- Added `titleSuggest` field to `CourseDocument` with `@CompletionField`
- Created `SuggestResult` DTO for response structure
- Implemented `suggest()` method in `SearchService` using prefix search
- Added `/api/search/suggest` endpoint in `SearchController`
- Updated `CourseIndexService` to populate `titleSuggest` field during indexing

### Fuzzy Search Enhancement
- Enhanced existing search method in `SearchService`
- Added fuzzy matching on title field using `Criteria.fuzzy()`
- Maintains existing exact match and description search
- Fuzzy matching works alongside all existing filters

## Running the Application

1. **Start Elasticsearch** (using Docker):
```bash
docker-compose up -d
```

2. **Start the Spring Boot application**:
```bash
cd course-search
./mvnw spring-boot:run
```

The courses are reindexed everytime the springboot application runs, so you don't have to separately run the cURL command for that (lines 28,29 in CourseIndexSerivce.java).

If at all you don't want this you can comment out the lines, or just comment out the method call in the entry point file.

You can also manually index objects onto ES using cURL, but that requires adding a mapping and formatting the json file. I am not explaining that process since it is too cumbersome. Recommended that you let the data un-index and re-index (better not to comment out the code mentioned).



3. **Access Swagger UI**:
```
http://localhost:8080/swagger-ui.html
```

4. **Test the endpoints** using the cURL examples above

## Data Structure

The application indexes course data with the following structure:
- `id`: Unique identifier
- `title`: Course title (supports fuzzy search and autocomplete)
- `description`: Course description
- `category`: Course category
- `type`: Course type
- `minAge`/`maxAge`: Age range
- `price`: Course price
- `nextSessionDate`: Next session date
- `titleSuggest`: Autocomplete field (populated with title)

## Error Handling

- Input validation for all parameters
- Global exception handler for consistent error responses
- Proper logging for debugging and monitoring
- Graceful handling of empty or invalid queries 

## Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run tests with detailed output
mvn test -Dtest=SearchControllerTest

# Run specific test class
mvn test -Dtest=SearchServiceTest
```

### Test Coverage

The application includes comprehensive test coverage:

#### Controller Tests (`SearchControllerTest`)
- ✅ Search with all parameters
- ✅ Search with only keyword
- ✅ Search with no parameters
- ✅ Search when service returns null
- ✅ Search with custom pagination
- ✅ Search with different sort options
- ✅ Autocomplete with valid query
- ✅ Autocomplete with empty query
- ✅ Autocomplete with single character
- ✅ Autocomplete with long query
- ✅ Autocomplete with special characters

#### Service Tests (`SearchServiceTest`)
- ✅ Basic search functionality
- ✅ Search with filters
- ✅ Search with pagination
- ✅ Search with sorting
- ✅ Autocomplete functionality
- ✅ Edge cases and error handling

### Test Examples

```bash
# Run controller tests
mvn test -Dtest=SearchControllerTest

# Run service tests  
mvn test -Dtest=SearchServiceTest

# Run all tests with coverage report
mvn clean test jacoco:report
``` 