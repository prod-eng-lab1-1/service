# Library Management System

## Team: IngDevDPF
* **Patrascu Alexandru** - Backend development, database design & User Gamification
* **Dinulescu Mihnea Stefan** - API development, testing & Catalog/Inventory Management
* **Frunzeanu Calin** - Monitoring, deployment & Transactional Logic (Dynamic Borrowing/Priority Waitlist)

## Project Description
The Library Management System is an advanced, enterprise-grade RESTful application. Moving beyond standard CRUD operations, this system implements dynamic business rules, a state-driven inventory, and a **Gamification & Priority Waitlist Engine**. 

Users are incentivized to read and return books through an Experience Points (XP) system, ranking up from BRONZE to GOLD. A user's rank directly dictates their privileges within the system, altering borrowing limits and shifting their priority dynamically in the automated reservation queues.

## Tech Stack
* **Backend:** Spring Boot (Java 21)
* **Database:** MongoDB
* **Testing:** JUnit 5, Mockito, JaCoCo (Unit Testing & Coverage), Cucumber (BDD)
* **Error Handling:** Graceful Global Exception Handling (`@ControllerAdvice`)
* **Deployment:** Docker & Docker Compose

---

## Features & Team Work Breakdown

### 👤 Feature 1: Gamified User Management & Foundation
**Assigned to:** Patrascu Alexandru
**Scope:** Building the member registry and the Gamification Engine. Every new user starts as `BRONZE`. The system tracks XP and automatically upgrades ranks (`SILVER`, `GOLD`) based on user activity, unlocking new platform privileges.
**Endpoints (5):**
* `POST /api/users` - Register a new member (Initialized with 0 XP, BRONZE).
* `GET /api/users` - Retrieve a full list of all registered library members.
* `GET /api/users/{id}` - Retrieve detailed information about a specific member.
* `PATCH /api/users/{id}/name` - Update a member's name dynamically.
* `DELETE /api/users/{id}` - Safely remove a member from the system.

### 📚 Feature 2: Book Catalog & Inventory Management
**Assigned to:** Dinulescu Mihnea Stefan
**Scope:** Managing the physical inventory. A "Book" represents a title with multiple physical copies. Tracks `totalCopies`, real-time `availableCopies`, and the size of the reservation queue.
**Endpoints (4):**
* `POST /api/books` - Add a new book title and define its initial stock.
* `GET /api/books` - Get the entire catalog with real-time stock and waitlist metrics.
* `GET /api/books/{id}` - Retrieve details of a specific book by ID.
* `DELETE /api/books/{id}` - Remove a book from the catalog entirely.

### 🔄 Feature 3: Dynamic Transactional Logic & Priority Waitlist
**Assigned to:** Frunzeanu Calin
**Scope:** Implementing the core, dynamic business rules of the library. 
**Advanced Business Rules Implemented:**
1. **Dynamic Borrow Limits (Polymorphism):** BRONZE users can borrow 1 book, SILVER can borrow 3, GOLD can borrow 5.
2. **XP Farming:** Returning a book yields +50 XP.
3. **Priority Waitlist Engine:** If stock is 0, users join a queue. The queue is automatically and stably sorted by Rank (GOLD users jump ahead of BRONZE users in line).
4. **Auto-Assign:** Upon return, the book skips the shelf and is instantly assigned to the 1st user in the priority queue.

**Endpoints (3):**
* `POST /api/books/{id}/borrow` - Borrows a book, checking dynamic rank limits.
* `POST /api/books/{id}/reserve` - Joins the waitlist. Triggers the Priority Sorting Algorithm based on User Rank.
* `POST /api/books/{id}/return` - Returns a book, grants XP, recalculates rank, and re-assigns the book automatically.

---

## Development Prerequisites & Setup

* Follow the [./PREREQUISITES.md](./PREREQUISITES.md) instructions to configure a local virtual machine with Ubuntu, Docker, IntelliJ.

### Run code locally
* Start the MongoDB database: `./start_mongo_only.sh`
* Build and run the Spring Boot service: `./gradlew bootRun`
* Use `requests.http` to test all API endpoints and business flows.