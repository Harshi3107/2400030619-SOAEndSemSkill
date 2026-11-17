# JWT Authentication Service

A beginner-friendly Spring Boot REST service that creates HS256-signed JWTs from an already-authenticated user's data. It has no database and does not authenticate users itself.

> **Trust boundary:** `POST /api/jwt/generate` is intentionally public for this assignment. A caller can submit arbitrary identity data and receive a token; the endpoint does not prove that the caller authenticated that user. In production, protect it so only a trusted identity/authentication service can request token issuance, and validate the trusted caller and its authorization.

## Architecture

The request passes through the controller, service, and JWT utility. Configuration supplies the signing key and security policy; DTOs define input/output; centralized exception handling formats errors.

```text
HTTP request -> JwtController -> JwtService -> JwtUtil -> signed JWT
                    |                |           |
               UserRequest       JwtResponse   HS256 key
```

## Technologies

- Java 17 and Spring Boot 3.4.5
- Spring Web, Spring Security 6, Bean Validation
- JJWT 0.12.6 (API plus runtime implementation and Jackson support)
- Springdoc OpenAPI 2.8.6 / Swagger UI
- Maven and embedded Tomcat

## Project structure

```text
jwt-authentication-service/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/example/jwtservice/
│   │   │   ├── JwtAuthenticationServiceApplication.java
│   │   │   ├── config/       # JWT key, stateless security, OpenAPI
│   │   │   ├── controller/   # REST endpoint
│   │   │   ├── dto/          # Validated request and response
│   │   │   ├── exception/    # JWT exception and JSON error handlers
│   │   │   ├── service/      # Token issuance business logic
│   │   │   └── util/         # JWT creation, parsing, and validation
│   │   └── resources/application.properties
│   └── test/java/com/example/jwtservice/
│       ├── service/JwtServiceTest.java
│       └── util/JwtUtilTest.java
└── README.md
```

### Main classes

- `JwtAuthenticationServiceApplication` starts Spring Boot.
- `JwtConfig` reads and validates the Base64 secret; HS256 requires at least 32 decoded bytes.
- `SecurityConfig` disables CSRF, configures stateless sessions, permits API docs, and exposes the assignment endpoint.
- `JwtController` validates the request and serves `POST /api/jwt/generate`.
- `JwtService` coordinates generation and forms the response.
- `JwtUtil` signs and parses JWTs and validates their signature, issuer, and expiration.
- `UserRequest` validates the submitted identity fields; `JwtResponse` carries the token details.
- `GlobalExceptionHandler` returns JSON errors for validation, malformed JSON, and server failures.

## JWT and claims

A JSON Web Token has a header, payload, and signature, encoded as three Base64URL sections separated by dots. The signature lets a verifier with the configured key detect changes to the token; the payload is encoded, **not encrypted**, so do not put secrets in claims.

This service includes:

| Claim | Meaning |
|---|---|
| `sub` | Username |
| `userId` | Application user identifier |
| `email` | User email address |
| `role` | User role |
| `iss` | Configured issuer |
| `iat` | Issue time |
| `exp` | Expiration time |

Tokens expire after one hour by default. `JwtUtil.validateToken` returns `false` for malformed, expired, wrong-issuer, or invalid-signature tokens; claim extraction throws a JJWT parsing exception for invalid tokens.

## Configuration and signing key

The service listens on port `8081`. The properties use environment-variable overrides:

```properties
server.port=8081
spring.application.name=jwt-authentication-service
jwt.secret=${JWT_SECRET:89cqUE3Q3flcN8MdBadF8otUL6UBUxG2ySzieDOEOvk=}
jwt.expiration=${JWT_EXPIRATION:3600000}
jwt.issuer=${JWT_ISSUER:jwt-authentication-service}
```

The committed default is a randomly generated, local-learning-only key. It is public and must never be treated as a production secret. Before any shared or production use, set `JWT_SECRET` to your own randomly generated Base64 value that decodes to at least 32 bytes. In PowerShell, generate one with:

