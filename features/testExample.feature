@api
Feature: Example Tests

  @TestCase:1 @fail #@Ignore
  Scenario: Test For failure
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 400

  @TestCase:1 @success
  Scenario: Test For Success
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 200