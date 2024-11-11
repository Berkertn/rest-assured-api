package org.automation.apiTest.steps;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static org.automation.apiTest.context.ApiContext.getRequestBody;
import static org.automation.apiTest.context.ApiContext.setResponse;
import static org.automation.apiTest.utils.HeaderUtil.getDefaultHeaders;

public class CommonAPISteps {

    public void sendRequest(String endpoint, String method) {
        String body = getRequestBody();
        Response response = switch (method.toUpperCase()) {
            case "GET" -> RestAssured.given().headers(getDefaultHeaders()).body(body).get(endpoint);
            case "POST" -> RestAssured.given().headers(getDefaultHeaders()).body(body).post(endpoint);
            case "DELETE" -> RestAssured.given().headers(getDefaultHeaders()).body(body).delete(endpoint);
            case "PATCH" -> RestAssured.given().headers(getDefaultHeaders()).body(body).patch(endpoint);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
        setResponse(response);
    }
}
