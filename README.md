# URL Shortener

<p align="center">
  <strong>Scalable RESTful URL Shortening Backend</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/PostgreSQL-Database-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis">
  <img src="https://img.shields.io/badge/Bucket4j-Rate%20Limiting-6C63FF?style=for-the-badge" alt="Bucket4j">
  <img src="https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven">
</p>

<p align="center">
  A backend service that converts long URLs into compact, unique short links with persistent storage, redirect handling, click analytics, Redis caching, URL validation, and IP-based rate limiting.
</p>

---

## Demo

The API can be tested using tools such as **Postman**, **curl**, or any REST client.

### Example Flow

```text
Long URL
   │
   ▼
POST /api/shorten
   │
   ▼
URL Validation
   │
   ▼
Generate / Reuse Short Code
   │
   ▼
PostgreSQL
   │
   ▼
Short URL
   │
   ▼
GET /{shortCode}
   │
   ▼
Redis Cache / PostgreSQL
   │
   ▼
302 Redirect
   │
   ▼
Original URL
```

---

## Overview

**URL Shortener** is a Spring Boot backend service designed to convert long URLs into short, shareable links.

The application accepts an original URL, validates and normalizes it, generates a unique six-character short code, stores the mapping in PostgreSQL, and returns a short URL.

When a user accesses the generated short code, the service resolves the original URL and responds with an HTTP `302 Found` redirect.

The project also includes backend features beyond basic URL shortening:

* URL normalization and validation
* Unique short-code generation
* Duplicate URL detection
* PostgreSQL persistence using Spring Data JPA
* Redis-backed caching
* Click-count tracking
* Last-access tracking
* Per-click IP address logging
* Analytics endpoint
* IP-based rate limiting using Bucket4j
* Global exception handling
* DTO-based API responses
* Maven-based build and dependency management

The current implementation follows a layered Spring architecture:

```text
Client
  │
  ▼
Controller
  │
  ▼
Service
  │
  ├───────────────┐
  ▼               ▼
Repository       Redis
  │               │
  ▼               │
PostgreSQL ◄──────┘
```

---

## Features

### URL Shortening

* Convert long URLs into short links.
* Generate six-character short codes.
* Ensure short-code uniqueness.
* Reuse an existing short code when the same normalized URL already exists.

The service checks whether the original URL already exists before generating a new code.

### URL Redirection

Accessing:

```http
GET /{shortCode}
```

resolves the stored URL and returns an HTTP `302` redirect to the original destination.

### URL Validation

URLs are:

* Trimmed.
* Normalized.
* Automatically prefixed with `https://` when no HTTP scheme is supplied.
* Validated using Java's URL/URI parsing.

Invalid URLs result in an application exception rather than being stored.

### Click Analytics

The system tracks:

* Total click count
* Creation timestamp
* Last-access timestamp
* Individual click records
* IP address associated with each recorded click

The analytics endpoint exposes aggregated information for a short code.

### Redis Caching

Short-code lookups use Spring Cache backed by Redis.

The application configures a `RedisCacheManager`, while the URL service uses cache annotations for URL retrieval and cache updates.

### Rate Limiting

The shorten endpoint uses **Bucket4j** to apply per-IP rate limiting.

The current configuration permits:

```text
10 requests
per IP
per 1 minute
```

Requests exceeding the configured bucket return HTTP `429 Too Many Requests`.

### Persistent Storage

URL mappings are persisted using:

* Spring Data JPA
* Hibernate
* PostgreSQL

The `Url` entity stores the original URL, short code, click count, creation time, and last-access time.

### Layered Architecture

The codebase separates responsibilities across:

* Controllers
* DTOs
* Services
* Repositories
* Models
* Utilities
* Configuration
* Exception handling

This keeps API handling, business logic, persistence, caching, and infrastructure concerns separated.

---

## Tech Stack

### Backend

| Technology        | Usage                        |
| ----------------- | ---------------------------- |
| Java 17           | Primary programming language |
| Spring Boot 4.0.5 | Backend framework            |
| Spring Web MVC    | REST API                     |
| Spring Data JPA   | Persistence abstraction      |
| Hibernate         | ORM                          |
| Lombok            | Boilerplate reduction        |

The project is configured for Java 17 and Spring Boot 4.0.5.

### Database

| Technology | Usage                         |
| ---------- | ----------------------------- |
| PostgreSQL | Persistent relational storage |
| JPA        | ORM abstraction               |
| Hibernate  | Entity persistence            |

