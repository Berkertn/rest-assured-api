package org.automation.apiTest.utils;

import io.cucumber.java.ParameterType;
import org.automation.apiTest.utils.enums.HttpMethod;

public class StepDefParameters {
    @ParameterType("GET|POST|DELETE|PATCH|PUT") // only allowed methods
    public HttpMethod httpMethod(String method) {
        return HttpMethod.valueOf(method.toUpperCase());
    }
}
