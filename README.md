# Library Management System

## Team: IngDevDPF
* **Patrascu Alexandru** - Backend development, database design & User Management
* **Dinulescu Mihnea Stefan** - API development, testing & Catalog/Inventory Management
* **Frunzeanu Calin** - Monitoring, deployment & Transactional Logic (Borrowing/Waitlist)

## Project Description
The Library Management System is an advanced backend RESTful application designed to manage the essential operations of a modern digital library. Moving beyond simple CRUD operations, this system implements real-world business logic and state management. 

It allows librarians to manage a catalog of books (handling multiple physical copies per title), keeps track of registered members, and enforces strict borrowing policies. A core feature of the system is the **Automated Waitlist (Reservation) Engine**, which seamlessly handles out-of-stock scenarios by queuing users and automatically reassigning returned books to the next person in line.

## Tech Stack
* **Backend:** Spring Boot (Java 21)
* **Database:** MongoDB
* **Testing:** JUnit 5, Mockito, JaCoCo (Unit Testing & Coverage), Cucumber (BDD End-to-End Testing)
* **Monitoring:** Prometheus, Grafana, Loki
* **Deployment:** Docker & Docker Compose

---

## Features & Team Work Breakdown

To follow the trunk-based development and ensure individual contributions are properly tracked via Pull Requests, the system logic and the **12 API endpoints** are divided into 3 main architectural features:

### 👤 Feature 1: User Management & Foundation
**Assigned to:** Patrascu Alexandru
**Branch name convention:** `feature/alex-user-management`
**Scope:** Building the foundation of the application. This feature handles the library's member registry, ensuring data integrity for users who will later interact with the book inventory.
**Endpoints (5):**
* `POST /api/users` - Register a new member (Validates duplicate emails to prevent fraud).
* `GET /api/users` - Retrieve a full list of all registered library members.
* `GET /api/users/{id}` - Retrieve detailed information about a specific member.
* `PATCH /api/users/{id}/name` - Update a member's name dynamically.
* `DELETE /api/users/{id}` - Safely remove a member from the system.

### 📚 Feature 2: Book Catalog & Inventory Management
**Assigned to:** Dinulescu Mihnea Stefan
**Branch name convention:** `feature/mihnea-book-catalog`
**Scope:** Managing the library's physical inventory. Unlike simple CRUDs, a "Book" here represents a title that can have multiple physical copies. This feature tracks `totalCopies` and exposes real-time statistics like `availableCopies` and the current `queueSize` for reservations.
**Endpoints (4):**
* `POST /api/books` - Add a new book title to the catalog and define its initial physical stock (e.g., 5 copies).
* `GET /api/books` - Get the entire catalog, including real-time stock availability and waitlist metrics.
* `GET /api/books/{id}` - Retrieve details, stock, and queue size of a specific book by ID.
* `DELETE /api/books/{id}` - Remove a book from the catalog entirely.

### 🔄 Feature 3: Transactional Logic, Borrowing & Waitlist System
**Assigned to:** Frunzeanu Calin
**Branch name convention:** `feature/calin-borrowing-logic`
**Scope:** Implementing the core business rules of the library. This feature connects Users and Books through a strict set of validations and manages the state of the inventory.
**Business Rules Implemented:**
1. **Borrow Limit:** A user cannot borrow more than 3 books simultaneously.
2. **Duplicate Prevention:** A user cannot borrow two physical copies of the exact same title.
3. **Waitlist Auto-Assign:** If a book's stock is 0, users can join a queue. Upon return, the book skips the shelf and is instantly assigned to the first user in the queue.

**Endpoints (3):**
* `POST /api/books/{id}/borrow` - Borrows a book. Decreases `availableCopies`. Fails if stock is 0, limit is reached, or the user already has the book.
* `POST /api/books/{id}/reserve` - Adds the user to the `reservationQueue` if the book is out of stock. Fails if the book is actually available.
* `POST /api/books/{id}/return` - Returns a book. Automatically checks the `reservationQueue` and transfers the book to the next waiting user without increasing the shelf stock.

---

## Development Prerequisites & Setup

* Follow the [./PREREQUISITES.md](./PREREQUISITES.md) instructions to configure a local virtual machine with Ubuntu, Docker, IntelliJ.

### Run code locally
* Start the MongoDB database:
  * `./start_mongo_only.sh`
* Build and run the Spring Boot service:
  * `./gradlew build`
  * `./gradlew bootRun`
* Use the provided `requests.http` file to test all API endpoints and business flows.

### Run via Docker (Full Stack)
* Build the image: `make build`
* Start all containers: `./start.sh`
* Access MongoDB Admin UI: `http://localhost:8090` (user: `unibuc` / pass: `adobe`)
