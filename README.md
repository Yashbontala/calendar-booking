# Calendar Booking 

## Summary
Simple calendar booking system (availability rules, search available 60-min slots, book appointment). Spring Boot + Java, in-memory repository for demo, tests included.

## Requirements
- Java 21 (OpenJDK)
- Maven 3.9+
- (Optional) Postman for manual testing

## How to run locally

1. Build:
```bash
mvn clean package
`````

2. Run:
```bash
mvn spring-boot:run
`````

## Swagger UI
```bash
(http://localhost:8080/swagger-ui/index.html)
`````
All API Details are mentioned in swagger with examples

## Running Tests
```bash
mvn test
`````

## Design & Assumptions

1.Time slots are always 60 minutes long.

2.Availabilities are per-date (a date cannot have overlapping times).

3.No authentication is implemented — assumed the owner is authenticated.

4.In-memory repository (no persistence). Can be replaced with a DB by implementing BookingRepository.




