package org.automation.apiTest.steps;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.automation.apiTest.utils.enums.HttpMethod;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;

import java.io.InputStream;

import static org.automation.apiTest.context.ApiContext.getRequestBody;
import static org.automation.apiTest.context.ApiContext.setResponse;
import static org.automation.apiTest.utils.FileUtils.getBaseUrlFromYaml;
import static org.automation.apiTest.utils.HeaderUtil.getDefaultHeaders;
import static org.automation.apiTest.utils.LoggerUtil.*;

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

    public static void validateResponseSchema(String responseBody, String schemaPathAndFileName) {
        try (InputStream inputStream = APIHandlers.class.getClassLoader().getResourceAsStream("schemas/" + schemaPathAndFileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Schema file not found: " + schemaPathAndFileName);
            }

            JSONObject jsonSchema = new JSONObject(new JSONTokener(inputStream));
            JSONObject jsonResponse = new JSONObject(responseBody);

            Schema schema = SchemaLoader.load(jsonSchema);
            schema.validate(jsonResponse); // Validate the response against the schema
        } catch (Exception e) {
            logError("Schema validation failed: " + e.getMessage());
        }
    }
}
