package org.automation.apiTest.steps;

import io.cucumber.java.en.When;
import org.automation.apiTest.utils.enums.HttpMethod;
import org.testng.Assert;

import static org.automation.apiTest.context.ApiContext.getResponse;
import static org.automation.apiTest.steps.APIHandlers.sendRequest;
import static org.automation.apiTest.utils.LoggerUtil.logInfo;

public class CommonAPISteps {

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
}
