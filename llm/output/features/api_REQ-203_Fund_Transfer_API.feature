Feature: Fund Transfer API [API]

  Scenario: Internal transfer [Risk: LOW]
    Given I send request: {fromAccount=string, toAccount=string, amount=1000}
    Then I verify response: {statusCode=200}

  Scenario: External transfer [Risk: HIGH]
    Given I send request: {fromAccount=string, toAccount=string, amount=50000, otp=string}
    Then I verify response: {statusCode=200, bodyContains=[transactionId]}

