Feature: Authentication API
  As an API consumer
  I want to call the authentication endpoint
  So that I can get a valid token

  @api @auth @smoke
  Scenario: Successfully authenticate with valid credentials
    Given I prepare the authentication API request
    When I send a POST request to "/api/auth/login" with valid credentials
    Then the response status should be 200
    And the response should contain a valid token
    And the response should contain user details

  @api @auth @negative
  Scenario: Authentication fails with invalid credentials
    Given I prepare the authentication API request
    When I send a POST request to "/api/auth/login" with invalid credentials
    Then the response status should be 401
    And the response should contain error message "Invalid credentials"

  @api @auth @error
  Scenario: Authentication fails with missing email
    Given I prepare the authentication API request
    When I send a POST request to "/api/auth/login" with missing email
    Then the response status should be 400
    And the response should contain error message "Email is required"