```powershell
$bytes = [byte[]]::new(32)
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

Set it in the same terminal before starting the application. Do not commit real secrets. Other optional overrides are `JWT_EXPIRATION` (milliseconds) and `JWT_ISSUER`.

## API

### `POST /api/jwt/generate`

Content type: `application/json`. All fields are required; `userId` must be positive, `email` must be valid, and `role` must use uppercase letters, digits, or underscores.

Request:

```json
{
  "userId": 101,
  "username": "Harshi",
  "email": "harshi@example.com",
  "role": "USER"
}
```

Successful response (`200 OK`):

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

Invalid input or malformed JSON returns `400 Bad Request` with a JSON `error` field. Validation errors also include field-level `details`. Generation failures return `500 Internal Server Error`.

### Stateless security

Spring Security uses `SessionCreationPolicy.STATELESS`; it does not create or use an HTTP session to remember a caller between requests. CSRF is disabled for this stateless JSON API. The Swagger UI and API docs are permitted, as is the assignment's token endpoint. Other routes require authentication. No authentication filter is installed here because the project only issues tokens and has no protected business API.

## Maven setup and commands

Install a JDK 17 (JDK 21 also runs this Java 17-targeted project) and Maven 3.9 or later, then open a terminal in the project directory.

```powershell
mvn clean test
mvn clean package
mvn spring-boot:run
```

Alternatively, after packaging:

```powershell
java -jar target\jwt-authentication-service-0.0.1-SNAPSHOT.jar
```

To install Maven on Windows, download the binary archive from [maven.apache.org](https://maven.apache.org/download.cgi), extract it, set `MAVEN_HOME` to its directory, add `%MAVEN_HOME%\bin` to `PATH`, open a new terminal, and check `mvn -version`. Configure `JAVA_HOME` to a JDK directory if Maven does not find Java.

## Import and run in Spring Tool Suite (STS)

1. Install a JDK and Maven as described above; ensure `java -version` and `mvn -version` work in a new terminal.
2. In STS, choose **File → Import → Maven → Existing Maven Projects**.
3. Browse to the `jwt-authentication-service` project folder, select its `pom.xml`, and finish. Allow Maven to resolve dependencies.
4. Right-click the project and choose **Run As → Spring Boot App**. Alternatively, right-click `JwtAuthenticationServiceApplication.java` and run it as a Java application.
5. To override the development secret in STS, open **Run Configurations → Spring Boot App → Environment** and add `JWT_SECRET` with a Base64-encoded key of at least 32 bytes.
6. Open [Swagger UI](http://localhost:8081/swagger-ui/index.html), expand **JWT → POST /api/jwt/generate**, choose **Try it out**, enter the sample JSON, and select **Execute**.

OpenAPI JSON is available at [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs).

You can also test with curl from PowerShell:

```powershell
curl.exe -X POST http://localhost:8081/api/jwt/generate `
  -H "Content-Type: application/json" `
  -d '{"userId":101,"username":"Harshi","email":"harshi@example.com","role":"USER"}'
```

## Tests

Run all unit tests with `mvn clean test`. Tests cover token creation, username/userId/role claims, expiry, malformed and invalid-signature tokens, and service response construction.

## Common errors

- **`mvn` is not recognized:** install Maven and add its `bin` folder to `PATH`, then open a new terminal.
- **Java version mismatch:** install/configure JDK 17 or later and verify `JAVA_HOME`.
- **Invalid or weak JWT secret on startup:** `JWT_SECRET` must be valid Base64 and decode to at least 32 bytes. Use the PowerShell generator above.
- **Port 8081 is already in use:** stop the other process or run with `--server.port=8082`.
- **HTTP 400:** check JSON syntax, include every field, use a positive numeric `userId`, a valid email, and an uppercase role.
- **Swagger is unavailable:** confirm the application started and visit `/swagger-ui/index.html` on port 8081.
- **Maven dependency download fails:** check network/proxy settings and retry `mvn -U clean test`.
