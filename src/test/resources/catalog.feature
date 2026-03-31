Feature: Book Catalog Management
  Scenario: Adding a new book to the library
    When the librarian adds a book named "Design Patterns" with 5 copies
    Then the catalog should contain "Design Patterns" with 5 available copies