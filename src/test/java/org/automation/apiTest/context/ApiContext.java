package org.automation.apiTest.context;

import io.cucumber.core.internal.com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.core.internal.com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static org.automation.apiTest.utils.FileUtils.getBaseUrlFromYaml;
import static org.automation.apiTest.utils.LoggerUtil.logInfo;

public class ApiContext {

    private static ThreadLocal<String> requestBody = new ThreadLocal<>();
    private static ThreadLocal<Response> response = new ThreadLocal<>();
    private static ThreadLocal<String> baseUrl = new ThreadLocal<>();
    private static ThreadLocal<String> env = new ThreadLocal<>();

    static ObjectMapper mapper = new ObjectMapper();

    public static void setRequestBody(String body) {
        requestBody.set(body);
    }

    public static String getRequestBody() {
        return requestBody.get();
    }

    public static void clearRequestBody() {
        requestBody.remove();
    }

    public static void setResponse(Response res) {
        response.set(res);
    }

    public static Response getResponse() {
        return response.get();
    }

    public static JsonPath getResponseAsJsonPath() {
        return response.get().jsonPath();
    }

    public static JsonNode getResponseAsJsonNode() throws JsonProcessingException {
        return mapper.readTree(getResponse().getBody().prettyPrint());
    }

    public static void clearResponse() {
        response.remove();
    }

    public static void clearTLVariables() {
        requestBody.remove();
        response.remove();
    }

    public static void setEnv(String envInfo) {
        env.set(envInfo);
        logInfo(String.format("Env set as: [%s]", envInfo));
    }

    public static String getEnv() {
        return env.get();
    }

    public static void setBaseUrl(String key) {
        String url = getBaseUrlFromYaml(key + getEnv());
        logInfo(String.format("BaseUrl: [%s]", url));
        baseUrl.set(url);
    }

    public static String getBaseUrl() {
        return baseUrl.get();
    }
}
