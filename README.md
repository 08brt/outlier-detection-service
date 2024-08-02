# Machine Age Outlier Detection Service

## Description

The Machine Age Outlier Detection Service is a standalone REST microservice designed to validate a list of machine ages. The service identifies machines whose ages are unreasonably long compared to other machines in the request. It is intended for use by a manufacturing company with an inventory system that records machine IDs and machine ages. Additionally, the service handles values with misspelled age units by skipping them.

## Requirements

To run this project, you will need the following:

- Java 17 or higher

### Dependencies

The project uses several dependencies, managed via Maven. Key dependencies include:

- **Spring Boot Starter Web**: For building web applications, including RESTful applications using Spring MVC.
- **Spring Boot Starter Validation**: For using Java Bean Validation with Hibernate Validator.
- **Spring Boot Starter Test**: For testing Spring Boot applications with libraries like JUnit, Hamcrest, and Mockito.
- **Apache Commons Math3**: For mathematical operations and statistical calculations.
- **ModelMapper**: For object mapping.
- **Lombok**: To reduce boilerplate code for model objects like getters, setters, and constructors.

## Running the Application

1. **Setup and Build:**
   - Unzip the project.
   - Run `mvn clean install` to build the project.
   - Ensure your IDE is set to use Java 17 for the project.

2. **Run the Application Locally:**
   - Use the command `mvn spring-boot:run` or run the application from your IDE.

## Running Tests

The project includes automated tests that generate their own data. Each run generates a random number of machine records between 1 and 1000, including some with outliers.

1. **Run Tests:**
    - Execute `mvn test` to run all the tests.
    - The tests will generate random data and validate machine ages.
    - The results will indicate if any outliers were detected.

2. **Test Data:**
    - The test data is generated dynamically during each test run.
    - The number of records generated is random, ranging from 1 to 1000.
    - The tests ensure that outliers are correctly identified.

### API Endpoint

- **URL:** `s`
- **Method:** `POST`
- **Content-Type:** `application/json`
- **Request Body:** A JSON array of machine objects with the following fields:
   - `id` (Long): The ID of the machine (must be positive).
   - `age` (String): The age of the machine in the format `'<number> <time unit>'`, e.g., '1 year', '16 months'.
- **Optional Query Parameter:**
   - `threshold` (Double): A predefined limit for the Z-score calculation, must be between 1 and 10 (default 2).

#### Example Request:
```json
[
    { "id": 1, "age": "1 year" },
    { "id": 2, "age": "2 years" },
    { "id": 3, "age": "10000 years" }
]
