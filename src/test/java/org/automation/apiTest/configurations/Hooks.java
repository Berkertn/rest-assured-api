package org.automation.apiTest.configurations;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import static org.automation.apiTest.context.ApiContext.getRequestBody;
import static org.automation.apiTest.context.ApiContext.getResponse;
import static org.automation.apiTest.utils.LoggerUtil.logInfo;

public class Hooks {

    @Before(order = -1) // Environment setup
    public void setupEnvironment() {
        String environment = System.getProperty("env", "dev");
        logInfo(String.format("Tests are running on the [%s] environment\n", environment));
    }

    @Before(order = 0) // Log scenario start
    public void beforeScenario(Scenario scenario) {
        logInfo("Starting Scenario: " + scenario.getName());
    }

    @After(order = 0) // Log scenario result
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            logInfo("Scenario failed: " + scenario.getName());
            logInfo(String.format("Request body: [%s]\n", getRequestBody()));
            logInfo(String.format("Response: [%s] \n", getResponse().getBody().prettyPrint()));
        } else {
            logInfo(String.format("Scenario passed: %s\n", scenario.getName()));
        }
    }
}
