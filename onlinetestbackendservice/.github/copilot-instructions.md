## Model preference
- Preferred model: Claude Sonnet 3.5 for all clients. If not available, fall back to the workspace default LLM.

## Quick repository summary
- Java Spring Boot monolith (package base: `com.example.onlinetest`).
- Main entry: `src/main/java/com/example/onlinetest/onlinetestbackendservice/OnlinetestbackendserviceApplication.java` (scans `com.example.onlinetest`).
- Web layer: controllers under `Controller` (e.g. `AuthController`, `UserProfileController`).
- Services: implementations in `Service` and corresponding interfaces prefixed with `I` (e.g. `IUserProfileService`, `IUserAuthenticationService`).
- Data layer: Spring Data JPA repos under `Repo` (entities like `User` and `UserBaseModel`, `UserRepo` uses `UUID`).

## What agents should know (big picture)
- This is a single Spring Boot service exposing REST endpoints (e.g. `/api/auth/*`, `/api/user/profile/*`).
- Authentication uses JWT (see `Service/JwtToken/*` and `Configuration/JwtConfig.java`). JWT secret is read from `jwt.secret` property and auto-generated if missing.
- Persistence uses Spring Data JPA + Microsoft SQL Server (see `pom.xml` dependency `mssql-jdbc` and `application.properties`).
- Email is sent via Spring Mail (configured in `application.properties`), used by forgot-password flow in `UserProfileService`.
- CORS is intentionally restricted to frontend at `http://localhost:3000` in `Configuration/CorsConfig.java`.

## Project-specific conventions and patterns
- Interfaces are named with a leading `I` and controllers depend on interfaces (constructor injection via `@Autowired`). Prefer preserving this pattern when refactoring.
- DTOs live under `Domain/Dto` and mapping logic is centralized in `Domain/Mapper.java`; use these for conversions rather than ad-hoc copying.
- Exception handling uses custom exceptions in `Domain/Exceptions` and a `GlobalExceptionHandler`. Throw existing exception types rather than inventing new patterns.
- Passwords are handled as plain text in current code (e.g. `AuthenticationService` and `UserProfileService` update `user.setPassword(...)`). Do NOT change storage or hashing behavior unless explicitly asked—changing auth behavior is breaking.

## Integration points & sensitive config
- Database: configured in `src/main/resources/application.properties` (`spring.datasource.*`). Local dev uses localhost:1433; CI/dev may override via environment or profile properties.
- Mail: `spring.mail.*` configured in `application.properties` (contains credentials). Treat these values as secrets; do not commit changes that expose or rotate them without instructions.
- JWT: `jwt.secret` and `jwt.expiration` in `application.properties`. `JwtConfig` will generate a secret if none is provided.

## Build / run / test (Windows dev environment)
- Build: `mvnw.cmd clean package` (from repository root)
- Run (dev): `mvnw.cmd spring-boot:run` (uses `application.properties`) or run the built jar:
  - `java -jar target/onlinetestbackendservice-0.0.1-SNAPSHOT.jar`
- Tests: `mvnw.cmd test`

## Safe-edit rules for agents
- When editing code that touches authentication, tokens, or password storage: leave behavior unchanged unless user explicitly requests an auth/security improvement. Flag required changes for human review.
- Do not modify `application.properties` credentials in pull requests. Instead, suggest using environment variables or Spring profiles and document the change.
- Use existing Mapper methods in `Domain/Mapper.java` for DTO/entity conversions.

## Concrete examples to reference
- Login/register endpoints: `AuthController` — POST `/api/auth/login` and `/api/auth/register`.
- Profile update: `UserProfileController` — PATCH `/api/user/profile/update/{userName}`.
- Forgot/reset password flows: implemented in `UserProfileService` (token created, emailed via `IEmailService`, and consumed in `reset-password`).

## Troubleshooting hints
- If running fails with DB connection errors, check `spring.datasource.url` and ensure MS SQL (or container) is reachable on port 1433.
- If JWT tokens fail validation, check `jwt.secret` in `application.properties` or allow `JwtConfig` to generate one for local dev.

## Editing & PR guidance
- Keep changes small and focused. Include unit or integration tests for behavior changes where possible.
- When adding endpoints, follow existing package and naming conventions (`Controller` -> `Service` interface -> `Service` impl -> `Repo`).

---
If any of the above is unclear, tell me which area you want expanded (build, auth, DB, or example edits). Also confirm: do you want me to add a policy or automated step to "Enable Claude Sonnet 3.5 for all clients" in CI or repository metadata, or should that be a note only for AI agents (as above)?
