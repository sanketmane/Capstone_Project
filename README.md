# Capstone Project

A SpringBoot microservices based demo E-commerce application.

## Services

- **APIGateway** - Routes client requests to backend services.
- **EurekaServer** - Provides service discovery.
- **UserManagementService** - Handles registration, login, and user profiles.
- **ProductCatalogService** - Manages products and categories.
- **CartService** - Manages shopping carts.
- **OrderManagementService** - Creates and tracks orders.
- **PaymentService** - Integrates with payment providers.
- **EmailService** - Sends email notifications.

## Requirements

- Java 17
- Maven Wrapper
- Supporting services as needed: MySQL, MongoDB, Redis, Elasticsearch, and Kafka

## Build and Run

Each service is an independent Maven project. From a service directory, run:

```bash
./mvnw clean package
./mvnw test
./mvnw spring-boot:run
```
