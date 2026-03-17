# Library Management System

## Team: IngDevDPF
* **Patrascu Alexandru** - Backend development, database design
* **Dinulescu Mihnea Stefan** - API development, testing
* **Frunzeanu Calin** - Monitoring and deployment

## Project Description
The Library Management System is an advanced backend RESTful application designed to manage the essential operations of a digital library. The system helps librarians organize the book catalog, manage users, and enforce real-world business rules such as borrowing limits, real-time stock tracking, and automated waitlist (reservation) management.

## Tech Stack
* **Backend:** Spring Boot (Java 21)
* **Database:** MongoDB
* **Testing:** JUnit, Mockito, JaCoCo (Unit Testing & Coverage)
* **Monitoring:** Prometheus, Grafana, Loki
* **Deployment:** Docker & Docker Compose

---

## Features & Team Work Breakdown

To follow the trunk-based development and ensure individual contributions are properly tracked via Pull Requests, the system is divided into 3 main features, assigned as follows:

### 👤 Feature 1: User Management
**Assigned to:** Patrascu Alexandru
**Branch name convention:** `feature/alex-user-management`
**Scope:** Managing library members (entities, repository, service, and controller).
**Endpoints (5):**
* `POST /api/users` - Register a new member
* `GET /api/users` - Retrieve all library members
* `GET /api/users/{id}` - Retrieve a member by ID
* `PATCH /api/users/{id}/name` - Update a member's name
* `DELETE /api/users/{id}` - Remove a member from the system

### 📚 Feature 2: Book Catalog & Inventory Management
**Assigned to:** Dinulescu Mihnea Stefan
**Branch name convention:** `feature/mihnea-book-catalog`
**Scope:** Managing the library's physical inventory, tracking total copies, and basic Book CRUD operations.
**Endpoints (4):**
* `POST /api/books` - Add a new book to the catalog (with specified number of copies)
* `GET /api/books` - Get all books along with their stock and queue size
* `GET /api/books/{id}` - Retrieve details of a specific book by ID
* `DELETE /api/books/{id}` - Remove a book from the catalog entirely

### 🔄 Feature 3: Advanced Borrowing Logic & Waitlist System
**Assigned to:** Frunzeanu Calin
**Branch name convention:** `feature/calin-borrowing-logic`
**Scope:** Implementing strict business rules (max 3 books/user, duplicate prevention) and an automated reservation queue system.
**Endpoints (3):**
* `POST /api/books/{id}/borrow` - Borrow a book (Validates max borrow limits, duplicates, and physical stock)
* `POST /api/books/{id}/reserve` - Join the waitlist for a book if the stock is currently 0
* `POST /api/books/{id}/return` - Return a book (Automatically re-assigns the book to the first user in the reservation queue, if any)

---

## Development Prerequisites & Setup

* Follow the [./PREREQUISITES.md](./PREREQUISITES.md) instructions to configure a local virtual machine with Ubuntu, Docker, IntelliJ.

### Run code locally
* Start the MongoDB database:
  * `./start_mongo_only.sh`
* Build and run the Spring Boot service:
  * `./gradlew build`
  * `./gradlew bootRun`
* Use `requests.http` to test all API endpoints listed above.

### Run via Docker (Full Stack)
* Build the image: `make build`
* Start all containers: `./start.sh`
* Access MongoDB Admin UI: `http://localhost:8090` (user: `unibuc` / pass: `adobe`)