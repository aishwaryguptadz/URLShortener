# URL Shortener

A scalable backend service that converts long URLs into short, shareable links.
This project demonstrates how to build a URL shortening service similar to Bitly or TinyURL using modern backend architecture.

The system generates a unique short code for each URL and redirects users to the original destination when the short link is accessed. 

---

## Features
- Create short URLs from long URLs
- Redirect users to the original URL
- Generate unique short codes
- Track URL creation timestamps
- RESTful API design
- URL validation
- Scalable architecture
- Simple analytics support (optional)

---

## Tech Stack

### Backend
- Java
- Spring Boot
- REST APIs

### Database
- PostgreSQL

### Tools
- Maven
- Docker
- Postman for API testing
- Git & GitHub

---

## System Architecture

Client -> API Server -> URL Service -> Database

Flow:
1. User submits a long URL.
2. Server generates a unique short code.
3. Mapping of short code -> original URL is stored in the database.
4. When the short URL is accessed, the system redirects to the original URL.

---

## Example

Original URL:

https://example.com/articles/how-to-build-a-scalable-backend-system

Shortened URL:

https://yourdomain.com/aB3xYz

---

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/aishwaryguptadz/URLShortener
```
### 2. Navigate to Project
```bash
cd URLShortener
```
### 3. Configure Database
Update database configuration in:

src/main/resources/application.properties

Example:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/urlshortener
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## Running the Application

Using Maven:
```bash
mvnw spring-boot:run
```
Or build and run:
```bash
mvnw clean install
java -jar target/url-shortener.jar
```
Server runs at:

http://localhost:8080

---

## API Endpoints

### Create Short URL
POST /api/url/shorten

Request Body
```json
{
  "url":"https://example.com/very/long/url"
}
```

Response

```json
{
  "shortUrl":"http://localhost:8080/aB3xYz"
}
```

---

## Redirect to Original URL

GET /{shortCode}

Example

GET /aB3xYz

Redirects to

https://example.com/very/long/url

---

## Database Schema
### URL Mapping Table

### Field          Type          Description
id             Long          Primary Key

original_key   Text          Original URL

short_code String Generate short code

created_at Timestamp Creation time

---

## Future Improvements
- URL expiration support
- Click analytics
- Custom short URLs
- Rate limiting
- Distributed ID generation
- Caching with Redis
- QR code generation

---

## Testing

You can test APIs using Postman.

Example workflow:
1. Send POST request to create a short URL
2. Copy the generated short URL
3. Open the short URL in a browser to test redirection

---

## License
This project is licensed under the MIT License.

---

## Author
Aishwary Kumar

B.Tech. CSE (AI)

Pranveer Singh Institute of Technology
