@E2E
Feature: Advanced Library Management

  Scenario: client borrows a book and decreases stock
    Given a user named Han with email han@rebels.org
    When the client creates a book "Millennium Falcon" with 1 copies
    And the client borrows the book for han@rebels.org
    Then the book has 0 available copies

  Scenario: client reserves a book when out of stock
    Given a user named Luke with email luke@rebels.org
    And a user named Leia with email leia@rebels.org
    When the client creates a book "The Jedi Path" with 1 copies
    And the client borrows the book for luke@rebels.org
    And the client reserves the book for leia@rebels.org
    Then the book has 1 user in queue