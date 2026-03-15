@E2E
Feature: book management

  Scenario: client creates a user and books
    Given a user named Han with email han@rebels.org
    When the client creates a book "Millennium Falcon Owner's Manual" for han@rebels.org
    And the client creates a book "The Smuggler's Guide" for han@rebels.org
    Then the client can retrieve 2 books for han@rebels.org

  Scenario: reassign book and mark as borrowed
    Given a user named Luke with email luke@rebels.org
    And a user named Leia with email leia@rebels.org
    When the client creates a book "The Jedi Path" for luke@rebels.org
    And the client reassigns the book to leia@rebels.org
    And the client marks the book as borrowed
    Then the client can retrieve 1 books for leia@rebels.org
    And the book "The Jedi Path" for leia@rebels.org is marked as borrowed