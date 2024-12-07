@api
Feature: Example Tests

  Background:
    When User sent GET request to "/api/users" with domain of "service1"
    Then Assert status code is 200
    Given User saves following fields values with order ids:
      | data[0].id |

  @TestCase-2 #@fail
  Scenario: Test For failure
    When User sent GET request to "/api/userss/test" with domain of "service1"
    Then Assert status code is 404

  @TestCase-2 @success @Ignore #feature implement on going
  Scenario: Test User For Success
    When User updates request body with saved values at paths:
      | path | type | index |
      |      |      |       |
    Then Assert response via schema name of "users_success_response_schema.json" where "users/"

  @TestCase-3 @fail
  Scenario Outline: Test Register For Fail
    Given Set request body from JSON file "login_request_body.json" in path "auth/"
    And User updates request body with the following values:
      | path        | value   | type |
      | <parameter> | <value> |      |
    When User sent POST request to "/api/register" with domain of "service1"
    Then Assert status code is 400
    Then Verify response paths and messages:
      | responsePath | responseMessage                               |
      | error        | Note: Only defined users succeed registration |
    Examples:
      | parameter | value          |
      | username  | testApiVolume1 |

  @TestCase-4 @success
  Scenario: Test Login For Success
    Given Set request body from JSON file "login_request_body.json" in path "auth/"
    When User sent POST request to "/api/login" with domain of "service1"
    Then Assert status code is 200
    And Assert response via schema name of "login_success_response_schema.json" where "auth/"