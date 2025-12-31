# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Maven Commands
- **Build project**: `mvn clean compile`
- **Run tests**: `mvn test`
- **Package application**: `mvn clean package`
- **Run application**: `mvn spring-boot:run`
- **Build skipping tests**: `mvn clean package -DskipTests`
- **Run single test class**: `mvn test -Dtest=ClassName`
- **Run single test method**: `mvn test -Dtest=ClassName#methodName`

### Running the Application
- **Default port**: 8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Database**: MySQL (configured for remote server 162.43.92.30:3306)
- **Alternative H2 setup**: Commented configuration available in application.yml

### Testing Commands
- **Run all tests**: `mvn test`
- **Run integration tests**: `mvn test -Dtest="*IntegrationTest"`
- **Run with test profile**: `mvn test -Dspring.profiles.active=test`
- **Run specific test package**: `mvn test -Dtest="com.casemgr.controller.*"`
- **Run tests with coverage**: `mvn clean test jacoco:report` (if Jacoco is configured)

## Architecture Overview

This is a Spring Boot 3.x application implementing a case management/marketplace platform with the following core architecture:

### Layered Architecture Pattern
```
Controllers → Services → Repositories → Entities
     ↓           ↓           ↓           ↓
  REST APIs → Business → Data Access → Database
```

### Key Packages and Responsibilities
- **`controller/`**: REST API endpoints, organized by domain (Admin*, Auth, Order, User, etc.)
- **`service/` + `service/impl/`**: Business logic, following interface-implementation pattern
- **`repository/`**: Spring Data JPA repositories with custom queries
- **`entity/`**: JPA entities, inheriting from `BaseEntity` for auditing (createTime/updateTime)
- **`converter/`**: MapStruct-based DTO converters for entity ↔ response mapping
- **`request/` + `response/`**: API contract-specific DTOs
- **`enumtype/`**: Type-safe enums for business constants
- **`config/`**: Spring configuration classes (security, WebSocket, OpenAPI)
- **`security/`**: JWT authentication and authorization
- **`utils/`**: Utility classes and helper methods

### Core Business Domains
- **Users**: Providers and clients with role-based access
- **Orders**: Central business entity with bidding, contracts, delivery
- **Industries**: Categorization and scope management
- **Revenue Sharing**: Commission and settlement system
- **Certifications**: User qualification management
- **Showcase**: Portfolio and showcase functionality

## Development Patterns and Conventions

### Entity Design
- All entities inherit from `BaseEntity` for automatic auditing
- Soft delete pattern using `@SQLDelete` and `@Where` annotations
- JPA relationships properly mapped with consideration for lazy/eager loading

### Service Layer
- Interface-first design: `SomeService` interface + `SomeServiceImpl` implementation
- Service-level `@Transactional` boundaries
- Business logic encapsulation with proper exception handling

### API Design
- All endpoints use Request/Response DTOs
- Entity-DTO conversion using MapStruct converters
- Global exception handling via `@ControllerAdvice`
- Swagger/OpenAPI documentation enabled

### Security Implementation
- JWT-based authentication with custom filters
- Role-based authorization (ADMIN, USER roles)
- Method-level security enabled
- CORS configuration for frontend integration

### Database Schema
- Spring Data JPA with Hibernate
- Custom repository methods using `@Query`
- Pagination support for large datasets
- MySQL primary database, H2 alternative configuration

### File Storage
- File uploads stored in `/fileStorage/` directory organized by date
- Unique filename generation using UUIDs
- Support for unlimited file sizes (configured in application.yml)

## API Testing

### Postman Collections
- Complete test collections available in `/postman/` directory
- **P0 Priority Tests**: Critical authentication and payment functionality
- Automated test scripts for validation with curl command examples
- Environment variables for different configurations
- Comprehensive test coverage for all endpoints

### Available Test Collections
- **Auth Controller (P0)**: Complete authentication flow testing including security validation
- **Payment Controller (P0)**: Payment processing and transaction testing  
- **Industries API**: 9 endpoints with multilingual support
- **Admin User Controller**: Full CRUD operations with role-based testing

### Running Postman Tests
```bash
# Manual testing with curl (test data files in project root)
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d @test-login.json
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d @test-sql-injection.json
```

## WebSocket Configuration

Real-time communication setup:
- **Endpoint**: `/ws`
- **Message destinations**: 
  - `/topic/*` for broadcast messages
  - `/queue/*` for point-to-point messages
