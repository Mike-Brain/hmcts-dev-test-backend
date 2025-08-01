# HMCTS Dev Test Backend

This is the backend for the brand new HMCTS case management system. Below you'll find instructions for running the application, including automatic database initialization and testing the main API endpoints.

## Building the Project

1. Build the project using Gradle:
   ```sh
   ./gradlew build
   ```

## Running the Application

1. Start the application and PostgreSQL using Docker Compose:
   ```sh
   docker-compose up -d
   ```
   - The application will run on port `4000`.
   - The PostgreSQL database service will automatically create the database and tables on startup if they do not exist.

2. View the application logs:
   ```sh
   docker-compose logs -f backend
   ```

## API Usage

The API runs on `http://localhost:4000` when using Docker Compose.


## Testing

- Run all unit and integration tests using Gradle:
  ```sh
  ./gradlew test
  ```

This will confirm the application functionality with comprehensive test coverage.

Use this `README.md` for an overview of running and interacting with the application.
