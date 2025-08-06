## User Aggregator Service

Overview
The User Aggregator Service dynamically aggregates users' data from multiple databases (PostgreSQL and MySQL) and
provides a unified REST endpoint (GET /users) to fetch consolidated user information. This service uses declarative
YAML-based configuration to specify data sources, which allows maximum flexibility to support unlimited databases with
different schemas.

Technologies Used
Spring Boot: To build the REST API.
Flyway: Database migration tool to create tables and seed data for both MySQL and PostgreSQL.
HikariCP: Connection pooling for database connections.
JdbcTemplate: For dynamic querying across multiple databases.
Java Streams API: To aggregate and transform user data efficiently.
Maven: Project build and dependency management.
Lombok: Reduces boilerplate code (e.g., getters, setters) for DTOs.
Features
Dynamic Data Source Aggregation:

Supports infinite databases by configuring them through application.yml.
Automatically maps inconsistent database schemas (column names) to a unified response format.
Flyway Database Migrations:

Flyway creates the schema (tables) and seeds initial data in both MySQL and PostgreSQL.
Efficient Data Aggregation:

Collects user data from multiple sources.
Aggregates and transforms data into a unified format.
Success Response Example
When calling the REST endpoint /users, the aggregated user list looks like this:

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

# Steps Completed

1. Project Setup
   Used Spring Initializer to bootstrap the project:
   Dependencies: Spring Web, Spring Data JPA, Flyway, MySQL Connector, PostgreSQL Driver, Lombok, Spring Boot Starter
   Test.
2. YAML Configuration
   Configured multiple data sources dynamically in application.yml.
   Each data source specifies connection details, table name, and column mappings.
3. Flyway for Database Migration
   Created separate migrations for MySQL and PostgreSQL:
   MySQL: Created user_table table with ldap_login, name, and surname.
   PostgreSQL: Created users table with user_id, login, first_name, and last_name.
   Populated both tables with initial data.
4. Dynamic Data Source Handling
   Implemented DynamicDataSourceConfig to dynamically create and load multiple DataSource objects from application.yml.
   Used HikariCP for connection pooling.
5. Aggregation Service
   Created DataAggregatorService that:
   Queries data from databases using JdbcTemplate.
   Applies column mappings from YAML configuration.
   Aggregates user data into a unified DTO using Stream API.
6. REST Controller
   Created a single REST endpoint (GET /users) to expose aggregated user data.
   Flow Diagram
   text

Client -> Calls `/users` -> Aggregation Service -> Queries All Data Sources -> Unifies User Data -> Returns JSON Array

# Steps to set up env

1. Clone the Repository
   bash

git clone <repository-url>
cd user-aggregator

2. Setup MySQL and PostgreSQL Databases
   Create PostgreSQL Database (db1):
   sql

CREATE DATABASE db1;
Create MySQL Database (db2):
sql

CREATE DATABASE db2;
Ensure connection details match those in application.yml.

3. Run Flyway Migrations
   Flyway will automatically create tables and insert sample data when the application starts.

4. Run the Application
   Run the Spring Boot application:

bash

mvn spring-boot:run

5. Test the Endpoint
   Use Postman or cURL to call the API:

curl -X GET http://localhost:8080/users

# Expected Response:

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

# Key Classes:

DynamicDataSourceConfig: Handles dynamic creation of multiple DataSource instances.
DataAggregatorService: Queries, maps, and aggregates user data from all data sources.
UserController: Exposes the /users endpoint.
Flyway Migrations (db/migration): Create tables and seed data for both databases.
Tools and Libraries
Spring Boot: REST API development and bean lifecycle management.
Flyway: Database schema migration and versioning.
Lombok: Reduces boilerplate code for DTOs (UserEntity and DataSourceProperties).
Maven: Dependency management and project build tool.
Future Improvements

Handle Duplicates:
Extend the aggregation logic to handle duplicate users across databases more effectively (e.g., deduplication based on
username).

Support More Database Types:
Extend support for Oracle, SQLite, or additional database types.
Add dynamic configuration to detect strategy types (mysql, postgres, etc.).
Better Error Handling:

Add proper exception handling for scenarios like:
Failed database connections.
Invalid data mappings.