- **STOMP protocol** with JSON message conversion

## Important Configuration Details

### Database
- Primary: MySQL at `162.43.92.30:3306/project`
- Hibernate DDL: `update` mode (auto-create/update schema)
- Connection pooling via Tomcat JDBC

### JWT Configuration
- Secret key configured in `application.yml`
- Token lifetime: 7200 seconds (2 hours)
- Custom authentication provider implementation

### Mail Configuration
- SMTP configured for `support@casedeep.com`
- SSL enabled on port 465
- Alternative Resend.com integration available

## Testing Strategy

- **Unit tests**: Standard JUnit 5 with Spring Boot Test
- **Integration tests**: Full context using `@SpringBootTest`
- **Security testing**: Spring Security Test support available
- **Test data**: Test profile configured for H2 database
- **Test profiles**: Use `@ActiveProfiles("test")` for test-specific configuration

## Key Dependencies and Tools

- **Spring Boot 3.0.6** with Jakarta EE
- **Java 17** (configured in Maven compiler plugin)
- **MapStruct 1.5.5** for DTO mapping
- **Lombok** for boilerplate reduction
- **JWT (JJWT) 0.11.5** for authentication
- **SpringDoc OpenAPI** for API documentation
- **MySQL Connector** for database access
- **Resend Java 4.1.1** for email services

## Development Workflow

1. **New Feature Development**:
   - Create/update entities as needed
   - Define repository interfaces
   - Implement service layer with interfaces
   - Create request/response DTOs
   - Add MapStruct converters
   - Implement controller endpoints
   - Add appropriate tests

2. **Database Changes**:
   - Entity modifications auto-update schema (ddl-auto: update)
   - For production, use explicit migration scripts

3. **Security Considerations**:
   - All admin endpoints require ADMIN role
   - Authentication endpoints need JWT tokens
   - Sensitive data should not be logged or exposed in responses

## Common Code Patterns

### Repository Pattern
```java
@Repository
public interface SomeEntityRepository extends JpaRepository<SomeEntity, Long> {
    @Query("SELECT e FROM SomeEntity e WHERE e.someField = :value")
    List<SomeEntity> findBySomeCustomCriteria(@Param("value") String value);
}
```

### Service Pattern
```java
@Service
@Transactional
public class SomeServiceImpl implements SomeService {
    // Business logic implementation
}
```

### Controller Pattern
```java
@RestController
@RequestMapping("/api/some-endpoint")
public class SomeController {
    // REST endpoint implementation
}
```

### DTO Conversion
```java
@Mapper(componentModel = "spring")
public interface SomeConverter {
    SomeResponse toResponse(SomeEntity entity);
}
```

## Scheduled Tasks

The application includes background processing via `@Scheduled` annotations:
- **RankingScoreUpdateTask**: Updates user ranking scores
- Configure task scheduling in application properties if needed

## Important Notes for Development

### Code Quality
- Follow existing code patterns and naming conventions
- Use MapStruct for all entity-DTO conversions
- Implement proper exception handling in service layers
- All new features should include comprehensive tests

### Authentication & Authorization
- Use `@PreAuthorize("hasRole('ADMIN')")` for admin-only endpoints
- JWT tokens are validated via `JWTAuthenticationFilter`
- Industry scope security implemented via `IndustryScopeSecurityUtil`

### Data Validation
- Use Bean Validation annotations in request DTOs
- Global exception handling via `GlobalExceptionHandler`
- Custom business exceptions extend `BusinessException`

### Performance Considerations
- Use pagination for large datasets via Spring Data's `Pageable`
- Lazy loading is default for JPA relationships
- Consider caching for frequently accessed read-only data

## Quick Reference for Common Tasks

### Adding a New Entity
1. Create entity in `/entity/` extending `BaseEntity`
2. Create repository interface in `/repository/`
3. Create service interface and implementation in `/service/` and `/service/impl/`
4. Create request/response DTOs in `/request/` and `/response/`
5. Add MapStruct converter in `/converter/`
6. Implement controller endpoints in `/controller/`
7. Add integration tests

### Debugging Common Issues
- **Authentication failures**: Check JWT token in `JWTAuthenticationFilter`
- **Database connection**: Verify MySQL connection at `162.43.92.30:3306`
- **CORS issues**: Frontend runs on `localhost:5173`, check CORS configuration
- **File upload issues**: Check `/fileStorage/` directory permissions and unlimited size configuration