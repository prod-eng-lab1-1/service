Feature: Gamified User Management
  Scenario: Creating a new user
    When the client creates a user named "Luke Skywalker" with email "luke@jedi.com"
    Then the user response status code is 201
    And the client can see at least 1 users in the system