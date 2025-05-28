Feature: Coffee Machine Operations

  @smoke
  Scenario: Verifying Title
    Given I open the coffee machine website
    And I wait for solid network silence
    When I verify the header is Coffee Machine
    Then I verify coffee machine status title
