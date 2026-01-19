Feature: Real API Testing Examples

  Description:
    This feature demonstrates how to test real APIs and capture responses.
    
    Uses JSONPlaceholder (https://jsonplaceholder.typicode.com) which is
    a free public API perfect for testing.
    
    Run with: mvn test -Dgroups="api,example"

  # ==================== EXAMPLE 1: Simple GET Request ====================
  @api @example @get
  Scenario: Fetch a public post and validate response
    Given I fetch post number 1 from JSONPlaceholder
    Then the response status code should be 200
    And the response should contain post fields
    And the response matches post contract

  # ==================== EXAMPLE 2: Create via POST Request ====================
  @api @example @post
  Scenario: Create a new post via API
    Given I have an API client configured
    When I create a new post with title "Test Post From API"
    Then the response status code should be 201
    And the post title should be "Test Post From API"

  # ==================== EXAMPLE 3: Capture Response as Fixture ====================
  @api @example @capture
  Scenario: Capture API response for offline use
    Given I fetch post number 2 from JSONPlaceholder
    When I capture the post response as a fixture named "example-post-response"
    Then the response should contain post fields

  # ==================== EXAMPLE 4: Fallback to Mock ====================
  @api @example @fallback
  Scenario: Handle API failure gracefully
    When I fetch post 999 with fallback
    Then the response status code should be 200
    And the response should contain post fields

  # ==================== EXAMPLE 5: Contract Validation ====================
  @api @example @contract
  Scenario: Validate API contract hasn't changed
    Given I fetch post number 5 from JSONPlaceholder
    Then the response matches post contract
    And the response should contain post fields

  # ==================== EXAMPLE 6: Fetch and Capture in One Step ====================
  @api @example @comprehensive
  Scenario: Fetch post and immediately capture as fixture
    Given I fetch and capture post 10 as "post-10-fixture"
    Then the response status code should be 200

  # ==================== EXAMPLE 7: Use ApiUtils Helper ====================
  @api @example @utils
  Scenario: Use ApiUtils for automatic fixture generation
    When I use ApiUtils to capture post 3
    Then the response status code should be 200

  # ==================== EXAMPLE 8: Multiple API Calls ====================
  @api @example @multiple
  Scenario: Make multiple API calls in one scenario
    Given I fetch post number 1 from JSONPlaceholder
    Then the response status code should be 200
    
    Given I fetch post number 2 from JSONPlaceholder
    Then the response status code should be 200
    
    Given I fetch post number 3 from JSONPlaceholder
    Then the response status code should be 200

  # ==================== EXAMPLE 9: Verify Specific Fields ====================
  @api @example @fields
  Scenario: Verify specific fields in response
    Given I fetch post number 1 from JSONPlaceholder
    Then the response should contain post fields
    And the post title should be "sunt aut facere repellat provident"
    And the response matches post contract

  # ==================== EXAMPLE 10: Compare with Fixture ====================
  @api @example @fixture
  Scenario: Load response from fixture
    Given I have an API client configured
    When I load authentication response from fixture "auth-success-response.json"
    Then the response status code should be 200
