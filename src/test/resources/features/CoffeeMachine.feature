Feature: Coffee Machine Operations

  @smoke
  Scenario: Verifying Title
    Given I open the coffee machine website
    And I wait for solid network silence
    When I verify the header is Coffee Machine
    Then I verify coffee machine status title
    Then I verify that the second header is Buy Coffee
    And I verify label is present
    Then I verify label text is Type:
    When I select Cappuccino as coffee type
    And I wait for 20 seconds