### Caching

| Technology        | Usage                      |
| ----------------- | -------------------------- |
| Redis             | URL lookup caching         |
| Spring Cache      | Cache abstraction          |
| RedisCacheManager | Redis cache implementation |

### Rate Limiting

| Technology        | Usage                      |
| ----------------- | -------------------------- |
| Bucket4j 8.1.0    | Token-bucket rate limiting |
| ConcurrentHashMap | Per-IP bucket storage      |

### Build & Development

| Technology            | Usage                           |
| --------------------- | ------------------------------- |
| Maven                 | Build and dependency management |
| Maven Wrapper         | Reproducible Maven execution    |
| Git                   | Version control                 |
| GitHub                | Repository hosting              |
| Postman / REST Client | API testing                     |

---

## Architecture

The project follows a **layered Spring Boot architecture**.

```text
                         ┌────────────────────┐
                         │       Client       │
                         │ Postman / Browser  │
                         └─────────┬──────────┘
                                   │
                                   ▼
                         ┌────────────────────┐
                         │   UrlController    │
                         │    REST Layer      │
                         └─────────┬──────────┘
                                   │
                                   ▼
                         ┌────────────────────┐
                         │     UrlService     │
                         │  Business Logic    │
                         └──────┬─────┬───────┘
                                │     │
                     ┌──────────┘     └──────────┐
                     ▼                           ▼
             ┌───────────────┐           ┌──────────────┐
             │ UrlRepository │           │ Redis Cache  │
             └───────┬───────┘           └──────────────┘
                     │
                     ▼
             ┌───────────────┐
             │  PostgreSQL   │
             └───────────────┘
```

### Request Flow — Create Short URL

```text
POST /api/shorten
        │
        ▼
UrlController
        │
        ├── Get client IP
        │
        ├── Resolve rate-limit bucket
        │
        └── Consume request token
                │
                ▼
           UrlService
                │
                ├── Normalize URL
                │
                ├── Validate URL
                │
                ├── Check existing URL
                │
                ├── Generate short code
                │
                └── Persist mapping
                        │
                        ▼
                   PostgreSQL
                        │
                        ▼
                   UrlResponse
```

### Request Flow — Redirect

```text
GET /{shortCode}
        │
        ▼
UrlController
        │
        ▼
UrlService
        │
        ├── Find URL
        │
        ├── Update click count
        │
        ├── Update last accessed
        │
        ├── Record IP address
        │
        └── Update cache
                │
                ▼
          HTTP 302 Redirect
                │
                ▼
         Original URL
```

### Request Flow — Analytics

```text
GET /api/analytics/{shortCode}
        │
        ▼
UrlController
        │
        ▼
UrlService
        │
        ▼
Cached / Persistent URL
        │
        ▼
AnalyticsResponse
```

---

## API / Database

### Base URL

When running locally:

```text
http://localhost:8080
```

---

### 1. Create Short URL

```http
POST /api/shorten
```

#### Request

```json
{
  "originalUrl": "https://example.com/very/long/url"
}
```

#### Response

```json
{
  "shortUrl": "http://localhost:8080/abc123"
}
```

The current controller accepts a `UrlRequest`, applies IP-based rate limiting, delegates URL creation to `UrlService`, and returns a `UrlResponse`.

---

### 2. Redirect to Original URL

```http
GET /{shortCode}
```

Example:

```http
GET /abc123
```

Response:

```text
HTTP 302 Found
Location: https://example.com/very/long/url
```

The service also updates click statistics and stores the requesting IP address.

---

### 3. URL Analytics

```http
GET /api/analytics/{shortCode}
```

Example:

```http
GET /api/analytics/abc123
```

Example response:

```json
{
  "originalUrl": "https://example.com/very/long/url",
  "shortCode": "abc123",
  "clickCount": 12,
  "createdAt": "2026-08-15T18:30:00",
  "lastAccessed": "2026-08-15T20:15:00"
}
```

The controller constructs an `AnalyticsResponse` containing the original URL, short code, click count, creation time, and last-access time.

---

### 4. Rate Limiting

The shorten endpoint is rate limited per client IP.

Current configuration:

```text
Limit:    10 requests
Window:   1 minute
Strategy: Token bucket
Scope:    Client IP
```

Exceeding the limit returns:

```text
HTTP 429 Too Many Requests
```

---

### Database Schema

The main URL mapping entity contains:

