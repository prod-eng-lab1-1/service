# Library Management System

## Team: IngDevDPF
* **Patrascu Alexandru** - Backend development, database design
* **Dinulescu Mihnea Stefan** - API development, testing
* **Frunzeanu Calin** - Monitoring and deployment

## Project Description
The Library Management System is a backend RESTful application designed to manage the essential operations of a digital library. The system helps librarians organize books, manage users, and keep track of borrowing activities in an efficient and structured way.

## Tech Stack
* **Backend:** Spring Boot (Java 21)
* **Database:** MongoDB
* **Testing:** JUnit, Mockito, Cucumber (BDD)
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

### 📚 Feature 2: Book Catalog Management
**Assigned to:** Dinulescu Mihnea Stefan
**Branch name convention:** `feature/mihnea-book-catalog`
**Scope:** Managing the library's physical inventory and basic Book CRUD operations.
**Endpoints (4):**
* `POST /api/books` - Add a new book to the catalog
* `GET /api/books?borrowerEmail={email}` - Get all books borrowed by a specific user
* `GET /api/books/{id}` - Retrieve a book by its ID
* `DELETE /api/books/{id}` - Remove a book from the catalog

### 🔄 Feature 3: Borrowing Logic & Book Updates
**Assigned to:** Frunzeanu Calin
**Branch name convention:** `feature/calin-borrowing-logic`
**Scope:** Implementing the business logic for borrowing, returning, reassigning books, and system monitoring.
**Endpoints (3):**
* `PATCH /api/books/{id}/borrowed` - Toggle the borrowed status (true/false)
* `PATCH /api/books/{id}/borrower` - Transfer a book to a new borrower
* `PATCH /api/books/{id}/title` - Edit the title of an existing book

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