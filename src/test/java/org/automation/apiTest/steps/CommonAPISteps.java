package org.automation.apiTest.steps;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import org.automation.apiTest.utils.enums.HttpMethod;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.automation.apiTest.context.ApiContext.*;
import static org.automation.apiTest.steps.APIHandlers.sendRequest;
import static org.automation.apiTest.steps.APIHandlers.validateResponseSchema;
import static org.automation.apiTest.utils.LoggerUtil.*;

public class CommonAPISteps {

    private static final ThreadLocal<List<Object>> savedValuesFromResponseTL =
            ThreadLocal.withInitial(ArrayList::new);

    @When("Set request body from JSON file {string} in path {string}")
    public void setRequestBodyFromJsonFile(String jsonFileName, String filePath) {
        String fullFilePath = Paths.get(filePath)
                .normalize()
                .resolve(jsonFileName)
                .toString();

        try {
            InputStream inputStream = APIHandlers.class.getClassLoader().getResourceAsStream("requests/" + fullFilePath);
            if (inputStream == null) {
                throw new IllegalArgumentException("Request JSON file not found: " + fullFilePath);
            }
            String jsonBody = Files.readString(Paths.get(fullFilePath));
            setRequestBody(jsonBody);
        } catch (IOException e) {
            logError("Failed to read JSON file: " + fullFilePath);
            logException(e);
        }
    }


    @When("User sent {httpMethod} request to {string} with domain of {string}")
    public void sentRequestToWithDomain(HttpMethod method, String request, String domain) {
        sendRequest(request, domain, method);
    }

    @When("Assert status code is {word}")
    public void assertStatusCode(String status) {
        int actualStatusCode = getResponse().getStatusCode();
        int expectedStatusCode = Integer.parseInt(status);
        Assert.assertEquals(actualStatusCode, expectedStatusCode, String.format
                ("\nResponse: %s \nStatus code is incorrect expected: [%s], actual:[%s]\n", getResponse().getBody().prettyPrint(), expectedStatusCode, actualStatusCode));
    }

    @When("Assert response via schema name of {string} where {string}")
    public void assertResponseSchema(String nameOfFile, String filePath) {
        String fullFilePath = Paths.get(filePath)
                .normalize()
                .resolve(nameOfFile)
                .toString();
        String response = getResponse().getBody().asString();
        validateResponseSchema(response, fullFilePath);
    }

    @When("User saves following fields values with order ids:")
    public void saveResponseField(DataTable testData) {
        // can be converted to table later List<Map<String, String>> rows = testDataTable.asMaps(String.class, String.class);
        // every iteration has id and starts with 0
        List<String> values = testData.asList(String.class);
        JsonPath response = getResponseAsJsonPath();
        values.forEach(value -> {
            Object fieldValue = response.get(value);
            if (fieldValue != null) {
                setSavedValuesFromResponseTL(fieldValue);
            }
        });
    }

    //TODO check this method after the implementing JSON Request
    @When("User updates request body with saved values at paths:")
    public void updateRequestBodyWithSelectedSaveValue(DataTable testData) {
        List<Map<String, String>> rows = testData.asMaps(String.class, String.class);
        String requestBodyJson = getRequestBody();

        try {
            // Parse the requestBody to a JsonNode
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(requestBodyJson);

            rows.forEach(row -> {
                String path = row.get("path"); // JSON path to update
                String type = row.get("type"); // Data type (e.g., boolean, string, etc.)
                int index = Integer.parseInt(row.get("index")); // Index in saved values

                // Retrieve the value from the saved values
                Object value = getSavedValuesFromResponseTL().get(index);

                if (value != null) {
                    try {
                        JsonNode updatedValue = convertValueByType(mapper, value, type);
                        // Update the JSON at the specified path
                        ((ObjectNode) rootNode).putPOJO(path, updatedValue);
                    } catch (Exception e) {
                        logWarn(String.format("Failed to parse value for path '%s'. Skipping update.", path));
                    }
                } else {
                    logWarn(String.format("No saved value found at index %d for path '%s'. Skipping update.", index, path));
                }
            });

            // Update the requestBody with the modified JSON
            setRequestBody(mapper.writeValueAsString(rootNode));
        } catch (Exception e) {
            logWarn("Failed to update request body. Invalid JSON format.");
        }
    }

    private JsonNode convertValueByType(ObjectMapper mapper, Object value, String type) throws Exception {
        return switch (type.toLowerCase()) {
            case "boolean" -> mapper.convertValue(Boolean.parseBoolean(value.toString()), JsonNode.class);
            case "int", "integer" -> mapper.convertValue(Integer.parseInt(value.toString()), JsonNode.class);
            case "double" -> mapper.convertValue(Double.parseDouble(value.toString()), JsonNode.class);
            default -> mapper.convertValue(value.toString(), JsonNode.class);
        };
    }

    public static Object getSavedValueWithKey(int index) {
        return savedValuesFromResponseTL.get().get(index);
    }

    public static List<Object> getSavedValuesFromResponseTL() {
        return savedValuesFromResponseTL.get();
    }

    public static void setSavedValuesFromResponseTL(Object valueToSet) {
        getSavedValuesFromResponseTL().add(valueToSet);
    }
}
