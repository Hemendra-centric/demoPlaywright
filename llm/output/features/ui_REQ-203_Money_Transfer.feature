Feature: Money Transfer

  Scenario: Transfer within same bank [null, Risk: LOW]
    Given User enters beneficiary details
    When User enters amount
    Then User confirms transfer

  Scenario: Transfer to new beneficiary [null, Risk: HIGH]
    Given User adds new beneficiary
    When User enters OTP
    When User confirms transfer
    Then Transfer is successful

