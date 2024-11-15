package org.automation.apiTest.steps;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.automation.apiTest.utils.enums.HttpMethod;

import static org.automation.apiTest.context.ApiContext.getRequestBody;
import static org.automation.apiTest.context.ApiContext.setResponse;
import static org.automation.apiTest.utils.FileUtils.getBaseUrlFromYaml;
import static org.automation.apiTest.utils.HeaderUtil.getDefaultHeaders;

public class APIHandlers {

    public static void sendRequest(String endpoint, String domain, HttpMethod method) {
        RestAssured.baseURI = getBaseUrlFromYaml(domain);
        String body = getRequestBody();
        RequestSpecification requestSpecification = prepareRequest(body);
        Response response = switch (method.toString()) {
            case "GET" -> requestSpecification.get(endpoint);
            case "POST" -> requestSpecification.post(endpoint);
            case "DELETE" -> requestSpecification.delete(endpoint);
            case "PATCH" -> requestSpecification.patch(endpoint);
            case "PUT" -> requestSpecification.put(endpoint);
            default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };
        setResponse(response);
    }

    private static RequestSpecification prepareRequest(String body) {
        RequestSpecification requestSpecification = RestAssured.given().headers(getDefaultHeaders());
        if (body != null && !body.isEmpty()) {
            requestSpecification.body(body);
        }
        return requestSpecification;
    }
}
