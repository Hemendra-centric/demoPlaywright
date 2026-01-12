Feature: Accessibility Compliance
  As a user
  I want the application to be accessible
  So that all users including those with disabilities can use it

  @accessibility @a11y @login @smoke
  Scenario: Login page meets accessibility standards
    Given I open the login page
    When I run an accessibility scan
    Then the page should have proper heading hierarchy
    And form fields should have proper ARIA labels

  @accessibility @a11y @keyboard @wip
  Scenario: Login page has keyboard navigation
    Given I open the login page
    When I navigate using keyboard only
    Then all interactive elements should be reachable
    And focus should be visible on all elements
