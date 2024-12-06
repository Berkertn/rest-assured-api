@api
Feature: Example Tests

  @TestCase-2 #@fail
  Scenario: Test For failure
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 200
    Given User saves following fields values with order ids:
      | support.url |
      | page        |
      | berk        |

  @TestCase-2 @success @Ignore #feature implement on going
  Scenario: Test For Success
    When Set request body from JSON file "dynamicRequest.json" in path "data/jsonFiles"
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 200
    And Assert response via schema name of "usersSuccessResponseSchema.json" where "users/"

  @TestCase-2 @success @Ignore #feature implement on going
  Scenario: Test For Success
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 200
    And Assert response via schema name of "usersSuccessResponseSchema.json" where "users/"