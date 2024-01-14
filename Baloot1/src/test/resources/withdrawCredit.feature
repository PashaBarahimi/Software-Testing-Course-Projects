Feature: Withdrawing credits from user account
  Users can withdraw credits from their account to buy from Baloot

  Scenario Outline: Withdrawing valid credit
    Given A sample user with <initial> credit
    When The user withdraws <credit> from their account
    Then The user credit should be <answer>

    Examples:
      | initial | credit | answer |
      | 42      | 20     | 22     |
      | 12      | 12     | 0      |

  Scenario: Withdrawing insufficient credit
    Given A sample user with 10 credit
    When The user withdraws 12 from their account
    Then InsufficientCredit is thrown
    And The user credit should be 10

  Scenario: Withdrawing invalid credit
    Given A sample user with 10 credit
    When The user withdraws -2 from their account
    Then IllegalArgumentException is thrown
    And The user credit should be 10
