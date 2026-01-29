Feature: User Login

  Scenario: Valid login [null, Risk: HIGH]
    Given User is on login page
    When User enters valid credentials
    When User clicks login
    Then User is redirected to dashboard

