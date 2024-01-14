Feature: Removing items from buy list
  Users can remove items from their buy list

  Scenario: Removing an item with only 1 quantity in the buy list
    Given A sample user
    And One item in the buy list
    When The user removes the item from their buy list
    Then The item should be removed from the buy list

  Scenario: Removing an item with more than 1 quantity in the buy list
    Given A sample user
    And Many of one item in the buy list
    When The user removes the item from their buy list
    Then The item quantity should be lowered by one

  Scenario: Removing an item that does not exist in the buy list
    Given A sample user
    When The user removes an item not in their buy list
    Then CommodityIsNotInBuyList is thrown
