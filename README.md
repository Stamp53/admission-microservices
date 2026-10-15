# Admission Management System - Microservices Architecture

A resilient, scalable microservices-based Admission Management System developed using **Java 17**, **Spring Boot 3.3.4**, and **Spring Cloud 2023.0.3**.

---

## 🏛️ Architecture Overview

The system consists of 3 distinct services:

1. **Service Registry (`service-registry`)** - Port `8761`:
   - Netflix Eureka Server for centralized service registration, discovery, and heartbeat monitoring.
2. **API Gateway (`api-gateway`)** - Port `8080`:
   - Spring Cloud Gateway acting as the single reverse proxy entry point.
   - Client-side Load Balancing (`lb://ADMISSION-SERVICE`) via Spring Cloud LoadBalancer.
   - Fault tolerance & circuit breaking via **Resilience4j**.
   - Custom fallback page on service failure or timeouts.
3. **Admission Service (`admission-service`)** - Port `8081`:
   - Core admission business logic, application processing, and reporting.
   - Spring Security 6 with Role-Based Access Control (`ROLE_ADMIN`, `ROLE_STUDENT`).
   - Thymeleaf frontend templates and H2 disk-persisted database with `AUTO_SERVER=TRUE`.

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+

### Build the Entire Project
Run from the root directory:
```bash
mvn clean package -DskipTests
```

### Starting the Services (Order Matters)
Start the services in the following sequence:

1. **Service Registry**:
   ```bash
   cd service-registry
   mvn spring-boot:run
   ```
2. **Admission Service**:
   ```bash
   cd admission-service
   mvn spring-boot:run
   ```
3. **API Gateway**:
   ```bash
   cd api-gateway
   mvn spring-boot:run
   ```

---

## 🌐 URLs & Access Points

| Service / Page | URL | Description |
|---|---|---|
| **API Gateway (Main Entry)** | `http://localhost:8080/login` | Student & Admin Login |
| **Track Application** | `http://localhost:8080/track` | Public application status tracker |
| **Student Registration** | `http://localhost:8080/register` | Online admission application form |
| **Eureka Registry Dashboard** | `http://localhost:8761` | Live microservices instance dashboard |
| **H2 Database Console** | `http://localhost:8081/h2-console` | JDBC URL: `jdbc:h2:file:./data/admissiondb` |

---

## 🔑 Demo Credentials

- **Administrator**: `admin` / `admin123`
- **Approved Student**: `alice` / `student123`
- **Pending Student**: `david` / `student123`
- **Rejected Student**: `emma` / `student123`
