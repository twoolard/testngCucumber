Feature: LoginFeature
  This feature deals with the login functionality of the application

  Scenario Outline: Login with correct username and password using scenario outline
    Given I navigate to the login page
    And I login using <Username> and <Password>
    Then I should see the userform page

    Examples:
      | Username | Password   |
      | execute  | automation |
      | testing  | QA         |