| Field          | Type          | Description                  |
| -------------- | ------------- | ---------------------------- |
| `id`           | Long          | Primary key                  |
| `originalUrl`  | String        | Original destination URL     |
| `shortCode`    | String        | Unique generated identifier  |
| `clickCount`   | int           | Number of recorded redirects |
| `createdAt`    | LocalDateTime | URL creation timestamp       |
| `lastAccessed` | LocalDateTime | Most recent access           |

The `shortCode` column is configured as unique.

### Click Tracking Table

Individual click events are represented by `UrlClick`:

| Field       | Type          | Description              |
| ----------- | ------------- | ------------------------ |
| `id`        | Long          | Primary key              |
| `ipAddress` | String        | Client IP                |
| `timestamp` | LocalDateTime | Click timestamp          |
| `url`       | Relationship  | Associated shortened URL |

Each click is associated with its corresponding `Url` entity using a JPA `ManyToOne` relationship.

---

## Project Structure

```text
URLShortener/
│
├── .mvn/
│   └── wrapper/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── aishwary/
│   │   │           └── URLShortener/
│   │   │               │
│   │   │               ├── config/
│   │   │               │   └── RedisConfig.java
│   │   │               │
│   │   │               ├── controller/
│   │   │               │   └── UrlController.java
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   ├── AnalyticsResponse.java
│   │   │               │   ├── UrlRequest.java
│   │   │               │   └── UrlResponse.java
│   │   │               │
│   │   │               ├── exception/
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   └── UrlNotFoundException.java
│   │   │               │
│   │   │               ├── model/
│   │   │               │   ├── Url.java
│   │   │               │   └── UrlClick.java
│   │   │               │
│   │   │               ├── repository/
│   │   │               │   ├── UrlClickRepository.java
│   │   │               │   └── UrlRepository.java
│   │   │               │
│   │   │               ├── service/
│   │   │               │   ├── RateLimitService.java
│   │   │               │   └── UrlService.java
│   │   │               │
│   │   │               ├── util/
│   │   │               │   └── ShortCodeGenerator.java
│   │   │               │
│   │   │               └── UrlShortenerApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── aishwary/
│                   └── URLShortener/
│                       └── UrlShortenerApplicationTests.java
│
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

The repository currently separates controllers, DTOs, exceptions, models, repositories, services, utilities, and configuration into dedicated packages.

### Package Responsibilities

| Package      | Responsibility                 |
| ------------ | ------------------------------ |
| `controller` | REST API endpoints             |
| `service`    | Business logic                 |
| `repository` | Database access                |
| `model`      | JPA entities                   |
| `dto`        | API request/response objects   |
| `config`     | Infrastructure configuration   |
| `exception`  | Application exception handling |
| `util`       | Utility functions              |
| `test`       | Application tests              |

---

## Setup

### Prerequisites

Install:

* Java 17
* Maven or use the included Maven Wrapper
* PostgreSQL
* Redis
* Git

The Maven project is configured for Java 17 and Spring Boot 4.0.5.

---

### 1. Clone the Repository

```bash
git clone https://github.com/aishwaryguptadz/URLShortener.git
cd URLShortener
```

---

### 2. Create PostgreSQL Database

Create a database named:

```sql
CREATE DATABASE urlshortener;
```

---

### 3. Configure PostgreSQL

Update:

```text
src/main/resources/application.properties
```

Use environment-specific credentials rather than committing passwords:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

> **Security:** Do not commit database credentials to Git. The current repository contains a plaintext database credential in `application.properties`; rotate that credential and replace it with environment variables or a secrets manager before deploying or sharing the repository.

---

### 4. Start Redis

Make sure Redis is running locally:

```text
localhost:6379
```

The application currently expects:

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

---

### 5. Build the Project

Using the Maven Wrapper:

#### Windows

```bash
mvnw.cmd clean install
```

#### Linux / macOS

```bash
./mvnw clean install
```

---

### 6. Run the Application

#### Windows

```bash
mvnw.cmd spring-boot:run
```

#### Linux / macOS

```bash
./mvnw spring-boot:run
```

The server will be available at:

```text
http://localhost:8080
```

---

### 7. Test the API

Create a short URL:

```bash
curl -X POST http://localhost:8080/api/shorten \
  -H "Content-Type: application/json" \
  -d "{\"originalUrl\":\"https://example.com\"}"
