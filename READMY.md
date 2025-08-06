## User Aggregator Service
# Overview
The User Aggregator Service dynamically aggregates user data from multiple databases (e.g., PostgreSQL, MySQL) and provides a unified REST API to fetch and store user information. The service is designed to support declarative YAML-based configuration as well as runtime operations for creating users.

# Key Features:

GET /users: Fetches consolidated user information aggregated from multiple databases.
POST /users/create: Allows users to be dynamically added to specified databases at runtime.
This service supports infinite data sources that can be configured via application.yml and automatically handles mismatched database column names by using a declarative mapping system. The service is extensible for schema evolution or new database configurations.

# Technologies Used
Spring Boot: To build the REST API.
HikariCP: Connection pooling for efficient database connections.
JdbcTemplate: Facilitates database queries and updates across multiple databases.
Spring WebFlux: Non-blocking API for fetching aggregated user data concurrently.
Lombok: Reduces boilerplate code for DTOs and configuration classes.
Maven: Dependency management and build system.

# Features
1. Dynamic Data Sources
   Supports dynamically specified databases using YAML configuration (application.yml).
   Each database can have custom schemas, connection details, and column mappings.
2. Multiple Endpoints
   GET /users
   Aggregates user data from all configured databases.
   Delivers a unified response despite inconsistent schemas across databases.
   POST /users/create
   Dynamically creates new users in the configured databases.
   Accepts a JSON payload to specify the target database and user information.
   
Example Payload for POST /users/create:

{
"databaseName": "database_mysql",
"username": "user-5",
"name": "New",
"surname": "User"
}

On success, the endpoint returns a message:
{
"message": "User user-5 successfully created in database database_mysql."
}
3. Unified Aggregated User Response
   Aggregates and standardizes user information from databases with mismatched schemas.
   Maps keys in each database to consistent values (e.g., user_id → id, ldap_login → username).
   
Example Response for GET /users:


      [
      {
      "id": "example-user-id-1",
      "username": "user-1",
      "name": "User",
      "surname": "Userenko"
      },
      {
      "id": "example-user-id-2",
      "username": "user-2",
      "name": "Testuser",
      "surname": "Testov"
      }
      ]

Configuration
Application YAML Format
Data sources can be configured declaratively in application.yml by specifying the database type, connection details, mapping schema, and table name.

# Example application.yml:
aggregator:
data-sources:
- name: database_postgres
url: jdbc:postgresql://localhost:5432/database_postgres
user: postgres
password: root
table: users
mapping:
id: user_id
username: login
name: first_name
surname: last_name

    - name: database_mysql
      url: jdbc:mysql://localhost:3306/database_mysql
      user: root
      password: root
      table: user_table
      mapping:
        id: ldap_login
        username: ldap_login
        name: name
        surname: surname

Setup and Execution

# Prerequisites
MySQL and PostgreSQL: Ensure the databases are set up and reachable.
Java 21 or later: The application is built using Java 21 features.
Maven: For building and running the project.

Steps to Run

# Clone the Repository:
git clone <repository-url>
cd user-aggregator

## Configure the Databases:

# For PostgreSQL:
CREATE DATABASE database_postgres;
For MySQL:
CREATE DATABASE database_mysql;

# Update Connection Details:
Update the application.yml file with your local database configuration details (e.g., username, password, URL).

# Run the Application: Start the Spring Boot application:
mvn spring-boot:run

Test the API:
# Fetch all users:
curl -X GET http://localhost:8080/users

# Create a new user:
curl -X POST http://localhost:8080/users/create \
-H "Content-Type: application/json" \
-d '{"databaseName": "database_mysql", "username": "user-5", "name": "Test", "surname": "User"}'
Project Structure
Key Components
Class/File	Description
DynamicDataSourceConfig	Dynamically creates DataSource instances using HikariCP for connection pooling.
DataAggregatorService	Queries user data from all configured data sources and aggregates the results.
UserCreationService	Handles the logic for dynamically inserting users into specified databases.
UserController	Exposes REST endpoints for fetching (GET /users) and creating users (POST /users/create).
application.yml	Declarative configuration for all external data sources and their mapping schemas.

# API Endpoints
1. GET /users
   Fetch aggregated user data from all configured databases.

# Response:

      [
      {
      "id": "example-user-id-1",
      "username": "user-1",
      "name": "User",
      "surname": "Userenko"
      },
      {
      "id": "example-user-id-2",
      "username": "user-2",
      "name": "Testuser",
      "surname": "Testov"
      }
      ]

2. POST /users/create
 Dynamically create a new user in the specified database.

# Request Body Example:
      {
      "databaseName": "database_mysql",
      "username": "user-5",
      "name": "New",
      "surname": "User"
      }
Response:
{
"message": "User user-5 successfully created in database database_mysql."
}

# Additional Notes
Modifying Databases: Adding or removing databases does not require code changes. Simply update the application.yml configuration file.
Concurrency: All data sources are queried in parallel for high performance, leveraging the non-blocking WebFlux API.
Scaling: The service supports an unlimited number of data sources, provided sufficient resources are available.
Tools and Libraries
Spring Boot: For efficient REST API development.
Lombok: Reduces boilerplate code for DTOs like UserEntity and DataSourceProperties.
Maven: Manages dependencies and builds the project.
OpenAPI/Swagger: Generates API documentation for easy testing and exploration.

## Example Use Cases
# Fetching all aggregated users:
X GET http://localhost:8080/users
Creating users in different databases:

# For MySQL:
curl -X POST http://localhost:8080/users/create -H "Content-Type: application/json" -d '{
"databaseName": "database_mysql",
"username": "mysql-user",
"name": "My",
"surname": "SQL"
}

# For PostgreSQL:
curl -X POST http://localhost:8080/users/create -H "Content-Type: application/json" -d '{
"databaseName": "database_postgres",
"username": "postgres-user",
"name": "Post",
"surname": "Gre"
}'
