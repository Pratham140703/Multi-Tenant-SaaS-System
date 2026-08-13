# Multi-Tenant SaaS HRMS

A multi-tenant Human Resource Management System built with Spring Boot and JSF + PrimeFaces. Multiple companies share one deployment with full data isolation per tenant.

## Tech Stack

- **Backend:** Spring Boot 4.1.0, Java 17
- **Security:** Spring Security (session-based)
- **Frontend:** JSF (Mojarra) + PrimeFaces, JoinFaces 6.1.0
- **Database:** PostgreSQL
- **Build:** Maven

## Roles

| Role | Landing Page |
|---|---|
| Super Admin | `/superadmin/companies.xhtml` |
| HR Admin | `/index.xhtml` |
| HR Manager | `/index.xhtml` |
| Employee | `/employee/my-dashboard.xhtml` |

## Getting Started

**Prerequisites:** Java 17, Maven 3.8+, PostgreSQL 14+

```sql
CREATE DATABASE hrms;
CREATE USER hrms_user WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE hrms TO hrms_user;
```

Edit `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hrms
spring.datasource.username=hrms_user
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
```

Run:

```bash
mvn spring-boot:run
```

App runs at `http://localhost:8080`

## Repository

[github.com/Pratham140703/Multi-Tenant-SaaS-System](https://github.com/Pratham140703/Multi-Tenant-SaaS-System)
