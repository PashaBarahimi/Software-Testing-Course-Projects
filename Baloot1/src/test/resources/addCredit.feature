Feature: Adding credits to user account
  Users can add credits to their account to use Baloot

  Scenario Outline: Adding valid credit
    Given A sample user with <initial> credit
    When The user adds <credit> to their account
    Then The user credit should be <answer>

    Examples:
    | initial | credit | answer |
    | 0       | 10     | 10     |
    | 12      | 20     | 32     |

  Scenario: Adding invalid credit
    Given A sample user with 10 credit
    When The user adds -2 to their account
    Then InvalidCreditRange is thrown
    And The user credit should be 10
