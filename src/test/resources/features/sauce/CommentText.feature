Feature: Guinea Pig comment

  @smoke
  Scenario: Can Submit Comment
    Given I am on the Guinea Pig homepage
    When I submit a comment
    Then I should see that comment displayed