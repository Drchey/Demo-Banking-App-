# 🏦 Demo Banking App

A demo banking REST API built with **Spring Boot** that simulates core banking transactions and mocks the network payment processing lifecycle.

> ⚠️ **Disclaimer:** This project is for learning and demonstration purposes only. It does not move real money and should not be used in production.

---

## ✨ Features

- 👤 Customer registration and authentication
- 🔐 Stateless security with **JWT** (JSON Web Tokens) and Spring Security
- 💳 Account creation with valid **IBAN** generation
- 💸 Core banking transactions: deposits, withdrawals and transfers
- 🔄 Mock payment processing lifecycle (e.g. initiated → processing → completed / failed)
- 🔗 HATEOAS-style REST responses with navigable links
- 🗄️ Persistent storage with PostgreSQL via Spring Data JPA

---

## 🛠️ Tech Stack

| Category        | Technology                          |
| --------------- | ----------------------------------- |
| Language        | Java 17                             |
| Framework       | Spring Boot 4.1.1                   |
| Web             | Spring Web MVC                      |
| Persistence     | Spring Data JPA, PostgreSQL         |
| Security        | Spring Security, JJWT 0.13.0        |
| API Style       | Spring HATEOAS                      |
| IBAN Utilities  | iban4j 3.2.14                       |
| Boilerplate     | Lombok                              |
| Build Tool      | Maven (Maven Wrapper included)      |

---

## 📋 Prerequisites

Make sure you have the following installed:

- [Java JDK 17+](https://adoptium.net/)
- [PostgreSQL](https://www.postgresql.org/download/) (running locally or via Docker)
- Git
- *(Optional)* [Postman](https://www.postman.com/) or cURL for testing endpoints

---

## 🚀 Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/Drchey/Demo-Banking-App-.git
cd Demo-Banking-App-
```

### 2. Create the database

```sql
CREATE DATABASE gtbankapp;
```

### 3. Configure the application

Edit `src/main/resources/application.properties` (or `application.yml`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/gtbankapp
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT settings (use a long, random secret)
jwt.secret=your_super_secret_key_here
jwt.expiration=3600000
```

> 🔒 Never commit real credentials. Use environment variables or a local, git-ignored config file.

### 4. Build and run

**Linux / macOS**
```bash
./mvnw spring-boot:run
```

**Windows**
```bash
mvnw.cmd spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## 🧪 Running Tests

```bash
./mvnw test
```

---

## 📡 API Overview

> Update this section to match your actual controllers and routes.

| Method | Endpoint                   | Description                     | Auth Required |
| ------ | -------------------------- | ------------------------------- | ------------- |
| POST   | `/api/auth/register`       | Register a new customer         | No            |
| POST   | `/api/auth/login`          | Log in and receive a JWT        | No            |
| POST   | `/api/accounts`            | Create a bank account           | Yes           |
| GET    | `/api/accounts/{id}`       | Get account details and balance | Yes           |
| POST   | `/api/transactions/deposit`  | Deposit funds                 | Yes           |
| POST   | `/api/transactions/withdraw` | Withdraw funds                | Yes           |
| POST   | `/api/transactions/transfer` | Transfer between accounts     | Yes           |
| GET    | `/api/transactions`        | View transaction history        | Yes           |

### Example: Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "user@example.com", "password": "password123"}'
```

### Example: Authenticated request

```bash
curl http://localhost:8080/api/accounts/1 \
  -H "Authorization: Bearer <your_jwt_token>"
```

---

## 🔄 Payment Processing Lifecycle

The app mocks how a payment network processes a transaction:

```
INITIATED  →  PROCESSING  →  COMPLETED
                   │
                   └──────→  FAILED
```


---

## 📁 Project Structure

```
Demo-Banking-App-
├── .mvn/wrapper/        # Maven wrapper files
├── src/
│   ├── main/
│   │   ├── java/        # Application source code
│   │   └── resources/   # Configuration files
│   └── test/            # Unit and integration tests
├── pom.xml              # Maven dependencies and build config
├── mvnw / mvnw.cmd      # Maven wrapper scripts
└── README.md
```

---

## 🗺️ Roadmap

- [ ] Add Swagger / OpenAPI documentation
- [ ] Dockerize the app with Docker Compose (app + PostgreSQL)
- [ ] Add role-based access control (admin vs. customer)
- [ ] Add more unit and integration tests
- [ ] Add CI with GitHub Actions
- [ ] Transaction limits and fraud-check simulation

---

## 🤝 Contributing

Contributions are welcome!

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "Add your feature"`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---

## 📄 License

This project currently has no license. Consider adding one (e.g. [MIT](https://choosealicense.com/licenses/mit/)).

---

## 👨‍💻 Author

**Drchey** — [GitHub](https://github.com/Drchey)