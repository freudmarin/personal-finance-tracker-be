# PersonalFinanceTracker

PersonalFinanceTracker is a Spring Boot application for tracking personal finances.  
It allows users to record transactions, which can be either **income** or **expense**, categorize them, and analyze their financial data.

## Features

- Add, update, and delete transactions (income or expense)
- Categorize transactions
- Tag transactions
- View summaries and charts grouped by transaction type
- Secure REST API

## Tech Stack

- Java
- Spring Boot
- Maven
- SQL (JPA/Hibernate)

## Getting Started

1. Clone the repository
2. Run `mvn clean install`
3. Start the application: `mvn spring-boot:run`
4. Access the API at `http://localhost:8080`

## Database Migration

- The application uses Flyway for database migrations.
- Old tables related to expenses have been removed; all financial records are now stored as transactions.
