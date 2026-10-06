# Citizen Registry

REST API for registering citizens and handling administrative applications(ID card, passport, registration).
Built for public sector institutions to support officers in processing citizen requests.

## Technologies

- Java 21
- Spring Boot 4.0
- PostgreSQL 17
- JWT (authentication)
- Flyway (migration)
- MapStruct (DTO mappings)
- Docker + Docker Compose
- Maven (dependency management)
- JUnit 5 + MockMvc (26 integration tests)

## Features

- CRUD operations for citizens
- Application management with status transitions (state machine)
- Officer management (only ADMIN)
- JWT authentication with roles (ADMIN, OFFICER)
- Pagination and sorting
- Input data validation
- API Documentation (Swagger UI)

## Running

### Requirements
- Docker and Docker Compose

### Steps
1. Clone the repository
2. Create a `.env` file in the project root:
   DB_USER=app_user 
   POSTGRES_PASSWORD=app_password
   JWT_SECRET=your-secret-key-min-256-bits
3. `docker compose up --build`
4. Open http://localhost:8080/swagger-ui/index.html

## Project structure

The project consists of 4 modules
- Citizen
- Officer
- Application
- Auth

Each module follows a layered architecture: 
- Controller (REST API)
- Service (business logic)
- Repository (Spring Data JPA)
- Database (PostgreSQL / H2 for tests)

Shared configuration and error handling are in 'common' package.

## API Endpoints

### Auth 
| Method | Path | Description | Role |
|--------|---------|------|------|
| POST | /api/auth/register | Register new officer | public |
| POST | /api/auth/login | Login | public |

### Citizens
| Method | Path | Description          | Role |
|--------|---------|----------------------|------|
| GET | /api/citizens | List of citizens     | ADMIN, OFFICER |
| GET | /api/citizens/{id} | Citizen details      | ADMIN, OFFICER |
| GET | /api/citizens/pesel/{pesel} | Search by PESEL      | ADMIN, OFFICER |
| POST | /api/citizens | Register new citizen | ADMIN, OFFICER |
| PUT | /api/citizens/{id} | Update citizen data  | ADMIN, OFFICER |

### Applications
| Method | Path                          | Description        | Role |
|--------|-------------------------------|--------------------|------|
| GET | /api/applications             | List of applications | ADMIN, OFFICER |
| GET | /api/applications/{id}        | Application details | ADMIN, OFFICER |
| POST| /api/applications             | Submit application | ADMIN, OFFICER |
| PATCH | /api/applications/{id}/assign | Assign officer     | ADMIN |
| PATCH | /api/applications/{id}/status | Change status | ADMIN, OFFICER |

### Officers 
| Method | Path | Description        | Role |
|--------|---------|--------------------|------|
| GET | /api/officers | List of officers   | ADMIN |
| GET | /api/officers/{id} | Officer details    | ADMIN |
| PATCH | /api/officers/{id}/deactivate | Deactivate officer | ADMIN |

## Tests

The project includes 26 integration tests using MockMvc and H2 in-memory database.

  ```bash
  ./mvnw test
