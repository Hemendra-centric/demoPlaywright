Feature: Authentication API [API]

  Scenario: Valid credentials [Risk: HIGH]
    Given I send request: {username=string, password=string}
    Then I verify response: {statusCode=200, bodyContains=[token, expiry]}

