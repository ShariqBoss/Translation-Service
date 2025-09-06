# Translation Management Service

### Version
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- IDE with Lombok plugin

### Running with H2 Database (Development)

```bash
# Clone and build
git clone <repository>
mvn clean package

# Run application
java -jar target/translation-service-1.0.0.jar
```


# The application will be available at http://localhost:8080


## API Documentation

Once running, access the API documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## Authentication

### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'
```

### Login and Get Token
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'
```

## API Usage Examples

### Create Translation
```bash
curl -X POST http://localhost:8080/api/translations/create \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "key": "button.save",
    "locale": "en",
    "content": "Save",
    "tags": ["mobile", "web"]
  }'
```

### Search Translations
```bash
curl "http://localhost:8080/api/translations/search?key=button&locale=en&page=0&size=10" \
  -H "Authorization: Bearer <token>"
```

### Export Translations for Frontend
```bash
curl "http://localhost:8080/api/translations/export/en" \
  -H "Authorization: Bearer <token>"
```

### Populate Test Data
```bash
curl -X POST "http://localhost:8080/api/populate/translations?count=100000"
```

## Performance Testing
 **Populate Database**: Use the `/api/populate/translations` endpoint to create 100k+ test records

## Database Schema

### Key Tables
- `translations`: Core translation data with indexes
- `tags`: Reusable tags for categorization
- `translation_tags`: Many-to-many relationship table
- `users`: Authentication data

### Security Features
- **JWT Authentication**: Stateless token-based security
- **Password Encryption**: BCrypt password hashing
- **Input Validation**: Comprehensive request validation
- **SQL Injection Prevention**: Parameterized queries
- **Lombok Integration**: Reduced boilerplate code with compile-time generation

## Testing

### Run All Tests
```bash
mvn test
```

### Generate Coverage Report
```bash
mvn jacoco:report
```

## Configuration

### Application Properties
Key configuration options in `application.yml`:

```yaml
# Database configuration for H2 Database
spring.datasource.url: jdbc:h2:mem:translation_db
spring.jpa.hibernate.ddl-auto: create-drop
spring.datasource.username: admin
spring.datasource.password: admin1234

port: 8080

# JWT configuration
jwt.secret: mySecretKey123456789012345678901234567890
jwt.expiration: 86400000

# Performance tuning
spring.jpa.properties.hibernate.jdbc.batch_size: 50

#logging configuration
logging:
  level:
    com.translation: INFO
    org.springframework.security: DEBUG
```
