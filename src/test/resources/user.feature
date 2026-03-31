Feature: Gamified User Management
  Scenario: Creating a new user
    When the client creates a user named "Luke Skywalker" with email luke_final_de_tot_1@jedi.com
    Then the user response status code is 201
    When the client retrieves all users
    Then the client can see at least 1 users