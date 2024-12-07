package org.automation.apiTest.steps;

import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import org.assertj.core.api.SoftAssertions;
import org.automation.apiTest.utils.enums.HttpMethod;
import org.testng.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.automation.apiTest.context.ApiContext.*;
import static org.automation.apiTest.steps.APIHandlers.sendRequest;
import static org.automation.apiTest.steps.APIHandlers.validateResponseSchema;
import static org.automation.apiTest.utils.LoggerUtil.*;

public class CommonAPISteps {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final ThreadLocal<List<Object>> savedValuesFromResponseTL =
            ThreadLocal.withInitial(ArrayList::new);

    @When("Set request body from JSON file {string} in path {string}")
    public void setRequestBodyFromJsonFile(String jsonFileName, String filePath) throws IOException {
        String fullFilePath = Paths.get(filePath)
                .normalize()
                .resolve(jsonFileName)
                .toString();

        InputStream inputStream = APIHandlers.class.getClassLoader().getResourceAsStream("requests/" + fullFilePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Request JSON file not found: " + fullFilePath);
        }
        // Read the json to set as string
        String jsonBody = new String(inputStream.readAllBytes());
        setRequestBody(jsonBody);
    }

    @When("User sent {httpMethod} request to {string} with domain of {string}")
    public void sentRequestToWithDomain(HttpMethod method, String request, String domain) {
        sendRequest(request, domain, method);
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

    @When("User updates request body with the following values:")
    public void updateRequestBodyWithValues(DataTable testData) {
        // | path | value | type |
        List<Map<String, String>> rows = testData.asMaps(String.class, String.class);
        String requestBodyJson = getRequestBody();

        try {
            JsonNode reqBody = mapper.readTree(requestBodyJson);
            updateJsonNodeWithPaths(reqBody, rows);
            setRequestBody(mapper.writeValueAsString(reqBody));
        } catch (Exception e) {
            logWarn("Failed to update request body. Invalid JSON format or invalid path.");
            logException(e);
        }
    }

    @When("User updates request body with saved values at paths:")
    public void updateRequestBodyWithSelectedSaveValue(DataTable testData) {
        // | path | type | index | we are waiting these headers to be in use
        List<Map<String, String>> rows = testData.asMaps(String.class, String.class);
        String requestBodyJson = getRequestBody();

        try {
            JsonNode rootNode = mapper.readTree(requestBodyJson);
            rows.forEach(row -> {
                String path = row.get("path");
                String type = row.get("type") != null ? row.get("type") : "auto";
                int index = Integer.parseInt(row.get("index"));

                // Get the value from the saved values
                Object value = getSavedValuesFromResponseTL().get(index);

                if (value != null) {
                    try {
                        JsonNode updatedValue = convertValueByTypeOrAuto(value, type);
                        // Update the json at the path
                        ((ObjectNode) rootNode).putPOJO(path, updatedValue);
                    } catch (Exception e) {
                        logWarn(String.format("Failed to parse value for path '%s'. Skipping update.", path));
                    }
                } else {
                    logWarn(String.format("No saved value found at index %d for path '%s'. Skipping update.", index, path));
                }
            });

            // Update the requestBody with the updated json
            setRequestBody(mapper.writeValueAsString(rootNode));
        } catch (Exception e) {
            logWarn("Failed to update request body. Invalid JSON format.");
        }
    }

    @Then("Assert status code is {word}")
    public void assertStatusCode(String status) {
        int actualStatusCode = getResponse().getStatusCode();
        int expectedStatusCode = Integer.parseInt(status);
        Assert.assertEquals(actualStatusCode, expectedStatusCode, String.format
                ("\nResponse: %s \nStatus code is incorrect expected: [%s], actual:[%s]\n", getResponse().getBody().prettyPrint(), expectedStatusCode, actualStatusCode));
    }

    @Then("Verify response paths and messages:")
    public void verifyResponsePathsAndMessages(DataTable dataTable) {
        SoftAssertions softAssert = new SoftAssertions();
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        JsonPath response = getResponseAsJsonPath();

        rows.forEach(row -> {
            String path = row.get("responsePath");
            String expectedMessage = row.get("responseMessage");
            String actualMessage = response.getString(path);
            softAssert.assertThat(actualMessage).withFailMessage(String.format("In path [%s] of response is not containing waiting message, actual: [%s] | expected: [%s]", path, actualMessage, expectedMessage)).isEqualTo(expectedMessage);
        });
        softAssert.assertAll();
    }

    @Then("Assert response via schema name of {string} where {string}")
    public void assertResponseSchema(String nameOfFile, String filePath) {
        String fullFilePath = Paths.get(filePath)
                .normalize()
                .resolve(nameOfFile)
                .toString();
        String response = getResponse().getBody().asString();
        validateResponseSchema(response, fullFilePath);
    }

    private void updateJsonNodeWithPaths(JsonNode reqBody, List<Map<String, String>> updates) {
        updates.forEach(row -> {
            String path = row.get("path");
            String value = row.get("value");
            String type = row.get("type") != null ? row.get("type") : "auto";

            try {
                JsonNode updatedValue = convertValueByTypeOrAuto(value, type);
                if (reqBody instanceof ObjectNode) {
                    ((ObjectNode) reqBody).putPOJO(path, updatedValue);
                } else {
                    logWarn("Root node is not an ObjectNode. Cannot update the request body.");
                }
            } catch (Exception e) {
                logWarn(String.format("Failed to update path '%s' with value '%s'. Skipping update.", path, value));
            }
        });
    }

    private JsonNode convertValueByTypeOrAuto(Object value, String type) {
        // If type is auto determine type
        if ("auto".equalsIgnoreCase(type)) {
            if (value instanceof Boolean) {
                return mapper.convertValue(value, JsonNode.class);
            } else if (value instanceof Integer) {
                return mapper.convertValue(value, JsonNode.class);
            } else if (value instanceof Double) {
                return mapper.convertValue(value, JsonNode.class);
            } else if (value instanceof String) {
                return mapper.convertValue(value.toString(), JsonNode.class);
            } else {
                logError("Unsupported type for value: " + value.getClass().getSimpleName());
            }
        }

        // Convert based on provided type
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
