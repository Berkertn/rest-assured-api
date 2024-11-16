@api
Feature: Example Tests

  @TestCase:1 @fail
  Scenario: Test For failure
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 400

  @TestCase:1 @success
  Scenario: Test For Success
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 200
    And Assert response via schema name of "usersSuccessResponseSchema.json" where "users/"