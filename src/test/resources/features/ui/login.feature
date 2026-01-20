Feature: User Login
  As a user
  I want to log in with valid credentials
  So that I can access the application

  @ui @login @smoke
  Scenario: User logs in with valid credentials
    Given I open the login page
    When I enter email "user@example.com"
    And I enter password "SecurePassword123"
    And I click the login button
    Then I should see success message

  @ui @login @negative
  Scenario: User sees error with invalid credentials
    Given I open the login page
    When I enter email "invalid@example.com"
    And I enter password "wrongpassword"
    And I click the login button
    Then I should see error message "Invalid email or password"

  @ui @login @validation
  Scenario: User sees validation error with empty email
    Given I open the login page
    When I enter password "somepassword"
    And I click the login button
    Then I should see error message "Email is required"