```

Then use the returned short code:

```bash
curl -i http://localhost:8080/<shortCode>
```

For analytics:

```bash
curl http://localhost:8080/api/analytics/<shortCode>
```

---

## My Role

I designed and implemented this project as a **Java backend / Spring Boot project**, with a focus on REST API development, persistence, caching, analytics, and backend scalability concepts.

### Backend Development

* Designed the RESTful URL-shortening service.
* Implemented URL creation and redirection endpoints.
* Implemented URL normalization and validation.
* Implemented unique short-code generation.
* Added duplicate URL detection.
* Designed service-layer business logic.
* Implemented DTO-based request/response handling.
* Implemented exception handling.

### Database / Persistence

* Designed the PostgreSQL persistence layer.
* Created JPA entities for shortened URLs and click events.
* Implemented Spring Data repositories.
* Added unique constraints for short codes.
* Implemented URL and click persistence.
* Tracked creation and last-access timestamps.

### Caching

* Integrated Redis with Spring Cache.
* Configured `RedisCacheManager`.
* Added caching for short-code URL lookups.
* Updated cache state when click statistics change.

### Rate Limiting

* Integrated Bucket4j.
* Implemented IP-based request buckets.
* Configured a token-bucket rate limit of 10 requests per minute.
* Added HTTP `429` handling for excessive requests.

### Analytics

* Implemented click-count tracking.
* Recorded last-access timestamps.
* Stored individual click events.
* Captured client IP addresses.
* Added an analytics endpoint for short URLs.

### Architecture

The resulting architecture separates:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL

Service
    ↕
Redis

Controller
    ↓
RateLimitService
    ↓
Bucket4j
```

This project was specifically built to strengthen practical understanding of **Core Java, Spring Boot, REST APIs, Spring Data JPA, PostgreSQL, Redis, caching, rate limiting, and backend system design**.

---

## Future Improvements

### Security

* [ ] Move all secrets to environment variables.
* [ ] Add Spring Security.
* [ ] Add authentication and authorization.
* [ ] Add API keys or OAuth2.
* [ ] Add stronger URL validation.
* [ ] Add SSRF protection.
* [ ] Sanitize and validate all external URL inputs.

### Analytics

* [ ] Add geographic analytics.
* [ ] Add browser/user-agent tracking.
* [ ] Add referrer tracking.
* [ ] Add daily/monthly click statistics.
* [ ] Add analytics dashboard.
* [ ] Add click-through trends.

### URL Management

* [ ] Add URL expiration.
* [ ] Add custom aliases.
* [ ] Add user-owned URLs.
* [ ] Add URL deletion.
* [ ] Add URL editing.
* [ ] Add maximum URL lifetime policies.

### Scalability

* [ ] Replace in-memory rate-limit buckets with distributed rate limiting.
* [ ] Use Redis for distributed rate-limit state.
* [ ] Add connection-pool tuning.
* [ ] Add database indexing.
* [ ] Add asynchronous analytics processing.
* [ ] Add message queues for high-volume click events.
* [ ] Introduce distributed short-code generation.

### Infrastructure

* [ ] Add Dockerfile.
* [ ] Add Docker Compose for application + PostgreSQL + Redis.
* [ ] Add CI/CD using GitHub Actions.
* [ ] Add production configuration profiles.
* [ ] Add centralized logging.
* [ ] Add metrics and monitoring.
* [ ] Add health checks.
* [ ] Add OpenTelemetry tracing.

### Testing

* [ ] Add controller tests.
* [ ] Add service-layer unit tests.
* [ ] Add repository integration tests.
* [ ] Add Redis integration tests.
* [ ] Add rate-limit tests.
* [ ] Add end-to-end API tests.
* [ ] Add Testcontainers for PostgreSQL and Redis.

### System Design

* [ ] Design a distributed ID-generation strategy.
* [ ] Evaluate Base62 encoding for short-code generation.
* [ ] Introduce read replicas for large-scale workloads.
* [ ] Introduce a distributed cache strategy.
* [ ] Design horizontal scaling for API instances.
* [ ] Add load balancing.
* [ ] Design eventual-consistency boundaries for analytics.

---

## License

This project is licensed under the **MIT License**.

See the repository's `LICENSE` configuration/documentation for the applicable license terms.

---

<p align="center">
  <strong>URL Shortener</strong>
</p>

<p align="center">
  Java • Spring Boot • PostgreSQL • Redis • Bucket4j • REST API
</p>

<p align="center">
  Built as a backend engineering project focused on scalable URL shortening and modern Spring architecture.
</p>
